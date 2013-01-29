package edu.neu.android.mhealth.uscteensver1.data;

import java.util.List;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.Paint.Style;
import edu.neu.android.mhealth.uscteensver1.ActionsDialog;
import edu.neu.android.mhealth.uscteensver1.AppObject;
import edu.neu.android.mhealth.uscteensver1.MainPage;
import edu.neu.android.mhealth.uscteensver1.ui.ButtonClock;
import edu.neu.android.mhealth.uscteensver1.ui.ButtonMerge;
import edu.neu.android.mhealth.uscteensver1.ui.ButtonQuest;
import edu.neu.android.mhealth.uscteensver1.ui.ButtonSplit;
import edu.neu.android.mhealth.uscteensver1.ui.UIID;

public class Chunk extends AppObject {
	
	public int mValue;
	public int mNext;
	public MainPage mPage;
	public ButtonQuest mQuest;
	public ButtonClock mClock;
	public ButtonMerge mMerge;
	public ButtonSplit mSplit;
	
	protected float mDispOffsetX;
	protected float mDispOffsetY;
	protected Paint mPaint;
	
	protected static Paint   sPaint;
	protected static boolean sPaintCreated = false;
	
	protected static void createPaint() {
		if (!sPaintCreated) {
			sPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			sPaint.setColor(Color.BLACK);
			sPaint.setStyle(Style.FILL);
			sPaint.setTypeface(Typeface.SERIF);
			sPaint.setFakeBoldText(false);
			sPaintCreated = true;
		}
	}
	
	public Chunk(Resources res, Object userData) {
		super(res);
		mKind = CHUNK;
		mZOrder = ZOrders.CHUNK;
		
		mPage = (MainPage) userData;		
		
		mQuest = new ButtonQuest(res, this, mPage);
		mClock = new ButtonClock(res, this, mPage);
		mMerge = new ButtonMerge(res, this, mPage);
		mSplit = new ButtonSplit(res, this, mPage);
				
		List<AppObject> objects = mPage.getObjectList();
		objects.add(mQuest);
		objects.add(mClock);
		objects.add(mMerge);
		objects.add(mSplit);
				
		createPaint();
	}
	
	public int getActionID() {
		if (mQuest.isAnswered()) {					
			for (int i = 0; i < ActionsDialog.ACTION_IMGS.length; ++i) {
				if (mQuest.getAnswer() == ActionsDialog.ACTION_IMGS[i]) {
					return i;
				}
			}
		}
		
		return -1;
	}
	
	public void release() {
		super.release();
		
		mQuest.release();
		mClock.release();
		mMerge.release();
		mSplit.release();	
		
		mPage.getObjectList().remove(mQuest);
		mPage.getObjectList().remove(mClock);
		mPage.getObjectList().remove(mMerge);
		mPage.getObjectList().remove(mSplit);
	}
	
	public boolean update(int current, int next) {
		// next must be bigger than the current
		if (next - current < 240) {
			return false; // just ignore, because the space for one chunk will be too small
		}
		mValue = current;
		mNext  = next;
		// move quest button to the center of the chunk
		float centerX = (current + next - mQuest.getWidth()) / 2;
		mQuest.setX(centerX);
		mClock.setX(current - mClock.getWidth() / 2);
		mMerge.setX(current - mMerge.getWidth() / 2);
		mSplit.setX(centerX);	

		return true;
	}

	@Override
	public boolean contains(float x, float y) {			
		if (x >= mValue + mDispOffsetX + 60 && x <= mNext + mDispOffsetX - 60) { 
			return true;
		}
		return false;
	}			

	public void setDisplayOffset(float offsetX, float offsetY) {
		mDispOffsetX = offsetX;
		mDispOffsetY = offsetY;
		
		mQuest.setDisplayOffset(offsetX, offsetY);
		mClock.setDisplayOffset(offsetX, offsetY);
		mMerge.setDisplayOffset(offsetX, offsetY);
		mSplit.setDisplayOffset(offsetX, offsetY);
	}

	@Override
	public void onDraw(Canvas c) {
		c.drawLine(mValue + mDispOffsetX, 0, mValue + mDispOffsetX, mHeight, sPaint);		
	}
}
