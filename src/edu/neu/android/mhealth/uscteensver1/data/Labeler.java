package edu.neu.android.mhealth.uscteensver1.data;

/*
 * This class gives you the ability to update the label file directly
 */

public class Labeler {
	
	private static RawLabelWrap sRawLabels = new RawLabelWrap();
	
	/**
	 * add a new label
	 * @param dateTime yyyy-MM-dd hh:mm:ss
	 * @param text	the label text
	 * @param commit true if the change should be committed to the file immediately
	 * @return true if the label is added, otherwise false
	 */
	public static boolean addLabel(String dateTime, String text, boolean commit) {
		DataSource.loadLabelData(dateTime.split(" ")[0], sRawLabels, false);
		boolean result = sRawLabels.add(dateTime, text);
		
		if (commit) {
			result = commitChanges();
		}
		
		return result;
	}
	
	/**
	 * delete a new label 
	 * @param dateTime  yyyy-MM-dd hh:mm:ss
	 * @param text	the label text
	 * @param commit true if the change should be committed to the file immediately
	 * @return true if the label is removed, otherwise false
	 */
	public static boolean removeLabel(String dateTime, String text, boolean commit) {
		DataSource.loadLabelData(dateTime.split(" ")[0], sRawLabels, false);
		boolean result = sRawLabels.remove(dateTime, text);
		
		if (commit) {
			result = commitChanges();
		}
		
		return result;
	}
	
	/**
	 * clear all labels in the file
	 * @param date 	the date to be clear
	 * @param commit true if the change should be committed to the file immediately
	 * @return true if all labels are cleared, otherwise false
	 */
	public static boolean clearAllLabels(String date, boolean commit) {		
		sRawLabels.clear();
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
