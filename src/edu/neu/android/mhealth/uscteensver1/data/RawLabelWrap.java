package edu.neu.android.mhealth.uscteensver1.data;

import java.util.ArrayList;
import java.util.Collections;

class RawLabelWrap extends ArrayList<RawLabel> {
	private static final long serialVersionUID = 6951199087406965582L;
	
	public RawLabelWrap() {
		clear();
	}
	
	public void sort() {
		Collections.sort(this, Collections.reverseOrder());
	}
}
