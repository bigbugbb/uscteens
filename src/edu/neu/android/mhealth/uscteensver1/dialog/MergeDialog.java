package edu.neu.android.mhealth.uscteensver1.dialog;

import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import edu.neu.android.mhealth.uscteensver1.pages.AppCmd;
import edu.neu.android.mhealth.uscteensver1.pages.AppScale;
import edu.neu.android.mhealth.uscteensver1.utils.NoteSender;
import edu.neu.android.mhealth.uscteensver1.utils.TextMeasurer;
import edu.neu.android.mhealth.uscteensver1.views.MergeHeader;
import edu.neu.android.wocketslib.Globals;
import edu.neu.android.wocketslib.support.DataStorage;

public class MergeDialog extends Activity {
    private static final String TAG = "MergeDialog";
    public static final String KEY = "ACTIONS_TO_MERGE";
    
    protected MergeHeader mMergeHeader;
    protected Button      mBackButton;
    protected TextView	  mMergeText;
    protected ListView    mListView;            
    
    protected MergeAdapter mAdapter;
    
    protected String[] mNames;
    protected String[] mSubnames;    
    protected Typeface mTypeface;
    protected ArrayList<String> mActions;
    
    protected boolean mImageLoaded;
    protected ArrayList<Drawable> mImages = new ArrayList<Drawable>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merge);
                
        mActions  = getIntent().getStringArrayListExtra(KEY);
        mNames    = new String[mActions.size()];
        mSubnames = new String[mActions.size()]; 
        for (int i = 0; i < mActions.size(); ++i) {
        	String[] split = mActions.get(i).split("[|]");
        	mNames[i]    = split[0];
        	mSubnames[i] = split.length > 1 ? split[1] : null;
        }
        
        mAdapter = new MergeAdapter(this);
        loadImages(new int[]{
            R.drawable.arrow_warning, R.drawable.selection, R.drawable.selection_circle
        });
        
        mTypeface = Typeface.createFromAsset(TeensAppManager.getAppAssets(), "font/arial.ttf");
        
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
    	mMergeHeader = (MergeHeader) findViewById(R.id.view_merge_header);
        
        mBackButton = (Button) findViewById(R.id.button_back);
        mBackButton.setText("BACK");
        mBackButton.setTypeface(mTypeface);
        mBackButton.setBackgroundDrawable(mImages.get(0));
        mBackButton.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (mBackButton.isPressed()) {
					mBackButton.setTextColor(getResources().getColor(R.color.pressed_blue));
				} else {
					mBackButton.setTextColor(Color.WHITE);
				}
				return false;
			}        	
        });
        mBackButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        
        mMergeText = (TextView) findViewById(R.id.textview_merge);
        mMergeText.setText(getResources().getString(R.string.merge_hint));
        
        mListView = (ListView) findViewById(R.id.listview_merge);
        mListView.setAdapter(mAdapter);        
        mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				DataStorage.SetValue(getApplicationContext(), TeensGlobals.MERGE_SELECTION, mActions.get(position));
		        Message msg = TeensGlobals.sGlobalHandler.obtainMessage();
		        msg.what = AppCmd.MERGE_FINISHING;
		        TeensGlobals.sGlobalHandler.sendMessage(msg);
		        finish();				
			}        	
        });
    }

    private void adjustLayout() {
        DisplayMetrics dm = new DisplayMetrics();
        Display display = getWindowManager().getDefaultDisplay();
        display.getMetrics(dm);

        LayoutParams laParams = null;
        laParams = mMergeHeader.getLayoutParams();
        laParams.width  = mMergeHeader.getExpectedWidth();
        laParams.height = mMergeHeader.getExpectedHeight();
        mMergeHeader.setLayoutParams(laParams);
        
        int remainingHeight = dm.heightPixels - mMergeHeader.getExpectedHeight();
        laParams = mMergeText.getLayoutParams();
        laParams.width  = mMergeHeader.getExpectedWidth();
        laParams.height = (int) (remainingHeight * 0.4f);
        mMergeText.setLayoutParams(laParams);                

        laParams = mListView.getLayoutParams();
        laParams.width  = Math.min(mMergeHeader.getExpectedWidth(), (int) (TextMeasurer.getMaxWidth(mNames, mTypeface, 22, dm.density) + 50 * dm.density)); // 30,12
        laParams.height = remainingHeight - (int) (remainingHeight * 0.4f);
        mListView.setLayoutParams(laParams);
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
    
    protected class MergeAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        public MergeAdapter(Activity activity) {
            mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);            
        }

        public int getCount() {
            return mActions.size();
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
                view = mInflater.inflate(R.layout.list_item_merge, null);
                view.setOnTouchListener(new OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {                        
                        for (int i = 0; i < getCount(); ++i) {                        	
                        	View view = mListView.getChildAt(i);                        	
                        	ImageView image = (ImageView) view.findViewById(R.id.selection_image);
                            image.setImageDrawable(mImages.get(view != v ? 1 : 2));
                        }
                        return false;
                    }
                });
            }

            TextView name    = (TextView) view.findViewById(R.id.action_name);
            TextView subname = (TextView) view.findViewById(R.id.action_subname);
            ImageView image  = (ImageView) view.findViewById(R.id.selection_image);

            // set all values in listview
            name.setText(mNames[position]);
            subname.setText(mSubnames[position]);
            subname.setVisibility(mSubnames[position] == null ? View.GONE : View.VISIBLE);
            image.setImageDrawable(mImages.get(1));

            name.setTypeface(mTypeface);
            subname.setTypeface(mTypeface);

            final DisplayMetrics metrics = TeensAppManager.getAppResources().getDisplayMetrics();
            final float density = metrics.density;

            LayoutParams laParams = null;
            laParams = mListView.getLayoutParams();
            int itemHeight = laParams.height / 3;
            laParams = image.getLayoutParams();
            laParams.height = (int) (itemHeight - 4 * density);
            image.setLayoutParams(laParams);

            return view;
        }               
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
}
