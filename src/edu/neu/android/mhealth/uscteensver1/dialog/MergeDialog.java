package edu.neu.android.mhealth.uscteensver1.dialog;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import edu.neu.android.mhealth.uscteensver1.R;
import edu.neu.android.mhealth.uscteensver1.TeensGlobals;
import edu.neu.android.mhealth.uscteensver1.pages.AppCmd;
import edu.neu.android.mhealth.uscteensver1.views.MergeView;
import edu.neu.android.mhealth.uscteensver1.views.MergeView.OnBackClickedListener;
import edu.neu.android.mhealth.uscteensver1.views.MergeView.OnItemClickListener;
import edu.neu.android.wocketslib.support.DataStorage;

public class MergeDialog extends Activity implements OnItemClickListener, OnBackClickedListener {
	private static final String TAG = "MergeDialog";
	public static final String KEY = "ACTIONS_TO_MERGE";	
	protected MergeView mView = null;
	protected ArrayList<String> mActions = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate");
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_merge);
		
		mActions = getIntent().getStringArrayListExtra(KEY);
		if (mActions != null) {
			setupViews();
			adjustLayout();
		} else {
			finish();
		}
	}
	
	@Override
	protected void onRestart() {
		Log.i(TAG, "onRestart");
		super.onRestart();
		finish();
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		Log.i(TAG, "onNewIntent");
	}
	
	@Override
	protected void onStart() {
		Log.i(TAG, "onStart");
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onResume() {
		Log.i(TAG, "onResume");
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onPause() {
		Log.i(TAG, "onPause");
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onStop() {
		Log.i(TAG, "onStop");
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	protected void onDestroy() {	
		Log.i(TAG, "onDestroy");
		super.onDestroy();
	}
	
	private void setupViews() {
		mView = (MergeView) findViewById(R.id.view_merge);
		mView.setActions(mActions);
		mView.setOnBackClickedListener(this);
		mView.setOnItemClickListener(this);
	}
	
	private void adjustLayout() {
		DisplayMetrics dm = new DisplayMetrics();  
        Display display = getWindowManager().getDefaultDisplay(); 		
        display.getMetrics(dm);
        
        LayoutParams laParams = null;
		laParams = mView.getLayoutParams();
		laParams.width = mView.getExpectedWidth();
		//laParams.height = (int) (720 * 0.9f); 
		mView.setLayoutParams(laParams);		
	}

	@Override
	public void onItemClick(View v, int pos) {
//		Intent i = new Intent();
//		i.putExtra(SELECTION, mActions.get(pos));
//		setResult(pos + 1, i);
		DataStorage.SetValue(getApplicationContext(), TeensGlobals.MERGE_SELECTION, mActions.get(pos));
		Message msg = TeensGlobals.sGlobalHandler.obtainMessage();				
		msg.what = AppCmd.MERGE_FINISHING;
		TeensGlobals.sGlobalHandler.sendMessage(msg);
		finish();
	}

	@Override
	public void onBackClicked() {
		finish();
	}

		
}
