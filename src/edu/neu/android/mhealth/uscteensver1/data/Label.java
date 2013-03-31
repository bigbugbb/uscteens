package edu.neu.android.mhealth.uscteensver1.data;

import java.util.ArrayList;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.Typeface;
import edu.neu.android.mhealth.uscteensver1.R;
import edu.neu.android.mhealth.uscteensver1.pages.AppObject;
import edu.neu.android.mhealth.uscteensver1.pages.AppScale;

public class Label extends AppObject {

	public final static int MAXIMUM_LABEL_SPACE = 240;
	
	public int mX;   // in pixel, has been scaled by DataSource.PIXEL_SCALE
	public int mY;   // in pixel, has been scaled by DataSource.PIXEL_SCALE
	public Rect mRect = new Rect();
	public String mText;

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
	}
	
	public Label(Resources res) {
		super(res);
		mKind   = LABEL;
		mZOrder = ZOrders.LABEL;
 
		createPaint();
		loadImages(res, new int[]{ R.drawable.dummy });
	}
	
	public boolean load(int x, int y, String text) {		
		boolean result = true;
		mX = x;
		mY = y;
		mText = text;
		
		sPaint.getTextBounds(text, 0, 1, mRect);
				
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
		if (mX + mDispOffsetX < -MAXIMUM_LABEL_SPACE || mX + mDispOffsetX > LabelManager.getViewWidth()) {
			return;
		}
		//c.drawLine(mX + mDispOffsetX, 0, mX + mDispOffsetX, mHeight, sPaint);
		c.drawBitmap(sImages.get(0), mX + mDispOffsetX, mY, null);
		c.drawText(mText, mX + sImages.get(0).getWidth() + mDispOffsetX, mY, sPaint);		
		
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
