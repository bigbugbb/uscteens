package edu.neu.android.mhealth.uscteensver1.ui;

import edu.neu.android.mhealth.uscteensver1.Actions;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Style;


public class ListViewActions extends ListView {
	
	public class ActionItem extends ListItem {
		
		public ActionItem(ListView parent, String text, Bitmap image) {
			super(parent, text, image);
			
			mPaintTxt.setColor(Color.BLACK);
			mPaintTxt.setStyle(Style.STROKE);
			mPaintTxt.setTextSize(sAppScale.doScaleT(45));
			mPaintTxt.setTypeface(Typeface.SERIF);
			mPaintTxt.setTextAlign(Align.CENTER);
			mPaintTxt.setFakeBoldText(false);
		}		

		protected void onDraw(Canvas c) {				
			c.drawBitmap(mImage, 8, mOffsetY + mHeight * mPosn + 6, null);
			c.drawRect(mImage.getWidth() + 16, mOffsetY + mHeight * mPosn,
				mWidth - 3, mOffsetY + mHeight * (mPosn + 1), mPaintBkg);
			c.drawText(mText, (mWidth + mImage.getWidth()) / 2, 
				mOffsetY + mHeight * mPosn + mHeight * 0.6f, mPaintTxt);
		}

	}
	
	public ListViewActions(Resources res) {
		super(res);

		for (int i = 0; i < Actions.ACTION_NAMES.length; ++i) {
			addItem(Actions.ACTION_NAMES[i], Actions.ACTION_IMGS[i]);
		}
	}
	
	public void addItem(String text, int drawable) {
		BitmapFactory.Options options = new BitmapFactory.Options(); 
        options.inPurgeable = true;
        options.inPreferredConfig = Config.RGB_565;     
        
        Bitmap image  = null;
    	Bitmap origin = BitmapFactory.decodeResource(mRes, drawable, options);
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
       	ListItem li = new ActionItem(this, text, image);
       	mItems.add(li);
       	li.register();
	}

	public void setPosn(float x, float y) {
		mX = x;
		mY = y;
		
		for (int i = 0; i < mItems.size(); ++i) {
			ListItem li = mItems.get(i);
			li.mX = mX;
			li.mY = mY + mItemHeight * i;
			li.mWidth  = mItemWidth;
			li.mHeight = mItemHeight;
		}
	}
	
	@Override
	public void onSizeChanged(int width, int height) {
		mWidth  = width;
		mHeight = sAppScale.doScaleH(height - sAppScale.doScaleH(130 + 100));
		mItemWidth  = (int) mWidth;
		mItemHeight = (int) mHeight / 4;
		
		for (int i = 0; i < mItems.size(); ++i) {
			ListItem li = mItems.get(i);
			li.mX = mX;
			li.mY = mY + mItemHeight * i;
			li.mWidth  = mItemWidth;
			li.mHeight = mItemHeight;
		}
		
		BitmapFactory.Options options = new BitmapFactory.Options(); 
        options.inPurgeable = true;
        options.inPreferredConfig = Config.RGB_565; 
        mImages.add(Bitmap.createBitmap((int) mWidth, (int) mHeight, Bitmap.Config.RGB_565));
        mInnerCanvas = new Canvas(mImages.get(0));
	}
}
