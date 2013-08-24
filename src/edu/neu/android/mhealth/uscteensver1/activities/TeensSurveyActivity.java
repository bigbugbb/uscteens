package edu.neu.android.mhealth.uscteensver1.activities;

import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;

import edu.neu.android.mhealth.uscteensver1.data.Labeler;
import edu.neu.android.wocketslib.Globals;
import edu.neu.android.wocketslib.emasurvey.SurveyActivity;
import edu.neu.android.wocketslib.emasurvey.model.SurveyAnswer;
import edu.neu.android.wocketslib.emasurvey.model.SurveyPromptEvent;
import edu.neu.android.wocketslib.emasurvey.model.SurveyQuestion;

public class TeensSurveyActivity extends SurveyActivity {

    @Override
    public void onStop() {
        if (isResponded()) {
            final SurveyPromptEvent promptEvent = getSurveyPromptEvent();
            if (promptEvent != null) {
                Labeler.getInstance().addLabel(new Date(), "Answer " + promptEvent.getPromptType());
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
                if (getSurveyPromptEvent().isReprompt()) {
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
