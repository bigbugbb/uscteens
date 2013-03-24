package edu.neu.android.mhealth.uscteensver1.data;

import java.io.Serializable;

public class LabelData implements Serializable, Comparable<LabelData> {
	private static final long serialVersionUID = 900403168675225124L;
	
	private int mHour;
	private int mMinute;
	private int mSecond;			
	private int mX; // the horizontal position for the label, scaling is needed
	private int	mY; // the vertical position for the label, scaling is needed
	private String mText; // such as Sleeping
	

	public LabelData(int hour, int minute, int second, int timeInSec, String text) {
		mHour   = hour;
		mMinute = minute;
		mSecond = second;
		mX      = timeInSec;
		mY      = 0;
		mText   = text;
	}

	public String getText() {
		return mText;
	}
	
	public int getX() {
		return mX;
	}
	
	public int getY() {
		return mY;
	}
	
	@Override
	public int compareTo(LabelData another) {
		return mX - another.getX();
	}
}
