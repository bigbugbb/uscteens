package edu.neu.android.mhealth.uscteensver1.ui;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import edu.neu.android.mhealth.uscteensver1.R;

public class HomeBackground extends Background {

	public HomeBackground(Resources res) {
		super(res);		
		loadImages(new int[]{ R.drawable.background_home });
	}	
	
	@Override
	public void onSizeChanged(int width, int height) {		
		mWidth  = mImages.get(0).getWidth();
		mHeight = mImages.get(0).getHeight();
		mX = (width  - mWidth)  / 2;
		mY = (height - mHeight) / 2;
	}

	@Override
	public void onDraw(Canvas c) {
		c.drawColor(Color.WHITE);
		c.drawBitmap(mImages.get(0), mX, mY, null);
	}
	
}
