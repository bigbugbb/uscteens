package edu.neu.android.mhealth.uscteensver1.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import edu.neu.android.mhealth.uscteensver1.R;
import edu.neu.android.mhealth.uscteensver1.TeensGlobals;
import edu.neu.android.mhealth.uscteensver1.pages.AppScale;

public class ProgressView extends View {

	private final static int PROGRESS_SPEED = 10;
	
	private Paint  mPaint;
	private String mText = "Loading...";
	private RectF  mRect;
	private Bitmap mImage;
	private Canvas mImageCanvas = null;
	private int    mOffset = 0;
	private int    mBkColor = Color.argb(150, 0, 0, 0);
	
	public ProgressView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setVisibility(View.GONE);
		
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setColor(Color.WHITE);
		mPaint.setStyle(Style.STROKE);				
		mPaint.setTypeface(Typeface.createFromAsset(TeensGlobals.sContext.getAssets(), "font/arial.ttf"));		
		mPaint.setFakeBoldText(false);
		
		setBackgroundColor(mBkColor);
	}

	public void show(String text) {
		mText = text;	
		this.setVisibility(View.VISIBLE);			
	}
	
	public void dismiss() {
		this.setVisibility(View.GONE);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		int w = getWidth();
		int h = getHeight();			
		
		// draw text
		mPaint.setTextAlign(Align.CENTER);
		mPaint.setTextSize(AppScale.doScaleT(40));
		mPaint.setStyle(Style.FILL);	
		mPaint.setStrokeWidth(AppScale.doScaleT(2.0f));
		canvas.drawText(mText, w / 2, h * 0.5f, mPaint);
		mPaint.setStyle(Style.STROKE);		
		// draw lines in the rectangle
		int imgW = mImage.getWidth();
		int imgH = mImage.getHeight();
		Rect  src = new Rect(imgW - mOffset, 0, imgW, imgH);
		RectF dst = new RectF(mRect.left + 2, mRect.top, mRect.left + mOffset - 2, mRect.bottom);
		canvas.drawBitmap(mImage, src, dst, null);		
		src = new Rect(0, 0, imgW - mOffset, imgH);
		dst = new RectF(mRect.left + mOffset - 2, mRect.top, mRect.right - 2, mRect.bottom);
		canvas.drawBitmap(mImage, src, dst, null);
		mOffset = (mOffset >= mRect.width()) ? 0 : mOffset + PROGRESS_SPEED;
		// draw round rectangle
		mPaint.setStrokeWidth(AppScale.doScaleT(7.0f));
		canvas.drawRoundRect(mRect, AppScale.doScaleT(26), AppScale.doScaleT(26), mPaint);
		
		synchronized (this) {
			try {
				wait(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			invalidate();
		}
		
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (this.getVisibility() == View.VISIBLE) {
			return true;
		}
		return super.onTouchEvent(event);		
	}	

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {		
		super.onSizeChanged(w, h, oldw, oldh);
			
		mRect = new RectF();
		mRect.left   = w * 0.2f;
		mRect.right  = w * 0.8f;
		mRect.top    = h * 0.556f;
		mRect.bottom = h * 0.61f;
		
		BitmapFactory.Options options = new BitmapFactory.Options(); 
        options.inPurgeable = true;
        options.inPreferredConfig = Config.RGB_565; 
        mImage = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.transparent, options);
        mImage = Bitmap.createScaledBitmap(mImage, (int) mRect.width(), (int) mRect.height(), true);
        mImageCanvas = new Canvas(mImage);
        
        mPaint.setStrokeWidth(AppScale.doScaleT(1.0f));
		float count = AppScale.doScaleW(15);
		for (float x = w * 0.01f; x < w * 0.585f; x += w * 0.04f) {
			float x1 = x;
			float y1 = h * 0.045f;
			float x2 = x + w * 0.02f;
			float y2 = y1 - w * 0.02f;
			for (float m = 0; m < count; m += 1.0f) {
				mImageCanvas.drawLine(x1 + m, y1, x2 + m, y2, mPaint);
			}
		}
	}
}
