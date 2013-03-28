package edu.neu.android.mhealth.uscteensver1.data;

import java.io.Serializable;

class RawLabel implements Serializable, Comparable<RawLabel> {
	private static final long serialVersionUID = 900403168675225124L;
	
	private int    mHour;
	private int    mMinute;
	private int    mSecond;
	private int    mTimeInSec;
	private String mText; // such as Sleeping
	

	public RawLabel(int hour, int minute, int second, int timeInSec, String text) {
		mHour      = hour;
		mMinute    = minute;
		mSecond    = second;
		mTimeInSec = timeInSec;
		mText      = text;
	}
	
	public RawLabel(String hour, String minute, String second, String text) {
		mHour      = Integer.parseInt(hour);
		mMinute    = Integer.parseInt(minute);
		mSecond    = Integer.parseInt(second);
		mTimeInSec = mHour * 3600 + mMinute * 60 + mSecond;
		mText      = text;
	}

	public String getText() {
		return mText;
	}
	
	public int getTimeInSec() {
		return mTimeInSec;
	}
	
	@Override
	public int compareTo(RawLabel another) {
		return mTimeInSec - another.getTimeInSec();
	}
}
