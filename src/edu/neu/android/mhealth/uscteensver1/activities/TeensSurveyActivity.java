package edu.neu.android.mhealth.uscteensver1.activities;

import java.util.ArrayList;
import java.util.Date;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;
import edu.neu.android.mhealth.uscteensver1.R;
import edu.neu.android.mhealth.uscteensver1.data.Labeler;
import edu.neu.android.wocketslib.Globals;
import edu.neu.android.wocketslib.emasurvey.SurveyActivity;
import edu.neu.android.wocketslib.emasurvey.model.SurveyAnswer;
import edu.neu.android.wocketslib.emasurvey.model.SurveyPromptEvent;
import edu.neu.android.wocketslib.emasurvey.model.SurveyQuestion;
import edu.neu.android.wocketslib.support.DataStorage;

public class TeensSurveyActivity extends SurveyActivity {
	
	private static final String KEY_COUNT_TIME       = "KEY_COUNT_TIME";
	private static final String KEY_TOTAL_SURVEY     = "KEY_TOTAL_SURVEY"; 
	private static final String KEY_COMPLETED_SURVEY = "KEY_COMPLETED_SURVEY";	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// clear the count for the previous day	
		long lastTime = DataStorage.GetValueLong(mContext, KEY_COUNT_TIME, System.currentTimeMillis());
		Date lastDate = new Date(lastTime);
		Date now = new Date();
		if (now.getDay() != lastDate.getDay()) {
			DataStorage.SetValue(mContext, KEY_TOTAL_SURVEY, 0);
			DataStorage.SetValue(mContext, KEY_COMPLETED_SURVEY, 0);
		}
		DataStorage.SetValue(mContext, KEY_COUNT_TIME, System.currentTimeMillis());
				
		// count the total survey for the current day		
		long total = DataStorage.GetValueLong(mContext, KEY_TOTAL_SURVEY, 0);
		DataStorage.SetValue(mContext, KEY_TOTAL_SURVEY, ++total);
	}

    @Override
    public void onStop() {
        if (isResponded()) {
            final SurveyPromptEvent spe = getSurveyPromptEvent();
            if (spe != null) {
                Labeler.getInstance().addLabel(new Date(), "Answer " + spe.getPromptType());
            }
        }

        super.onStop();
    }

    @Override
    public void onDestroy() {
        ArrayList<SurveyQuestion> poppedQuestions = getPoppedQuestions();

        if (poppedQuestions != null) {
            for (SurveyQuestion question : poppedQuestions) {
                String id = question.getQuestionId();
                id = id.substring(0, id.lastIndexOf('_'));
                if (isLocationQuestion(id)) { // location
                    labelSelectedAnswer(question);
                } else if (isWhoAreYouWithQuestion(id)) {
                    labelSelectedAnswer(question);
                }
            }
        }
        
        // count the completed survey for the current day
        Context context = getApplicationContext();
		long complete = DataStorage.GetValueLong(context, KEY_COMPLETED_SURVEY, 0);
		DataStorage.SetValue(context, KEY_COMPLETED_SURVEY, isCompleted() ? ++complete : complete);
		
		long total = DataStorage.GetValueLong(context, KEY_TOTAL_SURVEY, 1);
		// notifies the user of their compliance (percentage or ratio, 
		// e.g. 14/16 surveys completed or 14 completed, 2 missed).
		// Build notification
		Notification notification = new NotificationCompat.Builder(context)
			.setSmallIcon(R.drawable.ic_notification)
	        .setContentTitle("TeenGame")
	        .setWhen(System.currentTimeMillis())
	        .setContentText(String.format("Survey: %d completed, %d missed", complete, total - complete))	        
	        .setContentIntent(PendingIntent.getActivity(mContext, 0, new Intent(), 0))
	        .setAutoCancel(true)
	        .build();		    		  				
				    
		NotificationManager notificationManager = 
				(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notificationManager.notify(100, notification);
		
        super.onDestroy();
    }

    @Override
    protected void onSurveyNotCompleted() {
        mPromptSender.addPromptEvent(
            "Q" + mQuestIndex + " was not answered in " +
                    (mQuestIndex == 1 ? MINUTES_FOR_FIRST_QUESTION : MINUTES_FOR_OTHER_QUESTION) + " min" + (mQuestIndex == 1 ? "s" : ""),
            new Date(mPromptEvent.getPromptTime()), mPromptEvent.getPromptType(), new Date()
        );
    }

    @Override
    public void onQuestionLifeExpired(int whichQuestion) {
        Log.i(TAG, "onQuestionLifeExpired");
        switch (whichQuestion) {
        case 1:
            if (getSurveyPromptEvent().getRepromptCount() > 1) {
                finish();
                Toast.makeText(mContext, "Survey timed out!", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "first question life expired");
            } else {
                moveTaskToBack(false);
                Log.i(TAG, "move task to back");
            }
            break;
        default:
            finish();
            Toast.makeText(mContext, "Survey timed out!", Toast.LENGTH_SHORT).show();
            break;
        }
    }

    @Override
    public boolean isRepromptAccepted(long promptCount) {
        return mQuestIndex == 1 && promptCount <= 3;
    }

    private void labelSelectedAnswer(SurveyQuestion question) {
        SurveyAnswer[] answers = question.getAnswers();
        SurveyPromptEvent promptEvent = getSurveyPromptEvent();

        for (SurveyAnswer answer : answers) {
            if (answer == null || !answer.isSelected()) {
                continue;
            }

            if (promptEvent == null) {
                Labeler.getInstance().addLabel(new Date(), answer.getAnswerText());
            } else {
                long time = promptEvent.getScheduledPromptTime(); // the time is actually not accurate
                Labeler.getInstance().addLabel(new Date(time - Globals.MINUTES_15_IN_MS), answer.getAnswerText());
            }
        }
    }

    private boolean isLocationQuestion(String questionID) {
        final String locationQuestionIDs[] = {
            "Q3_m_Where", "Q3_n_WhereOther",
        };

        for (String locationQuestionID : locationQuestionIDs) {
            if (questionID.equals(locationQuestionID)) {
                return true;
            }
        }

        return false;
    }

    private boolean isWhoAreYouWithQuestion(String questionID) {
        final String whoAreYouWithQuestionIDs[] = {
            "Q2_l_Accompanies", "Q3_q_Accompanies", "Q4_m_Accompanies", "Q5_m_Accompanies",
        };

        for (String whoAreYouWithQuestionID : whoAreYouWithQuestionIDs) {
            if (questionID.equals(whoAreYouWithQuestionID)) {
                return true;
            }
        }

        return false;
    }
}
