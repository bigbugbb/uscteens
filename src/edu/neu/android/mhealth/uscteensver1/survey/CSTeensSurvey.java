package edu.neu.android.mhealth.uscteensver1.survey;

import java.util.ArrayList;
import java.util.PriorityQueue;

import edu.neu.android.wocketslib.emasurvey.model.QuestionComparator;
import edu.neu.android.wocketslib.emasurvey.model.QuestionSet;
import edu.neu.android.wocketslib.emasurvey.model.QuestionSetParamHandler;
import edu.neu.android.wocketslib.emasurvey.model.SurveyAnswer;
import edu.neu.android.wocketslib.emasurvey.model.SurveyQuestion;
import edu.neu.android.wocketslib.emasurvey.model.SurveyQuestion.TYPE;
import edu.neu.android.wocketslib.emasurvey.rule.ChanceBeChosen;
import edu.neu.android.wocketslib.emasurvey.rule.QuesFromAns;

public class CSTeensSurvey extends QuestionSet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private ArrayList<SurveyQuestion> defaultQuestionSet;
	private String context = null;
	public static final String optionalContext = "In the past four hours";
	public static final String defaultContext = "Since the last survey you answered";
	public static final int CS_EMA_DEFAULT = 0;
	public static final int CS_EMA_OPTIONAL = 1;
	
	public CSTeensSurvey(QuestionSetParamHandler param){
		super();
		if(param.getParamNum() != 1)
			return;
		int type = (Integer)param.getParams()[0];
		
		switch(type){
		case CS_EMA_DEFAULT:
			context = defaultContext;
			break;
		case CS_EMA_OPTIONAL:
			context = optionalContext;
			break;
		}
		setQuestions();
	}
	@Override
	public int getQuestionNum() {
		return defaultQuestionSet.size();
	}
	@Override
	public ArrayList<SurveyQuestion> getDefaultQuestionSet() {
		return defaultQuestionSet;
	}

	@Override
	protected void setQuestions() {
		// TODO Auto-generated method stub
		defaultQuestionSet  = new ArrayList<SurveyQuestion>();
/************ Initialize questions and answers *********/
		SurveyQuestion Q1_InhalerUse = new SurveyQuestion("Q1_InhalerUse", 
				"Did you use a CONTROL or RESCUE inhaler?", 
				TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet1 = new SurveyAnswer[3];
		answerSet1[0] = new SurveyAnswer(0, "Control Inhaler");
		answerSet1[1] = new SurveyAnswer(1, "Rescue Inhaler");
		answerSet1[2] = new SurveyAnswer(2, "I didn\'t use it.");
		Q1_InhalerUse.setDefault(SurveyQuestion.NO_DATA, answerSet1);
		defaultQuestionSet.add(Q1_InhalerUse);

		SurveyQuestion Q2_a_NeitherInhaler = new SurveyQuestion("Q2_a_NeitherInhaler", 
				"Why was the INHALER pressed?", 
				TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet2a = new SurveyAnswer[4];
		answerSet2a[0] = new SurveyAnswer(0, "I pressed it by accident.");
		answerSet2a[1] = new SurveyAnswer(1, "Someone else pressed it.");
		answerSet2a[2] = new SurveyAnswer(2, "It was bumped or something hit it.");
		answerSet2a[3] = new SurveyAnswer(3, "Other");
		Q2_a_NeitherInhaler.setDefault("Q1_InhalerUse", answerSet2a);
		defaultQuestionSet.add(Q2_a_NeitherInhaler);

		SurveyQuestion Q2_b_OtherInhaler = new SurveyQuestion("Q2_b_OtherInhaler", 
				"Why was the inhaler pressed? Please type your answer or type \"I don\'t know.\"", 
				TYPE.FREE_FORM_TEXT);
		SurveyAnswer[] answerSet2b = new SurveyAnswer[1];
		Q2_b_OtherInhaler.setDefault("Q2_a_NeitherInhaler", answerSet2b);
		defaultQuestionSet.add(Q2_b_OtherInhaler);

		SurveyQuestion Q3_a_AnyCoughing = new SurveyQuestion("Q3_a_AnyCoughing", 
				"Just before you used your inhaler, have you experienced COUGHING?",
				TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet3a = new SurveyAnswer[4];
		answerSet3a[0] = new SurveyAnswer(0, "Not at all");
		answerSet3a[1] = new SurveyAnswer(1, "A little");
		answerSet3a[2] = new SurveyAnswer(2, "Quite a bit");
		answerSet3a[3] = new SurveyAnswer(3, "Very much so");
		Q3_a_AnyCoughing.setDefault("Q1_InhalerUse", answerSet3a);
		defaultQuestionSet.add(Q3_a_AnyCoughing);

		SurveyQuestion Q3_b_AnyWheezing = new SurveyQuestion("Q3_b_AnyWheezing", 
				"Just before you used your inhaler, have you experienced WHEEZING?",
				TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet3b = new SurveyAnswer[4];
		answerSet3b[0] = new SurveyAnswer(0, "Not at all");
		answerSet3b[1] = new SurveyAnswer(1, "A little");
		answerSet3b[2] = new SurveyAnswer(2, "Quite a bit");
		answerSet3b[3] = new SurveyAnswer(3, "Very much so");
		Q3_b_AnyWheezing.setDefault("Q3_a_AnyCoughing", answerSet3b);
		defaultQuestionSet.add(Q3_b_AnyWheezing);

		SurveyQuestion Q3_c_AnyChestTightness = new SurveyQuestion("Q3_c_AnyChestTightness", 
				"Just before you used your inhaler, have you experienced CHEST TIGHTNESS?",
				TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet3c = new SurveyAnswer[4];
		answerSet3c[0] = new SurveyAnswer(0, "Not at all");
		answerSet3c[1] = new SurveyAnswer(1, "A little");
		answerSet3c[2] = new SurveyAnswer(2, "Quite a bit");
		answerSet3c[3] = new SurveyAnswer(3, "Very much so");
		Q3_c_AnyChestTightness.setDefault("Q3_b_AnyWheezing", answerSet3c);
		defaultQuestionSet.add(Q3_c_AnyChestTightness);

		SurveyQuestion Q3_d_AnyShortnessBreath = new SurveyQuestion("Q3_d_AnyShortnessBreath", 
				"Just before you used your inhaler, have you experienced SHORTNESS OF BREATH?",
				TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet3d = new SurveyAnswer[4];
		answerSet3d[0] = new SurveyAnswer(0, "Not at all");
		answerSet3d[1] = new SurveyAnswer(1, "A little");
		answerSet3d[2] = new SurveyAnswer(2, "Quite a bit");
		answerSet3d[3] = new SurveyAnswer(3, "Very much so");
		Q3_d_AnyShortnessBreath.setDefault("Q3_c_AnyChestTightness", answerSet3d);
		defaultQuestionSet.add(Q3_d_AnyShortnessBreath);

		SurveyQuestion Q4_a_MainActivity = new SurveyQuestion("Q4_a_MainActivity", 
				"What were you DOING just before you used your inhaler?\n (Choose all that apply)",
				TYPE.MULTI_CHOICE);
		SurveyAnswer[] answerSet4a = new SurveyAnswer[7];
		answerSet4a[0] = new SurveyAnswer(0, "Reading/Computer/Homework");
		answerSet4a[1] = new SurveyAnswer(1, "Using technology (TV, phone)");
		answerSet4a[2] = new SurveyAnswer(2, "Active Play/Sports/Exercising");
		answerSet4a[3] = new SurveyAnswer(3, "Eating/Drinking");
		answerSet4a[4] = new SurveyAnswer(4, "Going somewhere");
		answerSet4a[5] = new SurveyAnswer(5, "Sleeping");
		answerSet4a[6] = new SurveyAnswer(6, "Something else");
		Q4_a_MainActivity.setDefault("Q1_a_HowHappy", answerSet4a);
		defaultQuestionSet.add(Q4_a_MainActivity);

		SurveyQuestion Q4_b_SomethingElse = new SurveyQuestion("Q4_b_SomethingElse", 
				"Please specify what you were DOING just before you used your inhaler:",
				TYPE.FREE_FORM_TEXT);
		SurveyAnswer[] answerSet4b = new SurveyAnswer[1];
		Q4_b_SomethingElse.setDefault("Q4_a_MainActivity", answerSet4b);
		defaultQuestionSet.add(Q4_b_SomethingElse);
		
		SurveyQuestion Q4_c_UsingTech = new SurveyQuestion("Q4_c_UsingTech", 
				"While using technology (TV, phone), were you:",
				TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet4c = new SurveyAnswer[6];
		answerSet4c[0] = new SurveyAnswer(0, "Playing video games");
		answerSet4c[1] = new SurveyAnswer(1, "Talking");
		answerSet4c[2] = new SurveyAnswer(2, "Texting");
		answerSet4c[3] = new SurveyAnswer(3, "Using the Internet");
		answerSet4c[4] = new SurveyAnswer(4, "Watching shows/movies");
		answerSet4c[5] = new SurveyAnswer(5, "Other");
		Q4_c_UsingTech.setDefault("Q4_a_MainActivity", answerSet4c);
		defaultQuestionSet.add(Q4_c_UsingTech);

		SurveyQuestion Q4_d_GoingSomewhere = new SurveyQuestion("Q4_d_GoingSomewhere", 
				"While going somewhere, were you:",
				TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet4d = new SurveyAnswer[6];
		answerSet4d[0] = new SurveyAnswer(0, "Walking");
		answerSet4d[1] = new SurveyAnswer(1, "Biking");
		answerSet4d[2] = new SurveyAnswer(2, "Riding a bus");
		answerSet4d[3] = new SurveyAnswer(3, "Riding the Metro/train");
		answerSet4d[4] = new SurveyAnswer(4, "Riding in a car/taxi");
		answerSet4d[5] = new SurveyAnswer(5, "Other (skateboarding, etc.)");
		Q4_d_GoingSomewhere.setDefault("Q4_a_MainActivity", answerSet4d);
		defaultQuestionSet.add(Q4_d_GoingSomewhere);

		SurveyQuestion Q5_a_PhysicalContext = new SurveyQuestion("Q5_a_PhysicalContext", 
				"WHERE were you just before you used your inhaler?",
				TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet5a = new SurveyAnswer[8];
		answerSet5a[0] = new SurveyAnswer(0, "Home (Indoors)");
		answerSet5a[1] = new SurveyAnswer(1, "School");
		answerSet5a[2] = new SurveyAnswer(2, "Outdoors");
		answerSet5a[3] = new SurveyAnswer(3, "Restaurant");
		answerSet5a[4] = new SurveyAnswer(4, "Store/Mall");
		answerSet5a[5] = new SurveyAnswer(5, "Someone else\'s house (Indoors)");
		answerSet5a[6] = new SurveyAnswer(6, "In a car");
		answerSet5a[7] = new SurveyAnswer(7, "Other");
		Q5_a_PhysicalContext.setDefault("Q4_a_MainActivity", answerSet5a);
		defaultQuestionSet.add(Q5_a_PhysicalContext);

		SurveyQuestion Q5_b_Outdoors = new SurveyQuestion("Q5_b_Outdoors", 
				"Where were you OUTDOORS?",
				TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet5b = new SurveyAnswer[7];
		answerSet5b[0] = new SurveyAnswer(0, "Home (front/back yard)");
		answerSet5b[1] = new SurveyAnswer(1, "School");
		answerSet5b[2] = new SurveyAnswer(2, "Park/trail");
		answerSet5b[3] = new SurveyAnswer(3, "Sidewalk");
		answerSet5b[4] = new SurveyAnswer(4, "Road");
		answerSet5b[5] = new SurveyAnswer(5, "Parking lot");
		answerSet5b[6] = new SurveyAnswer(6, "Other");
		Q5_b_Outdoors.setDefault("Q5_a_PhysicalContext", answerSet5b);
		defaultQuestionSet.add(Q5_b_Outdoors);

		SurveyQuestion Q6_SocialContext = new SurveyQuestion("Q6_a_SocialContext", 
				"Just before you used your inhaler, were you:\n(Choose all that apply)",
				TYPE.MULTI_CHOICE);
		SurveyAnswer[] answerSet6a = new SurveyAnswer[4];
		answerSet6a[0] = new SurveyAnswer(0, "Alone");
		answerSet6a[1] = new SurveyAnswer(1, "With your mom/dad");
		answerSet6a[2] = new SurveyAnswer(2, "With your sister(s) or brother(s)");
		answerSet6a[3] = new SurveyAnswer(3, "With your friend(s)");
		Q6_SocialContext.setDefault("Q1a_HowHappy", answerSet6a);
		defaultQuestionSet.add(Q6_SocialContext);

		SurveyQuestion Q7_a_AnyStressful = new SurveyQuestion("Q7_a_AnyStressful", 
				context+", has anything stressful happened to you?",
				TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet7a = new SurveyAnswer[3];
		answerSet7a[0] = new SurveyAnswer(0, "No stressful things have happened");
		answerSet7a[1] = new SurveyAnswer(1, "A few stressful things have happened");
		answerSet7a[2] = new SurveyAnswer(2, "Many stressful things have happened");
		Q7_a_AnyStressful.setDefault("Q1a_HowHappy", answerSet7a);
		defaultQuestionSet.add(Q7_a_AnyStressful);

		SurveyQuestion Q7_b_AnyTeased = new SurveyQuestion("Q7_b_AnyTeased", 
				context+", has anyone teased you?",
				TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet7b = new SurveyAnswer[5];
		answerSet7b[0] = new SurveyAnswer(0, "Yes, and caused very much stress");
		answerSet7b[1] = new SurveyAnswer(1, "Yes, and caused some stress");
		answerSet7b[2] = new SurveyAnswer(2, "Yes, and caused a little stress");
		answerSet7b[3] = new SurveyAnswer(3, "Yes, but not at all stressful");
		answerSet7b[4] = new SurveyAnswer(4, "No");
		Q7_b_AnyTeased.setDefault("Q7_a_AnyStressful", answerSet7b);
		defaultQuestionSet.add(Q7_b_AnyTeased);

		SurveyQuestion Q7_c_HaveArgued = new SurveyQuestion("Q7_c_HaveArgued", 
				context+", have you argued with anyone?",
				TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet7c = new SurveyAnswer[5];
		answerSet7c[0] = new SurveyAnswer(0, "Yes, and caused very much stress");
		answerSet7c[1] = new SurveyAnswer(1, "Yes, and caused some stress");
		answerSet7c[2] = new SurveyAnswer(2, "Yes, and caused a little stress");
		answerSet7c[3] = new SurveyAnswer(3, "Yes, but not at all stressful");
		answerSet7c[4] = new SurveyAnswer(4, "No");
		Q7_c_HaveArgued.setDefault("Q7_b_AnyTeased", answerSet7c);
		defaultQuestionSet.add(Q7_c_HaveArgued);

		SurveyQuestion Q7_d_DisagreeWithParents = new SurveyQuestion("Q7_d_DisagreeWithParents", 
				context+", have you had a misunderstanding or disagreement with your parents?",
				TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet7d = new SurveyAnswer[5];
		answerSet7d[0] = new SurveyAnswer(0, "Yes, and caused very much stress");
		answerSet7d[1] = new SurveyAnswer(1, "Yes, and caused some stress");
		answerSet7d[2] = new SurveyAnswer(2, "Yes, and caused a little stress");
		answerSet7d[3] = new SurveyAnswer(3, "Yes, but not at all stressful");
		answerSet7d[4] = new SurveyAnswer(4, "No");
		Q7_d_DisagreeWithParents.setDefault("Q7_c_HaveArgued", answerSet7d);
		defaultQuestionSet.add(Q7_d_DisagreeWithParents);

		SurveyQuestion Q7_e_TooManyWork = new SurveyQuestion("Q7_e_TooManyWork", 
				context+", have you had too many things to do?",
				TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet7e = new SurveyAnswer[5];
		answerSet7e[0] = new SurveyAnswer(0, "Yes, and caused very much stress");
		answerSet7e[1] = new SurveyAnswer(1, "Yes, and caused some stress");
		answerSet7e[2] = new SurveyAnswer(2, "Yes, and caused a little stress");
		answerSet7e[3] = new SurveyAnswer(3, "Yes, but not at all stressful");
		answerSet7e[4] = new SurveyAnswer(4, "No");
		Q7_e_TooManyWork.setDefault("Q7_d_DisagreeWithParents", answerSet7e);
		defaultQuestionSet.add(Q7_e_TooManyWork);
		/*************** Set up rules *******************/
		for (int i = 0; i < defaultQuestionSet.size(); i++) {
			defaultQuestionSet.get(i).addRules(new ChanceBeChosen(1));
		}
		//set up branching questions and rules
		ArrayList<SurveyQuestion> questionSection_1 = new ArrayList<SurveyQuestion>();
		questionSection_1.add(Q2_a_NeitherInhaler);
		Q1_InhalerUse.addRules(new QuesFromAns(new int[]{2}, questionSection_1));

		ArrayList<SurveyQuestion> questionSection_2 = new ArrayList<SurveyQuestion>();
		questionSection_2.add(Q2_b_OtherInhaler);
		Q2_a_NeitherInhaler.addRules(new QuesFromAns(new int[]{3}, questionSection_2));

		ArrayList<SurveyQuestion> questionSection_3 = new ArrayList<SurveyQuestion>();
		questionSection_3.add(Q3_a_AnyCoughing);
		questionSection_3.add(Q3_b_AnyWheezing);
		questionSection_3.add(Q3_c_AnyChestTightness);
		questionSection_3.add(Q3_d_AnyShortnessBreath);
		questionSection_3.add(Q4_a_MainActivity);
		questionSection_3.add(Q5_a_PhysicalContext);
		questionSection_3.add(Q6_SocialContext);
		questionSection_3.add(Q7_a_AnyStressful);
		questionSection_3.add(Q7_b_AnyTeased);
		questionSection_3.add(Q7_c_HaveArgued);
		questionSection_3.add(Q7_d_DisagreeWithParents);
		questionSection_3.add(Q7_e_TooManyWork);
		Q1_InhalerUse.addRules(new QuesFromAns(new int[]{0,1}, questionSection_3));

		ArrayList<SurveyQuestion> questionSection_4 = new ArrayList<SurveyQuestion>();
		questionSection_4.add(Q4_b_SomethingElse);
		Q4_a_MainActivity.addRules(new QuesFromAns(new int[]{6}, questionSection_4));

		ArrayList<SurveyQuestion> questionSection_4b = new ArrayList<SurveyQuestion>();
		questionSection_4b.add(Q4_c_UsingTech);
		Q4_a_MainActivity.addRules(new QuesFromAns(new int[]{1}, questionSection_4b));

		ArrayList<SurveyQuestion> questionSection_4c = new ArrayList<SurveyQuestion>();
		questionSection_4c.add(Q4_d_GoingSomewhere);
		Q4_a_MainActivity.addRules(new QuesFromAns(new int[]{4}, questionSection_4c));

		ArrayList<SurveyQuestion> questionSection_5 = new ArrayList<SurveyQuestion>();
		questionSection_5.add(Q5_b_Outdoors);
		Q5_a_PhysicalContext.addRules(new QuesFromAns(new int[]{2}, questionSection_5));

	}
	@Override
	public String getReadableQuestionSetName() {
		// TODO Auto-generated method stub
		return "CS-EMA";
	}
	
	@Override
	public String[] getAllQuesIDs() {
		// TODO Auto-generated method stub
		String[] ids = new String[defaultQuestionSet.size()];
		for (int i = 0; i < ids.length; i++) {
			ids[i] = defaultQuestionSet.get(i).getQuestionId();
		}
		return ids;
	}

}
