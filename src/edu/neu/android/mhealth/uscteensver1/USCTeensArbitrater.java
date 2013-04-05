package edu.neu.android.mhealth.uscteensver1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.PriorityQueue;
import java.util.Random;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Environment;
import edu.neu.android.mhealth.uscteensver1.data.AccelData;
import edu.neu.android.mhealth.uscteensver1.data.AccelDataOutputStream;
import edu.neu.android.mhealth.uscteensver1.data.DataSource;
import edu.neu.android.wocketslib.Globals;
import edu.neu.android.wocketslib.dataupload.DataSender;
import edu.neu.android.wocketslib.dataupload.RawUploader;
import edu.neu.android.wocketslib.emasurvey.model.PromptRecorder;
import edu.neu.android.wocketslib.sensormonitor.Arbitrater;
import edu.neu.android.wocketslib.support.AppInfo;
import edu.neu.android.wocketslib.support.DataStorage;
import edu.neu.android.wocketslib.support.ServerLogger;
import edu.neu.android.wocketslib.utils.BasicLogger;
import edu.neu.android.wocketslib.utils.DateHelper;
import edu.neu.android.wocketslib.utils.FileHelper;
import edu.neu.android.wocketslib.utils.Log;
import edu.neu.android.wocketslib.utils.PackageChecker;
import edu.neu.android.wocketslib.utils.PhoneInfo;
import edu.neu.android.wocketslib.utils.WOCKETSException;
//import org.omg.PortableInterceptor.INACTIVE;

public class USCTeensArbitrater extends Arbitrater {
	private static final String TAG = "USCTeensArbitrater";	
	private static final String KEY_CSVFILE_CREATE_TIME = "KEY_CSVFILE_CREATE_TIME";	
	
	private static final String KEY_RANDOM_PROMPT = "_KEY_RANDOM_PROMPT";
	private static final String KEY_CS_PROMPT = "_KEY_CS_PROMPT";
	private static final String KEY_ALL_PROMPT = "_KEY_ALL_PROMPT";
	private static final String KEY_SCHEDULE = "_KEY_SCHEDULE";		

	private static final long PROMPT_OFFSET = 5 * 60 * 1000;

	private static final int KEY_RANDOM_EMA = 0;
	private static final int KEY_CS_EMA = 1;	
	
	private static long inhalerUseTime;

	private static final String NEWLINE = "\n";
	private static final String SKIPLINE = "\n\n";
	
	private static boolean sIsFirstRun = true;

	protected static Context aContext = null;

	/**
	 * Obscure a MAC address so that when this function runs, the Asthmapolis
	 * detection hack does not trigger itself.
	 * 
	 * @param anAddress
	 * @return
	 */

	public USCTeensArbitrater(Context aContext) {
		this.aContext = aContext;
	}

	// Temp for one run
	protected boolean isOkAudioPrompt = false;
	// private static boolean isPostponedPromptWaiting = false;
	// private static Timer alert_timer;

	// Status info
	private ArrayList<Integer> someTasks = new ArrayList<Integer>();

	public void getAndPrintPromptingSchedule() {
		if (Globals.IS_DEBUG) {
			long[] promptTimes = DataStorage.getPromptTimesKey(aContext,
					KEY_RANDOM_PROMPT);
			if (promptTimes != null) {
				for (int i = 0; i < promptTimes.length; i++) {
					Log.d(TAG,
							"Scheduled prompt: "
									+ DateHelper.getDate(promptTimes[i]));
				}
			} else {
				Log.d(TAG, "No Scheduled prompt times");
			}
			promptTimes = DataStorage
					.getPromptTimesKey(aContext, KEY_CS_PROMPT);
			if (promptTimes != null) {
				for (int i = 0; i < promptTimes.length; i++) {
					Log.d(TAG,
							"Triggered prompt: "
									+ DateHelper.getDate(promptTimes[i]));
				}
			} else {
				Log.d(TAG, "No Triggered prompt times");
			}

		}
	}

	private boolean setInhalerTriggeredSchedule(long inhalerUsedTime) {
		inhalerUsedTime += PROMPT_OFFSET;
		long[] somePromptTimes = DataStorage.getPromptTimesKey(aContext,
				KEY_CS_PROMPT);
		if (somePromptTimes == null) {
			long[] finalPromptTimes = new long[] { inhalerUsedTime };
			DataStorage.setPromptTimesKey(aContext, finalPromptTimes,
					KEY_CS_PROMPT);
			return true;
		}
		long latestPromptTime = somePromptTimes[somePromptTimes.length - 1];
		if ((inhalerUsedTime - latestPromptTime) > Globals.MIN_MS_BETWEEN_SCHEDULED_PROMPTS) {
			long[] finalPromptTimes = new long[somePromptTimes.length + 1];
			for (int i = 0; i < somePromptTimes.length; i++) {
				finalPromptTimes[i] = somePromptTimes[i];
			}
			finalPromptTimes[somePromptTimes.length] = inhalerUsedTime;
			DataStorage.setPromptTimesKey(aContext, finalPromptTimes,
					KEY_CS_PROMPT);
			return true;
		}
		return false;
	}

	private void resetSchedule() {
		long[] randomPromptTimes = DataStorage.getPromptTimesKey(aContext,
				KEY_RANDOM_PROMPT);
		long[] csPromptTimes = DataStorage.getPromptTimesKey(aContext,
				KEY_CS_PROMPT);
		PriorityQueue<Long> prompTimes = new PriorityQueue<Long>(
				csPromptTimes.length);
		for (int i = 0; i < csPromptTimes.length; i++) {
			prompTimes.add(csPromptTimes[i]);
		}
		if (randomPromptTimes != null) {
			boolean isExcluded = false;
			for (int j = 0; j < randomPromptTimes.length; j++) {
				isExcluded = false;
				for (int i = 0; i < csPromptTimes.length; i++) {
					if (Math.abs((csPromptTimes[i] - randomPromptTimes[j])) < Globals.MIN_MS_BETWEEN_SCHEDULED_PROMPTS)
						isExcluded = true;
				}
				if (!isExcluded)
					prompTimes.add(randomPromptTimes[j]);
			}
		}
		long[] finalPromptTime = new long[prompTimes.size()];
		for (int j = 0; j < finalPromptTime.length; j++) {
			finalPromptTime[j] = prompTimes.poll();
		}
		DataStorage
				.setPromptTimesKey(aContext, finalPromptTime, KEY_ALL_PROMPT);
	}

	private void setAndSavePromptingSchedule(int promptsPerDay,
			double startTimeHour, double endTimeHour) {
		long totalPromptingWindowMS = (long) ((endTimeHour - startTimeHour) * 60 * 60 * 1000);
		long intervalIncMS = (long) (totalPromptingWindowMS / ((double) promptsPerDay));
		Random r = new Random();
		long promptTimes[] = new long[promptsPerDay];

		int startIntervalTimeMS = (int) (startTimeHour * 60 * 60 * 1000);

		long startDayTime = DateHelper.getDailyTime(0, 0); // Midnight

		StringBuffer promptSchedule = new StringBuffer();

		promptSchedule.append("Scheduled prompts today: " + promptsPerDay
				+ NEWLINE);
		promptSchedule.append("Start hour: " + startTimeHour + NEWLINE);
		promptSchedule.append("End hour: " + endTimeHour + NEWLINE);

		for (int i = 0; i < promptsPerDay; i++) {
			// Add a random number of MS to the first start time block
			promptTimes[i] = startDayTime + startIntervalTimeMS + i
					* intervalIncMS + r.nextInt((int) intervalIncMS);
			Log.i(TAG, "Time to prompt: " + DateHelper.getDate(promptTimes[i]));

			if (i > 0) {
				// Shift any prompts too close together
				if ((promptTimes[i] - promptTimes[i - 1]) < Globals.MIN_MS_BETWEEN_SCHEDULED_PROMPTS) {
					promptTimes[i] += (Globals.MIN_MS_BETWEEN_SCHEDULED_PROMPTS - (promptTimes[i] - promptTimes[i - 1]));
					Log.i(TAG,
							"SHIFTED Time to prompt: "
									+ DateHelper.getDate(promptTimes[i]));
				}
			}

		}

		if (promptsPerDay > 0)
			for (int i = 0; i < promptsPerDay; i++) {
				promptSchedule.append("Prompt: "
						+ DateHelper.getDate(promptTimes[i]) + NEWLINE);
			}

		ServerLogger.sendNote(aContext, promptSchedule.toString(),
				Globals.NO_PLOT);

		// reset prompt schedule for the day
		DataStorage.setPromptTimesKey(aContext, promptTimes, KEY_RANDOM_PROMPT);
		PromptRecorder.writePromptSchedule(aContext,
				System.currentTimeMillis(), KEY_RANDOM_PROMPT, promptsPerDay,
				startTimeHour, endTimeHour);
		DataStorage.setPromptTimesKey(aContext, new long[] { promptsPerDay,
				startIntervalTimeMS, intervalIncMS }, KEY_SCHEDULE);
		DataStorage.setPromptTimesKey(aContext, new long[] {}, KEY_CS_PROMPT);
		DataStorage.setPromptTimesKey(aContext, promptTimes, KEY_ALL_PROMPT);
	}

	/**
	 * This is the key function to determine which tasks are active for
	 * prompting at any given moment. How to prompt is based on which tasks are
	 * active.
	 * 
	 * @param aContext
	 */
	protected void GatherPendingTasks(Context aContext) {
		someTasks.clear();

		String aKey = Globals.SURVEY;
		long currentTime = System.currentTimeMillis();
		long lastTimeCompleted = AppInfo.GetLastTimeCompleted(aContext, aKey);
		if (lastTimeCompleted > currentTime) {
			AppInfo.SetLastTimeCompleted(aContext, aKey, currentTime);
		}
		long lastTimePrompted = AppInfo.GetLastTimePrompted(aContext, aKey);
		if (lastTimeCompleted > currentTime) {
			AppInfo.SetLastTimePrompted(aContext, aKey, currentTime);
		}
		long timeSinceCompleted = currentTime - lastTimeCompleted;
		long timeSincePrompted = currentTime - lastTimePrompted;
		long[] somePromptTimes = DataStorage.getPromptTimesKey(aContext,
				KEY_ALL_PROMPT);
		// Will be 0 if none
		long lastScheduledPromptTime = getLastScheduledPromptTime(currentTime,
				somePromptTimes);
		Log.i(TAG,
				"LastScheduledPromptTime: "
						+ DateHelper.getDate(lastScheduledPromptTime));

		// If no scheduled prompt, just return
		if (lastScheduledPromptTime == 0)
			return;

		if (lastTimeCompleted == 0)
			Log.i(TAG, "LastTimeCompleted: never");
		else
			Log.i(TAG,
					"LastTimeCompleted: "
							+ DateHelper.getDate(lastTimeCompleted));

		// If completed after the last scheduled prompt time, then just return
		if (lastTimeCompleted > lastScheduledPromptTime)
			return;

		if (Globals.IS_DEBUG) {
			Log.d(TAG, "Time from scheduled prompt (min): "
					+ ((currentTime - lastScheduledPromptTime) / 1000 / 60));
			Log.d(TAG, "Time since completed (min): "
					+ (timeSinceCompleted / 1000 / 60));
			Log.d(TAG, "Time since prompted (min): "
					+ (timeSincePrompted / 1000 / 60));
		}

		// Check if time to prompt
		if (((currentTime - lastScheduledPromptTime) < (Globals.REPROMPT_TIMES
				* Globals.REPROMPT_DELAY_MS + Globals.MINUTES_1) && (timeSincePrompted > Globals.REPROMPT_DELAY_MS))) {
			Log.i(TAG, "Prompt!");
			if (isInhalerPrompt(lastScheduledPromptTime))
				someTasks.add(KEY_CS_EMA);
			else
				someTasks.add(KEY_RANDOM_EMA);
		}
		if ((currentTime - lastScheduledPromptTime) < ((Globals.REPROMPT_TIMES + 1)
				* Globals.REPROMPT_DELAY_MS + Globals.MINUTES_10_IN_MS)
				&& timeSincePrompted > ((Globals.REPROMPT_TIMES + 1) * Globals.REPROMPT_DELAY_MS)) {
			NotificationManager notificationManger = (NotificationManager) aContext
					.getSystemService(Context.NOTIFICATION_SERVICE);
			notificationManger.cancel(Globals.SURVEY_NOTIFICATION_ID);
		}
	}

	private boolean isInhalerPrompt(long lastScheduledPromptTime) {
		long[] somePromptTimes = DataStorage.getPromptTimesKey(aContext,
				KEY_CS_PROMPT);
		if (somePromptTimes == null)
			return false;
		for (int i = 0; i < somePromptTimes.length; i++) {
			if (somePromptTimes[i] == lastScheduledPromptTime)
				return true;
		}
		return false;
	}

	// Return 0 if none, otherwise return time
	private long getLastScheduledPromptTime(long currentTime,
			long[] somePromptTimes) {
		long lastTime = 0;

		if (somePromptTimes == null)
			return lastTime;

		for (int i = 0; i < somePromptTimes.length; i++) {
			if (somePromptTimes[i] > currentTime
					&& !isSameMinute(somePromptTimes[i], currentTime))
				return lastTime;
			else
				lastTime = somePromptTimes[i];
		}
		return lastTime;
	}

	private boolean isSameMinute(long timeA, long timeB) {
		Date dateA = new Date(timeA);
		Date dateB = new Date(timeB);
		return (Math.abs(timeA - timeB) < 60)
				&& dateA.getMinutes() == dateB.getMinutes();
	}

	/**
	 * Set which apps are available based on intervention schedule based on days
	 * into the study
	 * 
	 * @param aContext
	 */
	protected void SetAppActivityUsingSchedule(long lastArbitrationTime,
			int studyDay, boolean isNewSoftwareVersion) {
		boolean isForceReset = DataStorage.isForceReset(aContext);
		if ((!DateHelper.isToday(lastArbitrationTime)) || isNewSoftwareVersion
				|| isForceReset) {
			// if (Globals.IS_DEBUG)
			if (isNewSoftwareVersion)
				Log.i(TAG, "Resetting because new software version");
			else if (isForceReset)
				Log.i(TAG, "Resetting because force reset");
			else
				Log.i(TAG, "Resetting because day changed");

			// This is causing a problem so commented out ...
			// Log a few things we want to be sure are logged every day
			// if (VersionChecker.isNewUpdateAvailable(aContext))
			// Log.h(TAG, "NewVersionAvailable", Log.NO_LOG_SHOW);

			PackageChecker.installedPackageLogging(TAG, aContext);

			// // Force download check of all key files, including tutorials
			// Intent i = new Intent(aContext, FileGrabberService.class);
			// i.putExtra(FileGrabberService.EXTRA_FILES_LIST,
			// Globals.MASTER_FILE_LIST);
			// aContext.startService(i);

			BasicLogger.basicLogging(TAG, aContext);

			if (isForceReset) {
				DataStorage.setIsForceReset(aContext, false);
			}

			// Set a lock so main app waits until this is done before doing
			// anything
			// because variables are temporarily cleared then reset. Don't want
			// to
			// access during that.
			DataStorage.setIsInUpdate(aContext, true);

			AppInfo.resetAvailabilityAndTiming(aContext);

			int promptsPerDay = Globals.DEFAULT_PROMPTS_PER_DAY;
			double startTimeHour = Globals.DEFAULT_START_HOUR;
			double endTimeHour = Globals.DEFAULT_END_HOUR;

			Calendar today = Calendar.getInstance();
			if (today.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY
					&& today.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
				promptsPerDay = 3;
				startTimeHour = 15;
				endTimeHour = 21;
			} else {
				promptsPerDay = 7;
				startTimeHour = 7;
				endTimeHour = 21;
			}

			setAndSavePromptingSchedule(promptsPerDay, startTimeHour,
					endTimeHour);

		}
		// if inhaler used, reset schedule
		if (lastArbitrationTime < inhalerUseTime) {
			Log.i(TAG, "Resetting because inhaler used");
			if (setInhalerTriggeredSchedule(inhalerUseTime))
				resetSchedule();
		}
		// Always unset this in case program was updated at awkward time
		// If this is before prior }, it is possible to get stuck always in an
		// update!

		DataStorage.setIsInUpdate(aContext, false); // TODO Change to use a date
													// so this is less brittle
													// to an odd crash
	}

	// TODO Fix to use settings by week
	public static boolean isOkAudioPrompt() {

		return true;
	}
	
	private String[] getFileNamesForSavingInternalAccelData() {
		boolean isNewFileNeeded = false;				
		String lastCSVFileCreateTime = DataStorage.GetValueString(aContext, KEY_CSVFILE_CREATE_TIME, DataStorage.EMPTY);		
		
		if (lastCSVFileCreateTime.compareTo(DataStorage.EMPTY) != 0) {
			String time[] = lastCSVFileCreateTime.split("-");
			// Get create date
			Date createTime = new Date();
			createTime.setYear(Integer.parseInt(time[0]));
			createTime.setMonth(Integer.parseInt(time[1]));
			createTime.setDate(Integer.parseInt(time[2]));
			createTime.setMinutes(0);
			createTime.setSeconds(0);
			// Compare the last create date and the current date 
			// to figure out whether a new file should be created	
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.HOUR, -1);
			Date curTimeMinus1Hour = calendar.getTime();
			isNewFileNeeded = curTimeMinus1Hour.getTime() >= createTime.getTime();	
		} else {
			// First time to save the csv file, just create a new one
			isNewFileNeeded = true;
		}

		// check whether a new file should be created. If the answer is true, create a new file name
		if (isNewFileNeeded) {
			Date curTime = new Date();
			lastCSVFileCreateTime = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS").format(curTime);							
			DataStorage.SetValue(aContext, KEY_CSVFILE_CREATE_TIME, lastCSVFileCreateTime);
		}
		
		// compose the file names
		String[] names = new String[2];
		names[0] = USCTeensGlobals.SENSOR_TYPE + "." + PhoneInfo.getID(aContext)
				+ "." + lastCSVFileCreateTime + ".log.csv";
		names[1] = USCTeensGlobals.SENSOR_TYPE + "." + PhoneInfo.getID(aContext)
				+ "." + lastCSVFileCreateTime + ".log.bin";
		
		return names;
	}
	
	private String[] getFilePathNamesForSavingInternalAccelData(String[] fileNames) {
		String[] filePathNames = new String[2];
		String dateDir = new SimpleDateFormat("yyyy-MM-dd/HH/").format(new Date());
		
		filePathNames[0] = Globals.EXTERNAL_DIRECTORY_PATH + File.separator + 
				Globals.DATA_DIRECTORY + USCTeensGlobals.SENSOR_FOLDER + dateDir + fileNames[0];
		filePathNames[1] = Globals.EXTERNAL_DIRECTORY_PATH + File.separator + 
				Globals.DATA_DIRECTORY + USCTeensGlobals.SENSOR_FOLDER + dateDir + fileNames[1];
		
		return filePathNames;
	}
	
	private Object[] getInternalAccelDataForSaving() {	
		// Get internal accelerometer data which have been saved in the last minute
		String readable = "";
		ArrayList<Object> data = new ArrayList<Object>();	
		data.add(readable); // dummy		
		
//		int maxSeconds = USCTeensGlobals.TIME_FOR_WAITING_INTERNAL_ACCELEROMETER / 1000;
		int count = (int) DataStorage.GetValueLong(aContext, 
				DataStorage.KEY_INTERNAL_ACCEL_RECORDING_COUNT, 0);
		for (int i = 1; i <= count; ++i) {	
			String time = DataStorage.GetValueString(aContext, 
					DataStorage.KEY_INTERNAL_ACCEL_RECORDING_TIME + i, DataStorage.EMPTY);
			int intAccelAverage = (int) DataStorage.GetValueLong(
					aContext, DataStorage.KEY_INTERNAL_ACCEL_AVERAGE + i, 0);
			int intAccelSamples = (int) DataStorage.GetValueLong(
					aContext, DataStorage.KEY_INTERNAL_ACCEL_SAMPLES + i, 0);
			readable += time + ", " + intAccelAverage + ", " + intAccelSamples + "\n";
			
			String[] split = time.split("[ :,.]");
			AccelData binary = new AccelData(split[1], split[2], split[3], split[5], intAccelAverage, intAccelSamples);
			data.add(binary);
		}		
		data.set(0, readable);
		
		return data.toArray();
	}
	
	protected boolean writePhoneAccelDataToInternalDirectory() {
		boolean result = true;
				
		// Get the csv and bin file name
		String[] fileNames = getFileNamesForSavingInternalAccelData();
		// Get the data that should be written, for both .csv and .bin
		Object[] data = getInternalAccelDataForSaving();
		// Get the whole file path name
		String[] filePathNames = getFilePathNamesForSavingInternalAccelData(fileNames);
		
		// Write the file. If the file does not exist, create a new one.ll 
		File csvFile = new File(filePathNames[0]);
		File binFile = new File(filePathNames[1]);
		
		// First write the .csv file
		try {
			FileHelper.createDirsIfDontExist(csvFile);
			// Create the file if it does not exist
			if (!csvFile.exists()) {				
				try {
					csvFile.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// Write the file header immediately
				FileHelper.appendToFile(DataSource.INTERNAL_ACCEL_DATA_CSVFILEHEADER, filePathNames[0]);				
			}
			// Write the data to the internal sensor folder
			FileHelper.appendToFile((String) data[0], filePathNames[0]);
		} catch (WOCKETSException e) {
			// TODO: 
			result = false;
			e.printStackTrace();
		} finally {
			;
		}
		
		// Then write the .bin file, we use this file for loading 
		// because parsing all the strings in .csv file is very slow
		AccelDataOutputStream oos = null;
		try { 
			FileHelper.createDirsIfDontExist(binFile);
			// Create the file if it does not exist
			if (!binFile.exists()) {			
				binFile.createNewFile();							
			}
			oos = AccelDataOutputStream.getInstance(binFile, new FileOutputStream(binFile, true));			
			for (int i = 1; i < data.length; ++i) {
				oos.writeObject(data[i]); 
			}			
		} catch (Exception e) { 
			result = false;
			e.printStackTrace();
		} finally {
			try {
				if (oos != null) {
					oos.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
//		if (!isSaved) {
//			Log.e(TAG, "Error. Could not save internal acceleromter data to internal storage." + fileName);
//		}
		
		return result;
	}

	public void doArbitrate(boolean isNewSoftwareVersion) {

		/**
		 * This is the app that decides what to do in terms of prompting each
		 * time it is called.
		 */
		if (Globals.IS_DEBUG) {		
			Log.d(TAG, "Begin arbitrate");			
		}
		
		if (sIsFirstRun) {
			sIsFirstRun = false;			
		} else {
			writePhoneAccelDataToInternalDirectory();
		}
		
		// wait for the internal AC sensor to get enough data for the entire 20s
		try {
			synchronized (this) {				
				if (Globals.IS_DEBUG) {	Log.d(TAG, "Wait for internal AC sensor for 20s"); }
				wait(USCTeensGlobals.TIME_FOR_WAITING_INTERNAL_ACCELEROMETER + 1000);
				if (Globals.IS_DEBUG) {	Log.d(TAG, "Wait for internal AC sensor finished"); }				
			}			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Temporary
//		getAndPrintPromptingSchedule();

		// Mark that arbitration taking place
//		long lastArbitrationTime = DataStorage.getLastTimeArbitrate(aContext, 0);
//		DataStorage.setLastTimeArbitrate(aContext, System.currentTimeMillis());
//		int studyDay = DataStorage.getDayNumber(aContext, true);

		// Set which apps are available based on the day of the study
//		SetAppActivityUsingSchedule(lastArbitrationTime, studyDay, isNewSoftwareVersion);

//		isOkAudioPrompt = isOkAudioPrompt();

		if (Globals.IS_DEBUG) {
			Log.d(TAG, "End arbitrate");
		}				
	}

	private static final String KEY_MOVE_LOG_TO_EXTERNAL = "_KEY_MOVETOEXTERNAL";
	private static final String KEY_UPLOAD_JSON = "_KEY_UPLOAD_JSON";

	private void uploadDataAndLogToServer(Context aContext) {
		long lastUploadJSONToServer = DataStorage.GetValueLong(aContext, KEY_UPLOAD_JSON, -1);
		long lastMoveLogExternalTime = DataStorage.GetValueLong(aContext, KEY_MOVE_LOG_TO_EXTERNAL, -1);

		long currentTime = System.currentTimeMillis();
		if ((currentTime - lastUploadJSONToServer) > 60 * 60 * 1000) {
			// send JSON file
			String msg = "Starting 1-hour file upload";
			// Move JSON to external upload folder
			DataSender.sendInternalUploadDataToExternalUploadDir(aContext, false, true);

			if ((currentTime - lastMoveLogExternalTime) > 24 * 60 * 60 * 1000) {
				// send logs
				msg += " and 24-hour log and survey files upload";
				// Move Standard Log files to external upload folder (compress and do not include today)
				DataSender.sendLogsToExternalUploadDir(aContext, true, false);
				
				// TODO check where these files are saved 
				// Move Survey Log files to upload folder
				DataSender.sendExternalSurveyLogsToExternalUploadDir(aContext, true, false);

				// Move Data Log files to upload folder 
				DataSender.sendExternalDataLogsToExternalUploadDir(aContext, true, false);

				DataStorage.SetValue(aContext, KEY_MOVE_LOG_TO_EXTERNAL, currentTime);
			}
			Log.i(TAG, msg);
			ServerLogger.transmitOrQueueNote(aContext, msg, true);
			// Upload JSON files and remove (dont backup)
			int filesRemaining = RawUploader.uploadDataFromExternalUploadDir(aContext, true, true, true, false, .85, true);
			// Upload Log and SurveyLog files, backup and remove
			filesRemaining = RawUploader.uploadDataFromExternalUploadDir(aContext, false, true, true, true, .85, true);

			msg = "Completed file upload after " + String.format("%.1f", ((System.currentTimeMillis() - currentTime) / 1000.0 / 60.0))
					+ " minutes. Files remaining to upload: " + filesRemaining;
			ServerLogger.sendNote(aContext, msg, true);
			Log.i(TAG, msg);

			DataStorage.SetValue(aContext, KEY_UPLOAD_JSON, currentTime);
		}
	}

	public static void saveRecordsInLogcat(boolean isClear) {
		StringBuilder log = new StringBuilder();
		try {
			Process process = Runtime.getRuntime().exec("logcat -d -v time");
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(process.getInputStream()));

			String line = "";
			while ((line = bufferedReader.readLine()) != null) {
				if (line.contains("Asthma") && (line.contains("29 bytes"))
						|| line.contains("elapsed")) {
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
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return;
		}
		Date promptTime = new Date(System.currentTimeMillis());
		SimpleDateFormat folderFormat = new SimpleDateFormat("yyyy-MM-dd");
		String folderPath = Globals.SURVEY_LOG_DIRECTORY + File.separator
				+ "SurveyRecord_" + folderFormat.format(promptTime);
		File folder = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath() + File.separator + folderPath);
		folder.mkdirs();

		File logFile = new File(folder, "logCatRecord.txt");
		try {
			if (!logFile.exists())
				logFile.createNewFile();

			BufferedWriter writer = new BufferedWriter(new FileWriter(logFile,
					true));
			writer.append(log);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
