package edu.neu.android.mhealth.uscteensver1.ui;

import android.content.res.Resources;
import android.graphics.Canvas;
import edu.neu.android.mhealth.uscteensver1.R;
import edu.neu.android.mhealth.uscteensver1.main.AppObject;

public class HomeTitle extends AppObject {

	public HomeTitle(Resources res) {
		super(res);
		loadImages(new int[]{ R.drawable.title });
		setKind(TITLE);
		setZOrder(ZOrders.TITLE);
	}	
	
	@Override
	public void onSizeChanged(int width, int height) {		
		mWidth  = mImages.get(0).getWidth();
		mHeight = mImages.get(0).getHeight();
		mX = (width - mWidth) / 2;
		mY = (height - mHeight) * 0.45f;
	}

	@Override
	public void onDraw(Canvas c) {
		c.drawBitmap(mImages.get(0), mX, mY, null);
	}
}
