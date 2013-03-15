package edu.neu.android.mhealth.uscteensver1.data;

import edu.neu.android.mhealth.uscteensver1.USCTeensGlobals;

// chunk data from XML in direct
class RawChunk {
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
	public RawChunk(String date, int startSecond, int stopSecond) {
		mStartDate  = date + " " + getStringTimeFromSecond(startSecond);
		mStopDate   = date + " " + getStringTimeFromSecond(stopSecond);
		mActivity   = "UNLABELLED";
		mActivityID = -1;
		mCreateTime = date;
		mModifyTime = date;
	}
	
	public RawChunk(String startDate, String stopDate, 
			String activity, String createTime, String modifyTime) {
		mStartDate  = startDate;
		mStopDate   = stopDate;
		mActivity   = activity;
		mCreateTime = createTime;
		mModifyTime = modifyTime;
		
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
	
	public int getActivityID() {
		return mActivityID;
	}

	public String getStringTimeFromSecond(int secInDay) {
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
	
	public int getStopTime() {
		return getTimeInSecond(mStopDate);
	}
	
	protected int getTimeInSecond(String time) {	
		// 2013-02-20 03:00:00.000
		String[] result = time.split(" ");
		String[] times  = result[result.length - 1].split(":");
		
		return Integer.parseInt(times[0]) * 3600 + // hour 
			   Integer.parseInt(times[1]) * 60;    // minute
	}
	
	public String getActivityName() {
		return mActivity;
	}
}
