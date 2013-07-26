package edu.neu.android.mhealth.uscteensver1.pages;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import edu.neu.android.mhealth.uscteensver1.TeensGlobals;
import edu.neu.android.mhealth.uscteensver1.extra.Reward;
import edu.neu.android.mhealth.uscteensver1.extra.RewardManager;
import edu.neu.android.mhealth.uscteensver1.ui.DoneButton;
import edu.neu.android.mhealth.uscteensver1.ui.FixButton;
import edu.neu.android.mhealth.uscteensver1.ui.OnClickListener;
import edu.neu.android.mhealth.uscteensver1.ui.RewardBackground;
import edu.neu.android.mhealth.uscteensver1.ui.RewardButton;
import edu.neu.android.mhealth.uscteensver1.views.RewardView;
import edu.neu.android.wocketslib.support.DataStorage;
import edu.neu.android.wocketslib.utils.WeekdayHelper;

public class RewardPage extends AppPage implements OnClickListener {
	
	protected RewardBackground mBackground = null;
	protected FixButton	   mBtnFix    = null;
	protected DoneButton   mBtnDone   = null;
	protected RewardButton mBtnReward = null;
	protected RewardView  mRewardView = null;
	
	protected final static int BAR    = 0;
	protected final static int BKGND  = 1;
	protected final static int FIX    = 2;
	protected final static int DONE   = 3;
	protected final static int REWARD = 4;
	
	protected View mView = null;
	protected Reward mReward = null;

	public RewardPage(Context context, View view, Handler handler) {
		super(context, handler);
		mView = view;		
	}
	
	public void bindRewardView(RewardView view) {
		mRewardView = view;
	}
	
	public List<AppObject> load() {
		// create game objects
		if (mBackground == null) {
			mBackground = new RewardBackground(mContext.getResources());			
			mObjects.add(mBackground);
			mBackground.setID(BKGND);
		}
		if (mBtnDone == null) {
			mBtnDone = new DoneButton(mContext.getResources());
			mObjects.add(mBtnDone);
			mBtnDone.setID(DONE);
			mBtnDone.setOnClickListener(this);
		}
		if (mBtnReward == null) {
			mBtnReward = new RewardButton(mContext.getResources());
			mObjects.add(mBtnReward);
			mBtnReward.setID(REWARD);
			mBtnReward.setOnClickListener(this);
		}
		if (mBtnFix == null) {
			mBtnFix = new FixButton(mContext.getResources());
			mObjects.add(mBtnFix);
			mBtnFix.setID(FIX);
			mBtnFix.setOnClickListener(this);
		}
		// order by Z
		orderByZ(mObjects);
		
		return mObjects;
	}
	
	public void start() {
		load();
		for (AppObject obj : mObjects) {
			obj.onSizeChanged(mView.getWidth(), mView.getHeight());
		}
		
		if (mRewardView != null) {
			mRewardView.setVisibility(View.VISIBLE);
		}
		
		// get days between the start date and the selected date 
		String startDate    = DataStorage.getStartDate(mContext, "");
		String selectedDate = DataStorage.GetValueString(mContext, TeensGlobals.CURRENT_SELECTED_DATE, "2013-01-01");			
	    Date aStartDate = null;
		try {
			aStartDate = new SimpleDateFormat("yyyy-MM-dd").parse(startDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		int daysAfterStarting = 0;		
		while (daysAfterStarting < 14) {
			String date = WeekdayHelper.afterNDayFrom(aStartDate, daysAfterStarting);
			if (date.compareToIgnoreCase(selectedDate) == 0) {
				break;
			}
			daysAfterStarting++;			
		} // 0-13
		
		mReward = RewardManager.getReward(daysAfterStarting + 1); // from 1-14
		if (mReward == null || mReward.getLink().equals("")) {			
			mBtnReward.setVisible(false);
			mBtnDone.setX(mWidth * 0.2f);
			mBtnFix.setX(mWidth * 0.8f - mBtnFix.getWidth());
		} else {
			mRewardView.loadUrl(mReward.getHtml());
		}
	}
	
	public void stop() {
		if (mRewardView != null) {
			mRewardView.setVisibility(View.GONE);
		}
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
		// TODO Auto-generated method stub
		super.onSizeChanged(width, height);
	}

	@Override
	public void onDraw(Canvas c) {
		c.drawColor(Color.WHITE);
		for (AppObject obj : mObjects) {
			obj.onDraw(c);
		}
	}

	@Override
	public void onClick(AppObject obj) {
		Message msg = mHandler.obtainMessage();
		switch (obj.getID()) {
		case DONE:			
			msg.what = AppCmd.DONE;
			mHandler.sendMessage(msg);
			break;
		case FIX:						
			msg.what = AppCmd.BEGIN_LOADING;
			msg.obj  = DataStorage.GetValueString(mContext, TeensGlobals.CURRENT_SELECTED_DATE, "");
			mHandler.sendMessage(msg);
			break;
		case REWARD:
	        msg.what = AppCmd.REWARD;
	        msg.obj  = (mReward == null) ? null : mReward.getLink();
	        mHandler.sendMessage(msg);
			break;
		default:
			break;
		}
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
}
