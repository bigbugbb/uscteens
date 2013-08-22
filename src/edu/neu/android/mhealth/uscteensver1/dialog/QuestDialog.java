package edu.neu.android.mhealth.uscteensver1.dialog;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import edu.neu.android.mhealth.uscteensver1.R;
import edu.neu.android.mhealth.uscteensver1.TeensAppManager;
import edu.neu.android.mhealth.uscteensver1.TeensGlobals;
import edu.neu.android.mhealth.uscteensver1.data.Labeler;
import edu.neu.android.mhealth.uscteensver1.extra.Action;
import edu.neu.android.mhealth.uscteensver1.extra.ActionManager;
import edu.neu.android.mhealth.uscteensver1.pages.AppCmd;
import edu.neu.android.mhealth.uscteensver1.pages.AppScale;
import edu.neu.android.mhealth.uscteensver1.utils.NoteSender;
import edu.neu.android.mhealth.uscteensver1.views.ActionListView;
import edu.neu.android.mhealth.uscteensver1.views.ActionListView.OnOverScrolledListener;
import edu.neu.android.mhealth.uscteensver1.views.HeaderView;
import edu.neu.android.wocketslib.Globals;
import edu.neu.android.wocketslib.support.DataStorage;

public class QuestDialog extends Activity {
	
	static public String CHUNK_START_TIME = "CHUNK_START_TIME";
	static public String CHUNK_STOP_TIME  = "CHUNK_STOP_TIME";
	
	protected HeaderView mHeaderView;
	protected Button	 mBackButton;	
	protected ImageView  mTopArrow;
	protected ImageView  mBottomArrow;
	protected View       mTopLine;
	protected View		 mBottomLine;
	protected ViewGroup  mListWrap;
	protected ActionListView mListView;
	
	protected ActionAdapter mAdapter;
	protected HashMap<Integer, Action> mItemData = new HashMap<Integer, Action>();	
	
	protected boolean mImageLoaded;
	protected ArrayList<Drawable> mImages = new ArrayList<Drawable>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_quest);
		
		mAdapter = new ActionAdapter(this);
		loadImages(new int[]{ 
			R.drawable.popup_wind_arrow_ops, R.drawable.popup_wind_arrow, R.drawable.back_blue 
		});
		
		setupViews();
		adjustLayout();
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
		finish();
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

		mHeaderView = (HeaderView) findViewById(R.id.view_quest_header);
		int start = getIntent().getIntExtra(CHUNK_START_TIME, 0);
		int stop  = getIntent().getIntExtra(CHUNK_STOP_TIME, 0);
		mHeaderView.setTime(start, stop);
		
		mBackButton = (Button) findViewById(R.id.button_back);
		mBackButton.setBackgroundDrawable(mImages.get(2));
		mBackButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		mTopArrow    = (ImageView) findViewById(R.id.view_top_arrow);				
		mBottomArrow = (ImageView) findViewById(R.id.view_bottom_arrow);			
		
		mTopLine    = findViewById(R.id.view_line_top);
		mBottomLine = findViewById(R.id.view_line_bottom);
		
		mListWrap = (ViewGroup) findViewById(R.id.list_wrap);
		
		mListView = (ActionListView) findViewById(R.id.view_action_list);		       
		mListView.setAdapter(mAdapter);
		mListView.setOnOverScrolledListener(new OnOverScrolledListener() {

			@Override
			public void onOverScrolled(ListView view, int scrollX, int scrollY,
					boolean clampedX, boolean clampedY) {
				Log.d("ActionListView", "scrollY: " + scrollY + "\tslampedY: " + clampedY);
				if (scrollY < 0) {
					mTopArrow.setImageDrawable(null);
				} else if (scrollY > 0) {
					mBottomArrow.setImageDrawable(null);
				}
			}
			
		});
		mListView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				Log.d("ActionListView", "scrollState: " + scrollState);				
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				Log.d("ActionListView", "firstVisibleItem: " + firstVisibleItem + 
						"\tvisibleItemCount: " + visibleItemCount + "\ttotalItemCount: " + totalItemCount);
				// save index and top position
				// int index = mListView.getFirstVisiblePosition();
				View v = mListView.getChildAt(0);
				int top = (v == null) ? 0 : v.getTop(); 
				
				if (mAdapter.getCount() > 4) {
					mTopArrow.setImageDrawable(top < 0 ? mImages.get(0) : null);
					mBottomArrow.setImageDrawable(mImages.get(1));
				} else {
					mTopArrow.setImageDrawable(null);
					mBottomArrow.setImageDrawable(null);
				}
				// mListView.setSelectionFromTop(index, top);
			}
			
		});
		mListView.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				Action action = mItemData.get(position);
				
				// update the most recent selected activity
				ActionManager.setMostRecentAction(action);
				
				// add labeling
				Labeler.getInstance().addLabel(new Date(), "Labeling");
				
				setResultAndExit(action.getActionID());
			}
		});	
	}
	
	private void adjustLayout() {
		final DisplayMetrics metrics = getResources().getDisplayMetrics();
		final float density = metrics.density;
        Display display = getWindowManager().getDefaultDisplay(); 		
        display.getMetrics(metrics);        
        
        // adjust the layout according to the screen resolution				   
		LayoutParams laParams = null;
		laParams = mHeaderView.getLayoutParams();
		laParams.width  = mHeaderView.getExpectedWidth();
		laParams.height = mHeaderView.getExpectedHeight();
		mHeaderView.setLayoutParams(laParams);				
		
		int h1 = (int) AppScale.doScaleH(50);
		laParams = mTopArrow.getLayoutParams();
		laParams.width  = mHeaderView.getExpectedWidth();
		laParams.height = h1;
		mTopArrow.setLayoutParams(laParams);
		
		laParams = mBottomArrow.getLayoutParams();
		laParams.width  = mHeaderView.getExpectedWidth();
		laParams.height = h1;
		mBottomArrow.setLayoutParams(laParams);
		
		int h2 = Math.round(AppScale.doScaleH(1 * density));
		h2 = h2 > 1 ? h2 : 1;
		laParams = mTopLine.getLayoutParams();
		laParams.width  = mHeaderView.getExpectedWidth();
		laParams.height = h2;
		mTopLine.setLayoutParams(laParams);
		
		laParams = mBottomLine.getLayoutParams();
		laParams.width  = mHeaderView.getExpectedWidth();
		laParams.height = h2;
		mBottomLine.setLayoutParams(laParams);
		
		int height = (int) (metrics.heightPixels - (h1 * 2 + h2) - mHeaderView.getExpectedHeight());
		laParams = mListWrap.getLayoutParams();
		laParams.width  = mHeaderView.getExpectedWidth();
		laParams.height = height;
		mListWrap.setLayoutParams(laParams);
	}	
	
	public void onBackClicked() {
		finish();
		// Send note to server graph
		NoteSender noteSender = new NoteSender(TeensAppManager.getAppContext());
		noteSender.addNote(new Date(), "Cancel labeling", Globals.NO_PLOT);
		noteSender.send();
	}

	public void loadImages(int[] resIDs) {
		if (mImageLoaded) {
			return;
		}
		mImageLoaded = true;
		
		BitmapFactory.Options options = new BitmapFactory.Options(); 
        options.inPurgeable = true;
        options.inPreferredConfig = Config.RGB_565;        
        for (int id : resIDs) {
        	Bitmap origin = BitmapFactory.decodeResource(TeensAppManager.getAppResources(), id, options);
        	Bitmap scaled = null;
        	// scale the image according to the current screen resolution
        	float dstWidth  = origin.getWidth(),
        	      dstHeight = origin.getHeight();        	        	
    		dstWidth  = AppScale.doScaleW(dstWidth);
    		dstHeight = AppScale.doScaleH(dstHeight);
    		if (dstWidth != origin.getWidth() || dstHeight != origin.getHeight()) {
    			scaled = Bitmap.createScaledBitmap(origin, (int) dstWidth, (int) dstHeight, true);
    		}            
    		// add to the image list
        	if (scaled != null) {
	    		origin.recycle(); // explicit call to avoid out of memory
	    		mImages.add(new BitmapDrawable(getResources(), scaled));
	        } else {
	        	mImages.add(new BitmapDrawable(getResources(), origin));
	        }
        }
	}
	
	protected void setResultAndExit(String actionID) {
		DataStorage.SetValue(getApplicationContext(), TeensGlobals.QUEST_SELECTION, actionID);
		Message msg = TeensGlobals.sGlobalHandler.obtainMessage();		
		msg.what = AppCmd.QUEST_FINISHING;
		TeensGlobals.sGlobalHandler.sendMessage(msg);
		finish();
	}
	
	protected class ActionAdapter extends BaseAdapter {
	    
	    private ArrayList<Action> mActions;
	    private ArrayList<Action> mRecents; 
	    private LayoutInflater mInflater;
	    private Typeface  mTypeface;
	    private Resources mResources;
	    	        
	    public ActionAdapter(Activity activity) {
	    	mResources = activity.getResources();
	    	mActions   = ActionManager.getActivatedActions();
	    	mRecents   = ActionManager.getMostRecentActions();	    	
	    	mInflater  = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    	mTypeface  = Typeface.createFromAsset(TeensAppManager.getAppAssets(), "font/arial.ttf");	    	
	    }

	    public int getCount() {
	        return mActions.size() + mRecents.size();
	    }

	    public Object getItem(int position) {
	        return position;
	    }

	    public long getItemId(int position) {
	        return position;
	    }
	    
	    public View getView(int position, View convertView, ViewGroup parent) {
	        View view = convertView;
	        
	        if (view == null) {
	        	view = mInflater.inflate(R.layout.action_list_row, null);	  	        	
	        }	        
	        
	        Action action = null;
	        if (position < ActionManager.MOST_RECENT_ACTIONS_COUNT) {
	        	action = mRecents.get(position);
	        	Drawable background = mResources.getDrawable(R.drawable.action_list_highlight_selector); 
	        	view.setBackgroundDrawable(background);
	        } else {
	        	action = mActions.get(position - ActionManager.MOST_RECENT_ACTIONS_COUNT);	 
	        	Drawable background = mResources.getDrawable(R.drawable.action_list_selector);
	        	view.setBackgroundDrawable(background);	        	       
	        }	        
	        
	        TextView  name    = (TextView) view.findViewById(R.id.action_name);
	        TextView  subname = (TextView) view.findViewById(R.id.action_subname);
	        ImageView image   = (ImageView) view.findViewById(R.id.action_image);
	                
	        // set all values in listview
	        name.setText(action.getActionName());   
	        subname.setText(action.getActionSubName());
	        subname.setVisibility(action.getActionSubName() == null ? View.GONE : View.VISIBLE);        
	        image.setImageBitmap(action.getActionImage());

	        name.setTypeface(mTypeface);
	        subname.setTypeface(mTypeface);
	        
	        // attach the action to the item
	        mItemData.put(position, action);
	        
	        final DisplayMetrics metrics = TeensAppManager.getAppResources().getDisplayMetrics();
			final float density = metrics.density;					
			
	        LayoutParams laParams = null;	        
	        laParams = mListWrap.getLayoutParams();	
	        int itemHeight = laParams.height >> 2;			
	        laParams = image.getLayoutParams();
	        laParams.height = (int) (itemHeight - 4 * density);
	        image.setLayoutParams(laParams);
	        
	        return view;
	    }
	}	
}