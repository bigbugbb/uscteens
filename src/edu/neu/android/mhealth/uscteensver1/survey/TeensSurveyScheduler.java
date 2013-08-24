package edu.neu.android.mhealth.uscteensver1.survey;

import android.content.Context;

import com.google.gson.Gson;

import java.util.Date;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Random;

import edu.neu.android.mhealth.uscteensver1.activities.TeensSurveyActivity;
import edu.neu.android.mhealth.uscteensver1.data.AccelDataChecker;
import edu.neu.android.mhealth.uscteensver1.data.Labeler;
import edu.neu.android.wocketslib.Globals;
import edu.neu.android.wocketslib.algorithm.MotionInfo;
import edu.neu.android.wocketslib.emasurvey.SurveyScheduler;
import edu.neu.android.wocketslib.emasurvey.model.PromptRecorder;
import edu.neu.android.wocketslib.emasurvey.model.SurveyPromptEvent;
import edu.neu.android.wocketslib.support.AppInfo;
import edu.neu.android.wocketslib.support.DataStorage;
import edu.neu.android.wocketslib.support.ServerLogger;
import edu.neu.android.wocketslib.utils.DateHelper;
import edu.neu.android.wocketslib.utils.Log;
import edu.neu.android.wocketslib.utils.PhonePrompter;

public class TeensSurveyScheduler extends SurveyScheduler {
    private final static String TAG = "TeensSurveyScheduler";
    private final static String KEY_RANDOM_PROMPT = "_KEY_RANDOM_PROMPT";

    public TeensSurveyScheduler(Context context) {
        super(context, TeensSurveyActivity.class,
            new HashMap<String, Class<?>>() // prompt type to question set class
            {
                private static final long serialVersionUID = 1L;

                {
                    put("CS", TeensCSSurvey.class);
                    put("Random", TeensRandomSurvey.class);
                }
            }
        );

        TeensSurveyActivity.MINUTES_FOR_FIRST_QUESTION = 3;
    }

    @Override
    protected boolean isInPromptTime() {
        Date today = new Date();
        int hour = today.getHours();
        int min = today.getMinutes();

        int startHour = Globals.DEFAULT_START_HOUR;
        int endHour = Globals.DEFAULT_END_HOUR;

        return (hour >= startHour && hour < endHour - 1) || (hour == endHour - 1 && min < 45);
    }

    @Override
    protected SurveyPromptEvent detectNewPrompt() {
        MotionInfo motionInfo = AccelDataChecker.checkDataState(-1, -1);
        int code = motionInfo.getMotionCode();

        if (code != MotionInfo.ERROR && code != MotionInfo.NO_INTEREST) {
            SurveyPromptEvent spe = new SurveyPromptEvent(System.currentTimeMillis());
            spe.setPromptReason(motionInfo.getDetail());
            spe.setPromptType("CS");

            long lastPromptTime = AppInfo.GetLastTimePrompted(mContext, Globals.SURVEY);
            long internalLength = motionInfo.getStopTimeInMS() - motionInfo.getStartTimeInMS();
            long adjustedLength = Math.min(internalLength, (motionInfo.getStopTimeInMS() - lastPromptTime));
            spe.AddSurveySpecifiedRecord(TeensCSSurvey.INTERNAL_START_TIME, motionInfo.getStartTime());
            spe.AddSurveySpecifiedRecord(TeensCSSurvey.INTERNAL_STOP_TIME, motionInfo.getStopTime());
            spe.AddSurveySpecifiedRecord(TeensCSSurvey.INTERNAL_LENGTH, "" + internalLength / 60000);
            spe.AddSurveySpecifiedRecord(TeensCSSurvey.ADJUSTED_INTERVAL_LENGTH, "" + adjustedLength / 60000);
            return spe;
        }

        return null;
    }

    protected boolean isLastScheduledSurveyReprompted() {
        long lastScheduledPromptTime = getLastScheduledPromptTime(System.currentTimeMillis());
        if (lastScheduledPromptTime == 0) {
            return false;
        }
        SurveyPromptEvent spe = getEventByScheduledTime(lastScheduledPromptTime);
        return getPromptCount(spe.getID()) > 1;
    }

    @Override
    protected boolean isTimeForNextSurvey(long nextPromptTime) {
        long lastTimePrompted = AppInfo.GetLastTimePrompted(mContext, Globals.SURVEY);
        long timeSinceLastPrompt = System.currentTimeMillis() - lastTimePrompted;
        return timeSinceLastPrompt > Globals.REPROMPT_DELAY_MS;
    }

    @Override
    protected void reschedule() {
        long[] randomPromptTime = DataStorage.getPromptTimesKey(mContext, KEY_RANDOM_PROMPT);
        long[] csPromptTime = DataStorage.getPromptTimesKey(mContext, KEY_CS_PROMPT);
        PriorityQueue<Long> promptTimeQueue = new PriorityQueue<Long>(csPromptTime.length);

        for (long cst : csPromptTime) {
            promptTimeQueue.add(cst);
        }

        if (randomPromptTime != null) {
            for (long rt : randomPromptTime) {
                boolean isExcluded = false;
                for (long cst : csPromptTime) {
                    if (Math.abs(cst - rt) < Globals.MIN_MS_BETWEEN_SCHEDULED_PROMPTS) {
                        isExcluded = true;
                        break;
                    }
                }
                if (!isExcluded) {
                    promptTimeQueue.add(rt);
                } else {
                    DataStorage.SetValue(mContext, KEY_ALL_PROMPT_EVENT + rt, null);
                }
            }
        }

        long[] allPromptTime = promptTimeQueue.size() > 0 ? new long[promptTimeQueue.size()] : new long[]{};
        for (int i = 0; i < allPromptTime.length; ++i) {
            allPromptTime[i] = promptTimeQueue.poll();
        }

        DataStorage.setPromptTimesKey(mContext, allPromptTime, KEY_ALL_PROMPT);
    }

    @Override
    protected void onScheduleReset() {
        int promptsPerDay = Globals.DEFAULT_PROMPTS_PER_DAY;
        int startTimeHour = Globals.DEFAULT_START_HOUR;
        int endTimeHour = Globals.DEFAULT_END_HOUR;

        long totalPromptingWindowMS = (long) (endTimeHour - startTimeHour) * Globals.MINUTES_60_IN_MS;
        long intervalIncMS = (long) (totalPromptingWindowMS / (double) promptsPerDay);
        long promptTimes[] = promptsPerDay > 0 ? new long[promptsPerDay] : new long[]{};
        int startIntervalTimeMS = startTimeHour * Globals.MINUTES_60_IN_MS;
        long startDayTime = DateHelper.getDailyTime(0, 0); // Midnight

        StringBuilder promptSchedule = new StringBuilder();
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
            SurveyPromptEvent spe = new SurveyPromptEvent(promptTimes[i]);
            spe.setPromptType("Random");
            long internalLength = Globals.MIN_MS_BETWEEN_SCHEDULED_PROMPTS / 60000;
            long adjustedLength = Globals.MIN_MS_BETWEEN_SCHEDULED_PROMPTS / 60000;
            spe.AddSurveySpecifiedRecord(TeensCSSurvey.INTERNAL_LENGTH, "" + internalLength);
            spe.AddSurveySpecifiedRecord(TeensCSSurvey.ADJUSTED_INTERVAL_LENGTH, "" + adjustedLength);
            DataStorage.SetValue(mContext, KEY_ALL_PROMPT_EVENT + promptTimes[i], new Gson().toJson(spe));
        }
        ServerLogger.sendNote(mContext, promptSchedule.toString(), Globals.NO_PLOT);

        // reset prompt schedule for the day (don't change the sequence here)
        DataStorage.setPromptTimesKey(mContext, promptTimes, KEY_RANDOM_PROMPT);
        PromptRecorder.writePromptSchedule(mContext, System.currentTimeMillis(), KEY_RANDOM_PROMPT, promptsPerDay, startTimeHour, endTimeHour);
        DataStorage.setPromptTimesKey(mContext, new long[]{promptsPerDay, startIntervalTimeMS, intervalIncMS}, KEY_SCHEDULE);
        DataStorage.setPromptTimesKey(mContext, new long[]{}, KEY_CS_PROMPT);
        DataStorage.setPromptTimesKey(mContext, promptTimes, KEY_ALL_PROMPT);
    }

    @Override
    protected void onSurveyPrompting(final SurveyPromptEvent promptEvent) {
        String promptType = promptEvent.getPromptType();

        if (!promptEvent.isReprompt()) {
            Labeler.getInstance().addLabel(new Date(), promptType.equals("CS") ? "CS Prompt" : "Random Prompt");
        } else {
            Labeler.getInstance().addLabel(new Date(), promptType.equals("CS") ? "CS Reprompt" : "Random Reprompt");
        }
    }

    @Override
    protected int onAudioSelecting(final SurveyPromptEvent spe) {
        String promptType = spe.getPromptType();

        if (promptType.equals("CS")) {
            return PhonePrompter.CHIMES_NAMBOKU1;
        } else if (promptType.equals("Random")) {
            return PhonePrompter.CHIMES_HIKARI;
        }

        return PhonePrompter.CHIMES_NONE; // prompts with no audio
    }
}
