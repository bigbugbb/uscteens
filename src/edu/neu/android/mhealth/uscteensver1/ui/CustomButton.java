package edu.neu.android.mhealth.uscteensver1.ui;


import java.util.ArrayList;
import java.util.List;

import android.content.res.Resources;
import android.graphics.Bitmap;
import edu.neu.android.mhealth.uscteensver1.main.AppObject;

public class CustomButton extends AppObject {

	protected Object mUserData  = null;
	protected boolean mEnable = true;
	protected int mCanvasWidth  = 0;
	protected int mCanvasHeight = 0;
	protected OnClickListener mListener = null;

	public CustomButton(Resources res) {
		super(res);	
		mKind = BUTTON;
		mZOrder = ZOrders.BUTTON;
	}
	
	public void setEnable(boolean enable) {
		mEnable = enable;
	}
	
	public void setUserData(Object data) {
		mUserData = data;
	}
	
	public Object getUserData() {
		return mUserData;
	}
	
	public void setOnClickListener(OnClickListener listener) {
		mListener = listener;
	}	
	
	public interface OnClickListener {
		void onClick(AppObject obj);
	}

	@Override
	public boolean contains(float x, float y) {
		if (!mVisible || !mEnable) {
			return false;
		}
		return super.contains(x, y);
	}
}
