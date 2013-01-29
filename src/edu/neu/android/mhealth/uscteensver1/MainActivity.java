package edu.neu.android.mhealth.uscteensver1;

import java.util.ArrayList;
import java.util.List;

import edu.neu.android.mhealth.uscteensver1.R;
import edu.neu.android.mhealth.uscteensver1.data.ChunkManager;
import edu.neu.android.mhealth.uscteensver1.data.DataSource;
import edu.neu.android.mhealth.uscteensver1.data.NativeDataSource;
import edu.neu.android.mhealth.uscteensver1.dialog.HomePageDialog;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;

public class MainActivity extends FragmentActivity implements OnTouchListener {
	
	protected MainView 		mMainView 	   = null;
	protected SensorManager mSensorManager = null;	
	// all of the pages
	protected AppPage mCurPage = null;
	protected List<AppPage> mPages = new ArrayList<AppPage>();
	// chunk manager
	protected ChunkManager mChunkManager = null;
	// data manager
	protected DataSource mDataSource = null;
	// native data source, will replace DataSource in the future
	protected NativeDataSource mNativeDataSource = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// get views and set listeners
		setupViews();
		// adjust layouts according to the screen resolution
		adjustLayout();
		// create accelerometer
		createSensor();		
		// create app pages and all the UIs in the pages
		initPages();	
		// init chunk manager and other things
		initOthers();
	}
	
	@Override
	protected void onDestroy() {		
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
	
	private void createSensor() {
		// get system sensor manager to deal with sensor issues  
        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
	}
	
	private void initOthers() {
		mDataSource   = DataSource.getInstance(this);
		mChunkManager = ChunkManager.getInstance(this);
		mChunkManager.setUserData(mPages.get(2));
		
		mNativeDataSource = NativeDataSource.getDataSource();
	}
	
	private void initPages() {
		// only three pages now		
		mPages.add(new HomePage(this, mMainView, mHandler));
		mPages.add(new WeekdayPage(this, mMainView, mHandler));
		mPages.add(new MainPage(this, mMainView, mHandler));
		mPages.add(new WinPage(this, mMainView, mHandler));
		mCurPage = mPages.get(0);
		// set pages to main view
		mMainView.setPages(mPages);
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
	protected void onPause() {
		mCurPage.pause();
		mMainView.onPause();
		super.onPause();		
	}

	@Override
	protected void onResume() {		
		super.onResume();	
		mMainView.onResume();
		mCurPage.resume();
	}

	@Override
	protected void onStart() {
		mCurPage.start();
		mMainView.onStart(mCurPage);
		super.onStart();
	}

	@Override
	protected void onStop() {		
		mMainView.onStop();
		mCurPage.stop();
		super.onStop();
	}	
	
	// use main looper as the default
	protected final Handler mHandler = new Handler() {	
		public void handleMessage(Message msg) {        	
			Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
			vibrator.vibrate(20);
	
			Intent i = null;		
        	switch (msg.what) {    
        	case AppCmd.BEGIN:        		
        		switchPages(1);
        		break;
        	case AppCmd.WEEKDAY:
        		mDataSource.setDay((Integer) msg.obj);
        		if (mDataSource.loadData()) {
        			mChunkManager.loadChunks(mDataSource);
            		switchPages(2);
        		}
            	break;
        	case AppCmd.QUEST:
        		i = new Intent(getApplicationContext(), ActionsDialog.class);             
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
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:		
			if (mCurPage == mPages.get(0)) { // home page
				HomePageDialog dialog = new HomePageDialog();
				dialog.show(getSupportFragmentManager(), "HomePageDialog");
			} else if (mCurPage == mPages.get(1)) { // main page
				switchPages(0);				
			} else if (mCurPage == mPages.get(2)) {
				mChunkManager.saveChunks();
				mChunkManager.release();
				mDataSource.releaseData();
				switchPages(1);
			}
			return true;
		}
		
		return super.onKeyDown(keyCode, event);
	}
    
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_CANCELED) {
    		return;
    	} else if (requestCode == AppCmd.QUEST) {    
        	MainPage page = (MainPage) mCurPage;
        	page.finishQuest(resultCode, data.getStringExtra(ActionsDialog.ACTION_NAME));       	        	
        } else if (requestCode == AppCmd.MERGE) {        	
        	MainPage page = (MainPage) mCurPage;
        	page.finishMerge(data.getStringExtra(WarningDialog.SELECTION));        	
        }
    } 
}
