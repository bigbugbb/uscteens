package edu.neu.android.mhealth.uscteensver1.dialog;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.ViewGroup.LayoutParams;
import edu.neu.android.mhealth.uscteensver1.R;
import edu.neu.android.mhealth.uscteensver1.USCTeensGlobals;
import edu.neu.android.mhealth.uscteensver1.pages.AppCmd;
import edu.neu.android.mhealth.uscteensver1.views.QuestView;
import edu.neu.android.mhealth.uscteensver1.views.QuestView.OnBackClickedListener;
import edu.neu.android.wocketslib.support.DataStorage;

public class QuestDialog extends Activity implements OnBackClickedListener {
	
	static public String CHUNK_START_TIME = "CHUNK_START_TIME";
	static public String CHUNK_STOP_TIME  = "CHUNK_STOP_TIME";
	protected QuestView mActionsView = null;	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_quest);
		
		setupViews();
		adjustLayout();
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	protected void onDestroy() {	
		super.onDestroy();
	}
	
	private void setupViews() {
		mActionsView = (QuestView) findViewById(R.id.view_quest);	
		mActionsView.setOnBackClickedListener(this);
		mActionsView.setHandler(mHandler);
		
		int start = getIntent().getIntExtra(CHUNK_START_TIME, 0);
		int stop  = getIntent().getIntExtra(CHUNK_STOP_TIME, 0);
		mActionsView.setTime(start, stop);
	}
	
	private void adjustLayout() {
		DisplayMetrics dm = new DisplayMetrics();
        Display display = getWindowManager().getDefaultDisplay(); 		
        display.getMetrics(dm);
      
        // adjust the layout according to the screen resolution				   
		LayoutParams laParams = null;
		laParams = mActionsView.getLayoutParams();
		laParams.width  = mActionsView.getExpectedWidth();
		laParams.height = dm.heightPixels;
		mActionsView.setLayoutParams(laParams);
	}	

	public void onBackClicked() {
		finish();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mActionsView != null) {
    		return mActionsView.onTouchEvent(event);
    	}
		return false;
	} 
	
	protected void setResultAndExit(String actionID) {		
		DataStorage.SetValue(getApplicationContext(), USCTeensGlobals.QUEST_SELECTION, actionID);
		Message msg = USCTeensGlobals.sGlobalHandler.obtainMessage();				
		msg.what = AppCmd.QUEST_FINISHING;
		USCTeensGlobals.sGlobalHandler.sendMessage(msg);	
		finish();
	}
	
	protected final Handler mHandler = new Handler() {	
		public void handleMessage(Message msg) {        									
	    	switch (msg.what) {    
	    	case 1:
	    		setResultAndExit((String) msg.obj);
	    		break;
	        default:
	        	break;
	        }            
        }			
    };
	
}