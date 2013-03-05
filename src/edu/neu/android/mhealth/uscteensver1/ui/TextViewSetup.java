package edu.neu.android.mhealth.uscteensver1.ui;

import edu.neu.android.mhealth.uscteensver1.R;
import edu.neu.android.mhealth.uscteensver1.USCTeensGlobals;
import edu.neu.android.mhealth.uscteensver1.data.DataSource;
import edu.neu.android.wocketslib.support.DataStorage;
import edu.neu.android.wocketslib.utils.FileHelper;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.Align;

public class TextViewSetup extends TextView {
	
	//private String mText;

	public TextViewSetup(Resources res) {
		super(res);
		mPaintTxt.setColor(Color.RED);
		mPaintTxt.setTextAlign(Align.CENTER);
		mPaintBoard.setStrokeWidth(3);
		setVisible(false);
		//mText = res.getString(R.string.need_setup);		
//		if (FileHelper.isFileExists(DataSource.PATH_PREFIX + "config.xml")) {
//			setVisible(false);
//		}
	}

	@Override
	public void onSizeChanged(int width, int height) {		
		mCanvasWidth  = width;
		mCanvasHeight = height;
		
		mRect.left   = width * 0.13f;
		mRect.right  = width * 0.87f;
		mRect.top    = height * 0.6f;
		mRect.bottom = height * 0.86f;
		
		mX = mRect.left;
		mY = mRect.top;
		mWidth  = mRect.width();
		mHeight = mRect.height();
	}

	@Override
	public void onDraw(Canvas c) {
		if (!mVisible) {
			return;
		}
		c.drawRoundRect(mRect, 16, 16, mPaintBkg);
		c.drawRoundRect(mRect, 16, 16, mPaintBoard);
		 
		c.drawText("This phone needs to be setup for", mCanvasWidth / 2, 
				mRect.top + sAppScale.doScaleT(72), mPaintTxt);
		c.drawText("the Teen Activity Game to start!", mCanvasWidth / 2, 
				mRect.top + sAppScale.doScaleT(143), mPaintTxt);
	}
}
