package edu.neu.android.mhealth.uscteensver1.ui;

import java.util.ArrayList;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.MotionEvent;
import edu.neu.android.mhealth.uscteensver1.R;
import edu.neu.android.mhealth.uscteensver1.data.Chunk;
import edu.neu.android.mhealth.uscteensver1.pages.AppScale;

public class MergeButton extends ChunkButton {

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
	
	public MergeButton(Resources res, Chunk host, OnClickListener listener) {
		super(res, host);
		loadImages(res, new int[] { R.drawable.merge_btn });
		mWidth  = sImages.get(0).getWidth();
		mHeight = sImages.get(0).getHeight();
		mID = UIID.MERGE;
		mListener = listener;
		mVisible = false;
	}

	@Override
	public void measureSize(int width, int height) {
		mCanvasWidth  = width;
		mCanvasHeight = height;
		mY = height * 0.25f;
	}
	
	@Override
	public void onSizeChanged(int width, int height) {
		if (mCanvasWidth == width && mCanvasHeight == height) {
			return;
		}
		mCanvasWidth  = width;
		mCanvasHeight = height;

		mY = height * 0.25f;		
	}
	
	@Override
	public void onDraw(Canvas c) {
		if (mVisible) {
			c.drawBitmap(sImages.get(0), mX + mOffsetX, 
				mY + mOffsetY, null);
		}
	}
	
	@Override
	public boolean onDown(MotionEvent e) {
		mX += 3;
		mY += 3;
		return true;
	}
	
	@Override
	public boolean onUp(MotionEvent e) {
		if (mListener != null) {
			mListener.onClick(this);
		}
		mX -= 3;
		mY -= 3;
		return true;
	}
	
	@Override
	public void onCancelSelection(MotionEvent e) {
		mX -= 3;
		mY -= 3;
	}
	
}
