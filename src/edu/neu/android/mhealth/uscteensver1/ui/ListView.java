package edu.neu.android.mhealth.uscteensver1.ui;

import java.util.ArrayList;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Style;
import android.graphics.Paint;
import android.view.MotionEvent;
import edu.neu.android.mhealth.uscteensver1.AppObject;


public class ListView extends AppObject {
	
	protected int    mStart = 0;
	protected int    mEnd   = 0;	
	protected int    mItemWidth  = 0;
	protected int    mItemHeight = 0;	
	protected int    mOffsetY = 0;
	protected int    mLastAction = 0;
	protected int    mBorderWidth = 3;
	protected Canvas mInnerCanvas = null;
	protected ArrayList<ListItem> mItems = new ArrayList<ListItem>();
	protected OnReachedEndListener mOnReachedEndListener = null;
	protected OnItemClickListener  mOnItemClickListener  = null;
	protected OnListViewScrollingListener mOnListViewScrollingListener = null;
	
	public class ListItem {
		protected float    mX;
		protected float    mY;
		protected float    mWidth;
		protected float    mHeight;
		protected int	   mPosn;
		protected String   mText;
		protected Bitmap   mImage;
		protected Paint    mPaintBkg;		
		protected Paint    mPaintTxt;
		protected Paint	   mPaintLine;
		protected ListView mParent;
		
		public ListItem(ListView parent, String text, Bitmap image) {
			mText   = text;
			mImage  = image;
			mParent = parent;
			
			mPaintBkg = new Paint(Paint.ANTI_ALIAS_FLAG);
			mPaintBkg.setColor(Color.WHITE);
			mPaintBkg.setStyle(Style.FILL);
			mPaintBkg.setTypeface(Typeface.SERIF);
			mPaintBkg.setFakeBoldText(false);
			
			mPaintTxt = new Paint(Paint.ANTI_ALIAS_FLAG);
			mPaintTxt.setColor(Color.BLACK);
			mPaintTxt.setStyle(Style.STROKE);
			mPaintTxt.setTextSize(sAppScale.doScaleT(45));
			mPaintTxt.setTypeface(Typeface.SERIF);
			mPaintTxt.setFakeBoldText(false);
			
			mPaintLine = new Paint(Paint.ANTI_ALIAS_FLAG);
			mPaintLine.setStyle(Style.FILL);		
			mPaintLine.setColor(Color.rgb(179, 181, 181));
		}
		
		protected boolean contains(float x, float y) {
			if (x > mX && x < mX + mWidth && 
					y > mY + mOffsetY && y < mY + mOffsetY + mHeight)
				return true;
			return false;
		}
		
		protected void register() {
			mPosn = mItems.indexOf(this);
		}
		
		protected void onSizeChange(int width, int height) {
			
		}
		
		protected void onDraw(Canvas c) {						
			c.drawRect(0, mOffsetY + mHeight * mPosn + mBorderWidth, mWidth - mBorderWidth, 
				mOffsetY + mHeight * (mPosn + 1), mPaintBkg);			
			c.drawRect(0, 0, mWidth - mBorderWidth, mBorderWidth, mPaintLine);		
			c.drawText(mText, mWidth / 2 - sAppScale.doScaleW(250), 
				mOffsetY + mHeight * mPosn + mHeight * 0.6f, mPaintTxt);
			c.drawBitmap(mImage, mWidth - mImage.getWidth() * 1.6f, 
				mOffsetY + mHeight * mPosn + sAppScale.doScaleH(26), null);
		}
		
		public void setPaintBackground(Paint background) {
			mPaintBkg = background;
		}
		
		public Paint getPaintBackground() {
			return mPaintBkg;
		}
		
		public void setPaintText(Paint text) {
			mPaintTxt = text;
		}
		
		public Paint getPaintText() {
			return mPaintTxt;
		}
		
		public void setItemImage(Bitmap image) {
			if (mImage != null) {
				mImage.recycle();
			}
			mImage = image;
		}
		
		public void setItemImage(int drawable) {
			if (mImage != null) {
				mImage.recycle();
			}

			BitmapFactory.Options options = new BitmapFactory.Options(); 
	        options.inPurgeable = true;
	        options.inPreferredConfig = Config.RGB_565;
	        
	       	Bitmap origin = BitmapFactory.decodeResource(mRes, drawable, options);
        	Bitmap scaled = null;
        	// scale the image according to the current screen resolution
        	float dstWidth  = origin.getWidth(),
        	      dstHeight = origin.getHeight();        	
        	if (sAppScale != null) {
        		dstWidth  = sAppScale.doScaleW(dstWidth);
        		dstHeight = sAppScale.doScaleH(dstHeight);
        		if (dstWidth != origin.getWidth() || dstHeight != origin.getHeight()) {
        			scaled = Bitmap.createScaledBitmap(origin, (int) dstWidth, (int) dstHeight, true);
        		}
            }        	    		
        	if (scaled != null) {
	    		origin.recycle(); // explicit call to avoid out of memory
	    		mImage = scaled;
	        } else {
	        	mImage = origin;
	        }
		}
	}

	public ListView(Resources res) {
		super(res);	
	}
	
	public void addItem(String text, int drawable) {
		BitmapFactory.Options options = new BitmapFactory.Options(); 
        options.inPurgeable = true;
        options.inPreferredConfig = Config.RGB_565;
        
       	Bitmap origin = BitmapFactory.decodeResource(mRes, drawable, options);
    	Bitmap scaled = null;    	
    	// scale the image according to the current screen resolution
    	float dstWidth  = origin.getWidth(),
    	      dstHeight = origin.getHeight();        	
    	if (sAppScale != null) {
    		dstWidth  = sAppScale.doScaleW(dstWidth);
    		dstHeight = sAppScale.doScaleH(dstHeight);
    		if (dstWidth != origin.getWidth() || dstHeight != origin.getHeight()) {
    			scaled = Bitmap.createScaledBitmap(origin, (int) dstWidth, (int) dstHeight, true);
    		}
        }        	
    	Bitmap image = null;
    	if (scaled != null) {
    		origin.recycle(); // explicit call to avoid out of memory
    		image = scaled;
        } else {
        	image = origin;
        }
       	
       	ListItem li = new ListItem(this, text, image);
       	mItems.add(li);
       	li.register();
	}
	
	public ListItem getItem(int pos) {
		return mItems.get(pos);
	}	

	public int getItemCount() {
		return mItems.size();
	}
	
	@Override
	public void onDraw(Canvas c) {
		if (Math.abs(mSpeedY) > 0) {
			mOffsetY += mSpeedY;
			if (mAccSpeedY < 0) {
				mSpeedY = Math.max(0, mSpeedY + mAccSpeedY);
			} else {
				mSpeedY = Math.min(0, mSpeedY + mAccSpeedY);
			}
		}		
				
		if (mOffsetY > 0) {
			mOffsetY = 0;			
			if (mOnReachedEndListener != null) {
				mOnReachedEndListener.onReachedEnd(this, true, false);
			}
		} else if (mOffsetY + mItemHeight * mItems.size() < mHeight) {
			mOffsetY = (int) (mHeight - mItemHeight * mItems.size());
			if (mOnReachedEndListener != null) {
				mOnReachedEndListener.onReachedEnd(this, false, false);
			}
		}
		
		if (mInnerCanvas != null) {
			mInnerCanvas.drawColor(Color.rgb(179, 181, 181));
			for (ListItem lvi : mItems) {
				lvi.onDraw(mInnerCanvas);
			}
		}
		c.drawBitmap(mImages.get(0), mX, mY, null);
	}

	@Override
	public void onSizeChanged(int width, int height) {
		mWidth  = width;
		mHeight = height;
		mItemWidth  = (int) mWidth;
		mItemHeight = (int) mHeight / 10;
		
		for (int i = 0; i < mItems.size(); ++i) {
			ListItem li = mItems.get(i);
			li.mX = mX;
			li.mY = mY + mItemHeight * i;
			li.mWidth  = mItemWidth;
			li.mHeight = mItemHeight;
		}
				    
	}
		
	@Override
	public boolean onDown(MotionEvent e) {			
		mSpeedY = 0;		
		
		for (ListItem li : mItems) {
			if (li.contains(e.getX(), e.getY())) {
				Paint paint = li.getPaintBackground();
				paint.setColor(Color.rgb(198, 235, 245));
				break;
			}
		}
		
		mLastAction = e.getAction();
		return true;
	}

	@Override
	public boolean onUp(MotionEvent e) {		
		for (int i = 0; i < mItems.size(); ++i) {
			ListItem li = mItems.get(i);
			if (li.contains(e.getX(), e.getY())) {
				if (mOnItemClickListener != null && mLastAction == MotionEvent.ACTION_DOWN) {	
					mOnItemClickListener.onItemClicked(this, i);					
				}					
			}
			Paint paint = li.getPaintBackground();
			paint.setColor(Color.WHITE);
		}
		mLastAction = e.getAction();										
		
		return super.onUp(e);
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) { // up: -
		mSpeedY = velocityY / 100;
		mAccSpeedY = mSpeedY > 0 ? -1 : 1;
		mLastAction = e2.getAction();
		return true;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) { // up: +
		mOffsetY += (int) -distanceY;
		
		if (mOffsetY > 0) {
			mOffsetY = 0;
			if (mOnReachedEndListener != null) {
				mOnReachedEndListener.onReachedEnd(this, true, false);
			}
		} else if (mOffsetY + mItemHeight * mItems.size() < mHeight) {
			mOffsetY = (int) (mHeight - mItemHeight * mItems.size());
			if (mOnReachedEndListener != null) {
				mOnReachedEndListener.onReachedEnd(this, false, false);
			}
		} else {
			if (mOnListViewScrollingListener != null) {
				mOnListViewScrollingListener.onListViewScrolling(this, 0, (int) distanceY);
			}
		}
		
		mLastAction = e2.getAction();
		return true;
	}
	
	public void setOnReachedEndListener(OnReachedEndListener listener) {
		mOnReachedEndListener = listener;
	}
	
	public void setOnItemClickListener(OnItemClickListener listener) {
		mOnItemClickListener = listener;
	}
	
	public void setOnListViewScrollingListener(OnListViewScrollingListener listener) {
		mOnListViewScrollingListener = listener;
	}
	
	public interface OnItemClickListener {
		void onItemClicked(ListView view, int posn);
	}

	public interface OnReachedEndListener {
		void onReachedEnd(ListView view, boolean top, boolean left);
	}
	
	public interface OnListViewScrollingListener {
		void onListViewScrolling(ListView view, int dx, int dy);
	}

	@Override
	public void setSelected(boolean selected) {
		if (selected == false) {
			for (ListItem li : mItems) {				
				Paint paint = li.getPaintBackground();
				paint.setColor(Color.WHITE);				
			}
		}
		super.setSelected(selected);
	}
}