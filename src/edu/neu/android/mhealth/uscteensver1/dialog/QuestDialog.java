package edu.neu.android.mhealth.uscteensver1.dialog;

import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import edu.neu.android.mhealth.uscteensver1.R;
import edu.neu.android.mhealth.uscteensver1.TeensAppManager;
import edu.neu.android.mhealth.uscteensver1.data.Labeler;
import edu.neu.android.mhealth.uscteensver1.extra.Action;
import edu.neu.android.mhealth.uscteensver1.extra.ActionManager;
import edu.neu.android.mhealth.uscteensver1.pages.AppScale;
import edu.neu.android.mhealth.uscteensver1.ui.ActionListView.ActionItem;
import edu.neu.android.mhealth.uscteensver1.utils.NoteSender;
import edu.neu.android.mhealth.uscteensver1.views.ActionAdapter;
import edu.neu.android.mhealth.uscteensver1.views.HeaderView;
import edu.neu.android.mhealth.uscteensver1.views.QuestView.OnBackClickedListener;
import edu.neu.android.wocketslib.Globals;

public class QuestDialog extends Activity implements OnBackClickedListener {
	
	static public String CHUNK_START_TIME = "CHUNK_START_TIME";
	static public String CHUNK_STOP_TIME  = "CHUNK_STOP_TIME";
	
	protected HeaderView mDialogHeader;
	protected ListView   mListView;
	protected ImageView  mTopArrow;
	protected ImageView  mBottomArrow;
	protected View       mTopLine;
	protected View		 mBottomLine;
	protected ViewGroup  mListWrap;
	
	protected ActionAdapter mAdapter;
	
	protected boolean mImageLoaded;
	protected ArrayList<Bitmap> mImages = new ArrayList<Bitmap>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_quest);
		
		loadImages(new int[]{ R.drawable.popup_wind_arrow_ops, R.drawable.popup_wind_arrow });
		
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
//		mView = (QuestView) findViewById(R.id.view_quest);	
//		mView.setOnBackClickedListener(this);
//		mView.setHandler(mHandler);
		
		mDialogHeader = (HeaderView) findViewById(R.id.view_quest_header);
		int start = getIntent().getIntExtra(CHUNK_START_TIME, 0);
		int stop  = getIntent().getIntExtra(CHUNK_STOP_TIME, 0);
		mDialogHeader.setTime(start, stop);
		
		mTopArrow    = (ImageView) findViewById(R.id.view_top_arrow);
		mTopArrow.setImageBitmap(mImages.get(0));				
		mBottomArrow = (ImageView) findViewById(R.id.view_bottom_arrow);		
		mBottomArrow.setImageBitmap(mImages.get(1));
		
		mTopLine    = findViewById(R.id.view_line_top);
		mBottomLine = findViewById(R.id.view_line_bottom);
		
		mListWrap = (ViewGroup) findViewById(R.id.list_wrap);
		
		mListView = (ListView) findViewById(R.id.view_action_list);
		// Getting adapter by passing xml data ArrayList
		mAdapter = new ActionAdapter(this, ActionManager.getActivatedActions());        
		mListView.setAdapter(mAdapter);        
        // Click event for single list row
		mListView.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
//				Action action = ((ActionItem) li).getAction();
//				
//				Message msg = mHandler.obtainMessage();
//				msg.obj  = action.getActionID();
//				msg.what = 1;
//				mHandler.sendMessage(msg);
//				
//				// update the most recent selected activity
//				ActionManager.setMostRecentAction(action);
//				
//				// add labeling
//				Labeler.getInstance().addLabel(new Date(), "Labeling");
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
		laParams = mDialogHeader.getLayoutParams();
		laParams.width  = mDialogHeader.getExpectedWidth();
		laParams.height = mDialogHeader.getExpectedHeight();
		mDialogHeader.setLayoutParams(laParams);				
		
		int h1 = Math.round(AppScale.doScaleT(23 * density));
		laParams = mTopArrow.getLayoutParams();
		laParams.width  = mDialogHeader.getExpectedWidth();
		laParams.height = h1;
		mTopArrow.setLayoutParams(laParams);
		
		laParams = mBottomArrow.getLayoutParams();
		laParams.width  = mDialogHeader.getExpectedWidth();
		laParams.height = h1;
		mBottomArrow.setLayoutParams(laParams);
		
		int h2 = Math.round(AppScale.doScaleH(1 * density));
		h2 = h2 > 1 ? h2 : 2;
		laParams = mTopLine.getLayoutParams();
		laParams.width  = mDialogHeader.getExpectedWidth();
		laParams.height = h2;
		mTopLine.setLayoutParams(laParams);
		
		laParams = mBottomLine.getLayoutParams();
		laParams.width  = mDialogHeader.getExpectedWidth();
		laParams.height = h2;
		mBottomLine.setLayoutParams(laParams);
		
		int height = (int) (metrics.heightPixels - (h1 + h2) * 2 - mDialogHeader.getExpectedHeight());
		laParams = mListWrap.getLayoutParams();
		laParams.width  = mDialogHeader.getExpectedWidth();
		laParams.height = height;
		mListWrap.setLayoutParams(laParams);
		
		mAdapter.setListItemHeight(height / 4);
		
//		
//		ListAdapter listAdapter = mListView.getAdapter();
//		int itemHeight = (int) (metrics.heightPixels - (h1 + h2) * 2 - mDialogHeader.getExpectedHeight()) / 4;
//		int totalHeight = 0;
//		for (int i = 0; i < listAdapter.getCount(); i++) {
//			totalHeight += itemHeight;
//		}
//		laParams = mListView.getLayoutParams();
//		laParams.width  = mDialogHeader.getExpectedWidth();
//		laParams.height = totalHeight;	
//		mListView.setLayoutParams(laParams);
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
	    		mImages.add(scaled);
	        } else {
	        	mImages.add(origin);
	        }
        }
	}
	
	public class ActionAdapter extends BaseAdapter {
	    
	    private ArrayList<Action> mData;
	    private LayoutInflater mInflater;
	    private Typeface mTypeface;
	    
	    private int mHeight;
	    
	    public ActionAdapter(Activity activity, ArrayList<Action> data) {
	    	mData = data;
	    	mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    	mTypeface = Typeface.createFromAsset(TeensAppManager.getAppAssets(), "font/arial.ttf");
	    }

	    public int getCount() {
	        return mData.size();
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
	        
	        Action action = mData.get(position);

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
	        
	        final DisplayMetrics metrics = TeensAppManager.getAppResources().getDisplayMetrics();
			final float density = metrics.density;
			
	        LayoutParams laParams = null;
	        laParams = image.getLayoutParams();
	        laParams.height = (int) (mHeight - 4 * density);
	        image.setLayoutParams(laParams);
	        
	        return view;
	    }
	    
	    public void setListItemHeight(int height) {
	    	mHeight = height;
	    }
	}
	
//	@Override
//	public boolean onTouchEvent(MotionEvent event) {
//		if (mView != null) {
//    		return mView.onTouchEvent(event);
//    	}
//		return false;
//	} 
	
//	protected void setResultAndExit(String actionID) {
//		DataStorage.SetValue(getApplicationContext(), TeensGlobals.QUEST_SELECTION, actionID);
//		Message msg = TeensGlobals.sGlobalHandler.obtainMessage();		
//		msg.what = AppCmd.QUEST_FINISHING;
//		TeensGlobals.sGlobalHandler.sendMessage(msg);
//		finish();
//	}
	
//	protected final Handler mHandler = new Handler() {
//		public void handleMessage(Message msg) {
//	    	switch (msg.what) {
//	    	case 1:
//	    		setResultAndExit((String) msg.obj);
//	    		break;
//	        default:
//	        	break;
//	        }
//        }
//    };
	
}