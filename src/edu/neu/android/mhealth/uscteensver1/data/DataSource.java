package edu.neu.android.mhealth.uscteensver1.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import edu.neu.android.mhealth.uscteensver1.USCTeensGlobals;
import edu.neu.android.mhealth.uscteensver1.utils.ParadigmUtility;
import edu.neu.android.wocketslib.Globals;
import edu.neu.android.wocketslib.support.DataStorage;
import edu.neu.android.wocketslib.utils.FileHelper;

import android.content.Context;
import android.widget.Toast;

public class DataSource {
		
	protected Context mContext = null;	
	static protected DataSource aDataSource = null;
	
	static {
		System.loadLibrary("datasrc");
	}		
	
	static public DataSource getInstance(Context context) {
		if (aDataSource == null) {
			aDataSource = new DataSource(context);
			aDataSource.onCreate();
		}
		return aDataSource;
	}
	
	protected DataSource(Context context) {
		mContext = context;		
	}
		
	public static final int PIXEL_SCALE = 2;	
	public static final String PATH_PREFIX = "/sdcard/TestData/";

	// raw chunk data
	protected RawChunksWrap mRawChksWrap = new RawChunksWrap();
	// raw accelerometer sensor data
	protected AccelDataWrap mAccelDataWrap = new AccelDataWrap();
	// hourly accelerometer sensor data
	protected ArrayList<AccelData> mHourlyAccelData = new ArrayList<AccelData>();
	// current selected date
//	protected String mCurSelectedDate = "";
	// flag to indicate whether the data source is initialized
	protected boolean mInitialized = false;
	
	public void onCreate() {		
			
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
		DataStorage.SetValue(mContext, USCTeensGlobals.CURRENT_SELECTED_DATE, date);
		
		File file = new File(path);		
		if (!file.exists()) {
			Toast.makeText(mContext, "Can't find the activity data!", Toast.LENGTH_SHORT).show();
			return false;
		}
		// first clear the data container
		mAccelDataWrap.clear();
		mHourlyAccelData.clear();
		// load the daily data from csv files hour by hour
		String dateDir = new SimpleDateFormat("yyyy-MM-dd/").format(new Date());
		String[] hourDirs = FileHelper.getFilePathsDir(
				Globals.EXTERNAL_DIRECTORY_PATH + File.separator + 
				Globals.DATA_DIRECTORY + USCTeensGlobals.SENSOR_FOLDER + dateDir);		
		for (int i = 0; i < hourDirs.length; ++i) {
			// each hour corresponds to one csv file
			String[] filePath = FileHelper.getFilePathsDir(hourDirs[i]);			
			// load the hourly data from csv file and save the data to mHourlyAccelData
			loadHourlyAccelSensorData(filePath[0]);
			// add the houly data the data wrap
			mAccelDataWrap.add(mHourlyAccelData);
			// clear it because it has been filled after loadHourlyAccelSensorData is called
			mHourlyAccelData.clear();
		}
		mAccelDataWrap.updateDrawableData();
		// now we have a loaded daily accelerometer sensor data in the data wrap,
		// we convert it into the data structure that can be drawn easily.
		
//		mRawActivityData  = 
//		mMaxActivityValue = getMaxActivityValue(path);
/////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		// then load the corresponding chunks
		path = PATH_PREFIX + date + ".xml"; 
		file = new File(path);
		if (!file.exists()) {
			Toast.makeText(mContext, "Can't find the chunk data xml!", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (!loadRawChunkData(path)) {
			Toast.makeText(mContext, "Fail to read the chunk data file!", Toast.LENGTH_SHORT).show();
			return false;
		}
			
		return true;
	}
	
	private void onGetAccelData(AccelData acData) {
		mHourlyAccelData.add(acData);
	}
	
	private void onGetAccelData(int hour, int minute, int second, int milliSecond, 
				     			int timeInSec, int accelAverage, int accelSamples) {
		AccelData acData = new AccelData(hour, minute, second, milliSecond, 
				timeInSec, accelAverage, accelSamples);
		mHourlyAccelData.add(acData);
	}
	
	public String getCurrentSelectedDate() {
		return DataStorage.GetValueString(mContext, USCTeensGlobals.CURRENT_SELECTED_DATE, "");
	}
	
/////////////////////////////////////////////////////////////////////////////////////////////////////////
	public int[] getDrawableData() {
		return mAccelDataWrap.getDrawableData();
	}
	
	public int getDrawableDataLengthInPixel() {
		return mAccelDataWrap.getDrawableDataLength() * PIXEL_SCALE;
	}
	
	public int getMaxDrawableDataValue() {
		return mAccelDataWrap.getMaxDrawableDataValue();
	}
/////////////////////////////////////////////////////////////////////////////////////////////////////////

	public RawChunksWrap getRawChunks() {
		return mRawChksWrap;
	}
	
	private boolean loadRawChunkData(String path) {
		mRawChksWrap.clear();
		
		File file = new File(path);
		
		if (!file.exists()) {
			Toast.makeText(mContext, "Can't find the chunk data xml!", Toast.LENGTH_SHORT).show();
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
			    	   mRawChksWrap.add(rawchunk);
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
	
	public boolean areAllChunksLabelled(String date) {
		String path = PATH_PREFIX + date + ".xml";
		
		File file = new File(path);		
		if (!file.exists()) {			
			return false;
		}
		
		boolean allLabelled = false;
		SAXReader saxReader = new SAXReader();		
		try {
			Document document = saxReader.read(new File(path));								
			Element root = document.getRootElement();

		    for (Iterator i = root.elementIterator(); i.hasNext();) {
		       Element annotations = (Element) i.next();		       
	    	   for (Iterator n = annotations.attributeIterator(); n.hasNext();) {
	    	       Attribute attribute = (Attribute) n.next();
	    	       
	    	       if (attribute.getName().compareTo("ALLLABELLED") == 0) {
	    	    	   String text = attribute.getText();
	    	    	   if (text.compareToIgnoreCase("yes") == 0) {
	    	    		   allLabelled = true;
	    	    	   } else {
	    	    		   allLabelled = false;
	    	    	   }
	    	       } 
	    	    }	    	
		    }
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();			
		}
		
		return allLabelled;
	}

	public boolean saveChunkData(final ArrayList<Chunk> chunks) {
		boolean result = false;		
		String date = DataStorage.GetValueString(mContext, USCTeensGlobals.CURRENT_SELECTED_DATE, "");
		assert(date.compareTo("") != 0);
		String path = PATH_PREFIX + date + ".xml";				
		
		mRawChksWrap.clear();
		for (int i = 0; i < chunks.size(); ++i) {
			Chunk chunk = chunks.get(i);
			RawChunk rawChunk = chunk.toRawChunk();
			mRawChksWrap.add(rawChunk);
		}
		
		Document document = DocumentHelper.createDocument();
        
		// ACTIVITYDATA
        Element root = document.addElement("ACTIVITYDATA");
        root.addAttribute("xmlns", "urn:mites-schema");
        // ANNOTATIONS
        Element annotations = root.addElement("ANNOTATIONS")
	        .addAttribute("DATASET", "TeenActivity")
	        .addAttribute("ANNOTATOR", "Simple-Algorithm")
	        .addAttribute("EMAIL", "bigbugbb@gmail.com")
	        .addAttribute("DESCRIPTION", "Teen activity labelling")
	        .addAttribute("METHOD", "based on pre-defined theresholds")
	        .addAttribute("NOTES", "")
	        .addAttribute("ALLLABELLED", mRawChksWrap.areAllChunksLabelled() ? "yes" : "no");
        
        for (RawChunk rawChunk : mRawChksWrap) {
	        // ANNOTATION
	        Element annotation = annotations.addElement("ANNOTATION")
	        	.addAttribute("GUID", "");
	        // LABEL
	        Element label = annotation.addElement("LABEL")
		        .addAttribute("GUID", "")
		        .addText("1");
	        // START_DT
	        Element start_dt = annotation.addElement("START_DT")
	        	.addText(rawChunk.mStartDate);
	        // STOP_DT
	        Element stop_dt = annotation.addElement("STOP_DT")
	        	.addText(rawChunk.mStopDate);
	        // PROPERTIES
	        Element properties = annotation.addElement("PROPERTIES")
		        .addAttribute("ANNOTATION_SET", "Teen_Activity_Study")
		        .addAttribute("ACTIVITY_TYPE", rawChunk.mActivity)
		        .addAttribute("LAST_MODIFIED", rawChunk.mModifyTime)
		        .addAttribute("DATE_CREATED",  rawChunk.mCreateTime);
        }
                
        XMLWriter writer;
		try {
			// The file will be truncated if it exists, and created if it doesn't exist.
			writer = new XMLWriter(new FileOutputStream(path));
			writer.write(document);
	        writer.close();
	        result = true;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}                   
		
		return result;
	}
	
	private native int create();
	private native int destroy();
	private native int loadHourlyAccelSensorData(String filePath);
	private native int unloadActivityData(String path);
	private native int getMaxActivityValue(String path);
}
