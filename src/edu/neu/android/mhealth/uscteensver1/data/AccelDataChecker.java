package edu.neu.android.mhealth.uscteensver1.data;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import edu.neu.android.mhealth.uscteensver1.USCTeensGlobals;
import edu.neu.android.wocketslib.Globals;
import edu.neu.android.wocketslib.algorithm.MotionDetectAlgorithm;
import edu.neu.android.wocketslib.algorithm.ChunkingAlgorithm;
import edu.neu.android.wocketslib.algorithm.MotionDetectAlgorithm;
import edu.neu.android.wocketslib.emasurvey.model.SurveyExtraInfo;
import edu.neu.android.wocketslib.utils.DateHelper;
import edu.neu.android.wocketslib.utils.FileHelper;

public class AccelDataChecker {
	public final static String TAG = "AccelDataChecker";
	
	public static SurveyExtraInfo checkDataState(Context context, long startTime, long stopTime) {
		
		// Get start/stop time
		if (startTime == -1) {
			startTime = MotionDetectAlgorithm.getInstance(context).getStartTime();
		}		
		if (stopTime == -1) {
			stopTime = System.currentTimeMillis() - 120000; // make sure we have the data to analyze
		}
		if (startTime >= stopTime || Math.abs(startTime - stopTime) > 24 * 3600 * 1000) {
			return new SurveyExtraInfo("error", null, null);
		}
		
		// Get data first
		int[] sensorData = getData(startTime, stopTime);
		if (sensorData == null) {
			return new SurveyExtraInfo("error", null, null);
		}
				
		// Chunk the data just got		
		ArrayList<Integer> chunkPos = ChunkingAlgorithm.getInstance().doChunking(startTime, stopTime, sensorData);
		if (chunkPos == null) {
			return new SurveyExtraInfo("error", null, null);
		}
		
		// Analyze data to get the state for context sensitive prompt
		return MotionDetectAlgorithm.getInstance(context).doMotionDetection(sensorData, chunkPos, startTime, stopTime);
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
