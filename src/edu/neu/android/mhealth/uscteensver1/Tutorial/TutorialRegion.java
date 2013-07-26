package edu.neu.android.mhealth.uscteensver1.tutorial;

import java.io.Serializable;

import android.graphics.RectF;

/*
 * User can tap the region area to switch to another tutorial card 
 * indicated by the target id, which is also the tutorial card id.
 */
public class TutorialRegion implements Serializable {

	private static final String TAG = "TutorialRegion";
	private static final long serialVersionUID = 6449582139173269271L;
	
	protected RectF  mRect;
	protected String mTargetID;

	public TutorialRegion(RectF rect, String targetID) {
		mRect     = rect;
		mTargetID = targetID;
	}
	
	public TutorialRegion(float left, float top, float right, float bottom, String targetID) {
		mRect     = new RectF(left, top, right, bottom);
		mTargetID = targetID;
	}
	
	public void setRegionArea(RectF rect) {
		mRect = rect;
	}
	
	public RectF getRegionArea() {
		return mRect;
	}
	
	public void setTargetID(String targetID) {
		mTargetID = targetID;
	}
	
	public String getTargetID() {
		return mTargetID;
	}
}
