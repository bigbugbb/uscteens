package edu.neu.android.mhealth.uscteensver1.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import android.content.Context;
import android.util.Pair;
import edu.neu.android.mhealth.uscteensver1.USCTeensGlobals;
import edu.neu.android.wocketslib.Globals;
import edu.neu.android.wocketslib.support.DataStorage;
import edu.neu.android.wocketslib.utils.FileHelper;
import edu.neu.android.wocketslib.utils.WOCKETSException;

public class DataSource {
	// result code
	public final static int LOADING_SUCCEEDED  = 0;
	public final static int ERR_NO_SENSOR_DATA = 1;
	public final static int ERR_NO_CHUNK_DATA  = 2;	
	
	// value for minimum sensor data
	protected final static int MINIMUM_SENSOR_DATA_VALUE = 1600;
	
	protected static Context sContext = null;
	// raw chunk data
	protected static RawChunksWrap sRawChksWrap = new RawChunksWrap();
	// raw accelerometer sensor data
	protected static AccelDataWrap sAccelDataWrap = new AccelDataWrap();
	// hourly accelerometer data
	protected static ArrayList<AccelData> sHourlyAccelData = null;	
	// floating labels data
	protected static LabelDataWrap sLabelWrap = new LabelDataWrap();
	
	
	static {
		System.loadLibrary("datasrc");
	}		
	
	public static void initialize(Context context) {
		sContext = context;		
	}	
	
	/**
	 * 	 
	 * @param date	YYYY-MM-DD
	 * @return
	 */
	public static int loadRawData(String date) {
		DataStorage.SetValue(sContext, USCTeensGlobals.CURRENT_SELECTED_DATE, date);
		
		/* 
		 * first load the accelerometer sensor data
		 */
		if (!loadRawAccelData(date)) {
			return ERR_NO_SENSOR_DATA;
		}
		
		/* 
		 * then load the corresponding chunk data.
		 * if no chunk data, create the chunk data from sensor data
		 */
		String curDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		if (date.compareTo(curDate) != 0) {
			// the previous day's data are all available, just read it.
			// if the chunking file has not been generated, create it.
			if (!loadRawChunkData(date) && !createRawChunkData()) {
				return ERR_NO_CHUNK_DATA;			
			}
		} else {	 
			if (loadRawChunkData(date)) {
				assert(sRawChksWrap.size() > 0);
				RawChunk lastRawChunk = sRawChksWrap.get(sRawChksWrap.size() - 1);
				RawChunk lastPrevRawChunk = sRawChksWrap.size() > 1 ?
						sRawChksWrap.get(sRawChksWrap.size() - 2) : null;		
				boolean updateFromLastPrev = lastPrevRawChunk != null && !lastPrevRawChunk.isLabelled();
				
				int startTime = updateFromLastPrev ? 
						lastPrevRawChunk.getStartTime() : lastRawChunk.getStartTime();
				int endTime = 3600 * 24;
				ArrayList<RawChunk> rawChunks = new ArrayList<RawChunk>();				
				createRawChunkData(startTime, endTime, rawChunks);	
				if (rawChunks.size() > 0) {
					assert(rawChunks.get(0).getStartTime() == startTime);
					// remove the last 
					sRawChksWrap.remove(sRawChksWrap.size() - 1);
					if (updateFromLastPrev) {
						sRawChksWrap.remove(sRawChksWrap.size() - 1);
					}
					// add new raw chunks
					sRawChksWrap.addAll(rawChunks);
				}
			} else {
				if (!createRawChunkData()) {
					return ERR_NO_CHUNK_DATA;			
				}
			}
		}
		
		/*
		 * finally load the label data if it exists, we use it to draw text 
		 * hints on the graph for helping user remember what he/she did before
		 */
		loadLabelData(date);
			
		return LOADING_SUCCEEDED;
	}

	private static void onAddAccelData(int hour, int minute, int second, int milliSecond, 
				     			int timeInSec, int accelAverage, int accelSamples) {
		AccelData data = new AccelData(hour, minute, second, milliSecond, 
				timeInSec, accelAverage, accelSamples);
		sHourlyAccelData.add(data);
	}
	
	private static void onAddLabelData(int hour, int minute, int second, int timeInSec, String text) {
		LabelData data = new LabelData(hour, minute, second, timeInSec, text);
		sLabelWrap.add(data);
	}
	
	public static String getCurrentSelectedDate() {
		return DataStorage.GetValueString(sContext, USCTeensGlobals.CURRENT_SELECTED_DATE, "");
	}
	
/////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static int[] getDrawableData() {
		return sAccelDataWrap.getDrawableData();
	}
	
	public static int getDrawableDataLengthInPixel() {
		return sAccelDataWrap.getDrawableDataLength() * USCTeensGlobals.PIXEL_PER_DATA;
	}
	
	public static int getMaxDrawableDataValue() {
		int max = sAccelDataWrap.getMaxDrawableDataValue();
		return max < MINIMUM_SENSOR_DATA_VALUE ? MINIMUM_SENSOR_DATA_VALUE : max;
	}
	
	public static ArrayList<Pair<Integer, Integer>> getNoDataTimePeriods() {
		return sAccelDataWrap.getNoDataTimePeriods();
	}
/////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static RawChunksWrap getRawChunks() {
		return sRawChksWrap;
	}
	
	private static boolean loadRawAccelData(String date) {
		String[] hourDirs = FileHelper.getFilePathsDir(
				Globals.EXTERNAL_DIRECTORY_PATH + File.separator + 
				Globals.DATA_DIRECTORY + USCTeensGlobals.SENSOR_FOLDER + date);
		if (hourDirs == null || hourDirs.length == 0) {			
			return false;
		}
		// first clear the data container
		sAccelDataWrap.clear();
		// load the daily data from csv files hour by hour		
		for (int i = 0; i < hourDirs.length; ++i) {
			// each hour corresponds to one csv file
			String[] filePath = FileHelper.getFilePathsDir(hourDirs[i]);			
			// load the hourly data from csv file and save the data to mHourlyAccelData
			sHourlyAccelData = new ArrayList<AccelData>();
			loadHourlyAccelSensorData(filePath[0]);
			// add the houly data the data wrap
			sAccelDataWrap.add(sHourlyAccelData);
		}		
		// now we have a loaded daily accelerometer sensor data in the data wrap,
		// we convert it into the data structure that can be drawn easily.
		sAccelDataWrap.updateDrawableData();
		
		return true;
	}
	
	/**
	 * 
	 * @param date
	 * @return
	 */
	private static boolean loadRawChunkData(String date) {
		String path = Globals.EXTERNAL_DIRECTORY_PATH + File.separator + Globals.DATA_DIRECTORY + 
				USCTeensGlobals.ANNOTATION_FOLDER + date;
		String[] chunkFilePaths = FileHelper.getFilePathsDir(path);
		if (chunkFilePaths == null || chunkFilePaths.length == 0) {			
			return false;
		}	
		// first clear the data container
		sRawChksWrap.clear();		
		SAXReader saxReader = new SAXReader();				
		try {
			Document document = saxReader.read(new File(chunkFilePaths[0]));				
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
			    	   for (Iterator n = prop.attributeIterator(); n.hasNext();) {
			    	       Attribute attribute = (Attribute) n.next();			    	       
			    	       if (attribute.getName().compareTo("LAST_MODIFIED") == 0) {
			    	    	   modify = attribute.getText();
			    	       } else if (attribute.getName().compareTo("DATE_CREATED") == 0) {
			    	    	   create = attribute.getText(); 	    	   
			    	       }
			    	   }
			    	   
			    	   RawChunk rawchunk = new RawChunk(
			    			   start.getText(), stop.getText(), label.getText(), create, modify);
			    	   sRawChksWrap.add(rawchunk);
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
	
	private static boolean loadLabelData(String date) {
		String path = Globals.EXTERNAL_DIRECTORY_PATH + File.separator + Globals.DATA_DIRECTORY + 
				USCTeensGlobals.LABELS_FOLDER + date;
		String[] labelFilePaths = FileHelper.getFilePathsDir(path);
		if (labelFilePaths == null || labelFilePaths.length == 0) {			
			return false;
		}
		
		// first clear the data container
		sLabelWrap.clear();
		// load the daily data from the csv file	
		loadDailyLabelData(labelFilePaths[0]);		
		
		return sLabelWrap.size() > 0;
	}

	/**
	 * Create new raw chunks and add it to the raw chunk wrap
	 * @param startSecond	The start position to analyze the sensor data.
	 * @return true if successful, otherwise false
	 */
	private static int createRawChunkData(int startSecond, int stopSecond, ArrayList<RawChunk> rawChunks) {
		if (sAccelDataWrap.size() == 0 || rawChunks == null) {
			return 0;
		}
		
		int[] chunkPos = createDailyRawChunkData(startSecond, stopSecond, sAccelDataWrap.getDrawableData());
		
		// current selected date		
		String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());		

		// create raw chunk data for each chunking position	
		rawChunks.clear();
		for (int i = 0; i < chunkPos.length - 1; ++i) {						
			rawChunks.add(new RawChunk(today, chunkPos[i], chunkPos[i + 1]));
		}
						
		return rawChunks.size();
	}
	
	/*
	 * create raw chunk data from raw accelerometer data	 
	 */
	protected static boolean createRawChunkData() {
		int size = createRawChunkData(0, 3600 * 24, sRawChksWrap);		
		return size > 0;
	}
	
	public static boolean areAllChunksLabelled(String date) {
		String path = Globals.EXTERNAL_DIRECTORY_PATH + File.separator + Globals.DATA_DIRECTORY + 
				USCTeensGlobals.ANNOTATION_FOLDER + date + File.separator + USCTeensGlobals.ANNOTATION_SET + "." + 
				/*PhoneInfo.getID(mContext) + "." + */date + ".annotation.xml";
		
		File file = new File(path);		
		if (!file.exists()) {			
			return false;
		}
		
		boolean isAllLabelled = false;
		SAXReader saxReader = new SAXReader();		
		try {
			Document document = saxReader.read(new File(path));								
			Element root = document.getRootElement();

		    for (Iterator i = root.elementIterator(); i.hasNext();) {
		       Element annotations = (Element) i.next();		       
	    	   for (Iterator n = annotations.attributeIterator(); n.hasNext();) {
	    	       Attribute attribute = (Attribute) n.next();
	    	       
	    	       if (attribute.getName().compareTo("ALL_LABELLED") == 0) {
	    	    	   String text = attribute.getText();
	    	    	   isAllLabelled = text.compareToIgnoreCase("yes") == 0;
	    	       } 
	    	    }	    	
		    }
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();			
		}
		
		return isAllLabelled;
	}

	public static boolean saveChunkData(final ArrayList<Chunk> chunks) {
		boolean result = false;		
		String date = DataStorage.GetValueString(sContext, USCTeensGlobals.CURRENT_SELECTED_DATE, "");
		assert(date.compareTo("") != 0);
		String path = Globals.EXTERNAL_DIRECTORY_PATH + File.separator + Globals.DATA_DIRECTORY + 
				USCTeensGlobals.ANNOTATION_FOLDER + date + File.separator + USCTeensGlobals.ANNOTATION_SET + "." + 
				/*PhoneInfo.getID(mContext) + "." + */date + ".annotation.xml";			

		sRawChksWrap.clear();
		for (int i = 0; i < chunks.size(); ++i) {
			Chunk chunk = chunks.get(i);
			RawChunk rawChunk = chunk.toRawChunk();
			sRawChksWrap.add(rawChunk);
		}
		
		Document document = DocumentHelper.createDocument();
        
		// ACTIVITYDATA
        Element root = document.addElement("ACTIVITYDATA");
        root.addAttribute("xmlns", "urn:mites-schema");
        // ANNOTATIONS
        Element annotations = root.addElement("ANNOTATIONS")
	        .addAttribute("DATASET", "USCTeens")
	        .addAttribute("ANNOTATOR", "bigbug")
	        .addAttribute("EMAIL", "bigbugbb@gmail.com")
	        .addAttribute("DESCRIPTION", "teen activities")
	        .addAttribute("METHOD", "based on convolution & pre-defined thresholds")
	        .addAttribute("NOTES", "")
	        .addAttribute("ALL_LABELLED", sRawChksWrap.areAllChunksLabelled() ? "yes" : "no");
        
        for (RawChunk rawChunk : sRawChksWrap) {
        	int index = USCTeensGlobals.ACTIONS_GUID.length - 1; // unlabelled
        	for (int i = 0; i < USCTeensGlobals.ACTION_NAMES.length; ++i) {
        		String action = USCTeensGlobals.ACTION_NAMES[i];
        		if (rawChunk.mActivity.compareToIgnoreCase(action) == 0) {
        			index = i;
        		}
        	}
	        // ANNOTATION
	        Element annotation = annotations.addElement("ANNOTATION")
	        	.addAttribute("GUID", USCTeensGlobals.ANNOTATION_GUID);
	        // LABEL
	        Element label = annotation.addElement("LABEL")
		        .addAttribute("GUID", USCTeensGlobals.ACTIONS_GUID[index])
		        .addText(rawChunk.mActivity);
	        // START_DT
	        Element start_dt = annotation.addElement("START_DT")
	        	.addText(rawChunk.mStartDate);
	        // STOP_DT
	        Element stop_dt = annotation.addElement("STOP_DT")
	        	.addText(rawChunk.mStopDate);
	        // PROPERTIES
	        Element properties = annotation.addElement("PROPERTIES")
		        .addAttribute("ANNOTATION_SET", "Teen activity study")		       
		        .addAttribute("LAST_MODIFIED", rawChunk.mModifyTime)
		        .addAttribute("DATE_CREATED",  rawChunk.mCreateTime);
        }
                
        XMLWriter writer;
		try {
			File aFile = new File(path);							
			FileHelper.createDirsIfDontExist(aFile);
			// Create the file if it does not exist
			if (!aFile.exists()) {					
				aFile.createNewFile();
			}
			// The file will be truncated if it exists, and created if it doesn't exist.			
			writer = new XMLWriter(new FileOutputStream(path), new OutputFormat("    ", true));			
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
		} catch (WOCKETSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}                   
		
		return result;
	}
	
	private static native int create();
	private static native int destroy();
	private static native int loadHourlyAccelSensorData(String filePath);
	private static native int unloadActivityData(String path);
	private static native int[] createDailyRawChunkData(int startTime, int stopTime, int[] sensorData);
	private static native int loadDailyLabelData(String filePath);
	private static native int getMaxActivityValue(String path);
}
