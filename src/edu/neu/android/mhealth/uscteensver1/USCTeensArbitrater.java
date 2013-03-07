package edu.neu.android.mhealth.uscteensver1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
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

//import org.omg.PortableInterceptor.INACTIVE;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.widget.Toast;
import au.com.bytecode.opencsv.CSVWriter;
import edu.neu.android.wocketslib.Globals;
import edu.neu.android.wocketslib.dataupload.DataSender;
import edu.neu.android.wocketslib.dataupload.DataUploaderService;
import edu.neu.android.wocketslib.dataupload.RawUploader;
import edu.neu.android.wocketslib.emasurvey.SurveyActivity;
import edu.neu.android.wocketslib.emasurvey.model.PromptRecorder;
import edu.neu.android.wocketslib.emasurvey.model.QuestionSet;
import edu.neu.android.wocketslib.emasurvey.model.QuestionSetParamHandler;
import edu.neu.android.wocketslib.emasurvey.model.SurveyPromptEvent;
import edu.neu.android.wocketslib.emasurvey.model.SurveyPromptEvent.PROMPT_AUDIO;
import edu.neu.android.wocketslib.sensormonitor.Arbitrater;
import edu.neu.android.wocketslib.sensormonitor.ArbitraterInterface;
import edu.neu.android.wocketslib.sensormonitor.DataStore;
import edu.neu.android.wocketslib.sensormonitor.Sensor;
import edu.neu.android.wocketslib.support.AppInfo;
import edu.neu.android.wocketslib.support.DataStorage;
import edu.neu.android.wocketslib.support.LogcatReader;
import edu.neu.android.wocketslib.support.ServerLogger;
import edu.neu.android.wocketslib.utils.BasicLogger;
import edu.neu.android.wocketslib.utils.DateHelper;
import edu.neu.android.wocketslib.utils.Log;
import edu.neu.android.wocketslib.utils.PackageChecker;
import edu.neu.android.wocketslib.utils.PhonePrompter;
import edu.neu.android.wocketslib.utils.PhoneVibrator;
import edu.neu.android.wocketslib.utils.Util;
import edu.neu.android.wocketslib.wakefulintent.WakefulIntentService;

public class USCTeensArbitrater extends Arbitrater {
	private static final String TAG = "USCTeensArbitrater";
	
	private static final String KEY_RANDOM_PROMPT = "_KEY_RANDOM_PROMPT";
	private static final String KEY_CS_PROMPT = "_KEY_CS_PROMPT";
	private static final String KEY_ALL_PROMPT = "_KEY_ALL_PROMPT";
	private static final String KEY_SCHEDULE = "_KEY_SCHEDULE";

	private static final long PROMPT_OFFSET = 5 * 60 * 1000;

	private static final int KEY_RANDOM_EMA = 0;
	private static final int KEY_CS_EMA = 1;

	private static String inhaler = null;
	private static long inhalerUseTime;

	private static final String NEWLINE = "\n";
	private static final String SKIPLINE = "\n\n";

	protected static Context aContext = null;

	/**
	 * Obscure a MAC address so that when this function runs, the Asthmapolis
	 * detection hack does not trigger itself.
	 * 
	 * @param anAddress
	 * @return
	 */
	private String obscureAddress(String anAddress) {
		StringBuffer sb = new StringBuffer(anAddress);
		sb.insert(2, '=');
		sb.insert(2, '=');
		return sb.toString();
	}

	public USCTeensArbitrater(Context aContext) {
		this.aContext = aContext;
	}

	// Temp for one run
	protected boolean isOkAudioPrompt = false;
	// private static boolean isPostponedPromptWaiting = false;
	// private static Timer alert_timer;

	// Status info
	private ArrayList<Integer> someTasks = new ArrayList<Integer>();

	// TODO change to private
	private void PromptApp(Context aContext, int aKey, boolean isAudible, boolean isPostponed) {

		Log.i(TAG, "prompt: " + aKey + ",audible: " + isAudible
				+ ",postponed: " + isPostponed);

		long[] somePromptTimes = DataStorage.getPromptTimesKey(aContext,
				KEY_ALL_PROMPT);
		// Will be 0 if none
		long lastScheduledPromptTime = getLastScheduledPromptTime(
				System.currentTimeMillis(), somePromptTimes);
		boolean isReprompt = (System.currentTimeMillis() - lastScheduledPromptTime) > 60 * 1000;
		SurveyPromptEvent promptEvent = new SurveyPromptEvent(
				lastScheduledPromptTime, System.currentTimeMillis());
		String msg = "";
		// Intent appIntentToRun = new Intent(Intent.ACTION_MAIN);
		// String pkg = AppInfo.GetPackageName(aContext, aKey);
		// String className = AppInfo.GetClassName(aContext, aKey);

		// Indicate that a prompt took place so another one isn't done too soon
		// DataStorage.setTime(aContext, DataStorage.KEY_LAST_ALARM_TIME,
		// System.currentTimeMillis());
		// Indicate that this particular app was prompted
		AppInfo.SetLastTimePrompted(aContext, Globals.SURVEY,
				System.currentTimeMillis());
		// AppInfo.SetStartEntryTime(aContext, aKey,
		// System.currentTimeMillis());
		// appIntentToRun.setClassName(pkg, pkg + className);
		// appIntentToRun.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Intent i = new Intent(aContext, SurveyActivity.class);
		long lastTimeCompleted = AppInfo.GetLastTimeCompleted(aContext,
				Globals.SURVEY);
		long currentTime = System.currentTimeMillis();
		int classType = 0;
		String surveyName = null;
		switch (aKey) {
		case KEY_CS_EMA:
//			if ((currentTime - lastTimeCompleted) < 4 * 60 * 60 * 1000) {
//				classType = CSAsthmaSurvey.CS_EMA_DEFAULT;
//			} else {
//				classType = CSAsthmaSurvey.CS_EMA_OPTIONAL;
//			}
//			surveyName = CSAsthmaSurvey.class.getCanonicalName();
//			msg = PhonePrompter.StartPhoneAlert(TAG, aContext, true,
//					PhonePrompter.CHIMES_NAMBOKU1,
//					PhoneVibrator.VIBRATE_INTENSE);
//			String rescueInhaler = SetupInhalerActivity
//					.rescueInhalerMAC(aContext);
//			if (inhaler != null) {
//				if (rescueInhaler != null && inhaler.equals(rescueInhaler))
//					promptEvent.setPromptType("Rescue inhaler");
//				else
//					promptEvent.setPromptType("Inhaler");
//			} else
//				promptEvent.setPromptType("Inhaler");// TODO
			break;
		case KEY_RANDOM_EMA:
//			if ((currentTime - lastTimeCompleted) < 4 * 60 * 60 * 1000) {
//				classType = RandomAsthmaSurveyQuestionSet.RANDOM_EMA_DEFAULT;
//			} else {
//				classType = RandomAsthmaSurveyQuestionSet.RANDOM_EMA_OPTIONAL;
//			}
//			surveyName = RandomAsthmaSurveyQuestionSet.class.getCanonicalName();
//			msg = PhonePrompter.StartPhoneAlert(TAG, aContext, isAudible,
//					PhonePrompter.CHIMES_HIKARI, PhoneVibrator.VIBRATE_INTENSE);
//			promptEvent.setPromptType("Random");
//			long[] schedule = DataStorage.getPromptTimesKey(aContext,
//					KEY_SCHEDULE);
//			if (schedule != null && schedule.length >= 3)
//				promptEvent.setPromptSchedule(lastScheduledPromptTime,
//						(int) schedule[0], (int) schedule[1], schedule[2]);
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
		i.putExtra(QuestionSet.TAG, new QuestionSetParamHandler(1,
				new Object[] { classType }));
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
			notificationManger.cancel(Globals.SURVEY_NOTF_ID);
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
	 * Setup prompt due to inhaler use with schedule
	 */
	protected void SetInhalerUsedPromptWithSchedule() {
		boolean isOkPrompt = false;
		Date now = new Date();
		int hour = now.getHours();
		int min = now.getMinutes();
		Calendar today = Calendar.getInstance();
		if (today.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY
				&& today.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
			if ((hour >= 6 && hour < 7) || (hour >= 15 && hour < 20)
					|| (hour == 20 && min < 30))
				isOkPrompt = true;
			else
				isOkPrompt = false;
		} else {
			if (hour >= 7 && hour < 21)
				isOkPrompt = true;
			else
				isOkPrompt = false;
		}
		if (isOkPrompt)
			inhalerUseTime = System.currentTimeMillis();
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

		/**
		 * This is the app that decides what to do in terms of prompting each
		 * time it is called.
		 */
		if (Globals.IS_DEBUG) {		
			Log.d(TAG, "Begin arbitrate");			
		}
		
		// wait for the internal AC sensor to get enough data for the entire 20s
		try {
			if (Globals.IS_DEBUG) {	Log.d(TAG, "Wait for internal AC sensor for 20s"); }
			Thread.sleep(USCTeensGlobals.TIME_FOR_WAITING_ACSENSOR);
			if (Globals.IS_DEBUG) {	Log.d(TAG, "Wait for internal AC sensor finished"); }
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Temporary
		getAndPrintPromptingSchedule();

		// Mark that arbitration taking place
		long lastArbitrationTime = DataStorage.getLastTimeArbitrate(aContext, 0);
		DataStorage.setLastTimeArbitrate(aContext, System.currentTimeMillis());
		int studyDay = DataStorage.getDayNumber(aContext, true);

		// Set which apps are available based on the day of the study
		SetAppActivityUsingSchedule(lastArbitrationTime, studyDay, isNewSoftwareVersion);

		isOkAudioPrompt = isOkAudioPrompt();

		// if (Globals.IS_DEBUG)
		// Log.e(TAG, "IS OK TO AUDIO PROMPT: " + isOkAudioPrompt);
		// // Log.h(TAG, aDataStore.GetSummaryString(getApplicationContext()));
		// if (Globals.IS_DEBUG)
		// Log.e(TAG, "STUDY DAY: " +
		// DataStorage.getDayNumber(getApplicationContext(), true));
		// if (Globals.IS_DEBUG)
		// Log.e(TAG, AppInfo.AllAppStatus(getApplicationContext()));
		
		// get the recorded internal AC data for the last one minute
		

		if (Globals.IS_DEBUG) {
			Log.d(TAG, "End arbitrate");
		}				
	}

	private static final String KEY_MOVE_LOG_TO_EXTERNAL = "_KEY_MOVETOEXTERNAL";
	private static final String KEY_UPLOAD_JASON = "_KEY_UPLOAD_JASON";

	private void uploadDataAndLogToServer(Context aContext) {
		long lastUploadJSONToServer = DataStorage.GetValueLong(aContext,
				KEY_UPLOAD_JASON, -1);
		long lastMoveLogExternalTime = DataStorage.GetValueLong(aContext,
				KEY_MOVE_LOG_TO_EXTERNAL, -1);

		long currentTime = System.currentTimeMillis();
		if ((currentTime - lastUploadJSONToServer) > 60 * 60 * 1000) {
			// send JSON file
			String msg = "Starting 1-hour file upload";
			//Move JSON to external upload folder
			RawUploader.moveDataToExternal(aContext, false, true, true, .85);

			if ((currentTime - lastMoveLogExternalTime) > 24 * 60 * 60 * 1000) {
				// send logs
				msg += " and 24-hour log and survey files upload";
				//Move Log files to external upload folder
				DataSender.sendOldLogsToExternalUploadDir(aContext, new Date(), true);
				//Move Survey Log files to upload folder
//				DataSender.sendOldSurveyLogsToExternalUploadDir(aContext, new Date(), true); //////////////////////////
				
				DataStorage.SetValue(aContext, KEY_MOVE_LOG_TO_EXTERNAL,
						currentTime);
			}
			Log.i(TAG, msg);
			ServerLogger.transmitOrQueueNote(aContext, msg, true);
			//Upload JSON files and remove (dont backup)
			int filesRemaining = RawUploader.uploadDataFromExternal(aContext,
					true, true, true, false, .85);
			//Upload Log and SurveyLog files, backup and remove
			filesRemaining = RawUploader.uploadDataFromExternal(aContext,
					false, true, true, true, .85);

			msg = "Completed file upload after "
					+ String.format(
							"%.1f",
							((System.currentTimeMillis() - currentTime) / 1000.0 / 60.0))
					+ " minutes. Files remaining to upload: " + filesRemaining;
			ServerLogger.sendNote(aContext, msg, true);
			Log.i(TAG, msg);

			DataStorage.SetValue(aContext, KEY_UPLOAD_JASON, currentTime);
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
