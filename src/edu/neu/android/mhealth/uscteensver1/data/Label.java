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

	public final static int MAXIMUM_LABEL_WIDTH = 225;
	public final static int MAXIMUM_TEXT_WIDTH = 175;
	
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
	
	public Label(Resources res) {
		super(res);
		mKind   = LABEL;
		mZOrder = ZOrders.LABEL;
 
		createPaint();
		loadImages(res, new int[]{ R.drawable.label_marker });
	}
	
	public boolean load(int x, int y, String text) {
		boolean result = true;
		mX = x;
		mY = y;
		mText = text; // "Watching TV";
		
		int count = sPaint.breakText(mText, true, sMaxTxtWidth, null);
		if (count < mText.length()) {			
			mText = mText.subSequence(0, count) + "...";
		}
		
		sPaint.getTextBounds(mText, 0, mText.length(), mRect);
				
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
		
		float delta = mX + sImgWidth - USCTeensGlobals.MAX_WIDTH_IN_PIXEL;
		delta = delta > 0 ? -delta : 0;	
		x += delta;
		
		c.drawBitmap(sImages.get(0), x - AppScale.doScaleW(8), y - AppScale.doScaleH(35), null);		
		if (x + sImgWidth + mRect.width() > LabelManager.getViewWidth()) {
			c.drawText(mText, x - mRect.width() - AppScale.doScaleW(8), y, sPaint);
		} else {
			c.drawText(mText, x + sImages.get(0).getWidth(), y, sPaint);
		}
		
//		if (mVisible) {
//			c.drawBitmap(sImages.get(0), mX + mOffsetX, mY + mOffsetY, null);
//			// choose to draw left or right based on the clock button position				
//			if (mX + mOffsetX < mCanvasWidth * 0.83f) { 
//				// draw on the right side
//				if (mX + mWidth / 2 + mOffsetX > AppScale.doScaleW(-120)) {
//					sTimePaint.setTextAlign(Align.LEFT);
//					c.drawText(mHost.getChunkRealStartTimeInString(), 
//						mX + mWidth / 2 + mOffsetX + AppScale.doScaleW(50),
//						mY + mHeight / 2 + AppScale.doScaleH(10), sTimePaint);
//				}
//			} else { 
////				Chunk chunk = ChunkManager.getPrevChunk(mHost);
////				if (chunk.isSelected() && chunk.getChunkWidth() > Chunk.MINIMUM_CHUNK_SPACE * 1.75f) {
//					// draw on the left side
//					if (mX + mWidth / 2 + mOffsetX < mCanvasWidth + AppScale.doScaleW(120)) {
//						sTimePaint.setTextAlign(Align.RIGHT);
//						c.drawText(mHost.getChunkRealStartTimeInString(), 
//							mX - mWidth / 2 + mOffsetX + AppScale.doScaleW(30),
//							mY + mHeight / 2 + AppScale.doScaleH(10), sTimePaint);
//					}
////				} 
////				else {
////					// draw on the right side
////					if (mX + mWidth / 2 + mOffsetX > AppScale.doScaleW(-120)) {
////						sTimePaint.setTextAlign(Align.LEFT);
////						c.drawText(mHost.getChunkRealStartTimeInString(), 
////							mX + mWidth / 2 + mOffsetX + AppScale.doScaleW(50),
////							mY + mHeight / 2 + AppScale.doScaleH(10), sTimePaint);
////					}
////				}
//			}
//			//Log.d("clock button", mX + " " + mY + " " + mOffsetX);
//			if (isSelected()) {
//				c.drawCircle(mX + mWidth / 2 + mOffsetX, 
//					mY + mHeight / 2 + mOffsetY, AppScale.doScaleH(47), sPaint);								
//			}			 									
//		}
	}
}
