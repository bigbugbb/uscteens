package edu.neu.android.mhealth.uscteensver1.data;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import android.content.Context;
import android.widget.Toast;

public class DataSource {
		
	protected Context mContext = null;
	static protected DataSource sManager = null;
	
	static {
		System.loadLibrary("datasrc");
	}		
	
	static public DataSource getInstance(Context context) {
		if (sManager == null) {
			sManager = new DataSource(context);
			sManager.onCreate();
		}
		return sManager;
	}
	
	protected DataSource(Context context) {
		mContext = context;
	}
		
	public static final int PIXEL_SCALE = 8;	
	protected final String PATH_PREFIX = "/sdcard/TestData/";
	
	// configuration read from xml
	protected Configuration mConfig = new Configuration();
	// raw chunk data
	protected RawChunkList mRawChkList = new RawChunkList();
	// raw activity data
	protected int[]  mRawActivityData  = null;	
	protected int	 mMaxActivityValue = 0;
	// current selected date
	protected String mCurSelectedDate = "";
	
	public void onCreate() {		
		mConfig.load(PATH_PREFIX + "config.xml");	
	}
	
	public void onStop() {
		
	}
	
	public void onDestory() {
		
	}
		
	/**
	 * 	 
	 * @param date	YYYY-MM-DD
	 * @return
	 */
	public boolean loadRawData(String date) {
		String path = PATH_PREFIX + date + ".txt";
		mCurSelectedDate = date;
		
		File file = new File(path);		
		if (!file.exists()) {
			Toast.makeText(mContext, "Can't find the activity data!", Toast.LENGTH_SHORT).show();
			return false;
		}
		// first load raw activity data 
		mRawActivityData  = loadActivityData(path);
		mMaxActivityValue = getMaxActivityValue(path);
		
		// then load the corresponding chunks
		path = PATH_PREFIX + date + ".xml"; 
		file = new File(path);
		if (!file.exists()) {
			Toast.makeText(mContext, "Can't find the chunk file!", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (!loadRawChunkData(path)) {
			Toast.makeText(mContext, "Fail to read the chunk data!", Toast.LENGTH_SHORT).show();
			return false;
		}
			
		return true;
	}
	
	public String getCurSelectedDate() {
		return mCurSelectedDate;
	}
	
	public int[] getRawActivityData() {
		return mRawActivityData;
	}
	
	public int getActivityLengthInPixel() {
		return mRawActivityData.length * PIXEL_SCALE;
	}
	
	public int getMaxActivityValue() {
		return mMaxActivityValue;
	}
	
	public RawChunkList getRawChunkList() {
		return mRawChkList;
	}
	
	private boolean loadRawChunkData(String path) {
		File file = new File(path);
		
		if (!file.exists()) {
			Toast.makeText(mContext, "Can't find the chunk data!", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		SAXReader saxReader = new SAXReader();		
		try {
			Document document = saxReader.read(new File(path));								
			Element root = document.getRootElement();

		    for (Iterator i = root.elementIterator(); i.hasNext();) {
		       Element annotations = (Element) i.next();
		       for (Iterator j = annotations.elementIterator(); j.hasNext();) {
		    	   Element annotation = (Element) j.next();
		    	   for (Iterator k = annotation.elementIterator(); k.hasNext();) {
			    	   Element label = (Element) k.next();
			    	   Element start = (Element) k.next();
			    	   Element stop  = (Element) k.next();
			    	   Element prop  = (Element) k.next();
			    	   
			    	   String modify = "";
			    	   String create = "";
			    	   String type   = "";
			    	   for (Iterator n = prop.attributeIterator(); n.hasNext();) {
			    	       Attribute attribute = (Attribute) n.next();
			    	       
			    	       if (attribute.getName().compareTo("ACTIVITY_TYPE") == 0) {
			    	    	   type   = attribute.getText();
			    	       } else if (attribute.getName().compareTo("LAST_MODIFIED") == 0) {
			    	    	   modify = attribute.getText();
			    	       } else if (attribute.getName().compareTo("DATE_CREATED") == 0) {
			    	    	   create = attribute.getText(); 	    	   
			    	       }
			    	    }
			    	   
			    	   RawChunk rawchunk = new RawChunk(start.getText(), stop.getText(), type, create, modify);
			    	   mRawChkList.add(rawchunk);
			       }
		        
		       }
		    }
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		return true;
	}
	
	public Configuration getConfiguration() {
		return mConfig;
	}

	public boolean saveChunkData(RawChunkList rawChunks) {
		boolean result = false;		
		String path = PATH_PREFIX + mCurSelectedDate + ".xml";
		File file = new File(path);
		
		//DeleteFileUtil.delete(path);
//		try {
		//	file.createNewFile();				
			
			
						
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return false;
//		}
		
		return true;
	}
	
	private native int create();
	private native int destroy();
	private native int[] loadActivityData(String path);
	private native int unloadActivityData(String path);
	private native int getMaxActivityValue(String path);
}
