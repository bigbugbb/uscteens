package edu.neu.android.mhealth.uscteensver1.pages;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import edu.neu.android.mhealth.uscteensver1.R;
import edu.neu.android.mhealth.uscteensver1.TeensGlobals;
import edu.neu.android.mhealth.uscteensver1.ui.ArrowButton;
import edu.neu.android.mhealth.uscteensver1.ui.DateBackground;
import edu.neu.android.mhealth.uscteensver1.ui.DateListView;
import edu.neu.android.mhealth.uscteensver1.ui.ListView;
import edu.neu.android.mhealth.uscteensver1.ui.ListView.ListItem;
import edu.neu.android.mhealth.uscteensver1.ui.ListView.OnBoundaryListener;
import edu.neu.android.mhealth.uscteensver1.ui.ListView.OnItemClickListener;
import edu.neu.android.mhealth.uscteensver1.ui.ListView.OnListViewScrollingListener;
import edu.neu.android.wocketslib.support.DataStorage;
import edu.neu.android.wocketslib.utils.WeekdayHelper;


public class DatePage extends AppPage implements edu.neu.android.mhealth.uscteensver1.ui.OnClickListener,
        OnBoundaryListener,
        OnItemClickListener,
        OnListViewScrollingListener {

    private final static String TAG = "WeekdayPage";
    protected DateBackground mBackground = null;
    protected DateListView mListViewWeek1 = null;
    protected DateListView mListViewWeek2 = null;
    protected ArrowButton mArrowLeftUp = null;
    protected ArrowButton mArrowRightUp = null;
    protected ArrowButton mArrowLeftBottom = null;
    protected ArrowButton mArrowRightBottom = null;

    protected View mView = null;

    public DatePage(Context context, View view, Handler handler) {
        super(context, handler);
        mView = view;
    }

    public List<AppObject> load() {
        String startDate = DataStorage.getStartDate(mContext, "");
        // create game objects
        if (mBackground == null) {
            mBackground = new DateBackground(mContext.getResources());
            mObjects.add(mBackground);
        }
        if (mListViewWeek1 == null) {
            mListViewWeek1 = new DateListView(mContext.getResources(), 1, startDate);
            mObjects.add(mListViewWeek1);
            mListViewWeek1.setX(0);
            mListViewWeek1.setY(AppScale.doScaleH(178 + mListViewWeek1.getBorderWidth()));
            mListViewWeek1.setOnBoundaryListener(this);
            mListViewWeek1.setOnItemClickListener(this);
            mListViewWeek1.setOnListViewScrollingListener(this);
        }
        if (mListViewWeek2 == null) {
            mListViewWeek2 = new DateListView(mContext.getResources(), 2, startDate);
            mObjects.add(mListViewWeek2);
            mListViewWeek2.setX(AppScale.doScaleW(643));
            mListViewWeek2.setY(AppScale.doScaleH(178 + mListViewWeek2.getBorderWidth()));
            mListViewWeek2.setOnBoundaryListener(this);
            mListViewWeek2.setOnItemClickListener(this);
            mListViewWeek2.setOnListViewScrollingListener(this);
        }
        if (mArrowLeftUp == null) {
            mArrowLeftUp = new ArrowButton(mContext.getResources());
            mObjects.add(mArrowLeftUp);
            mArrowLeftUp.setOnClickListener(this);
            mArrowLeftUp.setX(0);
            mArrowLeftUp.setY(AppScale.doScaleH(128));
            mArrowLeftUp.setVisible(false);
            mArrowLeftUp.alignCenter(false);
            mArrowLeftUp.changeArrowDir(false);
        }
        if (mArrowRightUp == null) {
            mArrowRightUp = new ArrowButton(mContext.getResources());
            mObjects.add(mArrowRightUp);
            mArrowRightUp.setOnClickListener(this);
            mArrowRightUp.setX(AppScale.doScaleW(640));
            mArrowRightUp.setY(AppScale.doScaleH(128));
            mArrowRightUp.setVisible(false);
            mArrowRightUp.alignCenter(false);
            mArrowRightUp.changeArrowDir(false);
        }
        if (mArrowLeftBottom == null) {
            mArrowLeftBottom = new ArrowButton(mContext.getResources());
            mObjects.add(mArrowLeftBottom);
            mArrowLeftBottom.setOnClickListener(this);
            mArrowLeftBottom.setX(0);
            mArrowLeftBottom.setY(AppScale.doScaleH(670));
            mArrowLeftBottom.alignCenter(false);
        }
        if (mArrowRightBottom == null) {
            mArrowRightBottom = new ArrowButton(mContext.getResources());
            mObjects.add(mArrowRightBottom);
            mArrowRightBottom.setOnClickListener(this);
            mArrowRightBottom.setX(AppScale.doScaleW(640));
            mArrowRightBottom.setY(AppScale.doScaleH(670));
            mArrowRightBottom.alignCenter(false);
        }
        // order by Z
        orderByZ(mObjects);

        return mObjects;
    }

    public void start() {
        //release();
        load();
        for (AppObject obj : mObjects) {
            obj.onSizeChanged(mView.getWidth(), mView.getHeight());
        }
    }

    public void resume() {
        String startDate = DataStorage.getStartDate(mContext, "");
        // get current date in String
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        String curDate = fmt.format(new Date());

        mListViewWeek1.refresh(1, startDate, curDate);
        mListViewWeek2.refresh(2, startDate, curDate);
    }

    public void stop() {
        mBackground = null;
        mListViewWeek1 = null;
        mListViewWeek2 = null;
        mArrowLeftUp = null;
        mArrowRightUp = null;
        mArrowLeftBottom = null;
        mArrowRightBottom = null;

        release();
    }

    public void release() {
        super.release();
    }

    @Override
    public void onAppEvent(AppEvent e) {
        // TODO Auto-generated method stub
        super.onAppEvent(e);
    }

    @Override
    public void onSizeChanged(int width, int height) {
        super.onSizeChanged(width, height);
    }

    @Override
    public void onDraw(Canvas c) {
        for (AppObject obj : mObjects) {
            obj.onDraw(c);
        }
    }

    @Override
    public void onBoundary(ListView view, boolean top, boolean left) { // ignore left here
        if (top) {
            if (view == mListViewWeek1) {
                mArrowLeftUp.setVisible(false);
                mArrowLeftBottom.setVisible(true);
            } else {
                mArrowRightUp.setVisible(false);
                mArrowRightBottom.setVisible(true);
            }
        } else {
            if (view == mListViewWeek1) {
                mArrowLeftUp.setVisible(true);
                mArrowLeftBottom.setVisible(false);
            } else {
                mArrowRightUp.setVisible(true);
                mArrowRightBottom.setVisible(false);
            }
        }
    }

    @Override
    public void onItemClicked(ListView view, ListItem li, int posn) {
        if (view == mListViewWeek2) {
            posn += 7;
        }
        if (li.getItemImage() == R.drawable.lock) {
            return;
        }
        if (li.getItemImage() == R.drawable.not_available) {
            String text = "You can no longer label this day. Try to label within " +
                    TeensGlobals.MAX_LABEL_WINDOW + " days of the actual day.";
            Toast.makeText(mContext, text, Toast.LENGTH_LONG).show();
            return;
        }

        // get the start date
        String startDate = DataStorage.getStartDate(mContext, "");
        if (startDate.compareTo("") == 0) {
            // possibly fail to read the configuration file
            Log.e(TAG, "Can not get start date!");
            return;
        }

        // build the new date as a String according to the current selection
        String[] split = startDate.split("-");
        Date date = new Date(Integer.parseInt(split[0]) - 1900,
                Integer.parseInt(split[1]) - 1, Integer.parseInt(split[2]));
        String strDate = WeekdayHelper.afterNDayFrom(date, posn);

        Message msg = mHandler.obtainMessage();
        msg.obj = strDate;
        msg.what = AppCmd.BEGIN_LOADING;
        mHandler.sendMessage(msg);
    }

    @Override
    public void onListViewScrolling(ListView view, int dx, int dy) {
        if (view == mListViewWeek1) {
            mArrowLeftUp.setVisible(true);
            mArrowLeftBottom.setVisible(true);
        } else {
            mArrowRightUp.setVisible(true);
            mArrowRightBottom.setVisible(true);
        }
    }

    public boolean onDown(MotionEvent e) {
        boolean ret = false;
        float x = e.getX();
        float y = e.getY();

        for (int i = mObjects.size() - 1; i >= 0; --i) {
            if (mObjects.get(i).contains(x, y)) {
                mSelObject = mObjects.get(i);
                mSelObject.setSelected(true);
                ret = mSelObject.onDown(e);
                break;
            }
        }

        return ret;
    }

    public boolean onUp(MotionEvent e) {
        boolean ret = false;

        if (mSelObject != null) {
            ret = mSelObject.onUp(e);
            mSelObject.setSelected(false);
            mSelObject = null;
        }

        return ret;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {
        boolean ret = false;

        if (mSelObject != null) {
            if (mSelObject.contains(e2.getX(), e2.getY())) {
                ret = mSelObject.onFling(e1, e2, velocityX, velocityY);
            }

            mSelObject.onCancelSelection(e2);
            mSelObject.setSelected(false);
            mSelObject = null;
        }

        return ret;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
        boolean ret = false;

        if (mSelObject != null) {
            if (mSelObject.contains(e2.getX(), e2.getY())) {
                ret = mSelObject.onScroll(e1, e2, distanceX, distanceY);
            }
        }

        return ret;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
//		if (mSelObject != null) {
//			mSelObject.onCancelSelection(e);
//			mSelObject.setSelected(false);
//			mSelObject = null;
//		}
        return false; // do nothing here
    }

    @Override
    public void onClick(AppObject obj) {
        // TODO Auto-generated method stub

    }

}
