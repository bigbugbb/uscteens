package edu.neu.android.mhealth.uscteensver1.data;

import android.content.Context;
import android.widget.Toast;

public class DataSource {
	public ActionData mActData = null;
	public ChunkData  mChkData = null;
	
	protected int mDay = 0;
	protected Context mContext = null;
	
	static protected DataSource sManager = null;
	
	static public DataSource getInstance(Context context) {
		if (sManager == null) {
			sManager = new DataSource(context);			
		}
		return sManager;
	}
	
	protected DataSource(Context context) {
		mContext = context;
		mActData = ActionData.getInstance(context);
		mChkData = ChunkData.getInstance(context);
		mChkData.bindActionData(mActData);
	}
	
	public void setDay(int day) {
		mDay = day;
		mActData.setDay(day);
		mChkData.setDay(day);
	}
	
	public void releaseData() {
		mActData.release();
		mChkData.release();
	}
	
	public boolean loadData() {		
		if (mActData.loadData() == false) {
			Toast.makeText(mContext, "Can't find the activity data!", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (mChkData.loadData() == false) {
			Toast.makeText(mContext, "Can't find the chunk data!", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		return true;
	}	
	
	public void saveData() {
		
	}
}
