package edu.neu.android.mhealth.uscteensver1;

import java.util.List;

import edu.neu.android.mhealth.uscteensver1.ui.BackgroundWeekday;
import edu.neu.android.mhealth.uscteensver1.ui.ButtonArrow;
import edu.neu.android.mhealth.uscteensver1.ui.ButtonReturn;
import edu.neu.android.mhealth.uscteensver1.ui.ListView;
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
													OnItemClickListener {
	
	protected BackgroundWeekday mBackground = null;
	protected ListViewWeek      mListViewWeek1 = null;
	protected ListViewWeek      mListViewWeek2 = null;
	protected ButtonArrow		mArrowLeft  = null;
	protected ButtonArrow		mArrowRight = null;
	protected ButtonReturn		mBtnReturn  = null;
	
	protected View mView = null;

	protected WeekdayPage(Context context, View view, Handler handler) {
		super(context, handler);
		mView = view;
		load();
	}
	
	public List<AppObject> load() {
		// create game objects
		if (mBackground == null) {
			mBackground = new BackgroundWeekday(mContext.getResources());			
			mObjects.add(mBackground);			
		}
		if (mListViewWeek1 == null) {
			mListViewWeek1 = new ListViewWeek(mContext.getResources());
			mObjects.add(mListViewWeek1);	
			mListViewWeek1.setX(0);
			mListViewWeek1.setY(128);
			mListViewWeek1.setOnReachedEndListener(this);
			mListViewWeek1.setOnItemClickListener(this);
		}
		if (mListViewWeek2 == null) {
			mListViewWeek2 = new ListViewWeek(mContext.getResources());
			mObjects.add(mListViewWeek2);
			mListViewWeek2.setX(643);
			mListViewWeek2.setY(128);
			mListViewWeek2.setOnReachedEndListener(this);
			mListViewWeek2.setOnItemClickListener(this);
		}
		if (mArrowLeft == null) {
			mArrowLeft = new ButtonArrow(mContext.getResources());
			mObjects.add(mArrowLeft);
			mArrowLeft.setOnClickListener(this);
			mArrowLeft.setX(0);
			mArrowLeft.setY(620);
		}
		if (mArrowRight == null) {
			mArrowRight = new ButtonArrow(mContext.getResources());
			mObjects.add(mArrowRight);
			mArrowRight.setOnClickListener(this);
			mArrowRight.setX(640);
			mArrowRight.setY(620);		
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
	public void onClick(AppObject obj) {
//		switch (obj.getID()) {		
//		case BEGIN:
//			Message msg = mHandler.obtainMessage();     	
//	        msg.what = AppCmd.BEGIN;
//	        mHandler.sendMessage(msg);
//			break;
//		default:
//			break;
//		}
	}

	@Override
	public void onReachedEnd(ListView view, boolean top, boolean left) { // ignore left here
		if (top) {
			if (view == mListViewWeek1) {				
				mListViewWeek1.setPosn(0, 128);				
				mArrowLeft.setX(0);
				mArrowLeft.setY(620);
				mArrowLeft.changeArrowDir(true);
			} else {
				mListViewWeek2.setPosn(643, 128);
				mListViewWeek2.setY(128);
				mArrowRight.setX(640);
				mArrowRight.setY(620);
				mArrowRight.changeArrowDir(true);
			}
		} else {
			if (view == mListViewWeek1) {
				mListViewWeek1.setPosn(0, 228);				
				mArrowLeft.setX(0);
				mArrowLeft.setY(128);
				mArrowLeft.changeArrowDir(false);
			} else {
				mListViewWeek2.setPosn(643, 228);				
				mArrowRight.setX(640);
				mArrowRight.setY(128);
				mArrowRight.changeArrowDir(false);
			}
		}
	}

	@Override
	public void onItemClicked(ListView view, int posn) {
		Message msg = mHandler.obtainMessage();
		msg.obj  = Integer.valueOf(view == mListViewWeek1 ? posn + 1 : posn + 8);
		msg.what = AppCmd.WEEKDAY;
		mHandler.sendMessage(msg);
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
}
