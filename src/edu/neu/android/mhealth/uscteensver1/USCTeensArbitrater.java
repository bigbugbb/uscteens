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
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Random;

import com.google.gson.Gson;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import edu.neu.android.mhealth.uscteensver1.activities.USCTeensSurveyActivity;
import edu.neu.android.mhealth.uscteensver1.data.AccelData;
import edu.neu.android.mhealth.uscteensver1.data.AccelDataChecker;
import edu.neu.android.mhealth.uscteensver1.data.AccelDataOutputStream;
import edu.neu.android.mhealth.uscteensver1.data.CSState;
import edu.neu.android.mhealth.uscteensver1.data.DataSource;
import edu.neu.android.mhealth.uscteensver1.data.Labeler;
import edu.neu.android.mhealth.uscteensver1.survey.CSTeensSurvey;
import edu.neu.android.mhealth.uscteensver1.survey.RandomTeensSurvey;
import edu.neu.android.wocketslib.Globals;
import edu.neu.android.wocketslib.emasurvey.SurveyActivity;
import edu.neu.android.wocketslib.emasurvey.model.PromptRecorder;
import edu.neu.android.wocketslib.emasurvey.model.QuestionSet;
import edu.neu.android.wocketslib.emasurvey.model.QuestionSetParamHandler;
import edu.neu.android.wocketslib.emasurvey.model.SurveyPromptEvent;
import edu.neu.android.wocketslib.emasurvey.model.SurveyPromptEvent.PROMPT_AUDIO;
import edu.neu.android.wocketslib.sensormonitor.Arbitrater;
import edu.neu.android.wocketslib.sensormonitor.WocketSensorDataStorer;
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
	private static final String KEY_LAST_FILE_CREATE_TIME = "KEY_FILE_CREATE_TIME";	
	private static final String KEY_LAST_FILE_NAME = "KEY_LAST_FILE_NAME";
	private static final String KEY_RANDOM_PROMPT = "_KEY_RANDOM_PROMPT";
	private static final String KEY_CS_PROMPT = "_KEY_CS_PROMPT";
	private static final String KEY_ALL_PROMPT = "_KEY_ALL_PROMPT";
	private static final String KEY_ALL_REPROMPT = "_KEY_ALL_REPROMPT";
	private static final String KEY_ALL_PROMPT_STATE = "_KEY_ALL_PROMPT_STATE";
	private static final String KEY_SCHEDULE = "_KEY_SCHEDULE";		

	private static final int KEY_CS_EMA     = 1;
	private static final int KEY_RANDOM_EMA = 0;	

	private static final String NEWLINE = "\n";	
	private static Context sContext = null;

	// Status info
	private ArrayList<Integer> mSomeTasks = new ArrayList<Integer>();

	public USCTeensArbitrater(Context aContext) {
		sContext = aContext;
	}
	
	private boolean isLastFileExpired(long lastCreateTime, long now) {
		Date dateA = new Date(lastCreateTime);
		Date dateB = new Date(now);	
		long midnight = DateHelper.getDailyTime(0, 0);		
		long nextMidnight = midnight + 24 * 3600 * 1000;
		
		if (lastCreateTime < midnight || lastCreateTime > nextMidnight || dateA.getHours() != dateB.getHours()) {
			return true;
		}
		
		return false;
	}

	private String[] getFileNamesForSensorData() {
		long now = System.currentTimeMillis();
		long lastCreateTime = DataStorage.GetValueLong(sContext, KEY_LAST_FILE_CREATE_TIME, 0);
		String lastFileName = DataStorage.GetValueString(sContext, KEY_LAST_FILE_NAME, "");
		
		if (isLastFileExpired(lastCreateTime, now)) {			
			lastFileName = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS").format(now);			
			DataStorage.SetValue(sContext, KEY_LAST_FILE_NAME, lastFileName);
			DataStorage.SetValue(sContext, KEY_LAST_FILE_CREATE_TIME, now);
		} 
		
		// compose the file names
		String[] names = new String[2];
		names[0] = USCTeensGlobals.SENSOR_TYPE + "." + PhoneInfo.getID(sContext) + "." + lastFileName + ".log.csv";
		names[1] = USCTeensGlobals.SENSOR_TYPE + "." + PhoneInfo.getID(sContext) + "." + lastFileName + ".log.bin";
		
		return names;
	}
	
	private String[] getFilePathNamesForSensorData(String[] fileNames) {
		String[] filePathNames = new String[2];
		String dateDir = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		String hourDir = new SimpleDateFormat("/HH/").format(new Date());
		
		filePathNames[0] = USCTeensGlobals.DIRECTORY_PATH + File.separator + 
			Globals.DATA_DIRECTORY + File.separator + dateDir + USCTeensGlobals.SENSOR_FOLDER + hourDir + fileNames[0];
		filePathNames[1] = USCTeensGlobals.DIRECTORY_PATH + File.separator + 
			Globals.DATA_DIRECTORY + File.separator + dateDir + USCTeensGlobals.SENSOR_FOLDER + hourDir + fileNames[1];
		
		return filePathNames;
	}
	
	private Object[] getLastMinuteSensorData() {	
		// Get internal accelerometer data which have been saved in the last minute
		String readable = "";
		ArrayList<Object> data = new ArrayList<Object>();	
		data.add(readable); // dummy		
		
		int count = (int) DataStorage.GetValueLong(sContext, DataStorage.KEY_INTERNAL_ACCEL_RECORDING_COUNT, 0);
		DataStorage.SetValue(sContext, DataStorage.KEY_INTERNAL_ACCEL_RECORDING_COUNT, 0); 
		for (int i = 1; i <= count; ++i) {	
			String time = DataStorage.GetValueString(sContext, DataStorage.KEY_INTERNAL_ACCEL_RECORDING_TIME + i, DataStorage.EMPTY);
			int intAccelAverage = (int) DataStorage.GetValueLong(sContext, DataStorage.KEY_INTERNAL_ACCEL_AVERAGE + i, 0);
			int intAccelSamples = (int) DataStorage.GetValueLong(sContext, DataStorage.KEY_INTERNAL_ACCEL_SAMPLES + i, 0);
			readable += time + ", " + intAccelAverage + ", " + intAccelSamples + "\n";
			
			String[] split = time.split("[ :,.]");
			AccelData binary = new AccelData(split[1], split[2], split[3], split[5], intAccelAverage, intAccelSamples);
			data.add(binary);
		}		
		data.set(0, readable);
		
		return data.toArray();
	}
	
	protected boolean writeSensorToFiles() {
		boolean result = true;
				
		// Get the csv and bin file name
		String[] fileNames = getFileNamesForSensorData();
		// Get the data that should be written, for both .csv and .bin
		Object[] data = getLastMinuteSensorData();
		// Get the complete file path name
		String[] filePathNames = getFilePathNamesForSensorData(fileNames);
		
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
				// Write the csv file header
				FileHelper.appendToFile(DataSource.INTERNAL_ACCEL_DATA_CSVFILEHEADER, filePathNames[0]);				
			}
			// Write the data to the file	
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
				e.printStackTrace();
			}
		}
	
		return result;
	}

	private void promptTask(Context aContext, int aKey, boolean isAudible, boolean isPostponed) {
		Log.i(TAG, "prompt: " + aKey + ",audible: " + isAudible + ",postponed: " + isPostponed);							
		long now = System.currentTimeMillis();
		long[] allPromptTime = DataStorage.getPromptTimesKey(aContext, KEY_ALL_PROMPT);	
		long lastScheduledPromptTime = getLastScheduledPromptTime(now, allPromptTime); // would be 0 if none				
		boolean isReprompt = DataStorage.GetValueBoolean(aContext, KEY_ALL_REPROMPT + lastScheduledPromptTime, false);								
				
		if (isReprompt) {
			if (SurveyActivity.self == null) {
				return; // The survey is canceled or completed by the user, so there won't be any reprompt
			} else {
				Log.i(TAG, "do reprompt");
			}
		} else {
			if (SurveyActivity.self != null) {				
				return;
			}
			DataStorage.SetValue(aContext, KEY_ALL_REPROMPT + lastScheduledPromptTime, true);
		}			
		
		// Indicate that this particular app was prompted
		AppInfo.SetLastTimePrompted(aContext, Globals.SURVEY, now);
				
		String surveyClassName = null;
		// Construct survey prompt event
		SurveyPromptEvent promptEvent = new SurveyPromptEvent(lastScheduledPromptTime, now);
		String msg = "";
		switch (aKey) {
		case KEY_CS_EMA:
			surveyClassName = CSTeensSurvey.class.getCanonicalName();
			msg = PhonePrompter.StartPhoneAlert(TAG, aContext, true, PhonePrompter.CHIMES_NAMBOKU1, PhoneVibrator.VIBRATE_INTENSE);						
			promptEvent.setPromptType("CS");
			break;
		case KEY_RANDOM_EMA:
			surveyClassName = RandomTeensSurvey.class.getCanonicalName();
			msg = PhonePrompter.StartPhoneAlert(TAG, aContext, isAudible, PhonePrompter.CHIMES_HIKARI, PhoneVibrator.VIBRATE_INTENSE);
			promptEvent.setPromptType("Random");
			long[] schedule = DataStorage.getPromptTimesKey(aContext, KEY_SCHEDULE);
			if (schedule != null && schedule.length >= 3)
				promptEvent.setPromptSchedule(lastScheduledPromptTime, (int) schedule[0], (int) schedule[1], schedule[2]);			
			break;
		} // switch end		
		if (msg.toLowerCase().contains("silence")) {
			promptEvent.setPromptAudio(PROMPT_AUDIO.NONE);
		} else if (msg.toLowerCase().contains("normal")) {
			promptEvent.setPromptAudio(PROMPT_AUDIO.AUDIO);
		} else if (msg.toLowerCase().contains("vibrate")) {
			promptEvent.setPromptAudio(PROMPT_AUDIO.VIBRATION);
		}
		promptEvent.setReprompt(isReprompt);
		
		// Get CSState from JSON string
		String aJSONString = DataStorage.GetValueString(sContext, KEY_ALL_PROMPT_STATE + lastScheduledPromptTime, null);
		CSState css = null;
		if (aJSONString != null) {
			css = new Gson().fromJson(aJSONString, CSState.class); 
		}
		
		// Construct intent and start survey activity
		Intent i = new Intent(aContext, USCTeensSurveyActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.putExtra(QuestionSet.TAG, new QuestionSetParamHandler(surveyClassName, 1, new Object[] { css }));
		i.putExtra(USCTeensSurveyActivity.PROMPT_EVENT, promptEvent);	
		aContext.startActivity(i);
		
		// add new label
		Labeler.addLabel(new Date(), aKey == KEY_CS_EMA ? "CS Prompt" : "Random Prompt");
	}

	private boolean addCSPrompt(CSState css) {		
		long promptTime = System.currentTimeMillis();
		long[] savedPromptTime = DataStorage.getPromptTimesKey(sContext, KEY_CS_PROMPT);				
        
		if (savedPromptTime == null) {
			DataStorage.setPromptTimesKey(sContext, new long[] { promptTime }, KEY_CS_PROMPT);
			// Save the CSState
	        DataStorage.SetValue(sContext, KEY_ALL_PROMPT_STATE + promptTime, new Gson().toJson(css));
			return true;
		}
				
		long latestPromptTime = savedPromptTime[savedPromptTime.length - 1];		
		if (promptTime - latestPromptTime < Globals.MIN_MS_BETWEEN_SCHEDULED_PROMPTS) {
			promptTime = latestPromptTime + (long) (Globals.MIN_MS_BETWEEN_SCHEDULED_PROMPTS * 1.1f);
			// Save the CSState
			DataStorage.SetValue(sContext, KEY_ALL_PROMPT_STATE + promptTime, new Gson().toJson(css));
		}		
		long[] finalPromptTime = Arrays.copyOf(savedPromptTime, savedPromptTime.length + 1);
		finalPromptTime[savedPromptTime.length] = promptTime;
		DataStorage.setPromptTimesKey(sContext, finalPromptTime, KEY_CS_PROMPT);
		
		return true;				
	}

	private void resetSchedule() {
		long[] randomPromptTime = DataStorage.getPromptTimesKey(sContext, KEY_RANDOM_PROMPT);
		long[] csPromptTime = DataStorage.getPromptTimesKey(sContext, KEY_CS_PROMPT);
		PriorityQueue<Long> promptTimeQueue = new PriorityQueue<Long>(csPromptTime.length);
		
		for (int i = 0; i < csPromptTime.length; i++) {
			promptTimeQueue.add(csPromptTime[i]);
		}
		
		if (randomPromptTime != null) {
			boolean isExcluded = false;
			for (int j = 0; j < randomPromptTime.length; ++j) {
				isExcluded = false;
				for (int i = 0; i < csPromptTime.length; ++i) {
					if (Math.abs(csPromptTime[i] - randomPromptTime[j]) < Globals.MIN_MS_BETWEEN_SCHEDULED_PROMPTS) {
						isExcluded = true;
						break;
					}
				}
				if (!isExcluded) {
					promptTimeQueue.add(randomPromptTime[j]);
				}
			}
		}
		
		long[] allPromptTime = new long[promptTimeQueue.size()];
		for (int j = 0; j < allPromptTime.length; j++) {
			allPromptTime[j] = promptTimeQueue.poll();
		}
		
		DataStorage.setPromptTimesKey(sContext, allPromptTime, KEY_ALL_PROMPT);
	}

	private void setAndSavePromptingSchedule(int promptsPerDay, double startTimeHour, double endTimeHour) {
		long totalPromptingWindowMS = (long) (endTimeHour - startTimeHour) * Globals.MINUTES_60_IN_MS;
		long intervalIncMS = (long) (totalPromptingWindowMS / (double) promptsPerDay);
		long promptTimes[] = new long[promptsPerDay];
		int startIntervalTimeMS = (int) (startTimeHour * Globals.MINUTES_60_IN_MS);
		long startDayTime = DateHelper.getDailyTime(0, 0); // Midnight

		StringBuffer promptSchedule = new StringBuffer();
		promptSchedule.append("Scheduled prompts today: " + promptsPerDay + NEWLINE);
		promptSchedule.append("Start hour: " + startTimeHour + NEWLINE);
		promptSchedule.append("End hour: " + endTimeHour + NEWLINE);
		
		Random r = new Random();
		for (int i = 0; i < promptsPerDay; i++) {
			// Add a random number of MS to the first start time block
			promptTimes[i] = startDayTime + startIntervalTimeMS + i * intervalIncMS + r.nextInt((int) intervalIncMS);
			Log.i(TAG, "Time to prompt: " + DateHelper.getDate(promptTimes[i]));
			if (i > 0) {
				// Shift any prompts too close together
				if (promptTimes[i] - promptTimes[i - 1] < Globals.MIN_MS_BETWEEN_SCHEDULED_PROMPTS) {
					promptTimes[i] += Globals.MIN_MS_BETWEEN_SCHEDULED_PROMPTS - (promptTimes[i] - promptTimes[i - 1]);
					Log.i(TAG, "SHIFTED Time to prompt: " + DateHelper.getDate(promptTimes[i]));
				}
			}
		}
		
		/*
		 * Generate a time which is very close to the current time in millisecond
		 * Comment it if not for test		 
		 */
//		long testTime = System.currentTimeMillis() + 60000;
//		for (int i = 0; i < promptsPerDay; ++i) {
//			if (promptTimes[i] < testTime) {
//				continue;
//			}
//			promptTimes[i] = testTime;
//			break;
//		}
		
		for (int i = 0; i < promptsPerDay; i++) {
			promptSchedule.append("Prompt: " + DateHelper.getDate(promptTimes[i]) + NEWLINE);
		}
		ServerLogger.sendNote(sContext, promptSchedule.toString(), Globals.NO_PLOT);

		// reset prompt schedule for the day (don't change the sequence here)
		DataStorage.setPromptTimesKey(sContext, promptTimes, KEY_RANDOM_PROMPT);
		PromptRecorder.writePromptSchedule(sContext, System.currentTimeMillis(), KEY_RANDOM_PROMPT, promptsPerDay, startTimeHour, endTimeHour);
		DataStorage.setPromptTimesKey(sContext, new long[] { promptsPerDay, startIntervalTimeMS, intervalIncMS }, KEY_SCHEDULE);		
		DataStorage.setPromptTimesKey(sContext, new long[] {}, KEY_CS_PROMPT);
		DataStorage.setPromptTimesKey(sContext, promptTimes, KEY_ALL_PROMPT);
	}

	protected boolean updatePendingTask() {
		mSomeTasks.clear();
		
		long now = System.currentTimeMillis();
		long lastTimePrompted  = AppInfo.GetLastTimePrompted(sContext, Globals.SURVEY);
		long lastTimeCompleted = AppInfo.GetLastTimeCompleted(sContext, Globals.SURVEY);		
		long[] somePromptTimes = DataStorage.getPromptTimesKey(sContext, KEY_ALL_PROMPT);
		
		// Reset the time if user manipulates the phone's time 
		if (lastTimePrompted > now) {
			AppInfo.SetLastTimePrompted(sContext, Globals.SURVEY, now);
		}
		if (lastTimeCompleted > now) {
			AppInfo.SetLastTimeCompleted(sContext, Globals.SURVEY, now);
		}
		
		long timeSincePrompted  = now - lastTimePrompted;
		long timeSinceCompleted = now - lastTimeCompleted;
		long lastScheduledPromptTime  = getLastScheduledPromptTime(now, somePromptTimes);
		long timeSinceScheduledPrompt = now - lastScheduledPromptTime;
		Log.i(TAG, "LastScheduledPromptTime: " + DateHelper.getDate(lastScheduledPromptTime));

		// If no scheduled prompt, just return
		if (lastScheduledPromptTime == 0) {
			return false;
		}

		if (lastTimeCompleted == 0) {
			Log.i(TAG, "LastTimeCompleted: never");
		} else {
			Log.i(TAG, "LastTimeCompleted: " + DateHelper.getDate(lastTimeCompleted));
		}

		// If completed after the last scheduled prompt time, then just return
		if (lastTimeCompleted > lastScheduledPromptTime) {
			return false; // the task of the current time is finished
		}

		if (Globals.IS_DEBUG) {
			Log.d(TAG, "Time from scheduled prompt (min): " + (now - lastScheduledPromptTime) / Globals.MINUTES_1_IN_MS);
			Log.d(TAG, "Time since completed (min): " + timeSinceCompleted / Globals.MINUTES_1_IN_MS);
			Log.d(TAG, "Time since prompted (min): " + timeSincePrompted / Globals.MINUTES_1_IN_MS);
		}

		// Check if it is the time to prompt the task		
		if (timeSinceScheduledPrompt < Globals.REPROMPT_TIMES * Globals.REPROMPT_DELAY_MS &&
			timeSinceCompleted > Globals.MIN_MS_BETWEEN_SCHEDULED_PROMPTS && 
			timeSincePrompted > Globals.REPROMPT_DELAY_MS) 
		{
			mSomeTasks.add(isCSPrompt(lastScheduledPromptTime) ? KEY_CS_EMA : KEY_RANDOM_EMA);			
		}
		if (timeSinceScheduledPrompt < (Globals.REPROMPT_TIMES + 1) * Globals.REPROMPT_DELAY_MS + Globals.MINUTES_10_IN_MS
				&& timeSincePrompted > (Globals.REPROMPT_TIMES + 1) * Globals.REPROMPT_DELAY_MS) {
			NotificationManager nm = (NotificationManager) sContext.getSystemService(Context.NOTIFICATION_SERVICE);
			nm.cancel(Globals.SURVEY_NOTIFICATION_ID);
		}
		
		return true;
	}

	private boolean isCSPrompt(long promptTime) {
		long[] csPromptTimes = DataStorage.getPromptTimesKey(sContext, KEY_CS_PROMPT);
		
		if (csPromptTimes == null) {
			return false;
		}		
		for (int i = 0; i < csPromptTimes.length; ++i) {
			if (csPromptTimes[i] == promptTime) {
				return true;
			}
		}
		
		return false;
	}

	// Return 0 if none, otherwise return time
	private long getLastScheduledPromptTime(long currentTime, long[] promptTimes) {
		if (promptTimes == null) {
			return 0;
		}

		long lastTime = 0;
		for (int i = 0; i < promptTimes.length && promptTimes[i] < currentTime; ++i) { 
			if (!isSameMinute(currentTime, promptTimes[i])) {
				lastTime = promptTimes[i];
			}
		}

		return lastTime;
	}

	private boolean isSameMinute(long timeA, long timeB) {
		Date dateA = new Date(timeA);
		Date dateB = new Date(timeB);
		return Math.abs(timeA - timeB) < Globals.MINUTES_1_IN_MS && dateA.getMinutes() == dateB.getMinutes();
	}
	
	private boolean resetPromptingSchedule(boolean isNewSoftwareVersion) {	
		
		boolean isForceReset = DataStorage.isForceReset(sContext);
		boolean hasCrossDate = !DateHelper.isToday(DataStorage.getLastTimeArbitrate(sContext, 0));
		
		if (isNewSoftwareVersion) {
			Log.i(TAG, "Resetting because new software version");
		} else if (isForceReset) {
			Log.i(TAG, "Resetting because force reset");
			DataStorage.setIsForceReset(sContext, false);
		} else if (hasCrossDate) {
			Log.i(TAG, "Resetting because day changed");
		} else {
			return false; // reset is not needed currently
		}
		
		// clear the prompt history
		long[] somePromptTimes = DataStorage.getPromptTimesKey(sContext, KEY_ALL_PROMPT);
		if (somePromptTimes != null) {
			for (int i = 0; i < somePromptTimes.length; ++i) {
				DataStorage.SetValue(sContext, KEY_ALL_REPROMPT + somePromptTimes[i], false);
				DataStorage.SetValue(sContext, KEY_ALL_PROMPT_STATE + somePromptTimes[i], null);
			}
		}
				
		PackageChecker.installedPackageLogging(TAG, sContext);		
		AppInfo.resetAvailabilityAndTiming(sContext);

		int promptsPerDay    = Globals.DEFAULT_PROMPTS_PER_DAY;
		double startTimeHour = Globals.DEFAULT_START_HOUR;
		double endTimeHour   = Globals.DEFAULT_END_HOUR;
		
		setAndSavePromptingSchedule(promptsPerDay, startTimeHour, endTimeHour);
		
		return true;
	}

	protected void updatePromptingSchedule(boolean isNewSoftwareVersion) {
		// Set a lock so main app waits until this is done before doing anything
		// because variables are temporarily cleared during the reset issue.
		DataStorage.setIsInUpdate(sContext, true);
		
		// Reset the prompting schedule if it is necessary
		resetPromptingSchedule(isNewSoftwareVersion);
		
		// Analyze the data we have and get the CS state if available
		if (isInPromptTime()) {				
			CSState css = AccelDataChecker.checkDataState(sContext, -1, -1);
			if (css.getState() != CSState.DATA_STATE_ERROR && css.getState() != CSState.DATA_STATE_NORMAL) {				
				addCSPrompt(css);	
				resetSchedule();				
			}
		}

		// Always unset this in case program was updated at awkward time
		// If this is before prior }, it is possible to get stuck always in an update!
		DataStorage.setIsInUpdate(sContext, false);
		
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
	
	private static boolean isInPromptTime() {		
		Date today = new Date();
		int hour = today.getHours();
		int min  = today.getMinutes();	
		
		int startHour = Globals.DEFAULT_START_HOUR;
		int endHour   = Globals.DEFAULT_END_HOUR;
		
		return (hour >= startHour && hour < endHour - 1) || (hour == endHour - 1 && min < 30);
	}

	public static boolean isOkAudioPrompt() {
		return true;
	}

	public void doArbitrate(boolean isNewSoftwareVersion) {
		if (Globals.IS_DEBUG) {
			Log.d(TAG, "Begin arbitrate");
		}
		
		long lastTime = System.currentTimeMillis();
		
		// For testing purpose only
		saveRecordsInLogcat(false);
		
		// record the accelerometer data of the previous minute
		writeSensorToFiles();

		// Update the prompt schedule if it is necessary
		updatePromptingSchedule(isNewSoftwareVersion);

		// Try to prompt the next task if it is available
		updatePendingTask();
		if (mSomeTasks.size() > 0) {		
			promptTask(sContext, mSomeTasks.get(0), isOkAudioPrompt(), false);
		}
		
		// Mark that arbitration taking place
		DataStorage.setLastTimeArbitrate(sContext, System.currentTimeMillis());
				
		// wait for the internal AC sensor to get data for at least 20s 
		try {
			synchronized (this) {				
				if (Globals.IS_DEBUG) {	Log.d(TAG, "Wait for internal AC sensor for at most 20s"); }
				long timeCost = System.currentTimeMillis() - lastTime;
				long timeToWait = USCTeensGlobals.TIME_WAITING_SENSOR_DATA_IN_MS - timeCost;
				if (timeToWait > 0) {
					wait(timeToWait);
				}
				if (Globals.IS_DEBUG) {	Log.d(TAG, "Wait for internal AC sensor finished"); }				
			}			
		} catch (InterruptedException e) {			
			e.printStackTrace();
		}				

		if (Globals.IS_DEBUG) {
			Log.d(TAG, "End arbitrate");
		}
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
