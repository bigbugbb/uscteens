package edu.neu.android.mhealth.uscteensver1.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebView;

public class RewardView extends WebView {
	protected String mUrl = "file:///android_asset/html/test.html";

	public RewardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setVisibility(View.GONE);	
		
        getSettings().setJavaScriptEnabled(true);
        setScrollBarStyle(0);
//        setWebViewClient(new WebViewClient() {
//            public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
//            	loadurl(view, url);
//                return true;
//            }
//        });	
//        loadUrl(mUrl);
        new Thread() {
        	public void run(){        		
        		loadUrl(mUrl);
        	}
	    }.start();
	}

//	public void loadurl(final WebView view, final String url) {
//    	new Thread() {
//        	public void run(){        		
//        		view.loadUrl(mUrl);
//        	}
//	    }.start();
//	}
	
//	@Override
//	protected void onDraw(Canvas canvas) {
//		canvas.drawColor(Color.argb(0xff, 0xcc, 0xcc, 0xcc));
//	}

}
