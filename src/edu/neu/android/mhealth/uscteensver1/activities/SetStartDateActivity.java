package edu.neu.android.mhealth.uscteensver1.activities;

import android.os.Bundle;
import edu.neu.android.mhealth.uscteensver1.R;
import edu.neu.android.wocketslib.utils.BaseActivity;

public class SetStartDateActivity extends BaseActivity {
	private static final String TAG = "SetStartDateActivity";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, TAG);
		setContentView(R.layout.activity_setstartdate);								

		// get views and set listeners
		setupViews();
	}
	
	private void setupViews() {
//		mMainView = (MainView) findViewById(R.id.view_main);		
//		mMainView.setOnTouchListener(this);
//		mMainView.setLongClickable(true);
	}
}
