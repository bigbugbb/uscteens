package edu.neu.android.mhealth.uscteensver1.data;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.Paint.Style;
import edu.neu.android.mhealth.uscteensver1.USCTeensGlobals;
import edu.neu.android.mhealth.uscteensver1.dialog.QuestDialog;
import edu.neu.android.mhealth.uscteensver1.pages.AppObject;
import edu.neu.android.mhealth.uscteensver1.pages.GraphPage;
import edu.neu.android.mhealth.uscteensver1.ui.ButtonClock;
import edu.neu.android.mhealth.uscteensver1.ui.ButtonMerge;
import edu.neu.android.mhealth.uscteensver1.ui.ButtonQuest;
import edu.neu.android.mhealth.uscteensver1.ui.ButtonSplit;
import edu.neu.android.mhealth.uscteensver1.ui.UIID;

// chunk data mapping to the motion graph
public class Chunk extends AppObject {
	
	public final static int MINIMUM_SPACE = 240;
	
	public int mStart;  // in pixel, has been scaled by DataSource.PIXEL_SCALE
	public int mStop;   // in pixel, has been scaled by DataSource.PIXEL_SCALE
	public int mOffset; // plus to reconstruct the real world value, the offset has not been scaled
	public GraphPage   mParent;
	public ButtonQuest mQuest;
	public ButtonClock mClock;
	public ButtonMerge mMerge;
	public ButtonSplit mSplit;
	
	protected float mDispOffsetX;
	protected float mDispOffsetY;
	
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
	
	public Chunk(Resources res) {
		super(res);
		mKind = CHUNK;
		mZOrder = ZOrders.CHUNK;
		
		mParent = (GraphPage) ChunkManager.getUserData();		
		mQuest = new ButtonQuest(res, this, mParent);
		mClock = new ButtonClock(res, this, mParent);
		mMerge = new ButtonMerge(res, this, mParent);
		mSplit = new ButtonSplit(res, this, mParent);
				
		List<AppObject> objects = mParent.getObjectList();
		objects.add(mQuest);
		objects.add(mClock);
		objects.add(mMerge);
		objects.add(mSplit);
				
		createPaint();
	}
	
	public RawChunk toRawChunk() {
		
		String startDate = toDateTime(mStart / USCTeensGlobals.PIXEL_PER_DATA + mOffset);
		String stopDate  = toDateTime(mStop  / USCTeensGlobals.PIXEL_PER_DATA + mOffset);		
		int actionID = getActionID();
		String activity = (actionID == -1) ? "UNLABELLED" : USCTeensGlobals.ACTION_NAMES[actionID];			                  
		String modifyTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		RawChunk rawChunk = new RawChunk(startDate, stopDate, activity, modifyTime, modifyTime);
		
		return rawChunk;
	}
	
	private String toDateTime(int time) {
		int hour   = time / 3600;
		int minute = (time - hour * 3600) / 60;

		return DataSource.getCurrentSelectedDate() + " " + hour + ":" + minute + ":" + "00.000";
	}	

	public int getActionID() {
		if (mQuest.isAnswered()) {					
			for (int i = 0; i < USCTeensGlobals.ACTION_IMGS.length; ++i) {
				if (mQuest.getAnswer() == USCTeensGlobals.ACTION_IMGS[i]) {
					return i;
				}
			}
		}
		
		return -1;
	}
	
	public int getChunkRealStartTime() {
		return mStart / USCTeensGlobals.PIXEL_PER_DATA + mOffset;
	}
	
	public int getChunkRealStopTime() {
		return mStop / USCTeensGlobals.PIXEL_PER_DATA + mOffset;
	}
	
	public String getChunkRealStartTimeInString() {
		int time   = mStart / USCTeensGlobals.PIXEL_PER_DATA + mOffset;
		int hour   = time / 3600;
		int minute = (time - 3600 * hour) / 60;
		
		return hour + ":" + (minute > 9 ? minute : "0" + minute); 
	}
	
	public void release() {
		super.release();
		
		mQuest.release();
		mClock.release();
		mMerge.release();
		mSplit.release();	
		
		mParent.getObjectList().remove(mQuest);
		mParent.getObjectList().remove(mClock);
		mParent.getObjectList().remove(mMerge);
		mParent.getObjectList().remove(mSplit);
	}
	
	public boolean update(int start, int stop, int offset) {
		// next must be bigger than the current
		if (stop - start < sAppScale.doScaleW(MINIMUM_SPACE)) {
			return false; // just ignore, because the space for one chunk will be too small
		}		
		mStart  = start;
		mStop   = stop;
		mOffset = offset;
		// move quest button to the center of the chunk
		float centerX = (start + stop - mQuest.getWidth()) / 2;
		mQuest.setX(centerX);
		mClock.setX(start - mClock.getWidth() / 2);
		mMerge.setX(start - mMerge.getWidth() / 2);
		mSplit.setX(centerX);	

		return true;
	}

	@Override
	public boolean contains(float x, float y) {			
		if (x >= mStart + mDispOffsetX + sAppScale.doScaleW(60) && 
			x <= mStop + mDispOffsetX - sAppScale.doScaleW(60)) { 
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
		float viewWidth = ChunkManager.getViewWidth();
		if (mStop + offsetX > 0 && x + offsetX < 0) { // left case		
			inChunkOffsetX = Math.min(-(x + offsetX), mStop - w * 1.5f - x);
		} else if (mStart + offsetX < viewWidth && x + offsetX + w > viewWidth) { // right case			
			inChunkOffsetX = Math.max(viewWidth - (x + offsetX + w), -(x - mStart - w * 0.5f));
		}		
		mQuest.setOffsetInChunk(inChunkOffsetX, 0);	
		mSplit.setOffsetInChunk(inChunkOffsetX, 0);
	}

	@Override
	public void onDraw(Canvas c) {
		if (mStart + mDispOffsetX < 0 && mStart + mDispOffsetX > ChunkManager.getViewWidth()) {
			return;
		}
		c.drawLine(mStart + mDispOffsetX, 0, mStart + mDispOffsetX, mHeight, sPaint);		
	}
}
