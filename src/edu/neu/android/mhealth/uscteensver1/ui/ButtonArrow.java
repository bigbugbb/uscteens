package edu.neu.android.mhealth.uscteensver1.ui;

import edu.neu.android.mhealth.uscteensver1.R;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.Paint.Style;


public class ButtonArrow extends CustomButton { 
	
	protected Paint mPaint = null;
	protected boolean mDown = true;

	public ButtonArrow(Resources res) {
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
	
	public void changeArrowDir(boolean down) {
		mDown = down;
	}

	@Override
	public void onSizeChanged(int width, int height) {
		mCanvasWidth  = width;
		mCanvasHeight = height;
	}

	@Override
	public void onDraw(Canvas c) {		
		c.drawRect(mX, mY, mX + mCanvasWidth / 2, mY + sAppScale.doScaleH(50), mPaint);
		if (mVisible) {
			c.drawBitmap(mImages.get(mDown ? 0 : 1), mX + mCanvasWidth / 4 - mWidth / 2, mY + mHeight * 0.15f, null);
		}
	}

}