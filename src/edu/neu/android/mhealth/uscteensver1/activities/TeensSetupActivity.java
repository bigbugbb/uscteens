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
import edu.neu.android.mhealth.uscteensver1.survey.TeensCSSurvey;
import edu.neu.android.mhealth.uscteensver1.survey.TeensRandomSurvey;
import edu.neu.android.mhealth.uscteensver1.threads.UpdateRewardTask;
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


public class TeensSetupActivity extends BaseActivity {
    private static final String TAG = "USCTeensSetupActivity";

    private Button mBtnStartService;
    private Button mBtnSetStartDate;
    private Button mBtnEmail;
    private Button mBtnRandomEMA;
    private Button mBtnCSEMA;
    private Button mBtnRewards;
    private Button mBtnUpdateInfo;
    private Button mBtnFinishStudy;
    private Button mBtnSetupDone;

    private final static String PROMPT_TYPE_CS_TEST     = "CS-Test";
    private final static String PROMPT_TYPE_RANDOM_TEST = "Random-Test";

    private void showToastMessage(String aMsg) {
        Toast toast = Toast.makeText(getApplicationContext(), aMsg, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }

    /**
     * Set the update button on/off depending on whether the task is completed.
     */
    private class MySendAllFilesToServerTask extends SendAllFilesToServerTask {

        public MySendAllFilesToServerTask(Context context) {
            super(context);
        }

        protected void onPostExecute(Boolean isNeedUpdate) {
            super.onPostExecute(isNeedUpdate);
            mBtnFinishStudy.setEnabled(true);
        }
    }

    private class MyUpdateRewardTask extends UpdateRewardTask {

        public MyUpdateRewardTask(Context context) {
            super(context);
        }

        protected void onPostExecute(Boolean isNeedUpdate) {
            super.onPostExecute(isNeedUpdate);
            mBtnUpdateInfo.setEnabled(true);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        mBtnSetStartDate = (Button) findViewById(R.id.setstartdate);
        mBtnStartService = (Button) findViewById(R.id.startservice);
        mBtnEmail        = (Button) findViewById(R.id.setemail);       
        mBtnCSEMA        = (Button) findViewById(R.id.csema);
        mBtnRandomEMA    = (Button) findViewById(R.id.randomema);
        mBtnUpdateInfo   = (Button) findViewById(R.id.updateinfo);
        mBtnRewards      = (Button) findViewById(R.id.rewards);
        mBtnFinishStudy  = (Button) findViewById(R.id.buttonfinishstudy);
        mBtnSetupDone    = (Button) findViewById(R.id.setupdone);

        mBtnSetStartDate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent i = new Intent(getApplicationContext(), StartDateSetupActivity.class);
                startActivity(i);
            }
        });

        mBtnStartService.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {                        	
            	
                Intent i = new Intent(MonitorServiceBroadcastReceiver.TYPE_START_SENSOR_MONITOR_SERVICE_NOW);
                sendBroadcast(i);
              
                showToastMessage("Starting the service...");
            }
        });
        
        mBtnEmail.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), EmailSetupActivity.class);
				startActivity(i);
			}        	
        });

        mBtnFinishStudy.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                DataManager.listFilesInternalStorage();
                DataManager.listFilesExternalStorage();

                Log.o(TAG, Log.USER_ACTION, "Send all data and log files");
                showToastMessage("Request to finish this study, sending all data to the server now.");
                mBtnFinishStudy.setEnabled(false);
                new MySendAllFilesToServerTask(getApplicationContext()).execute();
            }
        });

        mBtnRandomEMA.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                long now = System.currentTimeMillis();
                AppInfo.SetStartManualTime(getApplicationContext(), Globals.SURVEY, now);
                Intent i = new Intent(TeensSetupActivity.this, TeensSurveyTestActivity.class);

                // Construct survey prompt event
                SurveyPromptEvent promptEvent = new SurveyPromptEvent(now, now);
                promptEvent.setPromptType(PROMPT_TYPE_RANDOM_TEST);
                promptEvent.setPromptAudio(PROMPT_AUDIO.AUDIO);
                SurveyActivity activity = TeensSurveyTestActivity.getSelf(TeensSurveyTestActivity.class);
                if (activity != null) {
                    final SurveyPromptEvent oldPromptEvent = activity.getSurveyPromptEvent();
                    promptEvent.setRepromptCount(oldPromptEvent.getRepromptCount() + 1);
                }

                i.putExtra(TeensSurveyTestActivity.SURVEY_PROMPT_EVENT, promptEvent);
                i.putExtra(QuestionSet.TAG, new QuestionSetParamHandler(
                        TeensRandomSurvey.class.getCanonicalName(), new Object[]{null}
                ));

                // Destroy the existent survey if it's not for "Random-Test";
                if (activity != null) {
                    final SurveyPromptEvent oldPromptEvent = activity.getSurveyPromptEvent();
                    assert (oldPromptEvent != null);
                    if (!oldPromptEvent.getPromptType().equals(PROMPT_TYPE_RANDOM_TEST)) {
                        activity.finish();
                    }
                }

                startActivity(i);
            }
        });

        mBtnCSEMA.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                long now = System.currentTimeMillis();
                AppInfo.SetStartManualTime(getApplicationContext(), Globals.SURVEY, now);
                Intent i = new Intent(TeensSetupActivity.this, TeensSurveyTestActivity.class);

                // Construct survey prompt event
                SurveyPromptEvent promptEvent = new SurveyPromptEvent(now, now);
                promptEvent.setPromptType(PROMPT_TYPE_CS_TEST);
                promptEvent.setPromptAudio(PROMPT_AUDIO.AUDIO);
                SurveyActivity activity = TeensSurveyTestActivity.getSelf(TeensSurveyTestActivity.class);
                if (activity != null) {
                    final SurveyPromptEvent oldPromptEvent = activity.getSurveyPromptEvent();
                    promptEvent.setRepromptCount(oldPromptEvent.getRepromptCount() + 1);
                }

                i.putExtra(TeensSurveyTestActivity.SURVEY_PROMPT_EVENT, promptEvent);
                i.putExtra(QuestionSet.TAG, new QuestionSetParamHandler(
                        TeensCSSurvey.class.getCanonicalName(), new Object[]{null}
                ));

                // Destroy the existent survey if it's not for "CS-Test";
                if (activity != null) {
                    final SurveyPromptEvent oldPromptEvent = activity.getSurveyPromptEvent();
                    assert (oldPromptEvent != null);
                    if (!oldPromptEvent.getPromptType().equals(PROMPT_TYPE_CS_TEST)) {
                        activity.finish();
                    }
                }

                startActivity(i);
            }
        });

        mBtnUpdateInfo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.o(TAG, Log.USER_ACTION, "Get udpate info");
                showToastMessage("Request to get update info, receiving all data from the server now.");
                mBtnUpdateInfo.setEnabled(false);
                new MyUpdateRewardTask(getApplicationContext()).execute();
            }
        });

        mBtnRewards.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), RewardsStateActivity.class);
                startActivity(i);
            }
        });

        mBtnSetupDone.setOnClickListener(new OnClickListener() {
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
            mBtnSetStartDate.setText("Change start date (" + times[1] + "/" + times[2] + "/" + times[0] + ")");
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
