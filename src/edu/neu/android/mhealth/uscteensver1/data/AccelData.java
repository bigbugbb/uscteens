package edu.neu.android.mhealth.uscteensver1.data;

import java.io.Serializable;

public class AccelData implements Serializable {
    private static final long serialVersionUID = 2981052987309112192L;

    public int mHour;
    public int mMinute;
    public int mSecond;
    public int mMillisecond;
    public int mTimeInSec;
    public int mAccelAverage;
    public int mAccelSamples;

    public AccelData() {
    }

    public AccelData(int hour, int minute, int second, int milliSecond,
                     int accelAverage, int accelSamples) {
        updateData(hour, minute, second, milliSecond, accelAverage, accelSamples);
    }

    public AccelData(String hour, String minute, String second, String milliSecond,
                     String accelAverage, String accelSamples) {
        mHour         = Integer.parseInt(hour);
        mMinute       = Integer.parseInt(minute);
        mSecond       = Integer.parseInt(second);
        mMillisecond  = Integer.parseInt(milliSecond);
        mTimeInSec    = mHour * 3600 + mMinute * 60 + mSecond;
        mAccelAverage = Integer.parseInt(accelAverage);
        mAccelSamples = Integer.parseInt(accelSamples);
    }

    public void updateData(int hour, int minute, int second, int milliSecond,
                           int accelAverage, int accelSamples) {
        mHour         = hour;
        mMinute       = minute;
        mSecond       = second;
        mMillisecond  = milliSecond;
        mTimeInSec    = mHour * 3600 + mMinute * 60 + mSecond;
        mAccelAverage = accelAverage;
        mAccelSamples = accelSamples;
    }
}
