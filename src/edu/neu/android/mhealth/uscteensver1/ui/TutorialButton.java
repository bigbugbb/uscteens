package edu.neu.android.mhealth.uscteensver1.ui;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.view.MotionEvent;
import edu.neu.android.mhealth.uscteensver1.TeensAppManager;
import edu.neu.android.mhealth.uscteensver1.pages.AppScale;

public class TutorialButton extends CustomButton {

    private int    mColor;
    private Paint  mPaint;
    private String mTutorial;
    private Rect   mRect;

    public TutorialButton(Resources res) {
        super(res);

        mColor = 0xff0066ff;
        		
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.LTGRAY);
        mPaint.setStyle(Style.STROKE);
        mPaint.setTypeface(Typeface.createFromAsset(TeensAppManager.getAppAssets(), "font/arial.ttf"));
        mPaint.setTextSize(AppScale.doScaleT(30));
        mPaint.setTextAlign(Paint.Align.LEFT);

        mTutorial = "Play tutorial";
        mRect = new Rect();
        mPaint.getTextBounds(mTutorial, 0, mTutorial.length(), mRect);
    }

    @Override
    public void onSizeChanged(int width, int height) {
        mWidth = mRect.width();
        mHeight = mRect.height();
        mX = mRect.height();
        mY = height - (mRect.height() << 1);

        mRect.offsetTo(mRect.height(), height - mRect.height());
    }

    @Override
    public void onDraw(Canvas c) {
        c.drawText(mTutorial, mRect.left, mRect.top, mPaint);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        mPaint.setColor(mColor);
        return true;
    }

    @Override
    public boolean onUp(MotionEvent e) {
        if (mListener != null) {
            mListener.onClick(this);
        }
        mPaint.setColor(Color.LTGRAY);
        return true;
    }

    @Override
    public void onCancelSelection(MotionEvent e) {
        mPaint.setColor(Color.LTGRAY);
    }

    @Override
    public boolean contains(float x, float y) {
        return (mX < x && x <= mX + mWidth) && (mY - mHeight < y && y <= mY + mHeight * 2);
    }
}
