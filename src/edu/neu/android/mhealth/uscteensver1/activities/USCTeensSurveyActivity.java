package edu.neu.android.mhealth.uscteensver1.activities;

import java.util.ArrayList;
import java.util.Date;

import edu.neu.android.mhealth.uscteensver1.data.Labeler;
import edu.neu.android.wocketslib.emasurvey.SurveyActivity;
import edu.neu.android.wocketslib.emasurvey.model.SurveyAnswer;
import edu.neu.android.wocketslib.emasurvey.model.SurveyPromptEvent;
import edu.neu.android.wocketslib.emasurvey.model.SurveyQuestion;

public class USCTeensSurveyActivity extends SurveyActivity {
	
	@Override
	public void onStop() {
		if (isResponded()) {
			SurveyPromptEvent promptEvent = getSurveyPromptEvent();
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
				Labeler.getInstance().addLabel(new Date(time), answer.getAnswerText());
			}
		}
	}
	
	private boolean isLocationQuestion(String questionID) {
		final String locationQuestionIDs[] = {
			"Q3_m_Where", "Q3_n_WhereOther", 
		};
		
		for (int i = 0; i < locationQuestionIDs.length; ++i) {
			if (questionID.equals(locationQuestionIDs[i])) {
				return true;
			}
		}
		
		return false;
	}
	
	private boolean isWhoAreYouWithQuestion(String questionID) {
		final String whoAreYouWithQuestionIDs[] = {
			"Q2_l_Accompanies", "Q3_q_Accompanies", "Q4_m_Accompanies", "Q5_m_Accompanies",
		};
		
		for (int i = 0; i < whoAreYouWithQuestionIDs.length; ++i) {
			if (questionID.equals(whoAreYouWithQuestionIDs[i])) {
				return true;
			}
		}
		
		return false;
	}
}
