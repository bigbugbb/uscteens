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
	 * @param commit    true if the change should be committed to the file immediately,
	 * 				    if the file does not exist, a new one will be created.
	 * @return true if the label is added, otherwise false
	 */
	public static boolean addLabel(String dateTime, String name, boolean commit) {
		boolean result = true;
				
		try {
			long currentLabelTime = sDateTimeFormat.parse(dateTime).getTime();
			long lastLabelTime = DataStorage.GetValueLong(sContext, KEY_LAST_LABEL_TIME, 0);
			String lastLabelName = DataStorage.GetValueString(sContext, KEY_LAST_LABEL_TIME, ":-)");
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
		if (date.compareTo(sRawLabels.getDate()) != 0) {
			sRawLabels.clear();
			//sRawLabels.setDate(date);
		}
		
		DataSource.loadLabelData(date, sRawLabels, false);
		result = sRawLabels.add(dateTime, name);					
		
		if (commit && result) {
			result = commitChanges(date);
		}		

		return result;
	}
	
	/**
	 * add a new label
	 * @param aDate     date for the label
	 * @param name	    the label name
	 * @param commit    true if the change should be committed to the file immediately,
	 * 				    if the file does not exist, a new one will be created.
	 * @return true if the label is added, otherwise false
	 */
	public static boolean addLabel(Date aDate, String name, boolean commit) {
		String dateTime = sDateTimeFormat.format(aDate);
		boolean result = addLabel(dateTime, name, commit);
		return result;
	}
	
	/**
	 * remove labels where the time matches. I.e., if there are two labels for "somename"
	 * at two different times, then it should only remove the label at the given time. 
	 * @param dateTime    yyyy-MM-dd kk:mm:ss
	 * @param name		  the label name
	 * @param commit 	  true if the change should be committed to the file immediately
	 * @return true if the label is removed, otherwise false
	 */
	public static boolean removeLabel(String dateTime, String name, boolean commit) {
		String date = dateTime.split(" ")[0];
		DataSource.loadLabelData(date, sRawLabels, false);
		boolean result = sRawLabels.remove(dateTime, name);
		
		if (commit && result) {
			result = commitChanges(date);
		}
		
		return result;
	}
	
	/**
	 * remove labels where the time matches. I.e., if there are two labels for "somename"
	 * at two different times, then it should only remove the label at the given time. 
	 * @param aDate       date time for the label to remove
	 * @param name		  the label name to remove
	 * @param commit 	  true if the change should be committed to the file immediately
	 * @return true if the label is removed, otherwise false
	 */
	public static boolean removeLabel(Date aDate, String name, boolean commit) {
		String dateTime = sDateTimeFormat.format(aDate);
		String date = dateTime.split(" ")[0];
		boolean result = removeLabel(dateTime, name, commit);
		return result;
	}
	
	/**
	 * remove all labels with the given String name for the given Date
	 * @param date    	the date for removing the label, should be the format: yyyy-MM-dd
	 * @param name		the label name to remove
	 * @param commit	true if the change should be committed to the file immediately
	 * @return
	 */
	public static boolean removeAllLabelsNamed(String date, String name, boolean commit) {
		DataSource.loadLabelData(date, sRawLabels, false);
		boolean result = sRawLabels.removeAll(name);	
		
		if (commit && result) {
			result = commitChanges(date);
		}
		
		return result;
	}
	
	/**
	 * remove all labels with the given String name for the given Date
	 * @param aDate     the date for removing the label, should be the format: yyyy-MM-dd
	 * @param name	    the label name to remove 
	 * @param commit	true if the change should be committed to the file immediately
	 * @return
	 */
	public static boolean removeAllLabelsNamed(Date aDate, String name, boolean commit) {
		String date = sDateFormat.format(aDate);	
		DataSource.loadLabelData(date, sRawLabels, false);
		boolean result = sRawLabels.removeAll(name);
		return result;
	}
	
	/**
	 * clear all labels of a specified date
	 * @param date 	    the date to clear, should be the format: yyyy-MM-dd
	 * @param commit    true if the change should be committed to the file immediately
	 * @return true if all labels are cleared, otherwise false
	 */
	public static boolean clearAllLabels(String date, boolean commit) {				
		boolean result = true;
		DataSource.loadLabelData(date, sRawLabels, false);
		sRawLabels.clear();
		
		if (commit) {
			result = commitChanges(date);
		}
		
		return result;
	}
	
	/**
	 * clear all labels in the file
	 * @param aDate 	the date to clear
	 * @param commit    true if the change should be committed to the file immediately
	 * @return true if all labels are cleared, otherwise false
	 */
	public static boolean clearAllLabels(Date aDate, boolean commit) {		
		String date = sDateFormat.format(aDate);
		boolean result = clearAllLabels(date, commit);		
		return result;
	}
	
	/**
	 * commit the changes to the file
	 * @param date    the date to commit, should be the format: yyyy-MM-dd
	 * @return true if the commit is successful, otherwise false
	 */
	public static boolean commitChanges(Date aDate) {
		String date = sDateFormat.format(aDate);
		boolean result = DataSource.saveLabelData(date, sRawLabels);
		return result;
	}
	
	/**
	 * commit the changes to the file
	 * @param date    the date to commit, should be the format: yyyy-MM-dd
	 * @return true if the commit is successful, otherwise false
	 */
	public static boolean commitChanges(String date) {
		boolean result = DataSource.saveLabelData(date, sRawLabels);
		return result;
	}
	
	/**
	 * reload the labels of the specified date from the file
	 * @param date    the date to load, should be the format: yyyy-MM-dd
	 */
	public static void peekLabels(String date) {
		DataSource.loadLabelData(date, sRawLabels, true);
	}
	
	/**
	 * reload the labels of the specified date from the file
	 * @param date    the date to load
	 */
	public static void peekLabels(Date aDate) {
		String date = sDateFormat.format(aDate);
		DataSource.loadLabelData(date, sRawLabels, true);
	}
}
