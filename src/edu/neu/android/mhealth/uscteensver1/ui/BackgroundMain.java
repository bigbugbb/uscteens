package edu.neu.android.mhealth.uscteensver1.ui;

import edu.neu.android.mhealth.uscteensver1.R;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;

public class BackgroundMain extends Background {
	
	protected int mCanvasWidth  = 0;
	protected int mCanvasHeight = 0;

	public BackgroundMain(Resources res) {
		super(res);		
		loadImages(new int[]{ R.drawable.menubar_background });
	}	
	
	@Override
	public void onSizeChanged(int width, int height) {	
		float radio = mImages.get(0).getHeight() / (float) mImages.get(0).getWidth();	
		int scaledWidth  = width;
		int scaledHeight = (int)(width * radio);

		Bitmap newImage = 
			Bitmap.createScaledBitmap(mImages.get(0), scaledWidth, scaledHeight, true);	
		mImages.get(0).recycle(); // explicit call to avoid out of memory
		mImages.set(0, newImage);
		System.gc();
		
		mWidth  = mImages.get(0).getWidth();
		mHeight = mImages.get(0).getHeight();
		mCanvasWidth  = width;
		mCanvasHeight = height;
		mX = 0;
		mY = mCanvasHeight - mHeight;
	}

	@Override
	public void onDraw(Canvas c) {		
		c.drawColor(Color.WHITE);
		c.drawBitmap(mImages.get(0), mX, mY, null);
	}

}
