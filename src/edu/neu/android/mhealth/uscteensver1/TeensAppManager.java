package edu.neu.android.mhealth.uscteensver1;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.telephony.TelephonyManager;
import edu.neu.android.wocketslib.ApplicationManager;
import edu.neu.android.wocketslib.support.AuthorizationChecker;
import edu.neu.android.wocketslib.support.DataStorage;
import edu.neu.android.wocketslib.utils.PhoneNotifier;

/**
 * The entry of the whole application.
 *
 * @author bigbug
 */
public class TeensAppManager extends ApplicationManager {

    public static final String TAG = "TeensAppManager";

    @Override
    public void onCreate() {
        super.onCreate();

        PhoneNotifier.setIcon(R.drawable.ic_notification);
        TeensGlobals.initGlobals(getAppContext());
    }

    public static AssetManager getAppAssets() {
        return getAppContext().getAssets();
    }

    public static Resources getAppResources() {
        return getAppContext().getResources();
    }

    public static String getParticipantId(Context c) {
        String subjectID = DataStorage.GetValueString(c, DataStorage.KEY_SUBJECT_ID,
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
        for (Activity activity : mActivityList) {
            activity.finish();
        }
        mActivityList.clear();
    }
}
