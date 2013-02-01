package edu.neu.android.mhealth.uscteensver1.data;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.RectF;
import edu.neu.android.mhealth.uscteensver1.Actions;
import edu.neu.android.mhealth.uscteensver1.ActionsDialog;
import edu.neu.android.mhealth.uscteensver1.R;
import edu.neu.android.mhealth.uscteensver1.ui.ButtonClock;
import edu.neu.android.mhealth.uscteensver1.ui.ButtonMerge;
import edu.neu.android.mhealth.uscteensver1.ui.ButtonSplit;
import edu.neu.android.mhealth.uscteensver1.ui.ListView;


public class ChunkManager {
	
	public Resources mRes   = null;
	public Object mUserData = null;			
	public ArrayList<Chunk> mChunks = null;
	
	protected int   mSelected = -1; // -1 indicates no chunk has been selected
	protected RectF mSelectedArea = new RectF();
	protected ButtonClock mClockL = null;
	protected ButtonClock mClockR = null;
	protected ButtonMerge mMergeL = null;
	protected ButtonMerge mMergeR = null;
	protected ButtonSplit mSplit  = null;
	
	protected float mDispOffsetX  = 0;
	protected float mDispOffsetY  = 0;	
	protected float mViewWidth    = 0;
	protected float mViewHeight   = 0;
	protected float mCanvasWidth  = 0;
	protected float mCanvasHeight = 0;
	
	protected int mLastValue = 0;
	protected Context mContext = null;
	protected DataSource mDataSrc = null;

	public ChunkManager(Context context, DataSource dataSrc) {
		mRes = context.getResources();
		mContext = context;		
		mDataSrc = dataSrc;
	}	
	
	protected void load() {
		
	}
	
	public void start() {
		loadChunks();
	}
	
	public void stop() {
		saveChunks();
		release();
	}
	
	public void loadChunks() {
		ArrayList<DataCell> cells = mDataSrc.mChkData;
		float lastValue = mDataSrc.mActData.length;
		if (mChunks == null) {
			mChunks = new ArrayList<Chunk>();
		}
		mLastValue = (int) lastValue;
		for (int i = 0; i < cells.size(); ++i) {
			insertChunk(i);		
			mChunks.get(i).update(cells.get(i).mPosition, 
				(i + 1 == cells.size()) ? (int) lastValue : cells.get(i + 1).mPosition);			
			mChunks.get(i).mQuest.setAnswer(
				cells.get(i).mActionID == -1 ? R.drawable.question_btn : Actions.ACTION_IMGS[cells.get(i).mActionID], 
				cells.get(i).mActionID == -1 ? "None" : Actions.ACTION_NAMES[cells.get(i).mActionID]);
		}						
	}
	
	public void release() {
		if (mChunks != null) {
			for (Chunk c : mChunks) {
				c.release();
			}
			mChunks = null;
		}
	}
	
	public void saveChunks() {
		ArrayList<DataCell> cells = new ArrayList<DataCell>();
		for (int i = 0; i < mChunks.size(); ++i) {
			Chunk c = mChunks.get(i);
			DataCell cell = new DataCell(c.mValue, c.getActionID());
			cells.add(cell);
		}
		mDataSrc.saveChunkData(cells);
	}
	
	public Chunk getChunk(int index) {
		return mChunks.get(index);
	}
	
	public int getChunkSize() {
		return mChunks.size();
	}
	
	public void setUserData(Object userData) {
		mUserData = userData;
	}
	
	public void setViewSize(float width, float height) {
		mViewWidth  = width;
		mViewHeight = height;
	}
	
	public void setCanvasSize(float width, float height) {
		mCanvasWidth  = width;
		mCanvasHeight = height;
	}
	
	public void setDisplayOffset(float offsetX, float offsetY) {
		mDispOffsetX = offsetX;
		mDispOffsetY = offsetY;
		
		for (int i = 0; i < mChunks.size(); ++i) {
			Chunk c = mChunks.get(i);
			c.setDisplayOffset(offsetX, offsetY);
		}
	}

	public void insertChunk(int index) {
		Chunk c = new Chunk(mRes, mUserData);			
		mChunks.add(index, c);
	}		

	public void deleteChunk(Chunk c) {
		mChunks.remove(c);
		c.release();
	}
	
	public void scaleChunk(int scale) {
		int i = 0;
		for (i = 0; i < mChunks.size(); ++i) {
			Chunk c = mChunks.get(i);
			if (c.mClock.isSelected()) {			
				break;
			}
		}		
		if (i == mChunks.size()) {
			return; // clock button has not been selected
		}
		
		Chunk curr = mChunks.get(i);
		Chunk prev = (i == 0) ? null : mChunks.get(i - 1);
		Chunk next = (i == mChunks.size() - 1) ? null : mChunks.get(i + 1);
		boolean success = true;
		if (prev != null) {
			success = prev.update(prev.mValue, (int) (curr.mValue + scale));
		}
		if (success) {
			curr.update((int) (curr.mValue + scale), next == null ? mLastValue : next.mValue);
		}
		
		if (mSelected > -1) {
			updateSelectedArea();
		}
	}
	
	public Chunk getPreviousUnmarkedChunk() {
		Chunk prev = null;
		float current = -mDispOffsetX;		
		
		boolean found = false;
		for (int i = mChunks.size() - 1; i >= 0; --i) {
			Chunk c = mChunks.get(i);
			if (c.mValue <= current) {
				if (c.mQuest.isAnswered()) {
					found = true;				
				} else {
					if (found) {
						prev = c;						
						break;
					}
				}
			}
		}
		
		return prev;
	}
	
	public Chunk getNextUnmarkedChunk() {
		Chunk next = null;
		float current = -mDispOffsetX;		
		
		boolean found = false;
		for (int i = 0; i < mChunks.size(); ++i) {
			Chunk c = mChunks.get(i);
			if (c.mValue >= current) {
				if (c.mQuest.isAnswered()) {
					found = true;				
				} else {
					if (found) {
						next = c;						
						break;
					}
				}
			}
		}
		
		return next;
	}
	
	public void selectChunk(Chunk chunk) {
		selectChunk(mChunks.indexOf(chunk));
	}
	
	public void selectChunk(int i) {
		mSelected = i;
		
		// set buttons invisible
		if (mClockL != null) {
			mClockL.setVisible(false);
			mClockL = null;
		}
		if (mClockR != null) {
			mClockR.setVisible(false);
			mClockR = null;
		}
		if (mMergeL != null) {
			mMergeL.setVisible(false);
			mMergeL = null;
		}
		if (mMergeR != null) {
			mMergeR.setVisible(false);
			mMergeR = null;
		}
		if (mSplit != null) {
			mSplit.setVisible(false);
			mSplit = null;
		}
		
		if (i > -1) {
			Chunk c = mChunks.get(i);
			mClockL = c.mClock;
			mMergeL = c.mMerge;
			mSplit  = c.mSplit;
			c = (i == mChunks.size() - 1) ? null : mChunks.get(i + 1);
			if (c != null) {
				mClockR = c.mClock;
				mMergeR = c.mMerge;
			}
		}	
		// set buttons visible
		if (mClockL != null && i != 0) {
			mClockL.setVisible(true);
		}
		if (mClockR != null) {
			mClockR.setVisible(true);
		}
		if (mMergeL != null && i != 0) {
			mMergeL.setVisible(true);
		}
		if (mMergeR != null) {
			mMergeR.setVisible(true);
		}
		if (mSplit != null) {
			mSplit.setVisible(true);
		}
		
		if (i > -1) {
			updateSelectedArea();
		}
	}
	
	public boolean selectChunk(float x, float y) {					
		
		for (int i = 0; i < mChunks.size(); ++i) {
			Chunk c = mChunks.get(i);
			if (c.contains(x, y)) {
				mSelected = i;							
				break;
			}
		}
		selectChunk(mSelected);
								
		return mSelected > -1;
	}
	
	public ArrayList<Chunk> getMergingChunks(ButtonMerge merge) {
		ArrayList<Chunk> chunks = new ArrayList<Chunk>();		
		int size = mChunks.size();
		for (int i = 1; i < size; ++i) {
			Chunk right = mChunks.get(i);
			if (right.mMerge == merge) {
				Chunk left = mChunks.get(i - 1);
				chunks.add(left);
				chunks.add(right);
			}
		}		
		return chunks;
	}
	
	public ArrayList<Float> getUnmarkedRange() {
		ArrayList<Float> range = new ArrayList<Float>();

		boolean found = false;
		for (int i = 0; i < mChunks.size(); ++i) {
			Chunk c = mChunks.get(i);
			if (!c.mQuest.isAnswered()) {
				if (!found) { // header
					range.add((float) c.mValue / mLastValue);					
					found = true;
				}								
			} else {
				if (found) {
					range.add((float) c.mValue / mLastValue);
					found = false;
				}
			}
		}
		// add the last one
		if (found) {
			range.add(1f);
		}
		
		return range;
	}
	
	public Chunk getChunkToSplit(ButtonSplit split) {
		Chunk chunkToSplit = null;
		
		for (Chunk c : mChunks) {			
			if (c.mSplit == split) {
				chunkToSplit = c;
				break;
			}
		}
		
		return chunkToSplit;
	}
	
	public boolean splitChunk(Chunk chunkToSplit) {
		if (chunkToSplit.mNext - chunkToSplit.mValue < 480) {
			return false;
		}
		int i = mChunks.indexOf(chunkToSplit);
		insertChunk(i + 1);
		
		Chunk newChunk = getChunk(i + 1);
		newChunk.setHeight(chunkToSplit.getHeight());
		int centerX = (chunkToSplit.mValue + chunkToSplit.mNext) / 2;
		newChunk.update(centerX, chunkToSplit.mNext);
		chunkToSplit.update(chunkToSplit.mValue, centerX);	
		
		newChunk.mClock.measureSize((int) mCanvasWidth, (int) mCanvasHeight);
		newChunk.mMerge.measureSize((int) mCanvasWidth, (int) mCanvasHeight);
		newChunk.mSplit.measureSize((int) mCanvasWidth, (int) mCanvasHeight);
		newChunk.mQuest.measureSize((int) mCanvasWidth, (int) mCanvasHeight);
		setDisplayOffset(mDispOffsetX, 0);		
		selectChunk(i);
		
		return true;
	}
	
	public boolean mergeChunk(Chunk leftChunk, Chunk rightChunk, Chunk maintain) {
		if (maintain == null) {
			maintain = leftChunk;
		}

		if (maintain == leftChunk) {			
			maintain.update(maintain.mValue, rightChunk.mNext);
			deleteChunk(rightChunk);
		} else {			
			maintain.update(leftChunk.mValue, maintain.mNext);
			deleteChunk(leftChunk);			
		}
	
		selectChunk(maintain);
		
		return true;
	}	
	
	public RectF getSelectedArea() {
		RectF area = new RectF();
		area.left   = mSelectedArea.left + mDispOffsetX;
		area.top    = mSelectedArea.top;
		area.right  = mSelectedArea.right + mDispOffsetX;
		area.bottom = mSelectedArea.bottom;
		return area;
	}
	
	protected void updateSelectedArea() {
		mSelectedArea.left   = mChunks.get(mSelected).mValue + 2;
		mSelectedArea.top    = 2;
		mSelectedArea.right  = 
			(mSelected == mChunks.size() - 1) ? mLastValue : mChunks.get(mSelected + 1).mValue - 2;
		mSelectedArea.bottom = mViewHeight - 2;
	}	
}

