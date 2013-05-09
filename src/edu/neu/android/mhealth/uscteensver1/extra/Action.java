package edu.neu.android.mhealth.uscteensver1.extra;

import java.io.Serializable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;

import com.google.gson.Gson;

import edu.neu.android.mhealth.uscteensver1.R;
import edu.neu.android.mhealth.uscteensver1.USCTeensGlobals;
import edu.neu.android.mhealth.uscteensver1.pages.AppScale;

public class Action implements Serializable {	
	private static final long serialVersionUID = -6124174446308636095L;
	protected static Context sContext;
	
	protected String  mActID;
	protected String  mActName;
	protected String  mIcoName;
	protected String  mIcoPath;
	protected Bitmap  mActImage;	
	protected boolean mImageLoaded = false;
	
	public static void initialize(Context context) {
		sContext = context;
	}
	
	public Action(String actID, String actName, String icoName, String icoPath) {
		mActID    = actID;
		mActName  = actName;
		mIcoName  = icoName;
		mIcoPath  = icoPath;
		mActImage = null;
	}
	
	public Action(String actID, String actName, String icoName, Bitmap actImage) {
		mActID    = actID;
		mActName  = actName;
		mIcoName  = icoName;
		mActImage = actImage;
	}
	
	public void loadIcon() {
		if (mImageLoaded) {
			return;
		}
		mImageLoaded = true;
		
		BitmapFactory.Options options = new BitmapFactory.Options(); 
        options.inPurgeable = true;
        options.inPreferredConfig = Config.RGB_565;     
                
    	Bitmap origin = BitmapFactory.decodeFile(mIcoPath, options);
    	Bitmap scaled = null;
    	// scale the image according to the current screen resolution
    	float dstWidth  = origin.getWidth(),
    	      dstHeight = origin.getHeight();        	
		dstWidth  = AppScale.doScaleW(dstWidth);
		dstHeight = AppScale.doScaleH(dstHeight);
		if (dstWidth != origin.getWidth() || dstHeight != origin.getHeight()) {
			scaled = Bitmap.createScaledBitmap(origin, (int) dstWidth, (int) dstHeight, true);
		}                
		// add to the image list
    	if (scaled != null) {
    		origin.recycle(); // explicit call to avoid out of memory    		    	
    		mActImage = scaled;
        } else {
        	mActImage = origin;
        }    	
	}
	
	public void setActionID(String actID) {
		mActID = actID;
	}
	
	public String getActionID() {
		return mActID;
	}
	
	public void setActionName(String actName) {
		mActName = actName;
	}
	
	public String getActionName() {
		return mActName;
	}	
	
	public void setIconName(String icoName) {
		mIcoName = icoName;
	}
	
	public String getIconName() {
		return mIcoName;
	}
	
	public void setActionImage(Bitmap actImage) {
		mActImage = actImage;
	}
	
	public Bitmap getActionImage() {
		return mActImage;
	}
	
	public void clear() {		
		if (mActImage != null) {			
			mActImage.recycle();		
			mActImage = null;
			mImageLoaded = false;
		}				
	}
	
	public static String toJSON(Action action) {
		Gson gson = new Gson(); 
		return gson.toJson(action);
	}
	
	public static Action fromJSON(String aJSONString) {
		Gson gson = new Gson();
		return gson.fromJson(aJSONString, Action.class);
	}
	
	public static Action createUnlabelledAction() {				
		BitmapFactory.Options options = new BitmapFactory.Options(); 
        options.inPurgeable = true;
        options.inPreferredConfig = Config.RGB_565;     
        
        Bitmap image  = null;
    	Bitmap origin = BitmapFactory.decodeResource(sContext.getResources(), R.drawable.question_btn, options);
    	Bitmap scaled = null;
    	// scale the image according to the current screen resolution
    	float dstWidth  = origin.getWidth(),
    	      dstHeight = origin.getHeight();        	
		dstWidth  = AppScale.doScaleW(dstWidth);
		dstHeight = AppScale.doScaleH(dstHeight);
		if (dstWidth != origin.getWidth() || dstHeight != origin.getHeight()) {
			scaled = Bitmap.createScaledBitmap(origin, (int) dstWidth, (int) dstHeight, true);
		}                
		// add to the image list
    	if (scaled != null) {
    		origin.recycle(); // explicit call to avoid out of memory
    		image = scaled;
        } else {
        	image = origin;
        } 	   		
    	
    	return new Action(USCTeensGlobals.UNLABELLED_GUID, "Unlabelled", "question_btn.png", image);
	}
}
