package edu.neu.android.mhealth.uscteensver1.data;

import java.util.ArrayList;

class RawChunksWrap extends ArrayList<RawChunk> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1412862931407805188L;

	public boolean areAllChunksLabelled() {
		for (RawChunk rawChunk : this) {
			if (rawChunk.getActivityID() == -1) {
				return false;
			}
		}	
		return true;
	}

}
