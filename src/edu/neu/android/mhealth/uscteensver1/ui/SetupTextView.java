package edu.neu.android.mhealth.uscteensver1.ui;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import edu.neu.android.mhealth.uscteensver1.TeensAppManager;
import edu.neu.android.mhealth.uscteensver1.pages.AppScale;

public class SetupTextView extends TextView {
	
	public SetupTextView(Resources res) {
		super(res);
		mPaintTxt.setColor(Color.RED);
		mPaintTxt.setTypeface(Typeface.createFromAsset(TeensAppManager.getAppAssets(), "font/arial.ttf"));
		mPaintTxt.setTextAlign(Align.CENTER);
		mPaintBoard.setStrokeWidth(Math.min(2, AppScale.doScaleW(3)));
		setVisible(false);
	}

	@Override
	public void onSizeChanged(int width, int height) {		
		mCanvasWidth  = width;
		mCanvasHeight = height;
		
		mRect.left   = width * 0.13f;
		mRect.right  = width * 0.87f;
		mRect.top    = height * 0.6f;
		mRect.bottom = height * 0.86f;
		
		mX = mRect.left;
		mY = mRect.top;
		mWidth  = mRect.width();
		mHeight = mRect.height();
	}

	@Override
	public void onDraw(Canvas c) {
		if (!mVisible) {
			return;
		}
		c.drawRoundRect(mRect, 16, 16, mPaintBkg);
		c.drawRoundRect(mRect, 16, 16, mPaintBoard);
		 
		c.drawText("This phone needs to be setup for", mCanvasWidth / 2, 
				mRect.top + AppScale.doScaleT(72), mPaintTxt);
		c.drawText("the Teen Activity Game to start!", mCanvasWidth / 2, 
				mRect.top + AppScale.doScaleT(143), mPaintTxt);
	}
}
