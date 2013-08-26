package edu.neu.android.mhealth.uscteensver1.data;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

class RawLabel implements Serializable, Comparable<RawLabel> {
    private static final long serialVersionUID = 900403168675225124L;

    private int mHour;
    private int mMinute;
    private int mSecond;
    private int mTimeInSec;
    private String mDate;
    private String mName; // such as Sleeping


    public RawLabel(String dateTime, String name) {
        Date date = null;
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        try {
            date = df.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
            date = new Date();
        }

        mHour      = date.getHours();
        mMinute    = date.getMinutes();
        mSecond    = date.getSeconds();
        mTimeInSec = mHour * 3600 + mMinute * 60 + mSecond;
        mDate      = dateTime.split(" ")[0];
        mName      = name;
    }

    public String getName() {
        return mName;
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
        sb.append(mHour < 10 ? " 0" : " ");
        sb.append(mHour);
        sb.append(mMinute < 10 ? ":0" : ":");
        sb.append(mMinute);
        sb.append(mSecond < 10 ? ":0" : ":");
        sb.append(mSecond);
        sb.append(", ");
        sb.append(mName);
        sb.append("\n");

        return sb.toString();
    }
}
