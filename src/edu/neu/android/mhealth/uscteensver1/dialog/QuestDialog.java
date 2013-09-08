package edu.neu.android.mhealth.uscteensver1.dialog;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
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
import edu.neu.android.mhealth.uscteensver1.views.QuestHeader;
import edu.neu.android.wocketslib.Globals;
import edu.neu.android.wocketslib.support.DataStorage;

public class QuestDialog extends Activity {

    static public String CHUNK_START_TIME = "CHUNK_START_TIME";
    static public String CHUNK_STOP_TIME  = "CHUNK_STOP_TIME";
    
    static private final String KEY_GROUP_EXPAND_STATE = "GROUP_EXPAND_STATE";

    protected QuestHeader    mQuestHeader;
    protected Button         mBackButton;
    protected ImageView      mTopArrow;
    protected ImageView      mBottomArrow;
    protected View           mTopLine;
    protected View           mBottomLine;
    protected ViewGroup      mListWrap;
    protected ViewGroup		 mLayoutDialog;
    protected ActionListView mListView;
    
    private int mListItemHeight;

    protected Typeface 		 mTypeface;    
    protected ActionAdapter  mAdapter;
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
        
        mTypeface = Typeface.createFromAsset(TeensAppManager.getAppAssets(), "font/arial.ttf");

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
    	
    	mLayoutDialog = (ViewGroup) findViewById(R.id.layout_quest_dialog); 

    	mQuestHeader = (QuestHeader) findViewById(R.id.view_quest_header);
        int start = getIntent().getIntExtra(CHUNK_START_TIME, 0);
        int stop  = getIntent().getIntExtra(CHUNK_STOP_TIME, 0);
        mQuestHeader.setTime(start, stop);

        mBackButton = (Button) findViewById(R.id.button_back);
        mBackButton.setText("BACK");
        mBackButton.setTypeface(mTypeface);
        mBackButton.setBackgroundDrawable(mImages.get(2));
        mBackButton.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				mBackButton.setTextColor(mBackButton.isPressed() ? 
						getResources().getColor(R.color.pressed_blue) : Color.WHITE);
				return false;
			}
        });
        mBackButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                // Send note to server graph
                NoteSender noteSender = new NoteSender(TeensAppManager.getAppContext());
                noteSender.addNote(new Date(), "Cancel labeling", Globals.NO_PLOT);
                noteSender.send();
            }
        });

        mTopArrow = (ImageView) findViewById(R.id.view_top_arrow);
        mBottomArrow = (ImageView) findViewById(R.id.view_bottom_arrow);

        mTopLine = findViewById(R.id.view_line_top);
        mBottomLine = findViewById(R.id.view_line_bottom);

        mListWrap = (ViewGroup) findViewById(R.id.list_wrap);

        mListView = (ActionListView) findViewById(R.id.listview_action);
        mListView.setAdapter(mAdapter);
        mListView.expandGroup(0);
        for (int i = 1; i < mAdapter.getGroupCount(); ++i) {
        	boolean isExpanded = DataStorage.GetValueBoolean(getApplicationContext(), KEY_GROUP_EXPAND_STATE + i, false);
        	if (isExpanded) {
        		mListView.expandGroup(i);
        	}
        }
        //mListView.setIndicatorBounds(getPixelFromDip(24), getPixelFromDip(30));
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

                if (mAdapter.getChildrenCount(1) > 4) { // TODO:
                    Drawable image = mTopArrow.getDrawable();
                    if (top < 0 && image == null) {
                        mTopArrow.setImageDrawable(mImages.get(0));
                    }
                    mBottomArrow.setImageDrawable(mImages.get(1));
                } else {
                    mTopArrow.setImageDrawable(null);
                    mBottomArrow.setImageDrawable(null);
                }
                // mListView.setSelectionFromTop(index, top);
            }

        });
        mListView.setOnGroupExpandListener(new OnGroupExpandListener() {

			@Override
			public void onGroupExpand(int groupPosition) {
				// set the state first so calculateListHeight can get the right result
				DataStorage.SetValue(getApplicationContext(), KEY_GROUP_EXPAND_STATE + groupPosition, true);
				
				mListWrap.getLayoutParams().height = Math.min(calculateListHeight(), mListItemHeight * 4);
				mLayoutDialog.requestLayout();								
			}
		});

		// Listview Group collasped listener
        mListView.setOnGroupCollapseListener(new OnGroupCollapseListener() {

			@Override
			public void onGroupCollapse(int groupPosition) {
				// set the state first so calculateListHeight can get the right result
				DataStorage.SetValue(getApplicationContext(), KEY_GROUP_EXPAND_STATE + groupPosition, false);
				
		        mListWrap.getLayoutParams().height = Math.min(calculateListHeight(), mListItemHeight * 4);
                mLayoutDialog.requestLayout();								
			}
		});
        mListView.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				int key = groupPosition == 0 ? childPosition : childPosition + ActionManager.MOST_RECENT_ACTIONS_COUNT;				
				Action action = mItemData.get(key);

                // update the most recent selected activity
                ActionManager.setMostRecentAction(action);

                // add labeling
                Labeler.getInstance().addLabel(new Date(), "Labeling");

                setResultAndExit(action.getActionID());
                
				return true;
			}
		});       
    }

    private void adjustLayout() {
    	final DisplayMetrics metrics = getResources().getDisplayMetrics();
        Display display = getWindowManager().getDefaultDisplay();
        display.getMetrics(metrics);

        // adjust the layout according to the screen resolution				   
        LayoutParams laParams = null;
        laParams = mQuestHeader.getLayoutParams();
        laParams.width  = mQuestHeader.getExpectedWidth();
        laParams.height = mQuestHeader.getExpectedHeight();

        int h1 = (int) AppScale.doScaleH(50);
        laParams = mTopArrow.getLayoutParams();
        laParams.width  = mQuestHeader.getExpectedWidth();
        laParams.height = h1;

        laParams = mBottomArrow.getLayoutParams();
        laParams.width  = mQuestHeader.getExpectedWidth();
        laParams.height = h1;

        int h2 = Math.max(1, getPixelFromDip(1));        
        laParams = mTopLine.getLayoutParams();
        laParams.width  = mQuestHeader.getExpectedWidth();
        laParams.height = h2;

        laParams = mBottomLine.getLayoutParams();
        laParams.width  = mQuestHeader.getExpectedWidth();
        laParams.height = h2;

        laParams = mListWrap.getLayoutParams();
        laParams.width  = mQuestHeader.getExpectedWidth();
        laParams.height = metrics.heightPixels - (h1 * 2 + h2) - mQuestHeader.getExpectedHeight();
        
        mListItemHeight = laParams.height / 4;
    }
    
    private int calculateListHeight() {
    	int height = 0;
    	
    	for (int i = 0; i < mAdapter.getGroupCount(); ++i) {
    		boolean isExpanded = DataStorage.GetValueBoolean(getApplicationContext(), KEY_GROUP_EXPAND_STATE + i, false);
    		if (isExpanded) {
    			for (int j = 0; j < mAdapter.getChildrenCount(i); ++j) {
    				height += mListItemHeight;
    			}
    		}
    		height += mListItemHeight / 2;
    	}
    	Log.i("bbb", "" + height);
    	
    	return height;
    }
    
    private int getPixelFromDip(int pixel) {
    	final DisplayMetrics metrics = getResources().getDisplayMetrics();
        final float density = metrics.density;
        return Math.round(pixel * density);
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
            float dstWidth  = AppScale.doScaleW(origin.getWidth());
            float dstHeight = AppScale.doScaleH(origin.getHeight());
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
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            NoteSender noteSender = new NoteSender(TeensAppManager.getAppContext());
            noteSender.addNote(new Date(), "Cancel labeling", Globals.NO_PLOT);
            noteSender.send();
        }
        return super.onKeyDown(keyCode, event);
    }

    protected class ActionAdapter extends BaseExpandableListAdapter {
                
        private LayoutInflater    mInflater;
        private Typeface          mTypeface;
        private Resources         mResources;
        
        private List<String> mGroupData;
        private HashMap<String, List<Action>> mChildData;

        public ActionAdapter(Activity activity) {
            mResources = activity.getResources();                        
            mInflater  = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mTypeface  = Typeface.createFromAsset(TeensAppManager.getAppAssets(), "font/arial.ttf");
            
            mGroupData = new ArrayList<String>();
            mGroupData.add("Most recent");
            for (Map.Entry<String, ArrayList<Action>> entry : ActionManager.getActivatedActions().entrySet()) {  
            	mGroupData.add(entry.getKey());
            }
            
            mChildData = new HashMap<String, List<Action>>();
            mChildData.put("Most recent", ActionManager.getMostRecentActions());
            for (Map.Entry<String, ArrayList<Action>> entry : ActionManager.getActivatedActions().entrySet()) {       
            	mChildData.put(entry.getKey(), entry.getValue());
            }
        }
 
		@Override
		public int getGroupCount() {
			// TODO Auto-generated method stub
			return mGroupData.size();
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			// TODO Auto-generated method stub
			return mChildData.get(mGroupData.get(groupPosition)).size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			// TODO Auto-generated method stub
			return mGroupData.get(groupPosition);
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return mChildData.get(mGroupData.get(groupPosition)).get(childPosition);
		}

		@Override
		public long getGroupId(int groupPosition) {
			// TODO Auto-generated method stub
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return childPosition;
		}

		@Override
		public boolean hasStableIds() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.list_group_action, null);
			}

			String text = (String) getGroup(groupPosition);
			TextView group = (TextView) convertView.findViewById(R.id.action_group_name);
			group.setTypeface(mTypeface, Typeface.BOLD);
			group.setText(text);	

            LayoutParams laParams = group.getLayoutParams();
            laParams.height = (mListItemHeight >> 1) - getPixelFromDip(8);
            group.setLayoutParams(laParams);  
    
			return convertView;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {

            if (convertView == null) {
            	convertView = mInflater.inflate(R.layout.list_item_action, null);
            }

        	String category = mGroupData.get(groupPosition);
        	Action action   = mChildData.get(category).get(childPosition);
            Drawable background = mResources.getDrawable(groupPosition == 0 ? 
            		R.drawable.selector_action_list_highlight : R.drawable.selector_action_list);
            convertView.setBackgroundDrawable(background);            

            TextView  name    = (TextView) convertView.findViewById(R.id.action_name);
            TextView  subname = (TextView) convertView.findViewById(R.id.action_subname);
            ImageView image   = (ImageView) convertView.findViewById(R.id.action_image);
            
            name.setTypeface(mTypeface);
            subname.setTypeface(mTypeface);

            // set all values in listview
            name.setText(action.getActionName());
            subname.setText(action.getActionSubName());
            subname.setVisibility(action.getActionSubName().equals("") ? View.GONE : View.VISIBLE);
            image.setImageBitmap(action.getActionImage());           

            // attach the action to the item
            int key = groupPosition == 0 ? childPosition : childPosition + ActionManager.MOST_RECENT_ACTIONS_COUNT;
            mItemData.put(key, action);        
            
            LayoutParams laParams = image.getLayoutParams();
            laParams.height = (int) (mListItemHeight - getPixelFromDip(4));
            
            return convertView;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
    }    
}