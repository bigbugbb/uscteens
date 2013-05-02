package edu.neu.android.mhealth.uscteensver1.pages;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import edu.neu.android.mhealth.uscteensver1.USCTeensGlobals;
import edu.neu.android.mhealth.uscteensver1.actions.Action;
import edu.neu.android.mhealth.uscteensver1.actions.ActionManager;
import edu.neu.android.mhealth.uscteensver1.actions.ActionWrap;
import edu.neu.android.mhealth.uscteensver1.data.Chunk;
import edu.neu.android.mhealth.uscteensver1.data.ChunkManager;
import edu.neu.android.mhealth.uscteensver1.data.ChunkManager.OnBoundaryScaleListener;
import edu.neu.android.mhealth.uscteensver1.data.LabelManager;
import edu.neu.android.mhealth.uscteensver1.ui.BackButton;
import edu.neu.android.mhealth.uscteensver1.ui.ChunkButton;
import edu.neu.android.mhealth.uscteensver1.ui.GraphBackground;
import edu.neu.android.mhealth.uscteensver1.ui.MergeButton;
import edu.neu.android.mhealth.uscteensver1.ui.MotionGraph;
import edu.neu.android.mhealth.uscteensver1.ui.MotionGraph.OnGraphMovedListener;
import edu.neu.android.mhealth.uscteensver1.ui.NextButton;
import edu.neu.android.mhealth.uscteensver1.ui.OnClickListener;
import edu.neu.android.mhealth.uscteensver1.ui.QuestButton;
import edu.neu.android.mhealth.uscteensver1.ui.SlideBar;
import edu.neu.android.mhealth.uscteensver1.ui.SlideBar.OnSlideBarChangeListener;
import edu.neu.android.mhealth.uscteensver1.ui.SplitButton;
import edu.neu.android.mhealth.uscteensver1.ui.UIID;
import edu.neu.android.mhealth.uscteensver1.utils.WeekdayCalculator;
import edu.neu.android.wocketslib.support.DataStorage;


public class GraphPage extends AppPage implements OnClickListener,
												 OnGraphMovedListener,
												 OnSlideBarChangeListener,
												 OnBoundaryScaleListener { 
	
	protected GraphBackground mBackground  = null;
	protected MotionGraph	  mMotionGraph = null;
	protected BackButton	  mBtnBack     = null;
	protected NextButton	  mBtnNext     = null;
	protected SlideBar	 	  mSlideBar    = null;
	
	protected View mView = null;	
	
	protected boolean mScrolling = false; // indicate whether we are scrolling the chunk
	
	public GraphPage(Context context, View view, Handler handler) {
		super(context, handler);				
		mView = view;
		ChunkManager.initialize(context);
		ChunkManager.setUserData(this);
		ChunkManager.setOnBoundaryScaleListener(this);
		
		LabelManager.initialize(context);		
		ActionManager.initialize(context);
		ActionManager.start();
	}

	public MotionGraph getMotionGraph() {
		return mMotionGraph;
	}
	
	public List<AppObject> getObjectList() {
		return mObjects;
	}

	public List<AppObject> load() {
		// create game objects
		if (mBackground == null) {
			mBackground = new GraphBackground(mContext.getResources());			
			mObjects.add(mBackground);
			mBackground.setID(UIID.BKGND);			
		}
		if (mMotionGraph == null) {
			mMotionGraph = new MotionGraph(mContext.getResources());
			mObjects.add(mMotionGraph);
			mMotionGraph.setID(UIID.GRAPH);							
			mMotionGraph.setOnGraphMovedListener(this);
		}
		if (mBtnBack == null) {
			mBtnBack = new BackButton(mContext.getResources());
			mObjects.add(mBtnBack);
			mBtnBack.setID(UIID.BACK);
			mBtnBack.setOnClickListener(this);
		}
		if (mBtnNext == null) {
			mBtnNext = new NextButton(mContext.getResources());
			mObjects.add(mBtnNext);
			mBtnNext.setID(UIID.NEXT);
			mBtnNext.setOnClickListener(this);
		}
		if (mSlideBar == null) {
			mSlideBar = new SlideBar(mContext.getResources());
			mObjects.add(mSlideBar);
			mSlideBar.setID(UIID.SLIDE);
			mSlideBar.setOnSlideBarChangeListener(this);			
		}		
		// order by Z
		orderByZ(mObjects);		
		
		return mObjects;
	}
	
	public void start() {				
		ChunkManager.start();
		LabelManager.start();		
		
		load();		
		for (AppObject obj : mObjects) {
			obj.onSizeChanged(mView.getWidth(), mView.getHeight());
		}
		
		// recover the chunk selection state when user quit the screen last time
		String selDate = DataStorage.GetValueString(mContext, USCTeensGlobals.CURRENT_SELECTED_DATE, "");
		String startDate = DataStorage.getStartDate(mContext, "");
		// initialize
		ChunkManager.selectChunk(0);
		mMotionGraph.moveGraph(0, 0);						
		mSlideBar.moveSliderBarToProgress(0);
		// try to recover from the record data		
		try {
			int diff = WeekdayCalculator.daysBetween(startDate, selDate);
			int index   = (int) DataStorage.GetValueLong(mContext, USCTeensGlobals.LAST_SELECTED_CHUNK + diff, 0);
			int offsetX = (int) DataStorage.GetValueLong(mContext, USCTeensGlobals.LAST_DISPLAY_OFFSET_X + diff, 0);
			// recover last position when user quit
			Chunk c = ChunkManager.selectChunk(index);
			if (c != null) {
				mMotionGraph.moveGraph(offsetX, 0);			
				float progress = (float) offsetX / mMotionGraph.getRightBound();
				mSlideBar.moveSliderBarToProgress(progress);
				ChunkManager.setDisplayOffset(-offsetX, 0);
				LabelManager.setDisplayOffset(-offsetX, 0);
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		} finally {
			;
		}
	}
	
	public void stop() {
		ChunkManager.stop();
		LabelManager.stop();
		ActionManager.stop();
		
		mBackground   = null;
		mMotionGraph  = null;
		mBtnBack 	  = null;
		mBtnNext 	  = null;
		mSlideBar 	  = null;
		
		release();								
	}
	
	public void release() {
		super.release();
	}

	// used to update the scale chunk operation if a user try to 
	// scroll the clock button to the boundary of the screen
	Runnable mScaleLeft = new Runnable() {				
		public void run() {			

			synchronized (this) {				
				ChunkManager.scaleChunkToBoundary(-2);				
			}

			if (ChunkManager.isScaledToLeftBoundary()) {
				mHandler.postDelayed(this, 15);
			} else {
				mHandler.removeCallbacks(this);				
			}
		}
	};
	
	Runnable mScaleRight = new Runnable() {				
		public void run() {	

			synchronized (this) {				
				ChunkManager.scaleChunkToBoundary(2);
			}
			
			if (ChunkManager.isScaledToRightBoundary()) {
				mHandler.postDelayed(this, 15);
			} else {
				mHandler.removeCallbacks(this);				
			}
		}
	};

	@Override
	public void onAppEvent(AppEvent e) {
		// TODO Auto-generated method stub
		super.onAppEvent(e);
	}

	@Override
	public void onSizeChanged(int width, int height) {
		// TODO Auto-generated method stub
		super.onSizeChanged(width, height);		
	}

	@Override
	public void onDraw(Canvas c) {				
		for (AppObject obj : mObjects) {
			obj.onDraw(c);
		}
	}
	
	@Override
	public boolean onDown(MotionEvent e) {		
		boolean ret = super.onDown(e);
		return ret;
	}
	
	@Override
	public boolean onUp(MotionEvent e) {		
		boolean ret = super.onUp(e);	
		mHandler.removeCallbacks(mScaleLeft);
		mHandler.removeCallbacks(mScaleRight);
		return ret;
	}
	
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		boolean ret = false;
		
		if (mSelObject != null) {
			if (mSelObject.contains(e2.getX(), e2.getY())) {
				ret = mSelObject.onScroll(e1, e2, distanceX, distanceY);
			} else if (mSelObject.getID() == UIID.SLIDE) {  
				ret = mSelObject.onScroll(e1, e2, distanceX, distanceY);
			} else if (mSelObject.getID() == UIID.CLOCK) {
				ChunkButton cb = (ChunkButton) mSelObject;				
				if (cb.getHost().isLastChunkOfToday()) {
					return ret;
				}
				ChunkManager.scaleChunk((int) -distanceX);
				if (distanceX < 0) {
					mHandler.removeCallbacks(mScaleLeft);
				} else {
					mHandler.removeCallbacks(mScaleRight);
				}
				
				if (ChunkManager.isScaledToLeftBoundary()) {
					mHandler.postDelayed(mScaleLeft, 15);
				} else if (ChunkManager.isScaledToRightBoundary()) {
					mHandler.postDelayed(mScaleRight, 15);
				}
				ret = true;
			} else {
				Log.d("scroll", "out of range");
				mSelObject.onCancelSelection(e2);
				mSelObject.setSelected(false);
				mSelObject = null;
				ret = true;
			}
		}

		return ret;
	}
	
	public void onProgressChanged(SlideBar slideBar, int progress) {
		mMotionGraph.moveGraph(progress);		
	}

	@Override
	public void OnGraphMoved(MotionGraph graph, float progress) {
		mSlideBar.moveSliderBarToProgress(progress);		
	}
	
	@Override
	public void onClick(AppObject obj) {
				
		switch (obj.getID()) {		
		case UIID.BACK:			     
			tryToBack((BackButton) obj);
			break;
		case UIID.NEXT:    	
			tryToNext((NextButton) obj);
			break;
		case UIID.QUEST:						
	        tryToQuest((QuestButton) obj);
			break;
		case UIID.SPLIT:
			tryToSplit((SplitButton) obj);
			break;
		case UIID.MERGE:
			tryToMerge((MergeButton) obj);
			break;
		default:
			break;
		}
	}
	
	private void tryToBack(BackButton back) {
		Chunk prevUnmarked = ChunkManager.getPreviousUnmarkedChunk();
		if (prevUnmarked != null) {
			mMotionGraph.moveGraph(prevUnmarked.mStart, 0);				
			float progress = (float) prevUnmarked.mStart / mMotionGraph.getRightBound();
			mSlideBar.moveSliderBarToProgress(progress);
		}
		
		if (ChunkManager.areAllChunksLabelled()) {
			Message msg = mHandler.obtainMessage();			
			msg.what = AppCmd.BACK;
			mHandler.sendMessage(msg);
		}

	}
	
	private void tryToNext(NextButton next) {
		Chunk nextUnmarked = ChunkManager.getNextUnmarkedChunk();
		if (nextUnmarked != null) {
			mMotionGraph.moveGraph(nextUnmarked.mStart, 0);			
			float progress = (float) nextUnmarked.mStart / mMotionGraph.getRightBound();
			mSlideBar.moveSliderBarToProgress(progress);
		}
		
		if (ChunkManager.areAllChunksLabelled()) {
			Message msg = mHandler.obtainMessage();			
			msg.what = AppCmd.NEXT;
			mHandler.sendMessage(msg);
		}
		
	}
	
	private void tryToQuest(QuestButton quest) {
		Message msg = mHandler.obtainMessage();
		msg.arg1 = quest.getHost().getChunkRealStartTime();
		msg.arg2 = quest.getHost().getChunkRealStopTime();		
		msg.what = AppCmd.QUEST;
		mHandler.sendMessage(msg);				
	}
	
	private void tryToSplit(SplitButton split) {		
		if (ChunkManager.splitChunk(split.getHost())) {
			mSlideBar.updateUnmarkedRange();
		} else {
			Toast.makeText(mContext, "Not enough space to split!", Toast.LENGTH_SHORT).show();
		}
	}
	
	private void tryToMerge(MergeButton merge) {		
		// get the chunks to merge according to the pressed merge button
		ArrayList<Chunk> mChunksToMerge = ChunkManager.getMergingChunks(merge);
		String actionL = mChunksToMerge.get(0).mQuest.getStringAnswer();
		String actionR = mChunksToMerge.get(1).mQuest.getStringAnswer();
		ArrayList<String> actions = new ArrayList<String>();
		if (actionL.compareTo("None") != 0) {
			actions.add(actionL);
		}
		if (actionR.compareTo("None") != 0) {
			actions.add(actionR);
		}
		// the left and right chunks are both unanswered, merge them directly
		if (actions.size() == 0 || actionL.compareTo(actionR) == 0) {
			ChunkManager.mergeChunk(mChunksToMerge.get(0), mChunksToMerge.get(1), null);	
			mSlideBar.updateUnmarkedRange();
		} else {
			actions.add("None");			
			Message msg = mHandler.obtainMessage();			
			msg.obj  = actions;
			msg.what = AppCmd.MERGE;
			mHandler.sendMessage(msg);									
		}
	}
	
	public void finishQuest(Object... params) {
		QuestButton quest = (QuestButton) mLastSelObject;
		String actionID = (String) params[0];	
		ActionWrap actions = ActionManager.getActions();
		Action action = actions.get(actionID);
		quest.setAnswer(action);
		synchronized (this) {
			mSlideBar.updateUnmarkedRange();
		}
		
	}		
	
	public void finishMerge(Object... params) {
		MergeButton merge = (MergeButton) mLastSelObject;
		String selection = (String) params[0];
		Chunk maintain = null; 		
		ArrayList<Chunk> mChunksToMerge = ChunkManager.getMergingChunks(merge);
		String actionL = mChunksToMerge.get(0).mQuest.getStringAnswer();
		String actionR = mChunksToMerge.get(1).mQuest.getStringAnswer();
		
		if (actionL.compareTo(selection) == 0) {
			maintain = mChunksToMerge.get(0);
		} else if (actionR.compareTo(selection) == 0){
			maintain = mChunksToMerge.get(1);
		} else { // "None"
			maintain = mChunksToMerge.get(0);
			Action action = ActionManager.getActions().get(USCTeensGlobals.UNLABELLED_GUID);
			maintain.mQuest.setAnswer(action);
		}    		
		// it's not called from AppPage.onTouch, so explicit synchronized is necessary
    	synchronized (this) {
    		ChunkManager.mergeChunk(mChunksToMerge.get(0), mChunksToMerge.get(1), maintain);
    		mSlideBar.updateUnmarkedRange();
    	}
    	
	}

	@Override
	public void onBoundaryScale(float x, float scaleDistance) {
		mMotionGraph.moveGraph(x, 0);	
	}
}
