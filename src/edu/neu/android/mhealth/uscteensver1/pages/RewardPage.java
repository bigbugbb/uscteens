package edu.neu.android.mhealth.uscteensver1.pages;

import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import edu.neu.android.mhealth.uscteensver1.ui.OnClickListener;
import edu.neu.android.mhealth.uscteensver1.ui.RewardButton;
import edu.neu.android.mhealth.uscteensver1.ui.RewardBackground;

public class RewardPage extends AppPage implements OnClickListener {
	
	protected RewardBackground mBackground = null;	
	protected RewardButton 	mBtnReward  = null;
	protected final static int BAR    = 0;
	protected final static int BKGND  = 1;
	protected final static int REWARD = 2;
	
	protected View mView = null;

	public RewardPage(Context context, View view, Handler handler) {
		super(context, handler);
		mView = view;		
	}
	
	public List<AppObject> load() {
		// create game objects
		if (mBackground == null) {
			mBackground = new RewardBackground(mContext.getResources());			
			mObjects.add(mBackground);
			mBackground.setID(BKGND);
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
		switch (obj.getID()) {		
		case REWARD:
			Message msg = mHandler.obtainMessage();     	
	        msg.what = AppCmd.REWARD;
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
