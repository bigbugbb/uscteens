package edu.neu.android.mhealth.uscteensver1;

import java.util.HashMap;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Handler;
import edu.neu.android.wocketslib.Globals;
import edu.neu.android.wocketslib.activities.helpcomment.GetHelpActivity;
import edu.neu.android.wocketslib.emasurvey.SurveyActivity;
import edu.neu.android.wocketslib.utils.FileHelper;

/**
 * The place for redefining/reset some global variables in the WocketsLib.
 *
 * @author bigbug
 */
public class TeensGlobals {
    public static String VERSION_NAME = "";

    public final static int PIXEL_PER_DATA = 2;
    public final static int MAXIMUM_WIDTH_IN_PIXEL = 3600 * 24 * PIXEL_PER_DATA;
    public final static int REFRESH_DATA_TIME_THRESHOLD = 60 * 1000; // in ms

    public final static String DATA_LOADING_RESULT = "DATA_LOADING_RESULT";
    public final static String QUEST_SELECTION = "QUEST_SELECTION";
    public final static String MERGE_SELECTION = "MERGE_SELECTION";
    public final static String LAST_DATA_LOADING_TIME = "LAST_DATA_LOADING_TIME";
    public final static String LAST_LABELING_TIME = "LAST_LABELING_TIME";
    public final static String CURRENT_SELECTED_DATE = "CURRENT_SELECTED_DATE";
    public final static String LAST_SELECTED_CHUNK = "LAST_SELECTED_CHUNK";
    public final static String LAST_DISPLAY_OFFSET_X = "LAST_DISPLAY_OFFSET_X";
    public final static String SENSOR_FOLDER = "/mHealth/sensors/";
    public final static String LABELS_FOLDER = "/labels/";
    public final static String ICON_FOLDER = "/icons/";
    public final static String REWARD_FOLDER = "/rewards/";
    public final static String SENSOR_TYPE = "InternalAccel";
    public final static String ANNOTATION_SET = "TeensActivities";

    public static Handler sGlobalHandler = null;
    public static boolean sUpdateConfig = false;
    public static int MAX_LABEL_WINDOW = 2;

    public static final boolean IS_CALIBRATION_ENABLED = false;

    public static final String ANNOTATION_GUID = "2F996145-7EB0-4E25-935C-10D53B15012D";
    public static final String UNLABELLED_GUID = "03F9A375-C162-4B24-AB74-BF23CD07B358";
    public static final String UNLABELLED_STRING = "Unlabelled";

    public static final int DAILY_LAST_SECOND = 3600 * 24 - 1;

    // Tutorial video id
    public static final String TUTORIAL_VIDEO_URI = "ytv://eXs3YeyIZTo";
    
    public static final String APPLY_GIFT_CARD_URL = "https://www.amazon.com/gp/css/gc/payment/ref=gc_ya_subnav_view-gc-balance?";

    public static String DIRECTORY_PATH = "";

    public static void initGlobals(Context context) {
        // By default the logging will go to the apps internal storage, not the external directory
        Globals.IS_DEBUG = false;
        Globals.IS_LOG_EXTERNAL = false;
        Globals.APP_DIRECTORY = "uscteens";

        Globals.setArbitrater(new TeensArbitrater(context));
        Globals.initDataDirectories(context);

        DIRECTORY_PATH = Globals.EXTERNAL_DIRECTORY_PATH;

        Globals.STUDY_NAME = "USCTeens"; // "Teens Study";
        Globals.STUDY_SERVER_NAME = "USCTeens";
        Globals.UNIQUE_LOG_STRING = "TESTTeens";

        FileHelper.testFunction(context);

        Globals.WOCKETS_SP_URI = Uri.parse("content://edu.neu.android.mhealth.uscteensver1.provider");
        Globals.PACKAGE_NAME = "edu.neu.android.mhealth.uscteensver1";

        Globals.FAQ_URL = "http://www.ccs.neu.edu/home/intille/studyinfo/asthmafaq.html";
        Globals.NEWS_URL = "http://www.ccs.neu.edu/home/intille/studyinfo/asthmafaq.html";
        Globals.DEFAULT_CC = "projectmobile@usc.edu";
        Globals.HOTLINE_NUMBER = "3234428206";

        Globals.IS_WOCKETS_ENABLED               = false;
        Globals.IS_BLUETOOTH_ENABLED             = false;
        Globals.IS_LOCATION_ENABLED              = true;
        Globals.IS_READING_SENSOR_ENABLED        = true; // true for debug
        Globals.IS_LOG_PHONE_BATTERY_ENABLED     = true;
        Globals.IS_RECORDING_PHONE_ACCEL_ENABLED = true;

        Globals.IS_ANNOTATION_EXTERNAL  = true;
        Globals.IS_SENSOR_DATA_EXTERNAL = true;

        Globals.DEFAULT_MAIL_USERNAME = "wocketssmtp@gmail.com";
        Globals.DEFAULT_MAIL_PASSWORD = "W0CKET$mtp";
        Globals.DEFAULT_PING_ADDRESS  = "http://wockets.ccs.neu.edu/ping.php";
        Globals.DEFAULT_SAMPLING_RATE = 40;

        // Send the status information once per hour
        Globals.JSON_DATA_UPLOAD_INTERVAL = Globals.MINUTES_60_IN_MS;

        // Backup all uploads to the sd card backups uploads directory (for testing)
        Globals.BACKUP_UPLOADS_EXTERNAL = true;

        // Percentage of uploads that must be successful to keep trying in a single session
        Globals.UPLOAD_SUCCESS_PERCENTAGE = .85f;

//		public static String serverAddress = "http://wockets.ccs.neu.edu:8080/Wockets/android/getWocketsDetail.html";
//		public static String serverAddress = "http://wockets.ccs.neu.edu:9080/Wockets/android/getWocketsDetail.html";
        Globals.DEFAULT_SERVER_ADDR = "http://wockets.ccs.neu.edu:8080/";

        // These variable are most easily set by calling initServerWebCalls() from
        // the main app after setting DEFAULT_SERVER_ADDR and PHP_DEFAULT_SERVER_ADDR
        Globals.SERVER_ADDRESS_PID = Globals.DEFAULT_SERVER_ADDR + "Wockets/android/getParticipantId.html";
        Globals.GET_WOCKETS_DETAIL_URL = Globals.DEFAULT_SERVER_ADDR + "Wockets/android/getWocketsDetail.html";
        Globals.POST_ANDROID_DATA_LOG_URL = Globals.DEFAULT_SERVER_ADDR + "Wockets/AndroidDataLog.html";
        Globals.URL_FILE_UPLOAD_SERVLET = Globals.DEFAULT_SERVER_ADDR + "FileUploader/Commonsfileuploadservlet";
        Globals.URL_GET_WOCKETS_DETAIL = Globals.DEFAULT_SERVER_ADDR + "Wockets/android/getWocketsDetail.html?pId=6809";

        Globals.PW_STAFF_PASSWORD   = "uscteen";
        Globals.PW_SUBJECT_PASSWORD = "setup";

        Globals.SFTP_SERVER_USER_NAME = "sftpdownload";
        Globals.SFTP_SERVER_PASSWORD  = "$parRow1ark";
        Globals.SFTP_SERVER_URL       = "wockets.ccs.neu.edu";

        // Survey prompting parameters
        Globals.AUDIO_PROMPT_START_HOUR = 15;
        Globals.AUDIO_PROMPT_END_HOUR   = 22;
        Globals.DEFAULT_PROMPTS_PER_DAY = 7;
        Globals.DEFAULT_START_HOUR      = 15;
        Globals.DEFAULT_END_HOUR        = 22;
        Globals.REPROMPT_DELAY_MS = 3 * Globals.MINUTES_1_IN_MS;
        Globals.MIN_MS_BETWEEN_SCHEDULED_PROMPTS = Globals.MINUTES_30_IN_MS;
        Globals.MAX_TIME_ALLOWED_BETWEEN_PROMPT_AND_COMPLETION_MS = 5 * Globals.MINUTES_1_IN_MS;
        Globals.MAX_TIME_ALLOWED_BETWEEN_MANUAL_START_AND_COMPLETION_MS = 5 * Globals.MINUTES_1_IN_MS / 2;
        Globals.IS_POPPING_SURVEY_BACK_ENABLED = true;
        Globals.TIMING_FOR_POPPING_SURVEY_BACK = 30 * 1000; // the last 30 seconds of the survey question life

        // Only apps that are prompted
        Globals.ALL_APP_KEYS_PROMPTED = new String[1];
        Globals.ALL_APP_KEYS_PROMPTED[0] = Globals.SURVEY;

        Globals.classFromKey = new HashMap<String, Class<?>>();
        Globals.classFromKey.put(Globals.GETHELP, GetHelpActivity.class);
        Globals.classFromKey.put(Globals.SURVEY, SurveyActivity.class);

        // Appinfo order of preference in main menu
        Globals.ALL_APP_KEYS = new String[2];
        Globals.ALL_APP_KEYS[0] = Globals.GETHELP;
        Globals.ALL_APP_KEYS[1] = Globals.SURVEY;

        Globals.MIN_MS_FOR_SENSING_WHEN_PHONE_PLUGGED_IN = 21000;

        try {
            String packageName = context.getPackageName();
            TeensGlobals.VERSION_NAME = "Ver. " +
                    context.getPackageManager().getPackageInfo(packageName, 0).versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        Globals.myBroadcastReceiverProcessor = new TeensBroadcastReceiverProcessor();
    }

}
