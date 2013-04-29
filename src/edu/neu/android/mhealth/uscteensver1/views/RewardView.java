package edu.neu.android.mhealth.uscteensver1.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebView;

public class RewardView extends WebView {

	public RewardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		setVisibility(View.GONE);
		loadUrl("www.google.com");
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawColor(Color.argb(0xff, 0xcc, 0xcc, 0xcc));
	}

}
