package edu.neu.android.mhealth.uscteensver1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.neu.android.mhealth.uscteensver1.R;
import edu.neu.android.mhealth.uscteensver1.TitleView.OnBackClickedListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;


public class ActionsDialog extends Activity implements OnBackClickedListener {
	
	protected TitleView   mTitleView = null;
	protected ListView    mLvAction  = null;
	protected ImageButton mBtnNext   = null;
	public static final String ACTION_NAME = "ACTION_NAME";
	public static final String[] ACTIONS = new String[] {  		
		"Reading/Homework", "Watching TV/Movies", "Using the computer",
		"Eating", "Sports", "Going somewhere",
		"Lying down", "Sitting", "Standing",
		"Walking", "Hanging with friends", "Doing chores", 
		"Cooking", "Riding in a car", "Playing video games", 
		"Using the phone", "Showering/Bathing", "Sleeping",
		"Doing something else", "I don't remember", "Running",
		"Basketball", "Football", "Soccer", 
		"Jogging", "Dance class", "Karate class",
		"Strength training", "Bicycling", "Swimming",
		"Baseball", "Skateboarding"
	};
	public static final int[] ACTION_IMGS = new int[] {	
		R.drawable.reading, R.drawable.watchingtv, R.drawable.usingcomputer,
		R.drawable.eating, R.drawable.sports, R.drawable.goingsomewhere,
		R.drawable.lyingdown, R.drawable.sitting, R.drawable.standing,
		R.drawable.walking, R.drawable.hangingwfriends, R.drawable.doingchores,
		R.drawable.cooking, R.drawable.ridinginacar, R.drawable.videogames,
		R.drawable.usingthephone, R.drawable.showering, R.drawable.sleeping,
		R.drawable.somethingelse, R.drawable.idontremember, R.drawable.running,
		R.drawable.basketball, R.drawable.football, R.drawable.soccer,
		R.drawable.jogging, R.drawable.dance, R.drawable.karate,
		R.drawable.strength_training, R.drawable.bicycling, R.drawable.swimming,
		R.drawable.baseball, R.drawable.skateboarding
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_actions);
		
		setupViews();
		adjustLayout();
	}
	
	private void setupViews() {
		mTitleView = (TitleView) findViewById(R.id.view_action_title);
		mLvAction  = (ListView) findViewById(R.id.lv_actions);
		mBtnNext   = (ImageButton) findViewById(R.id.btn_next_actions);
		
		mLvAction.setAdapter(new SimpleAdapter(this, getData(), R.layout.action_list_item,   
                new String[]{ "img_pre", "text" },   
                new int[]{ R.id.img_pre, R.id.text }));  
		mLvAction.setTextFilterEnabled(true);  
		mLvAction.setOnItemClickListener(new OnItemClickListener() {  
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
				Intent intent = new Intent(); 
				intent.putExtra(ACTION_NAME, ACTIONS[pos]);
                setResult(ACTION_IMGS[pos], intent); 
				finish();
			}             
		}); 
		
		mTitleView.setOnBackClickedListener(this);
		mTitleView.loadImages(new int[]{ R.drawable.popup_win_background, R.drawable.back_blue });	
	}
	
	private void adjustLayout() {
		DisplayMetrics dm = new DisplayMetrics();  
        Display display = getWindowManager().getDefaultDisplay(); 		
        display.getMetrics(dm);
      
        // adjust the layout according to the screen resolution				   
		LayoutParams laParams = null;
		laParams = mLvAction.getLayoutParams();
		laParams.width  = (int) (dm.widthPixels * 0.57f);
		laParams.height = (int) (dm.heightPixels * 0.684f);
		mLvAction.setLayoutParams(laParams);
	}	

	private List<Map<String, Object>> getData() {  
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();        
          
        for (int i = 0; i < ACTIONS.length; i++) {  
            Map<String, Object> map = new HashMap<String, Object>(); 
            map.put("img_pre", ACTION_IMGS[i]);
            map.put("text", ACTIONS[i]);                                      
            list.add(map);  
        }  
          
        return list;  
    }

	public void OnBackClicked() {
		finish();
	} 
}