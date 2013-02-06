package edu.neu.android.mhealth.uscteensver1.ui;


import java.util.ArrayList;

import edu.neu.android.mhealth.uscteensver1.R;
import edu.neu.android.mhealth.uscteensver1.data.Chunk;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Bitmap.Config;
import android.view.MotionEvent;

public class ButtonQuest extends ChunkButton {	
	
	protected int mAnswer = R.drawable.question_btn;
	protected String mActionName = "None";

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
        	if (sAppScale != null) {
        		dstWidth  = sAppScale.doScaleW(dstWidth);
        		dstHeight = sAppScale.doScaleH(dstHeight);
        		if (dstWidth != origin.getWidth() || dstHeight != origin.getHeight()) {
        			scaled = Bitmap.createScaledBitmap(origin, (int) dstWidth, (int) dstHeight, true);
        		}
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
	
	public ButtonQuest(Resources res, Chunk host, OnClickListener listener) {
		super(res, host);
		loadImages(res, new int[]{ R.drawable.question_btn });
		mWidth  = sImages.get(0).getWidth();
		mHeight = sImages.get(0).getHeight();
		mListener = listener;
		mID = UIID.QUEST;
	}
	
	public boolean isAnswered() {
		return mAnswer != R.drawable.question_btn;
	}
	
	public void setAnswer(int answer, String actionName) {				
		BitmapFactory.Options options = new BitmapFactory.Options(); 
        options.inPurgeable = true;
        options.inPreferredConfig = Config.RGB_565; 
        
		if (answer != mAnswer) {
			Bitmap image  = null;
			Bitmap origin = BitmapFactory.decodeResource(mRes, answer, options);
        	Bitmap scaled = null;
        	// scale the image according to the current screen resolution
        	float dstWidth  = origin.getWidth(),
        	      dstHeight = origin.getHeight();        	
        	if (sAppScale != null) {
        		dstWidth  = sAppScale.doScaleW(dstWidth);
        		dstHeight = sAppScale.doScaleH(dstHeight);
        		if (dstWidth != origin.getWidth() || dstHeight != origin.getHeight()) {
        			scaled = Bitmap.createScaledBitmap(origin, (int) dstWidth, (int) dstHeight, true);
        		}
            }        	
    		// add to the image list
        	if (scaled != null) {
	    		origin.recycle(); // explicit call to avoid out of memory
	    		image = scaled;
	        } else {
	        	image = origin;
	        } 
			// change answer			
			if (mImages.size() == 0) { // initialize
				mImages.add(image);				
			} else { // update the answer
				mImages.get(0).recycle();			
				mImages.set(0, image);
			}
			mAnswer = answer;	
			mActionName = actionName;
		}
    
	}
	
	public int getAnswer() {
		return mAnswer;
	}
	
	public String getStringAnswer() {
		return mActionName;
	}
	
	@Override
	public void measureSize(int width, int height) {
		mCanvasWidth  = width;
		mCanvasHeight = height;				
		mY = height * 0.634f;		
	}

	@Override
	public void onSizeChanged(int width, int height) {
		mCanvasWidth  = width;
		mCanvasHeight = height;		
		
		mY = height * 0.634f;		
	}

	@Override
	public void onDraw(Canvas c) {
		c.drawBitmap(mAnswer == R.drawable.question_btn ? sImages.get(0) : mImages.get(0), 
				mX + mOffsetX + mOffsetInChunkX, mY + mOffsetY + mOffsetInChunkY, null);
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
