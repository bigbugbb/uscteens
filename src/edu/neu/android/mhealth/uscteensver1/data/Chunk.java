package edu.neu.android.mhealth.uscteensver1.data;

import java.util.List;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.Paint.Style;
import edu.neu.android.mhealth.uscteensver1.Actions;
import edu.neu.android.mhealth.uscteensver1.ActionsDialog;
import edu.neu.android.mhealth.uscteensver1.AppObject;
import edu.neu.android.mhealth.uscteensver1.MainPage;
import edu.neu.android.mhealth.uscteensver1.ui.ButtonClock;
import edu.neu.android.mhealth.uscteensver1.ui.ButtonMerge;
import edu.neu.android.mhealth.uscteensver1.ui.ButtonQuest;
import edu.neu.android.mhealth.uscteensver1.ui.ButtonSplit;
import edu.neu.android.mhealth.uscteensver1.ui.UIID;

public class Chunk extends AppObject {
	
	protected final static int MINIMUM_SPACE = 240;
	
	public int mValue; // in pixel
	public int mNext;  // in pixel
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
	
	protected ChunkManager mManager = null;
	
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
	
	public Chunk(Resources res, ChunkManager manager, Object userData) {
		super(res);
		mKind = CHUNK;
		mZOrder = ZOrders.CHUNK;
		
		mManager = manager;
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
			for (int i = 0; i < Actions.ACTION_IMGS.length; ++i) {
				if (mQuest.getAnswer() == Actions.ACTION_IMGS[i]) {
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
		if (next - current < sAppScale.doScaleW(MINIMUM_SPACE)) {
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
		if (x >= mValue + mDispOffsetX + sAppScale.doScaleW(60) && 
			x <= mNext + mDispOffsetX - sAppScale.doScaleW(60)) { 
			return true;
		}
		return false;
	}			

	public void setDisplayOffset(float offsetX, float offsetY) {
		mDispOffsetX = offsetX;
		mDispOffsetY = offsetY;
		
		mQuest.setDisplayOffset(offsetX, offsetY);
		mMerge.setDisplayOffset(offsetX, offsetY);
		mClock.setDisplayOffset(offsetX, offsetY);		
		mSplit.setDisplayOffset(offsetX, offsetY);
		
		float x = mQuest.getX();
		float w = mQuest.getWidth();
		float inChunkOffsetX = 0;
		if (mNext + offsetX > 0 && x + offsetX < 0) {			
			inChunkOffsetX = Math.min(-(x + offsetX), mNext - w * 1.5f - x);
		} else if (mValue + offsetX < mManager.mViewWidth && x + offsetX + w > mManager.mViewWidth) {
			//offsetInChunkX = mValue - x + (mManager.mViewWidth - (mValue + offsetX));
			inChunkOffsetX = Math.max(mManager.mViewWidth - (x + offsetX + w),  
				- (x - mValue - w * 0.5f));
		}		
		mQuest.setOffsetInChunk(inChunkOffsetX, 0);	
		mSplit.setOffsetInChunk(inChunkOffsetX, 0);
	}

	@Override
	public void onDraw(Canvas c) {
		if (mValue + mDispOffsetX < 0 && mValue + mDispOffsetX > mManager.mViewWidth) {
			return;
		}
		c.drawLine(mValue + mDispOffsetX, 0, mValue + mDispOffsetX, mHeight, sPaint);		
	}
}
