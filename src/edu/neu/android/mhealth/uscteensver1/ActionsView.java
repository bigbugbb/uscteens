package edu.neu.android.mhealth.uscteensver1;

import java.util.ArrayList;

import edu.neu.android.mhealth.uscteensver1.ui.ButtonArrow;
import edu.neu.android.mhealth.uscteensver1.ui.ListView;
import edu.neu.android.mhealth.uscteensver1.ui.ListView.OnItemClickListener;
import edu.neu.android.mhealth.uscteensver1.ui.ListView.OnReachedEndListener;
import edu.neu.android.mhealth.uscteensver1.ui.ListViewActions;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Style;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.GestureDetector.OnGestureListener;
import android.view.View;
import android.widget.ImageView;

public class ActionsView extends ImageView implements OnGestureListener, 
													  OnItemClickListener, 
													  OnReachedEndListener {
	
	protected ButtonArrow mArrowUp   = null;
	protected ButtonArrow mArrowDown = null;
	protected ListViewActions mListViewActions = null;
	protected AppObject mSelObject = null;
	protected ArrayList<AppObject> mObjects = new ArrayList<AppObject>();
	
	protected boolean mImageLoaded = false;
	protected ArrayList<Bitmap> mImages = new ArrayList<Bitmap>();	
	protected RectF  mBackArea  = new RectF();
	protected PointF mBackTxtPt = new PointF();
	protected Paint  mPaintText = null;	
	protected Handler mHandler = null;
	
	protected int mExpectedWidth  = 0;	
	
	// gesture detector
	protected GestureDetector mGestureDetector = new GestureDetector(this);

	public ActionsView(Context context, AttributeSet attrs) {
		super(context, attrs);	
		
		mArrowUp   = new ButtonArrow(context.getResources());
		mArrowDown = new ButtonArrow(context.getResources());
		mListViewActions = new ListViewActions(context.getResources());
		mListViewActions.setOnItemClickListener(this);
		mListViewActions.setOnReachedEndListener(this);
		mObjects.add(mArrowUp);
		mObjects.add(mArrowDown);
		mObjects.add(mListViewActions);
		
		loadImages(new int[]{ 
			R.drawable.popup_win_background, R.drawable.back_blue  
		});
		
		mPaintText = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaintText.setColor(Color.WHITE);
		mPaintText.setStyle(Style.STROKE);
		mPaintText.setTextSize(34);
		mPaintText.setTypeface(Typeface.SERIF);
		mPaintText.setFakeBoldText(false);		
	}
	
	// used to update the view for drawing
	Runnable mRunnable = new Runnable() {		
		
		public void run() {
			invalidate();
			if (Math.abs(mListViewActions.getSpeedY()) > 0) {
				mHandler.postDelayed(this, 10);
			} else {
				mHandler.removeCallbacks(this);
			}
		}
	};
	
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
        	mImages.add(BitmapFactory.decodeResource(
        		getContext().getResources(), id, options)
        	);
        }
        
        AppScale scale = AppScale.getInstance();        
        mExpectedWidth  = (int) (mImages.get(0).getWidth() * scale.getScaleW());        
        
		mBackArea.left   = 10;
		mBackArea.right  = mBackArea.left + mImages.get(1).getWidth();
		mBackArea.top    = 5;
		mBackArea.bottom = mBackArea.top + mImages.get(1).getHeight();
				
		mBackTxtPt.x = mBackArea.left + 33;
		mBackTxtPt.y = mBackArea.bottom - 52;
	}
	
	public int getExpectedWidth() {
		return mExpectedWidth;
	}	

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		//super.onDraw(canvas);
		canvas.drawBitmap(mImages.get(0), 0, 0, null);
		canvas.drawBitmap(mImages.get(1), mBackArea.left, mBackArea.top, null);
		canvas.drawText("BACK", mBackTxtPt.x, mBackTxtPt.y, mPaintText);
		mArrowUp.onDraw(canvas);
		mArrowDown.onDraw(canvas);
		mListViewActions.onDraw(canvas);		
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
		mListViewActions.setPosn(1, mImages.get(0).getHeight());
		mListViewActions.onSizeChanged(w, h);		
		mArrowUp.setX((w - mArrowUp.getWidth()) / 2);
		mArrowUp.setY(h - 55);
		mArrowUp.onSizeChanged(w, h);
		mArrowDown.setX((w - mArrowDown.getWidth()) / 2);
		mArrowDown.setY(h - 55);
		mArrowDown.onSizeChanged(w, h);
		super.onSizeChanged(w, h, oldw, oldh);
	}
	
	public boolean onDown(MotionEvent e) {
		boolean ret = false;
		float x = e.getX();
		float y = e.getY();			
		
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
//		if (top) {
//			mListViewActions.setPosn(1, mImages.get(0).getHeight());		
//			mArrow.setY(getHeight() - 55);
//			mArrow.changeArrowDir(true);			
//		} else {			
//			mListViewActions.setPosn(1, 200);							
//			mArrow.setY(145);
//			mArrow.changeArrowDir(false);			
//		}
	}

	@Override
	public void onItemClicked(ListView view, int posn) {
		Message msg = mHandler.obtainMessage();
		msg.obj  = Integer.valueOf(posn);
		msg.what = 1;
		mHandler.sendMessage(msg);			
	}
}
