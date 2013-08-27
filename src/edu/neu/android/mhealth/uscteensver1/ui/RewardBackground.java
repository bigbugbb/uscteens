package edu.neu.android.mhealth.uscteensver1.ui;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import edu.neu.android.mhealth.uscteensver1.R;
import edu.neu.android.mhealth.uscteensver1.TeensAppManager;
import edu.neu.android.mhealth.uscteensver1.pages.AppScale;

public class RewardBackground extends Background {

    protected float mText1X;
    protected float mText1Y;
    protected float mText2X;
    protected float mText2Y;
    protected float mBarHeight;
    protected float mCanvasWidth;
    protected float mCanvasHeight;
    protected Paint mPaintText1;
    protected Paint mPaintText2;

    public RewardBackground(Resources res) {
        super(res);
        
        loadImages(
            new int[]{ R.drawable.congratulations_bar, R.drawable.win_background }
        );
        
        mText1X = 0;
        mText1Y = 0;
        mText2X = 0;
        mText2Y = 0;
        mBarHeight = 0;
        mCanvasWidth  = 0;
        mCanvasHeight = 0;        

        mPaintText1 = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintText1.setColor(Color.WHITE);
        mPaintText1.setStyle(Style.FILL);
        mPaintText1.setTypeface(Typeface.createFromAsset(TeensAppManager.getAppAssets(), "font/arial.ttf"));
        mPaintText1.setFakeBoldText(true);
        mPaintText1.setTextSize(AppScale.doScaleT(50));
        mPaintText1.setTextAlign(Paint.Align.CENTER);

        mPaintText2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintText2.setColor(Color.BLACK);
        mPaintText2.setStyle(Style.FILL);
        mPaintText2.setTypeface(Typeface.createFromAsset(TeensAppManager.getAppAssets(), "font/arial.ttf"));
        mPaintText2.setFakeBoldText(true);
        mPaintText2.setTextSize(AppScale.doScaleT(46));
        mPaintText2.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    public void onSizeChanged(int width, int height) {
        for (int i = 0; i < mImages.size(); ++i) {
            //float radio = mImages.get(i).getWidth() / (float) mImages.get(i).getHeight();
            int dstWidth = width;
            int dstHeight = mImages.get(i).getHeight();

            if (dstWidth == mImages.get(i).getWidth() &&
                    dstHeight == mImages.get(i).getHeight()) {
                continue;
            }

            Bitmap newImage = Bitmap.createScaledBitmap(mImages.get(i), dstWidth, dstHeight, true);
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
//		c.drawBitmap(mImages.get(1), (mCanvasWidth - mWidth) / 2, mBarHeight, null);
//		c.drawText("You have completed", mText2X, mText2Y, mPaintText2);
//		c.drawText("the Teen Activity Game", mText2X, mText2Y + AppScale.doScaleH(90), mPaintText2);
    }

}
