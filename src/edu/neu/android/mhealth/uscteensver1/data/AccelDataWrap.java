package edu.neu.android.mhealth.uscteensver1.data;

import java.util.ArrayList;
import java.util.Arrays;

import android.util.Pair;

class AccelDataWrap extends ArrayList<ArrayList<AccelData>> {	
	private static final long serialVersionUID = -80693842157700147L;
	protected int   mMaxAccelAvgValue;
	protected int[] mDrawableData = new int[SECONDS_IN_DAY];
	protected ArrayList<Pair<Integer, Integer>> mNoDataTime = new ArrayList<Pair<Integer, Integer>>();
	protected final static int DATA_VALUE_FOR_FILLING = 0;
	protected final static int NO_SENSOR_DATA = -1;
	protected final static int NO_SENSOR_DATA_TIME_THRESHOLD = 300;
	protected final static int SECONDS_IN_DAY = 3600 * 24;
	
	public AccelDataWrap() {
		clear();
	}	
	
	public void clear() {		
		mMaxAccelAvgValue = NO_SENSOR_DATA - 1;
		mNoDataTime.clear();
		Arrays.fill(mDrawableData, NO_SENSOR_DATA);
		super.clear();
	}
	
	public ArrayList<Pair<Integer, Integer>> getNoDataTimePeriods() {
		return mNoDataTime;
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
		int start = 0;
		int end   = 0;
		for (int i = 0; i < mDrawableData.length; ++i) {	
			if (mDrawableData[i] != NO_SENSOR_DATA) {
				continue;
			}
			if (i == 0 || mDrawableData[i - 1] != NO_SENSOR_DATA) {
				start = i;
			}
			if (i == SECONDS_IN_DAY - 1 || mDrawableData[i + 1] != NO_SENSOR_DATA) {				
				end = i + 1;
				if (end - start > NO_SENSOR_DATA_TIME_THRESHOLD) {
					// period of time with no sensor data
					mNoDataTime.add(Pair.create(start, end));
				} else {
					// fill this interval with some reasonable values
					if (start == 0) {
						Arrays.fill(mDrawableData, start, end, DATA_VALUE_FOR_FILLING);
					} else {
						int k = 1;
						for (int j = start; j < end; j += 2, ++k) {
							try {
								int value = mDrawableData[start - k] == NO_SENSOR_DATA ? 
										DATA_VALUE_FOR_FILLING :  mDrawableData[start - k];
								mDrawableData[j]     = value;
								mDrawableData[j + 1] = value;
							} catch (ArrayIndexOutOfBoundsException e) {
								// the number of the sensor data at the beginning is not enough 
								Arrays.fill(mDrawableData, j, end, DATA_VALUE_FOR_FILLING);
								j = end;
							}
						}
					}
				}
			}			
		}
			
		return true;
	}
}
