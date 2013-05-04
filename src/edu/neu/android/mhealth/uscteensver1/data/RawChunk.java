package edu.neu.android.mhealth.uscteensver1.data;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.neu.android.mhealth.uscteensver1.USCTeensGlobals;
import edu.neu.android.mhealth.uscteensver1.extra.Action;

// chunk data from XML in direct
class RawChunk implements Serializable {
	private static final long serialVersionUID = -3769827996533096658L;	
	// data read directly from XML
	protected String mStartDate;
	protected String mStopDate;
	protected String mCreateTime;
	protected String mModifyTime;
	protected Action mAction;
	
	/*
	 * seconds from the beginning of a day
	 */	
	public RawChunk(String date, int startTime, int stopTime) {				
		mStartDate  = date + " " + getStringTimeFromSecond(startTime);
		mStopDate   = date + " " + getStringTimeFromSecond(stopTime);				
		String now  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
		mCreateTime = now;
		mModifyTime = now;
		mAction     = Action.createUnlabelledAction();
	}
	
	public RawChunk(String startDate, String stopDate, 
			Action action, String createTime, String modifyTime) {
		mStartDate  = startDate;
		mStopDate   = stopDate;		
		mCreateTime = createTime;
		mModifyTime = modifyTime;
		mAction     = action;
	}
	
	public boolean isLabelled() {
		if (mAction == null) {
			return false;
		}
		return !mAction.getActionID().equals(USCTeensGlobals.UNLABELLED_GUID);
	}
	
	public boolean isModified() {
		return mCreateTime.compareTo(mModifyTime) != 0;
	}
	
	public void setAction(Action action) {
		mAction = action;
	}
	
	public Action getAction() {
		return mAction;
	}

	protected String getStringTimeFromSecond(int secInDay) {
		StringBuilder sb = new StringBuilder();
		
		int hour = secInDay / 3600;
		sb.append(hour > 9 ? "" : "0");
		sb.append(hour);
		secInDay -= hour * 3600;
		int minute = secInDay / 60;
		sb.append(minute > 9 ? ":" : ":0");
		sb.append(minute);
		int second = secInDay - minute * 60;
		sb.append(second > 9 ? ":" : ":0");
		sb.append(second);
		// ignore the millisecond
		sb.append(".000");
		
		return sb.toString();
	}
	
	public int getStartTime() {
		return getTimeInSecond(mStartDate);
	}
	
	public String getStartTimeInString() {
		return mStartDate;
	}
	
	public int getStopTime() {
		return getTimeInSecond(mStopDate);
	}

	public String getStopTimeInString() {
		return mStopDate;
	}
	
	public void setStartTime(int startTime) {
		mStartDate = getStringTimeFromSecond(startTime);
	}
	
	public void setStartTime(String startTime) {
		mStartDate = startTime;
	}

	public void setStopTime(int stopTime) {
		mStopDate = getStringTimeFromSecond(stopTime);		
	}
	
	public void setStopTime(String stopTime) {
		mStopDate = stopTime;
	}
	
	protected int getTimeInSecond(String time) {	
		// 2013-02-20 03:00:00.000
		String[] result = time.split(" ");
		String[] times  = result[result.length - 1].split(":");
		
		return Integer.parseInt(times[0]) * 3600 + // hour 
			   Integer.parseInt(times[1]) * 60 +   // minute
			   (int) Float.parseFloat(times[2]);   // second
	}
	
	public String getCreateTime() {
		return mCreateTime;
	}
	
	public String getModifyTime() {
		return mModifyTime;
	}
}
