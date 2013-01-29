package edu.neu.android.mhealth.uscteensver1;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Bitmap.Config;
import android.view.MotionEvent;

public class AppObject {
	public final static int UNKNOWN	   = 0;
	public final static int BACKGROUND = 1;
	public final static int TITLE	   = 2;
	public final static int CHUNK	   = 3;
	public final static int BUTTON	   = 4;
	public final static int SLIDERBAR  = 5;
	
	protected float mX = 0;
	protected float mY = 0;
	protected float mWidth = 0;
	protected float mHeight = 0;
	protected float mSpeedX = 0;
	protected float mSpeedY = 0;
	protected float mMinSpeedX = 0;
	protected float mMinSpeedY = 0;
	protected float mMaxSpeedX = 0;
	protected float mMaxSpeedY = 0;	
	protected float mAccSpeedX = 0;	
	protected float mAccSpeedY = 0;
	protected int mID     = -1;
	protected int mKind   = UNKNOWN;
	protected int mZOrder = 0;
	protected boolean mVisible = true;
	protected boolean mMovable = false;
	protected boolean mSelected = false;
	protected Resources mRes = null;
	protected boolean mImageLoaded = false;	
	protected List<Bitmap> mImages = new ArrayList<Bitmap>();

	protected AppObject(Resources res) {	
		//setResources(res);
		mRes = res;
	}
	
	public void loadImages(int[] resIDs) {
		if (mImageLoaded) {
			return;
		}
		mImageLoaded = true;
		
		BitmapFactory.Options options = new BitmapFactory.Options(); 
        options.inPurgeable = true;
        options.inPreferredConfig = Config.RGB_565; 
        for (int id : resIDs) {
        	mImages.add(BitmapFactory.decodeResource(mRes, id, options));
        }
	}

	public void release() {
		for (Bitmap image : mImages) {
			if (image != null) {
				image.recycle();
				image = null;
			}
		}
	}
	
	public void setX(float x) { mX = x; }
	
	public float getX() { return mX; }
	
	public void setY(float y) { mY = y; }
	
	public float getY() { return mY; }
	
	public void setWidth(float width) { mWidth = width; }
	
	public float getWidth() { return mWidth; }
	
	public void setHeight(float height) { mHeight = height; }
	
	public float getHeight() { return mHeight; }
	
	public void setSpeed(float x, float y) { setSpeedX(x); setSpeedY(y); }
	
	public void setSpeedX(float x) { mSpeedX = x; }
	
	public void setSpeedY(float y) { mSpeedY = y; }
	
	public float getSpeedX() { return mSpeedX; }
	
	public float getSpeedY() { return mSpeedY; }
	
	public void setMinSpeed(float x, float y) { setMinSpeedX(x); setMinSpeedY(y); }
	
	public void setMinSpeedX(float x) { mMinSpeedX = x; }
	
	public void setMinSpeedY(float y) { mMinSpeedY = y; }
	
	public float getMinSpeedX() { return mMinSpeedX; }
	
	public float getMinSpeedY() { return mMinSpeedY; }
	
	public void setMaxSpeed(float x, float y) { setMaxSpeedX(x); setMaxSpeedY(y); }
	
	public void setMaxSpeedX(float x) { mMaxSpeedX = x; }
	
	public void setMaxSpeedY(float y) { mMaxSpeedY = y; }
	
	public float getMaxSpeedX() { return mMaxSpeedX; }
	
	public float getMaxSpeedY() { return mMaxSpeedY; }
	
	public void setAccSpeed(float x, float y) { setAccSpeedX(x); setAccSpeedY(y); }
	
	public void setAccSpeedX(float x) { mAccSpeedX = x; }
	
	public void setAccSpeedY(float y) { mAccSpeedY = y; }
	
	public float getAccSpeedX() { return mAccSpeedX; }
	
	public float getAccSpeedY() { return mAccSpeedY; }
	
	public void setKind(int kind) { mKind = kind; }
	
	public int getKind() { return mKind; }
	
	public void setZOrder(int order) { mZOrder = order; }
	
	public int getZOrder() { return mZOrder; }
	
	public void setVisible(boolean visible) { mVisible = visible; }
	
	public boolean getVisible() { return mVisible; }
	
	public void setMovable(boolean movable) { mMovable = movable; }
	
	public boolean getMovable() { return mMovable; }
	
	public void setSelected(boolean selected) { mSelected = selected; }
	
	public boolean getSelected() { return mSelected; }

	public void setResources(Resources res) { mRes = res; }
	
	public void setID(int id) { mID = id; }
	
	public int getID() { return mID; }
	
	public String toJSONString() {
		JSONObject obj = new JSONObject();  	              

        try {
			obj.put("x", mX);
			obj.put("y", mY);
	        obj.put("width", mWidth); 
	        obj.put("height", mHeight);
	        obj.put("speed_x", mSpeedX);
	        obj.put("speed_y", mSpeedY);
	        obj.put("min_speed_x", mMinSpeedX);
	        obj.put("min_speed_y", mMinSpeedY);
	        obj.put("max_speed_x", mMaxSpeedX);
	        obj.put("max_speed_y", mMaxSpeedY);
	        obj.put("acc_speed_x", mAccSpeedX);
	        obj.put("acc_speed_y", mAccSpeedY);
	        obj.put("kind", mKind);
	        obj.put("z_order", mZOrder);
	        obj.put("visible", mVisible);
	        obj.put("movable", mMovable);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}        
        
        return obj.toString();
	}
    
	public void fromJSONString(String strJSON) {
		JSONObject obj;
		
		try {
			obj = new JSONObject(strJSON);
			mX = (float)obj.getDouble("x");
			mY = (float)obj.getDouble("y");
			mWidth  = (float)obj.getDouble("width");
			mHeight = (float)obj.getDouble("height");
			mSpeedX = (float)obj.getDouble("speed_x");
			mSpeedY = (float)obj.getDouble("speed_y");
			mMinSpeedX = (float)obj.getDouble("min_speed_x");
			mMinSpeedY = (float)obj.getDouble("min_speed_y");
			mMaxSpeedX = (float)obj.getDouble("max_speed_x");
			mMaxSpeedY = (float)obj.getDouble("max_speed_y");
			mAccSpeedX = (float)obj.getDouble("acc_speed_x");
			mAccSpeedY = (float)obj.getDouble("acc_speed_y");
			mKind       = obj.getInt("kind");
			mZOrder     = obj.getInt("z_order");
			mVisible    = obj.getBoolean("visible");
			mMovable    = obj.getBoolean("movable");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}							
	}
	
	public boolean isVisible() { return mVisible; }
	
	public boolean isMovable() { return mMovable; }
	
	public boolean isSelected() { return mSelected; }
	
	public void operate(AppCtrl ctrl) {}

	public void update() {}
	
	public void measureSize(int width, int height) {}

	public void onDraw(Canvas c) {
		// TODO Auto-generated method stub
	}
	
	public void onSizeChanged(int width, int height) {
		// invoked when the surface size is changed
	}
	
	public interface IDrawer {
		void doDraw(Canvas c);
	}
	
	public class ZOrders {
		public static final int BACKGROUND = 0;
		public static final int TITLE	   = 1;
		public static final int CHUNK	   = 2;
		public static final int BUTTON	   = 4;
		public static final int SLIDERBAR  = 4;
	}
	
	public boolean contains(float x, float y) {
		return (mX < x && x <= mX + mWidth) && (mY < y && y <= mY + mHeight);
	}
	
	public void onCancelSelection(MotionEvent e) {
		
	}
	
	public boolean onDown(MotionEvent e) {		
		return false;
	}
	
	public boolean onUp(MotionEvent e) {
		return false;
	}

	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		return false;
	}

	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}
		
}
