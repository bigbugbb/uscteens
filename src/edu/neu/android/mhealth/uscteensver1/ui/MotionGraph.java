package edu.neu.android.mhealth.uscteensver1.ui;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.Paint.Style;
import android.view.MotionEvent;
import edu.neu.android.mhealth.uscteensver1.R;
import edu.neu.android.mhealth.uscteensver1.data.RawActivity;
import edu.neu.android.mhealth.uscteensver1.data.Chunk;
import edu.neu.android.mhealth.uscteensver1.data.ChunkManager;
import edu.neu.android.mhealth.uscteensver1.data.DataSource;
import edu.neu.android.mhealth.uscteensver1.data.WeekdayCalculator;
import edu.neu.android.mhealth.uscteensver1.pages.AppObject;


public class MotionGraph extends AppObject {
			
	protected int    mStart = 0;  // the virtual pixel offset on the left side of the screen
	protected int    mEnd   = 0;  // the virtual pixel offset on the right side of the screen
	protected int    mCanvasWidth  = 0;
	protected int    mCanvasHeight = 0;
	protected int    mRightBound = 0;
	protected Paint  mBackgroundGray  = null;
	protected Paint  mBackgroundWhite = null;
	protected Paint  mPaint = null;
	protected Paint	 mDataPaint = null;
	protected Paint  mMarkedPaint = null;
	protected Paint  mSelChunkPaint = null;
	protected Paint  mSelChunkBackPaint = null;
	protected Paint  mPaintTxt  = null;
	protected Paint  mPaintDate = null;
	protected int[]  mActions = null;
	protected int    mActLenInPix = 0; // total activity data length in pixel(already scaled)
	protected String mDate = "";
	
	protected float  mOffsetX = 0;
	protected float  mOffsetY = 0;
	protected float  mOffsetSpeedX = 0;
	protected float  mOffsetSpeedY = 0;
	protected float  mAspectRatio  = 1;
	
	protected DataSource   mDataSrc = null;	
		

	public MotionGraph(Resources res) {
		super(res);				
		
		mDataSrc = DataSource.getInstance(null);
		mActions = mDataSrc.getRawActivityData();
		mActLenInPix = mDataSrc.getActivityLengthInPixel();	
		
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
		mPaint.setTypeface(Typeface.SERIF);
		mPaint.setFakeBoldText(false);	
		
		mDataPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mDataPaint.setColor(Color.BLACK);
		mDataPaint.setStrokeWidth(4.0f);
		mDataPaint.setFakeBoldText(false);
		
		mMarkedPaint = new Paint();
		mMarkedPaint.setColor(Color.argb(255, 198, 235, 245));
		mMarkedPaint.setStyle(Style.FILL);	
		
		mSelChunkPaint = new Paint();
		mSelChunkPaint.setColor(Color.argb(255, 255, 128, 0));
		mSelChunkPaint.setStrokeWidth(5.0f);
		mSelChunkPaint.setStyle(Style.STROKE);
		
		mSelChunkBackPaint = new Paint();
		mSelChunkBackPaint.setColor(Color.argb(255, 255, 255, 77));
		mSelChunkBackPaint.setStyle(Style.FILL);
		
		mPaintTxt = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaintTxt.setColor(Color.BLACK);
		mPaintTxt.setStyle(Style.STROKE);
		mPaintTxt.setTextSize(sAppScale.doScaleT(38));
		mPaintTxt.setFakeBoldText(false);
		
		mPaintDate = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaintDate.setColor(Color.BLACK);
		mPaintDate.setStyle(Style.STROKE);
		mPaintDate.setTypeface(Typeface.SERIF);
		mPaintDate.setTextSize(sAppScale.doScaleT(43));
		mPaintDate.setFakeBoldText(false);
		
		mOffsetSpeedX = 0;
		mOffsetSpeedY = 0;
		
		mDate = convertDateToDisplayFormat(mDataSrc.getCurSelectedDate());
			
		ChunkManager.setDisplayOffset(0, 0);	
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
	
	private String convertDateToDisplayFormat(String date) {
		String[] MONTHS = {
			"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JULY", "AUG", "SEPT", "OCT", "NOV", "DEC"			
		};
		
		String[] times = date.split("-");
		String weekday = WeekdayCalculator.getWeekday(date);
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
		//c.drawRect(0, 0, mWidth, mHeight, mBackgroundGray);
		c.drawRect(mOffsetX, 0, mWidth + mOffsetX, mHeight, mBackgroundWhite);
		// draw the border
		c.drawLine(0, 0, mWidth, 0, mPaint);
		c.drawLine(0, 0, 0, mHeight, mPaint);
		c.drawLine(mWidth, 0, mWidth, mHeight, mPaint);
		c.drawLine(0, mHeight, mWidth, mHeight, mPaint);
		
		if (Math.abs((int) mSpeedX) > 0) {
			int offset = (int) mSpeedX;
			if (mAccSpeedX < 0) {
				mSpeedX = Math.max(0, mSpeedX + mAccSpeedX);
			} else {
				mSpeedX = Math.min(0, mSpeedX + mAccSpeedX);
			}
				
			if (mStart - (int) offset < 0) {
				offset = mStart;
			} else if (mStart - (int) offset > mRightBound) {
				offset = mStart - mRightBound;
			}
			mStart = (mStart - offset < 0) ? 0 : mStart - offset;				
			mEnd   = mStart + (int) mWidth;			
			mEnd   = (mEnd > mActLenInPix) ? mActLenInPix : mEnd;
			ChunkManager.setDisplayOffset(-mStart, 0);
			
			if (mListener != null) {
				mListener.OnGraphMoved(this, (float) mStart / mRightBound);
			}
		}				
		
		// draw the marked region
		for (int i = 0; i < ChunkManager.getChunkSize(); ++i) {
			Chunk chunk = ChunkManager.getChunk(i);
			// get the right region to draw, clip the part out of screen
			if (chunk.mStart - mStart > mWidth || chunk.mStop - mStart < 0)
				continue;
			RectF r = new RectF(chunk.mStart - mStart + mOffsetX, 0,
					chunk.mStop - mStart + mOffsetX, mHeight);
			// check the selection
			if (chunk.isSelected()) {
				c.drawRect(r, mSelChunkBackPaint);
			} else if (chunk.mQuest.isAnswered()) {								
				c.drawRect(r, mMarkedPaint);
			}
		}
		
		// draw the graph
		float scale = (float) mHeight / mDataSrc.getMaxActivityValue();
		for (int i = mStart; i < mEnd - DataSource.PIXEL_SCALE; ++i) {	
			int index = i / DataSource.PIXEL_SCALE;		
			float x1 = i - mStart + mOffsetX;
			float y1 = mHeight - mActions[index] * scale;
			float x2 = i - mStart + DataSource.PIXEL_SCALE + mOffsetX;
			float y2 = mHeight - mActions[index + 1] * scale;
			if (mActions[index] >= 0 && mActions[index + 1] >= 0)
				c.drawLine(x1, y1, x2, y2, mDataPaint);
		}		
		// draw the chunk lines and the corresponding buttons
		for (int i = 0; i < ChunkManager.getChunkSize(); ++i) {
			Chunk chunk = ChunkManager.getChunk(i);
			chunk.onDraw(c);
		}
		// draw the time interval corresponding to the displayed region
		String timeStart = toStringTimeFromPosition(mStart);
		String timeEnd   = toStringTimeFromPosition(mEnd);
		mPaintTxt.setTextAlign(Paint.Align.LEFT);
		c.drawText(timeStart, sAppScale.doScaleW(20), mHeight + sAppScale.doScaleH(36), mPaintTxt);
		mPaintTxt.setTextAlign(Paint.Align.RIGHT);
		c.drawText(timeEnd, mWidth + sAppScale.doScaleW(-20), mHeight + sAppScale.doScaleH(36), mPaintTxt);
		// draw date on the bottom
		mPaintDate.setTextAlign(Paint.Align.CENTER);
		c.drawText(mDate, mWidth / 2, mHeight + sAppScale.doScaleH(200), mPaintDate);
		// draw the rectangle which indicates the chunk selection		
		c.drawRect(ChunkManager.getSelectedArea(), mSelChunkPaint);		
	}

	private String toStringTimeFromPosition(int position) {
		int hour   = position / 3600 / DataSource.PIXEL_SCALE;
		int minute = (position - hour * 3600 * DataSource.PIXEL_SCALE) / 60 / DataSource.PIXEL_SCALE; 
		String time = "" + hour + ":" + (minute > 9 ? minute : "0" + minute);
		
		return time; 
	}
	
	@Override
	public void onSizeChanged(int width, int height) {				
		// get the region size
		mWidth  = width;
		mHeight = height - (int)(width * mAspectRatio);
		mCanvasWidth  = width;
		mCanvasHeight = height;

		for (Chunk chunk : ChunkManager.getChunks()) {
			chunk.setHeight(mHeight);
		}
		
		// ....
		mStart = 0;
		mEnd   = mStart + (int)mWidth;
		mEnd   = (mEnd > mActLenInPix) ? mActLenInPix : mEnd;	
		mRightBound = mActLenInPix - (int) mWidth;
		
		ChunkManager.setViewSize(mWidth, mHeight);
		ChunkManager.setCanvasSize(width, height);
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
		if (ChunkManager.selectChunk(e.getX(), e.getY())) {
			return true;
		}

		return false;
	}

	@Override
	public boolean onUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return super.onUp(e);
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		mSpeedX = velocityX / sAppScale.doScaleW(50);
		mAccSpeedX = sAppScale.doScaleW(mSpeedX > 0 ? -3 : 3);
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
		mEnd   = (mEnd > mActLenInPix) ? mActLenInPix : mEnd;
		ChunkManager.setDisplayOffset(-mStart + mOffsetX, 0);
		
		if (mListener != null) {
			mListener.OnGraphMoved(this, (float) mStart / mRightBound);
		}
		
		return true;
	}
	
	public void moveGraph(float x, float y) {
		if (Math.abs((int) mSpeedX) > 0) {
			mSpeedX = 0;
		}
		mStart = (int) ((x > mActLenInPix - mWidth) ? mActLenInPix - mWidth : x);
		mEnd   = mStart + (int) mWidth;
		mEnd   = (mEnd > mActLenInPix) ? mActLenInPix : mEnd;
		
		ChunkManager.setDisplayOffset(-mStart, 0);
	}
	
	public void moveGraph(int progress) {
		if (Math.abs((int) mSpeedX) > 0) {
			mSpeedX = 0;
		}
		int x = (int) ((mActLenInPix - (int) mWidth) * (progress / 100f));
		mStart = (x < 0) ? 0 : x;
		mEnd   = mStart + (int) mWidth;	
		mEnd   = (mEnd > mActLenInPix) ? mActLenInPix : mEnd;
		
		ChunkManager.setDisplayOffset(-mStart, 0);
	}

	@Override
	public boolean contains(float x, float y) {
		return (mX < x && x <= mX + mWidth) && 
			(mY < y && y <= mY + mHeight + mCanvasHeight * 0.25);
	}			
	
	protected OnGraphMovedListener mListener = null;
	
	public void setOnGraphMovedListener(OnGraphMovedListener listener) {
		mListener = listener;
	}
	
	public interface OnGraphMovedListener {
		void OnGraphMoved(MotionGraph graph, float progress);
	}
}
