package edu.neu.android.mhealth.uscteensver1.ui;

import edu.neu.android.mhealth.uscteensver1.R;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.Paint.Style;
import android.view.MotionEvent;

public class ButtonBegin extends CustomButton {

	protected int   mColor = 0xff0066ff;
	protected float mTextX = 0;
	protected float mTextY = 0;
	protected Paint mPaintText = null;	

	public ButtonBegin(Resources res) {
		super(res);
		loadImages(new int[]{ R.drawable.begin_btn });
		
		mPaintText = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaintText.setColor(Color.WHITE);
		mPaintText.setStyle(Style.FILL);
		mPaintText.setTypeface(Typeface.SERIF);
		mPaintText.setFakeBoldText(true);
		mPaintText.setTextSize(sAppScale.doScaleW(46));
		mPaintText.setTextAlign(Paint.Align.CENTER);
	}

	@Override
	public void onSizeChanged(int width, int height) {		
		mWidth  = mImages.get(0).getWidth();
		mHeight = mImages.get(0).getHeight() / 2;
		mX = (width - mWidth) / 2;
		mY = height * 0.625f;
		mTextX = width / 2;
		mTextY = mY + mHeight * 0.6f;
	}

	@Override
	public void onDraw(Canvas c) {
		c.drawBitmap(mImages.get(0), mX, mY, null);
		c.drawText("BEGIN", mTextX, mTextY, mPaintText);
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
