package edu.neu.android.mhealth.uscteensver1.survey;

import java.util.ArrayList;

import edu.neu.android.wocketslib.emasurvey.model.QuestionSet;
import edu.neu.android.wocketslib.emasurvey.model.QuestionSetParamHandler;
import edu.neu.android.wocketslib.emasurvey.model.SurveyAnswer;
import edu.neu.android.wocketslib.emasurvey.model.SurveyQuestion;
import edu.neu.android.wocketslib.emasurvey.model.SurveyQuestion.TYPE;
import edu.neu.android.wocketslib.emasurvey.rule.ChanceBeChosen;
import edu.neu.android.wocketslib.emasurvey.rule.QuesAsSequence;
import edu.neu.android.wocketslib.emasurvey.rule.QuesFromAns;

public class EMAQuestionSet extends QuestionSet {

	public static final double VERSION = 1;
	private ArrayList<SurveyQuestion> defaultQuestionSet;
	private String context = null;
	public static final String optionalContext = "for the past hour";
	public static final String defaultContext = "since the last survey you answered";
	public static final int EMA_DEFAULT = 0;
	public static final int EMA_OPTIONAL = 1;

	public EMAQuestionSet(QuestionSetParamHandler param){
		super();
		if(param.getParamNum() != 1)
			return;
		int type = (Integer)param.getParams()[0];
		
		switch(type){
		case EMA_DEFAULT:
			context = defaultContext;
			break;
		case EMA_OPTIONAL:
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
	protected void setQuestions(){
		defaultQuestionSet = new ArrayList<SurveyQuestion>();
		
		/************ Initialize questions and answers *********/
		SurveyQuestion Q1_MainActivity = new SurveyQuestion("Q1_MainActivity","What have you been DOING "+context+"?\n(Choose all that apply)",TYPE.MULTI_CHOICE);
		SurveyAnswer[] answerSet1 = new SurveyAnswer[6];
		answerSet1[0] = new SurveyAnswer(0, "Reading or doing homework");
		answerSet1[1] = new SurveyAnswer(1, "Using technology (TV, phone)");
		answerSet1[2] = new SurveyAnswer(2, "Eating/Drinking");
		answerSet1[3] = new SurveyAnswer(3, "Sports/Exercising");
		answerSet1[4] = new SurveyAnswer(4, "Going somewhere");
		answerSet1[5] = new SurveyAnswer(5, "Other");		
		Q1_MainActivity.setDefault(SurveyQuestion.NO_DATA, answerSet1);
		defaultQuestionSet.add(Q1_MainActivity);

		SurveyQuestion Q1_a_UsingTech = new SurveyQuestion("Q1_a_UsingTech","While using technology (TV, phone), were you:",TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet1a = new SurveyAnswer[6];
		answerSet1a[0] = new SurveyAnswer(0, "Playing video games");
		answerSet1a[1] = new SurveyAnswer(1, "Talking");
		answerSet1a[2] = new SurveyAnswer(2, "Texting");
		answerSet1a[3] = new SurveyAnswer(3, "Using the Internet");
		answerSet1a[4] = new SurveyAnswer(4, "Watching shows/movies");
		answerSet1a[5] = new SurveyAnswer(5, "Other");		
		Q1_a_UsingTech.setDefault("Q1_MainActivity", answerSet1a);
		defaultQuestionSet.add(Q1_a_UsingTech);

		SurveyQuestion Q2_a_HowLong = new SurveyQuestion("Q2_a_HowLong","Approximately how many minutes did you spend "+mainActivity+"?", TYPE.MINUTES_PICKER);
		SurveyAnswer[] answerSet2a = new SurveyAnswer[1];
		Q2_a_HowLong.setDefault("Q1_MainActivity", answerSet2a);
		defaultQuestionSet.add(Q2_a_HowLong);

		SurveyQuestion Q2_b_a_WereYou = new SurveyQuestion("Q2_b_a_WereYou","While "+mainActivity+", were you:",TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet2ba = new SurveyAnswer[5];
		answerSet2ba[0] = new SurveyAnswer(0, "Lying down");
		answerSet2ba[1] = new SurveyAnswer(1, "Sitting");
		answerSet2ba[2] = new SurveyAnswer(2, "Standing");
		answerSet2ba[3] = new SurveyAnswer(3, "Walking");
		answerSet2ba[4] = new SurveyAnswer(4, "Jogging/Running");
		Q2_b_a_WereYou.setDefault("Q1_MainActivity", answerSet2ba);
		defaultQuestionSet.add(Q2_b_a_WereYou);

		SurveyQuestion Q2_b_b_WereYou = new SurveyQuestion("Q2_b_b_WereYou","While going somewhere, were you:",TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet2bb = new SurveyAnswer[6];
		answerSet2bb[0] = new SurveyAnswer(0, "Walking");
		answerSet2bb[1] = new SurveyAnswer(1, "Biking");
		answerSet2bb[2] = new SurveyAnswer(2, "Riding a bus");
		answerSet2bb[3] = new SurveyAnswer(3, "Riding the Metro/train");
		answerSet2bb[4] = new SurveyAnswer(4, "Riding in a car/taxi");
		answerSet2bb[5] = new SurveyAnswer(5, "Other (skateboarding, etc.)");
		Q2_b_b_WereYou.setDefault("Q1_MainActivity", answerSet2bb);
		defaultQuestionSet.add(Q2_b_b_WereYou);
		
		SurveyQuestion Q2_c_WherePhone = new SurveyQuestion("Q2_c_WherePhone","How did you have the PHONE while "+mainActivity+"?",TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet2c = new SurveyAnswer[5];
		answerSet2c[0] = new SurveyAnswer(0, "On my belt");
		answerSet2c[1] = new SurveyAnswer(1, "In my pocket");
		answerSet2c[2] = new SurveyAnswer(2, "In my handbag/purse/backpack");
		answerSet2c[3] = new SurveyAnswer(3, "Holding in my hand");
		answerSet2c[4] = new SurveyAnswer(4, "Not with me");
		Q2_c_WherePhone.setDefault("Q1_MainActivity", answerSet2c);
		defaultQuestionSet.add(Q2_c_WherePhone);

		SurveyQuestion Q2_d_YNotCarrying = new SurveyQuestion("Q2_d_YNotCarrying","Please indicate your reason for not carrying your phone while "+mainActivity+"?",TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet2d = new SurveyAnswer[7];
		answerSet2d[0] = new SurveyAnswer(0, "Forgot it");
		answerSet2d[1] = new SurveyAnswer(1, "Battery died");
		answerSet2d[2] = new SurveyAnswer(2, "Could not get it wet");
		answerSet2d[3] = new SurveyAnswer(3, "Too bulky");
		answerSet2d[4] = new SurveyAnswer(4, "Too Uncomfortable");
		answerSet2d[5] = new SurveyAnswer(5, "Embarrassed to carry it");
		answerSet2d[6] = new SurveyAnswer(6, "Not allowed to carry  it");
		Q2_d_YNotCarrying.setDefault("Q2_c_WherePhone", answerSet2d);
		defaultQuestionSet.add(Q2_d_YNotCarrying);

		SurveyQuestion Q2_e_a_MainPurpose = new SurveyQuestion("Q2_e_a_MainPurpose", "What was the MAIN PURPOSE of "+mainActivity, TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet2ea = new SurveyAnswer[6];
		answerSet2ea[0] = new SurveyAnswer(0, "Fun/Recreation");
		answerSet2ea[1] = new SurveyAnswer(1, "Personal care");
		answerSet2ea[2] = new SurveyAnswer(2, "To get somewhere");
		answerSet2ea[3] = new SurveyAnswer(3, "Baby sitting/Childcare");
		answerSet2ea[4] = new SurveyAnswer(4, "For work, homework, or housework");
		answerSet2ea[5] = new SurveyAnswer(5, "Other");
		Q2_e_a_MainPurpose.setDefault("Q1_MainActivity", answerSet2ea);
		defaultQuestionSet.add(Q2_e_a_MainPurpose);

		SurveyQuestion Q2_e_b_MainPurpose = new SurveyQuestion("Q2_e_b_MainPurpose", "Was this eating or drinking a meal or a snack?", TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet2eb = new SurveyAnswer[2];
		answerSet2eb[0] = new SurveyAnswer(0, "Meal");
		answerSet2eb[1] = new SurveyAnswer(1, "Snack");
		Q2_e_b_MainPurpose.setDefault("Q1_MainActivity", answerSet2eb);
		defaultQuestionSet.add(Q2_e_b_MainPurpose);

		SurveyQuestion Q2_f_HowEnjoyable = new SurveyQuestion("Q2_f_HowEnjoyable", "How ENJOYABLE was "+mainActivity, TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet2f = new SurveyAnswer[5];
		answerSet2f[0] = new SurveyAnswer(0, "Not at all");
		answerSet2f[1] = new SurveyAnswer(1, "A little");
		answerSet2f[2] = new SurveyAnswer(2, "Moderately");
		answerSet2f[3] = new SurveyAnswer(3, "Quite a bit");
		answerSet2f[4] = new SurveyAnswer(4, "Extremely");
		Q2_f_HowEnjoyable.setDefault("Q1_MainActivity", answerSet2f);
		defaultQuestionSet.add(Q2_f_HowEnjoyable);

		SurveyQuestion Q2_g_SelfMotivated = new SurveyQuestion("Q2_g_SelfMotivated", "Were you "+mainActivity+" because YOU want to do it?", TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet2g = new SurveyAnswer[2];
		answerSet2g[0] = new SurveyAnswer(0, "Yes");
		answerSet2g[1] = new SurveyAnswer(1, "No");
		Q2_g_SelfMotivated.setDefault("Q1_MainActivity",  answerSet2g);
		defaultQuestionSet.add(Q2_g_SelfMotivated);

		SurveyQuestion Q2_h_ParentsMotivated = new SurveyQuestion("Q2_h_ParentsMotivated", "Were you "+mainActivity+" because YOUR PARENTS want you do do it?", TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet2h = new SurveyAnswer[2];
		answerSet2h[0] = new SurveyAnswer(0, "Yes");
		answerSet2h[1] = new SurveyAnswer(1, "No");
		Q2_h_ParentsMotivated.setDefault("Q2_g_SelfMotivated", answerSet2h);
		defaultQuestionSet.add(Q2_h_ParentsMotivated);

		SurveyQuestion Q2_i_FriendsMotivated = new SurveyQuestion("Q2_i_FriendsMotivated", "Were you "+mainActivity+" because YOUR FRIENDS want you to do it?", TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet2i = new SurveyAnswer[2];
		answerSet2i[0] = new SurveyAnswer(0, "Yes");
		answerSet2i[1] = new SurveyAnswer(1, "No");
		Q2_i_FriendsMotivated.setDefault("Q2_g_SelfMotivated",  answerSet2i);
		defaultQuestionSet.add(Q2_i_FriendsMotivated);

		SurveyQuestion Q2_j_TeacherMotivated = new SurveyQuestion("Q2_j_TeacherMotivated", "Were you "+mainActivity+" because YOUR TEACHERS want you to do it?", TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet2j = new SurveyAnswer[2];
		answerSet2j[0] = new SurveyAnswer(0, "Yes");
		answerSet2j[1] = new SurveyAnswer(1, "No");
		Q2_j_TeacherMotivated.setDefault("Q2_g_SelfMotivated",answerSet2j);
		defaultQuestionSet.add(Q2_j_TeacherMotivated);

		SurveyQuestion Q2_k_Alone = new SurveyQuestion("Q2_k_Alone", "Were you "+mainActivity+" ALONE?", TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet2k = new SurveyAnswer[2];
		answerSet2k[0] = new SurveyAnswer(0, "Yes");
		answerSet2k[1] = new SurveyAnswer(1, "No");
		Q2_k_Alone.setDefault("Q1_MainActivity",  answerSet2k);
		defaultQuestionSet.add(Q2_k_Alone);

		SurveyQuestion Q2_l_Accompanies = new SurveyQuestion("Q2_l_Accompanies", "While "+mainActivity+", were you with:\n(Choose all that apply)", TYPE.MULTI_CHOICE);
		SurveyAnswer[] answerSet2l = new SurveyAnswer[5];
		answerSet2l[0] = new SurveyAnswer(0, "Friends");
		answerSet2l[1] = new SurveyAnswer(1, "Parents");
		answerSet2l[2] = new SurveyAnswer(2, "Siblings");
		answerSet2l[3] = new SurveyAnswer(3, "Teammates/Classmates");
		answerSet2l[4] = new SurveyAnswer(4, "People You Don\'t Know");
		Q2_l_Accompanies.setDefault("Q2_k_Alone", answerSet2l);
		defaultQuestionSet.add(Q2_l_Accompanies);

		SurveyQuestion Q3_a_Type = new SurveyQuestion("Q3_a_Type","What type of sports or exercise activity?",TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet3a = new SurveyAnswer[8];
		answerSet3a[0] = new SurveyAnswer(0, "Basketball/Football/Soccer");
		answerSet3a[1] = new SurveyAnswer(1, "Other running/Jogging");
		answerSet3a[2] = new SurveyAnswer(2, "Exercise/Dance/Karate class");
		answerSet3a[3] = new SurveyAnswer(3, "Weightlifting/Strength training");
		answerSet3a[4] = new SurveyAnswer(4, "Walking");
		answerSet3a[5] = new SurveyAnswer(5, "Bicycling");
		answerSet3a[6] = new SurveyAnswer(6, "Swimming");
		answerSet3a[7] = new SurveyAnswer(7, "Other (Baseball, skateboarding, etc.)");
		Q3_a_Type.setDefault("Q1_MainActivity", answerSet3a);
		defaultQuestionSet.add(Q3_a_Type);

		SurveyQuestion Q3_b_HowManyMins = new SurveyQuestion("Q3_b_HowManyMins","Approximately how many MINUTES did you spend participating in this sport or exercise activity?",TYPE.MINUTES_PICKER);
		SurveyAnswer[] answerSet3b = new SurveyAnswer[1];
		Q3_b_HowManyMins.setDefault("Q1_MainActivity", answerSet3b);
		defaultQuestionSet.add(Q3_b_HowManyMins);

		SurveyQuestion Q3_c_WhatInvolve = new SurveyQuestion("Q3_c_WhatInvolve","Did the sport or exercise activity involve:\n(Choose all that apply)",TYPE.MULTI_CHOICE);
		SurveyAnswer[] answerSet3c = new SurveyAnswer[5];
		answerSet3c[0] = new SurveyAnswer(0, "Flexibility");
		answerSet3c[1] = new SurveyAnswer(1, "Strengthening");
		answerSet3c[2] = new SurveyAnswer(2, "Balance");
		answerSet3c[3] = new SurveyAnswer(3, "Endurance");
		answerSet3c[4] = new SurveyAnswer(4, "None");
		Q3_c_WhatInvolve.setDefault("Q1_MainActivity", answerSet3c);
		defaultQuestionSet.add(Q3_c_WhatInvolve);

		SurveyQuestion Q3_d_ExtraWeight = new SurveyQuestion("Q3_d_ExtraWeight","How much extra weight were you carrying during the sport or exercise activity?",TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet3d = new SurveyAnswer[5];
		answerSet3d[0] = new SurveyAnswer(0, "None");
		answerSet3d[1] = new SurveyAnswer(1, "Less than 5 lbs");
		answerSet3d[2] = new SurveyAnswer(2, "5-10 lbs");
		answerSet3d[3] = new SurveyAnswer(3, "10-20 lbs");
		answerSet3d[4] = new SurveyAnswer(4, "More than 20lbs");
		Q3_d_ExtraWeight.setDefault("Q1_MainActivity", answerSet3d);
		defaultQuestionSet.add(Q3_d_ExtraWeight);

		SurveyQuestion Q3_e_UpOrDown = new SurveyQuestion("Q3_e_UpOrDown","Did the sport or exercise activity involve:",TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet3e = new SurveyAnswer[4];
		answerSet3e[0] = new SurveyAnswer(0, "Mainly going uphill");
		answerSet3e[1] = new SurveyAnswer(1, "Mainly going downhill");
		answerSet3e[2] = new SurveyAnswer(2, "Going both uphill and downhill");
		answerSet3e[3] = new SurveyAnswer(3, "Mainly staying on flat ground");
		Q3_e_UpOrDown.setDefault("Q1_MainActivity", answerSet3e);
		defaultQuestionSet.add(Q3_e_UpOrDown);

		SurveyQuestion Q3_f_HowSoreness = new SurveyQuestion("Q3_f_HowSoreness","How much PAIN/SORENESS did you feel during the sport or exercise activity?",TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet3f = new SurveyAnswer[5];
		answerSet3f[0] = new SurveyAnswer(0, "None");
		answerSet3f[1] = new SurveyAnswer(1, "A little");
		answerSet3f[2] = new SurveyAnswer(2, "Some");
		answerSet3f[3] = new SurveyAnswer(3, "Quite a bit");
		answerSet3f[4] = new SurveyAnswer(4, "A lot");
		Q3_f_HowSoreness.setDefault("Q1_MainActivity", answerSet3f);
		defaultQuestionSet.add(Q3_f_HowSoreness);

		SurveyQuestion Q3_g_MainPurpose = new SurveyQuestion("Q3_g_MainPurpose","What was the MAIN PURPOSE of participating in the sport or exercise activity?",TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet3g = new SurveyAnswer[6];
		answerSet3g[0] = new SurveyAnswer(0, "Fun/Recreation");
		answerSet3g[1] = new SurveyAnswer(1, "Personal care");
		answerSet3g[2] = new SurveyAnswer(2, "To get somewhere");
		answerSet3g[3] = new SurveyAnswer(3, "Baby sitting/Childcare");
		answerSet3g[4] = new SurveyAnswer(4, "For work, homework, or housework");
		answerSet3g[5] = new SurveyAnswer(5, "Other");
		Q3_g_MainPurpose.setDefault("Q1_MainActivity", answerSet3g);
		defaultQuestionSet.add(Q3_g_MainPurpose);

		SurveyQuestion Q3_h_HowEnjoyable = new SurveyQuestion("Q3_h_HowEnjoyable","How ENJOYABLE was participating in the sport or exercise activity?",TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet3h = new SurveyAnswer[5];
		answerSet3h[0] = new SurveyAnswer(0, "Not at all");
		answerSet3h[1] = new SurveyAnswer(1, "A little");
		answerSet3h[2] = new SurveyAnswer(2, "Moderately");
		answerSet3h[3] = new SurveyAnswer(3, "Quite a bit");
		answerSet3h[4] = new SurveyAnswer(4, "Extremely");
		Q3_h_HowEnjoyable.setDefault("Q1_MainActivity", answerSet3h);
		defaultQuestionSet.add(Q3_h_HowEnjoyable);

		SurveyQuestion Q3_i_SelfMotivated = new SurveyQuestion("Q3_i_SelfMotivated","Did you participate in the sport or exercise activity because YOU want to do it?",TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet3i = new SurveyAnswer[2];
		answerSet3i[0] = new SurveyAnswer(0, "Yes");
		answerSet3i[1] = new SurveyAnswer(1, "No");
		Q3_i_SelfMotivated.setDefault("Q1_MainActivity", answerSet3i);
		defaultQuestionSet.add(Q3_i_SelfMotivated);

		SurveyQuestion Q3_j_ParentsMotivated = new SurveyQuestion("Q3_j_ParentsMotivated","Did you participate in the sport or exercise activity because YOUR PARENTS want you to do it?",TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet3j = new SurveyAnswer[2];
		answerSet3j[0] = new SurveyAnswer(0, "Yes");
		answerSet3j[1] = new SurveyAnswer(1, "No");
		Q3_j_ParentsMotivated.setDefault("Q3_i_SelfMotivated", answerSet3j);
		defaultQuestionSet.add(Q3_j_ParentsMotivated);

		SurveyQuestion Q3_k_FriendsMotivated = new SurveyQuestion("Q3_k_FriendsMotivated","Did participate in the sport or exercise activity because YOUR FRIENDS want you to do it?",TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet3k = new SurveyAnswer[2];
		answerSet3k[0] = new SurveyAnswer(0, "Yes");
		answerSet3k[1] = new SurveyAnswer(1, "No");
		Q3_k_FriendsMotivated.setDefault("Q3_i_SelfMotivated", answerSet3k);
		defaultQuestionSet.add(Q3_k_FriendsMotivated);

		SurveyQuestion Q3_l_TeacherMotivated = new SurveyQuestion("Q3_l_TeacherMotivated","Did participate in the sport or exercise activity because YOUR TEACHERS want you to do it?",TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet3l = new SurveyAnswer[2];
		answerSet3l[0] = new SurveyAnswer(0, "Yes");
		answerSet3l[1] = new SurveyAnswer(1, "No");
		Q3_l_TeacherMotivated.setDefault("Q3_i_SelfMotivated", answerSet3l);
		defaultQuestionSet.add(Q3_l_TeacherMotivated);

		SurveyQuestion Q3_m_Where = new SurveyQuestion("Q3_m_Where","Where did you participate in the sport or exercise activity?",TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet3m = new SurveyAnswer[5];
		answerSet3m[0] = new SurveyAnswer(0, "Home");
		answerSet3m[1] = new SurveyAnswer(1, "Work");
		answerSet3m[2] = new SurveyAnswer(2, "School");
		answerSet3m[3] = new SurveyAnswer(3, "Car/Bus/Metro/Train");
		answerSet3m[4] = new SurveyAnswer(4, "Other");
		Q3_m_Where.setDefault("Q1_MainActivity",  answerSet3m);
		defaultQuestionSet.add(Q3_m_Where);

		SurveyQuestion Q3_n_WhereOther = new SurveyQuestion("Q3_n_WhereOther","WHERE was this OTHER place?",TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet3n = new SurveyAnswer[6];
		answerSet3n[0] = new SurveyAnswer(0, "Restaurant");
		answerSet3n[1] = new SurveyAnswer(1, "Store/Mall");
		answerSet3n[2] = new SurveyAnswer(2, "Someone else�s house");
		answerSet3n[3] = new SurveyAnswer(3, "Gym/Health Club");
		answerSet3n[4] = new SurveyAnswer(4, "Park/Trail");
		answerSet3n[5] = new SurveyAnswer(5, "Someplace else");
		Q3_n_WhereOther.setDefault("Q3_m_Where", answerSet3n);
		defaultQuestionSet.add(Q3_n_WhereOther);

		SurveyQuestion Q3_o_Outdoors = new SurveyQuestion("Q3_o_Outdoors","Did you participate in the sport or exercise activity OUTDOORS?",TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet3o = new SurveyAnswer[2];
		answerSet3o[0] = new SurveyAnswer(0, "Yes");
		answerSet3o[1] = new SurveyAnswer(1, "No");
		Q3_o_Outdoors.setDefault("Q1_MainActivity", answerSet3o);
		defaultQuestionSet.add(Q3_o_Outdoors);

		SurveyQuestion Q3_p_Alone = new SurveyQuestion("Q3_p_Alone","Did you participate in the sport or exercise activity ALONE?",TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet3p = new SurveyAnswer[2];
		answerSet3p[0] = new SurveyAnswer(0, "Yes");
		answerSet3p[1] = new SurveyAnswer(1, "No");
		Q3_p_Alone.setDefault("Q1_MainActivity", answerSet3p);
		defaultQuestionSet.add(Q3_p_Alone);

		SurveyQuestion Q3_q_Accompanies = new SurveyQuestion("Q3_q_Accompanies","While participating in the sport or exercise activity, were you with:\n(Choose all that apply)",TYPE.MULTI_CHOICE);
		SurveyAnswer[] answerSet3q = new SurveyAnswer[5];
		answerSet3q[0] = new SurveyAnswer(0, "Friends");
		answerSet3q[1] = new SurveyAnswer(1, "Parents");
		answerSet3q[2] = new SurveyAnswer(2, "Siblings");
		answerSet3q[3] = new SurveyAnswer(3, "Teammates/Classmates");
		answerSet3q[4] = new SurveyAnswer(4, "People You Don\'t Know");
		Q3_q_Accompanies.setDefault("Q3_p_Alone", answerSet3q);
		defaultQuestionSet.add(Q3_q_Accompanies);

		SurveyQuestion Q3_r_WherePhone = new SurveyQuestion("Q3_r_WherePhone","How did you have the PHONE while participating in the sport or exercise activity?",TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet3r = new SurveyAnswer[5];
		answerSet3r[0] = new SurveyAnswer(0, "On my belt");
		answerSet3r[1] = new SurveyAnswer(1, "In my pocket");
		answerSet3r[2] = new SurveyAnswer(2, "In my handbag/purse/backpack");
		answerSet3r[3] = new SurveyAnswer(3, "Holding in my hand");
		answerSet3r[4] = new SurveyAnswer(4, "Not with me");
		Q3_r_WherePhone.setDefault("Q1_MainActivity", answerSet3r);
		defaultQuestionSet.add(Q3_r_WherePhone);

		SurveyQuestion Q3_s_YNotCarrying = new SurveyQuestion("Q3_s_YNotCarrying","Please indicate your reason for not carrying your phone while participating in the sport or exercise activity:",TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet3s = new SurveyAnswer[7];
		answerSet3s[0] = new SurveyAnswer(0, "Forgot it");
		answerSet3s[1] = new SurveyAnswer(1, "Battery died");
		answerSet3s[2] = new SurveyAnswer(2, "Could not get it wet");
		answerSet3s[3] = new SurveyAnswer(3, "Too bulky");
		answerSet3s[4] = new SurveyAnswer(4, "Too Uncomfortable");
		answerSet3s[5] = new SurveyAnswer(5, "Embarrassed to carry it");
		answerSet3s[6] = new SurveyAnswer(6, "Not allowed to carry  it");
		Q3_s_YNotCarrying.setDefault("Q3_r_WherePhone", answerSet3s);
		defaultQuestionSet.add(Q3_s_YNotCarrying);

		SurveyQuestion Q4_a_WhatOther = new SurveyQuestion("Q4_a_WhatOther","What was this other activity?",TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet4a = new SurveyAnswer[8];
		answerSet4a[0] = new SurveyAnswer(0, "Hanging with friends");
		answerSet4a[1] = new SurveyAnswer(1, "Doing chores/cooking");
		answerSet4a[2] = new SurveyAnswer(2, "Riding in a car");
		answerSet4a[3] = new SurveyAnswer(3, "Playing video games");
		answerSet4a[4] = new SurveyAnswer(4, "Using the phone (talking/texting/Internet)");
		answerSet4a[5] = new SurveyAnswer(5, "Showering/Bathing");
		answerSet4a[6] = new SurveyAnswer(6, "Sleeping");
		answerSet4a[7] = new SurveyAnswer(7, "Doing something else");
		Q4_a_WhatOther.setDefault("Q1_MainActivity", answerSet4a);
		defaultQuestionSet.add(Q4_a_WhatOther);

		SurveyQuestion Q4_b_HowManyMins = new SurveyQuestion("Q4_b_HowManyMins","Approximately how many minutes did you spend "+mainActivity+"?",TYPE.MINUTES_PICKER);
		SurveyAnswer[] answerSet4b = new SurveyAnswer[1];
		Q4_b_HowManyMins.setDefault("Q1_MainActivity", answerSet4b);
		defaultQuestionSet.add(Q4_b_HowManyMins);

		SurveyQuestion Q4_c_WereYou = new SurveyQuestion("Q4_c_WereYou","When "+mainActivity+", were you:",TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet4c = new SurveyAnswer[5];
		answerSet4c[0] = new SurveyAnswer(0, "Lying down");
		answerSet4c[1] = new SurveyAnswer(1, "Sitting");
		answerSet4c[2] = new SurveyAnswer(2, "Standing");
		answerSet4c[3] = new SurveyAnswer(3, "Walking");
		answerSet4c[4] = new SurveyAnswer(4, "Jogging/Running");
		Q4_c_WereYou.setDefault("Q4_a_WhatOther", answerSet4c);
		defaultQuestionSet.add(Q4_c_WereYou);

		SurveyQuestion Q4_d_WherePhone = new SurveyQuestion("Q4_d_WherePhone","How did you have the PHONE while "+mainActivity+"?",TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet4d = new SurveyAnswer[5];
		answerSet4d[0] = new SurveyAnswer(0, "On my belt");
		answerSet4d[1] = new SurveyAnswer(1, "In my pocket");
		answerSet4d[2] = new SurveyAnswer(2, "In my handbag/purse/backpack");
		answerSet4d[3] = new SurveyAnswer(3, "Holding in my hand");
		answerSet4d[4] = new SurveyAnswer(4, "Not with me");
		Q4_d_WherePhone.setDefault("Q1_MainActivity", answerSet4d);
		defaultQuestionSet.add(Q4_d_WherePhone);

		SurveyQuestion Q4_e_YNotCarrying = new SurveyQuestion("Q4_e_YNotCarrying","Please indicate your reason for not carrying your phone while "+mainActivity+":",TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet4e = new SurveyAnswer[7];
		answerSet4e[0] = new SurveyAnswer(0, "Forgot it");
		answerSet4e[1] = new SurveyAnswer(1, "Battery died");
		answerSet4e[2] = new SurveyAnswer(2, "Could not get it wet");
		answerSet4e[3] = new SurveyAnswer(3, "Too bulky");
		answerSet4e[4] = new SurveyAnswer(4, "Too Uncomfortable");
		answerSet4e[5] = new SurveyAnswer(5, "Embarrassed to carry it");
		answerSet4e[6] = new SurveyAnswer(6, "Not allowed to carry  it");
		Q4_e_YNotCarrying.setDefault("Q4_d_WherePhone", answerSet4e);
		defaultQuestionSet.add(Q4_e_YNotCarrying);

		SurveyQuestion Q4_f_MainPurpose = new SurveyQuestion("Q4_f_MainPurpose", "What was the MAIN PURPOSE of "+mainActivity, TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet4f = new SurveyAnswer[6];
		answerSet4f[0] = new SurveyAnswer(0, "Fun/Recreation");
		answerSet4f[1] = new SurveyAnswer(1, "Personal care");
		answerSet4f[2] = new SurveyAnswer(2, "To get somewhere");
		answerSet4f[3] = new SurveyAnswer(3, "Baby sitting/Childcare");
		answerSet4f[4] = new SurveyAnswer(4, "For work, homework, or housework");
		answerSet4f[5] = new SurveyAnswer(5, "Other");
		Q4_f_MainPurpose.setDefault("Q4_a_WhatOther", answerSet4f);
		defaultQuestionSet.add(Q4_f_MainPurpose);

		SurveyQuestion Q4_g_HowEnjoyable = new SurveyQuestion("Q4_g_HowEnjoyable", "How ENJOYABLE was "+mainActivity, TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet4g = new SurveyAnswer[5];
		answerSet4g[0] = new SurveyAnswer(0, "Not at all");
		answerSet4g[1] = new SurveyAnswer(1, "A little");
		answerSet4g[2] = new SurveyAnswer(2, "Moderately");
		answerSet4g[3] = new SurveyAnswer(3, "Quite a bit");
		answerSet4g[4] = new SurveyAnswer(4, "Extremely");
		Q4_g_HowEnjoyable.setDefault("Q4_a_WhatOther", answerSet4g);
		defaultQuestionSet.add(Q4_g_HowEnjoyable);

		SurveyQuestion Q4_h_SelfMotivated = new SurveyQuestion("Q4_h_SelfMotivated", "Were you "+mainActivity+" because YOU want to do it?", TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet4h = new SurveyAnswer[2];
		answerSet4h[0] = new SurveyAnswer(0, "Yes");
		answerSet4h[1] = new SurveyAnswer(1, "No");
		Q4_h_SelfMotivated.setDefault("Q4_a_WhatOther",answerSet4h);
		defaultQuestionSet.add(Q4_h_SelfMotivated);

		SurveyQuestion Q4_i_ParentsMotivated = new SurveyQuestion("Q4_i_ParentsMotivated", "Were you "+mainActivity+" because YOUR PARENTS want you do do it?", TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet4i = new SurveyAnswer[2];
		answerSet4i[0] = new SurveyAnswer(0, "Yes");
		answerSet4i[1] = new SurveyAnswer(1, "No");
		Q4_i_ParentsMotivated.setDefault("Q4_h_SelfMotivated", answerSet4i);
		defaultQuestionSet.add(Q4_i_ParentsMotivated);

		SurveyQuestion Q4_j_FriendsMotivated = new SurveyQuestion("Q4_j_FriendsMotivated", "Were you "+mainActivity+" because YOUR FRIENDS want you to do it?", TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet4j = new SurveyAnswer[2];
		answerSet4j[0] = new SurveyAnswer(0, "Yes");
		answerSet4j[1] = new SurveyAnswer(1, "No");
		Q4_j_FriendsMotivated.setDefault("Q4_h_SelfMotivated", answerSet4j);
		defaultQuestionSet.add(Q4_j_FriendsMotivated);

		SurveyQuestion Q4_k_TeacherMotivated = new SurveyQuestion("Q4_k_TeacherMotivated", "Were you "+mainActivity+" because YOUR TEACHERS want you to do it?", TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet4k = new SurveyAnswer[2];
		answerSet4k[0] = new SurveyAnswer(0, "Yes");
		answerSet4k[1] = new SurveyAnswer(1, "No");
		Q4_k_TeacherMotivated.setDefault("Q4_h_SelfMotivated", answerSet4k);
		defaultQuestionSet.add(Q4_k_TeacherMotivated);

		SurveyQuestion Q4_l_Alone = new SurveyQuestion("Q4_l_Alone", "Were you "+mainActivity+" ALONE?", TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet4l = new SurveyAnswer[2];
		answerSet4l[0] = new SurveyAnswer(0, "Yes");
		answerSet4l[1] = new SurveyAnswer(1, "No");
		Q4_l_Alone.setDefault("Q4_a_WhatOther", answerSet4l);
		defaultQuestionSet.add(Q4_l_Alone);

		SurveyQuestion Q4_m_Accompanies = new SurveyQuestion("Q4_m_Accompanies", "While "+mainActivity+", were you with:\n(Choose all that apply)", TYPE.MULTI_CHOICE);
		SurveyAnswer[] answerSet4m = new SurveyAnswer[5];
		answerSet4m[0] = new SurveyAnswer(0, "Friends");
		answerSet4m[1] = new SurveyAnswer(1, "Parents");
		answerSet4m[2] = new SurveyAnswer(2, "Siblings");
		answerSet4m[3] = new SurveyAnswer(3, "Teammates/Classmates");
		answerSet4m[4] = new SurveyAnswer(4, "People You Don\'t Know");
		Q4_m_Accompanies.setDefault("Q4_l_Alone", answerSet4m);
		defaultQuestionSet.add(Q4_m_Accompanies);

		SurveyQuestion Q5_a_SomethingElse = new SurveyQuestion("Q5_a_SomethingElse","Can you tell us what you were doing for the past 30 minutes that you answered as something else?",TYPE.FREE_FORM_TEXT);
		SurveyAnswer[] answerSet5a = new SurveyAnswer[1];
		Q5_a_SomethingElse.setDefault("Q4_a_WhatOther", answerSet5a);
		defaultQuestionSet.add(Q5_a_SomethingElse);

		SurveyQuestion Q5_b_HowManyMins = new SurveyQuestion("Q5_b_HowManyMins","Approximately how many minutes did you spend doing this activity:",TYPE.MINUTES_PICKER);
		SurveyAnswer[] answerSet5b = new SurveyAnswer[1];
		Q5_b_HowManyMins.setDefault("Q4_a_WhatOther", answerSet5b);
		defaultQuestionSet.add(Q5_b_HowManyMins);

		SurveyQuestion Q5_c_WereYou = new SurveyQuestion("Q5_c_WereYou","For this activity, were you:",TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet5c = new SurveyAnswer[5];
		answerSet5c[0] = new SurveyAnswer(0, "Lying down");
		answerSet5c[1] = new SurveyAnswer(1, "Sitting");
		answerSet5c[2] = new SurveyAnswer(2, "Standing");
		answerSet5c[3] = new SurveyAnswer(3, "Walking");
		answerSet5c[4] = new SurveyAnswer(4, "Jogging/Running");
		Q5_c_WereYou.setDefault("Q4_a_WhatOther", answerSet5c);
		defaultQuestionSet.add(Q5_c_WereYou);

		SurveyQuestion Q5_d_WherePhone = new SurveyQuestion("Q5_d_WherePhone","How did you have the PHONE while doing this activity?",TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet5d = new SurveyAnswer[5];
		answerSet5d[0] = new SurveyAnswer(0, "On my belt");
		answerSet5d[1] = new SurveyAnswer(1, "In my pocket");
		answerSet5d[2] = new SurveyAnswer(2, "In my handbag/purse/backpack");
		answerSet5d[3] = new SurveyAnswer(3, "Holding in my hand");
		answerSet5d[4] = new SurveyAnswer(4, "Not with me");
		Q5_d_WherePhone.setDefault("Q4_a_WhatOther", answerSet5d);
		defaultQuestionSet.add(Q5_d_WherePhone);

		SurveyQuestion Q5_e_YNotCarrying = new SurveyQuestion("Q5_e_YNotCarrying","Please indicate your reason for not carrying your phone while doing this activity:",TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet5e = new SurveyAnswer[7];
		answerSet5e[0] = new SurveyAnswer(0, "Forgot it");
		answerSet5e[1] = new SurveyAnswer(1, "Battery died");
		answerSet5e[2] = new SurveyAnswer(2, "Could not get it wet");
		answerSet5e[3] = new SurveyAnswer(3, "Too bulky");
		answerSet5e[4] = new SurveyAnswer(4, "Too Uncomfortable");
		answerSet5e[5] = new SurveyAnswer(5, "Embarrassed to carry it");
		answerSet5e[6] = new SurveyAnswer(6, "Not allowed to carry  it");
		Q5_e_YNotCarrying.setDefault("Q5_d_WherePhone", answerSet5e);
		defaultQuestionSet.add(Q5_e_YNotCarrying);

		SurveyQuestion Q5_f_MainPurpose = new SurveyQuestion("Q5_f_MainPurpose", "What was the MAIN PURPOSE of "+mainActivity, TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet5f = new SurveyAnswer[6];
		answerSet5f[0] = new SurveyAnswer(0, "Fun/Recreation");
		answerSet5f[1] = new SurveyAnswer(1, "Personal care");
		answerSet5f[2] = new SurveyAnswer(2, "To get somewhere");
		answerSet5f[3] = new SurveyAnswer(3, "Baby sitting/Childcare");
		answerSet5f[4] = new SurveyAnswer(4, "For work, homework, or housework");
		answerSet5f[5] = new SurveyAnswer(5, "Other");
		Q5_f_MainPurpose.setDefault("Q4_a_WhatOther", answerSet5f);
		defaultQuestionSet.add(Q5_f_MainPurpose);

		SurveyQuestion Q5_g_HowEnjoyable = new SurveyQuestion("Q5_g_HowEnjoyable", "How ENJOYABLE was "+mainActivity, TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet5g = new SurveyAnswer[5];
		answerSet5g[0] = new SurveyAnswer(0, "Not at all");
		answerSet5g[1] = new SurveyAnswer(1, "A little");
		answerSet5g[2] = new SurveyAnswer(2, "Moderately");
		answerSet5g[3] = new SurveyAnswer(3, "Quite a bit");
		answerSet5g[4] = new SurveyAnswer(4, "Extremely");
		Q5_g_HowEnjoyable.setDefault("Q4_a_WhatOther", answerSet5g);
		defaultQuestionSet.add(Q5_g_HowEnjoyable);

		SurveyQuestion Q5_h_SelfMotivated = new SurveyQuestion("Q5_h_SelfMotivated", "Were you "+mainActivity+" because YOU want to do it?", TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet5h = new SurveyAnswer[2];
		answerSet5h[0] = new SurveyAnswer(0, "Yes");
		answerSet5h[1] = new SurveyAnswer(1, "No");
		Q5_h_SelfMotivated.setDefault("Q4_a_WhatOther", answerSet5h);
		defaultQuestionSet.add(Q5_h_SelfMotivated);

		SurveyQuestion Q5_i_ParentsMotivated = new SurveyQuestion("Q5_i_ParentsMotivated", "Were you "+mainActivity+" because YOUR PARENTS want you do do it?", TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet5i = new SurveyAnswer[2];
		answerSet5i[0] = new SurveyAnswer(0, "Yes");
		answerSet5i[1] = new SurveyAnswer(1, "No");
		Q5_i_ParentsMotivated.setDefault("Q5_h_SelfMotivated", answerSet5i);
		defaultQuestionSet.add(Q5_i_ParentsMotivated);

		SurveyQuestion Q5_j_FriendsMotivated = new SurveyQuestion("Q5_j_FriendsMotivated", "Were you "+mainActivity+" because YOUR FRIENDS want you to do it?", TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet5j = new SurveyAnswer[2];
		answerSet5j[0] = new SurveyAnswer(0, "Yes");
		answerSet5j[1] = new SurveyAnswer(1, "No");
		Q5_j_FriendsMotivated.setDefault("Q5_h_SelfMotivated", answerSet5j);
		defaultQuestionSet.add(Q5_j_FriendsMotivated);

		SurveyQuestion Q5_k_TeacherMotivated = new SurveyQuestion("Q5_k_TeacherMotivated", "Were you "+mainActivity+" because YOUR TEACHERS want you to do it?", TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet5k = new SurveyAnswer[2];
		answerSet5k[0] = new SurveyAnswer(0, "Yes");
		answerSet5k[1] = new SurveyAnswer(1, "No");
		Q5_k_TeacherMotivated.setDefault("Q5_h_SelfMotivated", answerSet5k);
		defaultQuestionSet.add(Q5_k_TeacherMotivated);

		SurveyQuestion Q5_l_Alone = new SurveyQuestion("Q5_l_Alone", "Were you "+mainActivity+" ALONE?", TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet5l = new SurveyAnswer[2];
		answerSet5l[0] = new SurveyAnswer(0, "Yes");
		answerSet5l[1] = new SurveyAnswer(1, "No");
		Q5_l_Alone.setDefault("Q4_a_WhatOther", answerSet5l);
		defaultQuestionSet.add(Q5_l_Alone);

		SurveyQuestion Q5_m_Accompanies = new SurveyQuestion("Q5_m_Accompanies", "While "+mainActivity+", were you with:\n(Choose all that apply)", TYPE.MULTI_CHOICE);
		SurveyAnswer[] answerSet5m = new SurveyAnswer[5];
		answerSet5m[0] = new SurveyAnswer(0, "Friends");
		answerSet5m[1] = new SurveyAnswer(1, "Parents");
		answerSet5m[2] = new SurveyAnswer(2, "Siblings");
		answerSet5m[3] = new SurveyAnswer(3, "Teammates/Classmates");
		answerSet5m[4] = new SurveyAnswer(4, "People You Don\'t Know");
		Q5_m_Accompanies.setDefault("Q5_l_Alone", answerSet5m);
		defaultQuestionSet.add(Q5_m_Accompanies);
		
		/*************** Set up rules *******************/
		//set up main activity which can not be skipped
		Q1_MainActivity.setMainActivity(true);
		Q4_a_WhatOther.setMainActivity(true);
		
		// set up probabilities for questions be chosen
		Q1_MainActivity.addRules(new ChanceBeChosen(1));
		Q2_a_HowLong.addRules(new ChanceBeChosen(1));
		Q2_b_a_WereYou.addRules(new ChanceBeChosen(1));
		Q2_c_WherePhone.addRules(new ChanceBeChosen(1));
		Q2_d_YNotCarrying.addRules(new ChanceBeChosen(1));
		Q2_e_a_MainPurpose.addRules(new ChanceBeChosen(0.3));
		Q2_f_HowEnjoyable.addRules(new ChanceBeChosen(0.3));
		Q2_g_SelfMotivated.addRules(new ChanceBeChosen(0.3));
		Q2_h_ParentsMotivated.addRules(new ChanceBeChosen(1));
		Q2_i_FriendsMotivated.addRules(new ChanceBeChosen(1));
		Q2_j_TeacherMotivated.addRules(new ChanceBeChosen(1));
		Q2_k_Alone.addRules(new ChanceBeChosen(0.3));
		Q2_l_Accompanies.addRules(new ChanceBeChosen(1));
		Q3_a_Type.addRules(new ChanceBeChosen(1));
		Q3_b_HowManyMins.addRules(new ChanceBeChosen(1));
		Q3_c_WhatInvolve.addRules(new ChanceBeChosen(0.4));
		Q3_d_ExtraWeight.addRules(new ChanceBeChosen(0.4));
		Q3_e_UpOrDown.addRules(new ChanceBeChosen(0.4));
		Q3_f_HowSoreness.addRules(new ChanceBeChosen(0.4));
		Q3_g_MainPurpose.addRules(new ChanceBeChosen(0.4));
		Q3_h_HowEnjoyable.addRules(new ChanceBeChosen(0.4));
		Q3_i_SelfMotivated.addRules(new ChanceBeChosen(0.4));
		Q3_j_ParentsMotivated.addRules(new ChanceBeChosen(1));
		Q3_k_FriendsMotivated.addRules(new ChanceBeChosen(1));
		Q3_l_TeacherMotivated.addRules(new ChanceBeChosen(1));
		Q3_m_Where.addRules(new ChanceBeChosen(0.4));
		Q3_n_WhereOther.addRules(new ChanceBeChosen(1));
		Q3_o_Outdoors.addRules(new ChanceBeChosen(0.4));
		Q3_p_Alone.addRules(new ChanceBeChosen(0.4));
		Q3_q_Accompanies.addRules(new ChanceBeChosen(1));
		Q3_r_WherePhone.addRules(new ChanceBeChosen(1));
		Q3_s_YNotCarrying.addRules(new ChanceBeChosen(1));
		Q4_a_WhatOther.addRules(new ChanceBeChosen(1));
		Q4_b_HowManyMins.addRules(new ChanceBeChosen(1));
		Q4_c_WereYou.addRules(new ChanceBeChosen(1));
		Q4_d_WherePhone.addRules(new ChanceBeChosen(1));
		Q4_e_YNotCarrying.addRules(new ChanceBeChosen(1));
		Q4_f_MainPurpose.addRules(new ChanceBeChosen(0.3));
		Q4_g_HowEnjoyable.addRules(new ChanceBeChosen(0.3));
		Q4_h_SelfMotivated.addRules(new ChanceBeChosen(0.3));
		Q4_i_ParentsMotivated.addRules(new ChanceBeChosen(1));
		Q4_j_FriendsMotivated.addRules(new ChanceBeChosen(1));
		Q4_k_TeacherMotivated.addRules(new ChanceBeChosen(1));
		Q4_l_Alone.addRules(new ChanceBeChosen(0.3));
		Q4_m_Accompanies.addRules(new ChanceBeChosen(1));
		Q5_a_SomethingElse.addRules(new ChanceBeChosen(1));
		Q5_b_HowManyMins.addRules(new ChanceBeChosen(1));
		Q5_c_WereYou.addRules(new ChanceBeChosen(1));
		Q5_d_WherePhone.addRules(new ChanceBeChosen(1));
		Q5_e_YNotCarrying.addRules(new ChanceBeChosen(1));
		Q5_f_MainPurpose.addRules(new ChanceBeChosen(0.3));
		Q5_g_HowEnjoyable.addRules(new ChanceBeChosen(0.3));
		Q5_h_SelfMotivated.addRules(new ChanceBeChosen(0.3));
		Q5_i_ParentsMotivated.addRules(new ChanceBeChosen(1));
		Q5_j_FriendsMotivated.addRules(new ChanceBeChosen(1));
		Q5_k_TeacherMotivated.addRules(new ChanceBeChosen(1));
		Q5_l_Alone.addRules(new ChanceBeChosen(0.3));
		Q5_m_Accompanies.addRules(new ChanceBeChosen(1));

		// set up branching questions and rules		
		ArrayList<SurveyQuestion> questionSection_1 = new ArrayList<SurveyQuestion>(2);
		questionSection_1.add(Q2_a_HowLong);
		questionSection_1.add(Q2_c_WherePhone);
		Q1_MainActivity.addRules(new QuesFromAns(new int[]{0,1,2,4}, questionSection_1));
		
		ArrayList<SurveyQuestion> questionSection_2a = new ArrayList<SurveyQuestion>(1);
		questionSection_2a.add(Q2_b_a_WereYou);
		Q1_MainActivity.addRules(new QuesFromAns(new int[]{0,1,2}, questionSection_2a));

		ArrayList<SurveyQuestion> questionSection_2b = new ArrayList<SurveyQuestion>(1);
		questionSection_2b.add(Q2_b_b_WereYou);
		Q1_MainActivity.addRules(new QuesFromAns(new int[]{4}, questionSection_2b));

		
		ArrayList<SurveyQuestion> questionSection_1a = new ArrayList<SurveyQuestion>(1);
		questionSection_1a.add(Q1_a_UsingTech);
		Q1_MainActivity.addRules(new QuesFromAns(new int[]{1}, questionSection_1a));

		ArrayList<SurveyQuestion> questionSection_20 = new ArrayList<SurveyQuestion>(3);
		questionSection_20.add(Q2_f_HowEnjoyable);
		questionSection_20.add(Q2_g_SelfMotivated);
		questionSection_20.add(Q2_k_Alone);
		Q1_MainActivity.addRules(new QuesFromAns(new int[]{0,1,2,4}, questionSection_20, 0.3));

		ArrayList<SurveyQuestion> questionSection_20a = new ArrayList<SurveyQuestion>(1);
		questionSection_20a.add(Q2_e_a_MainPurpose);
		Q1_MainActivity.addRules(new QuesFromAns(new int[]{0,1,4}, questionSection_20a, 0.3));

		ArrayList<SurveyQuestion> questionSection_20b = new ArrayList<SurveyQuestion>(1);
		questionSection_20b.add(Q2_e_b_MainPurpose);
		Q1_MainActivity.addRules(new QuesFromAns(new int[]{2}, questionSection_20b, 0.3));

		ArrayList<SurveyQuestion> questionSection_2 = new ArrayList<SurveyQuestion>(1);
		questionSection_2.add(Q2_d_YNotCarrying);
		Q2_c_WherePhone.addRules(new QuesFromAns(new int[]{4}, questionSection_2));

		ArrayList<SurveyQuestion> questionSection_3 = new ArrayList<SurveyQuestion>(3);
		questionSection_3.add(Q2_h_ParentsMotivated);
		questionSection_3.add(Q2_i_FriendsMotivated);
		questionSection_3.add(Q2_j_TeacherMotivated);
		Q2_g_SelfMotivated.addRules(new QuesAsSequence(questionSection_3));
		
		ArrayList<SurveyQuestion> questionSection_4 = new ArrayList<SurveyQuestion>(1);
		questionSection_4.add(Q2_l_Accompanies);
		Q2_k_Alone.addRules(new QuesFromAns(new int[]{1}, questionSection_4));

		ArrayList<SurveyQuestion> questionSection_5 = new ArrayList<SurveyQuestion>(13);
		questionSection_5.add(Q3_a_Type);
		questionSection_5.add(Q3_b_HowManyMins);
		questionSection_5.add(Q3_c_WhatInvolve);
		questionSection_5.add(Q3_d_ExtraWeight);
		questionSection_5.add(Q3_e_UpOrDown);
		questionSection_5.add(Q3_f_HowSoreness);
		questionSection_5.add(Q3_g_MainPurpose);
		questionSection_5.add(Q3_h_HowEnjoyable);
		questionSection_5.add(Q3_i_SelfMotivated);
		questionSection_5.add(Q3_m_Where);
		questionSection_5.add(Q3_o_Outdoors);
		questionSection_5.add(Q3_p_Alone);
		questionSection_5.add(Q3_r_WherePhone);
		Q1_MainActivity.addRules(new QuesFromAns(new int[]{3}, questionSection_5));

		ArrayList<SurveyQuestion> questionSection_6 = new ArrayList<SurveyQuestion>(3);
		questionSection_6.add(Q3_j_ParentsMotivated);
		questionSection_6.add(Q3_k_FriendsMotivated);
		questionSection_6.add(Q3_l_TeacherMotivated);
		Q3_i_SelfMotivated.addRules(new QuesAsSequence(questionSection_6));

		ArrayList<SurveyQuestion> questionSection_7 = new ArrayList<SurveyQuestion>(1);
		questionSection_7.add(Q3_n_WhereOther);
		Q3_m_Where.addRules(new QuesFromAns(new int[]{1}, questionSection_7));

		ArrayList<SurveyQuestion> questionSection_8 = new ArrayList<SurveyQuestion>(1);
		questionSection_8.add(Q3_q_Accompanies);
		Q3_p_Alone.addRules(new QuesFromAns(new int[]{1}, questionSection_8));

		ArrayList<SurveyQuestion> questionSection_9 = new ArrayList<SurveyQuestion>(1);
		questionSection_9.add(Q3_s_YNotCarrying);
		Q3_r_WherePhone.addRules(new QuesFromAns(new int[]{4}, questionSection_9));

		ArrayList<SurveyQuestion> questionSection_10 = new ArrayList<SurveyQuestion>(1);
		questionSection_10.add(Q4_a_WhatOther);
		Q1_MainActivity.addRules(new QuesFromAns(new int[]{5}, questionSection_10));

		ArrayList<SurveyQuestion> questionSection_22 = new ArrayList<SurveyQuestion>(2);
		questionSection_22.add(Q4_b_HowManyMins);
		questionSection_22.add(Q4_d_WherePhone);
		Q4_a_WhatOther.addRules(new QuesFromAns(new int[]{0,1,2,3,4,5,6,7}, questionSection_22));

		ArrayList<SurveyQuestion> questionSection_11 = new ArrayList<SurveyQuestion>(1);
		questionSection_11.add(Q4_c_WereYou);
		Q4_a_WhatOther.addRules(new QuesFromAns(new int[]{0,1,3,4}, questionSection_11));
		
		ArrayList<SurveyQuestion> questionSection_19 = new ArrayList<SurveyQuestion>(4);
		questionSection_19.add(Q4_f_MainPurpose);
		questionSection_19.add(Q4_g_HowEnjoyable);
		questionSection_19.add(Q4_h_SelfMotivated);
		questionSection_19.add(Q4_l_Alone);
		Q4_a_WhatOther.addRules(new QuesFromAns(new int[]{0,1,2,3,4,5,6}, questionSection_19, 0.3));

		ArrayList<SurveyQuestion> questionSection_12 = new ArrayList<SurveyQuestion>(1);
		questionSection_12.add(Q4_e_YNotCarrying);
		Q4_d_WherePhone.addRules(new QuesFromAns(new int[]{4}, questionSection_12));

		ArrayList<SurveyQuestion> questionSection_13 = new ArrayList<SurveyQuestion>(3);
		questionSection_13.add(Q4_i_ParentsMotivated);
		questionSection_13.add(Q4_j_FriendsMotivated);
		questionSection_13.add(Q4_k_TeacherMotivated);
		Q4_h_SelfMotivated.addRules(new QuesAsSequence(questionSection_13));

		ArrayList<SurveyQuestion> questionSection_14 = new ArrayList<SurveyQuestion>(1);
		questionSection_14.add(Q4_m_Accompanies);
		Q4_l_Alone.addRules(new QuesFromAns(new int[]{1}, questionSection_14));

		ArrayList<SurveyQuestion> questionSection_15 = new ArrayList<SurveyQuestion>(4);
		questionSection_15.add(Q5_a_SomethingElse);
		questionSection_15.add(Q5_b_HowManyMins);
		questionSection_15.add(Q5_c_WereYou);
		questionSection_15.add(Q5_d_WherePhone);
		Q4_a_WhatOther.addRules(new QuesFromAns(new int[]{7}, questionSection_15));
		
		ArrayList<SurveyQuestion> questionSection_21 = new ArrayList<SurveyQuestion>(4);
		questionSection_21.add(Q5_f_MainPurpose);
		questionSection_21.add(Q5_g_HowEnjoyable);
		questionSection_21.add(Q5_h_SelfMotivated);
		questionSection_21.add(Q5_l_Alone);
		Q4_a_WhatOther.addRules(new QuesFromAns(new int[]{7}, questionSection_21, 0.3));

		ArrayList<SurveyQuestion> questionSection_16 = new ArrayList<SurveyQuestion>(1);
		questionSection_16.add(Q5_e_YNotCarrying);
		Q5_d_WherePhone.addRules(new QuesFromAns(new int[]{4}, questionSection_16));

		ArrayList<SurveyQuestion> questionSection_17 = new ArrayList<SurveyQuestion>(3);
		questionSection_17.add(Q5_i_ParentsMotivated);
		questionSection_17.add(Q5_j_FriendsMotivated);
		questionSection_17.add(Q5_k_TeacherMotivated);
		Q5_h_SelfMotivated.addRules(new QuesAsSequence(questionSection_17));

		ArrayList<SurveyQuestion> questionSection_18 = new ArrayList<SurveyQuestion>(1);
		questionSection_18.add(Q5_m_Accompanies);
		Q5_l_Alone.addRules(new QuesFromAns(new int[]{1}, questionSection_18));
	}
	
	@Override
	public String getReadableQuestionSetName() {
		// TODO Auto-generated method stub
		return "USC-EMA";
	}
	@Override
	public String[] getAllQuesIDs() {
		// TODO Auto-generated method stub
		String[] IDs = new String[defaultQuestionSet.size()];
		for (int i = 0; i < IDs.length; i++) {
			IDs[i] = defaultQuestionSet.get(i).getAliasID();
		}
		return IDs;
	}			
	
}