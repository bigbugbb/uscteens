package edu.neu.android.mhealth.uscteensver1.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;

public class RewardView extends WebView {
	protected final static String DEFAULT_URL = "file:///android_asset/rewards/default.html";

	public RewardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setVisibility(View.GONE);
		
        getSettings().setJavaScriptEnabled(true);
        setScrollBarStyle(0);

        new Thread() {
        	public void run() {        		
        		loadUrl(DEFAULT_URL);
        	}
	    }.start();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		return super.onTouchEvent(event);
	}

	public void loadRewardUrl(final String rewardUrl) {
		new Thread() {
        	public void run(){        		
        		loadUrl(rewardUrl);
        	}
	    }.start();
	}
}
