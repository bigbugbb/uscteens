package edu.neu.android.mhealth.uscteensver1.pages;

import java.util.ArrayList;
import java.util.List;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.MotionEvent;

public abstract class AppObject {
	
    public final static int UNKNOWN    = 0;
    public final static int BACKGROUND = 1;
    public final static int TITLE      = 2;
    public final static int CHUNK      = 3;
    public final static int BUTTON     = 4;
    public final static int LABEL      = 5;
    public final static int TEXTVIEW   = 6;
    public final static int SLIDERBAR  = 7;

    protected float mX;
    protected float mY;

    protected float mWidth;
    protected float mHeight;

    protected float mSpeedX;
    protected float mSpeedY;
    protected float mMinSpeedX;
    protected float mMinSpeedY;
    protected float mMaxSpeedX;
    protected float mMaxSpeedY;
    protected float mAccSpeedX;
    protected float mAccSpeedY;

    protected int       mID;
    protected int       mKind;
    protected int       mZOrder;
    protected boolean   mVisible;
    protected boolean   mMovable;
    protected boolean   mSelected;
    protected Resources mRes;
    protected boolean   mImageLoaded;
    protected List<Bitmap> mImages;

    protected AppObject(Resources res) {

        mX = 0;
        mY = 0;

        mWidth  = 0;
        mHeight = 0;

        mSpeedX    = 0;
        mSpeedY    = 0;
        mMinSpeedX = 0;
        mMinSpeedY = 0;
        mMaxSpeedX = 0;
        mMaxSpeedY = 0;
        mAccSpeedX = 0;
        mAccSpeedY = 0;

        mID          = -1;
        mKind        = UNKNOWN;
        mZOrder      = 0;
        mVisible     = true;
        mMovable     = false;
        mSelected    = false;
        mRes         = res;
        mImageLoaded = false;
        mImages = new ArrayList<Bitmap>();
    }

    public void loadImages(int[] resIDs) {
        if (mImageLoaded) {
            return;
        }
        mImageLoaded = true;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPurgeable  = true;
        options.inPreferredConfig = Config.RGB_565;
        for (int id : resIDs) {
            Bitmap origin = BitmapFactory.decodeResource(mRes, id, options);
            Bitmap scaled = null;
            // scale the image according to the current screen resolution
            float dstWidth  = AppScale.doScaleW(origin.getWidth());
            float dstHeight = AppScale.doScaleH(origin.getHeight());
            if (dstWidth != origin.getWidth() || dstHeight != origin.getHeight()) {
                scaled = Bitmap.createScaledBitmap(origin, (int) dstWidth, (int) dstHeight, true);
            }
            // add to the image list
            if (scaled != null) {
                origin.recycle(); // explicit call to avoid out of memory
                mImages.add(scaled);
            } else {
                mImages.add(origin);
            }
        }
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

    public void setX(float x) {
        mX = x;
    }

    public float getX() {
        return mX;
    }

    public void setY(float y) {
        mY = y;
    }

    public float getY() {
        return mY;
    }

    public void setWidth(float width) {
        mWidth = width;
    }

    public float getWidth() {
        return mWidth;
    }

    public void setHeight(float height) {
        mHeight = height;
    }

    public float getHeight() {
        return mHeight;
    }

    public void setSpeed(float x, float y) {
        setSpeedX(x);
        setSpeedY(y);
    }

    public void setSpeedX(float x) {
        mSpeedX = x;
    }

    public void setSpeedY(float y) {
        mSpeedY = y;
    }

    public float getSpeedX() {
        return mSpeedX;
    }

    public float getSpeedY() {
        return mSpeedY;
    }

    public void setMinSpeed(float x, float y) {
        setMinSpeedX(x);
        setMinSpeedY(y);
    }

    public void setMinSpeedX(float x) {
        mMinSpeedX = x;
    }

    public void setMinSpeedY(float y) {
        mMinSpeedY = y;
    }

    public float getMinSpeedX() {
        return mMinSpeedX;
    }

    public float getMinSpeedY() {
        return mMinSpeedY;
    }

    public void setMaxSpeed(float x, float y) {
        setMaxSpeedX(x);
        setMaxSpeedY(y);
    }

    public void setMaxSpeedX(float x) {
        mMaxSpeedX = x;
    }

    public void setMaxSpeedY(float y) {
        mMaxSpeedY = y;
    }

    public float getMaxSpeedX() {
        return mMaxSpeedX;
    }

    public float getMaxSpeedY() {
        return mMaxSpeedY;
    }

    public void setAccSpeed(float x, float y) {
        setAccSpeedX(x);
        setAccSpeedY(y);
    }

    public void setAccSpeedX(float x) {
        mAccSpeedX = x;
    }

    public void setAccSpeedY(float y) {
        mAccSpeedY = y;
    }

    public float getAccSpeedX() {
        return mAccSpeedX;
    }

    public float getAccSpeedY() {
        return mAccSpeedY;
    }

    public void setKind(int kind) {
        mKind = kind;
    }

    public int getKind() {
        return mKind;
    }

    public void setZOrder(int order) {
        mZOrder = order;
    }

    public int getZOrder() {
        return mZOrder;
    }

    public void setVisible(boolean visible) {
        mVisible = visible;
    }

    public boolean getVisible() {
        return mVisible;
    }

    public void setMovable(boolean movable) {
        mMovable = movable;
    }

    public boolean getMovable() {
        return mMovable;
    }

    public void setSelected(boolean selected) {
        mSelected = selected;
    }

    public boolean getSelected() {
        return mSelected;
    }

    public void setResources(Resources res) {
        mRes = res;
    }

    public void setID(int id) {
        mID = id;
    }

    public int getID() {
        return mID;
    }

    public boolean isVisible() {
        return mVisible;
    }

    public boolean isMovable() {
        return mMovable;
    }

    public boolean isSelected() {
        return mSelected;
    }
    
    public void update() {
    }

    public void measureSize(int width, int height) {
    }

    public void onDraw(Canvas c) {
        // TODO Auto-generated method stub
    }

    public void onSizeChanged(int width, int height) {
        // invoked when the surface size is changed
    }

    public class ZOrders {
        public static final int BACKGROUND = 0;
        public static final int TITLE      = 1;
        public static final int LABEL      = 1;
        public static final int CHUNK      = 2;
        public static final int BUTTON     = 4;
        public static final int TEXTVIEW   = 4;
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
