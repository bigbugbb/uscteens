package edu.neu.android.mhealth.uscteensver1.algorithm;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import android.annotation.SuppressLint;
import android.content.Context;
import edu.neu.android.mhealth.uscteensver1.USCTeensGlobals;
import edu.neu.android.mhealth.uscteensver1.data.CSState;
import edu.neu.android.wocketslib.Globals;
import edu.neu.android.wocketslib.support.DataStorage;
import edu.neu.android.wocketslib.utils.DateHelper;

public class CSDetectingAlgorithm {

	private final static int NO_SENSOR_DATA = -1;
	private final static int MAX_SECOND_IN_DAY = 86400;
	
	private final static int NO_DATA_DURATION_THRESHOLD       = 10 * 60;  // in second
	private final static int NO_DATA_TOLERATION_THRESHOLD     = 2 * 60;   // in second
	private final static int NO_DATA_MAX_TOLERATION_THRESHOLD = 1 * 3600; // in second
	private final static int ACTIVITY_DURATION_THRESHOLD      = 30 * 60;  // in second
	private final static int ACTIVITY_TOLERATION_THRESHOLD    = 10 * 60;  // in second
	private final static int NO_ACTIVITY_DURATION_THRESHOLD   = 30 * 60;  // in second
	private final static int NO_ACTIVITY_TOLERATION_THRESHOLD = 2 * 60;   // in second
	private final static float MOTION_INTENSITY_THRESHOLD = USCTeensGlobals.SENSOR_DATA_SCALING_FACTOR * 0.2f;
	
	private final static String KEY_DETECTING_START_TIME = "KEY_DETECTING_START_TIME";

	private Context mContext;
	
	// singleton
	private static CSDetectingAlgorithm sAlgorithm;
	
	public static CSDetectingAlgorithm getInstance(Context context) {
		if (sAlgorithm == null) {
			sAlgorithm = new CSDetectingAlgorithm(context);
		}
		
		// update start time if necessary
		long startTime   = sAlgorithm.getStartTime();
		long defaultTime = DateHelper.getDailyTime(8, 0);
		if (startTime < defaultTime || startTime > System.currentTimeMillis()) {
			sAlgorithm.setStartTime(defaultTime);
		}
		
		return sAlgorithm;
	}
	
	private CSDetectingAlgorithm(Context context) {
		mContext = context;				
	}

	public long getStartTime() {
		long defaultTime = DateHelper.getDailyTime(8, 0);
		long startTime = DataStorage.GetValueLong(mContext, KEY_DETECTING_START_TIME, defaultTime);		
		return startTime;
	}
	
	protected void setStartTime(long startTime) {	
		DataStorage.SetValue(mContext, KEY_DETECTING_START_TIME, startTime);
	}
	
	/**
	 * Analyze data based on chunks and duration to get the possible context sensitive detection 
	 * @param sensorData	The accelerometer between from and to
	 * @param chunkPos    	All possible chunk positions between from and to
	 * @param from			Time in milliseconds since January 1, 1970 00:00:00 UTC 	
 	 * @param to			Time in milliseconds since January 1, 1970 00:00:00 UTC
	 * @return the array stores all chunks' positions from start second to stop second
	 */
	@SuppressLint("UseSparseArrays")
	public CSState doCSDetecting(int[] sensorData, ArrayList<Integer> chunkPos, long from, long to) {
		Date startTime = new Date(from);
		Date stopTime  = new Date(to);		
		HashMap<CSState, Integer> stateMap = new HashMap<CSState, Integer>();
		
		// Convert Date to second
		long midnight = DateHelper.getDailyTime(0, 0);
		int secFrom = startTime.getHours() * 3600 + startTime.getMinutes() * 60 + startTime.getSeconds();
		int secTo   = stopTime.getHours() * 3600 + stopTime.getMinutes() * 60 + stopTime.getSeconds();
		int secNow  = (int) ((System.currentTimeMillis() - midnight) / 1000);
				
		// Create the position-to-mean hash and position-to-duration hash
		HashMap<Integer, Float>   ptmHash = new HashMap<Integer, Float>();
		HashMap<Integer, Integer> ptdHash = new HashMap<Integer, Integer>(); 
		for (int i = 0; i < chunkPos.size() - 1; ++i) {
			int curPos = chunkPos.get(i);
			int nxtPos = chunkPos.get(i + 1);
			// Get mean value from the current chunk position to the next
			float sum = 0;
			for (int j = curPos; j < nxtPos; ++j) {
				sum += sensorData[j];
			}
			int duration = Math.abs(nxtPos - curPos);
			if (duration != 0) {
				ptmHash.put(curPos, sum / duration);
				ptdHash.put(curPos, duration);
			}
		}
		
		// Merge some chunks if their means are relatively the same
		ArrayList<Integer> merged = new ArrayList<Integer>();
		for (int i = chunkPos.size() - 2; i > 0; --i) {
			int curPos = chunkPos.get(i);
			int prvPos = chunkPos.get(i - 1);
			
			Float curMean = ptmHash.get(curPos);
			Float prvMean = ptmHash.get(prvPos);						
			if (curMean == null || prvMean == null) { continue; }
			
			// Skip chunks without any data at all
			if (Math.abs(curMean - NO_SENSOR_DATA) < 0.1f || Math.abs(prvMean - NO_SENSOR_DATA) < 0.1f) {
				continue;
			}
			
			// Merge the current chunk with the next one if both of them have high or low mean value
			if ((curMean >= MOTION_INTENSITY_THRESHOLD && prvMean >= MOTION_INTENSITY_THRESHOLD) ||
				(curMean < MOTION_INTENSITY_THRESHOLD && prvMean < MOTION_INTENSITY_THRESHOLD)) {					
				// Calculate and update the mean of the chunk after merging
				Integer prvDuration = ptdHash.get(prvPos);
				Integer curDuration = ptdHash.get(curPos);
				if (prvDuration == null || curDuration == null) { continue; }
				
				int sumDuration = prvDuration + curDuration;
				if (sumDuration > 0) {
					ptmHash.put(prvPos, (curMean * curDuration + prvMean * prvDuration) / sumDuration);
					ptdHash.put(prvPos, sumDuration);
				}
				// Clear the chunk that has been merged					
				ptmHash.put(curPos, null);
				ptdHash.put(curPos, null);
				merged.add(curPos);
			}
		}
		chunkPos.removeAll(merged);
		
		// Check whether there is some periods of time without any motion data
		for (int i = 0; i < chunkPos.size() - 1; ++i) {
			int curPos = chunkPos.get(i);
			int nxtPos = chunkPos.get(i + 1);
			
			Float curMean = ptmHash.get(curPos);
			if (curMean == null) { continue; }
			
			// It's better not to compare two float values directly
			if (Math.abs(curMean - NO_SENSOR_DATA) < 0.1f) {
				if (nxtPos - curPos >= NO_DATA_DURATION_THRESHOLD) {
					// If nxtPos is the last chunk position and its value is very close to the current time in second,
					// update the starting time using the beginning position of the last chunk to make sure that
					// this position will have the chance to be checked again in the near future
					if (i == chunkPos.size() - 2 && secNow - nxtPos < NO_DATA_TOLERATION_THRESHOLD &&
							nxtPos - curPos < NO_DATA_MAX_TOLERATION_THRESHOLD) {						
						stateMap.put(new CSState(CSState.DATA_STATE_NORMAL, startTime, stopTime), curPos);						
					} else { 
						Date dateFrom = new Date(midnight + curPos * 1000);
						Date dateTo   = new Date(midnight + nxtPos * 1000);
						stateMap.put(new CSState(CSState.DATA_STATE_MISSING, dateFrom, dateTo), nxtPos);
					}
					break;
				} else if (i == chunkPos.size() - 2 && secNow - nxtPos < NO_DATA_TOLERATION_THRESHOLD) {
					stateMap.put(new CSState(CSState.DATA_STATE_NORMAL, startTime, stopTime), curPos);
					break;
				}
			}
		}
		
		// Analyze chunks with motion data from secFrom to secTo based on their mean value and duration
		for (int i = 0; i < chunkPos.size() - 2; ++i) {			
			int curPos = chunkPos.get(i);
			int nxtPos = chunkPos.get(i + 1);
			int lstPos = chunkPos.get(i + 2);
			
			// Jump the period before the starting time 
			if (secFrom > curPos) { continue; }
			
			Integer curDuration = ptdHash.get(curPos);
			Integer nxtDuration = ptdHash.get(nxtPos);			
			Float curMean = ptmHash.get(curPos);
			Float nxtMean = ptmHash.get(nxtPos);		
			
			if (curDuration == null || nxtDuration == null || curMean == null || nxtMean == null) {
				continue;
			}
							
			// First figure out the chunk with small mean followed by another one with big mean
			if (curMean < MOTION_INTENSITY_THRESHOLD && nxtMean >= MOTION_INTENSITY_THRESHOLD) {
				if (curDuration >= NO_ACTIVITY_DURATION_THRESHOLD && nxtDuration >= NO_ACTIVITY_TOLERATION_THRESHOLD) {
					Date dateFrom = new Date(midnight + curPos * 1000);
					Date dateTo   = new Date(midnight + nxtPos * 1000);
					stateMap.put(new CSState(CSState.DATA_STATE_LOW_INTENSITY, dateFrom, dateTo), nxtPos);
					break;
				}
			} else if (curMean >= MOTION_INTENSITY_THRESHOLD && nxtMean < MOTION_INTENSITY_THRESHOLD) {				
				if (curDuration >= ACTIVITY_DURATION_THRESHOLD && nxtDuration >= ACTIVITY_TOLERATION_THRESHOLD) {
					Date dateFrom = new Date(midnight + curPos * 1000);
					Date dateTo   = new Date(midnight + nxtPos * 1000);
					stateMap.put(new CSState(CSState.DATA_STATE_HIGH_INTENSITY, dateFrom, dateTo), lstPos);
					break;
				}
			}
		}
		
		// Look for the earliest state by iterating each state in the hash map 
		Iterator<Entry<CSState, Integer>> iterator = stateMap.entrySet().iterator();
		CSState targetState = null;
		Integer minPosition = MAX_SECOND_IN_DAY;
		Entry<CSState, Integer> entry = null;
        while (iterator.hasNext()) {
            entry = iterator.next();
            if (entry.getValue() < minPosition) {
            	targetState = entry.getKey();
            	minPosition = entry.getValue();
            }
        }
        
        // If the satisfied position is found, update the start time to this position and return the state
        if (targetState != null) {
        	setStartTime(midnight + minPosition * 1000);
        	return targetState;
        }

		// Update start time for the next check
		if (chunkPos.size() >= 3) {
			setStartTime(midnight + chunkPos.get(chunkPos.size() - 3) * 1000);
		}
				
		return new CSState(CSState.DATA_STATE_NORMAL, startTime, stopTime);
	}
}
