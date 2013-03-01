package edu.neu.android.mhealth.uscteensver1.broadcastreceivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import edu.neu.android.mhealth.uscteensver1.services.TeensSensorService;
import edu.neu.android.wocketslib.Globals;
import edu.neu.android.wocketslib.broadcastreceivers.MonitorServiceBroadcastReceiver;
import edu.neu.android.wocketslib.sensormonitor.DataStore;
import edu.neu.android.wocketslib.utils.Log;

public class TeensBroadcastReceiver extends MonitorServiceBroadcastReceiver {

	PendingIntent mAlarmSender = null;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(TYPE_START_SENSOR_MONITOR_SERVICE_NOW)) {
			Log.o(TAG, "PhoneState", "StartSensorMonitorServiceNow");
			addNote(context, "Start sensor monitor now", true);
			if (Globals.IS_DEBUG)
				Log.i(TAG, "Got ACTION START_SENSOR_MONITOR_SERVICE_NOW");

			// Set the alarm for the next minute
			setAlarm(context);
			// And run the service immediately as well
			Intent i = new Intent(context, TeensSensorService.class);
			context.startService(i);
		}
	}

	private void setAlarm(Context aContext) {
		if (Globals.IS_DEBUG)
			Log.i(TAG, "Set alarm");

		if (mAlarmSender == null) {
			mAlarmSender = PendingIntent.getService(aContext, 12345, new Intent(aContext, TeensSensorService.class), 0);
		}
		DataStore.setRunning(true);

		// We want the alarm to go off 60 seconds from now.
		long firstTime = SystemClock.elapsedRealtime();

		// Schedule the alarm
		if (Globals.IS_DEBUG)
			Log.i(TAG, "Alarm created.");
		AlarmManager am = (AlarmManager) aContext.getSystemService(Context.ALARM_SERVICE);
		am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime, Globals.TIMER_PERIOD_MS, mAlarmSender);
	}
}
