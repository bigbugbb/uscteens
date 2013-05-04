package edu.neu.android.mhealth.uscteensver1.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;

public class RewardView extends WebView {
	protected String mUrl = "file:///android_asset/rewards/test.html";

	public RewardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setVisibility(View.GONE);	
		
        getSettings().setJavaScriptEnabled(true);
        setScrollBarStyle(0);

        new Thread() {
        	public void run(){        		
        		loadUrl(mUrl);
        	}
	    }.start();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		return super.onTouchEvent(event);
	}

}
