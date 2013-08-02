package edu.neu.android.mhealth.uscteensver1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

import android.content.Context;
import android.os.Environment;
import edu.neu.android.mhealth.uscteensver1.survey.TeensSurveyScheduler;
import edu.neu.android.mhealth.uscteensver1.threads.UpdateRewardTask;
import edu.neu.android.wocketslib.Globals;
import edu.neu.android.wocketslib.sensormonitor.Arbitrater;
import edu.neu.android.wocketslib.support.DataStorage;
import edu.neu.android.wocketslib.support.ServerLogger;
import edu.neu.android.wocketslib.utils.DateHelper;
import edu.neu.android.wocketslib.utils.Log;

/**
 * This class is used as a callback which has been invoked from
 * BluetoothSensorService in WocketsLib.
 * 
 * @author bigbug
 *
 */
public class TeensArbitrater extends Arbitrater {
	private static final String TAG = "TeensArbitrater";
	
	private Context mContext;
	private TeensSurveyScheduler mScheduler;
	
	public TeensArbitrater(Context context) {
		mContext = context;
		mScheduler = new TeensSurveyScheduler(context);
	}

	public void doArbitrate(boolean isNewSoftwareVersion) {		
		// For testing purpose only
		saveRecordsInLogcat(false);
		
		// Send application version information to server at midnight
		sendAppVersionAtMidnight();

		// Try to prompt the next survey if possible
		mScheduler.tryToPromptSurvey(isNewSoftwareVersion);	
		
		// Try to update reward file from sftp server
		tryToUpdateAppData();
				
		// Mark that arbitration taking place
		DataStorage.setLastTimeArbitrate(mContext, System.currentTimeMillis());
	}
	
	private void sendAppVersionAtMidnight() {
		long midnight = DateHelper.getDailyTime(0, 0);
		
		if (System.currentTimeMillis() - midnight < Globals.MINUTES_1_IN_MS) {
			ServerLogger.sendNote(mContext, TeensGlobals.VERSION_NAME, Globals.NO_PLOT);
		}
	}
	
	private void tryToUpdateAppData() {
		final String KEY = "LAST_REWARD_DATA_UPDATE_TIME";
		long lastUpdateTime = DataStorage.GetValueLong(mContext, KEY, 0);
		
		if (Math.abs(System.currentTimeMillis() - lastUpdateTime) > Globals.HOURS24_MS) {
			new UpdateRewardTask(mContext).execute();
			DataStorage.SetValue(mContext, KEY, System.currentTimeMillis());
		} 
	}
	
	public static void saveRecordsInLogcat(boolean isClear) {
		StringBuilder log = new StringBuilder();
		try {
			Process process = Runtime.getRuntime().exec("logcat -d -v time");
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

			String line = "";
			while ((line = bufferedReader.readLine()) != null) {
				if (line.contains("uscteens") && (line.contains("29 bytes")) || line.contains("elapsed")) {
					log.append(line + "\r\n");
				}
			}
			if (log.length() > 0) {
				saveLogCatRecord(log.toString());
			}
			if (isClear) {
				process = Runtime.getRuntime().exec("logcat -c");
			}
		} catch (IOException e) {
			Log.e(TAG, "Could not read or clear logcat.");
		}

	}

	private static void saveLogCatRecord(String log) {
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return;
		}
		String folderPath = Globals.SURVEY_LOG_DIRECTORY + 
				File.separator + DateHelper.serverDateFormat.format(new Date());
		File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + folderPath);
		folder.mkdirs();

		File logFile = new File(folder, "logCatRecord.txt");
		try {
			if (!logFile.exists()) {
				logFile.createNewFile();
			}
			BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true));
			writer.append(log);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
