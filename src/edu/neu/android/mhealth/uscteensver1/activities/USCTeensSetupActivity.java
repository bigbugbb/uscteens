package edu.neu.android.mhealth.uscteensver1.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import edu.neu.android.mhealth.uscteensver1.R;
import edu.neu.android.mhealth.uscteensver1.survey.CSTeensSurvey;
import edu.neu.android.mhealth.uscteensver1.survey.RandomTeensSurvey;
import edu.neu.android.mhealth.uscteensver1.video.VideoActivity;
import edu.neu.android.wocketslib.Globals;
import edu.neu.android.wocketslib.broadcastreceivers.MonitorServiceBroadcastReceiver;
import edu.neu.android.wocketslib.dataupload.DataManager;
import edu.neu.android.wocketslib.dataupload.SendAllFilesToServerTask;
import edu.neu.android.wocketslib.emasurvey.SurveyActivity;
import edu.neu.android.wocketslib.emasurvey.model.QuestionSet;
import edu.neu.android.wocketslib.emasurvey.model.QuestionSetParamHandler;
import edu.neu.android.wocketslib.emasurvey.model.SurveyPromptEvent;
import edu.neu.android.wocketslib.emasurvey.model.SurveyPromptEvent.PROMPT_AUDIO;
import edu.neu.android.wocketslib.support.AppInfo;
import edu.neu.android.wocketslib.support.DataStorage;
import edu.neu.android.wocketslib.utils.BaseActivity;
import edu.neu.android.wocketslib.utils.Log;


public class USCTeensSetupActivity extends BaseActivity {
	private static final String TAG = "SetupTeenGameActivity"; 
	public static final String KEY_RESCUE_INHALER = "_KEY_RESCUE_INHALER";
	private Button startService;
	private Button setStartDate;
	private Button randomEMA;
	private Button csEMA;
	private Button rewards;
	private Button tutorial;
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
	private class MySendAllFilesToServerTask extends SendAllFilesToServerTask {

		public MySendAllFilesToServerTask(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}

		protected void onPostExecute(Boolean isNeedUpdate) {
			super.onPostExecute(isNeedUpdate);
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
		randomEMA    = (Button) findViewById(R.id.randomema);
		tutorial 	 = (Button) findViewById(R.id.tutorial);
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
				Intent i = new Intent(MonitorServiceBroadcastReceiver.TYPE_START_SENSOR_MONITOR_SERVICE_NOW);
				sendBroadcast(i);
				Toast toast = Toast.makeText(
					getApplicationContext(), "Starting the service...", Toast.LENGTH_LONG
				);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
			}
		});
		
		finishStudy.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				DataManager.listFilesInternalStorage();
				DataManager.listFilesExternalStorage();

				Log.o(TAG, Log.USER_ACTION, "Send all data and log files");
				displayToastMessage("Request to finish this study, sending all data to the server now.");
				finishStudy.setEnabled(false);
				new MySendAllFilesToServerTask(getApplicationContext()).execute();
			}
		});
		
		randomEMA.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				long now = System.currentTimeMillis();
				AppInfo.SetStartManualTime(getApplicationContext(), Globals.SURVEY, now);
				Intent i = new Intent(USCTeensSetupActivity.this, SurveyActivity.class);
				
				// Construct survey prompt event
				SurveyPromptEvent promptEvent = new SurveyPromptEvent(now, now);				
				promptEvent.setPromptType("Random-Test");				
				promptEvent.setPromptAudio(PROMPT_AUDIO.AUDIO);
				promptEvent.setReprompt(USCTeensSurveyActivity.isWorking());
				
				i.putExtra(USCTeensSurveyActivity.PROMPT_EVENT, promptEvent);
				i.putExtra(QuestionSet.TAG, new QuestionSetParamHandler(
					RandomTeensSurvey.class.getCanonicalName(), 1, new Object[] { 0 }
				));
				startActivity(i);				
			}
		});
		
		csEMA.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				long now = System.currentTimeMillis();
				AppInfo.SetStartManualTime(getApplicationContext(), Globals.SURVEY, now);
				Intent i = new Intent(USCTeensSetupActivity.this, SurveyActivity.class);
				
				// Construct survey prompt event
				SurveyPromptEvent promptEvent = new SurveyPromptEvent(now, now);				
				promptEvent.setPromptType("CS-Test");				
				promptEvent.setPromptAudio(PROMPT_AUDIO.AUDIO);
				promptEvent.setReprompt(USCTeensSurveyActivity.isWorking());
				
				i.putExtra(USCTeensSurveyActivity.PROMPT_EVENT, promptEvent);
				i.putExtra(QuestionSet.TAG, new QuestionSetParamHandler(
					CSTeensSurvey.class.getCanonicalName(), 1, new Object[] { null }
				));
				startActivity(i);					
			}
		});
		
		tutorial.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), VideoActivity.class);
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
