package edu.neu.android.mhealth.uscteensver1.data;

/*
 * This class gives you the ability to update the label file directly
 */

public class Labeler {
	
	/**
	 * add a new label
	 * @param dateTime yyyy-MM-dd hh:mm:ss
	 * @param text	the label text
	 * @return true if the label is added, otherwise false
	 */
	public static boolean addLabel(String dateTime, String text, boolean commit) {
		RawLabelWrap rawLabels = DataSource.getRawLabels();		
		boolean result = rawLabels.add(dateTime, text);
		
		if (commit) {
			result = commitChanges();
		}
		
		return result;
	}
	
	/**
	 * delete a new label 
	 * @param dateTime  yyyy-MM-dd hh:mm:ss
	 * @param text	the label text
	 * @return true if the label is removed, otherwise false
	 */
	public static boolean removeLabel(String dateTime, String text, boolean commit) {
		RawLabelWrap rawLabels = DataSource.getRawLabels();
		boolean result = rawLabels.remove(dateTime, text);
		
		if (commit) {
			result = commitChanges();
		}
		
		return result;
	}
	
	/**
	 * clear all labels in the file
	 * @param date 	the date to be clear
	 * @return true if all labels are cleared, otherwise false
	 */
	public static boolean clearAllLabels(String date, boolean commit) {
		RawLabelWrap rawLabels = DataSource.getRawLabels();
		rawLabels.clear();
		boolean result = true;
		
		if (commit) {
			result = commitChanges();
		}
		
		return result;
	}
	
	/**
	 * commit the changes to the file
	 * @return true if the commit is successful, otherwise false
	 */
	public static boolean commitChanges() {
		return DataSource.saveLabelData();
	}
}
