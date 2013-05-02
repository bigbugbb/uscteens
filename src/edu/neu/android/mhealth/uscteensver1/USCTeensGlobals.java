 package edu.neu.android.mhealth.uscteensver1;

import java.io.File;
import java.util.HashMap;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import edu.neu.android.wocketslib.Globals;
import edu.neu.android.wocketslib.utils.FileHelper;

/**
 * Globals that are in the Wockets library that are redefined by the calling application
 * @author SSI
 *
 */
public class USCTeensGlobals {
	private static final String TAG = "USCTeensGlobals";	

	public static String VERSION_NAME = "";
	public final static int    PIXEL_PER_DATA = 2;	
	public final static int    MAX_WIDTH_IN_PIXEL = 3600 * 24 * PIXEL_PER_DATA;
	public final static int    UPDATING_TIME_THRESHOLD = 60 * 1000; // in ms
	public final static int	   MAX_AVAILABLE_LABELING_DAYS = 4;
	public final static String QUEST_SELECTION = "QUEST_SELECTION";
	public final static String MERGE_SELECTION = "MERGE_SELECTION";
	public final static String LAST_DATA_LOADING_TIME = "LAST_DATA_LOADING_TIME";
	public final static String CURRENT_SELECTED_DATE = "CURRENT_SELECTED_DATE";
	public final static String LAST_SELECTED_CHUNK = "LAST_SELECTED_CHUNK";
	public final static String LAST_DISPLAY_OFFSET_X = "LAST_DISPLAY_OFFSET_X";
	public final static String SENSOR_FOLDER     = "/Sensor/";
	public final static String ACTIVITY_FOLDER   = "/Activity/";
	public final static String ANNOTATION_FOLDER = "/Annotation/";
	public final static String LABELS_FOLDER = "/Labels/";
	public final static String SENSOR_TYPE = "InternalAccel";
	public final static String ANNOTATION_SET = "Activities";	
	public final static int TIME_FOR_WAITING_INTERNAL_ACCELEROMETER = 20 * 1000;
	
	public static int sAccelDataScalingFactor = 1800;
	
	public static Context sContext = null;
	public static Handler sGlobalHandler = null;
	
//	public static final String[] ACTION_NAMES = new String[] {  		
//		"Reading/Homework", "Watching TV/Movies", "Using the computer",
//		"Eating", "Sports", "Going somewhere",
//		"Lying down", "Sitting", "Standing",
//		"Walking", "Hanging with friends", "Doing chores", 
//		"Cooking", "Riding in a car", "Playing video games", 
//		"Using the phone", "Showering/Bathing", "Sleeping",
//		"Doing something else", "I don't remember", "Running",
//		"Basketball", "Football", "Soccer", 
//		"Jogging", "Dance class", "Karate class",
//		"Strength training", "Bicycling", "Swimming",
//		"Baseball", "Skateboarding", /*"Walking pet",
//		"Playing pet", "Playing kid", "In meeting",
//		"Teaching", "Labeling my day", "Putting around",
//		"Cleaning up", "Doing dishes", "Charging phone",
//		"Driving a car", "Taking the train", "Taking the bus",
//		"Doing a mix of things", "Waiting around"*/
//	};
	
//	public static final int[] ACTION_IMGS = new int[] {	
//		R.drawable.reading, R.drawable.watchingtv, R.drawable.usingcomputer,
//		R.drawable.eating, R.drawable.sports, R.drawable.goingsomewhere,
//		R.drawable.lyingdown, R.drawable.sitting, R.drawable.standing,
//		R.drawable.walking, R.drawable.hangingwfriends, R.drawable.doingchores,
//		R.drawable.cooking, R.drawable.ridinginacar, R.drawable.videogames,
//		R.drawable.usingthephone, R.drawable.showering, R.drawable.sleeping,
//		R.drawable.somethingelse, R.drawable.idontremember, R.drawable.running,
//		R.drawable.basketball, R.drawable.football, R.drawable.soccer,
//		R.drawable.jogging, R.drawable.dance, R.drawable.karate,
//		R.drawable.strength_training, R.drawable.bicycling, R.drawable.swimming,
//		R.drawable.baseball, R.drawable.skateboarding
//	};
	
	public static final String ANNOTATION_GUID = "2F996145-7EB0-4E25-935C-10D53B15012D";
	
	public static final String UNLABELLED_GUID = "03F9A375-C162-4B24-AB74-BF23CD07B358";
//	
//	public static final String[] ACTIONS_GUID = new String[] {
//		"D929FAB8-9614-466C-8E62-B845D53DB80D",
//		"28365949-D08E-443A-A8B2-FBD0893E01B9",
//		"2BCA02F4-27DB-4D18-8BB8-E43E72F54852",
//		"06F8226B-A0C5-42A6-95A3-8EFA770A0E4A",
//
//		"E1BB662B-BBF6-482E-9416-A3E9DF69870C",
//		"FDCAD523-3C2C-45F1-9705-130C14BDC30D",
//		"79637898-3F3F-4339-90AD-135665259479",
//		"6D748F4B-77A4-4876-ACFB-A0C194BF6E09",
//
//		"62A18268-D3FA-4A0E-A397-4170A961D2CE",
//		"004FF248-2439-4DE7-BD02-725FB6D9091D",
//		"28E2DCD3-D450-47A2-84FC-F1B474FAFB76",
//		"C8524DF3-E709-4AA8-BAC0-01AC26F1BF95",
//
//		"CC8B3B69-0806-419E-9515-25E7C33D608D",
//		"912D3872-8999-4A08-90EC-C5FBAAC7555F",
//		"7D4B6257-10F3-436E-9BD4-F9C2153767B1",
//		"D92248D8-E4F2-435F-A301-C1877044F20F",
//
//		"A11EF031-93F5-487A-B944-F0B0ED0F642C",
//		"1A812254-20BA-4823-8649-CE72CA640965",
//		"BE8C23CD-74DE-457B-925D-54CEF87413F6",
//		"3F1F4DA5-D973-4BF9-BEE4-E374B2EDBB5A",
//
//		"C38A7D91-FA60-49D3-84FA-3219F86CC209",
//		"71783773-27AF-45CE-B617-2B7D4F4F7E06",
//		"F842B45E-CCD3-40FB-935D-DAB8D8ACE8AA",
//		"3A82F7AF-1146-4783-946C-0CD9C00E59FC",
//
//		"2A43A7E9-2C31-43B0-B18E-C039A6FA11EC",
//		"71D004C4-D2DA-4C6A-9DB8-619C9428BE2C",
//		"1D082D89-C95E-456B-A093-484F44135552",
//		"A37B468C-7105-45BF-A4D6-9332E5846CB4",
//
//		"FFA9C6A2-2F0E-4E0F-A301-F777C123E9E6",
//		"10832F21-B1BE-4A25-B6B7-0F3CFC780E62",
//		"06D2F88D-0F25-445A-912A-819E90C1F162",
//		"850C114A-27C9-40E5-A9CA-873EC0C1A4FC",
//		/*
//		"30AF0C80-38D4-4DCC-8328-E5E9CB26244F",
//		"EA3C891A-DA60-4403-83EF-A02B135E5961",
//		"78B71146-47CF-4696-A7D5-0D8ADC63FAB5",
//		"DD98FB69-2131-45E2-9BF4-2BEE86AD21C6",
//		
//		"642EF652-DEC7-47A4-A3D2-A9BAE1E76461",
//		"36D12350-6B0E-4708-B9D5-42CF73F4434D",
//		"A536E5C6-4752-47C3-9277-2A9AAAE3D853",
//		"B3801125-0731-4C5A-B26C-323B5AABC141",
//		
//		"7BF7BD61-2992-42AA-A426-7DC6CC3D9154",
//		"CD4AE2EC-2F2D-48DC-9332-D766FD06A937",
//		"C5A54D27-742B-4192-92DC-56E97929ACF6",
//		"3AA860D1-E271-4D98-8BE9-6CF021CA8E51",
//
//		"7AA73808-7732-4F89-A835-0A6B9C5B2912",
//		"7C3B03D3-A738-4B40-9CA1-4B54232FA90B",
//		"02FF0313-8E27-4C13-AC20-F27784AA58DB",*/
//
//
//		"03F9A375-C162-4B24-AB74-BF23CD07B358",  // UNLABELLED
//	};
	
	public static void initGlobals(Context aContext) {
		// By default the logging will go to the apps internal storage, not the external directory
		Globals.IS_DEBUG = true;
		Globals.IS_LOG_EXTERNAL = false;
		Globals.APP_DIRECTORY = "uscteens";
		Globals.LOG_DIRECTORY = Globals.APP_DIRECTORY + File.separator + "logs";
		Globals.DATA_DIRECTORY = Globals.APP_DIRECTORY + File.separator + "data";
		Globals.INTERNAL_DIRECTORY_PATH = aContext.getFilesDir().getAbsolutePath();
		Globals.EXTERNAL_DIRECTORY_PATH = Environment.getExternalStorageDirectory().getAbsolutePath(); 
		Globals.SURVEY_LOG_DIRECTORY = Globals.APP_DIRECTORY + File.separator + "survey";
		Globals.UPLOADS_DIRECTORY = Globals.APP_DIRECTORY + File.separator + "uploads";
		Globals.BACKUP_DIRECTORY = Globals.APP_DIRECTORY + File.separator + "backup";

		Globals.STUDY_NAME = "USCTeens"; // "Teens Study"; 
		Globals.STUDY_SERVER_NAME = "USCTeens";
		Globals.UNIQUE_LOG_STRING = "TESTTeens";

		FileHelper.testFunction(aContext);
		
		Globals.WOCKETS_SP_URI = Uri.parse("content://edu.neu.android.mhealth.uscteensver1.provider");
		Globals.PACKAGE_NAME = "edu.neu.android.mhealth.uscteensver1";
		Globals.REPROMPT_DELAY_MS = 6 * 60 * 1000; 
		Globals.MIN_MS_BETWEEN_SCHEDULED_PROMPTS = 15 * 60 * 1000; // 15 minutes 

		Globals.FAQ_URL = "http://www.ccs.neu.edu/home/intille/studyinfo/asthmafaq.html";
		Globals.NEWS_URL = "http://www.ccs.neu.edu/home/intille/studyinfo/asthmafaq.html";
		Globals.DEFAULT_CC = "projectmobile@usc.edu";
		Globals.HOTLINE_NUMBER = "3234428206";
		
		Globals.IS_WOCKETS_ENABLED = false;
		Globals.IS_BLUETOOTH_ENABLED = false; 
		Globals.IS_LOCATION_ENABLED = false; 
		Globals.IS_READING_SENSOR_ENABLED = true; // true for debug

		Globals.DEFAULT_MAIL_USERNAME = "wocketssmtp@gmail.com";
		Globals.DEFAULT_MAIL_PASSWORD = "W0CKET$mtp";
		Globals.DEFAULT_PING_ADDRESS = "http://wockets.ccs.neu.edu/ping.php";	//TODO change to wockets server
		

		Globals.DEFAULT_SAMPLING_RATE = 40;
		
//		public static String serverAddress = "http://wockets.ccs.neu.edu:8080/Wockets/android/getWocketsDetail.html";
//		public static String serverAddress = "http://wockets.ccs.neu.edu:9080/Wockets/android/getWocketsDetail.html";
		
		Globals.DEFAULT_SERVER_ADDR = "http://wockets.ccs.neu.edu:8080/";
		Globals.SERVER_ADDRESS_PID = Globals.DEFAULT_SERVER_ADDR + "Wockets/android/getParticipantId.html";
		Globals.GET_WOCKETS_DETAIL_URL = Globals.DEFAULT_SERVER_ADDR + "Wockets/android/getWocketsDetail.html";
		Globals.POST_ANDROID_DATA_LOG_URL = Globals.DEFAULT_SERVER_ADDR + "Wockets/AndroidDataLog.html";
		Globals.URL_FILE_UPLOAD_SERVLET = Globals.DEFAULT_SERVER_ADDR + "FileUploader/Commonsfileuploadservlet";
		Globals.URL_GET_WOCKETS_DETAIL = Globals.DEFAULT_SERVER_ADDR + "Wockets/android/getWocketsDetail.html?pId=6809";		
		
		Globals.PW_STAFF_PASSWORD = "uscteen";
		Globals.PW_SUBJECT_PASSWORD = "setup";
//		Globals.PW_STATDOWN_PASSWORD = "statdb";
//		Globals.PW_STATDOWN_PASSWORD1 = "stattest";
//		Globals.PW_SYNC_PASSWORD = "sync";

		Globals.AUDIO_PROMPT_START_HOUR = 7;
		Globals.AUDIO_PROMPT_END_HOUR = 23;

		//Appinfo
		// Order of preference in main menu
		Globals.ALL_APP_KEYS = new String[2];
		Globals.ALL_APP_KEYS[0] = Globals.GETHELP;
		Globals.ALL_APP_KEYS[1] = Globals.SURVEY;

		// Only apps that are prompted 
		Globals.ALL_APP_KEYS_PROMPTED = new String[1];
		Globals.ALL_APP_KEYS_PROMPTED[0] = Globals.SURVEY;
		
		Globals.classFromKey = new HashMap<String, Class<?>>();

		Globals.classFromKey.put(Globals.GETHELP, edu.neu.android.wocketslib.activities.helpcomment.GetHelpActivity.class);
		Globals.classFromKey.put(Globals.SURVEY, edu.neu.android.wocketslib.emasurvey.SurveyActivity.class);

		Globals.MIN_MS_FOR_SENSING_WHEN_PHONE_PLUGGED_IN = 0;
	}
	
}
