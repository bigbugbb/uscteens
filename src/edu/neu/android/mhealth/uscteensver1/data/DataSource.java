package edu.neu.android.mhealth.uscteensver1.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
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
import edu.neu.android.wocketslib.support.DataStorage;

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
		mConfig  = new Configuration(context);
	}
		
	public static final int PIXEL_SCALE = 2;	
	public static final String PATH_PREFIX = "/sdcard/TestData/";
	
	// configuration read from xml
	protected Configuration mConfig = null;
	// raw chunk data
	protected RawChunkList mRawChkList = new RawChunkList();
	// raw activity data
	protected int[]  mRawActivityData  = null;	
	protected int	 mMaxActivityValue = 0;
	// current selected date
	protected String mCurSelectedDate = "";
	// flag to indicate whether the data source is initialized
	protected boolean mInitialized = false;
	
	public void onCreate() {		
			
	}
	
	public void onStop() {
		
	}
	
	public void onDestory() {
		
	}
	
	public boolean hasStartDate() {
//		File configFile = new File(PATH_PREFIX + "config.xml");
//		if (!configFile.exists()) {
//			mInitialized = false; 
//		}
//		return mInitialized;
		String startDate = DataStorage.GetValueString(mContext, USCTeensGlobals.START_DATE, "");
		return startDate.compareTo("") != 0;
	}
	
//	public boolean initialize() {		
//		if (!mInitialized) {
//			mInitialized = mConfig.load(PATH_PREFIX + "config.xml");
//		}
//		// check whether the config file exists, in case the it is deleted by user
//		File configFile = new File(PATH_PREFIX + "config.xml");
//		if (!configFile.exists()) {
//			mInitialized = false; 
//		}
//		
//		return mInitialized;
//	}
		
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
			Toast.makeText(mContext, "Can't find the chunk data xml!", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (!loadRawChunkData(path)) {
			Toast.makeText(mContext, "Fail to read the chunk data file!", Toast.LENGTH_SHORT).show();
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
		mRawChkList.clear();
		
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
	
	public Configuration getConfiguration() {
		return mConfig;
	}

	public boolean saveChunkData(RawChunkList rawChunks) {
		boolean result = false;		
		String path = PATH_PREFIX + mCurSelectedDate + ".xml";
		
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
        .addAttribute("ALLLABELLED", rawChunks.areAllChunksLabelled() ? "yes" : "no");
        
        for (RawChunk rawChunk : rawChunks) {
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
		
		return true;
	}
	
	private native int create();
	private native int destroy();
	private native int[] loadActivityData(String path);
	private native int unloadActivityData(String path);
	private native int getMaxActivityValue(String path);
}
