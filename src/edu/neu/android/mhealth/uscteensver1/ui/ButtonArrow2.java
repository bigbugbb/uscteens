package edu.neu.android.mhealth.uscteensver1.ui;

import android.content.res.Resources;
import android.graphics.Canvas;

public class ButtonArrow2 extends ButtonArrow {

	public ButtonArrow2(Resources res) {
		super(res);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onDraw(Canvas c) {
		c.drawRect(0, mY - 15, mCanvasWidth, mCanvasHeight, mPaint);
		c.drawBitmap(mImages.get(mDown ? 0 : 1), mX, mY, null);
	}

}
