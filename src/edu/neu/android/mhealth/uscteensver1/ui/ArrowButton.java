package edu.neu.android.mhealth.uscteensver1.ui;

import edu.neu.android.mhealth.uscteensver1.R;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.Paint.Style;


public class ArrowButton extends CustomButton { 
	
	protected Paint mPaint = null;
	protected boolean mDown = true;
	protected boolean mAlignCenter = true;

	public ArrowButton(Resources res) {
		super(res);
		loadImages(new int[]{ R.drawable.popup_wind_arrow, R.drawable.popup_wind_arrow_ops });
		setKind(BUTTON);
		setZOrder(ZOrders.BUTTON);
		mWidth  = mImages.get(0).getWidth();
		mHeight = mImages.get(0).getHeight();
		
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG); 
		mPaint.setColor(Color.WHITE);
		mPaint.setStyle(Style.FILL);
	}
	
	public void alignCenter(boolean alignCenter) {
		mAlignCenter = alignCenter;
	}
	
	public void changeArrowDir(boolean down) {
		mDown = down;
	}

	@Override
	public void onSizeChanged(int width, int height) {
		mCanvasWidth  = width;
		mCanvasHeight = height;
		mWidth  = mCanvasWidth;
		mHeight = sAppScale.doScaleH(50);
	}

	@Override
	public void onDraw(Canvas c) {	
		if (mAlignCenter) {
			c.drawRect(mX, mY, mX + mWidth, mY + mHeight, mPaint);
		} else {
			c.drawRect(mX, mY, mX + mWidth / 2, mY + mHeight, mPaint);
		}
		
		if (mVisible) {
			if (mAlignCenter) {
				c.drawBitmap(mImages.get(mDown ? 0 : 1), 
					(mWidth - mImages.get(0).getWidth()) / 2, mY + mHeight * 0.15f, null);
			} else {
				c.drawBitmap(mImages.get(mDown ? 0 : 1), 
					mX + (mWidth / 2 - mImages.get(0).getWidth()) / 2, mY + mHeight * 0.15f, null);
			}
		}
	}

}
