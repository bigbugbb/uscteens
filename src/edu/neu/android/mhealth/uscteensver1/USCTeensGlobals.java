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
	public final static int	   MAX_AVAILABLE_LABELING_DAYS = 2;	
	public final static String DATA_LOADING_RESULT = "DATA_LOADING_RESULT";
	public final static String QUEST_SELECTION = "QUEST_SELECTION";
	public final static String MERGE_SELECTION = "MERGE_SELECTION";
	public final static String LAST_DATA_LOADING_TIME = "LAST_DATA_LOADING_TIME";
	public final static String LAST_LABELING_TIME = "LAST_LABELING_TIME";
	public final static String CURRENT_SELECTED_DATE = "CURRENT_SELECTED_DATE";
	public final static String LAST_SELECTED_CHUNK = "LAST_SELECTED_CHUNK";
	public final static String LAST_DISPLAY_OFFSET_X = "LAST_DISPLAY_OFFSET_X";
	public final static String SENSOR_FOLDER     = "/Sensor/";
	public final static String ACTIVITY_FOLDER   = "/Activity/";
	public final static String REWARD_FOLDER     = "/Reward/";
	public final static String ANNOTATION_FOLDER = "/Annotation/";
	public final static String LABELS_FOLDER = "/Labels/";
	public final static String SENSOR_TYPE = "InternalAccel";
	public final static String ANNOTATION_SET = "Activities";	
	public final static int TIME_FOR_WAITING_INTERNAL_ACCELEROMETER = 20 * 1000;
	
	public static int SENSOR_DATA_SCALING_FACTOR = 3500;
	
	public static Context sContext = null;
	public static Handler sGlobalHandler = null;	
	public static boolean sUpdateConfig = false;
	
	public static final String ANNOTATION_GUID = "2F996145-7EB0-4E25-935C-10D53B15012D";
	public static final String UNLABELLED_GUID = "03F9A375-C162-4B24-AB74-BF23CD07B358";
	public static final String UNLABELLED_STRING = "Unlabelled";
	
	public static void initGlobals(Context aContext) {
		// By default the logging will go to the apps internal storage, not the external directory
		Globals.IS_DEBUG = false;
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
		Globals.DEFAULT_PING_ADDRESS  = "http://wockets.ccs.neu.edu/ping.php";	//TODO change to wockets server
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

		// Survey prompting parameters 
		Globals.AUDIO_PROMPT_START_HOUR = 7;
		Globals.AUDIO_PROMPT_END_HOUR   = 23; 
		Globals.REPROMPT_DELAY_MS = 5 * 60 * 1000; 
		Globals.MIN_MS_BETWEEN_SCHEDULED_PROMPTS = 30 * 60 * 1000; // 30 minutes 

		// Only apps that are prompted 
		Globals.ALL_APP_KEYS_PROMPTED = new String[1];
		Globals.ALL_APP_KEYS_PROMPTED[0] = Globals.SURVEY;
		
		Globals.classFromKey = new HashMap<String, Class<?>>();
		Globals.classFromKey.put(Globals.GETHELP, edu.neu.android.wocketslib.activities.helpcomment.GetHelpActivity.class);
		Globals.classFromKey.put(Globals.SURVEY, edu.neu.android.wocketslib.emasurvey.SurveyActivity.class);

		// Appinfo order of preference in main menu
		Globals.ALL_APP_KEYS = new String[2];
		Globals.ALL_APP_KEYS[0] = Globals.GETHELP;
		Globals.ALL_APP_KEYS[1] = Globals.SURVEY;

		Globals.MIN_MS_FOR_SENSING_WHEN_PHONE_PLUGGED_IN = 0;
	}
	
}
