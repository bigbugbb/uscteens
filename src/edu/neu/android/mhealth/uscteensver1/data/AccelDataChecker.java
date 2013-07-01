package edu.neu.android.mhealth.uscteensver1.data;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.util.Log;
import edu.neu.android.mhealth.uscteensver1.USCTeensGlobals;
import edu.neu.android.mhealth.uscteensver1.algorithm.ChunkingAlgorithm;
import edu.neu.android.wocketslib.Globals;
import edu.neu.android.wocketslib.utils.DateHelper;
import edu.neu.android.wocketslib.utils.FileHelper;

public class AccelDataChecker {
	public final static String TAG = "AccelDataChecker";
	
	private final static int NO_SENSOR_DATA = -1;
	private final static int NO_DATA_DURATION_THRESHOLD        = 30 * 60; // in second
	private final static int NO_DATA_TOLERATION_THRESHOLD      = 3 * 60;  // in second
	private final static int ACTIVITY_TOLERATION_THRESHOLD     = 3 * 60;  // in second
	private final static int ACTIVITY_DURATION_THRESHOLD       = 5 * 60;  // in second
	private final static int DURATION_AFTER_ACTIVITY_THRESHOLD = 5 * 60;  // in second
	private final static float MERGING_THRESHOLD  = USCTeensGlobals.SENSOR_DATA_SCALING_FACTOR * 0.45f;
	private final static float DATA_INTENSITY_THRESHOLD = USCTeensGlobals.SENSOR_DATA_SCALING_FACTOR * 0.2f;	

	private static long sStartTime = 0; 
	
	public static ContextSensitiveState checkDataState() {
		// Get the start/end time
		long now = System.currentTimeMillis();
		
		// Reset the start time if necessary
		if (sStartTime > now || now - sStartTime > Globals.HOURS4_MS) {
			sStartTime = now - (long) (2.5f * Globals.MINUTES_10_IN_MS);			
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
			USCTeensGlobals.DIRECTORY_PATH + File.separator + Globals.DATA_DIRECTORY + File.separator + date + USCTeensGlobals.SENSOR_FOLDER
		);	
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
		long midnight = DateHelper.getDailyTime(0, 0);
		int secTo   = stopTime.getHours() * 3600 + stopTime.getMinutes() * 60 + stopTime.getSeconds();
		int secFrom = startTime.getHours() * 3600 + startTime.getMinutes() * 60 + startTime.getSeconds();
		int secNow  = (int) ((System.currentTimeMillis() - midnight) / 1000);
				
		// Create the position-to-mean hash and position-to-duration hash
		HashMap<Integer, Float>   ptmHash = new HashMap<Integer, Float>();
		HashMap<Integer, Integer> ptdHash = new HashMap<Integer, Integer>(); 
		for (int i = 0; i < chunkPos.size() - 1; ++i) {
			int curPos = chunkPos.get(i);
			int nxtPos = chunkPos.get(i + 1);
			// Get mean value from the current chunk position to the next
			float sum = 0;
			for (int j = curPos; j < nxtPos; ++j) {
				sum += sensorData[j];
			}
			int duration = Math.abs(nxtPos - curPos);
			if (duration != 0) {
				ptmHash.put(curPos, sum / duration);
				ptdHash.put(curPos, duration);
			}
		}
		
		// Merge some chunks if their means are relatively the same
		ArrayList<Integer> merged = new ArrayList<Integer>();
		for (int i = chunkPos.size() - 2; i > 1; --i) {
			int curPos = chunkPos.get(i);
			int prvPos = chunkPos.get(i - 1);
			// Merge them if necessary
			Float curMean = ptmHash.get(curPos);
			Float prvMean = ptmHash.get(prvPos);
			if (curMean == null || prvMean == null) {
				continue;				
			}
			if (curMean > DATA_INTENSITY_THRESHOLD && prvMean > DATA_INTENSITY_THRESHOLD &&
					Math.abs(curMean - prvMean) < MERGING_THRESHOLD) {					
				// Calculate and update the mean of the chunk after merging
				Integer prvDuration = ptdHash.get(prvPos) == null ? 0 : ptdHash.get(prvPos);
				Integer curDuration = ptdHash.get(curPos) == null ? 0 : ptdHash.get(curPos);
				Integer sumDuration = prvDuration + curDuration;
				if (sumDuration > 0) {
					Float meanAfterMerging = (curMean * curDuration + prvMean * prvDuration) / sumDuration;
					ptmHash.put(prvPos, meanAfterMerging);
					ptdHash.put(prvPos, sumDuration);
				}
				// Clear the chunk that has been merged					
				ptmHash.put(curPos, null);
				ptdHash.put(curPos, null);
				merged.add(curPos);
			}
		}
		chunkPos.removeAll(merged);
		
		// Check whether there is some period of time without any data
		for (int i = 0; i < chunkPos.size() - 1; ++i) {
			int curPos = chunkPos.get(i);
			int nxtPos = chunkPos.get(i + 1);
			Float mean = ptmHash.get(curPos);
			// It's better not to compare two float values directly
			if (mean != null && Math.abs(mean - NO_SENSOR_DATA) < 0.1f) {
				if (nxtPos - curPos > NO_DATA_DURATION_THRESHOLD) {
					// If nxtPos is the last chunk position and its value is very close to the current time in second,
					// update the starting time using the beginning position of the last chunk to make sure that
					// this position will have the chance to be checked again in the near future
					if (i == chunkPos.size() - 2 && secNow - nxtPos < NO_DATA_TOLERATION_THRESHOLD) {
						sStartTime = midnight + curPos * 1000; // update for the next check
						return new ContextSensitiveState(ContextSensitiveState.DATA_STATE_NORMAL, startTime, stopTime);
					} else {
						sStartTime = midnight + nxtPos * 1000;
					}
					Date dateFrom = new Date(midnight + curPos * 1000);
					Date dateTo   = new Date(midnight + nxtPos * 1000);
					return new ContextSensitiveState(ContextSensitiveState.DATA_STATE_MISSING, dateFrom, dateTo);
				} else if (i == chunkPos.size() - 2 && secNow - nxtPos < NO_DATA_TOLERATION_THRESHOLD) {
					sStartTime = midnight + curPos * 1000; // update for the next check
					return new ContextSensitiveState(ContextSensitiveState.DATA_STATE_NORMAL, startTime, stopTime);					
				}
			}
		}
		
		// Look for valuable chunk from secFrom to secTo based on mean value and duration
		for (int i = 0; i < chunkPos.size() - 1; ++i) {			
			int curPos = chunkPos.get(i);
			int nxtPos = chunkPos.get(i + 1);
			// Jump the period before the starting time 
			if (secFrom > curPos) { continue; }
			// Get the chunk whose mean is valuable if it's larger than the intensity threshold
			Float mean = ptmHash.get(curPos);					
			if (mean == null || mean < DATA_INTENSITY_THRESHOLD) { continue; }
			// The duration of this chunk should not be too short	
			if (nxtPos - curPos >= ACTIVITY_DURATION_THRESHOLD) {
				if (Math.abs(nxtPos - secNow) >= DURATION_AFTER_ACTIVITY_THRESHOLD) {
					sStartTime = midnight + nxtPos * 1000;
					Date dateFrom = new Date(midnight + curPos * 1000);
					Date dateTo   = new Date(midnight + nxtPos * 1000);
					return new ContextSensitiveState(ContextSensitiveState.DATA_STATE_HIGH_INTENSITY, dateFrom, dateTo);
				} else {
					sStartTime = midnight + curPos * 1000;
					return new ContextSensitiveState(ContextSensitiveState.DATA_STATE_NORMAL, startTime, stopTime);
				}
			} else if (i == chunkPos.size() - 2 && secNow - nxtPos < ACTIVITY_TOLERATION_THRESHOLD) {
				sStartTime = midnight + curPos * 1000;
				return new ContextSensitiveState(ContextSensitiveState.DATA_STATE_NORMAL, startTime, stopTime);
			}
		}

		// Update of starting time for the next check
		sStartTime = to;
				
		return new ContextSensitiveState(ContextSensitiveState.DATA_STATE_NORMAL, startTime, stopTime);
	}
}
