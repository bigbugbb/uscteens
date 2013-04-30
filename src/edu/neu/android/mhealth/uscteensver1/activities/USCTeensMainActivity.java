package edu.neu.android.mhealth.uscteensver1.activities;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
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
import edu.neu.android.mhealth.uscteensver1.USCTeensGlobals;
import edu.neu.android.mhealth.uscteensver1.data.ChunkManager;
import edu.neu.android.mhealth.uscteensver1.data.DataSource;
import edu.neu.android.mhealth.uscteensver1.dialog.MergeDialog;
import edu.neu.android.mhealth.uscteensver1.dialog.QuestDialog;
import edu.neu.android.mhealth.uscteensver1.dialog.QuitDialog;
import edu.neu.android.mhealth.uscteensver1.pages.AppCmd;
import edu.neu.android.mhealth.uscteensver1.pages.AppPage;
import edu.neu.android.mhealth.uscteensver1.pages.AppScale;
import edu.neu.android.mhealth.uscteensver1.pages.DatePage;
import edu.neu.android.mhealth.uscteensver1.pages.GraphPage;
import edu.neu.android.mhealth.uscteensver1.pages.HomePage;
import edu.neu.android.mhealth.uscteensver1.pages.RewardPage;
import edu.neu.android.mhealth.uscteensver1.threads.GraphDrawer;
import edu.neu.android.mhealth.uscteensver1.threads.LoadDataTask;
import edu.neu.android.mhealth.uscteensver1.views.DummyView;
import edu.neu.android.mhealth.uscteensver1.views.GraphView;
import edu.neu.android.mhealth.uscteensver1.views.RewardView;
import edu.neu.android.wocketslib.Globals;
import edu.neu.android.wocketslib.activities.wocketsnews.StaffSetupActivity;
import edu.neu.android.wocketslib.support.AuthorizationChecker;
import edu.neu.android.wocketslib.support.DataStorage;
import edu.neu.android.wocketslib.utils.PasswordChecker;

public class USCTeensMainActivity extends USCTeensBaseActivity implements OnTouchListener {
	
	// the view for drawing anything
	protected GraphView mGraphView = null;
	// the view for displaying reward information
	protected RewardView mRewardView = null;
	// the view covering the screen if not authorized 
	protected DummyView mDummyView = null;
	// the view for display loading progress
	//protected ProgressView mProgressView = null;
	// all of the pages
	protected AppPage mCurPage = null;
	protected List<AppPage> mPages = new ArrayList<AppPage>();
	// special password for secret behaviors
	private PasswordChecker mPwdStaff     = new PasswordChecker(Globals.PW_STAFF_PASSWORD);
	private PasswordChecker mPwdSubject   = new PasswordChecker(Globals.PW_SUBJECT_PASSWORD);
	private PasswordChecker mPwdUninstall = new PasswordChecker("uninstall");
	// data loader
	private LoadDataTask mDataLoader = null;
	
	protected enum PageType {  
		HOME_PAGE, DATE_PAGE, GRAPH_PAGE, REWARD_PAGE
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, "MainActivity");
		setContentView(R.layout.activity_main);					

		USCTeensGlobals.sContext = getApplicationContext();
		USCTeensGlobals.sGlobalHandler = mHandler;
		DataSource.initialize(getApplicationContext());	

		// setup scale param according to the screen resolution
		setupScale();
		// get views and set listeners
		setupViews();
		// adjust layouts according to the screen resolution
		adjustLayout();	
		// create app pages and all the UIs in the pages
		initPages();	
	}
	
	@Override
	public void onDestroy() {		
		for (AppPage page : mPages) {
			try {
				page.release();				
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		}
		
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
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
        
		mDummyView = (DummyView) findViewById(R.id.view_dummy);
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
		Log.d("USCTeensMainActivity", "onPause in");
		DataSource.cancelLoading();
		
		mCurPage.pause();
		mGraphView.onPause();
		super.onPause();	
		Log.d("USCTeensMainActivity", "onPause out");
	}

	@Override
	public void onResume() {
		Log.d("USCTeensMainActivity", "onResume in");
		super.onResume();
				
		mDummyView.setVisibility(isAuthorized() ? View.GONE : View.VISIBLE);
				
		mGraphView.onResume();
		mCurPage.resume();		
		Log.d("USCTeensMainActivity", "onResume out");
	}

	@Override
	public void onStart() {
		Log.d("USCTeensMainActivity", "onStart in");
		// the initial page is not graph page, so it's ok here
		if (mCurPage == mPages.get(indexOfPage(PageType.GRAPH_PAGE))) {	
			DataSource.updateRawData();		
		}
		
		mCurPage.start();
		mGraphView.onStart(mCurPage);
		super.onStart();
		Log.d("USCTeensMainActivity", "onStart out");
	}

	@Override
	public void onStop() {	
		Log.d("USCTeensMainActivity", "onStop in");		
		mGraphView.onStop();
		mCurPage.stop();
		
		super.onStop();
		Log.d("USCTeensMainActivity", "onStop out");
	}	
	
	private boolean isAuthorized() {
		AuthorizationChecker.isAuthorized24hrs(getApplicationContext());
		
		String subjectID = DataStorage.GetValueString(getApplicationContext(), 
				DataStorage.KEY_SUBJECT_ID, AuthorizationChecker.SUBJECT_ID_UNDEFINED);
		String subjectLastName = DataStorage.GetValueString(getApplicationContext(), 
				DataStorage.KEY_LAST_NAME, AuthorizationChecker.SUBJECT_LAST_NAME_UNDEFINED);
		long dob = DataStorage.GetValueLong(getApplicationContext(), 
				DataStorage.KEY_DATE_OF_BIRTH, AuthorizationChecker.SUBJECT_DATE_OF_BIRTH_UNDEFINED);
		
		return subjectID != AuthorizationChecker.SUBJECT_ID_UNDEFINED &&
			subjectLastName != AuthorizationChecker.SUBJECT_LAST_NAME_UNDEFINED &&
			dob != AuthorizationChecker.SUBJECT_DATE_OF_BIRTH_UNDEFINED;
	}
	
	// use main looper as the default
	protected final Handler mHandler = new Handler() {	
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
        	case AppCmd.QUEST:
        		i = new Intent(USCTeensMainActivity.this, QuestDialog.class);           		
        		i.putExtra(QuestDialog.CHUNK_START_TIME, msg.arg1);
        		i.putExtra(QuestDialog.CHUNK_STOP_TIME, msg.arg2);        		
        		startActivityForResult(i, AppCmd.QUEST);
        		break;
        	case AppCmd.MERGE:
        		i = new Intent(USCTeensMainActivity.this, MergeDialog.class);
    			i.putStringArrayListExtra(MergeDialog.KEY, (ArrayList<String>) msg.obj);
    			startActivity(i);
        		break;
        	case AppCmd.QUEST_FINISHING:        	
        		((GraphPage) mCurPage).finishQuest(
        			(int) DataStorage.GetValueLong(getApplicationContext(), USCTeensGlobals.QUEST_SELECTION, 0)
        		); 
            	break;
        	case AppCmd.MERGE_FINISHING:        		
        		((GraphPage) mCurPage).finishMerge(
        			DataStorage.GetValueString(getApplicationContext(), USCTeensGlobals.MERGE_SELECTION, "")
        		);
        		break;
        	case AppCmd.DONE:
        		switchPages(indexOfPage(PageType.DATE_PAGE)); 
        		break;
        	case AppCmd.REWARD:
        		Toast.makeText(getApplicationContext(), "reward", Toast.LENGTH_LONG).show();
        		break;
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
			;
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
    
    @Override
	public boolean onTouch(View v, MotionEvent event) {
    	if (mCurPage != null) {
    		return mCurPage.onTouch(event);
    	}
		return false;
	}
    
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
//    	if (mProgressView.getVisibility() == View.VISIBLE) {
//    		if (keyCode == KeyEvent.KEYCODE_BACK) {
//    			DataSource.cancelLoading();
//    			return true;
//    		}
//    		return mProgressView.onKeyDown(keyCode, event);
//    	}    	
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
		} else if (mPwdSubject.isMatch(keyCode)) {			
			imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
			Intent i = new Intent(this, USCTeensSetupActivity.class);
			startActivity(i);
		} else if (mPwdUninstall.isMatch(keyCode)) {
			imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
			Uri packageUri = Uri.parse("package:" + Globals.PACKAGE_NAME);
			Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageUri);
			startActivity(uninstallIntent);
		}
		
		return super.onKeyDown(keyCode, event);
	}
    
//
//  It seems that onActivityResult can't work if USCTeensMainActivity 
//  has been set into singleInstance in AndroidManifest.xml.
//    
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		if (resultCode == RESULT_CANCELED) {
//    		return;
//    	} else if (requestCode == AppCmd.QUEST) {    
//        	      	        	
//        } else if (requestCode == AppCmd.MERGE) {        	
//        	        	
//        }
//    } 

}
