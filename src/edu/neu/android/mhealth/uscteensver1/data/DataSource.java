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
import edu.neu.android.wocketslib.utils.PhoneInfo;
import edu.neu.android.wocketslib.utils.WOCKETSException;

import android.content.Context;
import android.util.Pair;
import android.widget.Toast;

public class DataSource {
	// result code
	public final static int LOADING_SUCCEEDED  = 0;
	public final static int ERR_NO_SENSOR_DATA = 1;
	public final static int ERR_NO_CHUNK_DATA  = 2;
	
	// threshold for chunking generation
	private final static int CHUNKING_MEAN_AVG_DIFF     = 400;
	private final static int CHUNKING_MEAN_AVG_DISTANCE = 60;
	private final static int CHUNKING_MIN_SENSITIVITY   = 250;
	private final static int CHUNKING_MAX_SENSITIVITY   = 900;
	private final static int CHUNKING_MIN_DISTANCE 		= 120;
	
	protected static Context sContext = null;
	// raw chunk data
	protected static RawChunksWrap sRawChksWrap = new RawChunksWrap();
	// raw accelerometer sensor data
	protected static AccelDataWrap sAccelDataWrap = new AccelDataWrap();
	// hourly accelerometer data
	protected static ArrayList<AccelData> sHourlyAccelData = null;	
	
	
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
				boolean updateFromLastPrev = lastPrevRawChunk != null && !lastPrevRawChunk.isModified();
				
				int startTime = updateFromLastPrev ? 
						lastPrevRawChunk.getStartTime() : lastRawChunk.getStartTime();						
				ArrayList<RawChunk> rawChunks = new ArrayList<RawChunk>(); 
				createRawChunkData(startTime, rawChunks);	
				if (rawChunks != null && rawChunks.size() > 0) {
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
			
		return LOADING_SUCCEEDED;
	}

	private static void onGetAccelData(int hour, int minute, int second, int milliSecond, 
				     			int timeInSec, int accelAverage, int accelSamples) {
		AccelData acData = new AccelData(hour, minute, second, milliSecond, 
				timeInSec, accelAverage, accelSamples);
		sHourlyAccelData.add(acData);
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
		return sAccelDataWrap.getMaxDrawableDataValue();
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

	/**
	 * Create new raw chunks and add it to the raw chunk wrap
	 * @param startSecond	The start position to analyze the sensor data.
	 * @return true if successful, otherwise false
	 */
	protected static boolean createRawChunkData(int startSecond, ArrayList<RawChunk> rawChunks) {
		if (sAccelDataWrap.size() == 0 || rawChunks == null) {
			return false;
		}
		// current selected date
		String today = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());		
		// convolution to the accelerometer data
		int[] sensorData = sAccelDataWrap.getDrawableData();
		int size = sensorData.length;
		int[] convolution = Arrays.copyOf(sensorData, size);
		for (int i = 1; i < size - 1; ++i) { // [-1 0 1]
			convolution[i] = sensorData[i + 1] - sensorData[i - 1];
		}
		// calculate mean average of CHUNKING_MIN_DISTANCE points' of data to the left of current position 
		int sum = 0;
		int[] meanAverageL = Arrays.copyOf(sensorData, size);
		Arrays.fill(meanAverageL, 0, CHUNKING_MEAN_AVG_DISTANCE, 0);
		for (int i = 0; i < CHUNKING_MEAN_AVG_DISTANCE; ++i) {
			sum += sensorData[i];
		}
		for (int i = CHUNKING_MEAN_AVG_DISTANCE; i < size; ++i) {
			meanAverageL[i] = sum / CHUNKING_MEAN_AVG_DISTANCE;
			sum += sensorData[i];
			sum -= sensorData[i - CHUNKING_MEAN_AVG_DISTANCE];
		}
		// calculate mean average of CHUNKING_MIN_DISTANCE points' of data to the right of current position
		sum = 0;
		int[] meanAverageR = Arrays.copyOf(sensorData, sensorData.length);
		Arrays.fill(meanAverageR, sensorData.length - CHUNKING_MEAN_AVG_DISTANCE, sensorData.length, 0);
		for (int i = sensorData.length - 1; i >= size - CHUNKING_MEAN_AVG_DISTANCE; --i) {
			sum += sensorData[i];
		}
		for (int i = size - CHUNKING_MEAN_AVG_DISTANCE - 1; i >= 0; --i) {
			meanAverageR[i] = sum / CHUNKING_MEAN_AVG_DISTANCE;
			sum += sensorData[i];
			sum -= sensorData[i + CHUNKING_MEAN_AVG_DISTANCE];
		}
		// figure out the possible chunking positions
		int prev = 0, end = 3600 * 24;
		ArrayList<Integer> chunkPos = new ArrayList<Integer>();
		chunkPos.add(startSecond);		
		for (int i = startSecond + 1; i < size - CHUNKING_MIN_DISTANCE; ++i) {
			if (sensorData[i] == AccelDataWrap.NO_SENSOR_DATA) {
				if (sensorData[i - 1] != AccelDataWrap.NO_SENSOR_DATA || 
					sensorData[i + 1] != AccelDataWrap.NO_SENSOR_DATA) {
					if (i - prev >= CHUNKING_MIN_DISTANCE) {
						chunkPos.add(i);			
						prev = i;
					}
				}
			}
			if (Math.abs(convolution[i]) > CHUNKING_MIN_SENSITIVITY && i - prev >= CHUNKING_MIN_DISTANCE) {
				if (Math.abs(meanAverageL[i] - meanAverageR[i]) > CHUNKING_MEAN_AVG_DIFF) {
					chunkPos.add(i);
					prev = i;				
				}
			}	
		}
		chunkPos.add(end); 
		// create raw chunk data for each chunking position	
		rawChunks.clear();
		for (int i = 0; i < chunkPos.size() - 1; ++i) {					
			RawChunk rawChunk = new RawChunk(today, chunkPos.get(i), chunkPos.get(i + 1));
			rawChunks.add(rawChunk);
		}
				
		return true;
	}
	
	/*
	 * create raw chunk data from raw accelerometer data	 
	 */
	protected static boolean createRawChunkData() {
		boolean result = createRawChunkData(0, sRawChksWrap);		
		return result;
	}
	
	public static boolean areAllChunksLabelled(String date) {
		String path = Globals.EXTERNAL_DIRECTORY_PATH + File.separator + Globals.DATA_DIRECTORY + 
				USCTeensGlobals.ANNOTATION_FOLDER + date + File.separator + USCTeensGlobals.ANNOTATION_SET + "." + 
				/*PhoneInfo.getID(mContext) + "." + */date + ".annotation.xml";
		
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
	    	       
	    	       if (attribute.getName().compareTo("ALL_LABELLED") == 0) {
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
	private static native int getMaxActivityValue(String path);
}
