package edu.neu.android.mhealth.uscteensver1;

import java.util.ArrayList;

import android.app.Activity;
import android.content.pm.PackageManager.NameNotFoundException;
import edu.neu.android.wocketslib.ApplicationManager;
import edu.neu.android.wocketslib.Globals;


public class USCTeensAppManager extends ApplicationManager {

	public static final String TAG = "USCTeens";
	
    @Override 
    public void onCreate() {
    	super.onCreate();
    	
    	USCTeensGlobals.initGlobals(getAppContext());

    	USCTeensArbitrater arbitrater = new USCTeensArbitrater(getAppContext()); 
    	Globals.setArbitrater(arbitrater);   
    	
    	try {
			USCTeensGlobals.VERSION_NAME = "Ver. " + getPackageManager().getPackageInfo(getPackageName(), 0).versionName;					
		} catch (NameNotFoundException e) {		
			e.printStackTrace();
		}
    	
		USCTeensBroadcastReceiverProcessor aBRP = new USCTeensBroadcastReceiverProcessor();
		edu.neu.android.wocketslib.Globals.myBroadcastReceiverProcessor = aBRP;
    }
    
    private ArrayList<Activity> activityList = new ArrayList<Activity>();

	public void addActivity(Activity activity) {		
		activityList.add(activity);
	}

	public void killActivity(Activity activity) {
		activity.finish();
		activityList.remove(activity);
	}

	public void removeActivity(Activity activity) {
		activityList.remove(activity);
	}

	public void killAllActivities() {
		for (int i = 0; i < activityList.size(); i++)
			activityList.get(i).finish();
		activityList.clear();
	}
}
