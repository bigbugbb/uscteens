package edu.neu.android.mhealth.uscteensver1.activities;

import java.util.ArrayList;
import java.util.List;

import edu.neu.android.mhealth.uscteensver1.LibraryGlobals;
import edu.neu.android.mhealth.uscteensver1.R;
import edu.neu.android.mhealth.uscteensver1.broadcastreceivers.TeensBroadcastReceiver;
import edu.neu.android.mhealth.uscteensver1.data.DataSource;
import edu.neu.android.mhealth.uscteensver1.dialog.ActionsDialog;
import edu.neu.android.mhealth.uscteensver1.dialog.HomePageDialog;
import edu.neu.android.mhealth.uscteensver1.dialog.WarningDialog;
import edu.neu.android.mhealth.uscteensver1.pages.AppCmd;
import edu.neu.android.mhealth.uscteensver1.pages.AppObject;
import edu.neu.android.mhealth.uscteensver1.pages.AppPage;
import edu.neu.android.mhealth.uscteensver1.pages.AppScale;
import edu.neu.android.mhealth.uscteensver1.pages.HomePage;
import edu.neu.android.mhealth.uscteensver1.pages.MainPage;
import edu.neu.android.mhealth.uscteensver1.pages.WeekdayPage;
import edu.neu.android.mhealth.uscteensver1.pages.WinPage;
import edu.neu.android.mhealth.uscteensver1.threads.GraphDrawer;
import edu.neu.android.mhealth.uscteensver1.views.MainView;
import edu.neu.android.wocketslib.Globals;
import edu.neu.android.wocketslib.activities.wocketsnews.StaffSetupActivity;
import edu.neu.android.wocketslib.broadcastreceivers.MonitorServiceBroadcastReceiver;
import edu.neu.android.wocketslib.utils.BaseActivity;
import edu.neu.android.wocketslib.utils.FileHelper;
import edu.neu.android.wocketslib.utils.PasswordChecker;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

public class MainActivity extends MyBaseActivity implements OnTouchListener {
	
	protected MainView 		mMainView 	   = null;
//	protected SensorManager mSensorManager = null;	
	// all of the pages
	protected AppPage mCurPage = null;
	protected List<AppPage> mPages = new ArrayList<AppPage>();
	// data manager
	protected DataSource mDataSource = DataSource.getInstance(this);
	// special password for secret behaviors
	private PasswordChecker pwStaff = new PasswordChecker(Globals.PW_STAFF_PASSWORD);
	private PasswordChecker pwSubject = new PasswordChecker(Globals.PW_SUBJECT_PASSWORD);
	private PasswordChecker pwUninstall = new PasswordChecker("uninstall");
	
	protected enum PageType {  
		HOME_PAGE, WEEKDAY_PAGE, MAIN_PAGE, WIN_PAGE 
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, "MainActivity");
		setContentView(R.layout.activity_main);								
		
		// send a broadcast to start the monitor service
		startMonitor();
		// setup scale param according to the screen resolution
		setupScale();
		// get views and set listeners
		setupViews();
		// adjust layouts according to the screen resolution
		adjustLayout();
		// create accelerometer
//		createSensor();			
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
	
	private void startMonitor() {
		Intent bintent = new Intent(
				TeensBroadcastReceiver.TYPE_START_SENSOR_MONITOR_SERVICE_NOW);
		sendBroadcast(bintent);
//		Toast.makeText(getApplicationContext(),
//				"Starting the service...", Toast.LENGTH_LONG).show();
	}
	
	private void setupScale() {
		DisplayMetrics dm = new DisplayMetrics();  
        getWindowManager().getDefaultDisplay().getMetrics(dm);	
        
		AppScale appScale = AppScale.getInstance();
		appScale.calcScale(dm.widthPixels, dm.heightPixels);
		AppObject.setAppScale(appScale);
	}

	private void setupViews() {
		mMainView = (MainView) findViewById(R.id.view_main);		
		mMainView.setOnTouchListener(this);
		mMainView.setLongClickable(true);
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
	
//	private void createSensor() {
//		// get system sensor manager to deal with sensor issues  
//        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
//	}

	private void initPages() {		
		// only three pages now		
		mPages.add(new HomePage(this, mMainView, mHandler));
		mPages.add(new WeekdayPage(this, mMainView, mHandler));
		mPages.add(new MainPage(this, mMainView, mHandler));
		mPages.add(new WinPage(this, mMainView, mHandler));
		mCurPage = mPages.get(indexOfPage(PageType.HOME_PAGE));
		// set pages to main view
		mMainView.setPages(mPages);		
	}
	
	private int indexOfPage(PageType pageType) {
		int index = 0;
		
		switch (pageType) {
		case HOME_PAGE:
			index = 0;
			break;
		case WEEKDAY_PAGE:
			index = 1;
			break;
		case MAIN_PAGE:
			index = 2;
			break;
		case WIN_PAGE:
			index = 3;
			break;
		}
		
		return index;
	}

	public void switchPages(int pageTo) {
		if (mCurPage == mPages.get(pageTo)) {
			return;
		}		
		
		GraphDrawer drawer = mMainView.getDrawer();
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
		mCurPage.pause();
		mMainView.onPause();
		super.onPause();		
	}

	@Override
	public void onResume() {		
		super.onResume();	
		mMainView.onResume();
		mCurPage.resume();
		
		if (!mDataSource.initialize()) {
			// if the initialize fail, it means the config file does not exist, so create it and initialize again		
//			Intent i = new Intent(getApplicationContext(), ConfigDialog.class);			
//			startActivityForResult(i, AppCmd.CONFIG);
			//mInputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
			//mInputMethodManager.showSoftInput(arg0, 0);
		}
	}

	@Override
	public void onStart() {
		mCurPage.start();
		mMainView.onStart(mCurPage);
		super.onStart();
	}

	@Override
	public void onStop() {		
		mMainView.onStop();
		mCurPage.stop();
		super.onStop();
	}	
	
	// use main looper as the default
	protected final Handler mHandler = new Handler() {	
		public void handleMessage(Message msg) {        					
			Intent i = null;		
			
        	switch (msg.what) {   
        	case AppCmd.BEGIN:        
        		if (mDataSource.isInitialized()) {
        			switchPages(indexOfPage(PageType.WEEKDAY_PAGE));
        		} else {
        			Toast.makeText(getApplicationContext(), "Fail to do the configuration!", Toast.LENGTH_SHORT);
        		}
        		break;
        	case AppCmd.WEEKDAY:
         		if (mDataSource.loadRawData((String) msg.obj)) {        			
            		switchPages(2);
        		}
            	break;
        	case AppCmd.BACK:
        		switchPages(indexOfPage(PageType.WEEKDAY_PAGE));
        		break;
        	case AppCmd.NEXT:
        		switchPages(indexOfPage(PageType.WEEKDAY_PAGE));
        		break;
        	case AppCmd.QUEST:
        		i = new Intent(getApplicationContext(), ActionsDialog.class);           		
        		i.putExtra(ActionsDialog.CHUNK_START_TIME, msg.arg1);
        		i.putExtra(ActionsDialog.CHUNK_STOP_TIME, msg.arg2);
        		startActivityForResult(i, AppCmd.QUEST);
        		break;
        	case AppCmd.MERGE:
        		i = new Intent(getApplicationContext(), WarningDialog.class);
    			i.putStringArrayListExtra(WarningDialog.KEY, (ArrayList<String>) msg.obj);
    			startActivityForResult(i, AppCmd.MERGE);
        		break;
            default:
            	break;
            }            
        	
        	Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
			vibrator.vibrate(20);
        }			
    };
    
    @Override
	public boolean onTouch(View v, MotionEvent event) {
    	if (mCurPage != null) {
    		return mCurPage.onTouch(event);
    	}
		return false;
	}
    
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
    	
		if (keyCode == KeyEvent.KEYCODE_BACK) {		
			if (mCurPage == mPages.get(0)) { // home page
				HomePageDialog dialog = new HomePageDialog();
				dialog.show(getSupportFragmentManager(), "HomePageDialog");
			} else if (mCurPage == mPages.get(indexOfPage(PageType.WEEKDAY_PAGE))) {
				switchPages(indexOfPage(PageType.HOME_PAGE));				
			} else if (mCurPage == mPages.get(indexOfPage(PageType.MAIN_PAGE))) {
				switchPages(indexOfPage(PageType.WEEKDAY_PAGE));
			}
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_MENU) {
			if (!FileHelper.isFileExists(DataSource.PATH_PREFIX + "config.xml")) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(0, 0);
			}	
		}
		
		if (pwStaff.isMatch(keyCode)) {
			Intent i = new Intent(this, StaffSetupActivity.class);
			startActivity(i);
		} else if (pwSubject.isMatch(keyCode)) {
			Intent i = new Intent(this, SetupTeenGameActivity.class);
			startActivity(i);
		} else if (pwUninstall.isMatch(keyCode)) {
			Uri packageUri = Uri.parse("package:" + Globals.PACKAGE_NAME);
			Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageUri);
			startActivity(uninstallIntent);
		}
		
		return super.onKeyDown(keyCode, event);
	}
    
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_CANCELED) {
    		return;
    	} else if (requestCode == AppCmd.QUEST) {    
        	MainPage page = (MainPage) mCurPage;
        	page.finishQuest(resultCode - 1);       	        	
        } else if (requestCode == AppCmd.MERGE) {        	
        	MainPage page = (MainPage) mCurPage;
        	page.finishMerge(data.getStringExtra(WarningDialog.SELECTION));        	
        }
    } 
	
	
}
