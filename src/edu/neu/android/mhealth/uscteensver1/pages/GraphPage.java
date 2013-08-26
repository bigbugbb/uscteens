package edu.neu.android.mhealth.uscteensver1.pages;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import edu.neu.android.mhealth.uscteensver1.TeensAppManager;
import edu.neu.android.mhealth.uscteensver1.TeensGlobals;
import edu.neu.android.mhealth.uscteensver1.data.Chunk;
import edu.neu.android.mhealth.uscteensver1.data.ChunkManager;
import edu.neu.android.mhealth.uscteensver1.data.ChunkManager.OnBoundaryScaleListener;
import edu.neu.android.mhealth.uscteensver1.data.DataSource;
import edu.neu.android.mhealth.uscteensver1.data.LabelManager;
import edu.neu.android.mhealth.uscteensver1.extra.Action;
import edu.neu.android.mhealth.uscteensver1.extra.ActionManager;
import edu.neu.android.mhealth.uscteensver1.extra.ActionWrap;
import edu.neu.android.mhealth.uscteensver1.extra.Reward;
import edu.neu.android.mhealth.uscteensver1.extra.RewardManager;
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
import edu.neu.android.mhealth.uscteensver1.utils.NoteSender;
import edu.neu.android.wocketslib.Globals;
import edu.neu.android.wocketslib.support.DataStorage;
import edu.neu.android.wocketslib.utils.DateHelper;
import edu.neu.android.wocketslib.utils.WeekdayHelper;


public class GraphPage extends AppPage implements OnClickListener,
        OnGraphMovedListener,
        OnSlideBarChangeListener,
        OnBoundaryScaleListener {

    protected MotionGraph     mMotionGraph = null;
    protected BackButton      mBtnBack     = null;
    protected NextButton      mBtnNext     = null;
    protected SlideBar        mSlideBar    = null;
    protected GraphBackground mBackground  = null;

    protected View mView = null;

    public GraphPage(Context context, View view, Handler handler) {
        super(context, handler);
        mView = view;
        ChunkManager.setUserData(this);
        ChunkManager.setOnBoundaryScaleListener(this);
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
        String selDate = DataStorage.GetValueString(mContext, TeensGlobals.CURRENT_SELECTED_DATE, "");
        String startDate = DataStorage.getStartDate(mContext, "");
        // initialize
        ChunkManager.selectChunk(0);
        mMotionGraph.moveGraph(0, 0);
        mSlideBar.moveSliderBarToProgress(0);
        // try to recover from the record data
        long loadingResult = DataStorage.GetValueLong(
                mContext, TeensGlobals.DATA_LOADING_RESULT, DataSource.LOADING_SUCCEEDED);
        if (loadingResult == DataSource.ERR_NO_SENSOR_DATA) {
            return;
        }
        try {
            int diff = WeekdayHelper.daysBetween(startDate, selDate);
            int index = (int) DataStorage.GetValueLong(mContext, TeensGlobals.LAST_SELECTED_CHUNK + diff, 0);
            int offsetX = (int) DataStorage.GetValueLong(mContext, TeensGlobals.LAST_DISPLAY_OFFSET_X + diff, 0);
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
            e.printStackTrace();
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        } finally {
            // nothing now
        }
    }

    public void stop() {
        ChunkManager.stop();
        LabelManager.stop();

        mBackground = null;
        mMotionGraph = null;
        mBtnBack = null;
        mBtnNext = null;
        mSlideBar = null;

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
        super.onAppEvent(e);
    }

    @Override
    public void onSizeChanged(int width, int height) {
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
    public void onGraphMoved(MotionGraph graph, float progress) {
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
        case UIID.CLOCK:
            Vibrator vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(20);
            break;
        default:
            break;
        }
    }

    private void tryToBack(BackButton back) {
        Chunk prevUnmarked = ChunkManager.getPreviousUnmarkedChunk();
        if (prevUnmarked != null) {
            float offset = ChunkManager.PADDING_OFFSET;
            mMotionGraph.moveGraph(prevUnmarked.mStart > offset ? prevUnmarked.mStart - offset : prevUnmarked.mStart, 0);
            float progress = (float) prevUnmarked.mStart / mMotionGraph.getRightBound();
            mSlideBar.moveSliderBarToProgress(progress);
            ChunkManager.selectChunk(prevUnmarked);
        }

        if (ChunkManager.areAllChunksLabelled()) {
            Reward reward = RewardManager.getReward(getCountOfDayFromStartDate());
            Message msg = mHandler.obtainMessage();
            msg.what = reward != null ? AppCmd.BACK : AppCmd.DONE;
            mHandler.sendMessage(msg);
        }

        Vibrator vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(20);
    }

    private void tryToNext(NextButton next) {
        Chunk nextUnmarked = ChunkManager.getNextUnmarkedChunk();
        if (nextUnmarked != null) {
            float offset = ChunkManager.PADDING_OFFSET;
            mMotionGraph.moveGraph(nextUnmarked.mStart > offset ? nextUnmarked.mStart - offset : nextUnmarked.mStart, 0);
            float progress = (float) nextUnmarked.mStart / mMotionGraph.getRightBound();
            mSlideBar.moveSliderBarToProgress(progress);
            ChunkManager.selectChunk(nextUnmarked);
        }

        if (ChunkManager.areAllChunksLabelled()) {
            Reward reward = RewardManager.getReward(getCountOfDayFromStartDate());
            Message msg = mHandler.obtainMessage();
            msg.what = reward != null ? AppCmd.NEXT : AppCmd.DONE;
            mHandler.sendMessage(msg);
        }

        Vibrator vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(20);
    }

    private void tryToQuest(QuestButton quest) {

        Message msg = mHandler.obtainMessage();
        msg.arg1 = quest.getHost().getChunkRealStartTime();
        msg.arg2 = quest.getHost().getChunkRealStopTime();
        msg.what = AppCmd.QUEST;
        mHandler.sendMessage(msg);

        NoteSender noteSender = new NoteSender(TeensAppManager.getAppContext());
        noteSender.addNote(new Date(), "Start labeling", Globals.NO_PLOT);
        noteSender.send();
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
        if (!actionL.equals(TeensGlobals.UNLABELLED_STRING)) {
            actions.add(actionL);
        }
        if (!actionR.equals(TeensGlobals.UNLABELLED_STRING)) {
            actions.add(actionR);
        }
        // the left and right chunks are both unlabelled or are the same, merge them directly
        if (actions.size() == 0 || actionL.equals(actionR)) {
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
        QuestButton quest = null;

        // During the Handler receiving and handling the message,
        // mLastSelObject might be changed, so it's necessary to
        // handle the class cast exception.
        try {
            quest = (QuestButton) mLastSelObject;
        } catch (ClassCastException e) {
            e.printStackTrace();
            return;
        }

        String actionID = (String) params[0];
        ActionWrap actions = ActionManager.getActions();
        Action action = actions.get(actionID);
        quest.setAnswer(action);
        synchronized (this) {
            mSlideBar.updateUnmarkedRange();
        }

        NoteSender noteSender = new NoteSender(TeensAppManager.getAppContext());
        noteSender.addNote(new Date(), "Stop labeling", Globals.NO_PLOT);
        noteSender.send();

        // Write the changes to the csv file
        Chunk chunk = quest.getHost();
        DataSource.saveLabelLogs(chunk);
    }

    public void finishMerge(Object... params) {
        MergeButton merge = null;

        // During the Handler receiving and handling the message,
        // mLastSelObject might be changed, so it's necessary to
        // handle the class cast exception.
        try {
            merge = (MergeButton) mLastSelObject;
        } catch (ClassCastException e) {
            e.printStackTrace();
            return;
        }

        String selection = (String) params[0];
        Chunk maintain = null;
        ArrayList<Chunk> mChunksToMerge = ChunkManager.getMergingChunks(merge);
        String actionL = mChunksToMerge.get(0).mQuest.getStringAnswer();
        String actionR = mChunksToMerge.get(1).mQuest.getStringAnswer();

        if (actionL.equals(selection)) {
            maintain = mChunksToMerge.get(0);
        } else if (actionR.equals(selection)) {
            maintain = mChunksToMerge.get(1);
        } else { // "None"
            maintain = mChunksToMerge.get(0);
            Action action = ActionManager.getActions().get(TeensGlobals.UNLABELLED_GUID);
            maintain.mQuest.setAnswer(action);
        }
        // it's not called from AppPage.onTouch, so explicit synchronized is necessary
        synchronized (this) {
            ChunkManager.mergeChunk(mChunksToMerge.get(0), mChunksToMerge.get(1), maintain);
            mSlideBar.updateUnmarkedRange();
        }

        DataSource.saveLabelLogs(maintain);
    }

    @Override
    public void onBoundaryScale(float x, float scaleDistance) {
        mMotionGraph.moveGraph(x, 0);
    }

    private int getCountOfDayFromStartDate() {
        String startDate    = DataStorage.getStartDate(getContext(), "");
        String selectedDate = DataSource.getCurrentSelectedDate();

        for (int i = 1; i <= 14; ++i) {
            try {
                Date start = DateHelper.serverDateFormat.parse(startDate);
                Calendar c = Calendar.getInstance();
                c.setTime(start);
                c.add(Calendar.DATE, i - 1);
                // compare the date
                if (DateHelper.serverDateFormat.format(c.getTime()).equals(selectedDate)) {
                    return i;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return -1;
    }
}
