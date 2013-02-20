package edu.neu.android.mhealth.uscteensver1.data;

public class ChunkDataCell {
	public String mStartDT;
	public String mStopDT;
	public String mCreateTM;
	public String mModifyTM;
	public int mActionID;
	
	public ChunkDataCell(String startDT, String stopDT, int actionID, String createTM, String modifyTM) {
		mStartDT  = startDT;
		mStopDT   = stopDT;
		mActionID = actionID;
		mCreateTM = createTM;
		mModifyTM = modifyTM;
	}
	
	public int getStartPosition() {
		String[] result = mStartDT.split(" ");
		String[] times = result[result.length - 1].split(":");
		
		return Integer.parseInt(times[0]) * 3600 + // hour 
			   Integer.parseInt(times[1]) * 60;    // minute
	}
	
	public int getStopPosition() {		
		String[] result = mStopDT.split(" ");
		String[] times = result[result.length - 1].split(":");
		
		return Integer.parseInt(times[0]) * 3600 + // hour 
			   Integer.parseInt(times[1]) * 60;    // minute
	}
}