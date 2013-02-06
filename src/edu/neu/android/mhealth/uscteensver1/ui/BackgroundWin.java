package edu.neu.android.mhealth.uscteensver1.ui;

import edu.neu.android.mhealth.uscteensver1.R;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.Paint.Style;

public class BackgroundWin extends Background {
	
	protected float	mText1X = 0;
	protected float	mText1Y = 0;
	protected float mText2X = 0;
	protected float mText2Y = 0;
	protected float mBarHeight = 0;
	protected float mCanvasWidth  = 0;
	protected float mCanvasHeight = 0;
	protected Paint mPaintText1 = null;
	protected Paint mPaintText2 = null;

	public BackgroundWin(Resources res) {
		super(res);
		loadImages(new int[]{ 
			R.drawable.congratulations_bar, R.drawable.win_background
		});
		
		mPaintText1 = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaintText1.setColor(Color.WHITE);
		mPaintText1.setStyle(Style.FILL);
		mPaintText1.setTypeface(Typeface.SERIF);
		mPaintText1.setFakeBoldText(true);
		mPaintText1.setTextSize(50);
		mPaintText1.setTextAlign(Paint.Align.CENTER);
		
		mPaintText2 = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaintText2.setColor(Color.BLACK);
		mPaintText2.setStyle(Style.FILL);
		mPaintText2.setTypeface(Typeface.SERIF);
		mPaintText2.setFakeBoldText(true);
		mPaintText2.setTextSize(46);
		mPaintText2.setTextAlign(Paint.Align.CENTER);
	}

	@Override
	public void onSizeChanged(int width, int height) {		
		for (int i = 0; i < mImages.size(); ++i) {
			//float radio = mImages.get(i).getWidth() / (float) mImages.get(i).getHeight();	
			int scaledWidth  = width;
			int scaledHeight = (int)mImages.get(i).getHeight();
			
			if (scaledWidth == mImages.get(i).getWidth() && 
				scaledHeight == mImages.get(i).getHeight()) {
				continue;
			}
			
			Bitmap newImage = 
				Bitmap.createScaledBitmap(mImages.get(i), scaledWidth, scaledHeight, true);	
			mImages.get(i).recycle(); // explicit call to avoid out of memory
			mImages.set(i, newImage);			
			System.gc();
		}
		
		mWidth  = mImages.get(1).getWidth();
		mHeight = mImages.get(1).getHeight();
		mCanvasWidth  = width;
		mCanvasHeight = height;
		mBarHeight = mImages.get(0).getHeight();

		mText1X = width / 2;
		mText1Y = height * 0.13f;
		mText2X = width / 2;
		mText2Y = height * 0.35f;
	}

	@Override
	public void onDraw(Canvas c) {		
		c.drawBitmap(mImages.get(0), 0, 0, null);
		c.drawText("CONGRATULATIONS", mText1X, mText1Y, mPaintText1);
		c.drawBitmap(mImages.get(1), (mCanvasWidth - mWidth) / 2, mBarHeight, null);
		c.drawText("You have completed", mText2X, mText2Y, mPaintText2);
		c.drawText("the Teen Activity Game", mText2X, mText2Y + 90, mPaintText2);
	}
	
}