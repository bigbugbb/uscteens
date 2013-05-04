package edu.neu.android.mhealth.uscteensver1.pages;

import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import edu.neu.android.mhealth.uscteensver1.USCTeensGlobals;
import edu.neu.android.mhealth.uscteensver1.extra.Reward;
import edu.neu.android.mhealth.uscteensver1.extra.RewardManager;
import edu.neu.android.mhealth.uscteensver1.ui.DoneButton;
import edu.neu.android.mhealth.uscteensver1.ui.OnClickListener;
import edu.neu.android.mhealth.uscteensver1.ui.RewardBackground;
import edu.neu.android.mhealth.uscteensver1.ui.RewardButton;
import edu.neu.android.mhealth.uscteensver1.views.RewardView;
import edu.neu.android.wocketslib.support.DataStorage;

public class RewardPage extends AppPage implements OnClickListener {
	
	protected RewardBackground mBackground = null;
	protected DoneButton   nBtnDone   = null;
	protected RewardButton mBtnReward = null;
	protected RewardView  mRewardView = null;
	
	protected final static int BAR    = 0;
	protected final static int BKGND  = 1;
	protected final static int DONE   = 2;
	protected final static int REWARD = 3;
	
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
		if (nBtnDone == null) {
			nBtnDone = new DoneButton(mContext.getResources());
			mObjects.add(nBtnDone);
			nBtnDone.setID(DONE);
			nBtnDone.setOnClickListener(this);
		}
		if (mBtnReward == null) {
			mBtnReward = new RewardButton(mContext.getResources());
			mObjects.add(mBtnReward);
			mBtnReward.setID(REWARD);
			mBtnReward.setOnClickListener(this);
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
		
		String selctedDate = DataStorage.GetValueString(mContext, USCTeensGlobals.CURRENT_SELECTED_DATE, "2013-01-01");
		mReward = RewardManager.getReward(selctedDate);
		if (mReward == null) {
			mBtnReward.setEnable(false);
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
