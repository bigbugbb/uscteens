package edu.neu.android.mhealth.uscteensver1.data;

import java.text.SimpleDateFormat;
import java.util.Date;

/*
 * This class gives you the ability to update the label file directly
 */

public class Labeler {
	private static final String TAG = "Labeler";
	
	private RawLabelWrap mRawLabels = new RawLabelWrap();
	private SimpleDateFormat mDateFormat     = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat mDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
	
	private static final long TWO_MINUTE = 120 * 1000;
	private long   mLastLabelTime = 0;
	private String mLastLabelName = "";
	
	private static Labeler sLabeler;
	
	public static Labeler getInstance() {
		if (sLabeler == null) {
			sLabeler = new Labeler();			
		}
		return sLabeler;
	}
	
	/**
	 * add a new label
	 * @param dateTime  yyyy-MM-dd kk:mm:ss
	 * @param name	    the label name
	 * @return true if the label is added, otherwise false
	 */
	public boolean addLabel(String dateTime, String name) {
		boolean result = false;
										
		if (Math.abs(mLastLabelTime - System.currentTimeMillis()) < TWO_MINUTE && name.equals(mLastLabelName)) {
			return false; // skip this label because it's too frequent
		} else {
			mLastLabelTime = System.currentTimeMillis();
			mLastLabelName = name;
		}		
		
		String date = dateTime.split(" ")[0];
		DataSource.loadLabelData(date, mRawLabels);
		if (mRawLabels.add(dateTime, name)) {
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
	public boolean addLabel(Date aDate, String name) {
		String dateTime = mDateTimeFormat.format(aDate);
		return addLabel(dateTime, name);
	}
	
	/**
	 * remove labels where the time matches. I.e., if there are two labels for "somename"
	 * at two different times, then it should only remove the label at the given time. 
	 * @param dateTime    yyyy-MM-dd kk:mm:ss
	 * @param name		  the label name
	 * @return true if the label is removed, otherwise false
	 */
	public boolean removeLabel(String dateTime, String name) {
		String date = dateTime.split(" ")[0];
		DataSource.loadLabelData(date, mRawLabels);
		boolean result = mRawLabels.remove(dateTime, name);
		
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
	public boolean removeLabel(Date aDate, String name) {
		String dateTime = mDateTimeFormat.format(aDate);
		return removeLabel(dateTime, name);
	}
	
	/**
	 * remove all labels with the given String name for the given Date
	 * @param date    	the date for removing the label, should be the format: yyyy-MM-dd
	 * @param name		the label name to remove
	 * @return
	 */
	public boolean removeAllLabelsNamed(String date, String name) {
		DataSource.loadLabelData(date, mRawLabels);
		boolean result = mRawLabels.removeAll(name);	
		
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
	public boolean removeAllLabelsNamed(Date aDate, String name) {
		String date = mDateFormat.format(aDate);			
		return removeAllLabelsNamed(date, name);
	}
	
	/**
	 * clear all labels of a specified date
	 * @param date 	    the date to clear, should be the format: yyyy-MM-dd
	 * @return true if all labels are cleared, otherwise false
	 */
	public boolean clearAllLabels(String date) {						
		mRawLabels.clear();		
		return commitChanges(date);
	}
	
	/**
	 * clear all labels in the file
	 * @param aDate 	the date to clear
	 * @return true if all labels are cleared, otherwise false
	 */
	public boolean clearAllLabels(Date aDate) {		
		String date = mDateFormat.format(aDate);
		return clearAllLabels(date);
	}
	
	/**
	 * commit the changes to the file
	 * @param date    the date to commit, should be the format: yyyy-MM-dd
	 * @return true if the commit is successful, otherwise false
	 */
	public boolean commitChanges(Date aDate) {
		String date = mDateFormat.format(aDate);
		return commitChanges(date);
	}
	
	/**
	 * commit the changes to the file
	 * @param date    the date to commit, should be the format: yyyy-MM-dd
	 * @return true if the commit is successful, otherwise false
	 */
	public boolean commitChanges(String date) {
		return DataSource.saveLabelData(date, mRawLabels);
	}
}
