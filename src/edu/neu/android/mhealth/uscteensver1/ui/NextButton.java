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

public class NextButton extends CustomButton {

    protected int mColor = 0xff0066ff;
    protected float mTextX = 0;
    protected float mTextY = 0;
    protected Paint mPaintText = null;

    public NextButton(Resources res) {
        super(res);

        mPaintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintText.setColor(Color.YELLOW);
        mPaintText.setStyle(Style.FILL);
        mPaintText.setTypeface(Typeface.createFromAsset(TeensAppManager.getAppAssets(), "font/arial_bold.ttf"));
        mPaintText.setFakeBoldText(false);
        mPaintText.setTextSize(AppScale.doScaleT(34));
        mPaintText.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    public void onSizeChanged(int width, int height) {
        if (mCanvasWidth == width && mCanvasHeight == height) {
            return;
        }
        mCanvasWidth = width;
        mCanvasHeight = height;

        Rect bounds = new Rect();
        mPaintText.getTextBounds("NEXT", 0, 4, bounds);
        mWidth = bounds.width();
        mHeight = bounds.height();
        mX = width * (1 - 0.07f);
        mY = height * 0.96f;
    }

    @Override
    public boolean contains(float x, float y) {
        float left = mX - mWidth;
        float right = mX + mWidth;
        float upper = mY - mHeight * 2f;
        float bottom = mY + mHeight;

        return left < x && x < right && upper < y && y < bottom;
    }

    @Override
    public void onDraw(Canvas c) {
        c.drawText("NEXT", mX, mY, mPaintText);
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
        mPaintText.setColor(Color.YELLOW);
        return true;
    }

    @Override
    public void onCancelSelection(MotionEvent e) {
        mPaintText.setColor(Color.YELLOW);
    }

}
