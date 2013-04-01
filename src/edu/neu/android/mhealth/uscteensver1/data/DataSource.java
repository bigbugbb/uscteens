package edu.neu.android.mhealth.uscteensver1.data;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

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
import edu.neu.android.mhealth.uscteensver1.utils.WeekdayCalculator;
import edu.neu.android.wocketslib.Globals;
import edu.neu.android.wocketslib.support.DataStorage;
import edu.neu.android.wocketslib.utils.FileHelper;
import edu.neu.android.wocketslib.utils.Log;
import edu.neu.android.wocketslib.utils.WOCKETSException;

public class DataSource {
	private final static String TAG = "DataSource";
	// result code
	public final static int LOADING_SUCCEEDED  		= 0;
	public final static int ERR_CANCELLED           = 1;
	public final static int ERR_NO_SENSOR_DATA 		= 2;
	public final static int ERR_NO_CHUNK_DATA  		= 3;	
	public final static int ERR_WAITING_SENSOR_DATA = 4;
	
	// thresholds for chunking generation
	public final static int CHUNKING_MIN_MEAN_AVG         = 350;
	public final static int CHUNKING_MIN_MEAN_AVG_DIFF    = 350;
	public final static int CHUNKING_MAX_MEAN_AVG_DIFF    = 800;
	public final static int CHUNKING_MEAN_AVG_DISTANCE    = 60;
	public final static int CHUNKING_MIN_SENSITIVITY      = 400;
	public final static int CHUNKING_MAX_SENSITIVITY      = 999;
	public final static int CHUNKING_MIN_DISTANCE 		  = 120;
		
	// value for minimum sensor data
	protected final static int MINIMUM_SENSOR_DATA_VALUE = 1800;
	
	// value for no data period
	protected final static int NO_SENSOR_DATA = -1;
	
	public final static String INTERNAL_ACCEL_DATA_CSVFILEHEADER = 
			"DateTime, Milliseconds, InternalAccelAverage, InternalAccelSamples\n";
	
	public final static String INTERNAL_LABEL_DATA_CSVFILEHEADER = 
			"DateTime, Text\n";
	
	protected static Context sContext = null;
	// boolean to indicate whether the loading should be cancelled
	protected static boolean sCancelled = false;
	// raw chunk data
	protected static RawChunksWrap sRawChksWrap = new RawChunksWrap();
	// raw accelerometer sensor data
	protected static AccelDataWrap sAccelDataWrap = new AccelDataWrap();
//	// hourly accelerometer data
//	protected static ArrayList<AccelData> sHourlyAccelData = null;	
	// floating labels data
	protected static RawLabelWrap sRawLabelsWrap = new RawLabelWrap();
	
	
//	static {
//		System.loadLibrary("datasrc");
//	}		
	
	public static void initialize(Context context) {
		sContext = context;		
	}
	
	public static long getLastLoadingTime() {
		long lastLoadingTime = 
				DataStorage.GetValueLong(sContext, USCTeensGlobals.LAST_DATA_LOADING_TIME, 0);	
		return lastLoadingTime;
	}
	
	public static void cancelLoading() {
		sCancelled = true;
	}
	
	public static boolean updateRawData() {	
		boolean result = false;
		long currentTime = System.currentTimeMillis();
		long lastLoadingTime = DataSource.getLastLoadingTime();		
		
		if (currentTime - lastLoadingTime > USCTeensGlobals.UPDATING_TIME_THRESHOLD) {			
			try {				
				String select = DataStorage.GetValueString(
					sContext, USCTeensGlobals.CURRENT_SELECTED_DATE, "2013-01-01"
				);
				Date curDate  = new Date(currentTime);
				Date loadDate = new Date(lastLoadingTime);		
				Date selDate  = new SimpleDateFormat("yyyy-MM-dd").parse(select);		

				if (WeekdayCalculator.isSameDay(selDate, curDate) || 
						!WeekdayCalculator.isSameDay(loadDate, curDate)) {
					// the selected date is the same day as the current date, 
					// OR date crossing case					
					if (DataSource.loadRawData(select) == DataSource.LOADING_SUCCEEDED) {
						result = true;
					}					
				}
			} catch (ParseException e) {				
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
	/**
	 * 	 
	 * @param date	YYYY-MM-DD
	 * @return
	 */
	public static int loadRawData(String date) {
		DataStorage.SetValue(sContext, USCTeensGlobals.CURRENT_SELECTED_DATE, date);
		
		sCancelled = false;
		
		/* 
		 * first load the accelerometer sensor data
		 */
		int result = loadRawAccelData(date);
		
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
		
		// note the last time for loading data, used to indicate whether
		// the data should be reloaded after the user has switched to another program
		// and go back here after a while.
		DataStorage.SetValue(sContext, 
				USCTeensGlobals.LAST_DATA_LOADING_TIME, System.currentTimeMillis());
			
		return result;
	}

//	private static void onAddAccelData(int hour, int minute, int second, int milliSecond, 
//				     			int timeInSec, int accelAverage, int accelSamples) {
//		AccelData data = new AccelData(hour, minute, second, milliSecond, 
//				timeInSec, accelAverage, accelSamples);
//		sHourlyAccelData.add(data);
//	}
	
//	private static void onAddLabelData(int hour, int minute, int second, int timeInSec, String text) {
//		RawLabel data = new RawLabel(hour, minute, second, timeInSec, text);
//		sRawLabelsWrap.add(data);
//	}
	
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
	
	public static RawLabelWrap getRawLabels() {
		return sRawLabelsWrap;
	}
	
	public static RawChunksWrap getRawChunks() {
		return sRawChksWrap;
	}
	
	private static int loadHourlyRawAccelData(String filePath, ArrayList<AccelData> hourlyAccelData) {
		// get extension name indicating which type of file we should read from
		String extName = filePath.substring(filePath.lastIndexOf("."), filePath.length());
		
		// first clear the data container
//		hourlyAccelData.clear();
		
		// load the daily data from the csv file	
//		loadDailyLabelData(labelFilePaths[0]);
		if (extName.equals(".bin")) {
			File binFile = new File(filePath);	
			ObjectInputStream ois = null;
			try { 
				ois = new ObjectInputStream(new FileInputStream(binFile));
				Object obj = ois.readObject();
				while (obj != null) {		
					if (sCancelled) {
						return ERR_CANCELLED;
					}
					if (obj instanceof AccelData) {
						AccelData data = (AccelData) obj;
						hourlyAccelData.add(data); 						
					}	
					obj = ois.readObject();
				}
			} catch (EOFException e) {
				;//e.printStackTrace();
			} catch (Exception e) { 
				e.printStackTrace();
			} finally {
				try {
					ois.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else if (extName.equals(".csv")) { // .csv file if .bin does not exist		
			FileInputStream fis = null;
			BufferedReader br = null;
			File csvFile = new File(filePath);
			try {
				fis = new FileInputStream(csvFile);
				InputStreamReader in = new InputStreamReader(fis);
				br = new BufferedReader(in);			
				try {
					// skip the first line
					String result = br.readLine();
					while ((result = br.readLine()) != null) {
						if (sCancelled) {
							return ERR_CANCELLED;
						}
						// parse the line
						String[] split = result.split("[ :.,]");
						AccelData data = new AccelData(split[1], split[2], split[3], split[5], split[7], split[9]);
						hourlyAccelData.add(data);
					}
				} catch (IOException e) {
					Log.e(TAG, "readStringInternal: problem reading: " + csvFile.getAbsolutePath());
					e.printStackTrace();
				}
			} catch (FileNotFoundException e) {
				Log.e(TAG, "readStringInternal: cannot find: " + csvFile.getAbsolutePath());
				e.printStackTrace();
			} finally {
				if (br != null)
					try {
						br.close();
					} catch (IOException e) {
						Log.e(TAG, "readStringInternal: cannot close: " + csvFile.getAbsolutePath());
						e.printStackTrace();
					}
				if (fis != null)
					try {
						fis.close();
					} catch (IOException e) {
						Log.e(TAG, "readStringInternal: cannot close: " + csvFile.getAbsolutePath());
						e.printStackTrace();
					}
			}
		}
		
		return hourlyAccelData.size();
	}
	
	private static int loadRawAccelData(String date) {
		String[] hourDirs = FileHelper.getFilePathsDir(
				Globals.EXTERNAL_DIRECTORY_PATH + File.separator + 
				Globals.DATA_DIRECTORY + USCTeensGlobals.SENSOR_FOLDER + date);
		
		// first clear the data container
		sAccelDataWrap.clear();
		try {
			// load the daily data from .bin files hour by hour		
			for (int i = 0; i < hourDirs.length; ++i) {
				// each hour corresponds to one .bin file
				String[] filePaths = FileHelper.getFilePathsDir(hourDirs[i]);
				String filePath = filePaths[0]; // set a default value
				for (String path : filePaths) {
					String extName = path.substring(path.lastIndexOf("."), path.length());
					if (extName.equals(".bin")) {
						filePath = path;
					}
				}
				// load the hourly data from .bin file
				ArrayList<AccelData> hourlyAccelData = new ArrayList<AccelData>();						
				int result = loadHourlyRawAccelData(filePath, hourlyAccelData);	
				if (result == ERR_CANCELLED) {
					return ERR_CANCELLED;
				}
				// add the houly data the data wrap
				sAccelDataWrap.add(hourlyAccelData);
			}					
		} catch (Exception e) {
			e.printStackTrace();			
		}		
		
		// now we have a loaded daily accelerometer sensor data in the data wrap,
		// we convert it into the data structure that can be drawn easily.
		sAccelDataWrap.updateDrawableData();
		
		if (sAccelDataWrap.size() == 0) {
			String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
			if (date.compareTo(today) == 0) {
				return ERR_WAITING_SENSOR_DATA;
			} else {
				return ERR_NO_SENSOR_DATA;
			}
		}
		
		return LOADING_SUCCEEDED;
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
	 * load all labels from a label file of a specified date to the raw label wrap
	 * @param date	yyyy-MM-dd
	 * @param rawLabelWrap
	 * @return true if the raw label wrap has label data, otherwise false
	 */
	public static boolean loadLabelData(String date, RawLabelWrap rawLabelWrap, boolean alwaysLoad) {
		String path = Globals.EXTERNAL_DIRECTORY_PATH + File.separator + Globals.DATA_DIRECTORY + 
				USCTeensGlobals.LABELS_FOLDER + date;
		String[] labelFilePaths = FileHelper.getFilePathsDir(path);
		if (labelFilePaths == null || labelFilePaths.length == 0) {			
			return false;
		}
		
		// check if the date is loaded
		if (rawLabelWrap.isDateLoaded(date) && !alwaysLoad) {
			return true;
		}
		
		// first clear the data container		
		rawLabelWrap.clear();
		rawLabelWrap.setDate(date);
		
		// load the daily data from the csv file	
//		loadDailyLabelData(labelFilePaths[0]);
		String result = null;
		File labelFile = new File(labelFilePaths[0]);
		FileInputStream fis = null;
		BufferedReader br = null;
		try {
			fis = new FileInputStream(labelFile);
			InputStreamReader in = new InputStreamReader(fis);
			br = new BufferedReader(in);			
			try {
				// skip the first line
				result = br.readLine();
				while ((result = br.readLine()) != null) {
					// parse the line
					String[] split = result.split("[,]");
					rawLabelWrap.add(split[0].trim(), split[1].trim());
				}
			} catch (IOException e) {
				Log.e(TAG, "readStringInternal: problem reading: " + labelFile.getAbsolutePath());
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			Log.e(TAG, "readStringInternal: cannot find: " + labelFile.getAbsolutePath());
			e.printStackTrace();
		} finally {
			if (br != null)
				try {
					br.close();
				} catch (IOException e) {
					Log.e(TAG, "readStringInternal: cannot close: " + labelFile.getAbsolutePath());
					e.printStackTrace();
				}
			if (fis != null)
				try {
					fis.close();
				} catch (IOException e) {
					Log.e(TAG, "readStringInternal: cannot close: " + labelFile.getAbsolutePath());
					e.printStackTrace();
				}
		}
		
		return rawLabelWrap.size() > 0;
	}
	
	private static boolean loadLabelData(String date) {
		return loadLabelData(date, sRawLabelsWrap, true);
	}
	
	/**
	 * save all the labels to the label file specified by the date
	 * @param date	yyyy-MM-dd
	 * @param rawLabelWrap	labels to be saved
	 * @return true if succeed, otherwise false
	 */
	public static boolean saveLabelData(String date, RawLabelWrap rawLabelWrap) {						
		String path = Globals.EXTERNAL_DIRECTORY_PATH + File.separator + Globals.DATA_DIRECTORY + 
				USCTeensGlobals.LABELS_FOLDER + date;
		String filePathName = ""; 
		String[] labelFilePaths = FileHelper.getFilePathsDir(path);		
		if (labelFilePaths == null || labelFilePaths.length == 0) {	
			StringBuilder sb = new StringBuilder();
			sb.append(path);
			sb.append(File.separator);
			sb.append("Activities.");
			sb.append(new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS").format(new Date()));
			sb.append(".labels.csv");
			filePathName = sb.toString();
		} else {
			filePathName = labelFilePaths[0];
		}
		
		// build the content to write
		StringBuilder sb = new StringBuilder();
		sb.append(INTERNAL_LABEL_DATA_CSVFILEHEADER);
		Iterator iter = rawLabelWrap.entrySet().iterator(); 
		while (iter.hasNext()) { 
		    Map.Entry entry = (Map.Entry) iter.next(); 		    
		    RawLabel rawLabel = (RawLabel) entry.getValue(); 
		    sb.append(rawLabel.toString());
		} 
		String content = sb.toString();
		
		// load the daily data from the csv file	
//		loadDailyLabelData(labelFilePaths[0]);
		
		// First write the .csv file
		File labelFile = new File(filePathName);		
		boolean result = FileHelper.saveStringToFile(content, labelFile, false);
				
		return result;
	
	}

	/**
	 * Create new raw chunks and add it to the raw chunk wrap
	 * @param startSecond	The start position to analyze the sensor data.
	 * @return true if successful, otherwise false
	 */
	private static int createRawChunkData(int startSecond, int stopSecond, ArrayList<RawChunk> rawChunks) {
		if (rawChunks == null) {
			return 0;
		}
		
		//int[] chunkPos = createDailyRawChunkData(startSecond, stopSecond, sAccelDataWrap.getDrawableData());
		ArrayList<Integer> chunkPos = new ArrayList<Integer>();
		int[] sensorData = sAccelDataWrap.getDrawableData();
		int size = sensorData.length;
		
		// data should be enough for filling in at least one chunk space
		if (size < CHUNKING_MIN_DISTANCE) {
			return 0;
		}
		// convolution to the accelerometer data
		int[] convolution = Arrays.copyOf(sensorData, size);
		for (int i = 1; i < size - 1; ++i) { // [-1 0 1]
			convolution[i] = sensorData[i + 1] - sensorData[i - 1];
		}

		// calculate mean average of CHUNKING_MIN_DISTANCE points' of data to the left of current position
		int sum = 0;
		int[] meanAverageL = new int[size];
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
		int[] meanAverageR = new int[size];
		Arrays.fill(meanAverageR, size - CHUNKING_MEAN_AVG_DISTANCE, size, 0);		
		for (int i = size - 1; i >= size - CHUNKING_MEAN_AVG_DISTANCE; --i) {
			sum += sensorData[i];
		}
		for (int i = size - CHUNKING_MEAN_AVG_DISTANCE - 1; i >= 0; --i) {
			meanAverageR[i] = sum / CHUNKING_MEAN_AVG_DISTANCE;
			sum += sensorData[i];
			sum -= sensorData[i + CHUNKING_MEAN_AVG_DISTANCE];
		}

		// figure out the possible chunking positions
		int prev = startSecond;
		chunkPos.add(startSecond);
		for (int i = startSecond + 1; i < size - CHUNKING_MIN_DISTANCE; ++i) {
			if (i - prev < CHUNKING_MIN_DISTANCE) {
				continue;
			}
			// chunking for no data area
			if (sensorData[i] == NO_SENSOR_DATA) {
				if (sensorData[i - 1] != NO_SENSOR_DATA || sensorData[i + 1] != NO_SENSOR_DATA) {
					chunkPos.add(i);
					prev = i;
					continue;
				}
			}
			// chunking for data change area
			if (convolution[i] > CHUNKING_MIN_SENSITIVITY &&
				sensorData[i] > CHUNKING_MIN_SENSITIVITY && sensorData[i] < CHUNKING_MAX_SENSITIVITY) {
				if (Math.abs(meanAverageL[i] - meanAverageR[i]) > CHUNKING_MIN_MEAN_AVG_DIFF) {
					chunkPos.add(i);
					prev = i;
					continue;
				}
			}
			// chunking for separated sensor data with huge value
			if (sensorData[i] > CHUNKING_MAX_SENSITIVITY) {
				int next = i + CHUNKING_MIN_DISTANCE;
				if (meanAverageL[i] < CHUNKING_MIN_MEAN_AVG && meanAverageR[next] < CHUNKING_MIN_MEAN_AVG) {
					chunkPos.add(i);
				} else {
					continue;
				}
				if (next < size - CHUNKING_MIN_DISTANCE) {
					chunkPos.add(next);
					prev = next;
				} else {
					prev = i;
				}
			}
		}
		chunkPos.add(stopSecond);		
		
		// current selected date		
		String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());		

		// create raw chunk data for each chunking position	
		rawChunks.clear();
		for (int i = 0; i < chunkPos.size() - 1; ++i) {						
			rawChunks.add(new RawChunk(today, chunkPos.get(i), chunkPos.get(i + 1)));
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
	
//	private static native int loadHourlyAccelSensorData(String filePath);
//	private static native int[] createDailyRawChunkData(int startTime, int stopTime, int[] sensorData);
//	private static native int loadDailyLabelData(String filePath);
}
