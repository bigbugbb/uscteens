package edu.neu.android.mhealth.uscteensver1.main;

import java.util.List;

import edu.neu.android.mhealth.uscteensver1.ui.BackgroundHome;
import edu.neu.android.mhealth.uscteensver1.ui.ButtonBegin;
import edu.neu.android.mhealth.uscteensver1.ui.HomeTitle;
import edu.neu.android.mhealth.uscteensver1.ui.CustomButton.OnClickListener;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.View;

public class HomePage extends AppPage implements OnClickListener {
	
	protected HomeTitle		 mTitle      = null;
	protected BackgroundHome mBackground = null;
	protected ButtonBegin	 mBtnBegin   = null;
	protected final static int TITLE = 0;
	protected final static int BKGND = 1;
	protected final static int BEGIN = 2;
	
	protected View mView = null;

	protected HomePage(Context context, View view, Handler handler) {
		super(context, handler);
		mView = view;		
	}
	
	public List<AppObject> load() {
		// create game objects
		if (mBackground == null) {
			mBackground = new BackgroundHome(mContext.getResources());			
			mObjects.add(mBackground);
			mBackground.setID(BKGND);
		}
		if (mTitle == null) {
			mTitle = new HomeTitle(mContext.getResources());
			mObjects.add(mTitle);
			mTitle.setID(TITLE);
		}
		if (mBtnBegin == null) {
			mBtnBegin = new ButtonBegin(mContext.getResources());
			mObjects.add(mBtnBegin);
			mBtnBegin.setID(BEGIN);
			mBtnBegin.setOnClickListener(this);
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
	protected void onSizeChanged(int width, int height) {
		// TODO Auto-generated method stub
		super.onSizeChanged(width, height);
	}

	@Override
	public void onClick(AppObject obj) {
		switch (obj.getID()) {		
		case BEGIN:
			Message msg = mHandler.obtainMessage();     	
	        msg.what = AppCmd.BEGIN;
	        mHandler.sendMessage(msg);
			break;
		default:
			break;
		}
	}

}
