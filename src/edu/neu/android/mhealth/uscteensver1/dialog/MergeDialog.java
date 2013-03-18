package edu.neu.android.mhealth.uscteensver1.dialog;

import java.util.ArrayList;
import java.util.List;

import edu.neu.android.mhealth.uscteensver1.R;
import edu.neu.android.mhealth.uscteensver1.R.id;
import edu.neu.android.mhealth.uscteensver1.R.layout;
import edu.neu.android.mhealth.uscteensver1.USCTeensGlobals;
import edu.neu.android.mhealth.uscteensver1.pages.AppCmd;
import edu.neu.android.mhealth.uscteensver1.pages.AppScale;
import edu.neu.android.mhealth.uscteensver1.views.MergeView;
import edu.neu.android.mhealth.uscteensver1.views.MergeView.OnBackClickedListener;
import edu.neu.android.mhealth.uscteensver1.views.MergeView.OnItemClickListener;
import edu.neu.android.wocketslib.support.DataStorage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

public class MergeDialog extends Activity implements OnItemClickListener, OnBackClickedListener {
	
	public static final String KEY = "ACTIONS_TO_MERGE";	
	protected MergeView mView = null;
	protected ArrayList<String> mActions = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_merge);
		
		mActions = getIntent().getStringArrayListExtra(KEY);

		setupViews();
		adjustLayout();
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
		laParams.width = (int) AppScale.doScaleW(755);
		//laParams.height = (int) (720 * 0.9f); 
		mView.setLayoutParams(laParams);
	}

	@Override
	public void onItemClick(View v, int pos) {
//		Intent i = new Intent();
//		i.putExtra(SELECTION, mActions.get(pos));
//		setResult(pos + 1, i);
		DataStorage.SetValue(getApplicationContext(), USCTeensGlobals.MERGE_SELECTION, mActions.get(pos));
		Message msg = USCTeensGlobals.sGlobalHandler.obtainMessage();				
		msg.what = AppCmd.MERGE_FINISHING;
		USCTeensGlobals.sGlobalHandler.sendMessage(msg);
		finish();
	}

	@Override
	public void onBackClicked() {
		finish();
	}	
}
