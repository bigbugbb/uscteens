package edu.neu.android.mhealth.uscteensver1.ui;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.Paint.Style;
import android.view.MotionEvent;
import edu.neu.android.mhealth.uscteensver1.AppObject;
import edu.neu.android.mhealth.uscteensver1.R;
import edu.neu.android.mhealth.uscteensver1.data.Chunk;
import edu.neu.android.mhealth.uscteensver1.data.ChunkManager;


public class MotionGraph extends AppObject {
		
	protected int[] mData = null;
	protected ChunkManager mManager = null;
	protected int   mStart = 0;
	protected int   mEnd   = 0;
	protected int   mCanvasWidth  = 0;
	protected int   mCanvasHeight = 0;
	protected int   mRightBound = 0;
	protected Paint mPaint = null;
	protected Paint mAnswerPaint = null;
	protected Paint mSelChunkPaint = null;
	protected int[] mActions = null;
	protected int[] mActionsScaled = null;
	protected int   mLastAction = 0;
	protected OnGraphMovedListener mListener = null;
	
	protected static final float ACTIVITY_DATA_LIMIT = 256f;
	
	public interface OnGraphMovedListener {
		void OnGraphMoved(MotionGraph graph, float progress);
	}

	public MotionGraph(Resources res, ChunkManager manager) {
		super(res);				
		mManager = manager;
		loadImages(new int[]{ R.drawable.menubar_background });								
		
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setColor(Color.BLACK);
		mPaint.setStyle(Style.FILL);
		mPaint.setTypeface(Typeface.SERIF);
		mPaint.setFakeBoldText(false);	
		
		mAnswerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mAnswerPaint.setColor(Color.argb(255, 198, 235, 245));
		mAnswerPaint.setStyle(Style.FILL);	
		
		mSelChunkPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mSelChunkPaint.setColor(Color.argb(255, 0, 183, 223));
		mSelChunkPaint.setStrokeWidth(5.0f);
		mSelChunkPaint.setStyle(Style.STROKE);
		mSelChunkPaint.setFakeBoldText(true);	
			
	}
	
	public void setActionData(int[] data) {
		mActions = data;	
		mActionsScaled = new int[mActions.length];
	}
	
	public void setOnGraphMovedListener(OnGraphMovedListener listener) {
		mListener = listener;
	}		
	
	public int getRightBound() {
		return mRightBound;
	}

	@Override
	public void onDraw(Canvas c) {
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
			mEnd   = (mEnd > mActions.length) ? mActions.length : mEnd;
			mManager.setDisplayOffset(-mStart, 0);			
			if (mListener != null) {
				mListener.OnGraphMoved(this, (float) mStart / mRightBound);
			}
		}				
		
		// draw the answered region
		for (int i = 0; i < mManager.getChunkSize(); ++i) {
			Chunk chunk = mManager.getChunk(i);
			if (chunk.mQuest.isAnswered()) {
				if (chunk.mValue - mStart > mWidth || chunk.mNext - mStart < 0)
					continue;
				RectF r = new RectF(chunk.mValue - mStart, 0, chunk.mNext - mStart, mHeight);		
				c.drawRect(r, mAnswerPaint);
			}
		}
		
		// draw the graph		
		for (int i = mStart; i < mEnd - 1; ++i) {
			//c.drawPoint(i - mStart, mActionsScaled[i], mPaint);
			c.drawLine(i - mStart, mActionsScaled[i], i - mStart + 1, mActionsScaled[i + 1], mPaint);
		}
		// draw the chunk lines and the corresponding buttons
		for (int i = 0; i < mManager.getChunkSize(); ++i) {
			Chunk chunk = mManager.getChunk(i);
			chunk.onDraw(c);
		}		
		// draw the rectangle which indicates the chunk selection		
		c.drawRect(mManager.getSelectedArea(), mSelChunkPaint);
	}

	@Override
	public void onSizeChanged(int width, int height) {
		float radio = mImages.get(0).getHeight() / (float) mImages.get(0).getWidth();				
		mWidth  = width;
		mHeight = height - (int)(width * radio);
		mCanvasWidth  = width;
		mCanvasHeight = height;
		
		for (Chunk chunk : mManager.mChunks) {
			chunk.setHeight(mHeight);
		}
		
		// ....
		mStart = 0;
		mEnd   = mStart + (int)mWidth;
		mEnd   = (mEnd > mActions.length) ? mActions.length : mEnd;
		for (int i = 0; i < mActions.length; ++i) {
			mActionsScaled[i] = (int) (mHeight * (mActions[i] / ACTIVITY_DATA_LIMIT));
		}		
		mRightBound = mActions.length - (int) mWidth;
		
		mManager.setViewSize(mWidth, mHeight);
		mManager.setCanvasSize(width, height);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		mLastAction = e.getAction();
		
		if (Math.abs((int) mSpeedX) > 0) {
			mSpeedX = 0;
		}
		
		// find the chunk according to the input x and y
		if (e.getY() > mHeight) { 
			return false;
		}
		
		// try to select chunk
		if (mManager.selectChunk(e.getX(), e.getY())) {
			return true;
		}

		return false;
	}

	@Override
	public boolean onUp(MotionEvent e) {
		mLastAction = e.getAction();
		// TODO Auto-generated method stub
		return super.onUp(e);
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		mSpeedX = velocityX / 50;
		mAccSpeedX = mSpeedX > 0 ? -3 : 3;
		mLastAction = e2.getAction();
		return true;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		int offset = (int) distanceX;		
		
		if (mStart + (int) distanceX < 0) {
			offset = -mStart;
		} else if (mStart + (int) distanceX > mRightBound) {
			offset = mRightBound - mStart;
		}
		mStart = mStart + offset;				
		mEnd   = mStart + (int) mWidth;
		mStart = (mStart < 0) ? 0 : mStart;
		mEnd   = (mEnd > mActions.length) ? mActions.length : mEnd;
		mManager.setDisplayOffset(-mStart, 0);
		
		if (mListener != null) {
			mListener.OnGraphMoved(this, (float) mStart / mRightBound);
		}
		mLastAction = e2.getAction();
		
		return true;
	}
	
	public void moveGraph(float x, float y) {
		if (Math.abs((int) mSpeedX) > 0) {
			mSpeedX = 0;
		}
		mStart = (int) x;
		mEnd   = mStart + (int) mWidth;
		mEnd   = (mEnd > mActions.length) ? mActions.length : mEnd;
		
		mManager.setDisplayOffset(-mStart, 0);
	}
	
	public void moveGraph(int progress) {
		if (Math.abs((int) mSpeedX) > 0) {
			mSpeedX = 0;
		}
		int x = (int) ((mActions.length - (int) mWidth) * (progress / 100f));
		mStart = (x < 0) ? 0 : x;
		mEnd   = mStart + (int) mWidth;	
		mEnd   = (mEnd > mActions.length) ? mActions.length : mEnd;
		
		mManager.setDisplayOffset(-mStart, 0);
	}

	@Override
	public boolean contains(float x, float y) {
		return (mX < x && x <= mX + mWidth) && 
			(mY < y && y <= mY + mHeight + mCanvasHeight * 0.25);
	}			
	
		
}
