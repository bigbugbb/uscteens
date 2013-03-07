package edu.neu.android.mhealth.uscteensver1;

import java.io.File;

import edu.neu.android.wocketslib.ApplicationManager;
import edu.neu.android.wocketslib.Globals;
import edu.neu.android.wocketslib.utils.FileHelper;


public class USCTeensAppManager extends ApplicationManager {

	public static final String TAG = "USCTeens";
	
    @Override 
    public void onCreate() {
    	super.onCreate();
    	USCTeensGlobals.initGlobals(getAppContext());    	

    	USCTeensArbitrater arbitrater = new USCTeensArbitrater(getAppContext()); 
    	Globals.setArbitrater(arbitrater);     	
    }
}

// boolean isSaved = FileHelper.saveStringToFile(aJSONString, Globals.INTERNAL_DIRECTORY_PATH + File.separator + Globals.UPLOADS_DIRECTORY, fileName);