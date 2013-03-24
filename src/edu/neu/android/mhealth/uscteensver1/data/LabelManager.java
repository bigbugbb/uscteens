package edu.neu.android.mhealth.uscteensver1.data;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import edu.neu.android.mhealth.uscteensver1.USCTeensGlobals;

public class LabelManager {
	protected static Context   sContext   = null;
	protected static Resources sResources = null;			
	protected static ArrayList<Label> sLabels = null;
	
	protected static float sDispOffsetX  = 0;
	protected static float sDispOffsetY  = 0;
	protected static float sViewWidth    = 0; // the area to draw the activity data
	protected static float sViewHeight   = 0;
	protected static float sCanvasWidth  = 0;
	protected static float sCanvasHeight = 0;
			

	public static void initialize(Context context) {
		sContext   = context;	
		sResources = context.getResources();		
	}		
	
	public static void start() {
		loadLabels();
	}
	
	public static void stop() {		
		release();
	}
	
	protected static void loadLabels() {
		RawLabelWrap rawLabels = DataSource.getRawLabels();
		
		if (sLabels == null) {
			sLabels = new ArrayList<Label>();
		}
				
		for (int i = 0; i < rawLabels.size(); ++i) {
//			Chunk chunk = insertChunk(i);	
//			RawChunk rawChunk = rawChunks.get(i);
			
			// load each chunk
//			int start = (rawChunk.getStartTime() - timeOffset) * USCTeensGlobals.PIXEL_PER_DATA;
//			int stop  = (rawChunk.getStopTime()  - timeOffset) * USCTeensGlobals.PIXEL_PER_DATA;
//			int activityID = rawChunk.getActivityID();
//			String createTime = rawChunk.getCreateTime();
//			String modifyTime = rawChunk.getModifyTime();
//			chunk.load(start, stop, timeOffset, activityID, createTime, modifyTime);						
		}
	}	
	
	public static void release() {
		if (sLabels != null) {
			for (Label l : sLabels) {
				l.release();
			}
			sLabels = null;
		}
	}
	
	public static void setViewSize(float width, float height) {
		sViewWidth  = width;
		sViewHeight = height;
	}
	
	public static float getViewWidth() {
		return sViewWidth;
	}
	
	public static float getViewHeight() {
		return sViewHeight;
	}
	
	public static void setCanvasSize(float width, float height) {
		sCanvasWidth  = width;
		sCanvasHeight = height;
	}
	
	public static float getCanvasWidth() {
		return sCanvasWidth;
	}
	
	public static float getCanvasHeight() {
		return sCanvasHeight;
	}
	
	public static void setDisplayOffset(float offsetX, float offsetY) {
		sDispOffsetX = offsetX;
		sDispOffsetY = offsetY;
		
		for (int i = 0; i < sLabels.size(); ++i) {
			Label l = sLabels.get(i);
			l.setDisplayOffset(offsetX, offsetY);
		}
	}
}
