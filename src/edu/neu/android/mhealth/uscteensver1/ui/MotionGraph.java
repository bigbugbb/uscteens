package edu.neu.android.mhealth.uscteensver1.ui;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.Pair;
import android.view.MotionEvent;
import edu.neu.android.mhealth.uscteensver1.R;
import edu.neu.android.mhealth.uscteensver1.TeensAppManager;
import edu.neu.android.mhealth.uscteensver1.TeensGlobals;
import edu.neu.android.mhealth.uscteensver1.data.Chunk;
import edu.neu.android.mhealth.uscteensver1.data.ChunkManager;
import edu.neu.android.mhealth.uscteensver1.data.DataSource;
import edu.neu.android.mhealth.uscteensver1.data.Label;
import edu.neu.android.mhealth.uscteensver1.data.LabelManager;
import edu.neu.android.mhealth.uscteensver1.pages.AppObject;
import edu.neu.android.mhealth.uscteensver1.pages.AppScale;
import edu.neu.android.wocketslib.utils.WeekdayHelper;


public class MotionGraph extends AppObject {

    protected int   mStart;  // the virtual pixel offset on the left side of the screen
    protected int   mEnd;    // the virtual pixel offset on the right side of the screen
    protected int   mCanvasWidth;
    protected int   mCanvasHeight;
    protected int   mRightBound;
    
    protected Paint mBackgroundGray;
    protected Paint mBackgroundWhite;
    protected Paint mPaint;
    protected Paint mDataPaint;
    protected Paint mSlashPaint;
    protected Paint mMarkedPaint;
    protected Paint mSelChunkPaint;
    protected Paint mSelChunkBackPaint;
    protected Paint mPaintTxt;
    protected Paint mPaintDate;
    
    protected RectF   mSelectedRegion;
    protected float[] mPTS;
    protected int[]   mScaledData;
    protected int     mDataLengthInPixel; // total activity data length in pixel(already scaled)
    protected String  mDate;

    protected float mOffsetSpeedX;
    protected float mOffsetSpeedY;
    protected float mAspectRatio;

    public MotionGraph(Resources res) {
        super(res);
        
        mStart = 0;
        mEnd   = 0;    // the virtual pixel offset on the right side of the screen
        mCanvasWidth  = 0;
        mCanvasHeight = 0;
        mRightBound   = 0;
        
        mOffsetSpeedX = 0;
        mOffsetSpeedY = 0;        
        mSelectedRegion = new RectF();
        mDate = convertDateToDisplayFormat(DataSource.getCurrentSelectedDate());  

        mDataLengthInPixel = DataSource.getDrawableDataLengthInPixel();
//		mScaledData = Arrays.copyOf(DataSource.getDrawableData(), mDataLengthInPixel);
        mScaledData = DataSource.getDrawableData().clone();
        mPTS = null;

        loadImages(new int[]{ R.drawable.menubar_background });
        mAspectRatio = mImages.get(0).getHeight() / (float) mImages.get(0).getWidth();
        mImages.get(0).recycle();
        mImages.remove(0);

        mBackgroundGray = new Paint();
        mBackgroundGray.setColor(Color.rgb(179, 181, 181));
        mBackgroundGray.setStyle(Style.FILL);
        
        mBackgroundWhite = new Paint();
        mBackgroundWhite.setColor(Color.rgb(255, 255, 255));
        mBackgroundWhite.setStyle(Style.FILL);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Style.FILL);
        mPaint.setFakeBoldText(false);

        mDataPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDataPaint.setColor(Color.BLACK);
        mDataPaint.setStrokeWidth(Math.max(1.0f, Math.min(AppScale.doScaleT(4.0f), 4.0f)));
        mDataPaint.setFakeBoldText(false);

        mSlashPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSlashPaint.setColor(Color.GRAY);
        mSlashPaint.setStrokeWidth(1.0f);

        mMarkedPaint = new Paint();
        mMarkedPaint.setColor(Color.argb(255, 198, 235, 245));
        mMarkedPaint.setStyle(Style.FILL);

        mSelChunkPaint = new Paint();
        mSelChunkPaint.setColor(Color.argb(255, 255, 128, 0));
        mSelChunkPaint.setStrokeWidth(AppScale.doScaleW(4.0f));
        mSelChunkPaint.setStyle(Style.STROKE);

        mSelChunkBackPaint = new Paint();
        mSelChunkBackPaint.setColor(Color.argb(255, 255, 255, 102));
        mSelChunkBackPaint.setStyle(Style.FILL);

        mPaintTxt = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintTxt.setColor(Color.BLACK);
        mPaintTxt.setStyle(Style.STROKE);
        mPaintTxt.setTypeface(Typeface.createFromAsset(TeensAppManager.getAppAssets(), "font/arial_bold.ttf"));
        mPaintTxt.setTextSize(AppScale.doScaleT(36));

        mPaintDate = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintDate.setColor(Color.BLACK);
        mPaintDate.setStyle(Style.STROKE);
        mPaintDate.setTypeface(Typeface.createFromAsset(TeensAppManager.getAppAssets(), "font/arial.ttf"));
        mPaintDate.setTextSize(AppScale.doScaleT(38));
        mPaintDate.setFakeBoldText(false);                     
        	
        ChunkManager.setDisplayOffset(0, 0);
        LabelManager.setDisplayOffset(0, 0);
    }

    public void release() {
        for (Bitmap image : mImages) {
            if (image != null) {
                image.recycle();
                image = null;
            }
        }
        mImageLoaded = false;
        mImages.clear();
    }

    @SuppressLint("DefaultLocale")
    private String convertDateToDisplayFormat(String date) {
        String[] MONTHS = {
            "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JULY", "AUG", "SEPT", "OCT", "NOV", "DEC"
        };

        String[] times = date.split("-");
        String weekday = WeekdayHelper.getWeekday(date);
        String month   = MONTHS[Integer.parseInt(times[1]) - 1];
        String day     = times[2];
        String formatted = weekday.toUpperCase() + "  " + month + "  " + day;

        return formatted;
    }

    public int getRightBound() {
        return mRightBound;
    }

    @Override
    public void onDraw(Canvas c) {
        // draw the background with white color
        c.drawRect(0, 0, mWidth, mHeight, mBackgroundWhite);

        // draw the border
        c.drawLine(0, 0, mWidth, 0, mPaint);
        c.drawLine(0, 0, 0, mHeight, mPaint);
        c.drawLine(mWidth, 0, mWidth, mHeight, mPaint);
        c.drawLine(0, mHeight, mWidth, mHeight, mPaint);

        // respond to the scroll
        if (Math.abs((int) mSpeedX) > 0) {
            int offset = (int) mSpeedX;
            if (mAccSpeedX < 0) {
                mSpeedX = Math.max(0, mSpeedX + mAccSpeedX);
            } else {
                mSpeedX = Math.min(0, mSpeedX + mAccSpeedX);
            }

            if (mStart - offset < 0) {
                offset = mStart;
            } else if (mStart - offset > mRightBound) {
                offset = mStart - mRightBound;
            }
            mStart = (mStart - offset < 0) ? 0 : mStart - offset;
            mEnd = mStart + (int) mWidth;
            mEnd = (mEnd > mDataLengthInPixel) ? mDataLengthInPixel : mEnd;
            ChunkManager.setDisplayOffset(-mStart, 0);
            LabelManager.setDisplayOffset(-mStart, 0);

            if (mListener != null) {
                mListener.onGraphMoved(this, (float) mStart / mRightBound);
            }
        }

        // draw the marked region
        for (int i = 0; i < ChunkManager.getChunkSize(); ++i) {
            Chunk chunk = ChunkManager.getChunk(i);
            // get the right region to draw, clip the part out of screen
            if (chunk.mStart - mStart > mWidth || chunk.mStop - mStart < 0)
                continue;
            mSelectedRegion.left = chunk.mStart - mStart;
            mSelectedRegion.top = 0;
            mSelectedRegion.right = chunk.mStop - mStart;
            mSelectedRegion.bottom = mHeight;
            // check the selection
            if (chunk.isSelected()) {
                c.drawRect(mSelectedRegion, mSelChunkBackPaint);
            } else if (chunk.mQuest.isAnswered()) {
                c.drawRect(mSelectedRegion, mMarkedPaint);
            }
        }

        // draw the hint labels if they do exist
        ArrayList<Label> labels = LabelManager.getLabels();
        for (Label label : labels) {
            label.onDraw(c);
        }

		/*
		 *  draw the motion data
		 */
        int count = 0;
        for (int i = mStart; i < mEnd - TeensGlobals.PIXEL_PER_DATA; ++i) {
            int sec = i / TeensGlobals.PIXEL_PER_DATA;
            if (mScaledData[sec] >= 0 && mScaledData[sec + 1] >= 0) {
                // get all points' positions for drawing the data with lines
                mPTS[(count << 2) + 0] = i - mStart;
                mPTS[(count << 2) + 1] = mHeight - mScaledData[sec];
                mPTS[(count << 2) + 2] = i - mStart + TeensGlobals.PIXEL_PER_DATA;
                mPTS[(count << 2) + 3] = mHeight - mScaledData[sec + 1];
                ++count;
            } else if (mScaledData[sec] < 0) {
                int start = 0, stop = 0, delta = 0;
                // find the period that should be drawn
                for (Pair<Integer, Integer> pair : DataSource.getNoDataTimePeriods()) {
                    if (pair.first <= sec && sec < pair.second) {
                        start = pair.first * TeensGlobals.PIXEL_PER_DATA;
                        stop = pair.second * TeensGlobals.PIXEL_PER_DATA;
                        delta = stop - start;
                    }
                }
                // draw slashes with 45 degree for periods without data
                float slashX1 = start - mStart;
                float slashX2 = stop - mStart - TeensGlobals.PIXEL_PER_DATA;
                int step = (int) AppScale.doScaleH(12.0f);
                // draw slashes from top to bottom
                for (int m = (int) mHeight; m > 0; m -= step) {
                    c.drawLine(slashX1, m, slashX2, m - (slashX2 - slashX1), mSlashPaint);
                }
                // draw slashes from left to right
                for (int m = 0; m < delta; m += step) {
                    if (slashX1 + m < -mHeight || slashX1 + m > mWidth) {
                        continue;
                    }
                    c.drawLine(slashX1 + m, mHeight, slashX2, mHeight - (slashX2 - (slashX1 + m)), mSlashPaint);
                }
                // skip the space that has been drawn
                i += delta - (start >= mStart ? 0 : i - start) - 1;
            }
        }
        c.drawLines(mPTS, 0, count << 2, mDataPaint);

        // draw the chunk lines and the corresponding buttons
        for (int i = 0; i < ChunkManager.getChunkSize(); ++i) {
            Chunk chunk = ChunkManager.getChunk(i);
            chunk.onDraw(c);
        }

        // draw the rectangle which indicates the chunk selection
        c.drawRect(ChunkManager.getSelectedArea(), mSelChunkPaint);

        // draw the time interval corresponding to the displayed region
        String timeStart = toStringTimeFromPosition(mStart);
        String timeEnd = toStringTimeFromPosition(mEnd);
        mPaintTxt.setTextAlign(Paint.Align.LEFT);
        c.drawText(timeStart, AppScale.doScaleW(20), mHeight + AppScale.doScaleH(36), mPaintTxt);
        mPaintTxt.setTextAlign(Paint.Align.RIGHT);
        c.drawText(timeEnd, mWidth + AppScale.doScaleW(-20), mHeight + AppScale.doScaleH(36), mPaintTxt);

        // draw date on the bottom
        mPaintDate.setTextAlign(Paint.Align.CENTER);
        c.drawText(mDate, mWidth / 2, mHeight + AppScale.doScaleH(200), mPaintDate);
    }

    private String toStringTimeFromPosition(int position) {
        int hour = position / 3600 / TeensGlobals.PIXEL_PER_DATA;
        int minute = (position - hour * 3600 * TeensGlobals.PIXEL_PER_DATA) / 60 / TeensGlobals.PIXEL_PER_DATA;

        StringBuilder sb = new StringBuilder();
        sb.append(hour > 12 ? hour - 12 : hour == 0 ? 12 : hour);
        sb.append(":");
        sb.append(minute > 9 ? minute : "0" + minute);
        sb.append(hour > 11 ? " PM" : " AM");

        return sb.toString();
    }

    @Override
    public void onSizeChanged(int width, int height) {
        if (mCanvasWidth == width && mCanvasHeight == height) {
            return;
        }
        mCanvasWidth  = width;
        mCanvasHeight = height;
        // get the region size
        mWidth  = width;
        mHeight = height - (int) (width * mAspectRatio);

        // for drawing lines
        mPTS = new float[width * 4];

        // scale the data for drawing in the specified area
        float scale = mHeight / DataSource.getMaxDrawableDataValue();
        for (int i = 0; i < mScaledData.length; ++i) {
            if (mScaledData[i] >= 0) {
                mScaledData[i] *= scale;
                if (mScaledData[i] > mHeight) {
                    mScaledData[i] = (int) mHeight - 4;
                }
                mScaledData[i] += Math.max(1.0f, Math.min(AppScale.doScaleT(4.0f), 4.0f)) / 2;
            }
        }

        for (Chunk chunk : ChunkManager.getChunks()) {
            chunk.setHeight(mHeight);
        }

        // ....
        mStart = 0;
        mEnd   = mStart + (int) mWidth;
        mEnd   = mEnd > mDataLengthInPixel ? mDataLengthInPixel : mEnd;
        mRightBound = mDataLengthInPixel - (int) mWidth;

        ChunkManager.setViewSize(mWidth, mHeight);
        ChunkManager.setCanvasSize(width, height);

        LabelManager.setViewSize(mWidth, mHeight);
        LabelManager.setCanvasSize(width, height);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        if (Math.abs((int) mSpeedX) > 0) {
            mSpeedX = 0;
        }

        // find the chunk according to the input x and y
        if (e.getY() > mHeight) {
            return false;
        }

        // try to select chunk
        return ChunkManager.selectChunk(e.getX(), e.getY());
    }

    @Override
    public boolean onUp(MotionEvent e) {
        return super.onUp(e);
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {
        mSpeedX    = velocityX / AppScale.doScaleW(50);
        mAccSpeedX = AppScale.doScaleW(mSpeedX > 0 ? -3 : 3);
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
        int offset = (int) distanceX;
        //mOffsetX = -offset;

        if (mStart + (int) distanceX < 0) {
            offset = -mStart;
        } else if (mStart + (int) distanceX > mRightBound) {
            offset = mRightBound - mStart;
        }
        mStart = mStart + offset;
        mEnd   = mStart + (int) mWidth;
        mStart = (mStart < 0) ? 0 : mStart;
        mEnd   = (mEnd > mDataLengthInPixel) ? mDataLengthInPixel : mEnd;
        ChunkManager.setDisplayOffset(-mStart, 0);
        LabelManager.setDisplayOffset(-mStart, 0);

        if (mListener != null) {
            mListener.onGraphMoved(this, (float) mStart / mRightBound);
        }

        return true;
    }

    public void moveGraph(float x, float y) {
        if (Math.abs((int) mSpeedX) > 0) {
            mSpeedX = 0;
        }
        mStart = (int) ((x > mDataLengthInPixel - mWidth) ? mDataLengthInPixel - mWidth : x);
        mEnd   = mStart + (int) mWidth;
        mEnd   = (mEnd > mDataLengthInPixel) ? mDataLengthInPixel : mEnd;

        ChunkManager.setDisplayOffset(-mStart, 0);
        LabelManager.setDisplayOffset(-mStart, 0);
    }

    public void moveGraph(int progress) {
        if (Math.abs((int) mSpeedX) > 0) {
            mSpeedX = 0;
        }
        int x = (int) ((mDataLengthInPixel - (int) mWidth) * (progress / 100f));
        mStart = (x < 0) ? 0 : x;
        mEnd = mStart + (int) mWidth;
        mEnd = (mEnd > mDataLengthInPixel) ? mDataLengthInPixel : mEnd;

        ChunkManager.setDisplayOffset(-mStart, 0);
        LabelManager.setDisplayOffset(-mStart, 0);
    }

    @Override
    public boolean contains(float x, float y) {
        return (mX < x && x <= mX + mWidth) &&
               (mY < y && y <= mY + mHeight + mCanvasHeight * 0.25f);
    }

    protected OnGraphMovedListener mListener = null;

    public void setOnGraphMovedListener(OnGraphMovedListener listener) {
        mListener = listener;
    }

    public interface OnGraphMovedListener {
        void onGraphMoved(MotionGraph graph, float progress);
    }
}
