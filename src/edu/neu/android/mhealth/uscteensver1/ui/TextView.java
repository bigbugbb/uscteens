package edu.neu.android.mhealth.uscteensver1.ui;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.view.MotionEvent;

import edu.neu.android.mhealth.uscteensver1.TeensAppManager;
import edu.neu.android.mhealth.uscteensver1.pages.AppObject;
import edu.neu.android.mhealth.uscteensver1.pages.AppScale;


public class TextView extends AppObject {

    protected int mCanvasWidth = 0;
    protected int mCanvasHeight = 0;
    protected Paint mPaintBkg = null;
    protected Paint mPaintTxt = null;
    protected Paint mPaintBoard = null;
    protected RectF mRect = new RectF();
    protected OnClickListener mListener = null;

    protected TextView(Resources res) {
        super(res);
        mKind = TEXTVIEW;
        mZOrder = ZOrders.TEXTVIEW;

        mPaintBkg = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintBkg.setColor(Color.WHITE);
        mPaintBkg.setStyle(Style.FILL);

        mPaintBoard = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintBoard.setColor(Color.BLACK);
        mPaintBoard.setStyle(Style.STROKE);

        mPaintTxt = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintTxt.setColor(Color.BLACK);
        mPaintTxt.setStyle(Style.FILL);
        mPaintTxt.setTextSize(AppScale.doScaleT(45));
        mPaintTxt.setTypeface(Typeface.createFromAsset(TeensAppManager.getAppAssets(), "font/arial.ttf"));
        mPaintTxt.setFakeBoldText(true);
    }

    @Override
    public void onSizeChanged(int width, int height) {
        mCanvasWidth = width;
        mCanvasHeight = height;
    }

    public void setOnClickListener(OnClickListener listener) {
        mListener = listener;
    }

    @Override
    public boolean contains(float x, float y) {
        if (!mVisible) {
            return false;
        }
        return super.contains(x, y);
    }

    public boolean onDown(MotionEvent e) {
        //mPaintBkg.setColor(Color.rgb(198, 235, 245));
        return true;
    }

    public boolean onUp(MotionEvent e) {
        if (mListener != null) {
            mListener.onClick(this);
        }
        //mPaintBkg.setColor(Color.WHITE);
        return true;
    }

    @Override
    public void onCancelSelection(MotionEvent e) {
        mPaintBkg.setColor(Color.WHITE);
    }
}
