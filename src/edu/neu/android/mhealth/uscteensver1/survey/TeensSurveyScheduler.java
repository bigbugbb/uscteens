package edu.neu.android.mhealth.uscteensver1.survey;

import java.util.Date;

import android.content.Context;
import edu.neu.android.mhealth.uscteensver1.activities.USCTeensSurveyActivity;
import edu.neu.android.mhealth.uscteensver1.data.AccelDataChecker;
import edu.neu.android.mhealth.uscteensver1.data.Labeler;
import edu.neu.android.wocketslib.emasurvey.SurveyScheduler;
import edu.neu.android.wocketslib.emasurvey.model.SurveyExtraInfo;
import edu.neu.android.wocketslib.emasurvey.model.SurveyPromptEvent;

public class TeensSurveyScheduler extends SurveyScheduler {
	
	public TeensSurveyScheduler(Context context) {
		super(context, USCTeensSurveyActivity.class, CSTeensSurvey.class, RandomTeensSurvey.class);
	}
	
	@Override 
	protected SurveyExtraInfo detectCSPrompt() {
		SurveyExtraInfo extraInfo = AccelDataChecker.checkDataState(mContext, -1, -1);
		
		if (!extraInfo.getReason().equals("error") && !extraInfo.getReason().equals("normal")) {
			return extraInfo;
		}
		
		return null;
	}
	
	@Override
	protected void onSurveyPrompted(final SurveyPromptEvent promptEvent) {
		String promptType = promptEvent.getPromptType();
		if (!promptEvent.isReprompt()) {
			Labeler.getInstance().addLabel(new Date(), promptType.equals("CS") ? "CS Prompt" : "Random Prompt");
		} else {
			Labeler.getInstance().addLabel(new Date(), promptType.equals("Random") ? "CS Reprompt" : "Random Reprompt");
		}
	}

}
