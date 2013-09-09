package edu.neu.android.mhealth.uscteensver1.threads;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import android.graphics.Canvas;
import android.os.Handler;
import android.view.SurfaceHolder;
import edu.neu.android.mhealth.uscteensver1.pages.AppPage;
import edu.neu.android.mhealth.uscteensver1.views.GraphView;

public class GraphDrawer extends BaseThread {
    // default idle time
    protected final static int DEFAULT_IDLE_TIME = 10;
    // normal idle time
    protected final static int NORMAL_IDLE_TIME = 5000;
    // longest active time
    protected final static int MAX_ACTIVE_TIME = 10000;
    // the holder of the SurfaceView
    protected SurfaceHolder mHolder = null;
    // the app surface view
    protected GraphView mView = null;
    // the app page to draw
    protected AppPage mPage = null;
    // flag to indicate whether the drawer should be paused
    protected AtomicBoolean mPause = new AtomicBoolean(false);
    // for pause synchronization
    protected AtomicBoolean mPaused = new AtomicBoolean(false);
 // accumulated working time
    protected int mWorkTime = 0;
    // idle time after each drawing
    protected int mIdleTime = DEFAULT_IDLE_TIME;    
    // object for synchronization
    protected final static Object sLock = new Object(); 

    public GraphDrawer(GraphView view, AppPage page, Handler handler) {
        mView   = view;
        mPage   = page;
        mHolder = view.getHolder();
        setHandler(handler);
    }

    public void setPage(AppPage page) {
        synchronized (mHolder) {
            mPage = page;
        }
    }

    public void pause(boolean pause) {
        mPause.set(pause);
        interrupt();
        // wait until the thread is paused
        while (mPause.get() && !mPaused.get()) {
            if (!mRun) {
                mPause.set(false);
                break;
            }

            try {
                sleep(10);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    
    @Override
    public void interrupt() {
    	super.interrupt();
    	synchronized (sLock) {
    		mWorkTime = 0;
    		mIdleTime = DEFAULT_IDLE_TIME;
    	}    	
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
                    wait(mIdleTime);                                        
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            
            synchronized (sLock) {
            	mWorkTime += mIdleTime;
            	if (mWorkTime > MAX_ACTIVE_TIME) {
            		mIdleTime = NORMAL_IDLE_TIME;
                }
            }

            // handle pause logic
            while (mPause.get()) {
                mPaused.set(true);
                try {
                    sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (!mRun) {
                    mPause.set(false);
                    break;
                }
            }
            mPaused.set(false);
        }

        super.run();
    } // end of run

}
