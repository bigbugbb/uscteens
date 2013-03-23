package edu.neu.android.mhealth.uscteensver1.survey;


import java.util.ArrayList;

import edu.neu.android.wocketslib.emasurvey.model.QuestionSet;
import edu.neu.android.wocketslib.emasurvey.model.QuestionSetParamHandler;
import edu.neu.android.wocketslib.emasurvey.model.SurveyAnswer;
import edu.neu.android.wocketslib.emasurvey.model.SurveyQuestion;
import edu.neu.android.wocketslib.emasurvey.model.SurveyQuestion.TYPE;
import edu.neu.android.wocketslib.emasurvey.rule.ChanceBeChosen;
import edu.neu.android.wocketslib.emasurvey.rule.QuesAsGroup;
import edu.neu.android.wocketslib.emasurvey.rule.QuesAsSequence;
import edu.neu.android.wocketslib.emasurvey.rule.QuesFromAns;

public class RandomTeensSurveyQuestionSet extends QuestionSet{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final double VERSION = 1;
	private ArrayList<SurveyQuestion> defaultQuestionSet;
	private String contextUpperCase = null;
	public static final String optionalContextUpperCase = "In the past four hours";
	public static final String defaultContextUpperCase = "Since the last survey you answered";
	private String contextLowerCase = null;
	public static final String optionalContextLowerCase = "in the past four hours";
	public static final String defaultContextLowerCase = "since the last survey you answered";

	public static final int RANDOM_EMA_DEFAULT = 0;
	public static final int RANDOM_EMA_OPTIONAL = 1;
	
	public RandomTeensSurveyQuestionSet(QuestionSetParamHandler param){
		super();
		if(param.getParamNum() != 1)
			return;
		int type = (Integer)param.getParams()[0];
		
		switch(type){
		case RANDOM_EMA_DEFAULT:
			contextUpperCase = defaultContextUpperCase;
			contextLowerCase = defaultContextLowerCase;
			break;
		case RANDOM_EMA_OPTIONAL:
			contextUpperCase = optionalContextUpperCase;
			contextLowerCase = optionalContextLowerCase;
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
		defaultQuestionSet  = new ArrayList<SurveyQuestion>(27);
/************ Initialize questions and answers *********/
		SurveyQuestion Q1_a_HowHappy = new SurveyQuestion("Q1_a_HowHappy", 
				"How HAPPY were you feeling just before the phone went off?", 
				TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet1a = new SurveyAnswer[4];
		answerSet1a[0] = new SurveyAnswer(0, "Not at all");
		answerSet1a[1] = new SurveyAnswer(1, "A little");
		answerSet1a[2] = new SurveyAnswer(2, "Quite a bit");
		answerSet1a[3] = new SurveyAnswer(3, "Extremely");
		Q1_a_HowHappy.setDefault(SurveyQuestion.NO_DATA, answerSet1a);
		defaultQuestionSet.add(Q1_a_HowHappy);

		SurveyQuestion Q1_b_HowJoyful = new SurveyQuestion("Q1_b_HowJoyful", 
				"How JOYFUL were you feeling just before the phone went off?", 
				TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet1b = new SurveyAnswer[4];
		answerSet1b[0] = new SurveyAnswer(0, "Not at all");
		answerSet1b[1] = new SurveyAnswer(1, "A little");
		answerSet1b[2] = new SurveyAnswer(2, "Quite a bit");
		answerSet1b[3] = new SurveyAnswer(3, "Extremely");
		Q1_b_HowJoyful.setDefault("Q1_a_HowHappy", answerSet1b);
		defaultQuestionSet.add(Q1_b_HowJoyful);

		SurveyQuestion Q2_a_HowMad = new SurveyQuestion("Q2_a_HowMad", 
				"How MAD OR ANGRY were you feeling just before the phone went off?", 
				TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet2a = new SurveyAnswer[4];
		answerSet2a[0] = new SurveyAnswer(0, "Not at all");
		answerSet2a[1] = new SurveyAnswer(1, "A little");
		answerSet2a[2] = new SurveyAnswer(2, "Quite a bit");
		answerSet2a[3] = new SurveyAnswer(3, "Extremely");
		Q2_a_HowMad.setDefault("Q1_a_HowHappy", answerSet2a);
		defaultQuestionSet.add(Q2_a_HowMad);

		SurveyQuestion Q2_b_HowNervous = new SurveyQuestion("Q2_b_HowNervous", 
				"How NERVOUS OR ANXIOUS were you feeling just before the phone went off?", 
				TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet2b = new SurveyAnswer[4];
		answerSet2b[0] = new SurveyAnswer(0, "Not at all");
		answerSet2b[1] = new SurveyAnswer(1, "A little");
		answerSet2b[2] = new SurveyAnswer(2, "Quite a bit");
		answerSet2b[3] = new SurveyAnswer(3, "Extremely");
		Q2_b_HowNervous.setDefault("Q2_a_HowMad", answerSet2b);
		defaultQuestionSet.add(Q2_b_HowNervous);

		SurveyQuestion Q2_c_HowSad = new SurveyQuestion("Q2_c_HowSad", 
				"How SAD were you feeling just before the phone went off?", 
				TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet2c = new SurveyAnswer[4];
		answerSet2c[0] = new SurveyAnswer(0, "Not at all");
		answerSet2c[1] = new SurveyAnswer(1, "A little");
		answerSet2c[2] = new SurveyAnswer(2, "Quite a bit");
		answerSet2c[3] = new SurveyAnswer(3, "Extremely");
		Q2_c_HowSad.setDefault("Q2_a_HowMad", answerSet2c);
		defaultQuestionSet.add(Q2_c_HowSad);

		SurveyQuestion Q3_a_HowStressed = new SurveyQuestion("Q3_a_HowStressed", 
				"How STRESSED were you feeling just before the phone went off?", 
				TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet3a = new SurveyAnswer[4];
		answerSet3a[0] = new SurveyAnswer(0, "Not at all");
		answerSet3a[1] = new SurveyAnswer(1, "A little");
		answerSet3a[2] = new SurveyAnswer(2, "Quite a bit");
		answerSet3a[3] = new SurveyAnswer(3, "Extremely");
		Q3_a_HowStressed.setDefault("Q1_a_HowHappy", answerSet3a);
		defaultQuestionSet.add(Q3_a_HowStressed);

		SurveyQuestion Q3_b_Certainty = new SurveyQuestion("Q3_b_Certainty", 
				"Just before the phone went off, how certain did you feel that you can cope with all the things that you have to do?", 
				TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet3b = new SurveyAnswer[4];
		answerSet3b[0] = new SurveyAnswer(0, "Not at all");
		answerSet3b[1] = new SurveyAnswer(1, "A little");
		answerSet3b[2] = new SurveyAnswer(2, "Quite a bit");
		answerSet3b[3] = new SurveyAnswer(3, "Extremely");
		Q3_b_Certainty.setDefault("Q3_a_HowStressed", answerSet3b);
		defaultQuestionSet.add(Q3_b_Certainty);

		SurveyQuestion Q3_c_Confidence = new SurveyQuestion("Q3_c_Confidence", 
				"Just before the phone went off, how confident did you feel about your ability to handle your personal problems?", 
				TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet3c = new SurveyAnswer[4];
		answerSet3c[0] = new SurveyAnswer(0, "Not at all");
		answerSet3c[1] = new SurveyAnswer(1, "A little");
		answerSet3c[2] = new SurveyAnswer(2, "Quite a bit");
		answerSet3c[3] = new SurveyAnswer(3, "Extremely");
		Q3_c_Confidence.setDefault("Q3_a_HowStressed", answerSet3c);
		defaultQuestionSet.add(Q3_c_Confidence);

		SurveyQuestion Q4_a_HowEnergetic = new SurveyQuestion("Q4_a_HowEnergetic", 
				"How ENERGETIC or FULL OF PEP were you feeling just before the phone went off?",
				TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet4a = new SurveyAnswer[4];
		answerSet4a[0] = new SurveyAnswer(0, "Not at all");
		answerSet4a[1] = new SurveyAnswer(1, "A little");
		answerSet4a[2] = new SurveyAnswer(2, "Quite a bit");
		answerSet4a[3] = new SurveyAnswer(3, "Extremely");
		Q4_a_HowEnergetic.setDefault("Q1_a_HowHappy", answerSet4a);
		defaultQuestionSet.add(Q4_a_HowEnergetic);

		SurveyQuestion Q5_a_HowFatigued = new SurveyQuestion("Q5_a_HowFatigued", 
				"How FATIGUED or TIRED were you feeling just before the phone went off?",
				TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet5a = new SurveyAnswer[4];
		answerSet5a[0] = new SurveyAnswer(0, "Not at all");
		answerSet5a[1] = new SurveyAnswer(1, "A little");
		answerSet5a[2] = new SurveyAnswer(2, "Quite a bit");
		answerSet5a[3] = new SurveyAnswer(3, "Extremely");
		Q5_a_HowFatigued.setDefault("Q1_a_HowHappy", answerSet5a);
		defaultQuestionSet.add(Q5_a_HowFatigued);

		SurveyQuestion Q6_a_MainActivity = new SurveyQuestion("Q6_a_MainActivity", 
				"What were you DOING just before the phone went off?\n (Choose all that apply)",
				TYPE.MULTI_CHOICE);
		SurveyAnswer[] answerSet6a = new SurveyAnswer[7];
		answerSet6a[0] = new SurveyAnswer(0, "Reading/Computer/Homework");
		answerSet6a[1] = new SurveyAnswer(1, "Using technology (TV, phone)");
		answerSet6a[2] = new SurveyAnswer(2, "Active Play/Sports/Exercising");
		answerSet6a[3] = new SurveyAnswer(3, "Eating/Drinking");
		answerSet6a[4] = new SurveyAnswer(4, "Going somewhere");
		answerSet6a[5] = new SurveyAnswer(5, "Sleeping");
		answerSet6a[6] = new SurveyAnswer(6, "Something else");
		Q6_a_MainActivity.setDefault("Q1_a_HowHappy", answerSet6a);
		defaultQuestionSet.add(Q6_a_MainActivity);

		SurveyQuestion Q6_b_SomethingElse = new SurveyQuestion("Q6_b_SomethingElse", 
				"Please specify what you were DOING just before the phone went off:",
				TYPE.FREE_FORM_TEXT);
		SurveyAnswer[] answerSet6b = new SurveyAnswer[1];
		Q6_b_SomethingElse.setDefault("Q6_a_MainActivity", answerSet6b);
		defaultQuestionSet.add(Q6_b_SomethingElse);
		
		SurveyQuestion Q6_c_UsingTech = new SurveyQuestion("Q6_c_UsingTech", 
				"While using technology (TV, phone), were you:",
				TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet6c = new SurveyAnswer[6];
		answerSet6c[0] = new SurveyAnswer(0, "Playing video games");
		answerSet6c[1] = new SurveyAnswer(1, "Talking");
		answerSet6c[2] = new SurveyAnswer(2, "Texting");
		answerSet6c[3] = new SurveyAnswer(3, "Using the Internet");
		answerSet6c[4] = new SurveyAnswer(4, "Watching shows/movies");
		answerSet6c[5] = new SurveyAnswer(5, "Other");
		Q6_c_UsingTech.setDefault("Q6_a_MainActivity", answerSet6c);
		defaultQuestionSet.add(Q6_c_UsingTech);

		SurveyQuestion Q6_d_GoingSomewhere = new SurveyQuestion("Q6_d_GoingSomewhere", 
				"While going somewhere, were you:",
				TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet6d = new SurveyAnswer[6];
		answerSet6d[0] = new SurveyAnswer(0, "Walking");
		answerSet6d[1] = new SurveyAnswer(1, "Biking");
		answerSet6d[2] = new SurveyAnswer(2, "Riding a bus");
		answerSet6d[3] = new SurveyAnswer(3, "Riding the Metro/train");
		answerSet6d[4] = new SurveyAnswer(4, "Riding in a car/taxi");
		answerSet6d[5] = new SurveyAnswer(5, "Other (skateboarding, etc.)");
		Q6_d_GoingSomewhere.setDefault("Q6_a_MainActivity", answerSet6d);
		defaultQuestionSet.add(Q6_d_GoingSomewhere);
		
		SurveyQuestion Q7_a_PhysicalContext = new SurveyQuestion("Q7_a_PhysicalContext", 
				"WHERE were you just before the phone went off?",
				TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet7a = new SurveyAnswer[7];
		answerSet7a[0] = new SurveyAnswer(0, "Home (Indoors)");
		answerSet7a[1] = new SurveyAnswer(1, "School");
		answerSet7a[2] = new SurveyAnswer(2, "Outdoors");
		answerSet7a[3] = new SurveyAnswer(3, "Restaurant");
		answerSet7a[4] = new SurveyAnswer(4, "Store/Mall");
		answerSet7a[5] = new SurveyAnswer(5, "Someone else\'s house (Indoors)");
		answerSet7a[6] = new SurveyAnswer(6, "Other");
		Q7_a_PhysicalContext.setDefault("Q1_a_HowHappy", answerSet7a);
		defaultQuestionSet.add(Q7_a_PhysicalContext);

		SurveyQuestion Q7_b_Outdoors = new SurveyQuestion("Q7_b_Outdoors", 
				"Where were you OUTDOORS?",
				TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet7b = new SurveyAnswer[7];
		answerSet7b[0] = new SurveyAnswer(0, "Home (front/back yard)");
		answerSet7b[1] = new SurveyAnswer(1, "School");
		answerSet7b[2] = new SurveyAnswer(2, "Park/trail");
		answerSet7b[3] = new SurveyAnswer(3, "Sidewalk");
		answerSet7b[4] = new SurveyAnswer(4, "Road");
		answerSet7b[5] = new SurveyAnswer(5, "Parking lot");
		answerSet7b[6] = new SurveyAnswer(6, "Other");
		Q7_b_Outdoors.setDefault("Q7_a_PhysicalContext", answerSet7b);
		defaultQuestionSet.add(Q7_b_Outdoors);

		SurveyQuestion Q8_a_SocialContext = new SurveyQuestion("Q8_a_SocialContext", 
				contextUpperCase+", were you:\n(Choose all that apply)",
				TYPE.MULTI_CHOICE);
		SurveyAnswer[] answerSet8a = new SurveyAnswer[4];
		answerSet8a[0] = new SurveyAnswer(0, "Alone");
		answerSet8a[1] = new SurveyAnswer(1, "With your mom/dad");
		answerSet8a[2] = new SurveyAnswer(2, "With your sister(s) or brother(s)");
		answerSet8a[3] = new SurveyAnswer(3, "With your friend(s)");
		Q8_a_SocialContext.setDefault("Q1_a_HowHappy", answerSet8a);
		defaultQuestionSet.add(Q8_a_SocialContext);

		SurveyQuestion Q9_a_AnyStressful = new SurveyQuestion("Q9_a_AnyStressful", 
				contextUpperCase+", has anything stressful happened to you?",
				TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet9a = new SurveyAnswer[3];
		answerSet9a[0] = new SurveyAnswer(0, "No stressful things have happened");
		answerSet9a[1] = new SurveyAnswer(1, "A few stressful things have happened");
		answerSet9a[2] = new SurveyAnswer(2, "Many stressful things have happened");
		Q9_a_AnyStressful.setDefault("Q1_a_HowHappy", answerSet9a);
		defaultQuestionSet.add(Q9_a_AnyStressful);

		SurveyQuestion Q9_b_AnyTeased = new SurveyQuestion("Q9_b_AnyTeased", 
				contextUpperCase+", has anyone teased you?",
				TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet9b = new SurveyAnswer[5];
		answerSet9b[0] = new SurveyAnswer(0, "Yes, and caused very much stress");
		answerSet9b[1] = new SurveyAnswer(1, "Yes, and caused some stress");
		answerSet9b[2] = new SurveyAnswer(2, "Yes, and caused a little stress");
		answerSet9b[3] = new SurveyAnswer(3, "Yes, but not at all stressful");
		answerSet9b[4] = new SurveyAnswer(4, "No");
		Q9_b_AnyTeased.setDefault("Q9_a_AnyStressful", answerSet9b);
		defaultQuestionSet.add(Q9_b_AnyTeased);

		SurveyQuestion Q9_c_HaveArgued = new SurveyQuestion("Q9_c_HaveArgued", 
				contextUpperCase+", have you argued with anyone?",
				TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet9c = new SurveyAnswer[5];
		answerSet9c[0] = new SurveyAnswer(0, "Yes, and caused very much stress");
		answerSet9c[1] = new SurveyAnswer(1, "Yes, and caused some stress");
		answerSet9c[2] = new SurveyAnswer(2, "Yes, and caused a little stress");
		answerSet9c[3] = new SurveyAnswer(3, "Yes, but not at all stressful");
		answerSet9c[4] = new SurveyAnswer(4, "No");
		Q9_c_HaveArgued.setDefault("Q9_b_AnyTeased", answerSet9c);
		defaultQuestionSet.add(Q9_c_HaveArgued);

		SurveyQuestion Q9_d_DisagreeWithParents = new SurveyQuestion("Q9_d_DisagreeWithParents", 
				contextUpperCase+", have you had a misunderstanding or disagreement with your parents?",
				TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet9d = new SurveyAnswer[5];
		answerSet9d[0] = new SurveyAnswer(0, "Yes, and caused very much stress");
		answerSet9d[1] = new SurveyAnswer(1, "Yes, and caused some stress");
		answerSet9d[2] = new SurveyAnswer(2, "Yes, and caused a little stress");
		answerSet9d[3] = new SurveyAnswer(3, "Yes, but not at all stressful");
		answerSet9d[4] = new SurveyAnswer(4, "No");
		Q9_d_DisagreeWithParents.setDefault("Q9_c_HaveArgued", answerSet9d);
		defaultQuestionSet.add(Q9_d_DisagreeWithParents);

		SurveyQuestion Q9_e_TooManyWork = new SurveyQuestion("Q9_e_TooManyWork", 
				contextUpperCase+", have you had too many things to do?",
				TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet9e = new SurveyAnswer[5];
		answerSet9e[0] = new SurveyAnswer(0, "Yes, and caused very much stress");
		answerSet9e[1] = new SurveyAnswer(1, "Yes, and caused some stress");
		answerSet9e[2] = new SurveyAnswer(2, "Yes, and caused a little stress");
		answerSet9e[3] = new SurveyAnswer(3, "Yes, but not at all stressful");
		answerSet9e[4] = new SurveyAnswer(4, "No");
		Q9_e_TooManyWork.setDefault("Q9_d_DisagreeWithParents", answerSet9e);
		defaultQuestionSet.add(Q9_e_TooManyWork);

		SurveyQuestion Q10_a_AnyCoughing = new SurveyQuestion("Q10_a_AnyCoughing", 
				contextUpperCase+", have you experienced COUGHING?",
				TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet10a = new SurveyAnswer[4];
		answerSet10a[0] = new SurveyAnswer(0, "Not at all");
		answerSet10a[1] = new SurveyAnswer(1, "A little");
		answerSet10a[2] = new SurveyAnswer(2, "Quite a bit");
		answerSet10a[3] = new SurveyAnswer(3, "Very much so");
		Q10_a_AnyCoughing.setDefault("Q1_a_HowHappy", answerSet10a);
		defaultQuestionSet.add(Q10_a_AnyCoughing);

		SurveyQuestion Q10_b_AnyWheezing = new SurveyQuestion("Q10_b_AnyWheezing", 
				contextUpperCase+", have you experienced WHEEZING?",
				TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet10b = new SurveyAnswer[4];
		answerSet10b[0] = new SurveyAnswer(0, "Not at all");
		answerSet10b[1] = new SurveyAnswer(1, "A little");
		answerSet10b[2] = new SurveyAnswer(2, "Quite a bit");
		answerSet10b[3] = new SurveyAnswer(3, "Very much so");
		Q10_b_AnyWheezing.setDefault("Q10_a_AnyCoughing", answerSet10b);
		defaultQuestionSet.add(Q10_b_AnyWheezing);

		SurveyQuestion Q10_c_AnyChestTightness = new SurveyQuestion("Q10_c_AnyChestTightness", 
				contextUpperCase+", have you experienced CHEST TIGHTNESS?",
				TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet10c = new SurveyAnswer[4];
		answerSet10c[0] = new SurveyAnswer(0, "Not at all");
		answerSet10c[1] = new SurveyAnswer(1, "A little");
		answerSet10c[2] = new SurveyAnswer(2, "Quite a bit");
		answerSet10c[3] = new SurveyAnswer(3, "Very much so");
		Q10_c_AnyChestTightness.setDefault("Q10_b_AnyWheezing", answerSet10c);
		defaultQuestionSet.add(Q10_c_AnyChestTightness);

		SurveyQuestion Q10_d_AnyShortnessBreath = new SurveyQuestion("Q10_d_AnyShortnessBreath", 
				contextUpperCase+", have you experienced SHORTNESS OF BREATH?",
				TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet10d = new SurveyAnswer[4];
		answerSet10d[0] = new SurveyAnswer(0, "Not at all");
		answerSet10d[1] = new SurveyAnswer(1, "A little");
		answerSet10d[2] = new SurveyAnswer(2, "Quite a bit");
		answerSet10d[3] = new SurveyAnswer(3, "Very much so");
		Q10_d_AnyShortnessBreath.setDefault("Q10_c_AnyChestTightness", answerSet10d);
		defaultQuestionSet.add(Q10_d_AnyShortnessBreath);

		SurveyQuestion Q11_a_AvoidStrenuous = new SurveyQuestion("Q11_a_AvoidStrenuous", 
				contextUpperCase+", have you avoided strenuous activities because of your asthma?",
				TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet11a = new SurveyAnswer[4];
		answerSet11a[0] = new SurveyAnswer(0, "Not at all");
		answerSet11a[1] = new SurveyAnswer(1, "A little");
		answerSet11a[2] = new SurveyAnswer(2, "Quite a bit");
		answerSet11a[3] = new SurveyAnswer(3, "Very much so");
		Q11_a_AvoidStrenuous.setDefault("Q1_a_HowHappy", answerSet11a);
		defaultQuestionSet.add(Q11_a_AvoidStrenuous);

		SurveyQuestion Q11_b_AvoidAsthmaAttack = new SurveyQuestion("Q11_b_AvoidAsthmaAttack", 
				contextUpperCase+", have you avoided situations that could bring on an asthma attack?",
				TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet11b = new SurveyAnswer[4];
		answerSet11b[0] = new SurveyAnswer(0, "Not at all");
		answerSet11b[1] = new SurveyAnswer(1, "A little");
		answerSet11b[2] = new SurveyAnswer(2, "Quite a bit");
		answerSet11b[3] = new SurveyAnswer(3, "Very much so");
		Q11_b_AvoidAsthmaAttack.setDefault("Q11_a_AvoidStrenuous", answerSet11b);
		defaultQuestionSet.add(Q11_b_AvoidAsthmaAttack);

		SurveyQuestion Q11_c_AnyWorries = new SurveyQuestion("Q11_c_AnyWorries", 
				contextUpperCase+", have your worried about having an asthma attack?",
				TYPE.SINGLE_CHOICE);
		SurveyAnswer[] answerSet11c = new SurveyAnswer[4];
		answerSet11c[0] = new SurveyAnswer(0, "Not at all");
		answerSet11c[1] = new SurveyAnswer(1, "A little");
		answerSet11c[2] = new SurveyAnswer(2, "Quite a bit");
		answerSet11c[3] = new SurveyAnswer(3, "Very much so");
		Q11_c_AnyWorries.setDefault("Q11_b_AvoidAsthmaAttack", answerSet11c);
		defaultQuestionSet.add(Q11_c_AnyWorries);

		/*************** Set up rules *******************/
		Q1_a_HowHappy.addRules(new ChanceBeChosen(0.6));
		Q1_b_HowJoyful.addRules(new ChanceBeChosen(1));
		Q2_a_HowMad.addRules(new ChanceBeChosen(0.6));
		Q2_b_HowNervous.addRules(new ChanceBeChosen(1));
		Q2_c_HowSad.addRules(new ChanceBeChosen(1));
		Q3_a_HowStressed.addRules(new ChanceBeChosen(0.6));
		Q3_b_Certainty.addRules(new ChanceBeChosen(1));
		Q3_c_Confidence.addRules(new ChanceBeChosen(1));
		Q4_a_HowEnergetic.addRules(new ChanceBeChosen(0.6));
		Q5_a_HowFatigued.addRules(new ChanceBeChosen(0.6));
		Q6_a_MainActivity.addRules(new ChanceBeChosen(1));
		Q6_b_SomethingElse.addRules(new ChanceBeChosen(1));
		Q6_c_UsingTech.addRules(new ChanceBeChosen(1));
		Q6_d_GoingSomewhere.addRules(new ChanceBeChosen(1));
		Q7_a_PhysicalContext.addRules(new ChanceBeChosen(0.6));
		Q7_b_Outdoors.addRules(new ChanceBeChosen(1));
		Q8_a_SocialContext.addRules(new ChanceBeChosen(1));
		Q9_a_AnyStressful.addRules(new ChanceBeChosen(1));
		Q9_b_AnyTeased.addRules(new ChanceBeChosen(1));
		Q9_c_HaveArgued.addRules(new ChanceBeChosen(1));
		Q9_d_DisagreeWithParents.addRules(new ChanceBeChosen(1));
		Q9_e_TooManyWork.addRules(new ChanceBeChosen(1));
		Q10_a_AnyCoughing.addRules(new ChanceBeChosen(0.6));
		Q10_b_AnyWheezing.addRules(new ChanceBeChosen(1));
		Q10_c_AnyChestTightness.addRules(new ChanceBeChosen(1));
		Q10_d_AnyShortnessBreath.addRules(new ChanceBeChosen(1));
		Q11_a_AvoidStrenuous.addRules(new ChanceBeChosen(0.6));
		Q11_b_AvoidAsthmaAttack.addRules(new ChanceBeChosen(1));
		Q11_c_AnyWorries.addRules(new ChanceBeChosen(1));

		//set up branching questions and rules
		ArrayList<SurveyQuestion> questionSection_1 = new ArrayList<SurveyQuestion>();
		questionSection_1.add(Q2_a_HowMad);
		questionSection_1.add(Q3_a_HowStressed);
		questionSection_1.add(Q4_a_HowEnergetic);
		questionSection_1.add(Q5_a_HowFatigued);
		questionSection_1.add(Q6_a_MainActivity);
		questionSection_1.add(Q7_a_PhysicalContext);
		questionSection_1.add(Q8_a_SocialContext);
		questionSection_1.add(Q9_a_AnyStressful);
		questionSection_1.add(Q10_a_AnyCoughing);
		questionSection_1.add(Q11_a_AvoidStrenuous);
		Q1_a_HowHappy.addRules(new QuesAsGroup(questionSection_1));

		ArrayList<SurveyQuestion> questionSection_2 = new ArrayList<SurveyQuestion>();
		questionSection_2.add(Q1_b_HowJoyful);
		Q1_a_HowHappy.addRules(new QuesAsSequence(questionSection_2));
		
		ArrayList<SurveyQuestion> questionSection_3 = new ArrayList<SurveyQuestion>();
		questionSection_3.add(Q2_b_HowNervous);
		questionSection_3.add(Q2_c_HowSad);
		Q2_a_HowMad.addRules(new QuesAsSequence(questionSection_3));
		
		ArrayList<SurveyQuestion> questionSection_4 = new ArrayList<SurveyQuestion>();
		questionSection_4.add(Q3_b_Certainty);
		questionSection_4.add(Q3_c_Confidence);
		Q3_a_HowStressed.addRules(new QuesAsSequence(questionSection_4));
		
		ArrayList<SurveyQuestion> questionSection_5 = new ArrayList<SurveyQuestion>();
		questionSection_5.add(Q6_b_SomethingElse);
		Q6_a_MainActivity.addRules(new QuesFromAns(new int[]{6},questionSection_5));
		
		ArrayList<SurveyQuestion> questionSection_5b = new ArrayList<SurveyQuestion>();
		questionSection_5b.add(Q6_c_UsingTech);
		Q6_a_MainActivity.addRules(new QuesFromAns(new int[]{1},questionSection_5b));

		ArrayList<SurveyQuestion> questionSection_5c = new ArrayList<SurveyQuestion>();
		questionSection_5c.add(Q6_d_GoingSomewhere);
		Q6_a_MainActivity.addRules(new QuesFromAns(new int[]{4},questionSection_5c));

		ArrayList<SurveyQuestion> questionSection_6 = new ArrayList<SurveyQuestion>();
		questionSection_6.add(Q7_b_Outdoors);
		Q7_a_PhysicalContext.addRules(new QuesFromAns(new int[]{2},questionSection_6));

		ArrayList<SurveyQuestion> questionSection_7 = new ArrayList<SurveyQuestion>();
		questionSection_7.add(Q9_b_AnyTeased);
		questionSection_7.add(Q9_c_HaveArgued);
		questionSection_7.add(Q9_d_DisagreeWithParents);
		questionSection_7.add(Q9_e_TooManyWork);
		Q9_a_AnyStressful.addRules(new QuesAsSequence(questionSection_7));

		ArrayList<SurveyQuestion> questionSection_8 = new ArrayList<SurveyQuestion>();
		questionSection_8.add(Q10_b_AnyWheezing);
		questionSection_8.add(Q10_c_AnyChestTightness);
		questionSection_8.add(Q10_d_AnyShortnessBreath);
		Q10_a_AnyCoughing.addRules(new QuesAsSequence(questionSection_8));

		ArrayList<SurveyQuestion> questionSection_9 = new ArrayList<SurveyQuestion>();
		questionSection_9.add(Q11_b_AvoidAsthmaAttack);
		questionSection_9.add(Q11_c_AnyWorries);
		Q11_a_AvoidStrenuous.addRules(new QuesAsSequence(questionSection_9));

	}
	@Override
	public String getReadableQuestionSetName() {
		// TODO Auto-generated method stub
		return "Random-EMA";
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
