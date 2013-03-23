package edu.neu.android.mhealth.uscteensver1.data;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.neu.android.mhealth.uscteensver1.USCTeensGlobals;

// chunk data from XML in direct
class RawChunk implements Serializable {
	private static final long serialVersionUID = -3769827996533096658L;
	
	private static final int UNLABELLED = -1;
	// data read directly from xml
	protected String mStartDate;
	protected String mStopDate;
	protected String mCreateTime;
	protected String mModifyTime;
	protected String mActivity;
	
	protected int mActivityID;
	
	/*
	 * seconds from the beginning of a day
	 */	
	public RawChunk(String date, int startTime, int stopTime) {				
		mStartDate  = date + " " + getStringTimeFromSecond(startTime);
		mStopDate   = date + " " + getStringTimeFromSecond(stopTime);
		mActivity   = "UNLABELLED";
		mActivityID = UNLABELLED;
		
		String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
		mCreateTime = now;
		mModifyTime = now;
	}
	
	public RawChunk(String startDate, String stopDate, 
			String activity, String createTime, String modifyTime) {
		mStartDate  = startDate;
		mStopDate   = stopDate;		
		mCreateTime = createTime;
		mModifyTime = modifyTime;
		setActivity(activity);
	}
	
	public boolean isLabelled() {
		return mActivityID != UNLABELLED;
	}
	
	public boolean isModified() {
		return mCreateTime.compareTo(mModifyTime) != 0;
	}
	
	public int getActivityID() {
		return mActivityID;
	}
	
	public String getActivity() {
		return mActivity;
	}
	
	public void setActivity(String activity) {
		mActivity = activity;
		if (mActivity.compareToIgnoreCase("UNLABELLED") == 0) {
			mActivityID = -1;
		} else {
			for (int i = 0; i < USCTeensGlobals.ACTION_NAMES.length; ++i) {
				if (mActivity.compareToIgnoreCase(USCTeensGlobals.ACTION_NAMES[i]) == 0) {
					mActivityID = i;
					break;
				}
			}
		}
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
	
	public String getActivityName() {
		return mActivity;
	}
	
	public String getCreateTime() {
		return mCreateTime;
	}
	
	public String getModifyTime() {
		return mModifyTime;
	}
}
