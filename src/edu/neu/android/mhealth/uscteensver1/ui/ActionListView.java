package edu.neu.android.mhealth.uscteensver1.ui;

import java.util.ArrayList;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.Align;
import edu.neu.android.mhealth.uscteensver1.USCTeensGlobals;
import edu.neu.android.mhealth.uscteensver1.extra.Action;
import edu.neu.android.mhealth.uscteensver1.extra.ActionManager;
import edu.neu.android.mhealth.uscteensver1.pages.AppScale;


public class ActionListView extends ListView {
	
	public class ActionItem extends ListItem {
		protected Action mAction;
		
		public ActionItem(ListView parent, Action action) {
			super(parent, action.getActionName(), -1, action.getActionImage());
			mAction = action;
			mPaintTxt.setColor(Color.BLACK);
			mPaintTxt.setTextSize(AppScale.doScaleT(45));
			mPaintTxt.setTypeface(mTypeface);
			mPaintTxt.setTextAlign(Align.CENTER);
		}		
		
		public Action getAction() {
			return mAction;
		}

		protected void onDraw(Canvas c) {
			float y1 = mOffsetY + (mHeight + mBorderWidth) * mPosn;
			float y2 = y1 + mHeight;
			
			if (y1 > mCanvasHeight || y2 < 0) {
				return;
			}
			
			c.drawBitmap(mImage, 8, y1, null);
			c.drawRect(mImage.getWidth() + 16, y1, mWidth - 3, y2, mPaintBkg);
			c.drawText(mText, (mWidth + mImage.getWidth()) / 2, y1 + mHeight * 0.6f, mPaintTxt);
		}				
	}
	
	public ActionListView(Resources res) {
		super(res);		
		ArrayList<Action> actions = ActionManager.getActivatedActions();
		
		for (Action action : actions) {		
		    if (!action.getActionID().equals(USCTeensGlobals.UNLABELLED_GUID)) {
		    	addItem(action);
		    }		    
		}	
	}
	
	public void addItem(Action action) {		
       	ListItem li = new ActionItem(this, action);
       	mItems.add(li);
       	li.register();
	}

	public void setPosn(float x, float y) {
		mX = x;
		mY = y;
		
		for (int i = 0; i < mItems.size(); ++i) {
			ListItem li = mItems.get(i);
			li.mX = mX;
			li.mY = mY + (mItemHeight + mBorderWidth) * i;
			li.mWidth  = mItemWidth;
			li.mHeight = mItemHeight;
		}
	}
	
	@Override
	public void onSizeChanged(int width, int height) {
		if (mCanvasWidth == width && mCanvasHeight == height) {
			return;
		}
		mCanvasWidth  = width;
		mCanvasHeight = height;
		
		mWidth  = width;
		mHeight = height - AppScale.doScaleH(130 + 100) - (2 * mBorderWidth + 1);
		
		mItemWidth  = (int) mWidth;
		mItemHeight = (int) (mHeight - 3 * mBorderWidth) / 4;
		
		for (int i = 0; i < mItems.size(); ++i) {
			ListItem li = mItems.get(i);
			li.mX = mX;
			li.mY = mY + (mItemHeight + mBorderWidth) * i;
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
