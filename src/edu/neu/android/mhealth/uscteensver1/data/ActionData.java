package edu.neu.android.mhealth.uscteensver1.data;

import java.io.FileInputStream;

import org.apache.http.util.EncodingUtils;

import android.content.Context;

public class ActionData {
	
	protected int mDay = 0;
	protected float[] mData = null;
	protected Context mContext = null;
	
	static protected ActionData sObj = null;	

	protected ActionData(Context context) {
		mContext = context; 
	}
	
	static public ActionData getInstance(Context context) {
		if (sObj == null) {
			sObj = new ActionData(context);			
		}
		return sObj;
	}
	
	public void setDay(int day) {
		mDay = day;
	}
	
	public boolean loadData() {
		boolean result = false;
		String fileName = "/sdcard/TestData/day" + mDay + ".txt";
		
		try { 
			FileInputStream fin = new FileInputStream(fileName);		
//		    int length = fin.available(); 
//		    byte[] buffer = new byte[length]; 
//		    fin.read(buffer); 		    
//		    res = EncodingUtils.getString(buffer, "UTF-8"); 
		    fin.close(); 
		    result = true;
		    
		    NativeDataSource dateSrc = NativeDataSource.getDataSource();
			mData = dateSrc.loadActivityData(fileName);		    
		} catch (Exception e) { 
			e.printStackTrace(); 
		}
//		String[] values = res.split("\n");		
//		mData = new float[values.length];
//		for (int i = 0; i < mData.length; ++i) {
//			mData[i] = Integer.valueOf(values[i]);
//		}		
				
		return result;
	}
	
	public void release() {
		mData = null;
		System.gc();
	}
	
	public float[] getData() {
		return mData;
	}
}
