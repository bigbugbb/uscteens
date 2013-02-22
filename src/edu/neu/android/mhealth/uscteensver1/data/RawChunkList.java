package edu.neu.android.mhealth.uscteensver1.data;

import java.util.ArrayList;

public class RawChunkList extends ArrayList<RawChunk> {
	
	public boolean areAllChunksLabelled() {
		for (RawChunk rawChunk : this) {
			if (rawChunk.mActionID == -1) {
				return false;
			}
		}
		
		return true;
	}
	
}
