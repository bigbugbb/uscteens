package edu.neu.android.mhealth.uscteensver1.data;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

class RawLabel implements Serializable, Comparable<RawLabel> {
	private static final long serialVersionUID = 900403168675225124L;
	
	private int    mHour;
	private int    mMinute;
	private int    mSecond;
	private int    mTimeInSec;
	private String mDate;
	private String mText; // such as Sleeping	
	
	
	public RawLabel(String dateTime, String text) {
		Date date = null;
	    DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
	    try {
			date = df.parse(dateTime);
		} catch (ParseException e) {	
			e.printStackTrace();
			date = new Date();
		}
	    
	    mHour   = date.getHours();
	    mMinute = date.getMinutes();
	    mSecond = date.getSeconds();
	    mTimeInSec = mHour * 3600 + mMinute * 60 + mSecond;
	    mDate   = dateTime.split(" ")[0];
	    mText   = text;
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

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(mDate);
		sb.append(" ");
		sb.append(mHour < 10 ? "0" : "");
		sb.append(mHour);
		sb.append(":");
		sb.append(mMinute < 10 ? "0" : "");
		sb.append(mMinute);
		sb.append(":");
		sb.append(mSecond < 10 ? "0" : "");
		sb.append(mSecond);
		sb.append(", ");
		sb.append(mText);
		sb.append("\n");
		return sb.toString();
	}
}
