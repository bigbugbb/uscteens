package edu.neu.android.mhealth.uscteensver1.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import edu.neu.android.mhealth.uscteensver1.R;
import edu.neu.android.mhealth.uscteensver1.survey.EMAQuestionSet;
import edu.neu.android.wocketslib.Globals;
import edu.neu.android.wocketslib.broadcastreceivers.MonitorServiceBroadcastReceiver;
import edu.neu.android.wocketslib.emasurvey.model.QuestionSet;
import edu.neu.android.wocketslib.emasurvey.model.QuestionSetParamHandler;
import edu.neu.android.wocketslib.support.AppInfo;
import edu.neu.android.wocketslib.support.DataStorage;
import edu.neu.android.wocketslib.utils.BaseActivity;


public class USCTeensSetupActivity extends BaseActivity {
	private static final String TAG = "SetupTeenGameActivity"; 
	public static final String KEY_RESCUE_INHALER = "_KEY_RESCUE_INHALER";
	private Button startService;
	private Button setStartDate;
//	private Button randomEMA;
	private Button csEMA;
	private Button rewards;
	private Button finishStudy;
	private Button setupdone;

	private void displayToastMessage(String aMsg) {
		Toast aToast = Toast.makeText(getApplicationContext(),aMsg, Toast.LENGTH_LONG);
		aToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
		aToast.show();	
	}
	
	/**
	 * Set the update button on/off depending on if the code detects that the software
	 * is or is not at the latest version on the Android Market. 
	 */
	/*
	private class SendAllFilesToServerTask extends AsyncTask<Void, Void, Boolean> { 
		@Override
		protected Boolean doInBackground(Void... params) {

			// send JSON file
			long currentTime = System.currentTimeMillis();
			String msg = "Finish study- Starting data and log files upload";
			//Transmit Note first
			ServerLogger.transmitOrQueueNote(USCTeensSetupActivity.this, msg, true);

			//Move standard log files to internal upload folder
			DataSender.sendLogsToInternalUploadDir(USCTeensSetupActivity.this, true, false); 

//			//Move survey log files to internal upload folder
//			DataSender.sendInternalSurveyLogsToInternalUploadDir(SetupInhalerActivity.this, true, false); 

			//Move survey log files to internal upload folder
			DataSender.sendExternalSurveyLogsToExternalUploadDir(USCTeensSetupActivity.this, true, false); 

			//Move survey log files to internal upload folder
			DataSender.sendInternalDataLogsToInternalUploadDir(USCTeensSetupActivity.this, true, false); 
			
//			//Move Survey Log files to upload folder
//			DataSender.sendSurveyLogsToExternalUploadDir(SetupInhalerActivity.this, true);
			
			//Move all data in the internal upload queue to the external upload queue and zip if needed 
			if (Globals.IS_DEBUG)
				Log.i(TAG, "Move and zip internal upload dir data to external upload directory");
			DataSender.sendInternalUploadDataToExternalUploadDir(USCTeensSetupActivity.this, false, true);

//			Log.d(TAG, "WHATS LEFT ----------------------------------------------------------");
//			DataManager.listFilesInternalStorage();
//			DataManager.listFilesExternalStorage();

			//Upload JSON files and remove
			int filesRemaining = RawUploader.uploadDataFromExternalUploadDir(USCTeensSetupActivity.this,
					true, true, true, false, .85, true);

			//Upload Log and SurveyLog files, backup and remove
			filesRemaining = RawUploader.uploadDataFromExternalUploadDir(USCTeensSetupActivity.this,
					false, true, true, true, .85, true);

			//Upload possible remaining files in the internal memory
			filesRemaining = RawUploader.uploadDataFromInternalDir(USCTeensSetupActivity.this,
					false, true, true, false, .85, true);

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
	}*/

	/**
	 * Set the update button on/off depending on if the code detects that the software
	 * is or is not at the latest version on the Android Market. 
	 */
//	private class SendLogFilesTask extends AsyncTask<Void, Void, Boolean> 
//	{ 
//		@Override
//		protected Boolean doInBackground(Void... params) {
//
//			return true; 
//		}
//
//		protected void onPostExecute(Boolean isNeedUpdate) {
//			displayToastMessage("Transmission complete.");
//			finishStudy.setEnabled(true);
//		}
//	}

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup);
		setStartDate = (Button) findViewById(R.id.setstartdate);
		startService = (Button) findViewById(R.id.startservice);		
		csEMA        = (Button) findViewById(R.id.csema);		
//		randomEMA    = (Button) findViewById(R.id.randomema);
		rewards      = (Button) findViewById(R.id.rewards);
		finishStudy  = (Button) findViewById(R.id.buttonfinishstudy);
		setupdone    = (Button) findViewById(R.id.setupdone);

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
				
			//	Log.i(TAG, "Send all data and log files");
			//	displayToastMessage("Request to finish this study, sending all data to the server now.");
			//	finishStudy.setEnabled(false);
			//	new SendAllFilesToServerTask().execute();
			}
		});
		
//		randomEMA.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				AppInfo.SetStartManualTime(getApplicationContext(),
//						Globals.SURVEY, System.currentTimeMillis());
//				Intent i = new Intent(USCTeensSetupActivity.this, SurveyActivity.class);
//				
//				long lastTimeCompleted = AppInfo.GetLastTimeCompleted(
//						USCTeensSetupActivity.this, Globals.SURVEY);
//				long currentTime = System.currentTimeMillis();
//				int classType = 0;
//				if ((currentTime - lastTimeCompleted) < 4 * 60 * 60 * 1000) {
//					classType = RandomTeensSurveyQuestionSet.RANDOM_EMA_DEFAULT;
//				} else {
//					classType = RandomTeensSurveyQuestionSet.RANDOM_EMA_OPTIONAL;
//				}
//				i.putExtra("className",
//						RandomTeensSurveyQuestionSet.class.getCanonicalName());
//				i.putExtra(QuestionSet.TAG, new QuestionSetParamHandler(1,
//						new Object[] { classType }));
//				startActivity(i);
//			}
//		});
		
		csEMA.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(USCTeensSetupActivity.this, USCTeensSurveyActivity.class);
				long lastTimeCompleted = AppInfo.GetLastTimeCompleted(USCTeensSetupActivity.this, Globals.SURVEY);
				long currentTime = System.currentTimeMillis();
				int classType = 0;
				if((currentTime - lastTimeCompleted) < 4*60*60*1000){
					classType = EMAQuestionSet.EMA_DEFAULT;
				}
				else{
					classType = EMAQuestionSet.EMA_OPTIONAL;
				}
				i.putExtra("className", EMAQuestionSet.class.getCanonicalName());
				i.putExtra(QuestionSet.TAG, new QuestionSetParamHandler(1,new Object[]{classType}));
				startActivity(i);
			}
		});
		
		rewards.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), RewardsStateActivity.class);
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
		String startDate = DataStorage.getStartDate(getApplicationContext(), "");
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
