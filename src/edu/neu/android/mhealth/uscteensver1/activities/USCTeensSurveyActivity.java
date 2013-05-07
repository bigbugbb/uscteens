package edu.neu.android.mhealth.uscteensver1.activities;

import java.util.Date;

import edu.neu.android.mhealth.uscteensver1.data.Labeler;
import edu.neu.android.wocketslib.emasurvey.SurveyActivity;
import edu.neu.android.wocketslib.emasurvey.model.SurveyAnswer;
import edu.neu.android.wocketslib.emasurvey.model.SurveyQuestion;

public class USCTeensSurveyActivity extends SurveyActivity {
	
	protected void onExit() {
		for (SurveyQuestion question : popedQuestions) {
			String id = question.getQuestionId(); 
			id = id.substring(0, id.lastIndexOf('_'));
			if (isLocationQuestion(id)) { // location
				labelSelectedAnswer(question);
			} else if (isWhoAreYouWithQuestion(id)) {
				labelSelectedAnswer(question);
			}
		}
	}
	
	private void labelSelectedAnswer(SurveyQuestion question) {
		SurveyAnswer[] answers = question.getAnswers();
		for (SurveyAnswer answer : answers) {
			if (answer.isSelected()) {
				Labeler.addLabel(new Date(), answer.getAnswerText(), true);
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
