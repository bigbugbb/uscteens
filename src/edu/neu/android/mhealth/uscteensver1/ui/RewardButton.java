package edu.neu.android.mhealth.uscteensver1.ui;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.view.MotionEvent;
import edu.neu.android.mhealth.uscteensver1.R;
import edu.neu.android.mhealth.uscteensver1.TeensAppManager;
import edu.neu.android.mhealth.uscteensver1.pages.AppScale;

public class RewardButton extends CustomButton {

    protected int   mColor;
    protected float mTextX;
    protected float mTextY;
    protected Paint mPaintText;

    public RewardButton(Resources res) {
        super(res);
        loadImages(new int[]{ R.drawable.reward_btn, R.drawable.reward_disable_btn });

        mColor = 0xff0066ff;
        mTextX = 0;
        mTextY = 0;
        		
        mPaintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintText.setColor(Color.WHITE);
        mPaintText.setStyle(Style.FILL);
        mPaintText.setTypeface(Typeface.createFromAsset(TeensAppManager.getAppAssets(), "font/arial.ttf"));
        mPaintText.setFakeBoldText(true);
        mPaintText.setTextSize(AppScale.doScaleT(42));
        mPaintText.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    public void onSizeChanged(int width, int height) {
        mWidth = mImages.get(0).getWidth();
        mHeight = mImages.get(0).getHeight();
        mX = width * 0.93f - mWidth;
        mY = height * 0.84f;
    }

    @Override
    public void onDraw(Canvas c) {
        if (!mVisible) {
            return;
        }
        mTextX = mX + mWidth / 2;
        mTextY = mY + mHeight * 0.675f;
        c.drawBitmap(mImages.get(mEnable ? 0 : 1), mX, mY, null);
        c.drawText("Get reward", mTextX, mTextY, mPaintText);
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
