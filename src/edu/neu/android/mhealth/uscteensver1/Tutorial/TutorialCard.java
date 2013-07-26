package edu.neu.android.mhealth.uscteensver1.Tutorial;

import java.io.Serializable;
import java.util.ArrayList;

import android.graphics.RectF;
import android.util.Log;

public class TutorialCard implements Serializable {
	
	private static final String TAG = "TutorialCard";
	private static final long serialVersionUID = 1375988883058535043L;
	
	protected String mID;
	protected String mFileName;
	protected ArrayList<TutorialRegion> mRegions;
	protected ArrayList<String> mPrevIDs;
	
	public TutorialCard(String id, String fileName) {
		mID       = id;
		mFileName = fileName;
		mRegions  = null;
		mPrevIDs  = null;
	}
	
	public TutorialCard(String id, String fileName, 
			ArrayList<TutorialRegion> regions, ArrayList<String> prevIDs) {
		mID		  = id;
		mFileName = fileName;
		mRegions  = regions;
		mPrevIDs  = prevIDs;
	}
	
	public void addRegion(TutorialRegion region) {
		if (mRegions == null) {
			mRegions = new ArrayList<TutorialRegion>();
		}
		mRegions.add(region);
	}
	
	public void addRegion(RectF rect, String targetID) {
		TutorialRegion region = new TutorialRegion(rect, targetID);
		addRegion(region);
	}
	
	public void addRegion(float left, float top, float right, float bottom, String targetID) {
		TutorialRegion region = new TutorialRegion(left, top, right, bottom, targetID);
		addRegion(region);
	}
	
	public boolean deleteRegionByTargetID(String targetID) {
		if (mRegions == null) {
			Log.i(TAG, "There is no region to delete");
			return false;
		}
		
		int index = -1;
		for (int i = 0; i < mRegions.size(); ++i) {
			TutorialRegion region = mRegions.get(i);
			if (region.getTargetID().equals(targetID)) {
				index = i;
				break;
			}
		}		
		if (index != -1) {
			mRegions.remove(index);
			return true;
		}
		
		return false;
	}
	
	public boolean deleteRegionByArea(RectF rect) {
		if (mRegions == null) {
			Log.i(TAG, "There is no region to delete");
			return false;
		}
		
		int index = -1;
		for (int i = 0; i < mRegions.size(); ++i) {
			TutorialRegion region = mRegions.get(i);
			if (region.getRegionArea().contains(rect)) {
				index = i;
				break;
			}
		}
		if (index != -1) {
			mRegions.remove(index);
			return true;
		}
		
		return false;
	}
}
