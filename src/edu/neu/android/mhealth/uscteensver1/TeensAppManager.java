package edu.neu.android.mhealth.uscteensver1;

import java.util.ArrayList;

import android.app.Activity;
import android.content.pm.PackageManager.NameNotFoundException;
import edu.neu.android.wocketslib.ApplicationManager;
import edu.neu.android.wocketslib.Globals;


public class TeensAppManager extends ApplicationManager {

	public static final String TAG = "USCTeens";
	
    @Override 
    public void onCreate() {
    	super.onCreate();
    	
    	TeensGlobals.initGlobals(getAppContext());

    	TeensArbitrater arbitrater = new TeensArbitrater(getAppContext()); 
    	Globals.setArbitrater(arbitrater);   
    	
    	try {
    		String packageName = getPackageName();
			TeensGlobals.VERSION_NAME = "Ver. " + getPackageManager().getPackageInfo(packageName, 0).versionName;					
		} catch (NameNotFoundException e) {		
			e.printStackTrace();
		}
    	
		TeensBroadcastReceiverProcessor aBRP = new TeensBroadcastReceiverProcessor();
		edu.neu.android.wocketslib.Globals.myBroadcastReceiverProcessor = aBRP;
    }
    
    private ArrayList<Activity> mActivityList = new ArrayList<Activity>();

	public void addActivity(Activity activity) {		
		mActivityList.add(activity);
	}

	public void killActivity(Activity activity) {
		activity.finish();
		mActivityList.remove(activity);
	}

	public void removeActivity(Activity activity) {
		mActivityList.remove(activity);
	}

	public void killAllActivities() {
		for (int i = 0; i < mActivityList.size(); i++)
			mActivityList.get(i).finish();
		mActivityList.clear();
	}
}
