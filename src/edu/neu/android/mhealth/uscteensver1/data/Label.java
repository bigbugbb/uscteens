package edu.neu.android.mhealth.uscteensver1.data;

import java.util.ArrayList;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.Typeface;
import edu.neu.android.mhealth.uscteensver1.R;
import edu.neu.android.mhealth.uscteensver1.USCTeensGlobals;
import edu.neu.android.mhealth.uscteensver1.pages.AppObject;
import edu.neu.android.mhealth.uscteensver1.pages.AppScale;

public class Label extends AppObject {

	public final static int MAXIMUM_LABEL_WIDTH = 300;
	public final static int MAXIMUM_TEXT_WIDTH = 250;
	
	public int mX;   // in pixel, has been scaled by DataSource.PIXEL_SCALE
	public int mY;   // in pixel, has been scaled by DataSource.PIXEL_SCALE
	public Rect mRect = new Rect();
	public String mText;

	protected float mDispOffsetX;
	protected float mDispOffsetY;
	
	protected static Paint   sPaint;
	protected static boolean sPaintCreated = false;
	protected static float   sImgWidth;
	protected static float   sImgHeight;
	protected static float   sMaxLabelWidth;
	protected static float	 sMaxTxtWidth;
		
	
	protected static void createPaint() {
		if (!sPaintCreated) {
			sPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			sPaint.setColor(Color.argb(255, 255, 102, 0));
			sPaint.setStyle(Style.STROKE);
			sPaint.setTypeface(Typeface.createFromAsset(USCTeensGlobals.sContext.getAssets(), "font/arial.ttf"));
			sPaint.setTextSize(AppScale.doScaleT(28));
			sPaintCreated = true;
		}
	}
	
	protected static boolean sImageLoaded = false;
	protected static ArrayList<Bitmap> sImages = new ArrayList<Bitmap>();

	static protected void loadImages(Resources res, int[] resIDs) {
		if (sImageLoaded) {
			return;
		}
		sImageLoaded = true;
        
        BitmapFactory.Options options = new BitmapFactory.Options(); 
        options.inPurgeable = true;
        options.inPreferredConfig = Config.RGB_565;        
        for (int id : resIDs) {
        	Bitmap origin = BitmapFactory.decodeResource(res, id, options);
        	Bitmap scaled = null;
        	// scale the image according to the current screen resolution
        	float dstWidth  = origin.getWidth(),
        	      dstHeight = origin.getHeight();        	      
    		dstWidth  = AppScale.doScaleW(dstWidth);
    		dstHeight = AppScale.doScaleH(dstHeight);
    		if (dstWidth != origin.getWidth() || dstHeight != origin.getHeight()) {
    			scaled = Bitmap.createScaledBitmap(origin, (int) dstWidth, (int) dstHeight, true);
    		}            
    		// add to the image list
        	if (scaled != null) {
	    		origin.recycle(); // explicit call to avoid out of memory
	    		sImages.add(scaled);
	        } else {
	        	sImages.add(origin);
	        }
        }
        sImgWidth  = sImages.get(0).getWidth();
        sImgHeight = sImages.get(0).getHeight();
        sMaxTxtWidth   = AppScale.doScaleT(MAXIMUM_TEXT_WIDTH);
        sMaxLabelWidth = AppScale.doScaleW(MAXIMUM_LABEL_WIDTH);
	}
	
	static public ArrayList<Bitmap> getLabelImages(Resources res) {
		loadImages(res, new int[]{ R.drawable.label_marker });
		return sImages;
	}
	
	public Label(Resources res) {
		super(res);
		mKind   = LABEL;
		mZOrder = ZOrders.LABEL;
 
		createPaint();
		loadImages(res, new int[]{ R.drawable.label_marker });
	}
	
	public boolean load(int x, int y, String text) {
		boolean result = true;
		
		mText = text; // "Watching TV";		
		int count = sPaint.breakText(mText, true, sMaxTxtWidth, null);
		if (count < mText.length()) {			
			mText = mText.subSequence(0, count) + "...";
		}
		sPaint.getTextBounds(mText, 0, mText.length(), mRect);
		
		int pos[] = { x, y };
		LabelManager.adjustLabelCoordinate(
			pos, mRect.width(), mRect.height(), (int) sImgWidth, (int) sImgHeight
		);		
		mX = pos[0];
		mY = pos[1];
				
		return result;
	}
		
	public void release() {
		super.release();
	}

	@Override
	public boolean contains(float x, float y) {					
		return false;
	}			

	public void setDisplayOffset(float offsetX, float offsetY) {
		mDispOffsetX = offsetX;
		mDispOffsetY = offsetY;
	}

	@Override
	public void onDraw(Canvas c) {
		float x = mX + mDispOffsetX;
		float y = mY + mDispOffsetY;

		if (x < -sMaxLabelWidth || x - sMaxLabelWidth > LabelManager.getViewWidth()) {
			return;
		}				
		if (y > LabelManager.getCanvasHeight() * 0.48f) {
			return;
		}
		
		float delta = mX + sImgWidth - USCTeensGlobals.MAX_WIDTH_IN_PIXEL;
		delta = delta > 0 ? -delta : 0;	
		x += delta;
		
		c.drawBitmap(sImages.get(0), x - AppScale.doScaleW(8), y - AppScale.doScaleH(42), null);		
		if (x + sImgWidth + mRect.width() > LabelManager.getViewWidth()) {
			c.drawText(mText, x - mRect.width() - AppScale.doScaleW(14), y, sPaint);
		} else {
			c.drawText(mText, x + sImages.get(0).getWidth(), y, sPaint);
		}
	}
}
