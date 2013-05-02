package edu.neu.android.mhealth.uscteensver1.actions;

import java.io.Serializable;

import android.graphics.Bitmap;

import com.google.gson.Gson;

public class Action implements Serializable {	
	private static final long serialVersionUID = -6124174446308636095L;
	protected String  mActID;
	protected String  mActName;
	protected String  mIcoName;
	protected Bitmap  mActImage;
	
	public Action(String actID, String actName, String icoName, Bitmap actImage) {
		mActID    = actID.trim();
		mActName  = actName.trim();
		mIcoName  = icoName.trim();
		mActImage = actImage;
	}
	
	public void setActionID(String actID) {
		mActID = actID;
	}
	
	public String getActionID() {
		return mActID;
	}
	
	public void setActionName(String actName) {
		mActName = actName;
	}
	
	public String getActionName() {
		return mActName;
	}	
	
	public void setIconName(String icoName) {
		mIcoName = icoName;
	}
	
	public String getIconName() {
		return mIcoName;
	}
	
	public void setActionImage(Bitmap actImage) {
		mActImage = actImage;
	}
	
	public Bitmap getActionImage() {
		return mActImage;
	}
	
	public void clear() {
		if (mActImage != null) {
			mActImage.recycle();
			mActImage = null;
		}
	}
	
	public static String toJSON(Action action) {
		Gson gson = new Gson(); 
		return gson.toJson(action);
	}
	
	public static Action fromJSON(String aJSONString) {
		Gson gson = new Gson();
		return gson.fromJson(aJSONString, Action.class);
	}
}
