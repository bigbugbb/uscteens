package edu.neu.android.mhealth.uscteensver1.data;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import edu.neu.android.mhealth.uscteensver1.USCTeensGlobals;
import edu.neu.android.mhealth.uscteensver1.algorithm.CSDetectingAlgorithm;
import edu.neu.android.mhealth.uscteensver1.algorithm.ChunkingAlgorithm;
import edu.neu.android.wocketslib.Globals;
import edu.neu.android.wocketslib.utils.DateHelper;
import edu.neu.android.wocketslib.utils.FileHelper;

public class AccelDataChecker {
	public final static String TAG = "AccelDataChecker";
	
	public static ContextSensitiveState checkDataState() {
		// Get the start/end time
		long now = System.currentTimeMillis();
		
		// Reset the start time if necessary
		long startTime = CSDetectingAlgorithm.getInstance().getStartTime();
		if (startTime > now || now - startTime > Globals.HOURS4_MS) {
			startTime = now - (long) (2.5f * Globals.MINUTES_10_IN_MS);	
			CSDetectingAlgorithm.getInstance().setStartTime(startTime);
		}
		
		// Get data first
		long from = startTime, to = now - Globals.MINUTES_1_IN_MS;
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
		return CSDetectingAlgorithm.getInstance().doCSDetecting(sensorData, chunkPos, from, to);
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
}
