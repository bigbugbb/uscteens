package edu.neu.android.mhealth.uscteensver1.data;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

import org.apache.http.util.EncodingUtils;

import android.content.Context;

public class ChunkData {
	
	protected int mDay = 0;
	protected ArrayList<DataCell> mData = null;	
	protected Context mContext = null;
	protected ActionData mActData = null;
	
	static protected ChunkData sObj = null;
	
	static public ChunkData getInstance(Context context) {
		if (sObj == null) {
			sObj = new ChunkData(context);			
		}
		return sObj;
	}
	
	protected ChunkData(Context context) {
		mContext = context;		
	}
	
	public void setDay(int day) {
		mDay = day;
	}
	
	public boolean loadData() { // now just ignore the day for a moment
		boolean result = false;
		String fileName = "/sdcard/TestData/chunk" + mDay + ".txt";
		String res = "";     

		try { 
			FileInputStream fin = new FileInputStream(fileName);		
		    int length = fin.available(); 
		    byte[] buffer = new byte[length]; 
		    fin.read(buffer); 		    
		    res = EncodingUtils.getString(buffer, "UTF-8"); 
		    fin.close();     
		    
		    String[] values = res.split("\n");
		    mData = new ArrayList<DataCell>();
			for (int i = 0; i < values.length; ++i) {
				String[] stringCell = values[i].split(":");
				DataCell cell = new DataCell(Integer.valueOf(stringCell[0]), Integer.valueOf(stringCell[1]));
				mData.add(cell);
			}
			DataCell cell = mData.get(mData.size() - 1);
	
			result = true;
		} catch (Exception e) { 
			e.printStackTrace(); 
			// ....
		}		
		
		return result;
	}
	
	public boolean saveData(ArrayList<DataCell> cells) {
		boolean result = false;
		String toSave = "";
		String fileName = "/sdcard/TestData/chunk" + mDay + ".txt";
		
		for (DataCell cell : cells) {
			toSave += cell.mPosition + ":" + cell.mActionID + "\n";
		}
		
		try { 
			FileOutputStream fout = new FileOutputStream(fileName);
	        byte[] bytes = toSave.getBytes(); 
	        fout.write(bytes); 
	        fout.close();
	        
	        result = true;
	    } catch(Exception e) { 
	    	e.printStackTrace();
	    	// ....
	    } 	
		
		return result;
	}
	
	public void release() {
		mData = null;
		System.gc();
	}
	
	public ArrayList<DataCell> getData() {
		return mData;
	}
	
	public void bindActionData(ActionData actData) {
		mActData = actData;
	}
		
}
