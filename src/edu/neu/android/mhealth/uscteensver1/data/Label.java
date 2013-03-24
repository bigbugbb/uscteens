package edu.neu.android.mhealth.uscteensver1.data;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import edu.neu.android.mhealth.uscteensver1.pages.AppObject;
import edu.neu.android.mhealth.uscteensver1.pages.AppScale;

public class Label extends AppObject {

	public final static int MAXIMUM_LABEL_SPACE = 240;
	
	public int mStart;  // in pixel, has been scaled by DataSource.PIXEL_SCALE
	public int mStop;   // in pixel, has been scaled by DataSource.PIXEL_SCALE
	public int mOffset; // plus to reconstruct the real world value, the offset has not been scaled

	protected float mDispOffsetX;
	protected float mDispOffsetY;
	
	protected static Paint   sPaint;
	protected static boolean sPaintCreated = false;
	
	protected static void createPaint() {
		if (!sPaintCreated) {
			sPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			sPaint.setColor(Color.argb(255, 0, 0x99, 0x99));
			sPaint.setStyle(Style.STROKE);
			sPaint.setTypeface(Typeface.SERIF);
			sPaint.setTextSize(AppScale.doScaleT(20));
			sPaintCreated = true;
		}
	}
	
	public Label(Resources res) {
		super(res);
		mKind   = LABEL;
		mZOrder = ZOrders.LABEL;
 
		createPaint();
	}
	
	public void release() {
		super.release();
	}

	@Override
	public boolean contains(float x, float y) {			
		if (x >= mStart + mDispOffsetX + AppScale.doScaleW(60) && 
			x <= mStop + mDispOffsetX - AppScale.doScaleW(60)) { 
			return true;
		}
		return false;
	}			

	public void setDisplayOffset(float offsetX, float offsetY) {
		mDispOffsetX = offsetX;
		mDispOffsetY = offsetY;
	}

	@Override
	public void onDraw(Canvas c) {
		if (mStart + mDispOffsetX < 0 && mStart + mDispOffsetX > ChunkManager.getViewWidth()) {
			return;
		}
		c.drawLine(mStart + mDispOffsetX, 0, mStart + mDispOffsetX, mHeight, sPaint);		
	}
}
