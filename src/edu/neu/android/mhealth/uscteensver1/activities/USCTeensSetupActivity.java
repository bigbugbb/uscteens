package edu.neu.android.mhealth.uscteensver1.activities;

import java.util.Date;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import edu.neu.android.mhealth.uscteensver1.R;
import edu.neu.android.mhealth.uscteensver1.USCTeensGlobals;
import edu.neu.android.mhealth.uscteensver1.survey.CSTeensSurvey;
import edu.neu.android.mhealth.uscteensver1.survey.RandomTeensSurveyQuestionSet;
import edu.neu.android.wocketslib.Globals;
import edu.neu.android.wocketslib.broadcastreceivers.MonitorServiceBroadcastReceiver;
import edu.neu.android.wocketslib.dataupload.DataSender;
import edu.neu.android.wocketslib.dataupload.RawUploader;
import edu.neu.android.wocketslib.emasurvey.SurveyActivity;
import edu.neu.android.wocketslib.emasurvey.model.QuestionSet;
import edu.neu.android.wocketslib.emasurvey.model.QuestionSetParamHandler;
import edu.neu.android.wocketslib.sensormonitor.DataStore;
import edu.neu.android.wocketslib.sensormonitor.Sensor;
import edu.neu.android.wocketslib.support.AppInfo;
import edu.neu.android.wocketslib.support.DataStorage;
import edu.neu.android.wocketslib.support.LogcatReader;
import edu.neu.android.wocketslib.support.ServerLogger;
import edu.neu.android.wocketslib.utils.BaseActivity;
import edu.neu.android.wocketslib.utils.Log;
import edu.neu.android.wocketslib.utils.Util;

public class USCTeensSetupActivity extends BaseActivity {
	private static final String TAG = "SetupTeenGameActivity"; 
	public static final String KEY_RESCUE_INHALER = "_KEY_RESCUE_INHALER";
	private Button startService;
	private Button setStartDate;
	private Button randomEMA;
	private Button csEMA;
	private Button finishStudy;
	private Button setupdone;
	private AlertDialog setupRescueInhaler = null;
	private LinearLayout textDisplay = null;
	private ScrollView scrollView = null;

	private void displayToastMessage(String aMsg) {
		Toast aToast = Toast.makeText(getApplicationContext(),aMsg, Toast.LENGTH_LONG);
		aToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
		aToast.show();		
	}
	
	/**
	 * Set the update button on/off depending on if the code detects that the software
	 * is or is not at the latest version on the Android Market. 
	 */
	private class SendAllFilesToServerTask extends AsyncTask<Void, Void, Boolean> { 
		@Override
		protected Boolean doInBackground(Void... params) {
			// send JSON file
			long currentTime = System.currentTimeMillis();
			String msg = "Finish study - Starting data and log files upload";
			// Move JSON to external upload folder
			RawUploader.moveDataToExternal(USCTeensSetupActivity.this, false, true, true, .85); // raw .json data
			// Move Log files to external upload folder
			DataSender.sendLogsToExternalUploadDir(USCTeensSetupActivity.this, true); // log data with no postfix name
			// Move Survey Log files to upload folder
			// DataSender.sendSurveyLogsToExternalUploadDir(SetupActivity.this, true);
			// Transmit Note first
			ServerLogger.transmitOrQueueNote(USCTeensSetupActivity.this, msg, true); // wi data
			// Upload JSON files and remove
			int filesRemaining = RawUploader.uploadDataFromExternal(USCTeensSetupActivity.this, // need subject id being set
					true, true, true, false, .85);
			// Upload Log and SurveyLog files, backup and remove
			filesRemaining = RawUploader.uploadDataFromExternal(USCTeensSetupActivity.this,
					false, true, true, true, .85);
			// Upload possible remaining files in the internal memory
			filesRemaining = RawUploader.uploadDataFromInternal(USCTeensSetupActivity.this, // ?
					false, true, true, false, .85);

			msg = "Completed user-initiated file upload after "
					+ String.format(
							"%.1f",
							((System.currentTimeMillis() - currentTime) / 1000.0 / 60.0))
					+ " minutes. Files remaining to upload: " + filesRemaining;
			ServerLogger.sendNote(USCTeensSetupActivity.this, msg, true);
			return true;
		}

		protected void onPostExecute(Boolean isNeedUpdate) {
			displayToastMessage("Transmission complete.");
			finishStudy.setEnabled(true);
		}
	}

	/**
	 * Set the update button on/off depending on if the code detects that the software
	 * is or is not at the latest version on the Android Market. 
	 */
//	private class SendLogFilesTask extends AsyncTask<Void, Void, Boolean> 
//	{ 
//		@Override
//		protected Boolean doInBackground(Void... params) {
//			long startTime = System.currentTimeMillis(); 
//			String msg = "Starting user-initiated log file upload";
//			ServerLogger.sendNote(getApplicationContext(), msg, true);
//
//			DataSender.sendLogsToExternalUploadDir(getApplicationContext(), true);
//			int filesRemaining = -1; //RawUploader.uploadData(getApplicationContext(), false, true, true, .85);
//
//			msg = "Completed user-initiated log file upload after " + String.format("%.1f",((System.currentTimeMillis()-startTime)/1000.0/60.0)) + " minutes. Files remaining to upload: " + filesRemaining; 		
//			ServerLogger.sendNote(getApplicationContext(), msg, true);
//			return true; 
//		}
//
//		protected void onPostExecute(Boolean isNeedUpdate) {
//			displayToastMessage("Finished sending asthma log files.");
//			uploadLogs.setEnabled(true);
//		}
//	}

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, TAG);
		setContentView(R.layout.activity_setup);
		setStartDate = (Button) findViewById(R.id.setstartdate);
		startService = (Button) findViewById(R.id.startservice);		
		csEMA        = (Button) findViewById(R.id.csema);
		randomEMA    = (Button) findViewById(R.id.randomema);
		finishStudy  = (Button) findViewById(R.id.buttonfinishstudy);
		setupdone    = (Button) findViewById(R.id.setupdone);
//		uploadLogs = (Button) findViewById(R.id.buttonuploadlogs);

		setStartDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(getApplicationContext(), StartDateSetupActivity.class);
				startActivity(i);				
			}
		});
		
		startService.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(
						MonitorServiceBroadcastReceiver.TYPE_START_SENSOR_MONITOR_SERVICE_NOW);
				sendBroadcast(i);
				Toast.makeText(getApplicationContext(),
						"Starting the service...", Toast.LENGTH_LONG).show();
			}
		});
		
		finishStudy.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				Log.i(TAG, "Send all data and log files");
				displayToastMessage("Request to finish this study, sending all data to the server now.");
				finishStudy.setEnabled(false);
				new SendAllFilesToServerTask().execute();
			}
		});
		
		randomEMA.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AppInfo.SetStartManualTime(getApplicationContext(),
						Globals.SURVEY, System.currentTimeMillis());
				Intent i = new Intent(USCTeensSetupActivity.this, SurveyActivity.class);
				
				long lastTimeCompleted = AppInfo.GetLastTimeCompleted(
						USCTeensSetupActivity.this, Globals.SURVEY);
				long currentTime = System.currentTimeMillis();
				int classType = 0;
				if ((currentTime - lastTimeCompleted) < 4 * 60 * 60 * 1000) {
					classType = RandomTeensSurveyQuestionSet.RANDOM_EMA_DEFAULT;
				} else {
					classType = RandomTeensSurveyQuestionSet.RANDOM_EMA_OPTIONAL;
				}
				i.putExtra("className",
						RandomTeensSurveyQuestionSet.class.getCanonicalName());
				i.putExtra(QuestionSet.TAG, new QuestionSetParamHandler(1,
						new Object[] { classType }));
				startActivity(i);
			}
		});
		
		csEMA.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AppInfo.SetStartManualTime(getApplicationContext(),
						Globals.SURVEY, System.currentTimeMillis());
				Intent i = new Intent(USCTeensSetupActivity.this, SurveyActivity.class);
				
				long lastTimeCompleted = AppInfo.GetLastTimeCompleted(
						USCTeensSetupActivity.this, Globals.SURVEY);
				long currentTime = System.currentTimeMillis();
				int classType = 0;
				if ((currentTime - lastTimeCompleted) < 4 * 60 * 60 * 1000) {
					classType = CSTeensSurvey.CS_EMA_DEFAULT;
				} else {
					classType = CSTeensSurvey.CS_EMA_OPTIONAL;
				}
				i.putExtra("className", CSTeensSurvey.class.getCanonicalName());
				i.putExtra(QuestionSet.TAG, new QuestionSetParamHandler(1,
						new Object[] { classType }));
				startActivity(i);
			}
		});
		
		setupdone.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();				
			}
		});

	}

	@Override
	public void onResume() {
		String startDate = DataStorage.GetValueString(getApplicationContext(), USCTeensGlobals.START_DATE, "");
		if (startDate.compareTo("") != 0) {
			String[] times = startDate.split("-");
			setStartDate.setText("Change start date (" + times[1] + "/" + times[2] + "/" + times[0] + ")");
		}
		super.onResume();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
}