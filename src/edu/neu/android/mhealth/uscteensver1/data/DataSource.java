package edu.neu.android.mhealth.uscteensver1.data;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import android.util.Log;
import android.util.Pair;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import edu.neu.android.mhealth.uscteensver1.TeensAppManager;
import edu.neu.android.mhealth.uscteensver1.TeensGlobals;
import edu.neu.android.mhealth.uscteensver1.extra.Action;
import edu.neu.android.mhealth.uscteensver1.extra.ActionManager;
import edu.neu.android.wocketslib.Globals;
import edu.neu.android.wocketslib.algorithm.ChunkingAlgorithm;
import edu.neu.android.wocketslib.mhealthformat.AnnotationSaver;
import edu.neu.android.wocketslib.support.DataStorage;
import edu.neu.android.wocketslib.utils.DateHelper;
import edu.neu.android.wocketslib.utils.FileHelper;
import edu.neu.android.wocketslib.utils.WeekdayHelper;

public class DataSource {
	private final static String TAG = "DataSource";
	// result code
	public final static int LOADING_SUCCEEDED  		= 0;
	public final static int ERR_CANCELLED           = -1;
	public final static int ERR_NO_SENSOR_DATA 		= -2;
	public final static int ERR_NO_CHUNK_DATA  		= -3;	
	public final static int ERR_WAITING_SENSOR_DATA = -4;		
	
	public final static String INTERNAL_LABEL_DATA_CSVFILEHEADER = "DateTime, Text\n";
	
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
	
	public static long getLastLoadingTime() {
		long lastLoadingTime = 
				DataStorage.GetValueLong(TeensAppManager.getAppContext(), TeensGlobals.LAST_DATA_LOADING_TIME, 0);	
		return lastLoadingTime;
	}
	
	public static void cancelLoading() {
		sCancelled = true;
	}
	
	public static boolean updateRawData() {	
		boolean result = false;
		long currentTime = System.currentTimeMillis();
		long lastLoadingTime = DataSource.getLastLoadingTime();		
		
		if (currentTime - lastLoadingTime > TeensGlobals.REFRESH_DATA_TIME_THRESHOLD) {			
			try {				
				String select = DataStorage.GetValueString(
					TeensAppManager.getAppContext(), TeensGlobals.CURRENT_SELECTED_DATE, "2013-01-01"
				);
				Date curDate  = new Date(currentTime);
				Date loadDate = new Date(lastLoadingTime);		
				Date selDate  = new SimpleDateFormat("yyyy-MM-dd").parse(select);		

				if (WeekdayHelper.isSameDay(selDate, curDate) || 
						!WeekdayHelper.isSameDay(loadDate, curDate)) {
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
		DataStorage.SetValue(TeensAppManager.getAppContext(), TeensGlobals.CURRENT_SELECTED_DATE, date);
		
		sCancelled = false;
		
		clearRawData();
		/* 
		 * first load the accelerometer sensor data
		 */		
		int result = loadRawAccelData(date);
		if (result != LOADING_SUCCEEDED && result != ERR_NO_SENSOR_DATA) {
			return result;
		}
		
		/* 
		 * then load the corresponding chunk data.
		 * if no chunk data, create the chunk data from sensor data
		 */
		String curDate = DateHelper.serverDateFormat.format(new Date());
		if (!date.equals(curDate)) {
			// the previous day's data are all available, just read it.
			// create the annotation file if it does not exist.
			if (!loadRawChunkData(date) && 
					createRawChunkData(0, TeensGlobals.DAILY_LAST_SECOND, sRawChksWrap) <= 0) {
				return ERR_NO_CHUNK_DATA;			
			}
		} else {
			if (loadRawChunkData(date)) {
				assert(sRawChksWrap.size() > 0);
				RawChunk lastRawChunk = sRawChksWrap.get(sRawChksWrap.size() - 1);
				RawChunk lastPrevRawChunk = sRawChksWrap.size() > 1 ? sRawChksWrap.get(sRawChksWrap.size() - 2) : null;		
				boolean updateFromLastPrev = lastPrevRawChunk != null && !lastPrevRawChunk.isLabelled();
				
				int startTime = updateFromLastPrev ? lastPrevRawChunk.getStartTime() : lastRawChunk.getStartTime();
				int stopTime  = TeensGlobals.DAILY_LAST_SECOND;
				ArrayList<RawChunk> rawChunks = new ArrayList<RawChunk>();				
				createRawChunkData(startTime, stopTime, rawChunks);	
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
				if (createRawChunkData(0, TeensGlobals.DAILY_LAST_SECOND, sRawChksWrap) <= 0) {
					return ERR_NO_CHUNK_DATA;			
				}
			}
		}
		
		if (sCancelled) {
			return ERR_CANCELLED;
		}
		
		/*
		 * finally load the label data if it exists, we use it to draw text 
		 * hints on the graph for helping user remember what he/she did before
		 */
		loadLabelData(date);
		
		/* 
		 * note the last time for loading data, used to indicate whether
		 * the data should be reloaded after the user has switched to another program
		 * and go back here after a while.
		 */
		DataStorage.SetValue(TeensAppManager.getAppContext(), 
				TeensGlobals.LAST_DATA_LOADING_TIME, System.currentTimeMillis());
			
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
		return DataStorage.GetValueString(TeensAppManager.getAppContext(), TeensGlobals.CURRENT_SELECTED_DATE, "");
	}
	
/////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static int[] getDrawableData() {
		return sAccelDataWrap.getDrawableData();
	}
	
	public static int getDrawableDataLengthInPixel() {
		return sAccelDataWrap.getDrawableDataLength() * TeensGlobals.PIXEL_PER_DATA;
	}
	
	public static int getMaxDrawableDataValue() {
		return Globals.MAX_ACTIVITY_DATA_SCALE;
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
	
	public static int loadHourlyRawAccelData(String filePath, ArrayList<AccelData> hourlyAccelData, boolean cancelable) {
		
		CSVReader csvReader = null;
		try {								
			csvReader = new CSVReader(new FileReader(filePath));			
			String[] row = csvReader.readNext();
			while ((row = csvReader.readNext()) != null) {
				// hack the split position
				AccelData data = new AccelData(
						row[0].substring(11, 13), 
						row[0].substring(14, 16), 
						row[0].substring(17, 19), 
						row[0].substring(20),
						row[1], row[2]);
				hourlyAccelData.add(data);
			}
		} catch (IOException e) {				
			e.printStackTrace();
		} finally {
			try {
				if (csvReader != null) {
					csvReader.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return hourlyAccelData.size();
	}

	private static int loadRawAccelData(String date) {
		String[] hourDirs = FileHelper.getFilePathsDir(
			TeensGlobals.DIRECTORY_PATH + File.separator + Globals.DATA_DIRECTORY + TeensGlobals.SENSOR_FOLDER + date
		);
		
		try {
			// load the daily data from csv files hour by hour		
			for (int i = 0; i < hourDirs.length; ++i) {				
				String[] fileNames = new File(hourDirs[i]).list(new FilenameFilter() {
					@Override
					public boolean accept(File dir, String filename) {	
						return filename.endsWith(".csv") && 
								filename.startsWith(Globals.SENSOR_TYPE_PHONE_ACCELEROMETER);
					}
				});
				if (fileNames == null || fileNames.length == 0) {
					continue;
				}
				String filePath = hourDirs[i] + File.separator + fileNames[0];
				// load the hourly data from .bin file
				ArrayList<AccelData> hourlyAccelData = new ArrayList<AccelData>();						
				loadHourlyRawAccelData(filePath, hourlyAccelData, true);	
				if (sCancelled) {
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
			String today = DateHelper.serverDateFormat.format(new Date());
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
		String path = TeensGlobals.DIRECTORY_PATH + File.separator + Globals.DATA_DIRECTORY + TeensGlobals.SENSOR_FOLDER + date;
		String[] fileNames = new File(path).list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				return filename.startsWith(DATASET) && filename.endsWith(".annotation.xml");
			}
		});
		
		if (fileNames == null || fileNames.length == 0) {		
			return false;
		}
		
		String filePath = path + File.separator + fileNames[0];
		if (!new File(filePath).exists()) {	
			return false;
		}
		
		SAXReader saxReader = new SAXReader();				
		try {
			Document document = saxReader.read(new File(filePath));				
			Element annotations = document.getRootElement();
		   
	        for (Iterator i = annotations.elementIterator(); i.hasNext();) {
	    	    Element annotation = (Element) i.next();
	    	    for (Iterator j = annotation.elementIterator(); j.hasNext();) {
		    	    Element label = (Element) j.next();
		    	    Element start = (Element) j.next();
		    	    Element stop  = (Element) j.next();
		    	    Element prop  = (Element) j.next();			    	
		    	   
		    	    String guid = "";
		    	    for (Iterator n = label.attributeIterator(); n.hasNext();) {
		    	        Attribute attribute = (Attribute) n.next();			    	       
		    	        if (attribute.getName().equals("GUID")) {
		    	    	    guid = attribute.getText();
		    	        } 
		    	    }
		    	   
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
		    	    Action action = ActionManager.getAction(guid);			    	   
		    	    RawChunk rawchunk = new RawChunk(start.getText(), stop.getText(), action, create, modify);
		    	    sRawChksWrap.add(rawchunk);
		    	    Log.d(TAG, "read\t" + rawchunk.getStartTimeInString() + "\t" + rawchunk.getStopTimeInString());
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
	public static boolean loadLabelData(String date, RawLabelWrap rawLabelWrap) {
		String path = TeensGlobals.DIRECTORY_PATH + File.separator + Globals.APP_DATA_DIRECTORY + TeensGlobals.LABELS_FOLDER + date + File.separator;
		
		// first clear the data container		
		rawLabelWrap.clear();
		rawLabelWrap.setDate(date);
					
		// get the csv file path
		String[] fileNames = new File(path).list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				return filename.endsWith(".csv");
			}
		});
		if (fileNames == null || fileNames.length == 0) {
			return false;
		}
		String filePath = path + fileNames[0];
		
		CSVReader csvReader = null;
		try {								
			csvReader = new CSVReader(new FileReader(filePath));
			String[] row = csvReader.readNext();
			while ((row = csvReader.readNext()) != null) {				
				rawLabelWrap.add(row[0].trim(), row[1].trim());
			}
		} catch (IOException e) {				
			e.printStackTrace();
		} finally {
			try {
				if (csvReader != null) {
					csvReader.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return rawLabelWrap.size() > 0;
	}
	
	private static boolean loadLabelData(String date) {
		return loadLabelData(date, sRawLabelsWrap);
	}
	
	/**
	 * save all the labels to the label file specified by the date
	 * @param date	          yyyy-MM-dd
	 * @param rawLabelWrap    labels to be saved
	 * @return true if succeed, otherwise false
	 */
	public static boolean saveLabelData(String date, RawLabelWrap rawLabelWrap) {						
		String path = TeensGlobals.DIRECTORY_PATH + File.separator + Globals.APP_DATA_DIRECTORY + TeensGlobals.LABELS_FOLDER + date + File.separator;
		
		// build the file path name
		String filePathName = ""; 
		String[] labelFilePaths = FileHelper.getFilePathsDir(path);		
		if (labelFilePaths == null || labelFilePaths.length == 0) {	
			StringBuilder sb = new StringBuilder();
			sb.append(path);
			sb.append(File.separator);
			sb.append("Activities.");
			sb.append(Globals.mHealthTimestampFormat.format(new Date()));
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
		    ArrayList<RawLabel> rawLabels = (ArrayList<RawLabel>) entry.getValue();
		    for (RawLabel rawLabel : rawLabels) {  
		    	sb.append(rawLabel.toString());
		    }
		} 
		String content = sb.toString();

		// write the .csv file
		File labelFile = new File(filePathName);		
		boolean result = FileHelper.saveStringToFile(content, labelFile, false);
				
		return result;
	
	}

	private static int createRawChunkData(int startSecond, int stopSecond, ArrayList<RawChunk> rawChunks) {
		ArrayList<Integer> chunkPos = ChunkingAlgorithm.getInstance().doChunking(
			startSecond, stopSecond, sAccelDataWrap.getDrawableData()
		);				
		
		if (chunkPos == null) {
			return 0;
		}

		// current selected date		
		String date = getCurrentSelectedDate();		
		// create raw chunk data for each chunking position	
		rawChunks.clear();
		for (int i = 0; i < chunkPos.size() - 1; ++i) {						
			rawChunks.add(new RawChunk(date, chunkPos.get(i), chunkPos.get(i + 1)));
		}
						
		return rawChunks.size();
	}
	
	public static void clearRawData() {
		sRawChksWrap.clear();
		sAccelDataWrap.clear();		
	}
	
	public static final String DATASET   = "chunks";
	public static final String ANNOTATOR = "uscteens";
	
	public static boolean areAllChunksLabelled(String date) {
		String path = TeensGlobals.DIRECTORY_PATH + File.separator + Globals.DATA_DIRECTORY + TeensGlobals.SENSOR_FOLDER + date;
		String[] fileNames = new File(path).list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				return filename.startsWith(DATASET) && filename.endsWith(".annotation.xml");
			}
		});
		
		if (fileNames == null || fileNames.length == 0) {			
			return false;
		}
		
		String filePath = path + File.separator + fileNames[0];
		if (!new File(filePath).exists()) {	
			return false;
		}
		
		boolean isAllLabelled = true;
		SAXReader saxReader = new SAXReader();
		try {
			Document document = saxReader.read(new File(filePath));								
			Element root = document.getRootElement();
			for (Iterator i = root.elementIterator(); i.hasNext();) {
				Element annotation = (Element) i.next();
				for (Iterator j = annotation.elementIterator(); j.hasNext();) {
				    Element label = (Element) j.next();
				    for (Iterator k = label.attributeIterator(); k.hasNext();) {
				    	Attribute attribute = (Attribute) k.next();			    	       
				    	if (attribute.getName().equals("GUID")) {
				    		String guid = attribute.getText();
				    		if (guid.equals(TeensGlobals.UNLABELLED_GUID)) {
				    			isAllLabelled = false;
				    		}
				    	}
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
		String date = getCurrentSelectedDate();
		assert(date.compareTo("") != 0);
		
		sRawChksWrap.clear();
		for (int i = 0; i < chunks.size(); ++i) {
			Chunk chunk = chunks.get(i);
			RawChunk rawChunk = chunk.toRawChunk();
			sRawChksWrap.add(rawChunk);
		}
		
		Date selDate;
		try {
			selDate = Globals.mHealthDateDirFormat.parse(date);
		} catch (ParseException e1) {
			Log.d(TAG, "Failed to parse selected date");
			return false;
		}
		
		// Add all the chunks to the csv file
		saveChunkDataToCSV(date);
		
		// Add all the annotations to the xml file
		AnnotationSaver annotationSaver = new AnnotationSaver();
		annotationSaver.initialize(DATASET, ANNOTATOR, "bigbugbb@gmail.com",
				"chunks of activities", "based on convolution & pre-defined thresholds", "");
		annotationSaver.setDate(selDate);
        for (RawChunk rawChunk : sRawChksWrap) {
        	Action action = rawChunk.getAction();
        	try {
				annotationSaver.addAnnotation(
					TeensGlobals.ANNOTATION_GUID, 
					action.getActionID(), 
					action.getActionName(), 
					Globals.mHealthTimestampFormat.parse(rawChunk.mStartDate), 
					Globals.mHealthTimestampFormat.parse(rawChunk.mStopDate), 
					TeensGlobals.ANNOTATION_SET, 
					Globals.mHealthTimestampFormat.parse(rawChunk.mModifyTime), 
					Globals.mHealthTimestampFormat.parse(rawChunk.mCreateTime)
				);
				Log.d(TAG, rawChunk.mStartDate + "\t" + rawChunk.mStopDate);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        
        // Save the changes into the xml file
        boolean result = annotationSaver.commitToFile();

		return result;
	}
	
	private static void saveChunkDataToCSV(String date) {	
		File dir = new File(
			TeensGlobals.DIRECTORY_PATH + File.separator + Globals.DATA_MHEALTH_SENSORS_DIRECTORY + File.separator + date
		);
		dir.mkdirs();

		File chunkFile = new File(dir, DATASET + "." + ANNOTATOR + ".csv");				
		try {
			if (!chunkFile.exists()) {
				chunkFile.createNewFile();
			}
			String[] header = { "START_TIME", "STOP_TIME", "LABEL" };							
			CSVWriter writer = new CSVWriter(new FileWriter(chunkFile, false), ',', CSVWriter.NO_QUOTE_CHARACTER);			
			writer.writeNext(header);
		
			for (RawChunk rawChunk : sRawChksWrap) {
	        	Action action = rawChunk.getAction();
	        	String[] content = { rawChunk.mStartDate, rawChunk.mStopDate, action.getActionName() };
				writer.writeNext(content);				
	        }
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
