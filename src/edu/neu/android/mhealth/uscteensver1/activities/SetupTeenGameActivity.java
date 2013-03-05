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

public class SetupTeenGameActivity extends BaseActivity {
	private static final String TAG = "SetupTeenGameActivity"; 
	public static final String KEY_RESCUE_INHALER = "_KEY_RESCUE_INHALER";
	private Button startService;
	private Button setStartDate;
	private Button randomEMA;
	private Button csEMA;
	private Button finishStudy;
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
			String msg = "Finish study- Starting data and log files upload";
			//Move JSON to external upload folder
			RawUploader.moveDataToExternal(SetupTeenGameActivity.this, false, true, true, .85);
			//Move Log files to external upload folder
			DataSender.sendLogsToExternalUploadDir(SetupTeenGameActivity.this, true);
			//Move Survey Log files to upload folder
			//DataSender.sendSurveyLogsToExternalUploadDir(SetupActivity.this, true);
			//Transmit Note first
			ServerLogger.transmitOrQueueNote(SetupTeenGameActivity.this, msg, true);
			//Upload JSON files and remove
			int filesRemaining = RawUploader.uploadDataFromExternal(SetupTeenGameActivity.this,
					true, true, true, false, .85);
			//Upload Log and SurveyLog files, backup and remove
			filesRemaining = RawUploader.uploadDataFromExternal(SetupTeenGameActivity.this,
					false, true, true, true, .85);
			//Upload possible remaining files in the internal memory
			filesRemaining = RawUploader.uploadDataFromInternal(SetupTeenGameActivity.this,
					false, true, true, false, .85);

			msg = "Completed user-initiated file upload after "
					+ String.format(
							"%.1f",
							((System.currentTimeMillis() - currentTime) / 1000.0 / 60.0))
					+ " minutes. Files remaining to upload: " + filesRemaining;
			ServerLogger.sendNote(SetupTeenGameActivity.this, msg, true);
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
//		uploadLogs = (Button) findViewById(R.id.buttonuploadlogs);

		setStartDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(getApplicationContext(), SetStartDateActivity.class);
				startActivity(i);				
			}
		});
		
		startService.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent bintent = new Intent(
						MonitorServiceBroadcastReceiver.TYPE_START_SENSOR_MONITOR_SERVICE_NOW);
				sendBroadcast(bintent);
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
		
//		checkInhalers.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				DataStore.init(getApplicationContext());
//
//				String asthmaSensors = "Asthmapolis sensors found: \n";
//				for (int x = 0; x < DataStore.mSensors.size(); x++) {
//					if (DataStore.mSensors.get(x).mType == Sensor.ASTHMA) {
//						asthmaSensors = asthmaSensors
//								+ DataStore.mSensors.get(x).mAddress + "\n";
//					}
//				}
//				Toast.makeText(getApplicationContext(), asthmaSensors,
//						Toast.LENGTH_LONG).show();
//			}
//		});
//
//		rescueInhaler.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				new DetectInhalerAsyncTask().execute(null);
//			}
//		});

		randomEMA.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				AppInfo.SetStartManualTime(getApplicationContext(),
//						Globals.SURVEY, System.currentTimeMillis());
//				Intent i = new Intent(SetupActivity.this, SurveyActivity.class);
//				
//				long lastTimeCompleted = AppInfo.GetLastTimeCompleted(
//						SetupActivity.this, Globals.SURVEY);
//				long currentTime = System.currentTimeMillis();
//				int classType = 0;
//				if ((currentTime - lastTimeCompleted) < 4 * 60 * 60 * 1000) {
//					classType = RandomAsthmaSurveyQuestionSet.RANDOM_EMA_DEFAULT;
//				} else {
//					classType = RandomAsthmaSurveyQuestionSet.RANDOM_EMA_OPTIONAL;
//				}
//				i.putExtra("className",
//						RandomAsthmaSurveyQuestionSet.class.getCanonicalName());
//				i.putExtra(QuestionSet.TAG, new QuestionSetParamHandler(1,
//						new Object[] { classType }));
//				startActivity(i);
			}
		});
		csEMA.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				AppInfo.SetStartManualTime(getApplicationContext(),
//						Globals.SURVEY, System.currentTimeMillis());
//				Intent i = new Intent(SetupActivity.this, SurveyActivity.class);
//				
//				long lastTimeCompleted = AppInfo.GetLastTimeCompleted(
//						SetupActivity.this, Globals.SURVEY);
//				long currentTime = System.currentTimeMillis();
//				int classType = 0;
//				if ((currentTime - lastTimeCompleted) < 4 * 60 * 60 * 1000) {
//					classType = CSAsthmaSurvey.CS_EMA_DEFAULT;
//				} else {
//					classType = CSAsthmaSurvey.CS_EMA_OPTIONAL;
//				}
//				i.putExtra("className", CSAsthmaSurvey.class.getCanonicalName());
//				i.putExtra(QuestionSet.TAG, new QuestionSetParamHandler(1,
//						new Object[] { classType }));
//				startActivity(i);
			}
		});

	}

	class DetectInhalerAsyncTask extends AsyncTask<Void, String, Void> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
//			if (setupRescueInhaler == null) {
//				View setrescueView = SetupActivity.this
//						.getLayoutInflater().inflate(
//								R.layout.setrescueinhalerdialog, null);
//				setupRescueInhaler = new AlertDialog.Builder(
//						SetupActivity.this)
//						.setView(setrescueView)
//						.setTitle("Setup Rescue inhaler")
//						.setPositiveButton("Cancel",
//								new DialogInterface.OnClickListener() {
//
//									@Override
//									public void onClick(DialogInterface dialog,
//											int which) {
//										// TODO Auto-generated method stub
//										dialog.dismiss();
//									}
//								}).create();
//				textDisplay = (LinearLayout) setrescueView
//						.findViewById(R.id.dataDisplay);
//				scrollView = (ScrollView) setrescueView
//						.findViewById(R.id.scrollview);
//			}
//			textDisplay.removeAllViews();
//			setupRescueInhaler.show();
		}

		@Override
		protected void onProgressUpdate(String... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
//			if (textDisplay == null)
//				return;
//			TextView fileView = new TextView(SetupActivity.this);
//			fileView.setLayoutParams(new LayoutParams(
//					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
//			fileView.setPadding(5, 1, 5, 0);
//			fileView.setText(values[0]);
//			fileView.setTextSize(14);
//			fileView.setTextColor(Color.BLACK);
//			textDisplay.addView(fileView);
//			scrollView.fullScroll(ScrollView.FOCUS_DOWN);
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
//			String inhalerMAC = "";
//			if ((inhalerMAC = rescueInhalerMAC(SetupActivity.this)) != null) {
//				publishProgress("Rescue inhaler exists.");
//			}
//			publishProgress("Please push the button on the rescue inhaler.");
//			LogcatReader.clearLogcat();
//			publishProgress("Waiting for connection...");
//			while (setupRescueInhaler.isShowing()) {
//				if ((inhalerMAC = inhalerDetectedMAC()) != null) {
//					publishProgress("Inhaler detected!!\nRescue inhaler "+inhalerMAC+" setup!!");
//					setRescueInhalerMAC(inhalerMAC);
//					break;
//				} else {
//					publishProgress("Inhaler not detected, please press again.");
//				}
//				try {
//					Thread.currentThread().sleep(500);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
			return null;
		}

	}

	private String inhalerDetectedMAC() {
		// Here to check if the rescue inhaler goes off

		String nameColon = null;
		String nameUnder = null;
		String address = null;

		String asthmaSensors = "Asthapolis sensors found: \n";
		for (int x = 0; x < DataStore.mSensors.size(); x++) {
			if (DataStore.mSensors.get(x).mType == Sensor.ASTHMA) {
				address = DataStore.mSensors.get(x).mAddress;
				nameColon = Util.insertColons(address);
				nameUnder = Util.insertUnderscores(address);

				if ((LogcatReader.isInLogcat(nameColon, false))
						|| (LogcatReader.isInLogcat(nameUnder, false))) {
					return address;
				}
				
			}
		}
		LogcatReader.clearLogcat();
		return null;
	}

	public static String rescueInhalerMAC(Context c) {
		return DataStorage.GetValueString(c, 
				KEY_RESCUE_INHALER, null);
	}
	private void setRescueInhalerMAC(String mac){
		DataStorage.SetValue(SetupTeenGameActivity.this, 
				KEY_RESCUE_INHALER, mac);
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
