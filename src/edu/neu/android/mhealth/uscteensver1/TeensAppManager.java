package edu.neu.android.mhealth.uscteensver1;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.telephony.TelephonyManager;
import edu.neu.android.wocketslib.ApplicationManager;
import edu.neu.android.wocketslib.support.AuthorizationChecker;
import edu.neu.android.wocketslib.support.DataStorage;

/**
 * The entry of the whole application.
 * 
 * @author bigbug
 *
 */
public class TeensAppManager extends ApplicationManager {

	public static final String TAG = "TeensAppManager";
	
    @Override 
    public void onCreate() {
    	super.onCreate();
    	
    	TeensGlobals.initGlobals(getAppContext());   
    	
    	try {
    		String packageName = getPackageName();
			TeensGlobals.VERSION_NAME = "Ver. " + getPackageManager().getPackageInfo(packageName, 0).versionName;					
		} catch (NameNotFoundException e) {		
			e.printStackTrace();
		}
    	
		TeensBroadcastReceiverProcessor aBRP = new TeensBroadcastReceiverProcessor();
		edu.neu.android.wocketslib.Globals.myBroadcastReceiverProcessor = aBRP;
    }
    
    public static String getParticipantId(Context c) {
		String subjectID = DataStorage.GetValueString(c,
				edu.neu.android.wocketslib.support.DataStorage.KEY_SUBJECT_ID,
				AuthorizationChecker.SUBJECT_ID_UNDEFINED);
		if (!subjectID.equals(AuthorizationChecker.SUBJECT_ID_UNDEFINED)) {
			return subjectID;
		}
		TelephonyManager tm = (TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getDeviceId();
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
