package edu.neu.android.mhealth.uscteensver1.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

class RawLabelWrap extends HashMap<String, RawLabel> {
	private static final long serialVersionUID = 6951199087406965582L;	
	
	public RawLabelWrap() {}
	
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
}
