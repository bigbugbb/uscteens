package edu.neu.android.mhealth.uscteensver1.ui;


import android.content.res.Resources;
import edu.neu.android.mhealth.uscteensver1.pages.AppObject;

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

	@Override
	public boolean contains(float x, float y) {
		if (!mVisible || !mEnable) {
			return false;
		}
		return super.contains(x, y);
	}
}
