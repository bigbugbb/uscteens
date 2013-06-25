package edu.neu.android.mhealth.uscteensver1.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import edu.neu.android.wocketslib.support.DataStorage;

/*
 * This class gives you the ability to update the label file directly
 */

public class Labeler {
	private static final String TAG = "Labeler";
	
	private static RawLabelWrap sRawLabels = new RawLabelWrap();
	private static SimpleDateFormat sDateFormat     = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat sDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
	
	private static Context sContext;
	private static final long TWO_MINUTE = 120 * 1000;
	private static final String KEY_LAST_LABEL_NAME = "_KEY_LAST_LABEL_NAME";
	private static final String KEY_LAST_LABEL_TIME = "_KEY_LAST_LABEL_TIME";
	
	public static void initialize(Context context) {
		sContext = context;
	}
	
	/**
	 * add a new label
	 * @param dateTime  yyyy-MM-dd kk:mm:ss
	 * @param name	    the label name
	 * @return true if the label is added, otherwise false
	 */
	public static boolean addLabel(String dateTime, String name) {
		boolean result = false;
				
		try {
			long currentLabelTime = sDateTimeFormat.parse(dateTime).getTime();
			long lastLabelTime = DataStorage.GetValueLong(sContext, KEY_LAST_LABEL_TIME, 0);
			String lastLabelName = DataStorage.GetValueString(sContext, KEY_LAST_LABEL_NAME, ":-)");
			if (Math.abs(lastLabelTime - currentLabelTime) < TWO_MINUTE && name.equals(lastLabelName)) {
				return false; // skip this label because it's too frequent
			} else {
				DataStorage.SetValue(sContext, KEY_LAST_LABEL_NAME, name);		
				DataStorage.SetValue(sContext, KEY_LAST_LABEL_TIME, currentLabelTime);
			}
		} catch (ParseException e1) {			
			e1.printStackTrace();
		}
		
		String date = dateTime.split(" ")[0];
		DataSource.loadLabelData(date, sRawLabels);
		if (sRawLabels.add(dateTime, name)) {
			result = commitChanges(date);
		}		

		return result;
	}
	
	/**
	 * add a new label
	 * @param aDate     date for the label
	 * @param name	    the label name
	 * @return true if the label is added, otherwise false
	 */
	public static boolean addLabel(Date aDate, String name) {
		String dateTime = sDateTimeFormat.format(aDate);
		return addLabel(dateTime, name);
	}
	
	/**
	 * remove labels where the time matches. I.e., if there are two labels for "somename"
	 * at two different times, then it should only remove the label at the given time. 
	 * @param dateTime    yyyy-MM-dd kk:mm:ss
	 * @param name		  the label name
	 * @return true if the label is removed, otherwise false
	 */
	public static boolean removeLabel(String dateTime, String name) {
		String date = dateTime.split(" ")[0];
		DataSource.loadLabelData(date, sRawLabels);
		boolean result = sRawLabels.remove(dateTime, name);
		
		if (result) {
			result = commitChanges(date);
		}
		
		return result;
	}
	
	/**
	 * remove labels where the time matches. I.e., if there are two labels for "somename"
	 * at two different times, then it should only remove the label at the given time. 
	 * @param aDate       date time for the label to remove
	 * @param name		  the label name to remove
	 * @return true if the label is removed, otherwise false
	 */
	public static boolean removeLabel(Date aDate, String name) {
		String dateTime = sDateTimeFormat.format(aDate);
		return removeLabel(dateTime, name);
	}
	
	/**
	 * remove all labels with the given String name for the given Date
	 * @param date    	the date for removing the label, should be the format: yyyy-MM-dd
	 * @param name		the label name to remove
	 * @return
	 */
	public static boolean removeAllLabelsNamed(String date, String name) {
		DataSource.loadLabelData(date, sRawLabels);
		boolean result = sRawLabels.removeAll(name);	
		
		if (result) {
			result = commitChanges(date);
		}
		
		return result;
	}
	
	/**
	 * remove all labels with the given String name for the given Date
	 * @param aDate     the date for removing the label, should be the format: yyyy-MM-dd
	 * @param name	    the label name to remove 
	 * @return
	 */
	public static boolean removeAllLabelsNamed(Date aDate, String name) {
		String date = sDateFormat.format(aDate);			
		return removeAllLabelsNamed(date, name);
	}
	
	/**
	 * clear all labels of a specified date
	 * @param date 	    the date to clear, should be the format: yyyy-MM-dd
	 * @return true if all labels are cleared, otherwise false
	 */
	public static boolean clearAllLabels(String date) {						
		sRawLabels.clear();		
		return commitChanges(date);
	}
	
	/**
	 * clear all labels in the file
	 * @param aDate 	the date to clear
	 * @return true if all labels are cleared, otherwise false
	 */
	public static boolean clearAllLabels(Date aDate) {		
		String date = sDateFormat.format(aDate);
		return clearAllLabels(date);
	}
	
	/**
	 * commit the changes to the file
	 * @param date    the date to commit, should be the format: yyyy-MM-dd
	 * @return true if the commit is successful, otherwise false
	 */
	public static boolean commitChanges(Date aDate) {
		String date = sDateFormat.format(aDate);
		return commitChanges(date);
	}
	
	/**
	 * commit the changes to the file
	 * @param date    the date to commit, should be the format: yyyy-MM-dd
	 * @return true if the commit is successful, otherwise false
	 */
	public static boolean commitChanges(String date) {
		return DataSource.saveLabelData(date, sRawLabels);
	}
	
	/**
	 * reload the labels of the specified date from the file
	 * @param date    the date to load, should be the format: yyyy-MM-dd
	 */
	public static void peekLabels(String date) {
		DataSource.loadLabelData(date, sRawLabels);
	}
	
	/**
	 * reload the labels of the specified date from the file
	 * @param date    the date to load
	 */
	public static void peekLabels(Date aDate) {
		String date = sDateFormat.format(aDate);
		DataSource.loadLabelData(date, sRawLabels);
	}
}
