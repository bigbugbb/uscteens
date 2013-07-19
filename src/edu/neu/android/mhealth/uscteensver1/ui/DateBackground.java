package edu.neu.android.mhealth.uscteensver1.ui;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import edu.neu.android.mhealth.uscteensver1.R;
import edu.neu.android.mhealth.uscteensver1.TeensGlobals;
import edu.neu.android.mhealth.uscteensver1.pages.AppScale;

public class DateBackground extends Background {
	
	protected Paint mPaint = null;

	public DateBackground(Resources res) {
		super(res);		
		loadImages(new int[]{ R.drawable.weeksbar1, R.drawable.weeksbar2 });
		
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setColor(Color.WHITE);
		mPaint.setStyle(Style.FILL);
		mPaint.setTextSize(AppScale.doScaleW(48));
		mPaint.setTypeface(Typeface.createFromAsset(TeensGlobals.sContext.getAssets(), "font/arial_bold.ttf"));
		mPaint.setTextAlign(Align.CENTER);
	}	
	
	@Override
	public void onSizeChanged(int width, int height) {		
		mWidth  = width;
		mHeight = height;
	}

	@Override
	public void onDraw(Canvas c) {
		c.drawColor(Color.rgb(179, 181, 181));
		c.drawBitmap(mImages.get(0), 1, 0, null);
		c.drawText("WEEK 1", mWidth * 0.25f, AppScale.doScaleH(80), mPaint);
		c.drawBitmap(mImages.get(1), mWidth / 2 + 1, 0, null);
		c.drawText("WEEK 2", mWidth * 0.75f, AppScale.doScaleH(80), mPaint);
	}
}
