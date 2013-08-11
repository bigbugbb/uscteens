package edu.neu.android.mhealth.uscteensver1.views;

import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import edu.neu.android.mhealth.uscteensver1.pages.AppPage;
import edu.neu.android.mhealth.uscteensver1.threads.GraphDrawer;

public class GraphView extends SurfaceView implements SurfaceHolder.Callback {

	protected SurfaceHolder mHolder  = null;
	protected Handler       mHandler = null;
	protected GraphDrawer   mDrawer  = null;
	protected List<AppPage> mPages   = null;
	
	// constructor must have AttributeSet to create from XML
	public GraphView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mHolder = getHolder();
		mHolder.addCallback(this);				
	}
	
	public void onStop() {
		if (mDrawer != null) {
			mDrawer.end();
			mDrawer = null;
		}
	}
	
	public void onStart(AppPage page) {
		mDrawer = new GraphDrawer(this, page, mHandler);					
	}	
	
	public void onPause() {}
	
	public void onResume() {}
	
	public void setHandler(Handler handler) {
		mHandler = handler;
	}
	
	public void setPages(List<AppPage> pages) {
		mPages = pages;
	}
	
	public GraphDrawer getDrawer() {
		return mDrawer;
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		try {
			for (AppPage page : mPages) {				
				synchronized (page) {
					page.onSizeChanged(width, height);
				}	
			} 			
		} catch (Exception e) {
        	e.printStackTrace();
		}
	}

	public void surfaceCreated(SurfaceHolder holder) {
		if (mDrawer != null) {
			mDrawer.start();
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {}

}
