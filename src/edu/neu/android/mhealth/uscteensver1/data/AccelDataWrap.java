package edu.neu.android.mhealth.uscteensver1.data;

import java.util.ArrayList;
import java.util.Arrays;

class AccelDataWrap extends ArrayList<ArrayList<AccelData>> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2059915471290360389L;
	protected int   mMaxAccelAvgValue;
	protected int[] mDrawableData = new int[SECONDS_IN_DAY];
	protected final static int DATA_VALUE_FOR_FILLING = 0;
	protected final static int NO_SENSOR_DATA = -1;
	protected final static int NO_SENSOR_DATA_TIME_THRESHOLD = 300;
	protected final static int SECONDS_IN_DAY = 3600 * 24;
	
	public AccelDataWrap() {
		clear();
	}	
	
	public void clear() {		
		mMaxAccelAvgValue = NO_SENSOR_DATA - 1;
		Arrays.fill(mDrawableData, NO_SENSOR_DATA);
		super.clear();
	}

	public int[] getDrawableData() {		
		return mDrawableData;
	}
	
	public int getDrawableDataLength() {
		return mDrawableData.length;
	}
	
	public int getMaxDrawableDataValue() {			
		return mMaxAccelAvgValue;
	}
	
	public boolean updateDrawableData() {
		if (size() == 0) {
			return false;
		}

		// update daily data		
		for (ArrayList<AccelData> hourlyData : this) {
			for (AccelData aData : hourlyData) {				
				mDrawableData[aData.mTimeInSec] = aData.mAccelAverage;
				if (mMaxAccelAvgValue < aData.mAccelAverage) {
					mMaxAccelAvgValue = aData.mAccelAverage;
				}
			}
		}
		// fill no sensor data part according to the time threshold
		int nStart = 0;
		int nEnd   = 0;
		for (int i = 0; i < mDrawableData.length; ++i) {	
			if (mDrawableData[i] != NO_SENSOR_DATA) {
				continue;
			}
			if (i == 0 || mDrawableData[i - 1] != NO_SENSOR_DATA) {
				nStart = i;
			}
			if (i == SECONDS_IN_DAY - 1 || mDrawableData[i + 1] != NO_SENSOR_DATA) {				
				nEnd = i + 1;
				if (nEnd - nStart > NO_SENSOR_DATA_TIME_THRESHOLD) {
					; // no data during this interval					
				} else {
					// fill this interval with zero
					if (nStart == 0) {
						Arrays.fill(mDrawableData, nStart, nEnd, DATA_VALUE_FOR_FILLING);
					} else {
						int k = 1;
						for (int j = nStart; j < nEnd; j += 2, ++k) {
							try {
								mDrawableData[j]     = mDrawableData[nStart - k];
								mDrawableData[j + 1] = mDrawableData[nStart - k];
							} catch (ArrayIndexOutOfBoundsException e) {
								// the number of the sensor data at the beginning is not enough 
								Arrays.fill(mDrawableData, j, nEnd, DATA_VALUE_FOR_FILLING);
								j = nEnd;
							}
						}
					}
				}
			}			
		}
			
		return true;
	}
}
