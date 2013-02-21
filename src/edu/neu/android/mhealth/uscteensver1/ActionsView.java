package edu.neu.android.mhealth.uscteensver1;

import java.util.ArrayList;

import edu.neu.android.mhealth.uscteensver1.data.DataSource;
import edu.neu.android.mhealth.uscteensver1.data.WeekdayCalculator;
import edu.neu.android.mhealth.uscteensver1.ui.ButtonArrow;
import edu.neu.android.mhealth.uscteensver1.ui.ListView;
import edu.neu.android.mhealth.uscteensver1.ui.ListView.ListItem;
import edu.neu.android.mhealth.uscteensver1.ui.ListView.OnItemClickListener;
import edu.neu.android.mhealth.uscteensver1.ui.ListView.OnListViewScrollingListener;
import edu.neu.android.mhealth.uscteensver1.ui.ListView.OnReachedEndListener;
import edu.neu.android.mhealth.uscteensver1.ui.ListViewActions;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Style;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.GestureDetector.OnGestureListener;
import android.widget.ImageView;

public class ActionsView extends ImageView implements OnGestureListener, 
													  OnItemClickListener, 
													  OnReachedEndListener,
													  OnListViewScrollingListener {
	
	protected ButtonArrow mArrowUp   = null;
	protected ButtonArrow mArrowDown = null;
	protected ListViewActions mActionList = null;
	protected AppScale  mAppScale  = AppScale.getInstance();
	protected AppObject mSelObject = null;	
	protected ArrayList<AppObject> mObjects = new ArrayList<AppObject>();
	
	protected boolean mImageLoaded = false;
	protected ArrayList<Bitmap> mImages = new ArrayList<Bitmap>();	
	protected String  mDate = "";
	protected String  mTime = "";
	protected String  mTimePostfix = "";
	protected RectF   mBackArea  = new RectF();
	protected PointF  mBackTxtPt = new PointF();
	protected Paint   mPaintText = null;	
	protected Paint   mPaintDate = null;
	protected Paint   mPaintTime = null;
	protected Handler mHandler = null;
	
	protected int mExpectedWidth = 0;	
	
	// gesture detector
	protected GestureDetector mGestureDetector = new GestureDetector(this);

	public ActionsView(Context context, AttributeSet attrs) {
		super(context, attrs);	
		
		mArrowUp   = new ButtonArrow(context.getResources());
		mArrowUp.setVisible(false);
		mArrowUp.changeArrowDir(false);
		//mArrowUp.setVisible(false);
		mArrowDown = new ButtonArrow(context.getResources());		
		mActionList = new ListViewActions(context.getResources());
		mActionList.setOnItemClickListener(this);
		mActionList.setOnReachedEndListener(this);
		mActionList.setOnListViewScrollingListener(this);
		mObjects.add(mArrowUp);
		mObjects.add(mArrowDown);
		mObjects.add(mActionList);
		
		loadImages(new int[]{ 
			R.drawable.popup_win_background, R.drawable.back_blue  
		});
				
		mPaintText = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaintText.setColor(Color.WHITE);
		mPaintText.setStyle(Style.STROKE);
		mPaintText.setTextSize(mAppScale.doScaleT(34));
		mPaintText.setTypeface(Typeface.SERIF);
		mPaintText.setFakeBoldText(false);		
		
		mPaintDate = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaintDate.setColor(Color.WHITE);
		mPaintDate.setStyle(Style.FILL);
		mPaintDate.setTextSize(mAppScale.doScaleW(45));
		mPaintDate.setTypeface(Typeface.SERIF);
		mPaintDate.setTextAlign(Align.CENTER);		
		mPaintDate.setFakeBoldText(true);		
		
		mPaintTime = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaintTime.setColor(Color.WHITE);
		mPaintTime.setStyle(Style.FILL);		
		mPaintTime.setTypeface(Typeface.SERIF);
		mPaintTime.setTextAlign(Align.RIGHT);		
		mPaintTime.setFakeBoldText(false);	
		
		DataSource dataSrc = DataSource.getInstance(null);
		mDate = convertDateToDisplayFormat(dataSrc.getCurSelectedDate());
	}
	
	private String convertDateToDisplayFormat(String date) {
		String[] MONTHS = {
			"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JULY", "AUG", "SEPT", "OCT", "NOV", "DEC"			
		};
		
		String[] times = date.split("-");
		String weekday = WeekdayCalculator.getWeekday(date);
		String month   = MONTHS[Integer.parseInt(times[1]) - 1];
		String day     = times[2];		
		String formatted = " " + weekday.toUpperCase() + "  " + month + "  " + day;
		
		return formatted;
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
	
	public void setTime(int time) {
		int hour   = time / 3600;
		int minute = (time - hour * 3600) / 60;
		
		mTime = (hour > 12 ? hour - 12 : hour) + ":" + 
					(minute > 9 ? minute : "0" + minute);				
		mTimePostfix = (hour <= 12) ? "am" : "pm";  
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
        	if (mAppScale != null) {
        		dstWidth  = mAppScale.doScaleW(dstWidth);
        		dstHeight = mAppScale.doScaleH(dstHeight);
        		if (dstWidth != origin.getWidth() || dstHeight != origin.getHeight()) {
        			scaled = Bitmap.createScaledBitmap(origin, (int) dstWidth, (int) dstHeight, true);
        		}
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
		mBackArea.left   = mAppScale.doScaleW(10);
		mBackArea.right  = mBackArea.left + mImages.get(1).getWidth();
		mBackArea.top    = mAppScale.doScaleH(5);
		mBackArea.bottom = mBackArea.top + mImages.get(1).getHeight();
				
		mBackTxtPt.x = mBackArea.left + mAppScale.doScaleW(33);
		mBackTxtPt.y = mBackArea.bottom - mAppScale.doScaleH(52);
	}
	
	public int getExpectedWidth() {
		return mExpectedWidth;
	}	

	@Override
	protected void onDraw(Canvas canvas) {		
		canvas.drawBitmap(mImages.get(0), 0, 0, null);
		canvas.drawBitmap(mImages.get(1), mBackArea.left, mBackArea.top, null);
		canvas.drawText("BACK", mBackTxtPt.x, mBackTxtPt.y, mPaintText);
		canvas.drawText(mDate, getWidth() / 2, mAppScale.doScaleH(80), mPaintDate);
		mPaintTime.setTextSize(mAppScale.doScaleW(38));
		canvas.drawText(mTime, getWidth() - mAppScale.doScaleW(30), mAppScale.doScaleH(60), mPaintTime);
		mPaintTime.setTextSize(mAppScale.doScaleW(36));
		canvas.drawText(mTimePostfix, getWidth() - mAppScale.doScaleW(40), mAppScale.doScaleH(100), mPaintTime);
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
		void OnBackClicked();
	}		
	
	public void setOnBackClickedListener(OnBackClickedListener listener) {
		mListener = listener;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		mActionList.setPosn(0, mAppScale.doScaleH(180) + mActionList.getBorderWidth());
		mActionList.onSizeChanged(w, h);		
		mArrowUp.setX(0);
		mArrowUp.setY(mAppScale.doScaleH(130));
		mArrowUp.onSizeChanged(w, h);
		mArrowDown.setX(0);
		mArrowDown.setY(h - mAppScale.doScaleH(50));
		mArrowDown.onSizeChanged(w, h);
		super.onSizeChanged(w, h, oldw, oldh);
	}
	
	public boolean onDown(MotionEvent e) {		
		float x = e.getX();
		float y = e.getY();
		boolean ret = false;
		
		if (mBackArea.contains(e.getX(), e.getY())) {
			if (mListener != null) {
				mListener.OnBackClicked();
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
	public void onReachedEnd(ListView view, boolean top, boolean left) {				
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
		msg.obj  = Integer.valueOf(posn);
		msg.what = 1;
		mHandler.sendMessage(msg);			
	}

	@Override
	public void onListViewScrolling(ListView view, int dx, int dy) {	
		mArrowUp.setVisible(true);
		mArrowDown.setVisible(true);		
	}
}
