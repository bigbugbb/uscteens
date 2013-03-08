 package edu.neu.android.mhealth.uscteensver1;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.telephony.TelephonyManager;
import edu.neu.android.wocketslib.utils.FileHelper;
import edu.neu.android.wocketslib.utils.Log;
import edu.neu.android.wocketslib.Globals;

/**
 * Globals that are in the Wockets library that are redefined by the calling application
 * @author SSI
 *
 */
public class USCTeensGlobals {
	private static final String TAG = "USCTeensGlobals";	

	public final static int TIME_FOR_WAITING_INTERNAL_ACCELEROMETER = 20 * 1000;
		
	public static void initGlobals(Context aContext) {
		// By default the logging will go to the apps internal storage, not the external directory
		Globals.IS_LOG_EXTERNAL = false;
		Globals.APP_DIRECTORY = ".uscteens";
		Globals.LOG_DIRECTORY = Globals.APP_DIRECTORY + File.separator + "logs";
		Globals.DATA_DIRECTORY = Globals.APP_DIRECTORY + File.separator + "data";
		Globals.INTERNAL_DIRECTORY_PATH = aContext.getFilesDir().getAbsolutePath();
		Globals.EXTERNAL_DIRECTORY_PATH = Environment.getExternalStorageDirectory().getAbsolutePath(); 
		Globals.SURVEY_LOG_DIRECTORY = Globals.APP_DIRECTORY + File.separator + "survey";
		Globals.UPLOADS_DIRECTORY = Globals.APP_DIRECTORY + File.separator + "uploads";
		Globals.BACKUP_DIRECTORY = Globals.APP_DIRECTORY + File.separator + "backup";

		Globals.STUDY_NAME = "USCTeens"; // "Teens Study"; 
		Globals.STUDY_SERVER_NAME = "USCTeens"; 

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
