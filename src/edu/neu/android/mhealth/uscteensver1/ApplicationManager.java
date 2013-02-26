package edu.neu.android.mhealth.uscteensver1;


public class ApplicationManager extends edu.neu.android.wocketslib.ApplicationManager {

public static final String TAG = "USCAsthmaApplicationManager";
	
    @Override 
    public void onCreate() {
    	super.onCreate();
    	LibraryGlobals.initGlobals(getApplicationContext());

    	ArbitraterNew anArbitrater = new ArbitraterNew(getAppContext()); 
    	edu.neu.android.wocketslib.Globals.myArbitrater = anArbitrater; 
    	
    }
}
