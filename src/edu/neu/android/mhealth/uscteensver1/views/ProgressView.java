package edu.neu.android.mhealth.uscteensver1.views;

import edu.neu.android.mhealth.uscteensver1.R;
import edu.neu.android.mhealth.uscteensver1.data.ChunkManager;
import edu.neu.android.mhealth.uscteensver1.pages.AppPage;
import edu.neu.android.mhealth.uscteensver1.pages.AppScale;
import edu.neu.android.mhealth.uscteensver1.threads.BaseThread;
import edu.neu.android.mhealth.uscteensver1.threads.GraphDrawer;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class ProgressView extends SurfaceView implements SurfaceHolder.Callback {

	private final static int PROGRESS_SPEED = 10;
	
	private SurfaceHolder mHolder;
	private Drawer  mDrawer  = null;
	private Handler mHandler = null;
	
	private Paint  mPaint;
	private String mText = "Loading...";
	private RectF  mRect;
	private Bitmap mImage;
	private Canvas mImageCanvas = null;
	private int    mOffset = 0;
	private int    mBkColor = Color.argb(150, 0, 0, 0);
	
	public ProgressView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setVisibility(View.GONE);
		
		mHolder = getHolder();
		mHolder.addCallback(this);
		
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setColor(Color.WHITE);
		mPaint.setStyle(Style.STROKE);		
		Typeface font = Typeface.create("serif", Typeface.BOLD_ITALIC);
		mPaint.setTypeface(font);		
		mPaint.setFakeBoldText(false);
		
		setBackgroundColor(mBkColor);
	}

	public void show(String text) {
		mText = text;		
		mOffset = 0;
		mDrawer = new Drawer(this, mHandler);
		this.setVisibility(View.VISIBLE);			
	}
	
	public void dismiss() {
		this.setVisibility(View.GONE);
	}
	
	public void setHandler(Handler handler) {
		mHandler = handler;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		int w = getWidth();
		int h = getHeight();			
		
		// draw text
		mPaint.setTextAlign(Align.CENTER);
		mPaint.setTextSize(AppScale.doScaleT(40));
		mPaint.setStyle(Style.FILL);	
		mPaint.setStrokeWidth(AppScale.doScaleT(2.0f));
		canvas.drawText(mText, w / 2, h * 0.5f, mPaint);
		mPaint.setStyle(Style.STROKE);		
		// draw lines in the rectangle
		int imgW = mImage.getWidth();
		int imgH = mImage.getHeight();
		Rect  src = new Rect(imgW - mOffset, 0, imgW, imgH);
		RectF dst = new RectF(mRect.left, mRect.top, mRect.left + mOffset, mRect.bottom);
		canvas.drawBitmap(mImage, src, dst, null);		
		src = new Rect(0, 0, imgW - mOffset, imgH);
		dst = new RectF(mRect.left + mOffset, mRect.top, mRect.right, mRect.bottom);
		canvas.drawBitmap(mImage, src, dst, null);
		mOffset = (mOffset >= mRect.right) ? 0 : mOffset + PROGRESS_SPEED;
		// draw round rectangle
		mPaint.setStrokeWidth(AppScale.doScaleT(6.0f));
		canvas.drawRoundRect(mRect, AppScale.doScaleT(24), AppScale.doScaleT(24), mPaint);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (this.getVisibility() == View.VISIBLE) {
			return true;
		}
		return super.onTouchEvent(event);		
	}	

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (mDrawer != null) {
			mDrawer.start();
		}
				
		int w = getWidth();
		int h = getHeight();
		mRect = new RectF();
		mRect.left   = w * 0.2f;
		mRect.right  = w * 0.8f;
		mRect.top    = h * 0.556f;
		mRect.bottom = h * 0.61f;
		
		BitmapFactory.Options options = new BitmapFactory.Options(); 
        options.inPurgeable = true;
        options.inPreferredConfig = Config.RGB_565; 
        mImage = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.transparent, options);
        mImage = Bitmap.createScaledBitmap(mImage, (int) mRect.width(), (int) mRect.height(), true);
        mImageCanvas = new Canvas(mImage);
        
        mPaint.setStrokeWidth(AppScale.doScaleT(1.0f));
		float count = AppScale.doScaleW(12);
		for (float x = w * 0.01f; x < w * 0.585f; x += w * 0.04f) {
			float x1 = x;
			float y1 = h * 0.045f;
			float x2 = x + w * 0.02f;
			float y2 = y1 - w * 0.02f;
			for (float m = 0; m < count; m += 1.0f) {
				mImageCanvas.drawLine(x1 + m, y1, x2 + m, y2, mPaint);
			}
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if (mDrawer != null) {
			mDrawer.end();
			mDrawer = null;
		}
	}
	
	public class Drawer extends BaseThread {
		// the holder of the SurfaceView
		protected SurfaceHolder mHolder = null;
		// the surface view
		protected ProgressView mView = null;	
		// flag to indicate whether the drawer should be paused
		protected boolean mPause = false;
		
		public Drawer(ProgressView view, Handler handler) {
			mView   = view;			
			mHolder = view.getHolder(); 
			setHandler(handler);				
		}	

		public void pause(boolean pause) {
			mPause = pause;
		}
		
		@Override
		public void run() {			
			Canvas c = null;
			
			while (mRun) {
				
				handleEvent(mEventQueue.poll());	
				
				try {
	                c = mHolder.lockCanvas(null);
	                synchronized (mHolder) {	                			                	
	                	mView.onDraw(c);	                			                	
	                }
	            } catch (Exception e) {
	            	e.printStackTrace();
				} finally {
	                // do this in a finally so that if an exception is thrown
	                // during the above, we don't leave the Surface in an
	                // inconsistent state
	                if (c != null) {
	                	mHolder.unlockCanvasAndPost(c);
	                }
	            } // end finally block
				
				synchronized (this) {
					try {
						wait(30);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				while (mPause && mRun) {						
					try {
						sleep(50);					
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			
			super.run();
		} // end of run

	}
}
