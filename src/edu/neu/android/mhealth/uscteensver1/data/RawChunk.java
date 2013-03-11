package edu.neu.android.mhealth.uscteensver1.data;

import edu.neu.android.mhealth.uscteensver1.dialog.Actions;

public class RawChunk {
	// data read directly from xml
	protected String mStartDate;
	protected String mStopDate;
	protected String mCreateTime;
	protected String mModifyTime;
	protected String mActivity;
	
	protected int mActivityID;
	
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
			for (int i = 0; i < Actions.ACTION_NAMES.length; ++i) {
				if (mActivity.compareToIgnoreCase(Actions.ACTION_NAMES[i]) == 0) {
					mActivityID = i;
					break;
				}
			}
		}
	}
	
	public int getActivityID() {
		return mActivityID;
	}
		
	public int getStartTime() {
		// 2013-02-20 03:00:00.000
		String[] result = mStartDate.split(" ");
		String[] times  = result[result.length - 1].split(":");
		
		return Integer.parseInt(times[0]) * 3600 + // hour 
			   Integer.parseInt(times[1]) * 60;    // minute
	}
	
	public int getStopTime() {	
		// 2013-02-20 03:00:00.000
		String[] result = mStopDate.split(" ");
		String[] times  = result[result.length - 1].split(":");
		
		return Integer.parseInt(times[0]) * 3600 + // hour 
			   Integer.parseInt(times[1]) * 60;    // minute
	}
	
	public String getActivityName() {
		return mActivity;
	}
}
