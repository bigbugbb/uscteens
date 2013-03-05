package edu.neu.android.mhealth.uscteensver1;

import edu.neu.android.wocketslib.ApplicationManager;
import edu.neu.android.wocketslib.Globals;


public class USCTeensAppManager extends ApplicationManager {

	public static final String TAG = "USCTeens";
	
    @Override 
    public void onCreate() {
    	super.onCreate();
    	LibraryGlobals.initGlobals(getAppContext());
    	USCTeensGlobals.initGlobals(getAppContext());

    	ArbitraterNew arbitrater = new ArbitraterNew(getAppContext()); 
    	Globals.setArbitrater(arbitrater);     	
    }
}
