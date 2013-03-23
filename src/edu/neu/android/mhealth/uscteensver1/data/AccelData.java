package edu.neu.android.mhealth.uscteensver1.data;

import java.io.Serializable;

class AccelData implements Serializable {
	private static final long serialVersionUID = 2981052987309112192L;
	
	public int mHour;
	public int mMinute;
	public int mSecond;
	public int mMilliSecond;
	public int mTimeInSec;
	public int mAccelAverage;
	public int mAccelSamples;
	
	public AccelData(int hour, int minute, int second, int milliSecond, 
				     int timeInSec, int accelAverage, int accelSamples) {
		mHour         = hour;
		mMinute       = minute;
		mSecond       = second;
		mMilliSecond  = milliSecond;
		mTimeInSec    = timeInSec;
		mAccelAverage = accelAverage;
		mAccelSamples = accelSamples;
	}
}
