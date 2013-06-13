package edu.neu.android.mhealth.uscteensver1.views;

import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.widget.ImageView;
import edu.neu.android.mhealth.uscteensver1.R;
import edu.neu.android.mhealth.uscteensver1.USCTeensGlobals;
import edu.neu.android.mhealth.uscteensver1.data.DataSource;
import edu.neu.android.mhealth.uscteensver1.data.Labeler;
import edu.neu.android.mhealth.uscteensver1.pages.AppObject;
import edu.neu.android.mhealth.uscteensver1.pages.AppScale;
import edu.neu.android.mhealth.uscteensver1.ui.ActionListView;
import edu.neu.android.mhealth.uscteensver1.ui.ActionListView.ActionItem;
import edu.neu.android.mhealth.uscteensver1.ui.ArrowButton;
import edu.neu.android.mhealth.uscteensver1.ui.ListView;
import edu.neu.android.mhealth.uscteensver1.ui.ListView.ListItem;
import edu.neu.android.mhealth.uscteensver1.ui.ListView.OnBoundaryListener;
import edu.neu.android.mhealth.uscteensver1.ui.ListView.OnItemClickListener;
import edu.neu.android.mhealth.uscteensver1.ui.ListView.OnListViewScrollingListener;
import edu.neu.android.mhealth.uscteensver1.utils.WeekdayCalculator;
import edu.neu.android.wocketslib.support.DataStorage;

public class QuestView extends ImageView implements OnGestureListener, 
													OnItemClickListener, 
													OnBoundaryListener,
													OnListViewScrollingListener {
	protected ArrowButton mArrowUp   = null;
	protected ArrowButton mArrowDown = null;
	protected ActionListView mActionList = null;
	protected AppObject mSelObject = null;	
	protected ArrayList<AppObject> mObjects = new ArrayList<AppObject>();
	
	protected boolean mImageLoaded = false;
	protected ArrayList<Bitmap> mImages = new ArrayList<Bitmap>();	
	protected String  mDate		 = "";
	protected String  mTime      = "";
	protected RectF   mBackArea  = new RectF();
	protected PointF  mBackTxtPt = new PointF();
	protected Paint   mPaintText = null;	
	protected Paint   mPaintDate = null;
	protected Paint   mPaintTime = null;
	protected Handler mHandler = null;
	protected int mWidth  = 0;
	protected int mHeight = 0;
	
	protected int mExpectedWidth = 0;	
	
	// gesture detector
	protected GestureDetector mGestureDetector = new GestureDetector(this);

	public QuestView(Context context, AttributeSet attrs) {
		super(context, attrs);	
		
		mArrowUp = new ArrowButton(context.getResources());
		mArrowUp.setVisible(false);
		mArrowUp.changeArrowDir(false);		
		mArrowDown = new ArrowButton(context.getResources());		
		mActionList = new ActionListView(context.getResources());
		mActionList.setOnItemClickListener(this);
		mActionList.setOnBoundaryListener(this);
		mActionList.setOnListViewScrollingListener(this);
		mObjects.add(mArrowUp);
		mObjects.add(mArrowDown);
		mObjects.add(mActionList);
		
		loadImages(new int[]{ 
			R.drawable.popup_win_background, R.drawable.back_blue  
		});
				
		Typeface tf = Typeface.createFromAsset(USCTeensGlobals.sContext.getAssets(), "font/arial.ttf");
		mPaintText = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaintText.setColor(Color.WHITE);
		mPaintText.setStyle(Style.STROKE);
		mPaintText.setTextSize(AppScale.doScaleT(34));
		mPaintText.setTypeface(tf);
		mPaintText.setFakeBoldText(false);		
		
		mPaintDate = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaintDate.setColor(Color.WHITE);
		mPaintDate.setStyle(Style.FILL);
		mPaintDate.setTextSize(AppScale.doScaleW(45));
		mPaintDate.setTypeface(Typeface.createFromAsset(USCTeensGlobals.sContext.getAssets(), "font/arial_bold.ttf"));
		mPaintDate.setTextAlign(Align.CENTER);		
		
		mPaintTime = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaintTime.setColor(Color.WHITE);
		mPaintTime.setStyle(Style.FILL);		
		mPaintTime.setTypeface(tf);
		mPaintTime.setTextAlign(Align.CENTER);		
		mPaintTime.setFakeBoldText(false);	

		mDate = convertDateToDisplayFormat(DataSource.getCurrentSelectedDate());
	}
	
	private String convertDateToDisplayFormat(String date) {
		String[] months = {
			"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JULY", "AUG", "SEPT", "OCT", "NOV", "DEC"			
		};
		String[] times = date.split("-");
		String weekday = WeekdayCalculator.getWeekday(date);
		String month   = months[Integer.parseInt(times[1]) - 1];
		String day     = times[2];			
		
		return " " + weekday.toUpperCase() + "  " + month + "  " + day;
	}
	
	// used to update the view for drawing
	Runnable mRunnable = new Runnable() {
		public void run() {
			invalidate();
			if (Math.abs(mActionList.getSpeedY()) > 0 || mActionList.outOfBound()) {
				mHandler.postDelayed(this, 10);
			} else {
				mHandler.removeCallbacks(this);
			}
		}
	};
	
	public void setTime(int start, int stop) {		
		String s[] = { "", "" };
		int hour   = 0;
		int minute = 0;
		
		int times[] = { start, stop };
		for (int i = 0; i < 2; ++i) {
			hour   = times[i] / 3600;
			minute = (times[i] - hour * 3600) / 60;		
			s[i] = (hour > 12 ? hour - 12 : hour == 0 ? 12 : hour) + ":" + 
				(minute > 9 ? minute : "0" + minute);
			s[i] += (hour > 11) ? " PM" : " AM";
		}
		mTime = s[0] + " - " + s[1];		 
	}
	
	public void setHandler(Handler handler) {
		mHandler = handler;
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
        	Bitmap origin = BitmapFactory.decodeResource(getContext().getResources(), id, options);
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
        
        mExpectedWidth = (int) (mImages.get(0).getWidth());        
        
        // the area for the back button
		mBackArea.left   = AppScale.doScaleW(10);
		mBackArea.right  = mBackArea.left + mImages.get(1).getWidth();
		mBackArea.top    = AppScale.doScaleH(5);
		mBackArea.bottom = mBackArea.top + mImages.get(1).getHeight();
				
		mBackTxtPt.x = mBackArea.left + AppScale.doScaleW(33);
		mBackTxtPt.y = mBackArea.bottom - AppScale.doScaleH(52);
	}
	
	public int getExpectedWidth() {
		return mExpectedWidth;
	}	

	@Override
	protected void onDraw(Canvas canvas) {		
		canvas.drawBitmap(mImages.get(0), 0, 0, null);
		canvas.drawBitmap(mImages.get(1), mBackArea.left, mBackArea.top, null);
		canvas.drawText("BACK", mBackTxtPt.x, mBackTxtPt.y, mPaintText);
		canvas.drawText(mDate, getWidth() / 2, AppScale.doScaleH(65), mPaintDate);
		mPaintTime.setTextSize(AppScale.doScaleT(35));
		canvas.drawText(mTime, getWidth() / 2, AppScale.doScaleH(115), mPaintTime);
		//mPaintTime.setTextSize(mAppScale.doScaleW(36));
		//canvas.drawText(mTimePostfix, getWidth() - mAppScale.doScaleW(40), mAppScale.doScaleH(100), mPaintTime);
		mArrowUp.onDraw(canvas);
		mArrowDown.onDraw(canvas);
		mActionList.onDraw(canvas);		
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean ret = false;
		
		if (mGestureDetector.onTouchEvent(event)) {
			return true;
		} else {
			switch (event.getAction()) {
			case MotionEvent.ACTION_UP: 
				ret = onUp(event); 
				break;				
			default: 
				ret = false;
				break;
			}
		}
		
		return ret;
	}

	protected OnBackClickedListener mListener = null;
	
	public interface OnBackClickedListener {
		void onBackClicked();
	}		
	
	public void setOnBackClickedListener(OnBackClickedListener listener) {
		mListener = listener;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		if (mWidth == w && mHeight == h) {
			return;
		}
		mWidth  = w;
		mHeight = h;
		
		mActionList.setPosn(0, AppScale.doScaleH(180) + mActionList.getBorderWidth());
		mActionList.onSizeChanged(w, h);		
		mArrowUp.setX(0);
		mArrowUp.setY(AppScale.doScaleH(130));
		mArrowUp.onSizeChanged(w, h);
		mArrowDown.setX(0);
		mArrowDown.setY(h - AppScale.doScaleH(50));
		mArrowDown.onSizeChanged(w, h);
		super.onSizeChanged(w, h, oldw, oldh);
	}
	
	public boolean onDown(MotionEvent e) {		
		float x = e.getX();
		float y = e.getY();
		boolean ret = false;
		
		if (mBackArea.contains(e.getX(), e.getY())) {
			if (mListener != null) {
				mListener.onBackClicked();
			}
			return true;
		}
		
		for (int i = mObjects.size() - 1; i >= 0; --i) {
			if (mObjects.get(i).contains(x, y)) {				
				mSelObject = mObjects.get(i);	
				mSelObject.setSelected(true);				
				ret = mSelObject.onDown(e);			
				invalidate();
				break;
			}
		}
		
		return ret;
	}
	
	public boolean onUp(MotionEvent e) {
		boolean ret = false;

		if (mSelObject != null) {
			ret = mSelObject.onUp(e);
			mSelObject.setSelected(false);
			mSelObject = null;
			invalidate();
		}
		
		return ret;
	}		

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		boolean ret = false;
		
		if (mSelObject != null) {
			if (mSelObject.contains(e2.getX(), e2.getY())) {
				ret = mSelObject.onFling(e1, e2, velocityX, velocityY);
			}		
			mSelObject.onCancelSelection(e2);
			mSelObject.setSelected(false);
			mSelObject = null;	
			
			mHandler.postDelayed(mRunnable, 10);
		}

		return ret;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		boolean ret = false;
		
		if (mSelObject != null) {
			if (mSelObject.contains(e2.getX(), e2.getY())) {
				ret = mSelObject.onScroll(e1, e2, distanceX, distanceY);
				invalidate();
			}
		}

		return ret;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false; // do nothing here
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub		
	}

	@Override
	public void onBoundary(ListView view, boolean top, boolean left) {				
		if (top) {
			mArrowUp.setVisible(false);
			mArrowDown.setVisible(true);
		} else {
			mArrowUp.setVisible(true);
			mArrowDown.setVisible(false);
		}
	}

	@Override
	public void onItemClicked(ListView view, ListItem li, int posn) {
		Message msg = mHandler.obtainMessage();
		msg.obj  = ((ActionItem) li).getAction().getActionID();
		msg.what = 1;
		mHandler.sendMessage(msg);
		
		// automatically label "Labeling activity"
		long lastLabelingTime = DataStorage.GetValueLong(
			getContext(), USCTeensGlobals.LAST_DATA_LOADING_TIME, 0
		);	
		long currentTime = System.currentTimeMillis();
		if (currentTime - lastLabelingTime > 1000 * 300) { // 5 minutes
			// add the label "Labeling activity"			
			Labeler.addLabel(new Date(), "", true);
			// update the last labeling time
			DataStorage.SetValue(
				getContext(), USCTeensGlobals.LAST_LABELING_TIME, currentTime
			);
		}
	}

	@Override
	public void onListViewScrolling(ListView view, int dx, int dy) {	
		mArrowUp.setVisible(true);
		mArrowDown.setVisible(true);		
	}
}
