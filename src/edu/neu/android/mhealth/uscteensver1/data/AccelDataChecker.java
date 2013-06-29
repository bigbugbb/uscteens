package edu.neu.android.mhealth.uscteensver1.data;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.annotation.SuppressLint;
import edu.neu.android.mhealth.uscteensver1.USCTeensGlobals;
import edu.neu.android.mhealth.uscteensver1.algorithm.ChunkingAlgorithm;
import edu.neu.android.wocketslib.Globals;
import edu.neu.android.wocketslib.utils.DateHelper;
import edu.neu.android.wocketslib.utils.FileHelper;

public class AccelDataChecker {
	public final static String TAG = "AccelDataChecker";
	
	private final static int NO_SENSOR_DATA = -1;
	private final static int DURATION_THRESHOLD = 5 * 60;
	private final static float MERGING_THRESHOLD  = USCTeensGlobals.SENSOR_DATA_SCALING_FACTOR * 0.15f;
	private final static float INTENSITY_THRESHOLD = USCTeensGlobals.SENSOR_DATA_SCALING_FACTOR * 0.2f;	

	private static long sStartTime = 0; 
	
	public static ContextSensitiveState checkDataState() {
		// Get the start/end time
		long now = System.currentTimeMillis();
		
		// Reset the start time if necessary
		if (sStartTime > now || now - sStartTime > Globals.HOURS4_MS) {
			sStartTime = now - Globals.MINUTES_10_IN_MS;			
		}
		
		// Get data first
		long from = sStartTime, to = now - Globals.MINUTES_1_IN_MS;
		int[] sensorData = getData(from, to);
		if (sensorData == null) {
			return new ContextSensitiveState(ContextSensitiveState.DATA_STATE_ERROR, null, null);
		}
				
		// Chunk the data just got		
		ArrayList<Integer> chunkPos = ChunkingAlgorithm.getInstance().doChunking(from, to, sensorData);
		if (chunkPos == null) {
			return new ContextSensitiveState(ContextSensitiveState.DATA_STATE_ERROR, null, null);
		}
		
		// Analyze data to get the state for context sensitive prompt
		return analyzeData(sensorData, chunkPos, from, to);
	}
	
	private static int[] getData(long from, long to) {
		Date dateFrom = new Date(from);
		Date dateTo   = new Date(to);
		// read the whole piece of data according to the input time
		String date = DateHelper.getServerDateString(new Date());
		String[] hourDirs = FileHelper.getFilePathsDir(
				Globals.EXTERNAL_DIRECTORY_PATH + File.separator + 
				Globals.DATA_DIRECTORY + USCTeensGlobals.SENSOR_FOLDER + date);	
		if (hourDirs == null) {
			// no data to get
			return null;
		}
		
		int hourFrom = dateFrom.getHours();
		int hourTo   = dateTo.getHours();
		AccelDataWrap accelDataWrap = new AccelDataWrap();
		
		for (int i = 0; i < hourDirs.length; ++i) {
			int targetHour = Integer.valueOf(hourDirs[i].substring(hourDirs[i].lastIndexOf('/') + 1));
			if (targetHour < hourFrom || targetHour > hourTo) { // 12AM can not be handled here!!!
				continue;
			}
			
			try {
				// each hour corresponds to one .bin file
				String[] filePaths = FileHelper.getFilePathsDir(hourDirs[i]);
				String filePath = filePaths[0];			
				for (String path : filePaths) {				
					String extName = path.substring(path.lastIndexOf("."), path.length());
					if (extName.equals(".bin")) {
						filePath = path;
					}
				}
				// load the hourly data from .bin file
				ArrayList<AccelData> hourlyAccelData = new ArrayList<AccelData>();						
				DataSource.loadHourlyRawAccelData(filePath, hourlyAccelData, false);
				accelDataWrap.add(hourlyAccelData);
			} catch (Exception e) {
				e.printStackTrace();
			}			
		}		
		accelDataWrap.updateDrawableData();
		
		return accelDataWrap.getDrawableData();
	}
	

	/*
	 *  analyze the data of the previous 30+ minutes 
	 */
	@SuppressLint("UseSparseArrays")
	private static ContextSensitiveState analyzeData(int[] sensorData, ArrayList<Integer> chunkPos, long from, long to) {
		Date startTime = new Date(from);
		Date stopTime  = new Date(to);
		// Convert Date to second
		int secTo   = stopTime.getHours() * 3600 + stopTime.getMinutes() * 60 + stopTime.getSeconds();
		int secFrom = startTime.getHours() * 3600 + startTime.getMinutes() * 60 + startTime.getSeconds();
		
		// Create the position to mean value hash
		HashMap<Integer, Float> ctmHash = new HashMap<Integer, Float>();
		for (int i = 0; i < chunkPos.size() - 1; ++i) {
			int curPos = chunkPos.get(i);
			int nxtPos = chunkPos.get(i + 1);
			// Get mean value from the current chunk position to the next
			float sum = 0;
			for (int j = curPos; j < nxtPos; ++j) {
				sum += sensorData[j];
			}
			if (nxtPos - curPos > 0) {
				ctmHash.put(curPos, sum / (nxtPos - curPos));
			}
		}
		
		// Check whether there is some period with no data at all
		for (int i = 0; i < chunkPos.size() - 1; ++i) {
			int curPos = chunkPos.get(i);
			int nxtPos = chunkPos.get(i + 1);
			Float mean = ctmHash.get(curPos);
			if (mean != null && Math.abs(mean - NO_SENSOR_DATA) < 0.1f) {
				if (nxtPos - curPos > Globals.MINUTES_30_IN_MS) {
					sStartTime = DateHelper.getDailyTime(0, 0) + nxtPos * 1000;
					Date dateFrom = new Date(DateHelper.getDailyTime(0, 0) + curPos * 1000);
					Date dateTo   = new Date(DateHelper.getDailyTime(0, 0) + nxtPos * 1000);
					return new ContextSensitiveState(ContextSensitiveState.DATA_STATE_MISSING, dateFrom, dateTo);
				}
			}
		}
		
		// Merge the some chunks if their means are relatively the same
		ArrayList<Integer> merged = new ArrayList<Integer>();
		for (int i = chunkPos.size() - 1; i > 1; --i) {
			int curPos = chunkPos.get(i);
			int prvPos = chunkPos.get(i - 1);
			// Merge them if necessary
			Float curMean = ctmHash.get(curPos);
			Float prvMean = ctmHash.get(prvPos);
			if (curMean != null && prvMean != null && Math.abs(curMean - prvMean) < MERGING_THRESHOLD) {
				ctmHash.put(curPos, null);
				merged.add(curPos);
			}
		}
		chunkPos.removeAll(merged);
		
		// Look for valuable chunk from secFrom to secTo based on mean value and duration
		for (int i = 0; i < chunkPos.size() - 1; ++i) {			
			int curPos = chunkPos.get(i);
			int nxtPos = chunkPos.get(i + 1);
			// Jump the period before the starting time 
			if (secFrom > curPos) { 
				continue; 
			}
			// Get the chunk whose mean is larger than the intensity threshold
			Float mean = ctmHash.get(curPos);
			if (mean != null && mean > INTENSITY_THRESHOLD) {
				// The duration of this chunk should not be too short				
				if (nxtPos - curPos > DURATION_THRESHOLD) {			
					sStartTime = DateHelper.getDailyTime(0, 0) + nxtPos * 1000;
					Date dateFrom = new Date(DateHelper.getDailyTime(0, 0) + curPos * 1000);
					Date dateTo   = new Date(DateHelper.getDailyTime(0, 0) + nxtPos * 1000);
					return new ContextSensitiveState(ContextSensitiveState.DATA_STATE_HIGH_INTENSITY, dateFrom, dateTo);
				}
			}
		}
		
		// Remember to update start time for the next check
		sStartTime = to;
				
		return new ContextSensitiveState(ContextSensitiveState.DATA_STATE_NORMAL, startTime, stopTime);
	}
		
}
