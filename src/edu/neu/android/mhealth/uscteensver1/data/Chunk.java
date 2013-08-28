package edu.neu.android.mhealth.uscteensver1.data;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import edu.neu.android.mhealth.uscteensver1.TeensGlobals;
import edu.neu.android.mhealth.uscteensver1.extra.Action;
import edu.neu.android.mhealth.uscteensver1.pages.AppObject;
import edu.neu.android.mhealth.uscteensver1.pages.AppScale;
import edu.neu.android.mhealth.uscteensver1.pages.GraphPage;
import edu.neu.android.mhealth.uscteensver1.ui.ClockButton;
import edu.neu.android.mhealth.uscteensver1.ui.MergeButton;
import edu.neu.android.mhealth.uscteensver1.ui.QuestButton;
import edu.neu.android.mhealth.uscteensver1.ui.SplitButton;
import edu.neu.android.wocketslib.Globals;

// chunk data mapping to the motion graph
public class Chunk extends AppObject {

    public final static int MINIMUM_CHUNK_SPACE = 240;

    public int mStart;  // in pixel, has been scaled by DataSource.PIXEL_SCALE
    public int mStop;   // in pixel, has been scaled by DataSource.PIXEL_SCALE
    public int mOffset; // plus to reconstruct the real world value, the offset has not been scaled
    
    public GraphPage   mParent;
    public QuestButton mQuest;
    public ClockButton mClock;
    public MergeButton mMerge;
    public SplitButton mSplit;

    private Action mAction;

    private String mCreateTime;
    private String mModifyTime;

    public float mDispOffsetX;
    public float mDispOffsetY;

    private static Paint sPaint;
    private static boolean sPaintCreated = false;

    private static StringBuilder sStringBuilder = new StringBuilder();

    protected static void createPaint() {
        if (!sPaintCreated) {
            sPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            sPaint.setColor(Color.BLACK);
            sPaint.setStyle(Style.FILL);
            sPaintCreated = true;
        }
    }

    public Chunk(Resources res, Action action) {
        super(res);
        mKind   = CHUNK;
        mZOrder = ZOrders.CHUNK;

        mAction = action;
        mParent = (GraphPage) ChunkManager.getUserData();
        mQuest  = new QuestButton(res, this, mParent);
        mClock  = new ClockButton(res, this, mParent);
        mMerge  = new MergeButton(res, this, mParent);
        mSplit  = new SplitButton(res, this, mParent);

        mCreateTime = Globals.mHealthTimestampFormat.format(new Date());

        List<AppObject> objects = mParent.getObjectList();
        objects.add(mQuest);
        objects.add(mClock);
        objects.add(mMerge);
        objects.add(mSplit);

        createPaint();
    }

    public RawChunk toRawChunk() {
        String startDate = toDateTime(mStart / TeensGlobals.PIXEL_PER_DATA + mOffset);
        String stopDate = toDateTime(mStop / TeensGlobals.PIXEL_PER_DATA + mOffset);
        RawChunk rawChunk = new RawChunk(startDate, stopDate, mAction, mCreateTime, mModifyTime);
        return rawChunk;
    }

    private String toDateTime(int time) {
        StringBuilder sb = new StringBuilder();
        sb.append(DataSource.getCurrentSelectedDate());   

        int hour = time / 3600;
        sb.append(hour > 9 ? " " : " 0");
        sb.append(hour);
        time -= hour * 3600;
        int minute = time / 60;
        sb.append(minute > 9 ? ":" : ":0");
        sb.append(minute);
        int second = time - minute * 60;
        sb.append(second > 9 ? ":" : ":0");
        sb.append(second);
        // ignore the millisecond
        sb.append(".000");

        return sb.toString();
    }

    public boolean isLastChunkOfToday() {
        String curDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String startDate = toDateTime(mStart / TeensGlobals.PIXEL_PER_DATA).substring(0, 10);

        // is today
        if (curDate.equals(startDate)) {
            // is the last chunk
            if (Math.abs(mStop - TeensGlobals.DAILY_LAST_SECOND * TeensGlobals.PIXEL_PER_DATA) <
                    TeensGlobals.PIXEL_PER_DATA) {
                return true;
            }
        }

        return false;
    }

    public void setAction(Action action) {
        mAction = action;
        updateModifyTime();
    }

    public Action getAction() {
        return mAction;
    }

    public int getChunkWidth() {
        assert (mStop - mStart >= MINIMUM_CHUNK_SPACE);
        return mStop - mStart;
    }

    public int getChunkRealStartTime() {
        return mStart / TeensGlobals.PIXEL_PER_DATA + mOffset;
    }

    public int getChunkRealStopTime() {
        return mStop / TeensGlobals.PIXEL_PER_DATA + mOffset;
    }

    public String getChunkRealStartTimeInString() {
        int time   = mStart / TeensGlobals.PIXEL_PER_DATA + mOffset;
        int hour   = time / 3600;
        int minute = (time - 3600 * hour) / 60;

        sStringBuilder.delete(0, sStringBuilder.length());
        sStringBuilder.append(hour > 12 ? hour - 12 : hour == 0 ? 12 : hour);
        sStringBuilder.append(":");
        sStringBuilder.append(minute > 9 ? minute : "0" + minute);
        sStringBuilder.append(hour > 11 ? " PM" : " AM");

        return sStringBuilder.toString();
    }

    public void release() {
        super.release();

        mAction = null;

        mQuest.release();
        mClock.release();
        mMerge.release();
        mSplit.release();

        mParent.getObjectList().remove(mQuest);
        mParent.getObjectList().remove(mClock);
        mParent.getObjectList().remove(mMerge);
        mParent.getObjectList().remove(mSplit);
    }

    public boolean load(int start, int stop, int offset, String createTime, String modifyTime) {
        boolean result = update(start, stop, offset);
        // put it here because the methods above may update the modify time
        mCreateTime = createTime;
        mModifyTime = modifyTime;
        return result;
    }

    public boolean update(int start, int stop, int offset) {
        // next must be bigger than the current
        if (stop - start < AppScale.doScaleW(MINIMUM_CHUNK_SPACE)) {
            return false; // just ignore, because the space for one chunk will be too small
        }

        if (start != mStart || stop != mStop) {
            updateModifyTime();
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

        if (isLastChunkOfToday()) {
            mQuest.setVisible(false);
        }

        return true;
    }

    public void updateModifyTime() {
        mModifyTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
    }

    @Override
    public boolean contains(float x, float y) {
        return x >= mStart + mDispOffsetX + AppScale.doScaleW(60) &&
               x <= mStop + mDispOffsetX - AppScale.doScaleW(60);
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
        if (mStart + mDispOffsetX < 0 || mStart + mDispOffsetX > ChunkManager.getViewWidth()) {
            return;
        }
        c.drawLine(mStart + mDispOffsetX, 0, mStart + mDispOffsetX, mHeight, sPaint);

        if (TeensGlobals.IS_CALIBRATION_ENABLED) {
            float space = mHeight / 10;
            float h = space;
            for (int i = 1; i < 10; ++i) {
                c.drawLine(mStart + mDispOffsetX, h, mStart + mDispOffsetX + space / 4, h, sPaint);
                h += space;
            }
        }
    }
}
