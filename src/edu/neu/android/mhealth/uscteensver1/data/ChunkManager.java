package edu.neu.android.mhealth.uscteensver1.data;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.RectF;
import edu.neu.android.mhealth.uscteensver1.R;
import edu.neu.android.mhealth.uscteensver1.dialog.Actions;
import edu.neu.android.mhealth.uscteensver1.dialog.ActionsDialog;
import edu.neu.android.mhealth.uscteensver1.main.AppScale;
import edu.neu.android.mhealth.uscteensver1.ui.ButtonClock;
import edu.neu.android.mhealth.uscteensver1.ui.ButtonMerge;
import edu.neu.android.mhealth.uscteensver1.ui.ButtonSplit;
import edu.neu.android.mhealth.uscteensver1.ui.ListView;
import edu.neu.android.mhealth.uscteensver1.ui.MotionGraph;
import edu.neu.android.mhealth.uscteensver1.ui.MotionGraph.OnGraphMovedListener;


public class ChunkManager {
	
	public Resources mRes   = null;
	public Object mUserData = null;			
	public ArrayList<Chunk> mChunks = null;
	
	protected final static int MINIMUM_SPACE_FOR_SPLIT = 240;
	
	protected int   mSelected = -1; // -1 indicates no chunk has been selected
	protected RectF mSelectedArea = new RectF();
	protected ButtonClock mClockL = null;
	protected ButtonClock mClockR = null;
	protected ButtonMerge mMergeL = null;
	protected ButtonMerge mMergeR = null;
	protected ButtonSplit mSplit  = null;
	
	protected float mDispOffsetX  = 0;
	protected float mDispOffsetY  = 0; // not really useful here
	protected float mViewWidth    = 0; // the area to draw the activity data
	protected float mViewHeight   = 0;
	protected float mCanvasWidth  = 0;
	protected float mCanvasHeight = 0;
		
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
		RawChunkList rawChunks = mDataSrc.getRawChunkList();
		
		if (mChunks == null) {
			mChunks = new ArrayList<Chunk>();
		}
		
		int timeOffset = 0;
		for (int i = 0; i < rawChunks.size(); ++i) {
			Chunk chunk = insertChunk(i);	
			RawChunk rawChunk = rawChunks.get(i);
			
			if (i == 0) { // save the start time offset
				timeOffset = rawChunk.getStartTime();
			}
			int start = (rawChunk.getStartTime() - timeOffset) * DataSource.PIXEL_SCALE;
			int stop  = (rawChunk.getStopTime()  - timeOffset) * DataSource.PIXEL_SCALE;
			int actionID = rawChunk.getActionID();
			chunk.update(start, stop, timeOffset);			
			chunk.mQuest.setAnswer(
				actionID == -1 ? R.drawable.question_btn : Actions.ACTION_IMGS[actionID], 
				actionID == -1 ? "None" : Actions.ACTION_NAMES[actionID]
			);
		}
		// select the first chunk
		selectChunk(0);
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
		RawChunkList rawChunks = new RawChunkList();
		
		for (int i = 0; i < mChunks.size(); ++i) {
			Chunk chunk = mChunks.get(i);
			RawChunk rawChunk = chunk.toRawChunk();
			rawChunks.add(rawChunk);
		}
		mDataSrc.saveChunkData(rawChunks);
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

	public Chunk insertChunk(int index) {		
		Chunk chunk = new Chunk(mRes, this, mUserData);			
		mChunks.add(index, chunk);
		return chunk;
	}		

	public void deleteChunk(Chunk chunk) {
		mChunks.remove(chunk);
		chunk.release();
	}
	
	public boolean scaleChunk(int scale) {
		int i = 0;
		
		// look for the selected chunk
		for (i = 0; i < mChunks.size(); ++i) {
			Chunk c = mChunks.get(i);
			if (c.mClock.isSelected()) {			
				break;
			}
		}		
		assert(i < mChunks.size());
		
		Chunk curr = mChunks.get(i);
		Chunk prev = (i == 0) ? null : mChunks.get(i - 1);
		Chunk next = (i == mChunks.size() - 1) ? null : mChunks.get(i + 1);
		
		// scale the chunk
		boolean success = false;
		if (prev != null) {
			success = prev.update(prev.mStart, curr.mStart + scale, prev.mOffset);
		}
		if (success) {
			curr.update(curr.mStart + scale, curr.mStop, prev.mOffset);
		}
		
		if (mSelected > -1) {
			updateSelectedArea();
		}
		
		return success;
	}
	
	public boolean scaleChunkToBoundary(int scale) {
		int i = 0;
		
		// look for the selected chunk
		for (i = 0; i < mChunks.size(); ++i) {
			Chunk c = mChunks.get(i);
			if (c.mClock.isSelected()) {			
				break;
			}
		}		
		assert(i < mChunks.size());
		
		Chunk curr = mChunks.get(i);
		Chunk prev = (i == 0) ? null : mChunks.get(i - 1);
		Chunk next = (i == mChunks.size() - 1) ? null : mChunks.get(i + 1);
		
		// scale the chunk
		boolean success = false;
		if (prev != null) {
			success = prev.update(prev.mStart, curr.mStart + scale, prev.mOffset);
		}
		if (success) {
			curr.update(curr.mStart + scale, curr.mStop, prev.mOffset);
		}
		
		if (mSelected > -1) {
			updateSelectedArea();
		}			
		
		if (mListener != null) {
			if (scale > 0) {
				mListener.onBoundaryScale(curr.mClock.getX() - mCanvasWidth * 0.87f, scale);
			} else {
				mListener.onBoundaryScale(curr.mClock.getX() - mCanvasWidth * 0.06f, scale);
			}
		}
		
		return success;
	}
	
	public boolean isScaledToLeftBoundary() {
		// look for the selected chunk
		int i = 0;
		for (i = 0; i < mChunks.size(); ++i) {
			Chunk c = mChunks.get(i);
			if (c.mClock.isSelected()) {			
				break;
			}
		}			
		Chunk curr = mChunks.get(i);
		ButtonClock clock = curr.mClock;
		float clockX = clock.getX() + curr.mDispOffsetX;
		
		return clockX < mCanvasWidth * 0.1;
	}
	
	public boolean isScaledToRightBoundary() {
		// look for the selected chunk
		int i = 0;		
		for (i = 0; i < mChunks.size(); ++i) {
			Chunk c = mChunks.get(i);
			if (c.mClock.isSelected()) {			
				break;
			}
		}	
		Chunk curr = mChunks.get(i);
		ButtonClock clock = curr.mClock;
		float clockX = clock.getX() + curr.mDispOffsetX;
		
		return clockX > mCanvasWidth * 0.85;
	}
	
	public Chunk getPreviousUnmarkedChunk() {
		Chunk prev = null;
		float current = -mDispOffsetX - 1;

		for (int i = mChunks.size() - 1; i >= 0; --i) {
			Chunk c = mChunks.get(i);
			if (c.mStart <= current) {
				if (!c.mQuest.isAnswered()) {
					prev = c;
					break;
				}
			}
		}
		
		return prev;
	}
	
	public Chunk getNextUnmarkedChunk() {
		Chunk next = null;
		float current = -mDispOffsetX + 1;		
		
		for (int i = 0; i < mChunks.size(); ++i) {
			Chunk c = mChunks.get(i);
			if (c.mStart >= current) {
				if (!c.mQuest.isAnswered()) {		
					next = c;
					break;
				}
			}
		}
		
		return next;
	}
	
	public void selectChunk(Chunk chunk) {
		selectChunk(mChunks.indexOf(chunk));
	}
	
	public void selectChunk(int index) { ///////////////
		assert(index >= 0 && index < mChunks.size());
		if (mSelected > -1 && mSelected < mChunks.size()) {				
			mChunks.get(mSelected).setSelected(false);
		}
		mSelected = index;
		
		// set buttons of the last selected chunk invisible
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

		Chunk c = mChunks.get(index); ///////////////////////
		c.setSelected(true);
		mClockL = c.mClock;
		mMergeL = c.mMerge;
		mSplit  = c.mSplit;
		c = (index == mChunks.size() - 1) ? null : mChunks.get(index + 1);
		if (c != null) {
			mClockR = c.mClock;
			mMergeR = c.mMerge;
		}
	
		// set buttons visible
		if (mClockL != null && index != 0) {
			mClockL.setVisible(true);
		}
		if (mClockR != null) {
			mClockR.setVisible(true);
		}
		if (mMergeL != null && index != 0) {
			mMergeL.setVisible(true);
		}
		if (mMergeR != null) {
			mMergeR.setVisible(true);
		}
		if (mSplit != null) {
			mSplit.setVisible(true);
		}
		
		updateSelectedArea();
	}
	
	public boolean selectChunk(float x, float y) {					
		int i = 0;
		for (; i < mChunks.size(); ++i) {
			Chunk c = mChunks.get(i);
			if (c.contains(x, y)) {
				selectChunk(i);						
				break;
			}
		}
								
		return i < mChunks.size();
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
				break;
			}
		}		
		
		return chunks;
	}
	
	public ArrayList<Float> getUnmarkedRange() {
		ArrayList<Float> range = new ArrayList<Float>();		

		boolean found = false;
		for (Chunk c : mChunks) {
			if (!c.mQuest.isAnswered()) {
				if (!found) { // header
					range.add((float) c.mStart / mDataSrc.getActivityLengthInPixel());					
					found = true;
				}								
			} else {
				if (found) {
					range.add((float) c.mStart / mDataSrc.getActivityLengthInPixel());
					found = false;
				}
			}
		}
		// add the last
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
		AppScale appScale = AppScale.getInstance();

		int   centerX = (chunkToSplit.mStart + chunkToSplit.mStop) / 2;
		float offsetInChunkX = chunkToSplit.mSplit.getOffsetInChunkX();
		float splitX = centerX + offsetInChunkX;

		// check if there is enough space for the split
		if (offsetInChunkX > 0) {
			if (chunkToSplit.mStop - splitX < appScale.doScaleW(MINIMUM_SPACE_FOR_SPLIT)) {
				return false;
			}
		} else {
			if (splitX - chunkToSplit.mStart < appScale.doScaleW(MINIMUM_SPACE_FOR_SPLIT)) {
				return false;
			}
		}		

		// split at the splitX
		int i = mChunks.indexOf(chunkToSplit);				
		Chunk newChunk = insertChunk(i + 1); // insert a new chunk, which should be updated later
		newChunk.setHeight(chunkToSplit.getHeight());		
		newChunk.update((int) splitX, chunkToSplit.mStop, chunkToSplit.mOffset);
		chunkToSplit.update(chunkToSplit.mStart, (int) splitX, chunkToSplit.mOffset);

		newChunk.mClock.measureSize((int) mCanvasWidth, (int) mCanvasHeight);
		newChunk.mMerge.measureSize((int) mCanvasWidth, (int) mCanvasHeight);
		newChunk.mSplit.measureSize((int) mCanvasWidth, (int) mCanvasHeight);
		newChunk.mQuest.measureSize((int) mCanvasWidth, (int) mCanvasHeight);
		setDisplayOffset(mDispOffsetX, 0);	

		if (offsetInChunkX > 0) { // split left
			selectChunk(i + 1);
		} else {
			selectChunk(i);
		}
		
		return true;
	}
	
	public boolean mergeChunk(Chunk leftChunk, Chunk rightChunk, Chunk maintain) {
		if (maintain == null) {
			maintain = leftChunk;
		}

		if (maintain == leftChunk) {			
			maintain.update(maintain.mStart, rightChunk.mStop, maintain.mOffset);
			deleteChunk(rightChunk);
		} else {			
			maintain.update(leftChunk.mStart, maintain.mStop, maintain.mOffset);
			deleteChunk(leftChunk);			
		}
		// update the new chunk button offset, especially the offset in chunk
		setDisplayOffset(mDispOffsetX, mDispOffsetY);
	
		selectChunk(maintain);
		
		return true;
	}	
	
	public RectF getSelectedArea() {
		RectF area = new RectF();
		area.left   = mSelectedArea.left + mDispOffsetX;
		area.top    = mSelectedArea.top;
		area.right  = mSelectedArea.right + mDispOffsetX;
		area.bottom = mSelectedArea.bottom + 5;
		return area;
	}
	
	protected void updateSelectedArea() {

		mSelectedArea.left   = mChunks.get(mSelected).mStart + 2;
		mSelectedArea.top    = 2;
		mSelectedArea.right  = (mSelected == mChunks.size() - 1) ? 
			mDataSrc.getActivityLengthInPixel() : mChunks.get(mSelected + 1).mStart - 2;
		mSelectedArea.bottom = mViewHeight - 2;
	}	
	
	public boolean areAllChunksLabelled() {
		for (Chunk c : mChunks) {
			if (!c.mQuest.isAnswered()) {
				return false;
			}
		}
		
		return true;
	}
	
	protected OnBoundaryScaleListener mListener = null;
	
	public void setOnBoundaryScaleListener(OnBoundaryScaleListener listener) {
		mListener = listener;
	}
	
	public interface OnBoundaryScaleListener {
		void onBoundaryScale(float x, float scaleDistance);
	}
}

