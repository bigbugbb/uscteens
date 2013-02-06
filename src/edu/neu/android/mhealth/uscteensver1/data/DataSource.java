package edu.neu.android.mhealth.uscteensver1.data;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

import org.apache.http.util.EncodingUtils;

import android.content.Context;
import android.widget.Toast;

public class DataSource {
	
	public static final int PIXEL_SCALE = 3;
	
	public int[] mActData = null;
	//public int[] mChkData = null;
	public ArrayList<DataCell> mChkData = null;
	
	protected int mDay = 0;
	protected Context mContext = null;
	static protected DataSource sManager = null;
	
	static {
		System.loadLibrary("datasrc");
	}
	
	static public DataSource getInstance(Context context) {
		if (sManager == null) {
			sManager = new DataSource(context);			
		}
		return sManager;
	}
	
	protected DataSource(Context context) {
		mContext = context;
	}
	
	public void setDay(int day) {
		mDay = day;
	}

	public boolean loadData() {		
		if (loadActData() == false) {
			Toast.makeText(mContext, "Can't find the activity data!", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (loadChkData() == false) {
			Toast.makeText(mContext, "Can't find the chunk data!", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		return true;
	}	
	
	public void saveData() {
		
	}
	
	private boolean loadActData() {		
		boolean result = false;
		String fileName = "/sdcard/TestData/day" + mDay + ".txt";
//		
//		try { 
//			FileInputStream fin = new FileInputStream(fileName);		
//		    fin.close(); 
//		    result = true;		    
//		    mActData = loadActivityData(fileName);		    
//		} catch (Exception e) { 
//			e.printStackTrace(); 
//		}
//				
		mActData = new int[3600];
	    for (int i = 0; i < mActData.length; ++i) {
	    	mActData[i] = i % 100; 
	    }
	    result = true;
	    
		return result;		
	}
	
	public int getActLengthInPixel() {
		return mActData.length * PIXEL_SCALE;
	}
	
	private boolean loadChkData() {
/*		boolean result = false;
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
		    mChkData = new ArrayList<DataCell>();
			for (int i = 0; i < values.length; ++i) {
				String[] stringCell = values[i].split(":");
				DataCell cell = new DataCell(Integer.valueOf(stringCell[0]), Integer.valueOf(stringCell[1]));
				mChkData.add(cell);
			}
			DataCell cell = mChkData.get(mChkData.size() - 1);
	
			result = true;
		} catch (Exception e) { 
			e.printStackTrace(); 
			// ....
		}		
	*/
		///////////////////////////////////////////////////////////////////////
		boolean result = false;
		mChkData = new ArrayList<DataCell>();
		for (int i = 0; i < 9; ++i) {			
			DataCell cell = new DataCell(400 * i, -1);
			mChkData.add(cell);
		}
		DataCell cell = mChkData.get(mChkData.size() - 1);
		result = true;
		///////////////////////////////////////////////////////////////////////
		return result;
	}
	
	public boolean saveChunkData(ArrayList<DataCell> cells) {
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
	
	public native int create();
	public native int destroy();
    public native int[] loadActivityData(String path);
    public native int unloadActivityData(String path);
}
