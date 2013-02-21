package edu.neu.android.mhealth.uscteensver1.data;

import java.util.Calendar;
import java.util.Date;

public class RawActivity {
	protected int[]   mData   = null;	
	protected int	  mMax    = 0;
	protected boolean mLoaded = false;	
	
	public RawActivity() {
		
	}
	
	public boolean isLoaded() {
		return mLoaded;
	}
	
	public void setInternalData(int[] data, int max) {
		if (data != null) {
			mData   = data;
			mLoaded = true;
		}
		mMax = max;
	}
	
	public int[] getInternalData() {
		return mData;
	}
	
	public int getMaxActivityValue() {
		return mMax;
	}

}
