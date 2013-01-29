package edu.neu.android.mhealth.uscteensver1;

import java.util.ArrayList;
import java.util.List;

import edu.neu.android.mhealth.uscteensver1.R;
import edu.neu.android.mhealth.uscteensver1.WarningView.OnItemClickListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

public class WarningDialog extends Activity implements OnItemClickListener {
	
	public static final String KEY = "ACTIONS_TO_MERGE";
	public static final String SELECTION = "SELECTION_ACTION";
	protected WarningView mView = null;
	protected ArrayList<String> mActions = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_warning);
		
		mActions = getIntent().getStringArrayListExtra(KEY);

		setupViews();
		adjustLayout();
	}
	
	private void setupViews() {
		mView = (WarningView) findViewById(R.id.view_warning);
		mView.setActions(mActions);
		mView.setOnItemClickListener(this);
	}
	
	private void adjustLayout() {
		DisplayMetrics dm = new DisplayMetrics();  
        Display display = getWindowManager().getDefaultDisplay(); 		
        display.getMetrics(dm);
        
        LayoutParams laParams = null;
		laParams = mView.getLayoutParams();
		laParams.width  = (int) (755);
		//laParams.height = (int) (720 * 0.9f); 
		mView.setLayoutParams(laParams);
	}

	@Override
	public void onItemClick(View v, int pos) {
		Intent i = new Intent();
		i.putExtra(SELECTION, mActions.get(pos));
		setResult(pos + 1, i); 
		finish();
	}	
}
