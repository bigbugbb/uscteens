package edu.neu.android.mhealth.uscteensver1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.os.Environment;
import edu.neu.android.mhealth.uscteensver1.survey.TeensSurveyScheduler;
import edu.neu.android.wocketslib.Globals;
import edu.neu.android.wocketslib.sensormonitor.Arbitrater;
import edu.neu.android.wocketslib.support.DataStorage;
import edu.neu.android.wocketslib.utils.Log;
//import org.omg.PortableInterceptor.INACTIVE;

public class USCTeensArbitrater extends Arbitrater {
	private static final String TAG = "USCTeensArbitrater";
	private static final String KEY_LAST_FILE_NAME = "KEY_LAST_FILE_NAME";
	private static final String KEY_LAST_FILE_CREATE_TIME = "KEY_FILE_CREATE_TIME";		
	
	private Context mContext;
	private TeensSurveyScheduler mScheduler;
	
	public USCTeensArbitrater(Context context) {
		mContext = context;
		mScheduler = new TeensSurveyScheduler(context);
	}

//	private boolean isLastFileExpired(long lastCreateTime, long now) {
//		Date dateA = new Date(lastCreateTime);
//		Date dateB = new Date(now);	
//		long midnight = DateHelper.getDailyTime(0, 0);		
//		long nextMidnight = midnight + 24 * 3600 * 1000;
//		
//		if (lastCreateTime < midnight || lastCreateTime > nextMidnight || dateA.getHours() != dateB.getHours()) {
//			return true;
//		}
//		
//		return false;
//	}
//
//	private String[] getFileNamesForSensorData() {
//		long now = System.currentTimeMillis();
//		long lastCreateTime = DataStorage.GetValueLong(mContext, KEY_LAST_FILE_CREATE_TIME, 0);
//		String lastFileName = DataStorage.GetValueString(mContext, KEY_LAST_FILE_NAME, "");
//		
//		if (isLastFileExpired(lastCreateTime, now)) {			
//			lastFileName = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS").format(now);			
//			DataStorage.SetValue(mContext, KEY_LAST_FILE_NAME, lastFileName);
//			DataStorage.SetValue(mContext, KEY_LAST_FILE_CREATE_TIME, now);
//		} 
//		
//		// compose the file names
//		String[] names = new String[2];
//		names[0] = USCTeensGlobals.SENSOR_TYPE + "." + PhoneInfo.getID(mContext) + "." + lastFileName + ".log.csv";
//		names[1] = USCTeensGlobals.SENSOR_TYPE + "." + PhoneInfo.getID(mContext) + "." + lastFileName + ".log.bin";
//		
//		return names;
//	}
//	
//	private String[] getFilePathNamesForSensorData(String[] fileNames) {
//		String[] filePathNames = new String[2];
//		String dateDir = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
//		String hourDir = new SimpleDateFormat("/HH/").format(new Date());
//		
//		filePathNames[0] = USCTeensGlobals.DIRECTORY_PATH + File.separator + 
//			Globals.DATA_DIRECTORY + File.separator + dateDir + USCTeensGlobals.SENSOR_FOLDER + hourDir + fileNames[0];
//		filePathNames[1] = USCTeensGlobals.DIRECTORY_PATH + File.separator + 
//			Globals.DATA_DIRECTORY + File.separator + dateDir + USCTeensGlobals.SENSOR_FOLDER + hourDir + fileNames[1];
//		
//		return filePathNames;
//	}
//	
//	private Object[] getLastMinuteSensorData() {	
//		// Get internal accelerometer data which have been saved in the last minute
//		String readable = "";
//		ArrayList<Object> data = new ArrayList<Object>();	
//		data.add(readable); // dummy		
//		
//		int count = (int) DataStorage.GetValueLong(mContext, DataStorage.KEY_INTERNAL_ACCEL_RECORDING_COUNT, 0);
//		DataStorage.SetValue(mContext, DataStorage.KEY_INTERNAL_ACCEL_RECORDING_COUNT, 0); 
//		for (int i = 1; i <= count; ++i) {	
//			String time = DataStorage.GetValueString(mContext, DataStorage.KEY_INTERNAL_ACCEL_RECORDING_TIME + i, DataStorage.EMPTY);
//			int intAccelAverage = (int) DataStorage.GetValueLong(mContext, DataStorage.KEY_INTERNAL_ACCEL_AVERAGE + i, 0);
//			int intAccelSamples = (int) DataStorage.GetValueLong(mContext, DataStorage.KEY_INTERNAL_ACCEL_SAMPLES + i, 0);
//			readable += time + ", " + intAccelAverage + ", " + intAccelSamples + "\n";
//			
//			String[] split = time.split("[ :,.]");			
//			AccelData binary = new AccelData(split[1], split[2], split[3], split[split.length-1], intAccelAverage, intAccelSamples);
//			data.add(binary);
//		}		
//		data.set(0, readable);
//		
//		return data.toArray();
//	}
//	
//	protected boolean writeSensorToFiles() {
//		boolean result = true;
//				
//		// Get the csv and bin file name
//		String[] fileNames = getFileNamesForSensorData();
//		// Get the data that should be written, for both .csv and .bin
//		Object[] data = getLastMinuteSensorData();
//		// Get the complete file path name
//		String[] filePathNames = getFilePathNamesForSensorData(fileNames);
//		
//		// Write the file. If the file does not exist, create a new one.
//		File csvFile = new File(filePathNames[0]);
//		File binFile = new File(filePathNames[1]);
//		
//		// First write the .csv file
//		try {
//			FileHelper.createDirsIfDontExist(csvFile);
//			// Create the file if it does not exist
//			if (!csvFile.exists()) {				
//				try {
//					csvFile.createNewFile();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				// Write the csv file header
//				FileHelper.appendToFile(DataSource.INTERNAL_ACCEL_DATA_CSVFILEHEADER, filePathNames[0]);				
//			}
//			// Write the data to the file	
//			FileHelper.appendToFile((String) data[0], filePathNames[0]);
//		} catch (WOCKETSException e) {
//			// TODO: 
//			result = false;
//			e.printStackTrace();
//		} finally {
//			;
//		}
//		
//		// Then write the .bin file, we use this file for loading 
//		// because parsing all the strings from .csv file is very slow
//		AccelDataOutputStream oos = null;
//		try { 
//			FileHelper.createDirsIfDontExist(binFile);
//			// Create the file if it does not exist
//			if (!binFile.exists()) {			
//				binFile.createNewFile();							
//			}
//			oos = AccelDataOutputStream.getInstance(binFile, new FileOutputStream(binFile, true));			
//			for (int i = 1; i < data.length; ++i) {
//				oos.writeObject(data[i]); 
//			}			
//		} catch (Exception e) { 
//			result = false;
//			e.printStackTrace();
//		} finally {
//			try {
//				if (oos != null) {
//					oos.close();
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//	
//		return result;
//	}

	public void doArbitrate(boolean isNewSoftwareVersion) {		
		// For testing purpose only
		saveRecordsInLogcat(false);
		
		// record the accelerometer data of the previous minute
//		writeSensorToFiles();

		// Try to prompt the next survey if possible
		mScheduler.tryToPromptSurvey(isNewSoftwareVersion);		
				
		// Mark that arbitration taking place
		DataStorage.setLastTimeArbitrate(mContext, System.currentTimeMillis());			
	}
	
	public static void saveRecordsInLogcat(boolean isClear) {
		StringBuilder log = new StringBuilder();
		try {
			Process process = Runtime.getRuntime().exec("logcat -d -v time");
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

			String line = "";
			while ((line = bufferedReader.readLine()) != null) {
				if (line.contains("uscteens") && (line.contains("29 bytes")) || line.contains("elapsed")) {
					log.append(line + "\r\n");
				}
			}
			if (log.length() > 0)
				saveLogCatRecord(log.toString());
			if (isClear)
				process = Runtime.getRuntime().exec("logcat -c");

		} catch (IOException e) {
			Log.e(TAG, "Could not read or clear logcat.");
		}

	}

	private static void saveLogCatRecord(String log) {
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return;
		}
		Date promptTime = new Date(System.currentTimeMillis());
		SimpleDateFormat folderFormat = new SimpleDateFormat("yyyy-MM-dd");
		String folderPath = Globals.SURVEY_LOG_DIRECTORY + File.separator + folderFormat.format(promptTime);
		File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + folderPath);
		folder.mkdirs();

		File logFile = new File(folder, "logCatRecord.txt");
		try {
			if (!logFile.exists())
				logFile.createNewFile();

			BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true));
			writer.append(log);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
