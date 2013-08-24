package edu.neu.android.mhealth.uscteensver1.threads;

import android.graphics.Canvas;
import android.os.Handler;
import android.view.SurfaceHolder;

import edu.neu.android.mhealth.uscteensver1.pages.AppPage;
import edu.neu.android.mhealth.uscteensver1.views.GraphView;

public class GraphDrawer extends BaseThread {
    // default idle time
    protected final static int DEFAULT_IDLE_TIME = 10;
    // the holder of the SurfaceView
    protected SurfaceHolder mHolder = null;
    // the app surface view
    protected GraphView mView = null;
    // the app page to draw
    protected AppPage mPage = null;
    // flag to indicate whether the drawer should be paused
    protected boolean mPause = false;
    // for pause synchronization
    protected boolean mPaused = false;
    // idle time after each drawing
    protected int mIdleTime = DEFAULT_IDLE_TIME;

    public GraphDrawer(GraphView view, AppPage page, Handler handler) {
        mView = view;
        mPage = page;
        mHolder = view.getHolder();
        setHandler(handler);
    }

    public void setPage(AppPage page) {
        synchronized (mHolder) {
            mPage = page;
        }
    }

    public void setIdleTime(int idleTime) {
        if (idleTime < 1 || idleTime > 1000) {
            idleTime = DEFAULT_IDLE_TIME;
        }
        mIdleTime = idleTime;
    }

    public void pause(boolean pause) {
        mPause = pause;
        // wait until the thread is paused
        while (mPause && !mPaused) {
            if (!mRun) {
                mPause = false;
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

            // handle pause logic
            while (mPause) {
                mPaused = true;
                try {
                    sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (!mRun) {
                    mPause = false;
                    break;
                }
            }
            mPaused = false;
        }

        super.run();
    } // end of run

}
