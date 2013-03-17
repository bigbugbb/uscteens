package edu.neu.android.mhealth.uscteensver1.data;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.RectF;
import edu.neu.android.mhealth.uscteensver1.R;
import edu.neu.android.mhealth.uscteensver1.USCTeensGlobals;
import edu.neu.android.mhealth.uscteensver1.dialog.QuestDialog;
import edu.neu.android.mhealth.uscteensver1.pages.AppScale;
import edu.neu.android.mhealth.uscteensver1.ui.ClockButton;
import edu.neu.android.mhealth.uscteensver1.ui.MergeButton;
import edu.neu.android.mhealth.uscteensver1.ui.SplitButton;
import edu.neu.android.mhealth.uscteensver1.ui.ListView;
import edu.neu.android.mhealth.uscteensver1.ui.MotionGraph;
import edu.neu.android.mhealth.uscteensver1.ui.MotionGraph.OnGraphMovedListener;


public class ChunkManager {
	
	protected static Resources sResources = null;
	protected static Object sUserData = null;			
	protected static ArrayList<Chunk> sChunks = null;
	
	protected static int   sSelected = -1; // -1 indicates no chunk has been selected
	protected static RectF sSelectedArea = new RectF();
	protected static ClockButton sClockL = null;
	protected static ClockButton sClockR = null;
	protected static MergeButton sMergeL = null;
	protected static MergeButton sMergeR = null;
	protected static SplitButton sSplit  = null;
	
	protected static float sDispOffsetX  = 0;
	protected static float sDispOffsetY  = 0; // not really useful here
	protected static float sViewWidth    = 0; // the area to draw the activity data
	protected static float sViewHeight   = 0;
	protected static float sCanvasWidth  = 0;
	protected static float sCanvasHeight = 0;
		
	protected static Context sContext = null;

	public static void initialize(Context context) {
		sResources = context.getResources();
		sContext = context;		
	}	
	
	protected static void load() {
		
	}
	
	public static void start() {
		loadChunks();
	}
	
	public static void stop() {
		saveChunks();
		release();
	}
	
	protected static void loadChunks() {
		RawChunksWrap rawChunks = DataSource.getRawChunks();
		
		if (sChunks == null) {
			sChunks = new ArrayList<Chunk>();
		}
		
		int timeOffset = 0;
		for (int i = 0; i < rawChunks.size(); ++i) {
			Chunk chunk = insertChunk(i);	
			RawChunk rawChunk = rawChunks.get(i);
			
			if (i == 0) { // save the start time offset
				timeOffset = rawChunk.getStartTime();
			}
			int start = (rawChunk.getStartTime() - timeOffset) * USCTeensGlobals.PIXEL_PER_DATA;
			int stop  = (rawChunk.getStopTime()  - timeOffset) * USCTeensGlobals.PIXEL_PER_DATA;
			int activityID = rawChunk.getActivityID();
			chunk.update(start, stop, timeOffset);			
			chunk.mQuest.setAnswer(
				activityID == -1 ? R.drawable.question_btn : USCTeensGlobals.ACTION_IMGS[activityID], 
				activityID == -1 ? "None" : USCTeensGlobals.ACTION_NAMES[activityID]
			);
		}
		// select the first chunk
		selectChunk(0);
	}
	
	public static void release() {
		if (sChunks != null) {
			for (Chunk c : sChunks) {
				c.release();
			}
			sChunks = null;
		}
	}
	
	protected static void saveChunks() {		
		DataSource.saveChunkData(sChunks);
	}
	
	public static Object getUserData() {
		return sUserData;
	}
	
	public static Chunk getChunk(int index) {
		return sChunks.get(index);
	}
	
	public static ArrayList<Chunk> getChunks() {
		return sChunks;
	}
	
	public static int getChunkSize() {
		return sChunks.size();
	}
	
	public static void setUserData(Object userData) {
		sUserData = userData;
	}
	
	public static void setViewSize(float width, float height) {
		sViewWidth  = width;
		sViewHeight = height;
	}
	
	public static float getViewWidth() {
		return sViewWidth;
	}
	
	public static float getViewHeight() {
		return sViewHeight;
	}
	
	public static void setCanvasSize(float width, float height) {
		sCanvasWidth  = width;
		sCanvasHeight = height;
	}
	
	public static float getCanvasWidth() {
		return sCanvasWidth;
	}
	
	public static float getCanvasHeight() {
		return sCanvasHeight;
	}
	
	public static void setDisplayOffset(float offsetX, float offsetY) {
		sDispOffsetX = offsetX;
		sDispOffsetY = offsetY;
		
		for (int i = 0; i < sChunks.size(); ++i) {
			Chunk c = sChunks.get(i);
			c.setDisplayOffset(offsetX, offsetY);
		}
	}

	public static Chunk insertChunk(int index) {		
		Chunk chunk = new Chunk(sResources);			
		sChunks.add(index, chunk);
		return chunk;
	}		

	public static void deleteChunk(Chunk chunk) {
		sChunks.remove(chunk);
		chunk.release();
	}
	
	public static boolean scaleChunk(int scale) {
		int i = 0;
		
		// look for the selected chunk
		for (i = 0; i < sChunks.size(); ++i) {
			Chunk c = sChunks.get(i);
			if (c.mClock.isSelected()) {			
				break;
			}
		}		
		assert(i < sChunks.size());
		
		Chunk curr = sChunks.get(i);
		Chunk prev = (i == 0) ? null : sChunks.get(i - 1);
		Chunk next = (i == sChunks.size() - 1) ? null : sChunks.get(i + 1);
		
		// scale the chunk
		boolean success = false;
		if (prev != null) {
			success = prev.update(prev.mStart, curr.mStart + scale, prev.mOffset);
		}
		if (success) {
			curr.update(curr.mStart + scale, curr.mStop, prev.mOffset);
		}
		
		if (sSelected > -1) {
			updateSelectedArea();
		}
		
		return success;
	}
	
	public static boolean scaleChunkToBoundary(int scale) {
		int i = 0;
		
		// look for the selected chunk
		for (i = 0; i < sChunks.size(); ++i) {
			Chunk c = sChunks.get(i);
			if (c.mClock.isSelected()) {			
				break;
			}
		}		
		assert(i < sChunks.size());
		
		Chunk curr = sChunks.get(i);
		Chunk prev = (i == 0) ? null : sChunks.get(i - 1);
		Chunk next = (i == sChunks.size() - 1) ? null : sChunks.get(i + 1);
		
		// scale the chunk
		boolean success = false;
		if (prev != null) {
			success = prev.update(prev.mStart, curr.mStart + scale, prev.mOffset);
		}
		if (success) {
			curr.update(curr.mStart + scale, curr.mStop, prev.mOffset);
		}
		
		if (sSelected > -1) {
			updateSelectedArea();
		}			
		
		if (sListener != null) {
			if (scale > 0) {
				sListener.onBoundaryScale(curr.mClock.getX() - sCanvasWidth * 0.87f, scale);
			} else {
				sListener.onBoundaryScale(curr.mClock.getX() - sCanvasWidth * 0.06f, scale);
			}
		}
		
		return success;
	}
	
	public static boolean isScaledToLeftBoundary() {
		// look for the selected chunk
		int i = 0;
		for (i = 0; i < sChunks.size(); ++i) {
			Chunk c = sChunks.get(i);
			if (c.mClock.isSelected()) {			
				break;
			}
		}			
		Chunk curr = sChunks.get(i);
		ClockButton clock = curr.mClock;
		float clockX = clock.getX() + curr.mDispOffsetX;
		
		return clockX < sCanvasWidth * 0.1;
	}
	
	public static boolean isScaledToRightBoundary() {
		// look for the selected chunk
		int i = 0;		
		for (i = 0; i < sChunks.size(); ++i) {
			Chunk c = sChunks.get(i);
			if (c.mClock.isSelected()) {			
				break;
			}
		}	
		Chunk curr = sChunks.get(i);
		ClockButton clock = curr.mClock;
		float clockX = clock.getX() + curr.mDispOffsetX;
		
		return clockX > sCanvasWidth * 0.85;
	}
	
	public static Chunk getPreviousUnmarkedChunk() {
		Chunk prev = null;
		float current = -sDispOffsetX - 1;

		for (int i = sChunks.size() - 1; i >= 0; --i) {
			Chunk c = sChunks.get(i);
			if (c.mStart <= current) {
				if (!c.mQuest.isAnswered()) {
					prev = c;
					break;
				}
			}
		}
		
		return prev;
	}
	
	public static Chunk getNextUnmarkedChunk() {
		Chunk next = null;
		float current = -sDispOffsetX + 1;		
		
		for (int i = 0; i < sChunks.size(); ++i) {
			Chunk c = sChunks.get(i);
			if (c.mStart >= current) {
				if (!c.mQuest.isAnswered()) {		
					next = c;
					break;
				}
			}
		}
		
		return next;
	}
	
	public static void selectChunk(Chunk chunk) {
		selectChunk(sChunks.indexOf(chunk));
	}
	
	public static void selectChunk(int index) { ///////////////
		assert(index >= 0 && index < sChunks.size());		
		if (sSelected > -1 && sSelected < sChunks.size()) {		
			sChunks.get(sSelected).setSelected(false);
		}
		sSelected = index;
		
		// set buttons of the last selected chunk invisible
		if (sClockL != null) {
			sClockL.setVisible(false);
			sClockL = null;
		}
		if (sClockR != null) {
			sClockR.setVisible(false);
			sClockR = null;
		}
		if (sMergeL != null) {
			sMergeL.setVisible(false);
			sMergeL = null;
		}
		if (sMergeR != null) {
			sMergeR.setVisible(false);
			sMergeR = null;
		}
		if (sSplit != null) {
			sSplit.setVisible(false);
			sSplit = null;
		}		

		Chunk c = sChunks.get(index); ///////////////////////		
		c.setSelected(true);
		sClockL = c.mClock;
		sMergeL = c.mMerge;
		sSplit  = c.mSplit;
		c = (index == sChunks.size() - 1) ? null : sChunks.get(index + 1);
		if (c != null) {
			sClockR = c.mClock;
			sMergeR = c.mMerge;
		}
	
		// set buttons visible
		if (sClockL != null && index != 0) {
			sClockL.setVisible(true);
		}
		if (sClockR != null) {
			sClockR.setVisible(true);
		}
		if (sMergeL != null && index != 0 && !sMergeL.getHost().isLastChunkOfToday()) {
			sMergeL.setVisible(true);
		}
		if (sMergeR != null && !sMergeR.getHost().isLastChunkOfToday()) {
			sMergeR.setVisible(true);
		}
		if (sSplit != null && !sSplit.getHost().isLastChunkOfToday()) {
			sSplit.setVisible(true);
		}
		
		updateSelectedArea();
	}
	
	public static boolean selectChunk(float x, float y) {					
		int i = 0;
		for (; i < sChunks.size(); ++i) {
			Chunk c = sChunks.get(i);
			if (c.contains(x, y)) {
				selectChunk(i);						
				break;
			}
		}
								
		return i < sChunks.size();
	}
	
	public static ArrayList<Chunk> getMergingChunks(MergeButton merge) {
		ArrayList<Chunk> chunks = new ArrayList<Chunk>();		
		int size = sChunks.size();

		for (int i = 1; i < size; ++i) {
			Chunk right = sChunks.get(i);
			if (right.mMerge == merge) {
				Chunk left = sChunks.get(i - 1);
				chunks.add(left);
				chunks.add(right);
				break;
			}
		}		
		
		return chunks;
	}
	
	public static ArrayList<Float> getUnmarkedRange() {
		ArrayList<Float> range = new ArrayList<Float>();		

		boolean found = false;
		for (Chunk c : sChunks) {
			if (!c.mQuest.isAnswered()) {
				if (!found) { // header
					range.add((float) c.mStart / DataSource.getDrawableDataLengthInPixel());					
					found = true;
				}								
			} else {
				if (found) {
					range.add((float) c.mStart / DataSource.getDrawableDataLengthInPixel());
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
	
	public static Chunk getChunkToSplit(SplitButton split) {
		Chunk chunkToSplit = null;

		for (Chunk c : sChunks) {			
			if (c.mSplit == split) {
				chunkToSplit = c;
				break;
			}
		}

		return chunkToSplit;
	}
	
	public static boolean splitChunk(Chunk chunkToSplit) {
		AppScale appScale = AppScale.getInstance();

		int   centerX = (chunkToSplit.mStart + chunkToSplit.mStop) / 2;
		float offsetInChunkX = chunkToSplit.mSplit.getOffsetInChunkX();
		float splitX = centerX + offsetInChunkX;

		// check if there is enough space for the split
		if (offsetInChunkX > 0) {
			if (chunkToSplit.mStop - splitX < appScale.doScaleW(Chunk.MINIMUM_CHUNK_SPACE)) {
				return false;
			}
		} else {
			if (splitX - chunkToSplit.mStart < appScale.doScaleW(Chunk.MINIMUM_CHUNK_SPACE)) {
				return false;
			}
		}		

		// split at the splitX
		int i = sChunks.indexOf(chunkToSplit);				
		Chunk newChunk = insertChunk(i + 1); // insert a new chunk, which should be updated later
		newChunk.setHeight(chunkToSplit.getHeight());		
		newChunk.update((int) splitX, chunkToSplit.mStop, chunkToSplit.mOffset);
		chunkToSplit.update(chunkToSplit.mStart, (int) splitX, chunkToSplit.mOffset);

		newChunk.mClock.measureSize((int) sCanvasWidth, (int) sCanvasHeight);
		newChunk.mMerge.measureSize((int) sCanvasWidth, (int) sCanvasHeight);
		newChunk.mSplit.measureSize((int) sCanvasWidth, (int) sCanvasHeight);
		newChunk.mQuest.measureSize((int) sCanvasWidth, (int) sCanvasHeight);
		setDisplayOffset(sDispOffsetX, 0);	

		if (offsetInChunkX > 0) { // split left
			selectChunk(i + 1);
		} else {
			selectChunk(i);
		}
		
		return true;
	}
	
	public static boolean mergeChunk(Chunk leftChunk, Chunk rightChunk, Chunk maintain) {
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
		setDisplayOffset(sDispOffsetX, sDispOffsetY);
	
		selectChunk(maintain);
		
		return true;
	}	
	
	public static RectF getSelectedArea() {
		RectF area = new RectF();
		area.left   = sSelectedArea.left + sDispOffsetX;
		area.top    = sSelectedArea.top;
		area.right  = sSelectedArea.right + sDispOffsetX;
		area.bottom = sSelectedArea.bottom + 5;
		return area;
	}
	
	protected static void updateSelectedArea() {
		sSelectedArea.left   = sChunks.get(sSelected).mStart + 2;
		sSelectedArea.top    = 2;
		sSelectedArea.right  = (sSelected == sChunks.size() - 1) ? 
			DataSource.getDrawableDataLengthInPixel() : sChunks.get(sSelected + 1).mStart - 2;
		sSelectedArea.bottom = sViewHeight - 2;
	}	
	
	public static boolean areAllChunksLabelled() {
		for (Chunk c : sChunks) {
			if (!c.mQuest.isAnswered()) {
				return false;
			}
		}
		
		return true;
	}
	
	protected static OnBoundaryScaleListener sListener = null;
	
	public static void setOnBoundaryScaleListener(OnBoundaryScaleListener listener) {
		sListener = listener;
	}
	
	public interface OnBoundaryScaleListener {
		void onBoundaryScale(float x, float scaleDistance);
	}
}

