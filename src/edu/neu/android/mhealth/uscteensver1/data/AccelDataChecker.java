package edu.neu.android.mhealth.uscteensver1.data;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Date;

import edu.neu.android.mhealth.uscteensver1.TeensGlobals;
import edu.neu.android.wocketslib.Globals;
import edu.neu.android.wocketslib.algorithm.ChunkingAlgorithm;
import edu.neu.android.wocketslib.algorithm.MotionDetectAlgorithm;
import edu.neu.android.wocketslib.algorithm.MotionInfo;
import edu.neu.android.wocketslib.utils.DateHelper;
import edu.neu.android.wocketslib.utils.FileHelper;

public class AccelDataChecker {
	public final static String TAG = "AccelDataChecker";
	
	public static MotionInfo checkDataState(long startTime, long stopTime) {
		
		// Get start/stop time
		if (startTime == -1) {
			startTime = MotionDetectAlgorithm.getInstance().getStartTime();
		}		
		if (stopTime == -1) {
			stopTime = System.currentTimeMillis() - 120000; // make sure we have the data to analyze
		}
		if (startTime >= stopTime || Math.abs(startTime - stopTime) > 24 * 3600 * 1000) {
			return new MotionInfo(MotionInfo.ERROR, null, null);
		}
		
		// Get data first
		int[] sensorData = getData(startTime, stopTime);
		if (sensorData == null) {
			return new MotionInfo(MotionInfo.ERROR, null, null);
		}
				
		// Chunk the data just got		
		ArrayList<Integer> chunkPos = ChunkingAlgorithm.getInstance().doChunking(startTime, stopTime, sensorData);
		if (chunkPos == null) {
			return new MotionInfo(MotionInfo.ERROR, null, null);
		}
		
		// Analyze data to get the state for context sensitive prompt
		return MotionDetectAlgorithm.getInstance().doMotionDetection(sensorData, chunkPos, startTime, stopTime);
	}
	
	private static int[] getData(long from, long to) {
		Date dateFrom = new Date(from);
		Date dateTo   = new Date(to);
		// read the whole piece of data according to the input time
		String date = DateHelper.getServerDateString(new Date());
		String[] hourDirs = FileHelper.getFilePathsDir(
			TeensGlobals.DIRECTORY_PATH + File.separator + Globals.DATA_DIRECTORY + File.separator + TeensGlobals.SENSOR_FOLDER + date
		);	
		if (hourDirs == null) {
			// no data to get
			return null;
		}
		
		int hourFrom = dateFrom.getHours();
		int hourTo   = dateTo.getHours();
		AccelDataWrap accelDataWrap = new AccelDataWrap();
		
		for (int i = 0; i < hourDirs.length; ++i) {
			File file = new File(hourDirs[i]);
			if (file.isFile()) { continue; }
			String hour = hourDirs[i].substring(hourDirs[i].lastIndexOf('/') + 1);
			int targetHour = Integer.valueOf(hour);
			if (targetHour < hourFrom || targetHour > hourTo) { // 12AM can not be handled here!!!
				continue;
			}
			
			try {
				// each hour corresponds to one .bin file
				String[] fileNames = new File(hourDirs[i]).list(new FilenameFilter() {
					@Override
					public boolean accept(File dir, String filename) {
						return filename.endsWith(".csv");
					}
				});
				if (fileNames == null || fileNames.length == 0) {
					continue;
				}
				String filePath = hourDirs[i] + File.separator + fileNames[0];
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
