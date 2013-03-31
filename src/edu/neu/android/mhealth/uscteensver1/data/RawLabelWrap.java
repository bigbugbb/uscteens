package edu.neu.android.mhealth.uscteensver1.data;

import java.util.HashMap;

class RawLabelWrap extends HashMap<String, RawLabel> {
	private static final long serialVersionUID = 6951199087406965582L;	
	private String mDate = "";
	
	public RawLabelWrap() {}
	
	public void setDate(String date) {
		mDate = date;
	}
	
	public boolean isDateLoaded(String date) {
		return mDate.equals(date);
	}
	
	public boolean add(String dateTime, String text) {
		boolean result = true;
		RawLabel rawLabel = this.get(dateTime);
		
		if (rawLabel == null) {
			rawLabel = new RawLabel(dateTime, text);
			put(dateTime, rawLabel);		
		}
		
		return result;
	}
	
	public boolean remove(String dateTime, String text) {
		boolean result = true;
		put(dateTime, null);
		
		return result;
	}

	@Override
	public void clear() {
		mDate = "";
		super.clear();
	}
}
