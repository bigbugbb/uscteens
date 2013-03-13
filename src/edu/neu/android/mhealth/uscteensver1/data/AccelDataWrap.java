package edu.neu.android.mhealth.uscteensver1.data;

import java.util.ArrayList;

public class AccelDataWrap extends ArrayList<ArrayList<AccelData>> {
	//protected static int[] mRawActivityData  = null;	
	protected int   mMaxAccelAvgValue   = 0;
	protected int[] mDrawableData = new int[3600 * 24];
	
	public AccelDataWrap() {
		
	}	

	public int[] getDrawableData() {
		if (mDrawableData.length == 0) {
			
		}
		return mDrawableData;
	}
	
	public int getDrawableDataLength() {
		return mDrawableData.length;
	}
	
	public int getMaxDrawableDataValue() {	
		return 200;
	}
}
