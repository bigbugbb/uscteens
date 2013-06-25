package edu.neu.android.mhealth.uscteensver1.ui;

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
import edu.neu.android.mhealth.uscteensver1.USCTeensGlobals;
import edu.neu.android.mhealth.uscteensver1.extra.Action;
import edu.neu.android.mhealth.uscteensver1.extra.ActionManager;
import edu.neu.android.mhealth.uscteensver1.pages.AppScale;


public class ActionListView extends ListView {
	
	public class ActionItem extends ListItem {
		protected Action mAction;
		protected String mActSubName;
		protected Paint  mPaintSubTxt;
		protected Paint  mPaintHighlight;		
		
		public ActionItem(ListView parent, Action action) {	
			super(parent, action.getActionName(), -1, action.getActionImage());
			mAction = action;
			mActSubName = action.getActionSubName();
			
			// the paint for text before '|'
			mPaintTxt.setColor(Color.BLACK);
			mPaintTxt.setTextSize(AppScale.doScaleT(45));
			mPaintTxt.setTypeface(mTypeface);
			mPaintTxt.setTextAlign(Align.CENTER);
			
			// the paint for text after '|'
			mPaintSubTxt = new Paint(Paint.ANTI_ALIAS_FLAG);
			mPaintSubTxt.setColor(Color.BLACK);		
			mPaintSubTxt.setTextSize(AppScale.doScaleT(30));
			mPaintSubTxt.setTypeface(mTypeface);
			mPaintSubTxt.setTextAlign(Align.CENTER);
			
			// paint for highlighting the most recent selections
			mPaintHighlight = new Paint(Paint.ANTI_ALIAS_FLAG);
			mPaintHighlight.setColor(Color.rgb(92, 189, 150));
			mPaintHighlight.setStyle(Style.FILL);
		}		
		
		public Action getAction() {
			return mAction;
		}
		
		public Paint getBackgroundPaint() {
			return mPosn < ActionManager.MOST_RECENT_ACTIONS_COUNT ? 
				mPaintHighlight : mPaintBkg;
		}
		
		public int getBackgroundColor() {		
			int color = Color.WHITE;
			
			if (mPosn < ActionManager.MOST_RECENT_ACTIONS_COUNT) {				 
				color = Color.rgb(92, 189, 150);				
			}
			if (mSelected) {
				color = Color.rgb(198, 235, 245);
			}
			
			return color;
		}

		protected void onDraw(Canvas c) {
			float y1 = mOffsetY + (mHeight + mBorderWidth) * mPosn;
			float y2 = y1 + mHeight;
			
			if (y1 > mCanvasHeight || y2 < 0) {
				return;
			}
			
			// draw activity icon
			c.drawBitmap(mImage, 8, y1, null);
			
			// draw the background for the text
			c.drawRect(mImage.getWidth() + 16, y1, mWidth - 3, y2, getBackgroundPaint());
			
			// draw the activity name ('|' breaks the text into two lines)
			if (mActSubName == null) {
				c.drawText(mText, (mWidth + mImage.getWidth()) / 2, y1 + mHeight * 0.6f, mPaintTxt);
			} else {
				c.drawText(mText, (mWidth + mImage.getWidth()) / 2, y1 + mHeight * 0.45f, mPaintTxt);
				c.drawText(mActSubName, (mWidth + mImage.getWidth()) / 2, y1 + mHeight * 0.75f, mPaintSubTxt);
			}
		}				
	}
	
	public ActionListView(Resources res) {
		super(res);		
		ArrayList<Action> actions = ActionManager.getActivatedActions();
		ArrayList<Action> recents = ActionManager.getMostRecentActions();
		
		// add the most recent selections first
		for (Action action : recents) {
			if (!action.getActionID().equals(USCTeensGlobals.UNLABELLED_GUID)) {
		    	addItem(action);
		    }		
		}
		
		// then add the other activities
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
