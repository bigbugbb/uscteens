package edu.neu.android.mhealth.uscteensver1.ui;


import android.content.res.Resources;
import edu.neu.android.mhealth.uscteensver1.pages.AppObject;

public abstract class CustomButton extends AppObject {

    protected Object  mUserData;
    protected boolean mEnable;
    protected String  mText;
    protected int mCanvasWidth;
    protected int mCanvasHeight;
    protected OnClickListener mListener;

    public CustomButton(Resources res) {
        super(res);
        mKind   = BUTTON;
        mZOrder = ZOrders.BUTTON;
        
        mEnable   = true;
        mUserData = null;
        mText     = "";
        
        mCanvasWidth  = 0;
        mCanvasHeight = 0;
        
        mListener = null;
    }

    public void setEnable(boolean enable) {
        mEnable = enable;
    }

    public void setUserData(Object data) {
        mUserData = data;
    }
    
    public void setText(String text) {
    	mText = text;
    }
    
    public String getText() {
    	return mText;
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
