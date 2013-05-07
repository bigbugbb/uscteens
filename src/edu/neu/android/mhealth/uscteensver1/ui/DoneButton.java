package edu.neu.android.mhealth.uscteensver1.ui;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.view.MotionEvent;
import edu.neu.android.mhealth.uscteensver1.R;
import edu.neu.android.mhealth.uscteensver1.USCTeensGlobals;
import edu.neu.android.mhealth.uscteensver1.pages.AppScale;

public class DoneButton extends CustomButton {

	protected int   mColor = 0xff0066ff;
	protected float mTextX = 0;
	protected float mTextY = 0;	
	protected Paint mPaintText = null;	

	public DoneButton(Resources res) {
		super(res);
		loadImages(new int[]{ R.drawable.done_btn });
		
		mPaintText = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaintText.setColor(Color.WHITE);
		mPaintText.setStyle(Style.FILL);
		mPaintText.setTypeface(Typeface.createFromAsset(USCTeensGlobals.sContext.getAssets(), "font/arial.ttf"));
		mPaintText.setFakeBoldText(true);
		mPaintText.setTextSize(AppScale.doScaleT(42));
		mPaintText.setTextAlign(Paint.Align.CENTER);
	}

	@Override
	public void onSizeChanged(int width, int height) {		
		mWidth  = mImages.get(0).getWidth();
		mHeight = mImages.get(0).getHeight() / 2;
		mX = width * 0.25f - mWidth / 2;
		mY = height * 0.84f;
		mTextX = width * 0.25f;
		mTextY = mY + mHeight * 1.35f;
	}

	@Override
	public void onDraw(Canvas c) {
		c.drawBitmap(mImages.get(0), mX, mY, null);
		c.drawText("DONE", mTextX, mTextY, mPaintText);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		mPaintText.setColor(mColor);
		return true;
	}

	@Override
	public boolean onUp(MotionEvent e) {
		if (mListener != null) {
			mListener.onClick(this);
		}
		mPaintText.setColor(Color.WHITE);
		return true;
	}

	@Override
	public void onCancelSelection(MotionEvent e) {
		mPaintText.setColor(Color.WHITE);
	}

}