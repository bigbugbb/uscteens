package edu.neu.android.mhealth.uscteensver1.activities;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import edu.neu.android.mhealth.uscteensver1.R;
import edu.neu.android.mhealth.uscteensver1.TeensGlobals;
import edu.neu.android.mhealth.uscteensver1.data.ChunkManager;
import edu.neu.android.mhealth.uscteensver1.data.DataSource;
import edu.neu.android.mhealth.uscteensver1.dialog.MergeDialog;
import edu.neu.android.mhealth.uscteensver1.dialog.QuestDialog;
import edu.neu.android.mhealth.uscteensver1.dialog.QuitDialog;
import edu.neu.android.mhealth.uscteensver1.extra.ActionManager;
import edu.neu.android.mhealth.uscteensver1.extra.RewardManager;
import edu.neu.android.mhealth.uscteensver1.pages.AppCmd;
import edu.neu.android.mhealth.uscteensver1.pages.AppPage;
import edu.neu.android.mhealth.uscteensver1.pages.AppScale;
import edu.neu.android.mhealth.uscteensver1.pages.DatePage;
import edu.neu.android.mhealth.uscteensver1.pages.GraphPage;
import edu.neu.android.mhealth.uscteensver1.pages.HomePage;
import edu.neu.android.mhealth.uscteensver1.pages.RewardPage;
import edu.neu.android.mhealth.uscteensver1.threads.GraphDrawer;
import edu.neu.android.mhealth.uscteensver1.threads.LoadDataTask;
import edu.neu.android.mhealth.uscteensver1.views.GraphView;
import edu.neu.android.mhealth.uscteensver1.views.RewardView;
import edu.neu.android.wocketslib.Globals;
import edu.neu.android.wocketslib.activities.wocketsnews.StaffSetupActivity;
import edu.neu.android.wocketslib.emasurvey.SurveyActivity;
import edu.neu.android.wocketslib.support.AuthorizationChecker;
import edu.neu.android.wocketslib.support.DataStorage;
import edu.neu.android.wocketslib.utils.AppUsageLogger;
import edu.neu.android.wocketslib.utils.FileHelper;
import edu.neu.android.wocketslib.utils.PasswordChecker;
import edu.neu.android.wocketslib.video.openyoutubeplayer.OpenYouTubePlayerActivity;
import edu.neu.android.wocketslib.views.DummyView;

public class TeensMainActivity extends TeensBaseActivity implements OnTouchListener {

    // the view for drawing anything
    private GraphView mGraphView = null;
    // the view for displaying reward information
    private RewardView mRewardView = null;
    // the view covering the screen if not authorized
    private DummyView mDummyView = null;
    // the view for display loading progress
    //protected ProgressView mProgressView = null;
    // all of the pages
    private AppPage mCurPage = null;
    private List<AppPage> mPages = new ArrayList<AppPage>();
    // data loader
    private LoadDataTask mDataLoader = null;
    // audio text speech
    private TextToSpeech mText2Speech;

    protected enum PageType {
        HOME_PAGE, DATE_PAGE, GRAPH_PAGE, REWARD_PAGE
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, "MainActivity");
        setContentView(R.layout.activity_main);

        // setup global params
        setupGlobal();
        // setup scale param according to the screen resolution
        setupScale();
        // get views and set listeners
        setupViews();
        // adjust layouts according to the screen resolution
        adjustLayout();
        // create app pages and all the UIs in the pages
        initPages();
        // load extra data
        loadExtra();           
    }

    @Override
    public void onDestroy() {
        for (AppPage page : mPages) {
            try {
                page.release();
            } catch (Exception e) { // not good, remove in the future
                e.printStackTrace();
            }
        }
        releaseExtra();

        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    private void setupGlobal() {
        Context context = getApplicationContext();
        TeensGlobals.sGlobalHandler = mHandler;
        TeensGlobals.MAX_LABEL_WINDOW = (int) DataStorage.GetValueLong(context, "KEY_LABEL_WINDOW", 2);

        // update flag indicating whether we should
        // copy folders from assets to external storage
        String oldVersion = DataStorage.getVersion(context, "");
        String newVersion = AppUsageLogger.getVersion(context, "USCTeens");
        DataStorage.setVersion(context, newVersion);
        TeensGlobals.sUpdateConfig = !newVersion.equals(oldVersion);
		if (TeensGlobals.sUpdateConfig) {
			String dirPath = TeensGlobals.DIRECTORY_PATH + File.separator + Globals.APP_DATA_DIRECTORY;
			//FileHelper.deleteDir(dirPath + TeensGlobals.ICON_FOLDER);	
			FileHelper.deleteDir(dirPath + TeensGlobals.REWARD_FOLDER);
		}
    }

    private void setupScale() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        AppScale.calcScale(dm.widthPixels, dm.heightPixels);
    }

    private void setupViews() {
        mGraphView = (GraphView) findViewById(R.id.view_graph);
        mGraphView.setOnTouchListener(this);
        mGraphView.setLongClickable(true);

        mRewardView = (RewardView) findViewById(R.id.view_reward);
        mDummyView  = (DummyView) findViewById(R.id.view_dummy);
        //mProgressView = (ProgressView) findViewById(R.id.view_progress);
    }

    private void adjustLayout() {
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        );
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
    }

    private void initPages() {
        // only three pages now
        Context context = getApplicationContext();
        mPages.add(new HomePage(context, mGraphView, mHandler));
        mPages.add(new DatePage(context, mGraphView, mHandler));
        mPages.add(new GraphPage(context, mGraphView, mHandler));
        mPages.add(new RewardPage(context, mGraphView, mHandler));
        mCurPage = mPages.get(indexOfPage(PageType.HOME_PAGE));
        // bind reward view to reward page
        ((RewardPage) mPages.get(indexOfPage(PageType.REWARD_PAGE))).bindRewardView(mRewardView);
        // set pages to main view
        mGraphView.setPages(mPages);
        mGraphView.setHandler(mHandler);
    }

    private void loadExtra() {
        // load activities for activity selection list
        ActionManager.start();
        // load rewards for the reward view
        RewardManager.start();

        mText2Speech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = mText2Speech.setLanguage(Locale.US);
                    if (result == TextToSpeech.LANG_MISSING_DATA ||
                            result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language is not supported");
                    }
                } else {
                    Log.e("TTS", "Initilization Failed");
                }
            }
        });
    }

    private void releaseExtra() {
        if (mText2Speech != null) {
            mText2Speech.stop();
            mText2Speech.shutdown();
        }
        ActionManager.stop();
        RewardManager.stop();
    }

    private int indexOfPage(PageType pageType) {
        int index = 0;

        switch (pageType) {
        case HOME_PAGE:
            index = 0;
            break;
        case DATE_PAGE:
            index = 1;
            break;
        case GRAPH_PAGE:
            index = 2;
            break;
        case REWARD_PAGE:
            index = 3;
            break;
        }

        return index;
    }

    public void switchPages(int pageTo) {
        if (mCurPage == mPages.get(pageTo)) {
            return;
        }

        GraphDrawer drawer = mGraphView.getDrawer();
        if (drawer != null) {
            drawer.pause(true);
        }

        // first stop to update the page
        mCurPage.pause();
        mCurPage.stop();
        // reset the current page
        mCurPage.reset();
        // get the new app page
        mCurPage = mPages.get(pageTo);
        // finally start the new game mode
        mCurPage.start();
        mCurPage.resume();

        // set the new page to graph drawer
        if (drawer != null) {
            drawer.setPage(mCurPage);
            drawer.pause(false);
        }
    }

    @Override
    public void onPause() {
        Log.d("TeensMainActivity", "onPause in");
        DataSource.cancelLoading();

        // after onPause the surface will be destroyed, 
        // so we should stop the drawing thread here instead of in the onStop.        
        mGraphView.onPause();             
        mCurPage.pause();
        super.onPause();
        Log.d("TeensMainActivity", "onPause out");
    }

    @Override
    public void onResume() {
        Log.d("TeensMainActivity", "onResume in");
        super.onResume();

        mDummyView.setVisibility(
            AuthorizationChecker.isAuthorized(TeensMainActivity.this) ? View.GONE : View.VISIBLE
        );
        
        popSurveyBack();

        mCurPage.resume();
        mGraphView.onResume();
        Log.d("TeensMainActivity", "onResume out");
    }

    @Override
    public void onStart() {
        Log.d("TeensMainActivity", "onStart in");

        // the initial page is not graph page, so it's ok here
        if (mCurPage == mPages.get(indexOfPage(PageType.GRAPH_PAGE))) {
            DataSource.updateRawData();
        }

        mCurPage.start();
        mGraphView.onStart(mCurPage);
        super.onStart();
        Log.d("TeensMainActivity", "onStart out");
    }

    @Override
    public void onStop() {
        Log.d("TeensMainActivity", "onStop in");
        mGraphView.onStop();
        mCurPage.stop();

        super.onStop();
        Log.d("TeensMainActivity", "onStop out");
    }
    
    private void popSurveyBack() {
    	SurveyActivity activity = SurveyActivity.getSelf(TeensSurveyActivity.class);
        if (activity != null) {     
        	if (activity.isFinishing()) {
        		Log.d("TeensMainActivity", "SurveyActivity is finishing");
        		return;
        	}
    		Intent i = activity.getIntent();
    		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    		i.putExtra(SurveyActivity.FORCE_POP_BACK, true);
    		i.putExtra(SurveyActivity.FORCE_UPDATE_QUESTION_LIFE, true);
    		getApplicationContext().startActivity(i);
        }
    }

    // use main looper as the default
    private final Handler mHandler = new Handler() {
        @SuppressWarnings("unchecked")
        public void handleMessage(Message msg) {
            Intent i = null;

            switch (msg.what) {
            case AppCmd.BEGIN:
                switchPages(indexOfPage(PageType.DATE_PAGE));
                break;
            case AppCmd.BEGIN_LOADING:
                onBeginLoading(msg);
                break;
            case AppCmd.END_LOADING:
                onEndLoading(msg);
                break;
            case AppCmd.BACK:
                switchPages(indexOfPage(PageType.REWARD_PAGE));
                break;
            case AppCmd.NEXT:
                switchPages(indexOfPage(PageType.REWARD_PAGE));
                break;
            case AppCmd.TUTOR:
                i = new Intent(null, Uri.parse(TeensGlobals.TUTORIAL_VIDEO_URI),
                            TeensMainActivity.this, OpenYouTubePlayerActivity.class);
                startActivity(i);
                break;
            case AppCmd.QUEST:
                i = new Intent(TeensMainActivity.this, QuestDialog.class);
                i.putExtra(QuestDialog.CHUNK_START_TIME, msg.arg1);
                i.putExtra(QuestDialog.CHUNK_STOP_TIME, msg.arg2);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(i, AppCmd.QUEST);
                break;
            case AppCmd.MERGE:
                i = new Intent(TeensMainActivity.this, MergeDialog.class);
                i.putStringArrayListExtra(MergeDialog.KEY, (ArrayList<String>) msg.obj);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                break;
            case AppCmd.QUEST_FINISHING:
                ((GraphPage) mCurPage).finishQuest(
                    DataStorage.GetValueString(getApplicationContext(), TeensGlobals.QUEST_SELECTION, "")
                );
                break;
            case AppCmd.MERGE_FINISHING:
                ((GraphPage) mCurPage).finishMerge(
                    DataStorage.GetValueString(getApplicationContext(), TeensGlobals.MERGE_SELECTION, "")
                );
                break;
            case AppCmd.DONE:
                switchPages(indexOfPage(PageType.DATE_PAGE));
                break;
            case AppCmd.REWARD:
                onReward(msg);
                break;
            case AppCmd.SCREEN_ON:            	
            	getWindow().setFlags(
            		WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            	);
            	return;
            case AppCmd.SCREEN_OFF:
            	getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            	return;
            default:
                break;
            }

            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(20);
        }
    };

    private void onBeginLoading(Message msg) {
        // avoid multiple loading operations
        if (mDataLoader != null) {
            return;
        }

        // give the loading task more cpu time
        GraphDrawer drawer = mGraphView.getDrawer();
        if (drawer != null) {
            drawer.pause(true);
        }

        // start the loading thread
        mDataLoader = (LoadDataTask) new LoadDataTask(this, mHandler).execute((String) msg.obj);
    }

    private void onEndLoading(Message msg) {
        // results from loading thread
        if (msg.arg1 == DataSource.LOADING_SUCCEEDED) {
            switchPages(indexOfPage(PageType.GRAPH_PAGE));
        } else if (msg.arg1 == DataSource.ERR_CANCELLED) {
            // nothing now
        } else if (msg.arg1 == DataSource.ERR_NO_SENSOR_DATA) {
            Toast.makeText(this, R.string.no_data, Toast.LENGTH_LONG).show();
            switchPages(indexOfPage(PageType.GRAPH_PAGE)); // still can be labelled
        } else if (msg.arg1 == DataSource.ERR_NO_CHUNK_DATA) {
            Toast.makeText(this, R.string.chunk_error, Toast.LENGTH_LONG).show();
        } else if (msg.arg1 == DataSource.ERR_WAITING_SENSOR_DATA) {
            Toast.makeText(this, R.string.wait_data, Toast.LENGTH_LONG).show();
        }
        mDataLoader = null;

        // make sure the drawer is working again
        GraphDrawer drawer = mGraphView.getDrawer();
        if (drawer != null) {
            drawer.setPage(mCurPage);
            drawer.pause(false);
        }
    }

    private void onReward(Message msg) {                               
        //saveToClipBoard(mRewardView.getClaimCode());
        if (msg.obj != null) {
        	String[] rewardInfo = (String[]) msg.obj;
            Intent i = new Intent("android.intent.action.VIEW", Uri.parse(rewardInfo[1]));            
            startActivity(i);
        }                
    }        
    
//    private void saveToClipBoard(String content) {
//    	ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);    	
////    	ClipData clip = ClipData.newPlainText("label", "Text to copy");
////    	clipboard.setPrimaryClip(clip);
//    	clipboard.setText(content);
//
//    	String msg = content + " has been copied to clipboard.";
//    	Toast toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG);
//        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
//        toast.show();
//    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {    	
        if (mCurPage != null) {
        	GraphDrawer drawer = mGraphView.getDrawer();
            if (drawer != null) {
                drawer.interrupt();
            }
            return mCurPage.onTouch(event);
        }
        return false;
    }

    // special password for secret behaviors
    private PasswordChecker mPwdStaff     = new PasswordChecker(Globals.PW_STAFF_PASSWORD);
    private PasswordChecker mPwdSubject   = new PasswordChecker(Globals.PW_SUBJECT_PASSWORD);
    private PasswordChecker mPwdSetup     = new PasswordChecker("sss");
    private PasswordChecker mPwdTeens     = new PasswordChecker("teens");
    private PasswordChecker mPwdEternal   = new PasswordChecker("eternal");
    private PasswordChecker mPwdMortal    = new PasswordChecker("mortal");
    private PasswordChecker mPwdUninstall = new PasswordChecker("uninstall");

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {  
    	
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mCurPage == mPages.get(indexOfPage(PageType.HOME_PAGE))) { // home page
                QuitDialog dialog = new QuitDialog();
                dialog.show(getSupportFragmentManager(), "HomePageDialog");
            } else if (mCurPage == mPages.get(indexOfPage(PageType.DATE_PAGE))) {
                if (mDataLoader != null) {
                    DataSource.cancelLoading();
                }
                switchPages(indexOfPage(PageType.HOME_PAGE));
            } else if (mCurPage == mPages.get(indexOfPage(PageType.GRAPH_PAGE))) {
                if (ChunkManager.areAllChunksLabelled()) {
                    switchPages(indexOfPage(PageType.REWARD_PAGE));
                } else {
                    switchPages(indexOfPage(PageType.DATE_PAGE));
                }
            } else if (mCurPage == mPages.get(indexOfPage(PageType.REWARD_PAGE))) {
                switchPages(indexOfPage(PageType.DATE_PAGE));
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_MENU) {
            // pop up the keyboard only in the home page
            if (mCurPage == mPages.get(indexOfPage(PageType.HOME_PAGE))) {
                imm.toggleSoftInput(0, 0);
            }
        }

        if (mPwdStaff.isMatch(keyCode)) {
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            Intent i = new Intent(this, StaffSetupActivity.class);
            startActivity(i);
        } else if (mPwdSubject.isMatch(keyCode) || mPwdSetup.isMatch(keyCode)) {
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            Intent i = new Intent(this, TeensSetupActivity.class);
            startActivity(i);
        } else if (mPwdUninstall.isMatch(keyCode)) {
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            Uri packageUri = Uri.parse("package:" + Globals.PACKAGE_NAME);
            Intent i = new Intent(Intent.ACTION_DELETE, packageUri);
            startActivity(i);
        } else if (mPwdTeens.isMatch(keyCode)) {
            mText2Speech.speak("Welcome to use teens activity game!", TextToSpeech.QUEUE_FLUSH, null);
        } else if (mPwdEternal.isMatch(keyCode)) {
        	Globals.MIN_MS_FOR_SENSING_WHEN_PHONE_PLUGGED_IN = 50000;
        	DataStorage.SetValue(getApplicationContext(), "MIN_MS_FOR_SENSING_WHEN_PHONE_PLUGGED_IN", 
        			Globals.MIN_MS_FOR_SENSING_WHEN_PHONE_PLUGGED_IN);
        	mText2Speech.speak("Record raw data per second", TextToSpeech.QUEUE_FLUSH, null);
        } else if (mPwdMortal.isMatch(keyCode)) {
        	Globals.MIN_MS_FOR_SENSING_WHEN_PHONE_PLUGGED_IN = 21000;
        	DataStorage.SetValue(getApplicationContext(), "MIN_MS_FOR_SENSING_WHEN_PHONE_PLUGGED_IN", 
        			Globals.MIN_MS_FOR_SENSING_WHEN_PHONE_PLUGGED_IN);
        	mText2Speech.speak("Record 20 seconds of raw data per minute", TextToSpeech.QUEUE_FLUSH, null);
        }

        return super.onKeyDown(keyCode, event);
    }
}
