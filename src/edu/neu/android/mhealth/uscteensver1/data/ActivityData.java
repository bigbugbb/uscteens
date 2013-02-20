package edu.neu.android.mhealth.uscteensver1.data;

import java.util.Calendar;
import java.util.Date;

public class ActivityData {
	protected String mStart   = "";
	protected String mStop    = "";
	protected String mModify  = "";
	protected String mCreate  = "";	
	protected String mWeekday = "";
	
	protected int[]   mData   = null;	
	protected boolean mLoaded = false;	
	
	public ActivityData(String start, String stop, String create, String modify) {
		mStart  = start;
		mStop   = stop;
		mModify = modify;
		mCreate = create;	
		
		String[] result = mStart.split(" ");	
		mWeekday = WeekdayCalculator.getWeekday(result[0]);
	}
	
	public boolean isLoaded() {
		return mLoaded;
	}
	
	public void setInternalData(int[] data) {
		if (data != null) {
			mData   = data;
			mLoaded = true;
		}
	}
	
	public int[] getInternalData() {
		return mData;
	}
	
	public String getStartDate() {
		String[] result = mStart.split(" ");
		
		return result[0];
	}
	
	public String getStopDate() {		
		String[] result = mStop.split(" ");
		
		return result[0];
	}
	
	public int getStartPosition() {
		String[] result = mStart.split(" ");
		String[] times = result[result.length - 1].split(":");
		
		return Integer.parseInt(times[0]) * 3600 + // hour 
			   Integer.parseInt(times[1]) * 60;    // minute
	}
	
	public int getStopPosition() {		
		String[] result = mStop.split(" ");
		String[] times = result[result.length - 1].split(":");
		
		return Integer.parseInt(times[0]) * 3600 + // hour 
			   Integer.parseInt(times[1]) * 60;    // minute
	}
	
	public String getWeekday() {
		return mWeekday;
	}
}
