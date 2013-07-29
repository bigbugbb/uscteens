package edu.neu.android.mhealth.uscteensver1.ui;

import java.util.ArrayList;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.view.MotionEvent;
import edu.neu.android.mhealth.uscteensver1.R;
import edu.neu.android.mhealth.uscteensver1.TeensAppManager;
import edu.neu.android.mhealth.uscteensver1.data.ChunkManager;
import edu.neu.android.mhealth.uscteensver1.pages.AppObject;
import edu.neu.android.mhealth.uscteensver1.pages.AppScale;


public class SlideBar extends AppObject {
	// canvas size
	protected int mCanvasWidth  = 0;
	protected int mCanvasHeight = 0;
	// all paints and rects
	protected Paint mPaintText = null;
	protected Paint mPaint1 = new Paint();
	protected Paint mPaint2 = new Paint(); 
	protected Paint mPaint3 = new Paint();	
	protected RectF mRect1 = new RectF();
	protected RectF mRect2 = new RectF();
	protected RectF mRect3 = new RectF();
	// range for unmarked chunks
	protected ArrayList<Float> mRange = null;
	// points for drawing separate lines
	protected float[] mPoints = new float[12]; 
	// slide bar button position
	protected float mSliderBarBtnX = 0;
	protected float mSliderBarBtnY = 0;
	
	protected OnSlideBarChangeListener mListener = null;
	
	public interface OnSlideBarChangeListener {
		void onProgressChanged(SlideBar slideBar, int progress);
	}
	
	public SlideBar(Resources res) {
		super(res);
		loadImages(new int[]{ R.drawable.slidebar_btn });
		
		mPaint1.setAntiAlias(true);
		mPaint1.setARGB(255, 230, 230, 230);                 
		mPaint1.setStrokeWidth(4.0f);
		mPaint1.setStyle(Style.STROKE);
		
		mPaint2.setAntiAlias(true);		    
		mPaint2.setARGB(255, 147, 187, 211);
		mPaint2.setStyle(Style.FILL);
		
		mPaint3.setAntiAlias(true);		    
		mPaint3.setARGB(255, 182, 46, 41);
		mPaint3.setStyle(Style.FILL);
		
		mPaintText = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaintText.setColor(Color.WHITE);
		mPaintText.setStyle(Style.FILL);
		mPaintText.setTypeface(Typeface.createFromAsset(TeensAppManager.getAppAssets(), "font/arial.ttf"));
		mPaintText.setFakeBoldText(false);
		mPaintText.setTextSize(AppScale.doScaleT(24));
		mPaintText.setTextAlign(Paint.Align.CENTER);
		
	}

	public void setOnSlideBarChangeListener(OnSlideBarChangeListener listener) {
		mListener = listener;
	}
	
	public void updateUnmarkedRange() {
		mRange = ChunkManager.getUnmarkedRange();

		if (mCanvasWidth != 0) {
			for (int i = 0; i < mRange.size(); ++i) {				
				if (mRange.get(i) >= 0 && mRange.get(i) <= 1) {
					mRange.set(i, mRange.get(i) * mWidth + mCanvasWidth * 0.15f + 2);
				}
			}
		}
	}
	
	@Override
	public void onDraw(Canvas c) {		    	    
	    c.drawRoundRect(mRect2, 12, 12, mPaint2);
	    if (mRange != null) {
		    for (int i = 0; i < mRange.size(); i += 2) {
		    	RectF rect = new RectF(mRect3);
		    	rect.left  = mRange.get(i);
		    	rect.right = mRange.get(i + 1);
		    	c.drawRect(rect, mPaint3);
		    }
	    }
	    c.drawRoundRect(mRect1, 15, 15, mPaint1);
	    c.drawLines(mPoints, mPaint1);
	    c.drawText("AM", mCanvasWidth * 0.2f, mCanvasHeight * 0.99f, mPaintText);
	    c.drawText("PM", mCanvasWidth * 0.8f, mCanvasHeight * 0.99f, mPaintText);
	    c.drawBitmap(mImages.get(0), mSliderBarBtnX, mSliderBarBtnY, null);
	}

	@Override
	public void onSizeChanged(int width, int height) {
		if (mCanvasWidth == width && mCanvasHeight == height) {
			return;
		}
		mCanvasWidth  = width;
		mCanvasHeight = height;			
		
		mRect1.left   = width * 0.15f;
		mRect1.top    = mCanvasHeight * 0.92f;
		mRect1.right  = width * 0.85f;
		mRect1.bottom = mCanvasHeight * 0.96f;
	    
		mRect2.left   = width * 0.15f + 2;
		mRect2.top    = mCanvasHeight * 0.92f + 2;
		mRect2.right  = width * 0.85f - 2;
		mRect2.bottom = mCanvasHeight * 0.96f - 2;
	    
		mRect3.left   = width * 0.15f + 2;
		mRect3.top    = mCanvasHeight * 0.92f + 2;
		mRect3.right  = width * 0.85f - 2;
		mRect3.bottom = mCanvasHeight * 0.96f - 2;
		
		float step = (mRect2.right - mRect2.left) / 4;
		float base = mRect2.left + step;
		for (int i = 0; i < mPoints.length; i += 4) {
			mPoints[i + 0] = base + (i >> 2) * step;  
			mPoints[i + 1] = mCanvasHeight * 0.96f;
			mPoints[i + 2] = mPoints[i + 0];
			mPoints[i + 3] = mCanvasHeight * 0.907f;
		}
		
		// scale the slide bar button image
		float radio = mImages.get(0).getHeight() / (float) mImages.get(0).getWidth();	
		float dstWidth  = width * 0.033f;
		float dstHeight = width * 0.033f * radio;

		Bitmap newImage = 
			Bitmap.createScaledBitmap(mImages.get(0), (int) dstWidth, (int) dstHeight, true);	
		mImages.get(0).recycle();
		mImages.set(0, newImage);		
		
		mSliderBarBtnX = width * 0.15f + 2;
		mSliderBarBtnY = height * 0.92f + 2 - (dstHeight - (height * 0.04f - 4)) / 2;
		
		mX = width * 0.15f + 2;
		mY = mSliderBarBtnY;
		mWidth  = width * 0.7f - 4;
		mHeight = dstHeight;
		
		updateUnmarkedRange();
	}

	@Override
	public boolean onDown(MotionEvent e) {
		float maxX = mCanvasWidth * 0.85f - 2 - mImages.get(0).getWidth();
		mSliderBarBtnX = Math.max(mX, Math.min(maxX, e.getX()));
		
		if (mListener != null) {
			int progress = (int) ((mSliderBarBtnX - mX) / (mWidth - mImages.get(0).getWidth()) * 100);
			mListener.onProgressChanged(this, progress);
		}
		
		return true;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		float maxX = mCanvasWidth * 0.85f - 2 - mImages.get(0).getWidth();
		mSliderBarBtnX = Math.max(mX, Math.min(maxX, e2.getX()));
		
		if (mListener != null) {
			int progress = (int) ((mSliderBarBtnX - mX) / (mWidth - mImages.get(0).getWidth()) * 100);
			mListener.onProgressChanged(this, progress);
		}
		
		return true;
	}

	public void moveSliderBarToProgress(float progress) {
		float maxX = mCanvasWidth * 0.85f - 2 - mImages.get(0).getWidth();
		float curX = (maxX - mRect2.left) * progress + mRect2.left;
		mSliderBarBtnX = Math.min(curX, maxX);
	}
}
