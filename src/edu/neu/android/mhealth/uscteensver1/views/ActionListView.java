package edu.neu.android.mhealth.uscteensver1.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.ListView;
import edu.neu.android.mhealth.uscteensver1.pages.AppScale;

public class ActionListView extends ListView {
	
	private static final int MAX_Y_OVERSCROLL_DISTANCE = 70;
	
	private int mMaxYOverscrollDistance;
	
	public ActionListView(Context context) {
		super(context);		
		initBounceListView();
	}
	
	public ActionListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initBounceListView();
	}
	
	public ActionListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initBounceListView();
	}
	
	private void initBounceListView() {
	    // get the density of the screen and do some maths with it on the max 
		// overscroll distance variable so that you get similar behaviors no 
		// matter what the screen size
	
		final DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
		final float density = metrics.density;
	
		mMaxYOverscrollDistance = (int) (density * AppScale.doScaleH(MAX_Y_OVERSCROLL_DISTANCE));
	}
	
	@SuppressLint("NewApi")
	@Override
	protected boolean overScrollBy(int deltaX, int deltaY, int scrollX,
			 int scrollY, int scrollRangeX, int scrollRangeY,
			 int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
		// This is where the magic happens, we have replaced the incoming
		// maxOverScrollY with our own custom variable mMaxYOverscrollDistance;		
		return super.overScrollBy(deltaX, deltaY, scrollX, scrollY,
				scrollRangeX, scrollRangeY, maxOverScrollX, mMaxYOverscrollDistance, isTouchEvent);
	}	
}
