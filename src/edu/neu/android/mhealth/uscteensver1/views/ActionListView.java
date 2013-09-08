package edu.neu.android.mhealth.uscteensver1.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ExpandableListView;
import android.widget.ListView;

public class ActionListView extends ExpandableListView {

    private OnOverScrolledListener mListener;

    public ActionListView(Context context) {
        super(context);
    }

    public ActionListView(Context context, AttributeSet attrs) {
        super(context, attrs);                
    }

    public ActionListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setOnOverScrolledListener(OnOverScrolledListener listener) {
        mListener = listener;
    }

    @SuppressLint("NewApi")
    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX,
                                   int scrollY, int scrollRangeX, int scrollRangeY,
                                   int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        // This is where the magic happens, we have replaced the incoming
        // maxOverScrollY with our own custom variable mMaxYOverscrollDistance;
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY,
                scrollRangeX, scrollRangeY, maxOverScrollX, getHeight() >> 2, isTouchEvent);
    }

    @SuppressLint("NewApi")
    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX,
                                  boolean clampedY) {
        if (mListener != null) {
            mListener.onOverScrolled(this, scrollX, scrollY, clampedX, clampedY);
        }
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
    }

    public interface OnOverScrolledListener {
        void onOverScrolled(ListView view, int scrollX, int scrollY, boolean clampedX, boolean clampedY);
    }
}
