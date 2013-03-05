package edu.neu.android.mhealth.uscteensver1.pages;

import java.util.ArrayList;
import java.util.List;

import edu.neu.android.mhealth.uscteensver1.R;
import edu.neu.android.mhealth.uscteensver1.R.drawable;
import edu.neu.android.mhealth.uscteensver1.data.Chunk;
import edu.neu.android.mhealth.uscteensver1.data.ChunkManager;
import edu.neu.android.mhealth.uscteensver1.data.ChunkManager.OnBoundaryScaleListener;
import edu.neu.android.mhealth.uscteensver1.data.DataSource;
import edu.neu.android.mhealth.uscteensver1.dialog.Actions;
import edu.neu.android.mhealth.uscteensver1.ui.*;
import edu.neu.android.mhealth.uscteensver1.ui.MotionGraph.OnGraphMovedListener;
import edu.neu.android.mhealth.uscteensver1.ui.SlideBar.OnSlideBarChangeListener;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;


public class MainPage extends AppPage implements OnClickListener,
												 OnGraphMovedListener,
												 OnSlideBarChangeListener,
												 OnBoundaryScaleListener {

	protected BackgroundMain mBackground  = null;
	protected MotionGraph	 mMotionGraph = null;
	protected ButtonBack	 mBtnBack     = null;
	protected ButtonNext	 mBtnNext     = null;
	protected SlideBar	 	 mSlideBar    = null;
	
	protected View mView = null;
	protected ChunkManager mChunkManager = null;
	
	protected boolean mScrolling = false; // indicate whether we are scrolling the chunk
	
	public MainPage(Context context, View view, Handler handler) {
		super(context, handler);				
		mView = view;
		mChunkManager = new ChunkManager(context, DataSource.getInstance(null));
		mChunkManager.setUserData(this);
		mChunkManager.setOnBoundaryScaleListener(this);
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
			mBackground = new BackgroundMain(mContext.getResources());			
			mObjects.add(mBackground);
			mBackground.setID(UIID.BKGND);			
		}
		if (mMotionGraph == null) {
			mMotionGraph = new MotionGraph(mContext.getResources(), mChunkManager);
			mObjects.add(mMotionGraph);
			mMotionGraph.setID(UIID.GRAPH);							
			mMotionGraph.setOnGraphMovedListener(this);
		}
		if (mBtnBack == null) {
			mBtnBack = new ButtonBack(mContext.getResources());
			mObjects.add(mBtnBack);
			mBtnBack.setID(UIID.BACK);
			mBtnBack.setOnClickListener(this);
		}
		if (mBtnNext == null) {
			mBtnNext = new ButtonNext(mContext.getResources());
			mObjects.add(mBtnNext);
			mBtnNext.setID(UIID.NEXT);
			mBtnNext.setOnClickListener(this);
		}
		if (mSlideBar == null) {
			mSlideBar = new SlideBar(mContext.getResources(), mChunkManager);
			mObjects.add(mSlideBar);
			mSlideBar.setID(UIID.SLIDE);
			mSlideBar.setOnSlideBarChangeListener(this);			
		}		
		// order by Z
		orderByZ(mObjects);		
		
		return mObjects;
	}
	
	public void start() {
		mChunkManager.start();
		load();		
		for (AppObject obj : mObjects) {
			obj.onSizeChanged(mView.getWidth(), mView.getHeight());
		}
	}
	
	public void stop() {
		mChunkManager.stop();
		
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
	// scroll the button clock to the boundary of the screen
	Runnable mScaleLeft = new Runnable() {				
		public void run() {			

			synchronized (this) {				
				mChunkManager.scaleChunkToBoundary(-2);				
			}

			if (mChunkManager.isScaledToLeftBoundary()) {
				mHandler.postDelayed(this, 15);
			} else {
				mHandler.removeCallbacks(this);				
			}
		}
	};
	
	Runnable mScaleRight = new Runnable() {				
		public void run() {	

			synchronized (this) {				
				mChunkManager.scaleChunkToBoundary(2);
			}
			
			if (mChunkManager.isScaledToRightBoundary()) {
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
				mChunkManager.scaleChunk((int) -distanceX);
				if (distanceX < 0) {
					mHandler.removeCallbacks(mScaleLeft);
					
				} else {
					mHandler.removeCallbacks(mScaleRight);
				}
				
				if (mChunkManager.isScaledToLeftBoundary()) {
					mHandler.postDelayed(mScaleLeft, 15);
				} else if (mChunkManager.isScaledToRightBoundary()) {
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
			tryToBack((ButtonBack) obj);
			break;
		case UIID.NEXT:    	
			tryToNext((ButtonNext) obj);
			break;
		case UIID.QUEST:						
	        tryToQuest((ButtonQuest) obj);
			break;
		case UIID.SPLIT:
			tryToSplit((ButtonSplit) obj);
			break;
		case UIID.MERGE:
			tryToMerge((ButtonMerge) obj);
			break;
		default:
			break;
		}
	}
	
	private void tryToBack(ButtonBack back) {
		Chunk prevUnmarked = mChunkManager.getPreviousUnmarkedChunk();
		if (prevUnmarked != null) {
			mMotionGraph.moveGraph(prevUnmarked.mStart, 0);				
			float progress = (float) prevUnmarked.mStart / mMotionGraph.getRightBound();
			mSlideBar.moveSliderBarToProgress(progress);
		}
		
		if (mChunkManager.areAllChunksLabelled()) {
			Message msg = mHandler.obtainMessage();			
			msg.what = AppCmd.BACK;
			mHandler.sendMessage(msg);
		}

	}
	
	private void tryToNext(ButtonNext next) {
		Chunk nextUnmarked = mChunkManager.getNextUnmarkedChunk();
		if (nextUnmarked != null) {
			mMotionGraph.moveGraph(nextUnmarked.mStart, 0);			
			float progress = (float) nextUnmarked.mStart / mMotionGraph.getRightBound();
			mSlideBar.moveSliderBarToProgress(progress);
		}
		
		if (mChunkManager.areAllChunksLabelled()) {
			Message msg = mHandler.obtainMessage();			
			msg.what = AppCmd.NEXT;
			mHandler.sendMessage(msg);
		}
		
	}
	
	private void tryToQuest(ButtonQuest quest) {
		Message msg = mHandler.obtainMessage();
		msg.arg1 = quest.getHost().getChunkRealStartTime();
		msg.arg2 = quest.getHost().getChunkRealStopTime();		
		msg.what = AppCmd.QUEST;
		mHandler.sendMessage(msg);				
	}
	
	private void tryToSplit(ButtonSplit split) {		
		if (mChunkManager.splitChunk(split.getHost())) {
			mSlideBar.updateUnmarkedRange();
		} else {
			Toast.makeText(mContext, "Not enough space to split!", Toast.LENGTH_SHORT).show();
		}
	}
	
	private void tryToMerge(ButtonMerge merge) {		
		// get the chunks to merge according to the pressed merge button
		ArrayList<Chunk> mChunksToMerge = mChunkManager.getMergingChunks(merge);
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
			mChunkManager.mergeChunk(mChunksToMerge.get(0), mChunksToMerge.get(1), null);	
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
		ButtonQuest quest = (ButtonQuest) mLastSelObject;
		int index = (Integer) params[0];
		quest.setAnswer(Actions.ACTION_IMGS[index], Actions.ACTION_NAMES[index]);
		synchronized (this) {
			mSlideBar.updateUnmarkedRange();
		}
		
	}		
	
	public void finishMerge(Object... params) {
		ButtonMerge merge = (ButtonMerge) mLastSelObject;
		String selection = (String) params[0];
		Chunk maintain = null; 		
		ArrayList<Chunk> mChunksToMerge = mChunkManager.getMergingChunks(merge);
		String actionL = mChunksToMerge.get(0).mQuest.getStringAnswer();
		String actionR = mChunksToMerge.get(1).mQuest.getStringAnswer();
		
		if (actionL.compareTo(selection) == 0) {
			maintain = mChunksToMerge.get(0);
		} else if (actionR.compareTo(selection) == 0){
			maintain = mChunksToMerge.get(1);
		} else { // "None"
			maintain = mChunksToMerge.get(0);
			maintain.mQuest.setAnswer(R.drawable.question_btn, "None");
		}    		
		// it's not called from AppPage.onTouch, so explicit synchronized is necessary
    	synchronized (this) {
    		mChunkManager.mergeChunk(mChunksToMerge.get(0), mChunksToMerge.get(1), maintain);
    		mSlideBar.updateUnmarkedRange();
    	}
    	
	}

	@Override
	public void onBoundaryScale(float x, float scaleDistance) {
		mMotionGraph.moveGraph(x, 0);	
	}
}
