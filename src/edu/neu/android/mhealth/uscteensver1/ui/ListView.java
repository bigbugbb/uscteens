package edu.neu.android.mhealth.uscteensver1.ui;

import java.util.ArrayList;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.view.MotionEvent;
import edu.neu.android.mhealth.uscteensver1.USCTeensGlobals;
import edu.neu.android.mhealth.uscteensver1.pages.AppObject;
import edu.neu.android.mhealth.uscteensver1.pages.AppScale;


public class ListView extends AppObject {
	
	protected int    mStart = 0;
	protected int    mEnd   = 0;	
	protected int    mItemWidth  = 0;
	protected int    mItemHeight = 0;	
	protected int	 mCanvasWidth  = 0;
	protected int 	 mCanvasHeight = 0;
	protected int    mOffsetY = 0;
	protected int    mLastAction = 0;
	protected int    mBorderWidth = 3;
	protected float  mOffsetSpeedX = 0;
	protected float  mOffsetSpeedY = 0;
	protected float  mOffsetAccSpeedX = 0;
	protected float  mOffestAccSpeedY = 0;
	protected Typeface mTypeface = null;
	protected Canvas   mInnerCanvas = null;
	protected ArrayList<ListItem> mItems = new ArrayList<ListItem>();
	protected OnBoundaryListener  mOnBoundaryListener  = null;
	protected OnItemClickListener mOnItemClickListener = null;
	protected OnListViewScrollingListener mOnListViewScrollingListener = null;
	
	public class ListItem {
		protected float    mX;
		protected float    mY;
		protected float    mWidth;
		protected float    mHeight;
		protected int	   mPosn;
		protected String   mText;
		protected Bitmap   mImage;
		protected int	   mDrawable;
		protected boolean  mSelected;
		protected Paint    mPaintBkg;		
		protected Paint    mPaintTxt;
		protected Paint	   mPaintLine;
		protected ListView mParent;
		
		public ListItem(ListView parent, String text, int drawable, Bitmap image) {
			mText   = text;
			mImage  = image;
			mParent = parent;
			mDrawable = drawable;
			
			mPaintBkg = new Paint(Paint.ANTI_ALIAS_FLAG);
			mPaintBkg.setColor(Color.WHITE);
			mPaintBkg.setStyle(Style.FILL);		
			mPaintBkg.setFakeBoldText(false);
			
			mPaintTxt = new Paint(Paint.ANTI_ALIAS_FLAG);
			mPaintTxt.setColor(Color.BLACK);		
			mPaintTxt.setTextSize(AppScale.doScaleT(45));
			mPaintTxt.setTypeface(mTypeface);
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
		
		protected void onSelected(boolean selected) {
			Paint paint = getBackgroundPaint();
			paint.setColor(getBackgroundColor());
		}
		
		protected void setSelected(boolean selected) {
			mSelected = selected;
			onSelected(selected);
		}
		
		protected boolean isSelected() {
			return mSelected;
		}
		
		protected void onSizeChange(int width, int height) {
			
		}
		
		protected void onDraw(Canvas c) {	
			float y1 = mOffsetY + (mHeight + mBorderWidth) * mPosn;
			float y2 = mOffsetY + (mHeight + mBorderWidth) * mPosn + mHeight;
			
			if (y1 > mCanvasHeight || y2 < 0) {
				return;
			}
			
			c.drawRect(0, y1, mWidth - mBorderWidth, y2, mPaintBkg);		
			c.drawText(mText, mWidth / 2 - AppScale.doScaleW(250), y1 + mHeight * 0.6f, mPaintTxt);
			c.drawBitmap(mImage, mWidth * 0.86f - mImage.getWidth() * 0.5f, y1 + AppScale.doScaleH(26), null);
		}
		
		public void setBackgroundPaint(Paint background) {
			mPaintBkg = background;
		}
		
		public Paint getBackgroundPaint() {
			return mPaintBkg;
		}
		
		public int getBackgroundColor() {		
			int color = Color.WHITE;

			if (mSelected) {
				color = Color.rgb(198, 235, 245);
			}
			
			return color;
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
    		dstWidth  = AppScale.doScaleW(dstWidth);
    		dstHeight = AppScale.doScaleH(dstHeight);
    		if (dstWidth != origin.getWidth() || dstHeight != origin.getHeight()) {
    			scaled = Bitmap.createScaledBitmap(origin, (int) dstWidth, (int) dstHeight, true);
    		}        
        	if (scaled != null) {
	    		origin.recycle(); // explicit call to avoid out of memory
	    		mImage = scaled;
	        } else {
	        	mImage = origin;
	        }        	
		}
		
		public int getItemImage() {
			return mDrawable;
		}
	}

	public ListView(Resources res) {
		super(res);	
		
		mTypeface = Typeface.createFromAsset(USCTeensGlobals.sContext.getAssets(), "font/arial.ttf");
		mOffsetSpeedY = AppScale.doScaleH(16f);
		//mOffestAccSpeedY = sAppScale.doScaleH(3f);
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
		dstWidth  = AppScale.doScaleW(dstWidth);
		dstHeight = AppScale.doScaleH(dstHeight);
		if (dstWidth != origin.getWidth() || dstHeight != origin.getHeight()) {
			scaled = Bitmap.createScaledBitmap(origin, (int) dstWidth, (int) dstHeight, true);
		}            
    	Bitmap image = null;
    	if (scaled != null) {
    		origin.recycle(); // explicit call to avoid out of memory
    		image = scaled;
        } else {
        	image = origin;
        }
       	
       	ListItem li = new ListItem(this, text, drawable, image);
       	mItems.add(li);
       	li.register();
	}
	
	public ListItem getItem(int pos) {
		return mItems.get(pos);
	}	

	public int getItemCount() {
		return mItems.size();
	}
	
	public int getBorderWidth() {
		return mBorderWidth;
	}
	
	public boolean outOfBound() {
		return mOffsetY > 0 || 
			mOffsetY + (mItemHeight + mBorderWidth) * mItems.size() - mBorderWidth < mHeight;
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
			mOffsetY = (int) Math.max(0, Math.min(mItemHeight * 1.3f, mOffsetY - mOffsetSpeedY));
			mSpeedY = 0;
			if (mOnBoundaryListener != null) {
				mOnBoundaryListener.onBoundary(this, true, false);
			}
		} else if (mOffsetY + (mItemHeight + mBorderWidth) * mItems.size() - mBorderWidth <= mHeight) {			
			mOffsetY = (int) Math.min(- (mItemHeight + mBorderWidth) * (mItems.size() - 4), 
				Math.max(mOffsetY + mOffsetSpeedY, 
					- (mItemHeight + mBorderWidth) * (mItems.size() - 4) - mItemHeight * 1.3f));
			mSpeedY = 0;
			if (mOnBoundaryListener != null) {
				mOnBoundaryListener.onBoundary(this, false, false);
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
				li.setSelected(true);				
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
					mOnItemClickListener.onItemClicked(this, li, i);					
				}		
				li.setSelected(false);
			}			
		}
		mLastAction = e.getAction();										
		
		return super.onUp(e);
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) { // up: -				
		mSpeedY = Math.min(Math.abs(velocityY / AppScale.doScaleH(60)), 80);
		mSpeedY = velocityY > 0 ? mSpeedY : -mSpeedY;
		mAccSpeedY = AppScale.doScaleH(mSpeedY > 0 ? -2.5f : 2.5f);
		mLastAction = e2.getAction();
		return true;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) { // up: +
		mOffsetY += (int) -distanceY;
		
		if (mOffsetY > 0) {
			mOffsetY = (int) Math.min(mOffsetY, mItemHeight * 1.3f);			
			if (mOnBoundaryListener != null) {
				mOnBoundaryListener.onBoundary(this, true, false);
			}
		} else if (mOffsetY + (mItemHeight + mBorderWidth) * mItems.size() - mBorderWidth <= mHeight) {			
			mOffsetY = (int) Math.max(mOffsetY, 
				- (mItemHeight + mBorderWidth) * (mItems.size() - 4) - mItemHeight * 1.3f);
			if (mOnBoundaryListener != null) {
				mOnBoundaryListener.onBoundary(this, false, false);
			}
		} else {
			if (mOnListViewScrollingListener != null) {
				mOnListViewScrollingListener.onListViewScrolling(this, 0, (int) distanceY);
			}
		}
		
		mLastAction = e2.getAction();
		return true;
	}
	
	public void setOnBoundaryListener(OnBoundaryListener listener) {
		mOnBoundaryListener = listener;
	}
	
	public void setOnItemClickListener(OnItemClickListener listener) {
		mOnItemClickListener = listener;
	}
	
	public void setOnListViewScrollingListener(OnListViewScrollingListener listener) {
		mOnListViewScrollingListener = listener;
	}
	
	public interface OnItemClickListener {
		void onItemClicked(ListView view, ListItem li, int posn);
	}

	public interface OnBoundaryListener {
		void onBoundary(ListView view, boolean top, boolean left);
	}
	
	public interface OnListViewScrollingListener {
		void onListViewScrolling(ListView view, int dx, int dy);
	}

	@Override
	public void setSelected(boolean selected) {
		if (selected == false) {
			for (ListItem li : mItems) {				
				li.setSelected(false);				
			}
		}
		super.setSelected(selected);
	}
}
