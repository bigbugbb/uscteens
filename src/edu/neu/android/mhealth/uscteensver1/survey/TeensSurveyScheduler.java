package edu.neu.android.mhealth.uscteensver1.survey;

import java.util.Date;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Random;

import com.google.gson.Gson;

import android.content.Context;
import edu.neu.android.mhealth.uscteensver1.activities.USCTeensSurveyActivity;
import edu.neu.android.mhealth.uscteensver1.data.AccelDataChecker;
import edu.neu.android.mhealth.uscteensver1.data.Labeler;
import edu.neu.android.wocketslib.Globals;
import edu.neu.android.wocketslib.emasurvey.SurveyScheduler;
import edu.neu.android.wocketslib.emasurvey.model.PromptRecorder;
import edu.neu.android.wocketslib.emasurvey.model.SurveyExtraInfo;
import edu.neu.android.wocketslib.emasurvey.model.SurveyPromptEvent;
import edu.neu.android.wocketslib.support.DataStorage;
import edu.neu.android.wocketslib.support.ServerLogger;
import edu.neu.android.wocketslib.utils.DateHelper;
import edu.neu.android.wocketslib.utils.Log;
import edu.neu.android.wocketslib.utils.PhonePrompter;

public class TeensSurveyScheduler extends SurveyScheduler {
	protected final static String TAG = "TeensSurveyScheduler";
	private final static String KEY_RANDOM_PROMPT = "_KEY_RANDOM_PROMPT";
	
	public TeensSurveyScheduler(Context context) {
		super(context, USCTeensSurveyActivity.class, 
			new HashMap<String, Class<?>>() // prompt type to question set class 
			{ 
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				{ 
					put("CS", CSTeensSurvey.class); 
					put("Random", RandomTeensSurvey.class); 
				} 
			}
		);
	}
	
	@Override
	protected boolean isInPromptTime() {		
		Date today = new Date();
		int hour = today.getHours();
		int min  = today.getMinutes();	
		
		int startHour = Globals.DEFAULT_START_HOUR;
		int endHour   = Globals.DEFAULT_END_HOUR;
		
		return (hour >= startHour && hour < endHour - 1) || (hour == endHour - 1 && min < 30);
	}
	
	@Override 
	protected SurveyPromptEvent detectNewPrompt() {
		SurveyExtraInfo extInfo = AccelDataChecker.checkDataState(mContext, -1, -1);
		String reason = extInfo.getReason();
		
		if (!reason.equals("error") && !reason.equals("normal")) {
			SurveyPromptEvent spe = new SurveyPromptEvent(System.currentTimeMillis(), 0);
			spe.setPromptReason(reason);			
			spe.setPromptType("CS");
			spe.AddSurveySpecifiedRecord(CSTeensSurvey.START_TIME, extInfo.getStartTime());
			spe.AddSurveySpecifiedRecord(CSTeensSurvey.STOP_TIME, extInfo.getStopTime());
			return spe;
		}
		
		return null;
	}
	
	@Override
	protected boolean isSurveyPostponable(SurveyPromptEvent spe) {
		return spe.getPromptType().equals("CS");
	}
	
	@Override
	protected void reschedule() {
		long[] randomPromptTime = DataStorage.getPromptTimesKey(mContext, KEY_RANDOM_PROMPT);
		long[] csPromptTime = DataStorage.getPromptTimesKey(mContext, KEY_CS_PROMPT);
		PriorityQueue<Long> promptTimeQueue = new PriorityQueue<Long>(csPromptTime.length);
		
		for (int i = 0; i < csPromptTime.length; i++) {
			promptTimeQueue.add(csPromptTime[i]);
		}
		
		if (randomPromptTime != null) {			
			for (int j = 0; j < randomPromptTime.length; ++j) {
				boolean isExcluded = false;
				for (int i = 0; i < csPromptTime.length; ++i) {
					if (Math.abs(csPromptTime[i] - randomPromptTime[j]) < Globals.MIN_MS_BETWEEN_SCHEDULED_PROMPTS) {
						isExcluded = true;
						break;
					}
				}
				if (!isExcluded) {
					promptTimeQueue.add(randomPromptTime[j]);
				} else {
					DataStorage.SetValue(mContext, KEY_ALL_PROMPT_EVENT + randomPromptTime[j], null);
				}
			}
		}
		
		long[] allPromptTime = new long[promptTimeQueue.size()];
		for (int j = 0; j < allPromptTime.length; j++) {
			allPromptTime[j] = promptTimeQueue.poll();
		}
		
		DataStorage.setPromptTimesKey(mContext, allPromptTime, KEY_ALL_PROMPT);
	}
	
	@Override
	protected void onScheduleReset() {
		int promptsPerDay = Globals.DEFAULT_PROMPTS_PER_DAY;
		int startTimeHour = Globals.DEFAULT_START_HOUR;
		int endTimeHour   = Globals.DEFAULT_END_HOUR;
		
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

		for (int i = 0; i < promptsPerDay; i++) {
			promptSchedule.append("Prompt: " + DateHelper.getDate(promptTimes[i]) + NEWLINE);
			// Create the corresponding prompt event for the random prompts
			SurveyPromptEvent spe = new SurveyPromptEvent(System.currentTimeMillis(), 0);					
			spe.setPromptType("Random");
			DataStorage.SetValue(mContext, KEY_ALL_PROMPT_EVENT + promptTimes[i], new Gson().toJson(spe));
		}
		ServerLogger.sendNote(mContext, promptSchedule.toString(), Globals.NO_PLOT);

		// reset prompt schedule for the day (don't change the sequence here)
		DataStorage.setPromptTimesKey(mContext, promptTimes, KEY_RANDOM_PROMPT);
		PromptRecorder.writePromptSchedule(mContext, System.currentTimeMillis(), KEY_RANDOM_PROMPT, promptsPerDay, startTimeHour, endTimeHour);
		DataStorage.setPromptTimesKey(mContext, new long[] { promptsPerDay, startIntervalTimeMS, intervalIncMS }, KEY_SCHEDULE);		
		DataStorage.setPromptTimesKey(mContext, new long[] {}, KEY_CS_PROMPT);
		DataStorage.setPromptTimesKey(mContext, promptTimes, KEY_ALL_PROMPT);
	}
	
	@Override
	protected void onSurveyPrompting(final SurveyPromptEvent promptEvent) {
		String promptType = promptEvent.getPromptType();
		
		if (!promptEvent.isReprompt()) {
			Labeler.getInstance().addLabel(new Date(), promptType.equals("CS") ? "CS Prompt" : "Random Prompt");
		} else {
			Labeler.getInstance().addLabel(new Date(), promptType.equals("Random") ? "CS Reprompt" : "Random Reprompt");
		}
	}

	@Override
	protected int onAudioSelecting(final SurveyPromptEvent spe) {
		String promptType = spe.getPromptType();
		
		if (promptType.equals("Random")) {
			return PhonePrompter.CHIMES_NAMBOKU1;
		} else if (promptType.equals("CS")) {
			return PhonePrompter.CHIMES_CHIMES1;
		}
		
		return PhonePrompter.CHIMES_NONE; // prompts with no audio
	}
}
