package edu.neu.android.mhealth.uscteensver1;

import java.util.List;

import edu.neu.android.mhealth.uscteensver1.data.DataSource;
import edu.neu.android.mhealth.uscteensver1.ui.BackgroundWeekday;
import edu.neu.android.mhealth.uscteensver1.ui.ButtonArrow;
import edu.neu.android.mhealth.uscteensver1.ui.ButtonReturn;
import edu.neu.android.mhealth.uscteensver1.ui.ListView;
import edu.neu.android.mhealth.uscteensver1.ui.ListView.OnListViewScrollingListener;
import edu.neu.android.mhealth.uscteensver1.ui.ListViewWeek;
import edu.neu.android.mhealth.uscteensver1.ui.CustomButton.OnClickListener;
import edu.neu.android.mhealth.uscteensver1.ui.ListView.ListItem;
import edu.neu.android.mhealth.uscteensver1.ui.ListView.OnItemClickListener;
import edu.neu.android.mhealth.uscteensver1.ui.ListView.OnReachedEndListener;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


public class WeekdayPage extends AppPage implements OnClickListener,
													OnReachedEndListener,
													OnItemClickListener,
													OnListViewScrollingListener {
	
	protected BackgroundWeekday mBackground = null;
	protected ListViewWeek      mListViewWeek1 = null;
	protected ListViewWeek      mListViewWeek2 = null;
	protected ButtonArrow		mArrowLeftUp  = null;
	protected ButtonArrow		mArrowRightUp = null;
	protected ButtonArrow		mArrowLeftBottom  = null;
	protected ButtonArrow		mArrowRightBottom = null;
	protected ButtonReturn		mBtnReturn  = null;
	
	protected View mView = null;

	protected WeekdayPage(Context context, View view, Handler handler) {
		super(context, handler);
		mView = view;
		DataSource.getInstance(context).loadActList(1);
		DataSource.getInstance(context).loadActList(2);
		load();
	}
	
	public List<AppObject> load() {
		AppScale appScale = AppScale.getInstance();
		// create game objects
		if (mBackground == null) {
			mBackground = new BackgroundWeekday(mContext.getResources());			
			mObjects.add(mBackground);			
		}
		if (mListViewWeek1 == null) {
			mListViewWeek1 = new ListViewWeek(mContext.getResources(), 1);
			mObjects.add(mListViewWeek1);	
			mListViewWeek1.setX(0);
			mListViewWeek1.setY(appScale.doScaleH(178 + mListViewWeek1.getBorderWidth()));
			mListViewWeek1.setOnReachedEndListener(this);
			mListViewWeek1.setOnItemClickListener(this);
			mListViewWeek1.setOnListViewScrollingListener(this);
		}
		if (mListViewWeek2 == null) {
			mListViewWeek2 = new ListViewWeek(mContext.getResources(), 2);
			mObjects.add(mListViewWeek2);
			mListViewWeek2.setX(appScale.doScaleW(643));
			mListViewWeek2.setY(appScale.doScaleH(178 + mListViewWeek2.getBorderWidth()));
			mListViewWeek2.setOnReachedEndListener(this);
			mListViewWeek2.setOnItemClickListener(this);
			mListViewWeek2.setOnListViewScrollingListener(this);
		}
		if (mArrowLeftUp == null) {
			mArrowLeftUp = new ButtonArrow(mContext.getResources());
			mObjects.add(mArrowLeftUp);
			mArrowLeftUp.setOnClickListener(this);
			mArrowLeftUp.setX(0);
			mArrowLeftUp.setY(appScale.doScaleH(128));
			mArrowLeftUp.setVisible(false);
			mArrowLeftUp.alignCenter(false);
			mArrowLeftUp.changeArrowDir(false);
		}
		if (mArrowRightUp == null) {
			mArrowRightUp = new ButtonArrow(mContext.getResources());
			mObjects.add(mArrowRightUp);
			mArrowRightUp.setOnClickListener(this);
			mArrowRightUp.setX(appScale.doScaleW(640));
			mArrowRightUp.setY(appScale.doScaleH(128));	
			mArrowRightUp.setVisible(false);
			mArrowRightUp.alignCenter(false);
			mArrowRightUp.changeArrowDir(false);
		}
		if (mArrowLeftBottom == null) {
			mArrowLeftBottom = new ButtonArrow(mContext.getResources());
			mObjects.add(mArrowLeftBottom);
			mArrowLeftBottom.setOnClickListener(this);
			mArrowLeftBottom.setX(0);
			mArrowLeftBottom.setY(appScale.doScaleH(670));
			mArrowLeftBottom.alignCenter(false);
		}
		if (mArrowRightBottom == null) {
			mArrowRightBottom = new ButtonArrow(mContext.getResources());
			mObjects.add(mArrowRightBottom);
			mArrowRightBottom.setOnClickListener(this);
			mArrowRightBottom.setX(appScale.doScaleW(640));
			mArrowRightBottom.setY(appScale.doScaleH(670));	
			mArrowRightBottom.alignCenter(false);
		}
		// order by Z
		orderByZ(mObjects);
		
		return mObjects;
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
	protected void onSizeChanged(int width, int height) {		
		super.onSizeChanged(width, height);
	}

	@Override
	protected void onDraw(Canvas c) {
		for (AppObject obj : mObjects) {
			obj.onDraw(c);
		}
	}

	@Override
	public void onReachedEnd(ListView view, boolean top, boolean left) { // ignore left here
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
		if (li.getItemImage() == R.drawable.lock) {
			return;
		}
		
		Message msg = mHandler.obtainMessage();
		msg.obj  = Integer.valueOf(view == mListViewWeek1 ? posn + 1 : posn + 8);
		msg.what = AppCmd.WEEKDAY;
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
