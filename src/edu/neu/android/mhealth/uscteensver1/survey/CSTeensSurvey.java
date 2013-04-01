package edu.neu.android.mhealth.uscteensver1.survey;

import java.util.ArrayList;

import edu.neu.android.wocketslib.emasurvey.model.QuestionSet;
import edu.neu.android.wocketslib.emasurvey.model.QuestionSetParamHandler;
import edu.neu.android.wocketslib.emasurvey.model.SurveyAnswer;
import edu.neu.android.wocketslib.emasurvey.model.SurveyQuestion;
import edu.neu.android.wocketslib.emasurvey.model.SurveyQuestion.TYPE;

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
		defaultQuestionSet = new ArrayList<SurveyQuestion>();
/************ Initialize questions and answers *********/
		SurveyQuestion Q1_TeenActivity = new SurveyQuestion("Q1_TeenActivity", 
				"What have you been DOING for the past hour//since the last survey you answered? Please choose all that apply.", 
				TYPE.MULTI_CHOICE);
		SurveyAnswer[] answerSet1 = new SurveyAnswer[6];
		answerSet1[0] = new SurveyAnswer(0, "Reading or doing homework");
		answerSet1[1] = new SurveyAnswer(1, "Using technology (TV, phone)");
		answerSet1[2] = new SurveyAnswer(2, "Eating/drinking");
		answerSet1[3] = new SurveyAnswer(3, "Sports/Exercising");
		answerSet1[4] = new SurveyAnswer(4, "Going somewhere");
		answerSet1[5] = new SurveyAnswer(5, "Other");		
		Q1_TeenActivity.setDefault(SurveyQuestion.NO_DATA, answerSet1);
		defaultQuestionSet.add(Q1_TeenActivity);

		SurveyQuestion Q1_a_UseTech = new SurveyQuestion("Q1_a_UseTech", 
				"While using technology (TV, phone), were you:", 
				TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet1a = new SurveyAnswer[6];
		answerSet1a[0] = new SurveyAnswer(0, "Playing video games");
		answerSet1a[1] = new SurveyAnswer(1, "Talking");
		answerSet1a[2] = new SurveyAnswer(2, "Texting");
		answerSet1a[3] = new SurveyAnswer(3, "Using the Internet");
		answerSet1a[4] = new SurveyAnswer(4, "Watching shows/movies");
		answerSet1a[5] = new SurveyAnswer(5, "Other");
		Q1_a_UseTech.setDefault("Q1_TeenActivity", answerSet1a);
		defaultQuestionSet.add(Q1_a_UseTech);

		SurveyQuestion Q2_a_SpendTime = new SurveyQuestion("Q2_a_SpendTime", 
				"Approximately how many minutes did you spend [ANSWER 1]?", 
				TYPE.MINUTES_PICKER);
		SurveyAnswer[] answerSet2a = new SurveyAnswer[1];
		Q2_a_SpendTime.setDefault("Q1_TeenActivity", answerSet2a);
		defaultQuestionSet.add(Q2_a_SpendTime);

		SurveyQuestion Q2_b_WhileAns1 = new SurveyQuestion("Q2_b_WhileAns1", 
				"While [ANSWER 1], were you:",
				TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet2b = new SurveyAnswer[5];
		answerSet2b[0] = new SurveyAnswer(0, "Lying down");
		answerSet2b[1] = new SurveyAnswer(1, "Sitting");
		answerSet2b[2] = new SurveyAnswer(2, "Standing");
		answerSet2b[3] = new SurveyAnswer(3, "Walking");
		answerSet2b[4] = new SurveyAnswer(4, "Jogging/Running");
		Q2_b_WhileAns1.setDefault("Q1_TeenActivity", answerSet2b);
		defaultQuestionSet.add(Q2_b_WhileAns1);

		SurveyQuestion Q2_b_a_WhileGoing = new SurveyQuestion("Q2_b_a_WhileGoing", 
				"While going somewhere, were you:",
				TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet2ba = new SurveyAnswer[6];
		answerSet2ba[0] = new SurveyAnswer(0, "Walking");
		answerSet2ba[1] = new SurveyAnswer(1, "Biking");
		answerSet2ba[2] = new SurveyAnswer(2, "Riding a bus");
		answerSet2ba[3] = new SurveyAnswer(3, "Riding the Metro/train");
		answerSet2ba[4] = new SurveyAnswer(4, "Riding in a car/taxi");
		answerSet2ba[5] = new SurveyAnswer(5, "Other (skateboarding, etc.)");
		Q2_b_a_WhileGoing.setDefault("Q1_TeenActivity", answerSet2ba);
		defaultQuestionSet.add(Q2_b_a_WhileGoing);

		SurveyQuestion Q2_c_HowHavePhone = new SurveyQuestion("Q2_c_HowHavePhone", 
				"How did you have the PHONE while [ANSWER 1]?",
				TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet2c = new SurveyAnswer[6];
		answerSet2c[0] = new SurveyAnswer(0, "On my belt");
		answerSet2c[1] = new SurveyAnswer(1, "In my pocket");
		answerSet2c[2] = new SurveyAnswer(2, "In my handbag/purse/backpack");
		answerSet2c[3] = new SurveyAnswer(3, "Holding in my hand");
		answerSet2c[4] = new SurveyAnswer(4, "Within reach, but not on me");
		answerSet2c[5] = new SurveyAnswer(5, "Not with me");
		Q2_c_HowHavePhone.setDefault("Q1_TeenActivity", answerSet2c);
		defaultQuestionSet.add(Q2_c_HowHavePhone);

		SurveyQuestion Q2_d_ReasonNotCarrying = new SurveyQuestion("Q2_d_ReasonNotCarrying", 
				"Please indicate your reason for not carrying your phone while [ANSWER 1]?",
				TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet2d = new SurveyAnswer[7];
		answerSet2d[0] = new SurveyAnswer(0, "Forgot it");
		answerSet2d[1] = new SurveyAnswer(1, "Battery died");
		answerSet2d[2] = new SurveyAnswer(2, "Did not want to damage it");
		answerSet2d[3] = new SurveyAnswer(3, "Too bulky");
		answerSet2d[4] = new SurveyAnswer(4, "Too uncomfortable");
		answerSet2d[5] = new SurveyAnswer(5, "Embarrassed to carry it");
		answerSet2d[6] = new SurveyAnswer(6, "Not allowed to carry it");
		Q2_d_ReasonNotCarrying.setDefault("Q2_c_HowHavePhone", answerSet2d);
		defaultQuestionSet.add(Q2_d_ReasonNotCarrying);

		SurveyQuestion Q2_e_MainPurpose = new SurveyQuestion("Q2_e_MainPurpose", 
				"What was the MAIN PURPOSE of [ANSWER 1]?",
				TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet2e = new SurveyAnswer[6];
		answerSet2e[0] = new SurveyAnswer(0, "Fun/Recreation");
		answerSet2e[1] = new SurveyAnswer(1, "Personal care");
		answerSet2e[2] = new SurveyAnswer(2, "To get somewhere");
		answerSet2e[3] = new SurveyAnswer(3, "Baby sitting/Childcare");
		answerSet2e[4] = new SurveyAnswer(4, "For work, homework, or housework");
		answerSet2e[5] = new SurveyAnswer(5, "Other");		
		Q2_e_MainPurpose.setDefault("Q1_TeenActivity", answerSet2e);
		defaultQuestionSet.add(Q2_e_MainPurpose);

		SurveyQuestion Q2_e_a_MealSnack = new SurveyQuestion("Q2_e_a_MealSnack", 
				"Was this eating or drinking a meal or a snack?",
				TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet2ea = new SurveyAnswer[2];
		answerSet2ea[0] = new SurveyAnswer(0, "Meal");
		answerSet2ea[1] = new SurveyAnswer(1, "Snack");
		Q2_e_a_MealSnack.setDefault("Q1_TeenActivity", answerSet2ea);
		defaultQuestionSet.add(Q2_e_a_MealSnack);
		
		SurveyQuestion Q2_f_Enjoyable = new SurveyQuestion("Q2_f_Enjoyable", 
				"How ENJOYABLE was [ANSWER 1]?",
				TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet2f = new SurveyAnswer[5];
		answerSet2f[0] = new SurveyAnswer(0, "Not at all");
		answerSet2f[1] = new SurveyAnswer(1, "A little");
		answerSet2f[2] = new SurveyAnswer(2, "Moderately");
		answerSet2f[3] = new SurveyAnswer(3, "Quite a bit");
		answerSet2f[4] = new SurveyAnswer(4, "Extremely");		
		Q2_f_Enjoyable.setDefault("Q1_TeenActivity", answerSet2f);
		defaultQuestionSet.add(Q2_f_Enjoyable);

		SurveyQuestion Q2_g_WantToDo = new SurveyQuestion("Q2_g_WantToDo", 
				"Were you [ANSWER 1] because YOU want to do it?",
				TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet2g = new SurveyAnswer[2];
		answerSet2g[0] = new SurveyAnswer(0, "Yes");
		answerSet2g[1] = new SurveyAnswer(1, "No");		
		Q2_g_WantToDo.setDefault("Q1_TeenActivity", answerSet2g);
		defaultQuestionSet.add(Q2_g_WantToDo);

		SurveyQuestion Q2_h_ParentsWant = new SurveyQuestion("Q2_h_ParentsWant", 
				"Were you [ANSWER 1] because YOUR PARENTS want you to do it?",
				TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet2h = new SurveyAnswer[2];
		answerSet2h[0] = new SurveyAnswer(0, "Yes");
		answerSet2h[1] = new SurveyAnswer(1, "No");		
		Q2_h_ParentsWant.setDefault("Q2_g_WantToDo", answerSet2h);
		defaultQuestionSet.add(Q2_h_ParentsWant);
		
		SurveyQuestion Q2_i_FriendsWant = new SurveyQuestion("Q2_i_ParentsWant", 
				"Were you [ANSWER 1] because YOUR FRIENDS want you to do it?",
				TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet2i = new SurveyAnswer[2];
		answerSet2i[0] = new SurveyAnswer(0, "Yes");
		answerSet2i[1] = new SurveyAnswer(1, "No");		
		Q2_i_FriendsWant.setDefault("Q2_g_WantToDo", answerSet2i);
		defaultQuestionSet.add(Q2_i_FriendsWant);
		
		SurveyQuestion Q2_j_FriendsWant = new SurveyQuestion("Q2_j_FriendsWant", 
				"Were you [ANSWER 1] because YOUR TEACHERS want you to do it?",
				TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet2j = new SurveyAnswer[2];
		answerSet2j[0] = new SurveyAnswer(0, "Yes");
		answerSet2j[1] = new SurveyAnswer(1, "No");		
		Q2_j_FriendsWant.setDefault("Q2_g_WantToDo", answerSet2j);
		defaultQuestionSet.add(Q2_j_FriendsWant);
		
		SurveyQuestion Q2_k_Alone = new SurveyQuestion("Q2_k_Alone", 
				"Were you [ANSWER 1] ALONE?",
				TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet2k = new SurveyAnswer[2];
		answerSet2k[0] = new SurveyAnswer(0, "Yes");
		answerSet2k[1] = new SurveyAnswer(1, "No");		
		Q2_k_Alone.setDefault("Q1_TeenActivity", answerSet2k);
		defaultQuestionSet.add(Q2_k_Alone);
		
		SurveyQuestion Q2_l_AnsWith = new SurveyQuestion("Q2_l_AnsWith", 
				"While [ANSWER 1], were you with: Please choose all that apply.",
				TYPE.MULTI_CHOICE);
		SurveyAnswer[] answerSet2l = new SurveyAnswer[5];
		answerSet2l[0] = new SurveyAnswer(0, "Friends");
		answerSet2l[1] = new SurveyAnswer(1, "Parents");
		answerSet2l[2] = new SurveyAnswer(2, "Siblings");
		answerSet2l[3] = new SurveyAnswer(3, "Teammates/Classmates");
		answerSet2l[4] = new SurveyAnswer(4, "People You Don't Know");
		Q2_l_AnsWith.setDefault("Q2_k_Alone", answerSet2l);
		defaultQuestionSet.add(Q2_l_AnsWith);
		
		SurveyQuestion Q3_a_WhatType = new SurveyQuestion("Q3_a_WhatType", 
				"What type of sports or exercise activity?",
				TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet3a = new SurveyAnswer[9];
		answerSet3a[0] = new SurveyAnswer(0, "Basketball/Football/Soccer");
		answerSet3a[1] = new SurveyAnswer(1, "Other running/Jogging");
		answerSet3a[2] = new SurveyAnswer(2, "Exercise/Dance/Karate class");
		answerSet3a[3] = new SurveyAnswer(3, "Weightlifting/Strength");
		answerSet3a[4] = new SurveyAnswer(4, "Training");
		answerSet3a[5] = new SurveyAnswer(5, "Walking");
		answerSet3a[6] = new SurveyAnswer(6, "Bicycling");
		answerSet3a[7] = new SurveyAnswer(7, "Swimming");
		answerSet3a[8] = new SurveyAnswer(8, "Other (Baseball, skateboarding, etc.)");
		Q3_a_WhatType.setDefault("Q1_TeenActivity", answerSet3a);
		defaultQuestionSet.add(Q3_a_WhatType);
		
		SurveyQuestion Q3_b_SpendTime = new SurveyQuestion("Q3_b_SpendTime", 
				"Approximately how many MINUTES did you spend participating in this sport or exercise activity?", 
				TYPE.MINUTES_PICKER);
		SurveyAnswer[] answerSet3b = new SurveyAnswer[1];
		Q3_b_SpendTime.setDefault("Q1_TeenActivity", answerSet3b);
		defaultQuestionSet.add(Q3_b_SpendTime);
		
		SurveyQuestion Q3_c_SportInvolve = new SurveyQuestion("Q3_c_SportInvolve", 
				"Did the sport or exercise activity involve: Please choose all that apply.", 
				TYPE.MULTI_CHOICE);
		SurveyAnswer[] answerSet3c = new SurveyAnswer[5];
		answerSet3c[0] = new SurveyAnswer(0, "Flexibility");
		answerSet3c[1] = new SurveyAnswer(1, "Strengthening");
		answerSet3c[2] = new SurveyAnswer(2, "Balance");
		answerSet3c[3] = new SurveyAnswer(3, "Endurance");
		answerSet3c[4] = new SurveyAnswer(4, "None");
		Q3_c_SportInvolve.setDefault("Q1_TeenActivity", answerSet3c);
		defaultQuestionSet.add(Q3_c_SportInvolve);
		
		SurveyQuestion Q3_d_ExtraWeight = new SurveyQuestion("Q3_d_ExtraWeight", 
				"How much extra weight were you carrying during  the sport or exercise activity?", 
				TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet3d = new SurveyAnswer[5];
		answerSet3d[0] = new SurveyAnswer(0, "None");
		answerSet3d[1] = new SurveyAnswer(1, "Less than 5 lbs");
		answerSet3d[2] = new SurveyAnswer(2, "5-10 lbs");
		answerSet3d[3] = new SurveyAnswer(3, "10-20 lbs");
		answerSet3d[4] = new SurveyAnswer(4, "More than 20lbs");
		Q3_d_ExtraWeight.setDefault("Q1_TeenActivity", answerSet3d);
		defaultQuestionSet.add(Q3_d_ExtraWeight);
		
		SurveyQuestion Q3_e_SportInvolve = new SurveyQuestion("Q3_e_SportInvolve", 
				"Did the sport or exercise activity involve:", 
				TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet3e = new SurveyAnswer[4];
		answerSet3e[0] = new SurveyAnswer(0, "Mainly going uphill");
		answerSet3e[1] = new SurveyAnswer(1, "Mainly going downhill");
		answerSet3e[2] = new SurveyAnswer(2, "Going both uphill and downhill");
		answerSet3e[3] = new SurveyAnswer(3, "Mainly staying on flat ground");		
		Q3_e_SportInvolve.setDefault("Q1_TeenActivity", answerSet3e);
		defaultQuestionSet.add(Q3_e_SportInvolve);
		
		SurveyQuestion Q3_f_PainSoreness = new SurveyQuestion("Q3_f_PainSoreness", 
				"How much PAIN/SORENESS did you feel during the sport or exercise activity?", 
				TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet3f = new SurveyAnswer[5];
		answerSet3f[0] = new SurveyAnswer(0, "None");
		answerSet3f[1] = new SurveyAnswer(1, "A little");
		answerSet3f[2] = new SurveyAnswer(2, "Some");
		answerSet3f[3] = new SurveyAnswer(3, "Quite a bit");	
		answerSet3f[4] = new SurveyAnswer(4, "A lot");
		Q3_f_PainSoreness.setDefault("Q1_TeenActivity", answerSet3f);
		defaultQuestionSet.add(Q3_f_PainSoreness);
		
		SurveyQuestion Q3_g_MainPurpose = new SurveyQuestion("Q3_g_MainPurpose", 
				"What was the MAIN PURPOSE of participating in the sport or exercise activity?", 
				TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet3g = new SurveyAnswer[6];
		answerSet3g[0] = new SurveyAnswer(0, "Fun/Recreation");
		answerSet3g[1] = new SurveyAnswer(1, "Personal care");
		answerSet3g[2] = new SurveyAnswer(2, "To get somewhere");
		answerSet3g[3] = new SurveyAnswer(3, "Baby sitting/Childcare");	
		answerSet3g[4] = new SurveyAnswer(4, "For work, homework, or housework");
		answerSet3g[5] = new SurveyAnswer(5, "Other");
		Q3_g_MainPurpose.setDefault("Q1_TeenActivity", answerSet3g);
		defaultQuestionSet.add(Q3_g_MainPurpose);
		
		SurveyQuestion Q3_h_Enjoyable = new SurveyQuestion("Q3_h_Enjoyable", 
				"How ENJOYABLE was participating in the sport or exercise activity?", 
				TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet3h = new SurveyAnswer[5];
		answerSet3h[0] = new SurveyAnswer(0, "Not at all");
		answerSet3h[1] = new SurveyAnswer(1, "A little");
		answerSet3h[2] = new SurveyAnswer(2, "Moderately");
		answerSet3h[3] = new SurveyAnswer(3, "Quite a bit");	
		answerSet3h[4] = new SurveyAnswer(4, "Extremely");	
		Q3_h_Enjoyable.setDefault("Q1_TeenActivity", answerSet3h);
		defaultQuestionSet.add(Q3_h_Enjoyable);
//		
//		SurveyQuestion Q5_a_PhysicalContext = new SurveyQuestion("Q5_a_PhysicalContext", 
//				"WHERE were you just before you used your inhaler?",
//				TYPE.SINGLE_CHOICE);
//		SurveyAnswer[] answerSet5a = new SurveyAnswer[8];
//		answerSet5a[0] = new SurveyAnswer(0, "Home (Indoors)");
//		answerSet5a[1] = new SurveyAnswer(1, "School");
//		answerSet5a[2] = new SurveyAnswer(2, "Outdoors");
//		answerSet5a[3] = new SurveyAnswer(3, "Restaurant");
//		answerSet5a[4] = new SurveyAnswer(4, "Store/Mall");
//		answerSet5a[5] = new SurveyAnswer(5, "Someone else\'s house (Indoors)");
//		answerSet5a[6] = new SurveyAnswer(6, "In a car");
//		answerSet5a[7] = new SurveyAnswer(7, "Other");
//		Q5_a_PhysicalContext.setDefault("Q4_a_MainActivity", answerSet5a);
//		defaultQuestionSet.add(Q5_a_PhysicalContext);
//
//		SurveyQuestion Q5_b_Outdoors = new SurveyQuestion("Q5_b_Outdoors", 
//				"Where were you OUTDOORS?",
//				TYPE.SINGLE_CHOICE);
//		SurveyAnswer[] answerSet5b = new SurveyAnswer[7];
//		answerSet5b[0] = new SurveyAnswer(0, "Home (front/back yard)");
//		answerSet5b[1] = new SurveyAnswer(1, "School");
//		answerSet5b[2] = new SurveyAnswer(2, "Park/trail");
//		answerSet5b[3] = new SurveyAnswer(3, "Sidewalk");
//		answerSet5b[4] = new SurveyAnswer(4, "Road");
//		answerSet5b[5] = new SurveyAnswer(5, "Parking lot");
//		answerSet5b[6] = new SurveyAnswer(6, "Other");
//		Q5_b_Outdoors.setDefault("Q5_a_PhysicalContext", answerSet5b);
//		defaultQuestionSet.add(Q5_b_Outdoors);
//
//		SurveyQuestion Q6_SocialContext = new SurveyQuestion("Q6_a_SocialContext", 
//				"Just before you used your inhaler, were you:\n(Choose all that apply)",
//				TYPE.MULTI_CHOICE);
//		SurveyAnswer[] answerSet6a = new SurveyAnswer[4];
//		answerSet6a[0] = new SurveyAnswer(0, "Alone");
//		answerSet6a[1] = new SurveyAnswer(1, "With your mom/dad");
//		answerSet6a[2] = new SurveyAnswer(2, "With your sister(s) or brother(s)");
//		answerSet6a[3] = new SurveyAnswer(3, "With your friend(s)");
//		Q6_SocialContext.setDefault("Q1a_HowHappy", answerSet6a);
//		defaultQuestionSet.add(Q6_SocialContext);
//
//		SurveyQuestion Q7_a_AnyStressful = new SurveyQuestion("Q7_a_AnyStressful", 
//				context+", has anything stressful happened to you?",
//				TYPE.SINGLE_CHOICE);
//		SurveyAnswer[] answerSet7a = new SurveyAnswer[3];
//		answerSet7a[0] = new SurveyAnswer(0, "No stressful things have happened");
//		answerSet7a[1] = new SurveyAnswer(1, "A few stressful things have happened");
//		answerSet7a[2] = new SurveyAnswer(2, "Many stressful things have happened");
//		Q7_a_AnyStressful.setDefault("Q1a_HowHappy", answerSet7a);
//		defaultQuestionSet.add(Q7_a_AnyStressful);
//
//		SurveyQuestion Q7_b_AnyTeased = new SurveyQuestion("Q7_b_AnyTeased", 
//				context+", has anyone teased you?",
//				TYPE.SINGLE_CHOICE);
//		SurveyAnswer[] answerSet7b = new SurveyAnswer[5];
//		answerSet7b[0] = new SurveyAnswer(0, "Yes, and caused very much stress");
//		answerSet7b[1] = new SurveyAnswer(1, "Yes, and caused some stress");
//		answerSet7b[2] = new SurveyAnswer(2, "Yes, and caused a little stress");
//		answerSet7b[3] = new SurveyAnswer(3, "Yes, but not at all stressful");
//		answerSet7b[4] = new SurveyAnswer(4, "No");
//		Q7_b_AnyTeased.setDefault("Q7_a_AnyStressful", answerSet7b);
//		defaultQuestionSet.add(Q7_b_AnyTeased);
//
//		SurveyQuestion Q7_c_HaveArgued = new SurveyQuestion("Q7_c_HaveArgued", 
//				context+", have you argued with anyone?",
//				TYPE.SINGLE_CHOICE);
//		SurveyAnswer[] answerSet7c = new SurveyAnswer[5];
//		answerSet7c[0] = new SurveyAnswer(0, "Yes, and caused very much stress");
//		answerSet7c[1] = new SurveyAnswer(1, "Yes, and caused some stress");
//		answerSet7c[2] = new SurveyAnswer(2, "Yes, and caused a little stress");
//		answerSet7c[3] = new SurveyAnswer(3, "Yes, but not at all stressful");
//		answerSet7c[4] = new SurveyAnswer(4, "No");
//		Q7_c_HaveArgued.setDefault("Q7_b_AnyTeased", answerSet7c);
//		defaultQuestionSet.add(Q7_c_HaveArgued);
//
//		SurveyQuestion Q7_d_DisagreeWithParents = new SurveyQuestion("Q7_d_DisagreeWithParents", 
//				context+", have you had a misunderstanding or disagreement with your parents?",
//				TYPE.SINGLE_CHOICE);
//		SurveyAnswer[] answerSet7d = new SurveyAnswer[5];
//		answerSet7d[0] = new SurveyAnswer(0, "Yes, and caused very much stress");
//		answerSet7d[1] = new SurveyAnswer(1, "Yes, and caused some stress");
//		answerSet7d[2] = new SurveyAnswer(2, "Yes, and caused a little stress");
//		answerSet7d[3] = new SurveyAnswer(3, "Yes, but not at all stressful");
//		answerSet7d[4] = new SurveyAnswer(4, "No");
//		Q7_d_DisagreeWithParents.setDefault("Q7_c_HaveArgued", answerSet7d);
//		defaultQuestionSet.add(Q7_d_DisagreeWithParents);
//
//		SurveyQuestion Q7_e_TooManyWork = new SurveyQuestion("Q7_e_TooManyWork", 
//				context+", have you had too many things to do?",
//				TYPE.SINGLE_CHOICE);
//		SurveyAnswer[] answerSet7e = new SurveyAnswer[5];
//		answerSet7e[0] = new SurveyAnswer(0, "Yes, and caused very much stress");
//		answerSet7e[1] = new SurveyAnswer(1, "Yes, and caused some stress");
//		answerSet7e[2] = new SurveyAnswer(2, "Yes, and caused a little stress");
//		answerSet7e[3] = new SurveyAnswer(3, "Yes, but not at all stressful");
//		answerSet7e[4] = new SurveyAnswer(4, "No");
//		Q7_e_TooManyWork.setDefault("Q7_d_DisagreeWithParents", answerSet7e);
//		defaultQuestionSet.add(Q7_e_TooManyWork);
//		/*************** Set up rules *******************/
//		for (int i = 0; i < defaultQuestionSet.size(); i++) {
//			defaultQuestionSet.get(i).addRules(new ChanceBeChosen(1));
//		}
//		//set up branching questions and rules
//		ArrayList<SurveyQuestion> questionSection_1 = new ArrayList<SurveyQuestion>();
//		questionSection_1.add(Q2_a_NeitherInhaler);
//		Q1_InhalerUse.addRules(new QuesFromAns(new int[]{2}, questionSection_1));
//
//		ArrayList<SurveyQuestion> questionSection_2 = new ArrayList<SurveyQuestion>();
//		questionSection_2.add(Q2_b_OtherInhaler);
//		Q2_a_NeitherInhaler.addRules(new QuesFromAns(new int[]{3}, questionSection_2));
//
//		ArrayList<SurveyQuestion> questionSection_3 = new ArrayList<SurveyQuestion>();
//		questionSection_3.add(Q3_a_AnyCoughing);
//		questionSection_3.add(Q3_b_AnyWheezing);
//		questionSection_3.add(Q3_c_AnyChestTightness);
//		questionSection_3.add(Q3_d_AnyShortnessBreath);
//		questionSection_3.add(Q4_a_MainActivity);
//		questionSection_3.add(Q5_a_PhysicalContext);
//		questionSection_3.add(Q6_SocialContext);
//		questionSection_3.add(Q7_a_AnyStressful);
//		questionSection_3.add(Q7_b_AnyTeased);
//		questionSection_3.add(Q7_c_HaveArgued);
//		questionSection_3.add(Q7_d_DisagreeWithParents);
//		questionSection_3.add(Q7_e_TooManyWork);
//		Q1_InhalerUse.addRules(new QuesFromAns(new int[]{0,1}, questionSection_3));
//
//		ArrayList<SurveyQuestion> questionSection_4 = new ArrayList<SurveyQuestion>();
//		questionSection_4.add(Q4_b_SomethingElse);
//		Q4_a_MainActivity.addRules(new QuesFromAns(new int[]{6}, questionSection_4));
//
//		ArrayList<SurveyQuestion> questionSection_4b = new ArrayList<SurveyQuestion>();
//		questionSection_4b.add(Q4_c_UsingTech);
//		Q4_a_MainActivity.addRules(new QuesFromAns(new int[]{1}, questionSection_4b));
//
//		ArrayList<SurveyQuestion> questionSection_4c = new ArrayList<SurveyQuestion>();
//		questionSection_4c.add(Q4_d_GoingSomewhere);
//		Q4_a_MainActivity.addRules(new QuesFromAns(new int[]{4}, questionSection_4c));
//
//		ArrayList<SurveyQuestion> questionSection_5 = new ArrayList<SurveyQuestion>();
//		questionSection_5.add(Q5_b_Outdoors);
//		Q5_a_PhysicalContext.addRules(new QuesFromAns(new int[]{2}, questionSection_5));

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
