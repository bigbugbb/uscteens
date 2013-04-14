package edu.neu.android.mhealth.uscteensver1.ui;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.Typeface;
import edu.neu.android.mhealth.uscteensver1.R;
import edu.neu.android.mhealth.uscteensver1.USCTeensGlobals;
import edu.neu.android.mhealth.uscteensver1.pages.AppScale;

public class HomeBackground extends Background {
	
	private Paint  mPaint;
	private String mVersion;
	private Rect   mRect = new Rect();

	public HomeBackground(Resources res) {
		super(res);		
		loadImages(new int[]{ R.drawable.background_home });
		
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setColor(Color.LTGRAY);
		mPaint.setStyle(Style.STROKE);
		mPaint.setTypeface(Typeface.createFromAsset(USCTeensGlobals.sContext.getAssets(), "font/arial.ttf"));
		mPaint.setTextSize(AppScale.doScaleT(30));
		
		mVersion = USCTeensGlobals.VERSION_NAME;
		mPaint.getTextBounds(mVersion, 0, mVersion.length(), mRect);				
	}	
	
	@Override
	public void onSizeChanged(int width, int height) {
		mWidth  = mImages.get(0).getWidth();
		mHeight = mImages.get(0).getHeight();
		mX = (width  - mWidth)  / 2;
		mY = (height - mHeight) / 2;
		
		mRect.offsetTo(width - mRect.width() - mRect.height(), 
				height - mRect.height());
	}

	@Override
	public void onDraw(Canvas c) {
		c.drawColor(Color.WHITE);
		c.drawBitmap(mImages.get(0), mX, mY, null);
		c.drawText(mVersion, mRect.left, mRect.top, mPaint);
	}
	
}
