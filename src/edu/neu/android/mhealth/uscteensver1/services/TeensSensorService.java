package edu.neu.android.mhealth.uscteensver1.services;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.PowerManager;
import edu.neu.android.wocketslib.Globals;
import edu.neu.android.wocketslib.R;
import edu.neu.android.wocketslib.activities.datasummaryviewer.GetDataSummaryActivity;
import edu.neu.android.wocketslib.activities.datasummaryviewer.WocketsDataSaver;
import edu.neu.android.wocketslib.broadcastreceivers.MonitorServiceBroadcastReceiver;
import edu.neu.android.wocketslib.dataupload.DataUploaderService;
import edu.neu.android.wocketslib.mhealth.sensordata.DataSaver;
import edu.neu.android.wocketslib.sensormonitor.Arbitrater;
import edu.neu.android.wocketslib.sensormonitor.BluetoothSensorService;
import edu.neu.android.wocketslib.sensormonitor.DataStore;
import edu.neu.android.wocketslib.sensormonitor.Defines;
import edu.neu.android.wocketslib.sensormonitor.MyLocation;
import edu.neu.android.wocketslib.sensormonitor.Sensor;
import edu.neu.android.wocketslib.support.DataStorage;
import edu.neu.android.wocketslib.support.ServerLogger;
import edu.neu.android.wocketslib.utils.Log;
import edu.neu.android.wocketslib.utils.PhoneNotifier;
import edu.neu.android.wocketslib.utils.PhonePowerChecker;
import edu.neu.android.wocketslib.utils.PhonePrompter;
import edu.neu.android.wocketslib.utils.PhoneVibrator;
import edu.neu.android.wocketslib.wakefulintent.WakefulIntentService;

public class TeensSensorService extends BluetoothSensorService implements SensorEventListener {

	public static final String TAG = "TeensSensorService";	

	private static boolean isNewSoftwareVersion = false;
	protected PowerManager.WakeLock mWakeLock = null;
	protected BluetoothAdapter 	    mBluetoothAdapter = null;

	// data saver
	protected DataSaver mWocketDataSaver = null;

	private static String phoneID = null;

	// WOCKET INFO FILLERS

	private int checkBattery(Context aContext) {
		int level = PhonePowerChecker.getBatteryRemaining(aContext);
		Log.o(TAG, "PhoneState", "BatteryRemaining: " + level + "%");
		ServerLogger.addPhoneBatteryReading(TAG, aContext, level);
		ServerLogger.addNote(aContext, "Battery reading: " + level, Globals.NO_PLOT); // TODO

		if (Globals.IS_DEBUG)
			Log.i(TAG, "Got ACTION_BATTERY_CHANGED. Level: " + level + "%");

		PhonePowerChecker.isCharging(aContext);

		return level;
	}

	private long serviceStartTime = 0;
	private Date serviceStartDatePlus1Min = null;

	SensorManager aSensorManager = null;

	private List<android.hardware.Sensor> someSensors = null;
	private android.hardware.Sensor intAccSensor = null;
	
	@Override
	// Called when the service is created and needs to be run
	public void onCreate() {
		// Acquire a wake lock so that the CPU will stay awake while we are
		// reading from the sensors
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getString(R.string.activity_monitor_app_name));
		mWakeLock.acquire();
		if (!mWakeLock.isHeld()) {
			Log.e(TAG, "WakeLock not held when should be");
		} else {
			if (Globals.IS_DEBUG)
				Log.i(TAG, "Got wakelock");
		}

		serviceStartTime = System.currentTimeMillis();

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, 1);
		serviceStartDatePlus1Min = calendar.getTime();

		// Set Sensor + Manager
		aSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		someSensors = aSensorManager.getSensorList(android.hardware.Sensor.TYPE_ACCELEROMETER);
		if (someSensors.size() > 0) {
			intAccSensor = someSensors.get(0);
		}

		// Setup the listener (unregister if needed)
		aSensorManager.registerListener(this, intAccSensor, SensorManager.SENSOR_DELAY_GAME);
		ServerLogger.reset(getApplicationContext());

		// The data store might not be initialized if the app was closed
		// due to a low memory condition, or some other reason beyond our
		// control.
		// If this happens, we just want to load all our data back from NV and
		// let the
		// service keep running.
		if (!DataStore.getInitialized()) { // TODO optimize this		
			DataStore.init(getApplicationContext());
		}

		mBluetoothAdapter = null;
		if ((Globals.IS_WOCKETS_ENABLED) || (Globals.IS_BLUETOOTH_ENABLED))
			mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		// Start up the thread running the service. Note that we create a
		// separate thread because the service normally runs in the process's
		// main thread, which we don't want to block.
		if (Globals.IS_READING_SENSOR_ENABLED) {
			// Show the icon in the status bar
			PhoneNotifier.showReadingNotification(TAG, getApplicationContext());
		}
		Thread thr = new Thread(null, mTask, "BluetoothServiceThread");
		thr.start();
	}

	@Override
	// Called when the service has ended
	public void onDestroy() {
		// Shutdown internal accelerometer
		aSensorManager.unregisterListener(this, intAccSensor);

		// Cancel the notification -- we use the same ID that we had used to
		// start it
		if (Globals.IS_READING_SENSOR_ENABLED)
			PhoneNotifier.cancel(PhoneNotifier.READING_NOTIFICATION);

		while (PhoneVibrator.isVibrating()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}        
		PhoneVibrator.vibratePhoneStop();

		while (PhonePrompter.isPrompting()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		PhonePrompter.cancel();

		Log.i(TAG, "End service after " + String.format("%.1f", (System.currentTimeMillis() - serviceStartTime) / 1000.0) + " sec");
		double runTime = (System.currentTimeMillis() - serviceStartTime) / 1000.0;
		if (runTime > 60)
			Log.e(TAG, "Service run over 1 min");
		if (!mWakeLock.isHeld()) {
			Log.e(TAG, "WakeLock will be released but not held when should be.");
		} else {
			if (Globals.IS_DEBUG)
				Log.i(TAG, "WakeLock is Held and will be released.");
		}

		// Release the wake lock so that the CPU can go back into low power mode
		mWakeLock.release();

		if (mWakeLock.isHeld()) {
			Log.e(TAG, "WakeLock was not released when it should have been.");
		}
	}
	
	@Override
	public void onAccuracyChanged(android.hardware.Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}
//
//	private double x;
//	private double y;
//	private double z;
//	private boolean isFirstSample = true;
//	double lastx, lasty, lastz;
//	double dx, dy, dz;
//	double sumInternal = 0;
//	private int numSamplesInternal = 0;
//	private long lastInternalSampleTime = 0;
//	private static long MS_EACH_INTERNAL_SAMPLE = 1000 / 10; // 10 samples per
//																// sec
//	int skippedSamples = 0;
//	long tdiff = 0;
//	long startIntCollectionTime = 0;
//
//	private void addIntPoint(double x, double y, double z, long timeStamp) {
//		// Log.d(TAG, "Internal x: " + x + " y: " + y + " z: " + z + " ts: " +
//		// timeStamp);
//		// Log.o(TAG, "" + x, "" + y, "" + z);
//
//		dx = Math.abs(lastx - x);
//		dy = Math.abs(lasty - y);
//		dz = Math.abs(lastz - z);
//		lastInternalSampleTime = timeStamp;
//		numSamplesInternal++;
//		sumInternal += dx + dy + dz;
//		lastx = x;
//		lasty = y;
//		lastz = z;
//	}
//
//	long ts = 0;

	@Override
	public void onSensorChanged(SensorEvent event) {

//		if (event.sensor.getType() == android.hardware.Sensor.TYPE_ACCELEROMETER) {
//
//			ts = (new Date()).getTime() + (event.timestamp - System.nanoTime()) / 1000000L; // TODO
//																							// make
//																							// efficient
//
//			if (isFirstSample) {
//				x = event.values[SensorManager.DATA_X] / SensorManager.GRAVITY_EARTH;
//				y = event.values[SensorManager.DATA_Y] / SensorManager.GRAVITY_EARTH;
//				z = event.values[SensorManager.DATA_Z] / SensorManager.GRAVITY_EARTH;
//				numSamplesInternal = 0;
//				sumInternal = 0;
//				lastx = x;
//				lasty = y;
//				lastz = z;
//				isFirstSample = false;
//				startIntCollectionTime = ts;
//				lastInternalSampleTime = ts;
//			}
//
//			tdiff = (ts - lastInternalSampleTime);
//
//			// Log.d(TAG, "TDIFF: " + tdiff);
//
//			if (tdiff > MS_EACH_INTERNAL_SAMPLE) {
//				skippedSamples = (int) (tdiff / MS_EACH_INTERNAL_SAMPLE);
//
//				if (skippedSamples > 0)
//					skippedSamples--;
//
//				// Log.d(TAG, "SKIP: " + skippedSamples);
//				for (int i = 0; i < skippedSamples; i++) {
//					// Log.d(TAG,"Insert");
//					addIntPoint(lastx, lasty, lastz, ts + MS_EACH_INTERNAL_SAMPLE * i);
//				}
//
//				x = event.values[SensorManager.DATA_X] / SensorManager.GRAVITY_EARTH;
//				y = event.values[SensorManager.DATA_Y] / SensorManager.GRAVITY_EARTH;
//				z = event.values[SensorManager.DATA_Z] / SensorManager.GRAVITY_EARTH;
//
//				addIntPoint(x, y, z, ts + MS_EACH_INTERNAL_SAMPLE * skippedSamples);
//			} else {
//				// Not enough time has elapsed. Do not count the sample
//				// Log.d(TAG,"Skip");
//			}
//		}
	}
	
	Runnable mTask = new Runnable() {
		public void run() {
			Log.e(TAG, "NEWS THREAD ------------------------------------------------------------------------------------------------------------------------- "
					+ Globals.NEWS_URL);

			// reset configuration info before processing Wocket data
			compressANDTransferZipFiles(new Date(), getApplicationContext());

			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (Globals.IS_DEBUG)
				Log.i(TAG, "EXIT THREAD");
			// Done with our work...stop the service!
			stopSelf();
		}
	};

}
