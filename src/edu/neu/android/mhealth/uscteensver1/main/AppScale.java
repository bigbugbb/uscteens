package edu.neu.android.mhealth.uscteensver1.main;

public class AppScale {
	
	protected float mScaleW = 1;
	protected float mScaleH = 1;
	protected float mScaleT = 1;
	
	protected float mExpectW = 1280;
	protected float mExpectH = 720;
	
	protected static boolean sCreated = false;
	protected static AppScale sUiScale = null;
	
	static public AppScale getInstance() {
		if (!sCreated) {
			sUiScale = new AppScale();
			sCreated = true;
		}
		return sUiScale;
	}
	
	public float doScaleW(float srcWidth) {
		return srcWidth * mScaleW;
	}
	
	public float doScaleH(float srcHeight) {
		return srcHeight * mScaleH;
	}
	
	public float doScaleT(float textSize) {
		return textSize * mScaleT;
	}
	
	public float getScaleW() {
		return mScaleW;
	}
	
	public float getScaleH() {
		return mScaleH;
	}
	
	public void setScale(float sw, float sh) {
		mScaleW = sw;
		mScaleH = sh;
	}
	
	public void calcScale(float baseW, float baseH, float expectW, float expectH) {				
		mExpectW = expectW;
		mExpectH = expectH;
		calcScale(baseW, baseH);
	}
	
	public void calcScale(float baseW, float baseH) {
		baseW = Math.max(baseW, baseH);
		baseH = Math.min(baseW, baseH);
		mScaleW = baseW / mExpectW;
		mScaleH = baseH / mExpectH;
		mScaleT = mScaleH;
	}
}
