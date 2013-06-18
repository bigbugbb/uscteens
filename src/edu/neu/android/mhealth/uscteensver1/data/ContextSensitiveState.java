package edu.neu.android.mhealth.uscteensver1.data;

import java.io.Serializable;
import java.util.Date;

public class ContextSensitiveState implements Serializable {
	
	private static final long serialVersionUID = -987466237866182313L;
	
	public final static int DATA_STATE_NORMAL         = 0;
	public final static int DATA_STATE_MISSING        = 1;
	public final static int DATA_STATE_HIGH_INTENSITY = 2;
	public final static int DATA_STATE_LOW_INTENSITY  = 3;
	public final static int DATA_STATE_ERROR          = 4;
	
	private int  mState;
	private Date mStartTime;
	private Date mEndTime;		
	
	public ContextSensitiveState(int state, Date startTime, Date endTime) {
		mState     = state;
		mStartTime = startTime;
		mEndTime   = endTime;
	}
	
	public int getState() {
		return mState;
	}
	
	public String getStartTime() {
		return "";
	}
	
	public String getEndTime() {
		return "";
	}
}
