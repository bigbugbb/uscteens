package edu.neu.android.mhealth.uscteensver1.threads;

import edu.neu.android.mhealth.uscteensver1.pages.AppPage;
import edu.neu.android.mhealth.uscteensver1.views.GraphView;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;

public class GraphDrawer extends BaseThread {
	// the holder of the SurfaceView
	protected SurfaceHolder mHolder = null;
	// the app surface view
	protected GraphView mView = null;
	// the app page to draw
	protected AppPage  mPage = null;	
	// flag to indicate whether the drawer should be paused
	protected boolean mPause = false;
	// for pause synchronization
//	private Object mLock = new Object();
	
	public GraphDrawer(GraphView view, AppPage page, Handler handler) {
		mView   = view;
		mPage   = page;		
		mHolder = view.getHolder(); 
		setHandler(handler);				
	}	
	
	public void setPage(AppPage page) {
		mPage = page;
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
                	if (c != null) {
                		//Log.d("draw page", "in onDraw");
                		synchronized (mPage) {
                			mPage.onDraw(c);
                		}
                	}
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
					wait(10);
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
