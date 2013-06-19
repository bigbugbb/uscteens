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
import android.content.Intent;
import android.os.Environment;
import edu.neu.android.mhealth.uscteensver1.data.AccelData;
import edu.neu.android.mhealth.uscteensver1.data.AccelDataChecker;
import edu.neu.android.mhealth.uscteensver1.data.AccelDataOutputStream;
import edu.neu.android.mhealth.uscteensver1.data.ContextSensitiveState;
import edu.neu.android.mhealth.uscteensver1.data.DataSource;
import edu.neu.android.mhealth.uscteensver1.survey.CSTeensSurvey;
import edu.neu.android.mhealth.uscteensver1.survey.RandomTeensSurvey;
import edu.neu.android.wocketslib.Globals;
import edu.neu.android.wocketslib.dataupload.DataSender;
import edu.neu.android.wocketslib.dataupload.RawUploader;
import edu.neu.android.wocketslib.emasurvey.SurveyActivity;
import edu.neu.android.wocketslib.emasurvey.model.PromptRecorder;
import edu.neu.android.wocketslib.emasurvey.model.QuestionSet;
import edu.neu.android.wocketslib.emasurvey.model.QuestionSetParamHandler;
import edu.neu.android.wocketslib.emasurvey.model.SurveyPromptEvent;
import edu.neu.android.wocketslib.emasurvey.model.SurveyPromptEvent.PROMPT_AUDIO;
import edu.neu.android.wocketslib.sensormonitor.Arbitrater;
import edu.neu.android.wocketslib.support.AppInfo;
import edu.neu.android.wocketslib.support.DataStorage;
import edu.neu.android.wocketslib.support.ServerLogger;
import edu.neu.android.wocketslib.utils.DateHelper;
import edu.neu.android.wocketslib.utils.FileHelper;
import edu.neu.android.wocketslib.utils.Log;
import edu.neu.android.wocketslib.utils.PackageChecker;
import edu.neu.android.wocketslib.utils.PhoneInfo;
import edu.neu.android.wocketslib.utils.PhonePrompter;
import edu.neu.android.wocketslib.utils.PhoneVibrator;
import edu.neu.android.wocketslib.utils.WOCKETSException;
//import org.omg.PortableInterceptor.INACTIVE;

public class USCTeensArbitrater extends Arbitrater {
	///////////////////////////////////////////////////////////////////////////
	private static final String TAG = "USCTeensArbitrater";	
	private static final String KEY_CSVFILE_CREATE_TIME = "KEY_CSVFILE_CREATE_TIME";	
	
	private static final String KEY_RANDOM_PROMPT = "_KEY_RANDOM_PROMPT";
	private static final String KEY_CS_PROMPT = "_KEY_CS_PROMPT";
	private static final String KEY_ALL_PROMPT = "_KEY_ALL_PROMPT";
	private static final String KEY_SCHEDULE = "_KEY_SCHEDULE";		

	private static final long PROMPT_OFFSET = 5 * 60 * 1000;

	private static final int KEY_CS_EMA     = 1;
	private static final int KEY_RANDOM_EMA = 0;	
	private static final String KEY_RANDOM_PROMPT_COUNTER = "_KEY_RANDOM_PROMPT_COUNTER";
	private static final String KEY_CS_PROMPT_COUNTER = "_KEY_CS_PROMPT_COUNTER";

	private static final String NEWLINE = "\n";
	private static final String SKIPLINE = "\n\n";
	private static final long ONE_MINUTE = 60 * 1000;
	private static final String KEY_LAST_CHECK_TIME = "_KEY_LAST_CHECK_TIME";
	
	private static boolean sIsFirstRun = true;
	private static Context sContext = null;


	public USCTeensArbitrater(Context aContext) {
		sContext = aContext;
	}

	private String[] getFileNamesForSavingInternalAccelData() {
		boolean isNewFileNeeded = false;				
		String lastCSVFileCreateTime = DataStorage.GetValueString(sContext, KEY_CSVFILE_CREATE_TIME, DataStorage.EMPTY);		
		
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
			DataStorage.SetValue(sContext, KEY_CSVFILE_CREATE_TIME, lastCSVFileCreateTime);
		}
		
		// compose the file names
		String[] names = new String[2];
		names[0] = USCTeensGlobals.SENSOR_TYPE + "." + PhoneInfo.getID(sContext)
				+ "." + lastCSVFileCreateTime + ".log.csv";
		names[1] = USCTeensGlobals.SENSOR_TYPE + "." + PhoneInfo.getID(sContext)
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
		int count = (int) DataStorage.GetValueLong(sContext, 
				DataStorage.KEY_INTERNAL_ACCEL_RECORDING_COUNT, 0);
		DataStorage.SetValue(sContext, DataStorage.KEY_INTERNAL_ACCEL_RECORDING_COUNT, 0); 
		for (int i = 1; i <= count; ++i) {	
			String time = DataStorage.GetValueString(sContext, 
					DataStorage.KEY_INTERNAL_ACCEL_RECORDING_TIME + i, DataStorage.EMPTY);
			int intAccelAverage = (int) DataStorage.GetValueLong(
					sContext, DataStorage.KEY_INTERNAL_ACCEL_AVERAGE + i, 0);
			int intAccelSamples = (int) DataStorage.GetValueLong(
					sContext, DataStorage.KEY_INTERNAL_ACCEL_SAMPLES + i, 0);
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
		// Get the complete file path name
		String[] filePathNames = getFilePathNamesForSavingInternalAccelData(fileNames);
		
		// Write the file. If the file does not exist, create a new one.
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
		// because parsing all the strings from .csv file is very slow
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

	// Temp for one run
	protected boolean isOkAudioPrompt = false;
	// private static boolean isPostponedPromptWaiting = false;
	// private static Timer alert_timer;

	// Status info
	private ArrayList<Integer> someTasks = new ArrayList<Integer>();

	// TODO change to private
	private void PromptApp(Context aContext, int aKey, boolean isAudible, boolean isPostponed) {

		Log.i(TAG, "prompt: " + aKey + ",audible: " + isAudible + ",postponed: " + isPostponed);

		long[] somePromptTimes = DataStorage.getPromptTimesKey(aContext, KEY_ALL_PROMPT);
		// Will be 0 if none
		long lastScheduledPromptTime = getLastScheduledPromptTime(System.currentTimeMillis(), somePromptTimes);
		boolean isReprompt = (System.currentTimeMillis() - lastScheduledPromptTime) > 60 * 1000 ? true : false;
		SurveyPromptEvent promptEvent = new SurveyPromptEvent(lastScheduledPromptTime, System.currentTimeMillis());
		String msg = "";
		
		// Indicate that this particular app was prompted
		AppInfo.SetLastTimePrompted(aContext, Globals.SURVEY, System.currentTimeMillis());
		
		Intent i = new Intent(aContext, SurveyActivity.class);
		long lastTimeCompleted = AppInfo.GetLastTimeCompleted(aContext, Globals.SURVEY);
		long currentTime = System.currentTimeMillis();
		int classType = 0;
		String surveyName = null;
		int counter;
		
		switch (aKey) {
		case KEY_CS_EMA:
			counter = (int) DataStorage.GetValueLong(aContext, KEY_CS_PROMPT_COUNTER, 0);
			if (!isReprompt) {				
				DataStorage.SetValue(aContext, KEY_CS_PROMPT_COUNTER, ++counter);
			}
			promptEvent.AddSurveySpecifiedRecord("CS_NUM", counter + "");
			if (currentTime - lastTimeCompleted < 4 * 60 * ONE_MINUTE) {
				classType = CSTeensSurvey.CS_EMA_DEFAULT;
				promptEvent.AddSurveySpecifiedRecord("CS_SPAN", "1");
			} else {
				classType = CSTeensSurvey.CS_EMA_OPTIONAL;
				promptEvent.AddSurveySpecifiedRecord("CS_SPAN", "2");
			}
			surveyName = CSTeensSurvey.class.getCanonicalName();
			msg = PhonePrompter.StartPhoneAlert(TAG, aContext, true, PhonePrompter.CHIMES_NAMBOKU1, PhoneVibrator.VIBRATE_INTENSE);						
			promptEvent.setPromptType("Teen Activity");
			break;
		case KEY_RANDOM_EMA:
			counter = (int) DataStorage.GetValueLong(aContext, KEY_RANDOM_PROMPT_COUNTER, 0);
			if (!isReprompt) {				
				DataStorage.SetValue(aContext, KEY_RANDOM_PROMPT_COUNTER, ++counter);
			}
			promptEvent.AddSurveySpecifiedRecord("RAN_NUM", counter + "");
			if ((currentTime - lastTimeCompleted) < 4 * 60 * ONE_MINUTE) {
				classType = RandomTeensSurvey.RANDOM_EMA_DEFAULT;
				promptEvent.AddSurveySpecifiedRecord("RAN_SPAN", "1");
			} else {
				classType = RandomTeensSurvey.RANDOM_EMA_OPTIONAL;
				promptEvent.AddSurveySpecifiedRecord("RAN_SPAN", "2");
			}
			surveyName = RandomTeensSurvey.class.getCanonicalName();
			msg = PhonePrompter.StartPhoneAlert(TAG, aContext, isAudible, PhonePrompter.CHIMES_HIKARI, PhoneVibrator.VIBRATE_INTENSE);
			promptEvent.setPromptType("Random");
			long[] schedule = DataStorage.getPromptTimesKey(aContext, KEY_SCHEDULE);
			if (schedule != null && schedule.length >= 3)
				promptEvent.setPromptSchedule(lastScheduledPromptTime, (int) schedule[0], (int) schedule[1], schedule[2]);
			break;
		}
		if (msg.toLowerCase().contains("silence"))
			promptEvent.setPromptAudio(PROMPT_AUDIO.NONE);
		else if (msg.toLowerCase().contains("normal"))
			promptEvent.setPromptAudio(PROMPT_AUDIO.AUDIO);
		else if (msg.toLowerCase().contains("vibrate"))
			promptEvent.setPromptAudio(PROMPT_AUDIO.VIBRATION);
		promptEvent.setReprompt(isReprompt);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.putExtra(QuestionSet.TAG, new QuestionSetParamHandler(1, new Object[] { classType }));
		i.putExtra("className", surveyName);
		i.putExtra("promptEvent", promptEvent);
		if (!isReprompt && SurveyActivity.self != null)
			SurveyActivity.self.finish();
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		aContext.startActivity(i);
		// aContext.startActivity(appIntentToRun);
		// appIntentToRun = null;
		msg += " Is reprompt: " + isReprompt;
		// Give the audio or vibration time to work
		// I think a code change in Bluetoothsensorservice makes this not
		// necessary.
		// try {
		// Thread.sleep(4000);
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

	}

	public void getAndPrintPromptingSchedule() {
		if (Globals.IS_DEBUG) {
			long[] promptTimes = DataStorage.getPromptTimesKey(sContext, KEY_RANDOM_PROMPT);
			if (promptTimes != null) {
				for (int i = 0; i < promptTimes.length; i++) {
					Log.d(TAG, "Scheduled prompt: " + DateHelper.getDate(promptTimes[i]));
				}
			} else {
				Log.d(TAG, "No Scheduled prompt times");
			}
			promptTimes = DataStorage.getPromptTimesKey(sContext, KEY_CS_PROMPT);
			if (promptTimes != null) {
				for (int i = 0; i < promptTimes.length; i++) {
					Log.d(TAG, "Triggered prompt: " + DateHelper.getDate(promptTimes[i]));
				}
			} else {
				Log.d(TAG, "No Triggered prompt times");
			}

		}
	}

	private boolean setActivityTriggeredSchedule(long activityTriggeredTime) {
		activityTriggeredTime += PROMPT_OFFSET;
		long[] somePromptTimes = DataStorage.getPromptTimesKey(sContext, KEY_CS_PROMPT);
		if (somePromptTimes == null) {
			long[] finalPromptTimes = new long[] { activityTriggeredTime };
			DataStorage.setPromptTimesKey(sContext, finalPromptTimes, KEY_CS_PROMPT);
			return true;
		}
		long latestPromptTime = somePromptTimes[somePromptTimes.length - 1];
		if ((activityTriggeredTime - latestPromptTime) > Globals.MIN_MS_BETWEEN_SCHEDULED_PROMPTS) {
			long[] finalPromptTimes = new long[somePromptTimes.length + 1];
			for (int i = 0; i < somePromptTimes.length; i++) {
				finalPromptTimes[i] = somePromptTimes[i];
			}
			finalPromptTimes[somePromptTimes.length] = activityTriggeredTime;
			DataStorage.setPromptTimesKey(sContext, finalPromptTimes, KEY_CS_PROMPT);
			return true;
		}
		return false;
	}

	private void resetSchedule() {
		long[] randomPromptTimes = DataStorage.getPromptTimesKey(sContext, KEY_RANDOM_PROMPT);
		long[] csPromptTimes = DataStorage.getPromptTimesKey(sContext, KEY_CS_PROMPT);
		PriorityQueue<Long> prompTimes = new PriorityQueue<Long>(csPromptTimes.length);
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
		DataStorage.setPromptTimesKey(sContext, finalPromptTime, KEY_ALL_PROMPT);
	}

	private void setAndSavePromptingSchedule(int promptsPerDay, double startTimeHour, double endTimeHour) {
		long totalPromptingWindowMS = (long) ((endTimeHour - startTimeHour) * 60 * 60 * 1000);
		long intervalIncMS = (long) (totalPromptingWindowMS / ((double) promptsPerDay));
		Random r = new Random();
		long promptTimes[] = new long[promptsPerDay];

		int startIntervalTimeMS = (int) (startTimeHour * 60 * 60 * 1000);

		long startDayTime = DateHelper.getDailyTime(0, 0); // Midnight

		StringBuffer promptSchedule = new StringBuffer();

		promptSchedule.append("Scheduled prompts today: " + promptsPerDay + NEWLINE);
		promptSchedule.append("Start hour: " + startTimeHour + NEWLINE);
		promptSchedule.append("End hour: " + endTimeHour + NEWLINE);

		for (int i = 0; i < promptsPerDay; i++) {
			// Add a random number of MS to the first start time block
			promptTimes[i] = startDayTime + startIntervalTimeMS + i * intervalIncMS + r.nextInt((int) intervalIncMS);
			Log.i(TAG, "Time to prompt: " + DateHelper.getDate(promptTimes[i]));

			if (i > 0) {
				// Shift any prompts too close together
				if ((promptTimes[i] - promptTimes[i - 1]) < Globals.MIN_MS_BETWEEN_SCHEDULED_PROMPTS) {
					promptTimes[i] += (Globals.MIN_MS_BETWEEN_SCHEDULED_PROMPTS - (promptTimes[i] - promptTimes[i - 1]));
					Log.i(TAG, "SHIFTED Time to prompt: " + DateHelper.getDate(promptTimes[i]));
				}
			}

		}

		if (promptsPerDay > 0)
			for (int i = 0; i < promptsPerDay; i++) {
				promptSchedule.append("Prompt: " + DateHelper.getDate(promptTimes[i]) + NEWLINE);
			}

		ServerLogger.sendNote(sContext, promptSchedule.toString(), Globals.NO_PLOT);

		// reset prompt schedule for the day
		DataStorage.setPromptTimesKey(sContext, promptTimes, KEY_RANDOM_PROMPT);
		PromptRecorder.writePromptSchedule(sContext, System.currentTimeMillis(), KEY_RANDOM_PROMPT, promptsPerDay, startTimeHour, endTimeHour);
		DataStorage.setPromptTimesKey(sContext, new long[] { promptsPerDay, startIntervalTimeMS, intervalIncMS }, KEY_SCHEDULE);
		DataStorage.setPromptTimesKey(sContext, new long[] {}, KEY_CS_PROMPT);
		DataStorage.setPromptTimesKey(sContext, promptTimes, KEY_ALL_PROMPT);
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
		long[] somePromptTimes = DataStorage.getPromptTimesKey(aContext, KEY_ALL_PROMPT);
		// Will be 0 if none
		long lastScheduledPromptTime = getLastScheduledPromptTime(currentTime, somePromptTimes);
		Log.i(TAG, "LastScheduledPromptTime: " + DateHelper.getDate(lastScheduledPromptTime));

		// If no scheduled prompt, just return
		if (lastScheduledPromptTime == 0)
			return;

		if (lastTimeCompleted == 0)
			Log.i(TAG, "LastTimeCompleted: never");
		else
			Log.i(TAG, "LastTimeCompleted: " + DateHelper.getDate(lastTimeCompleted));

		// If completed after the last scheduled prompt time, then just return
		if (lastTimeCompleted > lastScheduledPromptTime)
			return;

		if (Globals.IS_DEBUG) {
			Log.d(TAG, "Time from scheduled prompt (min): " + ((currentTime - lastScheduledPromptTime) / 1000 / 60));
			Log.d(TAG, "Time since completed (min): " + (timeSinceCompleted / 1000 / 60));
			Log.d(TAG, "Time since prompted (min): " + (timeSincePrompted / 1000 / 60));
		}

		// Check if time to prompt
		if (((currentTime - lastScheduledPromptTime) < (Globals.REPROMPT_TIMES * Globals.REPROMPT_DELAY_MS + Globals.MINUTES_1) && (timeSincePrompted > Globals.REPROMPT_DELAY_MS))) {
			Log.i(TAG, "Prompt!");
			if (isActivityPrompt(lastScheduledPromptTime))
				someTasks.add(KEY_CS_EMA);
			else
				someTasks.add(KEY_RANDOM_EMA);
		}
		if ((currentTime - lastScheduledPromptTime) < ((Globals.REPROMPT_TIMES + 1) * Globals.REPROMPT_DELAY_MS + Globals.MINUTES_10_IN_MS)
				&& timeSincePrompted > ((Globals.REPROMPT_TIMES + 1) * Globals.REPROMPT_DELAY_MS)) {
			NotificationManager notificationManger = (NotificationManager) aContext.getSystemService(Context.NOTIFICATION_SERVICE);
			notificationManger.cancel(Globals.SURVEY_NOTIFICATION_ID);
		}
	}

	private boolean isActivityPrompt(long lastScheduledPromptTime) {
		long[] somePromptTimes = DataStorage.getPromptTimesKey(sContext, KEY_CS_PROMPT);
		if (somePromptTimes == null)
			return false;
		for (int i = 0; i < somePromptTimes.length; i++) {
			if (somePromptTimes[i] == lastScheduledPromptTime)
				return true;
		}
		return false;
	}

	// Return 0 if none, otherwise return time
	private long getLastScheduledPromptTime(long currentTime, long[] somePromptTimes) {
		long lastTime = 0;

		if (somePromptTimes == null)
			return lastTime;

		for (int i = 0; i < somePromptTimes.length; i++) {
			if (somePromptTimes[i] > currentTime && !isSameMinute(somePromptTimes[i], currentTime))
				return lastTime;
			else
				lastTime = somePromptTimes[i];
		}
		return lastTime;
	}

	private boolean isSameMinute(long timeA, long timeB) {
		Date dateA = new Date(timeA);
		Date dateB = new Date(timeB);
		return (Math.abs(timeA - timeB) < 60) && dateA.getMinutes() == dateB.getMinutes();
	}

	/**
	 * Set which apps are available based on intervention schedule based on days
	 * into the study
	 * 
	 * @param aContext
	 */
	protected void SetAppActivityUsingSchedule(long lastArbitrationTime, int studyDay, boolean isNewSoftwareVersion) {
		boolean isForceReset = DataStorage.isForceReset(sContext);
		if ((!DateHelper.isToday(lastArbitrationTime)) || isNewSoftwareVersion || isForceReset) {
			// if (Globals.IS_DEBUG)
			if (isNewSoftwareVersion)
				Log.i(TAG, "Resetting because new software version");
			else if (isForceReset)
				Log.i(TAG, "Resetting because force reset");
			else
				Log.i(TAG, "Resetting because day changed");

			PackageChecker.installedPackageLogging(TAG, sContext);

			if (isForceReset) {
				DataStorage.setIsForceReset(sContext, false);
			}

			// Set a lock so main app waits until this is done before doing
			// anything
			// because variables are temporarily cleared then reset. Don't want
			// to access during that.
			DataStorage.setIsInUpdate(sContext, true);

			AppInfo.resetAvailabilityAndTiming(sContext);

			int promptsPerDay    = Globals.DEFAULT_PROMPTS_PER_DAY;
			double startTimeHour = Globals.DEFAULT_START_HOUR;
			double endTimeHour   = Globals.DEFAULT_END_HOUR;

			Calendar today = Calendar.getInstance();
			if (today.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && today.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
				promptsPerDay = 3;
				startTimeHour = 15;
				endTimeHour   = 21;
			} else {
				promptsPerDay = 7;
				startTimeHour = 8;
				endTimeHour   = 22;
			}

			setAndSavePromptingSchedule(promptsPerDay, startTimeHour, endTimeHour);
		}
		
		// Check the previous 30+ minutes accelerometer data state every 10 minutes
		// to determine whether the context sensitive schedule should be reset
		long now  = System.currentTimeMillis();
		long to   = now - ONE_MINUTE;
		long from = to - 45 * ONE_MINUTE;
		long lastCheckTime = DataStorage.GetValueLong(sContext, KEY_LAST_CHECK_TIME, now);
		if (now - lastCheckTime >= 10 * ONE_MINUTE) {
			ContextSensitiveState css = AccelDataChecker.checkDataState(from, to);
			if (css.getState() != ContextSensitiveState.DATA_STATE_ERROR && 
				css.getState() != ContextSensitiveState.DATA_STATE_NORMAL) {
				Log.i(TAG, "Resetting because activity is detected or missed");						
				if (isOkActivityPrompt()) {				
					if (setActivityTriggeredSchedule(now)) {
						resetSchedule();
						CSTeensSurvey.setLatestPromptTime(css.getStartTime(), css.getEndTime());
					}
				}
			}
			DataStorage.SetValue(sContext, KEY_LAST_CHECK_TIME, now);
		}

		// Always unset this in case program was updated at awkward time
		// If this is before prior }, it is possible to get stuck always in an
		// update!

		DataStorage.setIsInUpdate(sContext, false); // TODO Change to use a date
													// so this is less brittle
													// to an odd crash
	}
	
	private static boolean isOkActivityPrompt() {
		boolean isOkPrompt = false;
		Date cur = new Date();
		int hour = cur.getHours();
		int min  = cur.getMinutes();
		Calendar today = Calendar.getInstance();
		
		int dayOfWeek = today.get(Calendar.DAY_OF_WEEK);
		if (dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY) {			
			isOkPrompt = (hour >= 15 && hour < 20) || (hour == 20 && min < 45);
		} else {			
			isOkPrompt = (hour >= 8 && hour < 22);			
		}
		
		return isOkPrompt;
	}

	// TODO Fix to use settings by week
	public static boolean isOkAudioPrompt() {
		// Date now = new Date();
		// int hour = now.getHours();
		// int min = now.getMinutes();
		// Calendar today = Calendar.getInstance();
		// if (today.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY
		// && today.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
		// if((hour >= 6 && hour < 20)||(hour == 20 && min < 30))
		// return true;
		// else
		// return false;
		// } else {
		// if(hour >= 7 && hour < 21)
		// return true;
		// else
		// return false;
		// }
		return true;
	}

	public void doArbitrate(boolean isNewSoftwareVersion) {

		// Debug: save all Asthmapolis and related messages to SD card
		// Only for testing purpose
		saveRecordsInLogcat(false);
		
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

		/**
		 * This is the app that decides what to do in terms of prompting each
		 * time it is called.
		 */
		// Mark that arbitration taking place
		long lastArbitrationTime = DataStorage.getLastTimeArbitrate(sContext, 0);
		DataStorage.setLastTimeArbitrate(sContext, System.currentTimeMillis());
		int studyDay = DataStorage.getDayNumber(sContext, true);				
		
		// Set which apps are available based on the day of the study
		SetAppActivityUsingSchedule(lastArbitrationTime, studyDay, isNewSoftwareVersion);

		getAndPrintPromptingSchedule();

		isOkAudioPrompt = isOkAudioPrompt();

		// if (Globals.IS_DEBUG)
		// Log.e(TAG, "IS OK TO AUDIO PROMPT: " + isOkAudioPrompt);
		// // Log.h(TAG, aDataStore.GetSummaryString(getApplicationContext()));
		// if (Globals.IS_DEBUG)
		// Log.e(TAG, "STUDY DAY: " +
		// DataStorage.getDayNumber(getApplicationContext(), true));
		// if (Globals.IS_DEBUG)
		// Log.e(TAG, AppInfo.AllAppStatus(getApplicationContext()));

		// Determine which apps are in the task list as needing to run
		// Sets the postponed app info as well
		GatherPendingTasks(sContext);

		int aKey = 0;

		// Now just check tasks
		if (someTasks.size() > 0) {
			aKey = someTasks.get(0);
			PromptApp(sContext, aKey, isOkAudioPrompt, false);
		}

		// move the data from internal memory to external memory hourly
		// TODO SSI Remove to make testing simpler 
		uploadDataAndLogToServer(sContext);

		if (Globals.IS_DEBUG) 
			Log.d(TAG, "End arbitrate");
	}

	private static final String KEY_MOVE_LOG_TO_EXTERNAL = "_KEY_MOVETOEXTERNAL";
	private static final String KEY_UPLOAD_JSON = "_KEY_UPLOAD_JSON";

	// TODO make sure this is correct and working
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
			int filesRemaining = RawUploader.uploadDataFromExternalUploadDir(aContext, true, true, true, false, .85, false);
			// Upload Log and SurveyLog files, backup and remove
			filesRemaining = RawUploader.uploadDataFromExternalUploadDir(aContext, false, true, true, true, .85, false);

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
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

			String line = "";
			while ((line = bufferedReader.readLine()) != null) {
				if (line.contains("Asthma") && (line.contains("29 bytes")) || line.contains("elapsed")) {
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
		String folderPath = Globals.SURVEY_LOG_DIRECTORY + File.separator + "SurveyRecord_" + folderFormat.format(promptTime);
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
