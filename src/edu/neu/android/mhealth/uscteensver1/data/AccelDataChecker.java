package edu.neu.android.mhealth.uscteensver1.data;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import android.util.Log;
import edu.neu.android.mhealth.uscteensver1.USCTeensGlobals;
import edu.neu.android.wocketslib.Globals;
import edu.neu.android.wocketslib.utils.DateHelper;
import edu.neu.android.wocketslib.utils.FileHelper;

public class AccelDataChecker {
	public final static String TAG = "AccelDataChecker";
	
	private final static float LOW_INTENSITY_THRESHOLD  = USCTeensGlobals.SENSOR_DATA_SCALING_FACTOR / 8f;
	private final static float HIGH_INTENSITY_THRESHOLD = USCTeensGlobals.SENSOR_DATA_SCALING_FACTOR / 2f;
	
	private final static int ONE_MINUTE = 60 * 1000;
	private final static int MIN_TIME_INTERVAL = 45 * 60 * 1000; // 45 minutes

	public static ContextSensitiveState checkDataState(long from, long to) {
		long now = System.currentTimeMillis();
		long oneMinAgo = now - ONE_MINUTE;			
		
		// check the input parameters
		if (to > oneMinAgo || from > to - MIN_TIME_INTERVAL) {
			Log.d(TAG, "Input time is not acceptable for checking data state!");
			return new ContextSensitiveState(ContextSensitiveState.DATA_STATE_ERROR, null, null);
		}
		
		// Get data first		
		int[] sensorData = getData(from, to);	
		
		// Analyze data to get the state for context sensitive prompt	
		ContextSensitiveState css = analyzeData(sensorData, from, to);
		
		return css;
	}
	
	private static int[] getData(long from, long to) {
		Date dateFrom = new Date(from);
		Date dateTo   = new Date(to);
		// read the whole piece of data according to the input time
		String date = DateHelper.getServerDateString(new Date());
		String[] hourDirs = FileHelper.getFilePathsDir(
				Globals.EXTERNAL_DIRECTORY_PATH + File.separator + 
				Globals.DATA_DIRECTORY + USCTeensGlobals.SENSOR_FOLDER + date);	
		int hourFrom = dateFrom.getHours();
		int hourTo   = dateTo.getHours();
		AccelDataWrap accelDataWrap = new AccelDataWrap();
		
		for (int i = 0; i <= hourTo - hourFrom; ++i) {
			// each hour corresponds to one .bin file
			String[] filePaths = FileHelper.getFilePathsDir(hourDirs[i]);
			String filePath = filePaths[0]; // set a default value
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
		}		
		accelDataWrap.updateDrawableData();
		
		return accelDataWrap.getDrawableData();
	}
	

	/*
	 *  analyze the data of the previous 30+ minutes 
	 */
	private static ContextSensitiveState analyzeData(int[] sensorData, long from, long to) {
		Date dateFrom = new Date(from);
		Date dateTo   = new Date(to);
		// convert Date to seconds
//		int secFrom = dateFrom.getHours() * 3600 + dateFrom.getMinutes() * 60 + dateFrom.getSeconds();
		int secTo   = dateTo.getHours() * 3600 + dateTo.getMinutes() * 60 + dateTo.getSeconds();
		
		// first, check 30+ minutes of “high intensity” data (use a global threshold) 
		// followed by 10 minutes of low intensity data
		float sum = 0;
		for (int i = secTo - 40 * 60; i < secTo - 10 * 60; ++i) {
			sum += sensorData[i];
		}		
		if (sum / (30 * 60) > HIGH_INTENSITY_THRESHOLD) {
			sum = 0;
			for (int i = secTo - 10 * 60; i < secTo; ++i) {
				sum += sensorData[i];
			}
			if (sum / (10 * 60) < LOW_INTENSITY_THRESHOLD) {
				return new ContextSensitiveState(ContextSensitiveState.DATA_STATE_HIGH_INTENSITY,
						new Date(to - 40 * ONE_MINUTE), dateTo);
			}
		}
		
		// then, check 30+ minutes of missing data  (based on each rule we will define
		// how to get the start/end time used when printing the first question.
		boolean isMissing = true;
		for (int i = secTo - 30 * 60; i < secTo; ++i) {
			if (sensorData[i] != AccelDataWrap.NO_SENSOR_DATA) {
				isMissing = false;
				break;
			}
		}		
		if (isMissing) {
			return new ContextSensitiveState(ContextSensitiveState.DATA_STATE_MISSING, 
					new Date(to - 30 * ONE_MINUTE), dateTo);
		}
		
		// finally, check 30+ minutes of low-intensity data
		boolean isLowIntensity = true;
		for (int i = secTo - 30 * 60; i < secTo; ++i) {
			if (sensorData[i] > LOW_INTENSITY_THRESHOLD) {
				isLowIntensity = false;
			}
		}
		if (isLowIntensity) {
			return new ContextSensitiveState(ContextSensitiveState.DATA_STATE_LOW_INTENSITY, 
					new Date(to - 30 * ONE_MINUTE), dateTo);
		}
		
		return new ContextSensitiveState(ContextSensitiveState.DATA_STATE_NORMAL, dateFrom, dateTo);
	}
		
}
