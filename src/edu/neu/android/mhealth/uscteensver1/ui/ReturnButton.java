package edu.neu.android.mhealth.uscteensver1.ui;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Typeface;

import edu.neu.android.mhealth.uscteensver1.TeensAppManager;
import edu.neu.android.mhealth.uscteensver1.pages.AppScale;

public class ReturnButton extends CustomButton {

    protected Paint mPaintText = null;

    public ReturnButton(Resources res) {
        super(res);

        mPaintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintText.setColor(Color.WHITE);
        mPaintText.setStyle(Style.FILL);
        mPaintText.setTypeface(Typeface.createFromAsset(TeensAppManager.getAppAssets(), "font/arial.ttf"));
        mPaintText.setFakeBoldText(false);
        mPaintText.setTextSize(AppScale.doScaleH(36));
        mPaintText.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    public void onDraw(Canvas c) {
        c.drawBitmap(mImages.get(0), mX, mY, null);
        c.drawText("BACK", mX, mY + AppScale.doScaleH(10), mPaintText);
    }

}
