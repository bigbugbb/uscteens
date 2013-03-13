package edu.neu.android.mhealth.uscteensver1.data;

import java.util.ArrayList;

public class AccelDataWrap extends ArrayList<ArrayList<AccelData>> {
	//protected static int[] mRawActivityData  = null;	
	protected int   mMaxAccelAvgValue   = 0;
	protected int[] mDrawableData = new int[3600 * 24];
	protected final static int NO_SENSOR_DATA = -1;
	protected final static int NO_SENSOR_DATA_TIME_THRESHOLD = 300;
	
	public AccelDataWrap() {
		
	}	

	public int[] getDrawableData() {		
		return mDrawableData;
	}
	
	public int getDrawableDataLength() {
		return mDrawableData.length;
	}
	
	public int getMaxDrawableDataValue() {	
		
		return 250;
	}
	
	public boolean updateDrawableData() {
		if (size() == 0) {
			return false;
		}
		// update daily data
		int n = 0;
		for (ArrayList<AccelData> hourlyData : this) {			
			// update hourly data		
			for (int i = 0; i < hourlyData.size(); ++i) {
				AccelData curData  = hourlyData.get(i);
				AccelData prevData = i > 0 ? hourlyData.get(i - 1) : null;
				
				while (n < curData.mTimeInSec) {
					if (prevData != null) {
						if (curData.mTimeInSec - prevData.mTimeInSec >= NO_SENSOR_DATA_TIME_THRESHOLD) {
							while (n < curData.mTimeInSec) {
								mDrawableData[n++] = NO_SENSOR_DATA;
							}
						} else {
							mDrawableData[n++] = 0;
						}
					} else {
						if (curData.mTimeInSec >= NO_SENSOR_DATA_TIME_THRESHOLD) {
							while (n < curData.mTimeInSec) {
								mDrawableData[n++] = NO_SENSOR_DATA;
							}
						} else {
							mDrawableData[n++] = 0;
						}
					}
				}
				mDrawableData[n++] = curData.mAccelAverage;
			}
		}
		return true;
	}
}
