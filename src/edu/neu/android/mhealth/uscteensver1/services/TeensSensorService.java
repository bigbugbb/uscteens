package edu.neu.android.mhealth.uscteensver1.services;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.PowerManager;
import edu.neu.android.wocketslib.Globals;
import edu.neu.android.wocketslib.R;
import edu.neu.android.wocketslib.activities.datasummaryviewer.GetDataSummaryActivity;
import edu.neu.android.wocketslib.activities.datasummaryviewer.WocketsDataSaver;
import edu.neu.android.wocketslib.dataupload.DataUploaderService;
import edu.neu.android.wocketslib.json.model.ActivityCountData;
import edu.neu.android.wocketslib.json.model.WocketStatsData;
import edu.neu.android.wocketslib.mhealth.sensordata.DataSaver;
import edu.neu.android.wocketslib.sensormonitor.BluetoothSensorService;
import edu.neu.android.wocketslib.sensormonitor.DataStore;
import edu.neu.android.wocketslib.sensormonitor.WocketSensor;
import edu.neu.android.wocketslib.support.DataStorage;
import edu.neu.android.wocketslib.support.ServerLogger;
import edu.neu.android.wocketslib.utils.AppUsageLogger;
import edu.neu.android.wocketslib.utils.Log;
import edu.neu.android.wocketslib.utils.PhoneInfo;
import edu.neu.android.wocketslib.utils.PhoneNotifier;
import edu.neu.android.wocketslib.utils.PhonePrompter;
import edu.neu.android.wocketslib.wakefulintent.WakefulIntentService;

public class TeensSensorService extends BluetoothSensorService implements SensorEventListener {

	public static final String TAG = "TeensSensorService";	

	protected PowerManager.WakeLock mWakeLock = null;
	protected BluetoothAdapter 	    mBluetoothAdapter = null;

	// data saver
	protected DataSaver mWocketDataSaver = null;

	private static String phoneID = null;

	private long serviceStartTime = 0;
	SensorManager aSensorManager = null;

	private List<android.hardware.Sensor> someSensors = null;
	private android.hardware.Sensor intAccSensor = null;

	private Date serviceStartDatePlus1Min;
	
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

		// get the service start time
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

	private double x;
	private double y;
	private double z;
	private boolean isFirstSample = true;
	double lastx, lasty, lastz;
	double dx, dy, dz;
	double sumInternal = 0;
	private int numSamplesInternal = 0;
	private long lastInternalSampleTime = 0;
	private static long MS_EACH_INTERNAL_SAMPLE = 1000 / 10; // 10 samples per
																// sec
	int skippedSamples = 0;
	long tdiff = 0;
	long startIntCollectionTime = 0;

	private void addIntPoint(double x, double y, double z, long timeStamp) {
		// Log.d(TAG, "Internal x: " + x + " y: " + y + " z: " + z + " ts: " +
		// timeStamp);
		// Log.o(TAG, "" + x, "" + y, "" + z);

		dx = Math.abs(lastx - x);
		dy = Math.abs(lasty - y);
		dz = Math.abs(lastz - z);
		lastInternalSampleTime = timeStamp;
		numSamplesInternal++;
		sumInternal += dx + dy + dz;
		lastx = x;
		lasty = y;
		lastz = z;
	}

	long ts = 0;

	private boolean isNewSoftwareVersion;

	@Override
	public void onSensorChanged(SensorEvent event) {

		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

			ts = (new Date()).getTime() + (event.timestamp - System.nanoTime()) / 1000000L;

			if (isFirstSample) {
				x = event.values[SensorManager.DATA_X] / SensorManager.GRAVITY_EARTH;
				y = event.values[SensorManager.DATA_Y] / SensorManager.GRAVITY_EARTH;
				z = event.values[SensorManager.DATA_Z] / SensorManager.GRAVITY_EARTH;
				numSamplesInternal = 0;
				sumInternal = 0;
				lastx = x;
				lasty = y;
				lastz = z;
				isFirstSample = false;
				startIntCollectionTime = ts;
				lastInternalSampleTime = ts;
			}

			tdiff = (ts - lastInternalSampleTime);

			// Log.d(TAG, "TDIFF: " + tdiff);

			if (tdiff > MS_EACH_INTERNAL_SAMPLE) {
				skippedSamples = (int) (tdiff / MS_EACH_INTERNAL_SAMPLE);

				if (skippedSamples > 0)
					skippedSamples--;

				// Log.d(TAG, "SKIP: " + skippedSamples);
				for (int i = 0; i < skippedSamples; i++) {
					// Log.d(TAG,"Insert");
					addIntPoint(lastx, lasty, lastz, ts + MS_EACH_INTERNAL_SAMPLE * i);
				}

				x = event.values[SensorManager.DATA_X] / SensorManager.GRAVITY_EARTH;
				y = event.values[SensorManager.DATA_Y] / SensorManager.GRAVITY_EARTH;
				z = event.values[SensorManager.DATA_Z] / SensorManager.GRAVITY_EARTH;

				addIntPoint(x, y, z, ts + MS_EACH_INTERNAL_SAMPLE * skippedSamples);
			} else {
				// Not enough time has elapsed. Do not count the sample
				// Log.d(TAG,"Skip");
			}
		}
	}
	
	private void checkAndLogVersion() {
		String version = AppUsageLogger.getVersion(getApplicationContext(), TAG);
		// Check if the version has changed. If so, init AppData
		if (!(DataStorage.getVersion(getApplicationContext(), "unk").equals(version))) {
			Log.i(TAG, "Version changed");
			isNewSoftwareVersion = true;
			Globals.InitAppInfo(getApplicationContext());
			DataStorage.setVersion(getApplicationContext(), version);
		} else
			isNewSoftwareVersion = false;

		// TODO add a last time checked for new files
		if (isNewSoftwareVersion) {
			// TODO remove this eventually
			// Make sure we have the most recent copy of all important files.
			Log.h(TAG, "New software version detected: " + version, Log.NO_LOG_SHOW);

			ServerLogger.addNote(getApplicationContext(),"New software version detected: " + version, Globals.PLOT);
		}
	}
	
	private Runnable mTask = new Runnable() {
		public void run() {
			Log.e(TAG, "NEWS THREAD ------------------------------------------------------------------------------------------------------------------------- "
					+ Globals.NEWS_URL);

			// reset configuration info before processing Wocket data
			compressANDTransferZipFiles(new Date(), getApplicationContext());

			DataStore.resetAll();
			checkAndLogVersion();

			WocketsDataSaver dataSaver = new WocketsDataSaver(getApplicationContext());
			long lastJSONQueueClearTime = DataStorage.getTime(getApplicationContext(), DataStorage.LAST_CLEAR_JSON_QUEUE_TIME);

			if ((System.currentTimeMillis() - lastJSONQueueClearTime) > Globals.MINUTES_60_IN_MS) {
				ServerLogger.addNote(getApplicationContext(), "60minUpload", Globals.PLOT);
				DataStorage.setTime(getApplicationContext(), DataStorage.LAST_CLEAR_JSON_QUEUE_TIME, System.currentTimeMillis());
				WakefulIntentService.sendWakefulWork(getApplicationContext(), DataUploaderService.class, "Message");
			}
			
			String tmp = "Time running " + ((System.currentTimeMillis() - serviceStartTime) / 1000.0) + " s";

			// Send all the information gathered during this arbitrate, or queue
			// up to send later if no network
			long startTimeTransmit = System.currentTimeMillis();

			processThenSendInternalAccelData(dataSaver);

			Log.d(TAG, "Start to put the data in the graph");
			dataSaver.cleanAndCommitData(new Date());

			Intent updateDataViewer = new Intent(GetDataSummaryActivity.INTENT_ACTION_UPDATE_DATA);
			sendBroadcast(updateDataViewer);

			// zephyrReader.stopThread();

			Log.d(TAG, "Start to queue wocket info.");
			// TODO
			// DataSender.transmitOrQueueWocketInfo(getApplicationContext(), wi,
			// true);

			ServerLogger.send(TAG, getApplicationContext());

			ServerLogger.addNote(getApplicationContext(), tmp + " Then time transmitting " + ((System.currentTimeMillis() - startTimeTransmit) / 1000.0) + " s", Globals.NO_PLOT);

			Log.e(TAG, "TEST: " + serviceStartTime + " " + Globals.MIN_MS_FOR_SENSING_WHEN_PHONE_PLUGGED_IN + " " + System.currentTimeMillis());
			//TODO Change to only do this if the phone is plugged in!
			while (System.currentTimeMillis() < (serviceStartTime + Globals.MIN_MS_FOR_SENSING_WHEN_PHONE_PLUGGED_IN)) {
				//Check if the phone is plugged in. If so, wait a while because we want to keep
				//reading sensor data for a while (usually most of the minute) before shutting down
				Log.e(TAG, "Waiting " + (((serviceStartTime + Globals.MIN_MS_FOR_SENSING_WHEN_PHONE_PLUGGED_IN)-System.currentTimeMillis())/1000.0) + " more seconds because plugged in....");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					Log.e(TAG, "Error in sleep in BluetoothSensorService");
					e.printStackTrace();
				}				
			}
			
			try {
				for (int i = 0; i < 20; ++i) {
					
					Thread.sleep(1000);
				}
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

	private void processThenSendInternalAccelData(WocketsDataSaver dataSaver) {
		double difftS = ((lastInternalSampleTime - startIntCollectionTime) / 1000.0);
		int intAC = (int) (1000 * (sumInternal / (double) numSamplesInternal));
		Log.d(TAG, "----------------------------------------------------------------------- SAMPLES: " + numSamplesInternal);
		Log.d(TAG, "----------------------------------------------------------------------- SUM    : " + sumInternal);
		Log.d(TAG, "----------------------------------------------------------------------- TIME    : " + difftS);
		Log.d(TAG, "----------------------------------------------------------------------- SR      : " + (numSamplesInternal / difftS));
		Log.d(TAG, "----------------------------------------------------------------------- SCALED  : " + intAC);
		Context aContext = getApplicationContext();
		WocketSensor wocket = new WocketSensor(aContext, "Internal", PhoneInfo.getID(aContext));

		ServerLogger.initWocketsInfo(getApplicationContext());

		dataSaver.setInternalData((int) numSamplesInternal);

		Date now = new Date();
		if ((now.before(Globals.REASONABLE_DATE))) {
			Log.e(TAG, "Creating internal data when the lastConnectiontime for the internal is not set!: " + now);
		} else {
			ActivityCountData aActivityCountData = new ActivityCountData();
			aActivityCountData = new ActivityCountData();
			aActivityCountData.activityCount = intAC;
			aActivityCountData.createTime = serviceStartDatePlus1Min; // now;
			aActivityCountData.originalTime = serviceStartDatePlus1Min; // now;
			aActivityCountData.macID = wocket.mAddress;
			ServerLogger.addActivityCountData(aActivityCountData, getApplicationContext());

			// Remove after testing:
			WocketStatsData aWocketStatsData = new WocketStatsData();
			aWocketStatsData.createTime = serviceStartDatePlus1Min; // now;
			aWocketStatsData.macID = wocket.mAddress;
			// aWocketStatsData.wocketBattery = wocket.mBattery;
			aWocketStatsData.receivedBytes = numSamplesInternal;
			aWocketStatsData.transmittedBytes = ((int) (numSamplesInternal / difftS));
			ServerLogger.addWocketsStatsData(aWocketStatsData, getApplicationContext());

			Log.i(TAG, "Send this info to JSON for INTERNAL (" + wocket.mAddress + ") and connect time: " + now + ". AC: " + intAC);
		}
	}
}
