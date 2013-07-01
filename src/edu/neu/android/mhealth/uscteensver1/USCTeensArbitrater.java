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
import java.util.HashMap;
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

	private static final int KEY_CS_EMA     = 1;
	private static final int KEY_RANDOM_EMA = 0;	
	private static final String KEY_RANDOM_PROMPT_COUNTER = "_KEY_RANDOM_PROMPT_COUNTER";
	private static final String KEY_CS_PROMPT_COUNTER = "_KEY_CS_PROMPT_COUNTER";

	private static final String NEWLINE = "\n";	
	private static final String KEY_FIRST_CHECK_TIME  = "_KEY_FIRST_CHECK_TIME";
	private static final String KEY_LAST_CHECK_TIME = "_KEY_LAST_CHECK_TIME";	
	
	private static boolean sIsFirstRun = true;
	private static Context sContext = null;
	private static final long PROMPT_OFFSET = 2 * Globals.MINUTES_1_IN_MS;
	
	private static HashMap<Long, Boolean> sRepromtHash = new HashMap<Long, Boolean>();

	// Status info
	private ArrayList<Integer> mSomeTasks = new ArrayList<Integer>();

	public USCTeensArbitrater(Context aContext) {
		sContext = aContext;
	}

	private String[] getFileNamesForSavingInternalAccelData() {
		boolean isNewFileNeeded = false;				
		String lastCSVFileCreateTime = DataStorage.GetValueString(sContext, KEY_CSVFILE_CREATE_TIME, DataStorage.EMPTY);		
		
		if (lastCSVFileCreateTime.compareTo(DataStorage.EMPTY) != 0) {
			String time[] = lastCSVFileCreateTime.split("-");
			// Get creating date
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
		String dateDir = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		String hourDir = new SimpleDateFormat("/HH/").format(new Date());
		
		filePathNames[0] = USCTeensGlobals.DIRECTORY_PATH + File.separator + 
			Globals.DATA_DIRECTORY + File.separator + dateDir + USCTeensGlobals.SENSOR_FOLDER + hourDir + fileNames[0];
		filePathNames[1] = USCTeensGlobals.DIRECTORY_PATH + File.separator + 
			Globals.DATA_DIRECTORY + File.separator + dateDir + USCTeensGlobals.SENSOR_FOLDER + hourDir + fileNames[1];
		
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
	
	protected boolean writeAccelDataToInternalDirectory() {
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
	
		return result;
	}

	private void promptTask(Context aContext, int aKey, boolean isAudible, boolean isPostponed) {
		Log.i(TAG, "prompt: " + aKey + ",audible: " + isAudible + ",postponed: " + isPostponed);				
		long[] allPromptTime = DataStorage.getPromptTimesKey(aContext, KEY_ALL_PROMPT);		
		long now = System.currentTimeMillis();
		long lastScheduledPromptTime = getLastScheduledPromptTime(now, allPromptTime); // would be 0 if none				
		boolean isReprompt = sRepromtHash.get(lastScheduledPromptTime) != null;
		SurveyPromptEvent promptEvent = new SurveyPromptEvent(lastScheduledPromptTime, now);
		String msg = "";
		
		sRepromtHash.put(lastScheduledPromptTime, true);
		
		// Indicate that this particular app was prompted
		AppInfo.SetLastTimePrompted(aContext, Globals.SURVEY, now);
		
		Intent i = new Intent(aContext, SurveyActivity.class);
		long lastTimeCompleted = AppInfo.GetLastTimeCompleted(aContext, Globals.SURVEY);
		int classType = 0;
		String surveyClassName = null;
		int counter = 0;
		
		switch (aKey) {
		case KEY_CS_EMA:
			counter = (int) DataStorage.GetValueLong(aContext, KEY_CS_PROMPT_COUNTER, 0);
			if (!isReprompt) {				
				DataStorage.SetValue(aContext, KEY_CS_PROMPT_COUNTER, ++counter);
			}
			promptEvent.AddSurveySpecifiedRecord("CS_NUM", counter + "");
			if (now - lastTimeCompleted < 4 * Globals.MINUTES_60_IN_MS) {
				classType = CSTeensSurvey.CS_EMA_DEFAULT;
				promptEvent.AddSurveySpecifiedRecord("CS_SPAN", "1");
			} else {
				classType = CSTeensSurvey.CS_EMA_OPTIONAL;
				promptEvent.AddSurveySpecifiedRecord("CS_SPAN", "2");
			}
			surveyClassName = CSTeensSurvey.class.getCanonicalName();
			msg = PhonePrompter.StartPhoneAlert(TAG, aContext, true, PhonePrompter.CHIMES_NAMBOKU1, PhoneVibrator.VIBRATE_INTENSE);						
			promptEvent.setPromptType("CS");
			Labeler.addLabel(new Date(), "CS Survey");
			break;
		case KEY_RANDOM_EMA:
			counter = (int) DataStorage.GetValueLong(aContext, KEY_RANDOM_PROMPT_COUNTER, 0);
			if (!isReprompt) {				
				DataStorage.SetValue(aContext, KEY_RANDOM_PROMPT_COUNTER, ++counter);
			}
			promptEvent.AddSurveySpecifiedRecord("RAN_NUM", counter + "");
			if (now - lastTimeCompleted < 4 * Globals.MINUTES_60_IN_MS) {
				classType = RandomTeensSurvey.RANDOM_EMA_DEFAULT;
				promptEvent.AddSurveySpecifiedRecord("RAN_SPAN", "1");
			} else {
				classType = RandomTeensSurvey.RANDOM_EMA_OPTIONAL;
				promptEvent.AddSurveySpecifiedRecord("RAN_SPAN", "2");
			}
			surveyClassName = RandomTeensSurvey.class.getCanonicalName();
			msg = PhonePrompter.StartPhoneAlert(TAG, aContext, isAudible, PhonePrompter.CHIMES_HIKARI, PhoneVibrator.VIBRATE_INTENSE);
			promptEvent.setPromptType("Random");
			long[] schedule = DataStorage.getPromptTimesKey(aContext, KEY_SCHEDULE);
			if (schedule != null && schedule.length >= 3)
				promptEvent.setPromptSchedule(lastScheduledPromptTime, (int) schedule[0], (int) schedule[1], schedule[2]);
			// add new label
			Labeler.addLabel(new Date(), "Random Survey");
			break;
		} // switch end
		if (msg.toLowerCase().contains("silence"))
			promptEvent.setPromptAudio(PROMPT_AUDIO.NONE);
		else if (msg.toLowerCase().contains("normal"))
			promptEvent.setPromptAudio(PROMPT_AUDIO.AUDIO);
		else if (msg.toLowerCase().contains("vibrate"))
			promptEvent.setPromptAudio(PROMPT_AUDIO.VIBRATION);
		promptEvent.setReprompt(isReprompt);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.putExtra(QuestionSet.TAG, new QuestionSetParamHandler(1, new Object[] { classType }));
		i.putExtra("className", surveyClassName);
		i.putExtra("promptEvent", promptEvent);
		if (!isReprompt && SurveyActivity.self != null)
			SurveyActivity.self.finish();
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		aContext.startActivity(i);		
	}

	public void printPromptingSchedule() {
		if (!Globals.IS_DEBUG) {
			return;
		}
		
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

	private boolean setCSPrompt(long promptTime) {
		promptTime += PROMPT_OFFSET;
		
		long[] savedPromptTime = DataStorage.getPromptTimesKey(sContext, KEY_CS_PROMPT);
		if (savedPromptTime == null) {
			DataStorage.setPromptTimesKey(sContext, new long[] { promptTime }, KEY_CS_PROMPT);
			return true;
		}
		long latestPromptTime = savedPromptTime[savedPromptTime.length - 1];
		if (promptTime - latestPromptTime > Globals.MIN_MS_BETWEEN_SCHEDULED_PROMPTS) {
			long[] finalPromptTimes = new long[savedPromptTime.length + 1];
			for (int i = 0; i < savedPromptTime.length; i++) {
				finalPromptTimes[i] = savedPromptTime[i];
			}
			finalPromptTimes[savedPromptTime.length] = promptTime;
			DataStorage.setPromptTimesKey(sContext, finalPromptTimes, KEY_CS_PROMPT);
			return true;
		}
		return false;
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
		Random r = new Random();
		long promptTimes[] = new long[promptsPerDay];
		int startIntervalTimeMS = (int) (startTimeHour * Globals.MINUTES_60_IN_MS);
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
		long lastTimePrompted = AppInfo.GetLastTimePrompted(sContext, Globals.SURVEY);
		long lastTimeCompleted = AppInfo.GetLastTimeCompleted(sContext, Globals.SURVEY);		
		long[] somePromptTimes = DataStorage.getPromptTimesKey(sContext, KEY_ALL_PROMPT);
		
		// User may set the phone's time 
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
		if (timeSinceScheduledPrompt < Globals.REPROMPT_TIMES * Globals.REPROMPT_DELAY_MS + 2000 &&
				timeSinceCompleted > Globals.MIN_MS_BETWEEN_SCHEDULED_PROMPTS &&
				timeSincePrompted > Globals.REPROMPT_DELAY_MS) {
			Log.i(TAG, "Prompt!");			
			mSomeTasks.add(isCSPrompt(lastScheduledPromptTime) ? KEY_CS_EMA : KEY_RANDOM_EMA);			
		}
		if (timeSinceScheduledPrompt < (Globals.REPROMPT_TIMES + 1) * Globals.REPROMPT_DELAY_MS + Globals.MINUTES_10_IN_MS
				&& timeSincePrompted > (Globals.REPROMPT_TIMES + 1) * Globals.REPROMPT_DELAY_MS) {
			NotificationManager nm = (NotificationManager) sContext.getSystemService(Context.NOTIFICATION_SERVICE);
			nm.cancel(Globals.SURVEY_NOTIFICATION_ID);
		}
		
		return true;
	}

	private boolean isCSPrompt(long lastScheduledPromptTime) {
		long[] csPromptTime = DataStorage.getPromptTimesKey(sContext, KEY_CS_PROMPT);
		
		if (csPromptTime == null) {
			return false;
		}		
		for (int i = 0; i < csPromptTime.length; ++i) {
			if (csPromptTime[i] == lastScheduledPromptTime) {
				return true;
			}
		}
		
		return false;
	}

	// Return 0 if none, otherwise return time
	private long getLastScheduledPromptTime(long currentTime, long[] somePromptTimes) {
		long lastTime = 0;

		if (somePromptTimes == null) {
			return lastTime;
		}

		for (int i = 0; i < somePromptTimes.length; ++i) {
			if (somePromptTimes[i] > currentTime && !isSameMinute(somePromptTimes[i], currentTime)) {
				return lastTime;
			} else {
				lastTime = somePromptTimes[i];
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
		PackageChecker.installedPackageLogging(TAG, sContext);		
		AppInfo.resetAvailabilityAndTiming(sContext);

		int promptsPerDay    = Globals.DEFAULT_PROMPTS_PER_DAY;
		double startTimeHour = Globals.DEFAULT_START_HOUR;
		double endTimeHour   = Globals.DEFAULT_END_HOUR;
		setAndSavePromptingSchedule(promptsPerDay, startTimeHour, endTimeHour);
		
		sRepromtHash.clear();
		
		return true;
	}

	protected void updatePromptingSchedule(boolean isNewSoftwareVersion) {
		// Set a lock so main app waits until this is done before doing anything
		// because variables are temporarily cleared during the reset issue.
		DataStorage.setIsInUpdate(sContext, true);
		
		// Reset the prompting schedule if it is necessary
		resetPromptingSchedule(isNewSoftwareVersion);
		
		// Check the previous 30+ minutes accelerometer data state every 10 minutes
		// to determine whether the context sensitive schedule should be reset
		long now  = System.currentTimeMillis();		
		long firstCheckTime  = DataStorage.GetValueLong(sContext, KEY_FIRST_CHECK_TIME, 0);
		long lastCheckTime = DataStorage.GetValueLong(sContext, KEY_LAST_CHECK_TIME, 0);		
		if (firstCheckTime == 0 || firstCheckTime > now || lastCheckTime > now) { // the first time trying to check
			firstCheckTime = lastCheckTime = now;
			DataStorage.SetValue(sContext, KEY_FIRST_CHECK_TIME, now);
			DataStorage.SetValue(sContext, KEY_LAST_CHECK_TIME, now);				
		}				
		
		// Make sure that we have at least data for 30 minutes to analyze
		if (now - firstCheckTime >= Globals.MINUTES_30_IN_MS && now - lastCheckTime >= Globals.MINUTES_30_IN_MS) {
			if (isOkActivityPrompt()) {	
				ContextSensitiveState css = AccelDataChecker.checkDataState();
				if (css.getState() != ContextSensitiveState.DATA_STATE_ERROR && 
					css.getState() != ContextSensitiveState.DATA_STATE_NORMAL) {
					Log.i(TAG, "Resetting because activity is detected or missed");														
					if (setCSPrompt(now)) {
						resetSchedule();
						CSTeensSurvey.setLatestPromptTime(css.getStartTime(), css.getStopTime());
					}
				}
			}
			DataStorage.SetValue(sContext, KEY_LAST_CHECK_TIME, now);
		}

		// Always unset this in case program was updated at awkward time
		// If this is before prior }, it is possible to get stuck always in an update!
		DataStorage.setIsInUpdate(sContext, false); // TODO Change to use a date
													// so this is less brittle
													// to an odd crash
	}
	
	private static boolean isOkActivityPrompt() {		
		Date today = new Date();
		int hour = today.getHours();
		int min  = today.getMinutes();			
		return (hour > 8 && hour < 20) || (hour == 21 && min < 30);
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
		
		if (sIsFirstRun) {
			sIsFirstRun = false;			
		} else {
			// record the accelerometer data of the previous minute
			writeAccelDataToInternalDirectory();
		}				

		// Update the prompt schedule if it is necessary
		updatePromptingSchedule(isNewSoftwareVersion);
		printPromptingSchedule();	

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
