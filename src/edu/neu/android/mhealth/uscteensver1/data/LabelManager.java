package edu.neu.android.mhealth.uscteensver1.data;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import edu.neu.android.mhealth.uscteensver1.USCTeensGlobals;
import edu.neu.android.mhealth.uscteensver1.pages.AppScale;

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
			
	// used for adjusting the label's y coordinate
	protected static ArrayList<Integer> sPrevXs = new ArrayList<Integer>();

	public static void initialize(Context context) {
		sContext   = context;	
		sResources = context.getResources();				
		Labeler.initialize(context);
	}		
	
	public static void start() {
		loadLabels();
	}
	
	public static void stop() {		
		release();
	}
	
	protected static void loadLabels() {
		RawLabelWrap rawLabelWrap = DataSource.getRawLabels();
		
		sPrevXs.clear();
		if (sLabels == null) {
			sLabels = new ArrayList<Label>();
		}
						
		ArrayList<RawLabel> rawLabels = rawLabelWrap.toSortedArray();
		for (RawLabel rawLabel : rawLabels) {
			Label label = insertLabel();			    				
			String name = rawLabel.getName();			
			int x = rawLabel.getTimeInSec() * USCTeensGlobals.PIXEL_PER_DATA;
			int y = (int) AppScale.doScaleH(65);
			// load each label				
			label.load(x, y, name);			
		}
	}	
	
	public static void adjustLabelCoordinate(int[] pos, int textWidth, int textHeight, 
			int imgWidth, int imgHeight) {
		int i = 0;
		int width = (imgWidth << 1) + textWidth;
		
		while (i < sPrevXs.size()) {
			if (sPrevXs.get(i) + width <= pos[0]) {
				sPrevXs.set(i, pos[0]);
				break;
			}
			++i;
		}
		
		if (i >= sPrevXs.size()) {
			sPrevXs.add(pos[0]);
		}
		
		pos[1] += Math.max(textHeight, imgHeight) * i;
	}
	
	
	public static void release() {
		if (sLabels != null) {
			for (Label l : sLabels) {
				l.release();
			}
			sLabels = null;
		}
	}
	
	public static ArrayList<Label> getLabels() {
		return sLabels;
	}
	
	public static Label getLabel(int index) {
		return sLabels.get(index);
	}
	
	public static int getLabelSize() {
		return sLabels.size();
	}
	
	public static Label insertLabel(int index) {		
		Label label = new Label(sResources);			
		sLabels.add(index, label);
		return label;
	}		
	
	public static Label insertLabel() {
		Label label = new Label(sResources);
		sLabels.add(label);
		return label;
	}

	public static void deleteChunk(Label label) {
		sLabels.remove(label);
		label.release();
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
