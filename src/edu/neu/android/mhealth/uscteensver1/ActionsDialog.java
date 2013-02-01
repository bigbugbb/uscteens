package edu.neu.android.mhealth.uscteensver1;

import edu.neu.android.mhealth.uscteensver1.R;
import edu.neu.android.mhealth.uscteensver1.ActionsView.OnBackClickedListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.ViewGroup.LayoutParams;

public class ActionsDialog extends Activity implements OnBackClickedListener {
	
	protected ActionsView mActionsView = null;	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_actions);
		
		setupViews();
		adjustLayout();
	}
	
	private void setupViews() {
		mActionsView = (ActionsView) findViewById(R.id.view_action_title);	
		mActionsView.setOnBackClickedListener(this);
		mActionsView.setHandler(mHandler);
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

	public void OnBackClicked() {
		finish();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mActionsView != null) {
    		return mActionsView.onTouchEvent(event);
    	}
		return false;
	} 
	
	protected void setResultAndExit(int index) {
		Intent i = new Intent();	
		setResult(index, i); 
		finish();
	}
	
	protected final Handler mHandler = new Handler() {	
		public void handleMessage(Message msg) {        									
        	switch (msg.what) {    
        	case 1:
        		int index = (Integer) msg.obj;
        		setResultAndExit(index);
        		break;
            default:
            	break;
            }            
        }			
    };
}