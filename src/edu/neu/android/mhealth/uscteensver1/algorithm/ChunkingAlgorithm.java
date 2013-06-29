package edu.neu.android.mhealth.uscteensver1.algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import edu.neu.android.mhealth.uscteensver1.USCTeensGlobals;

final public class ChunkingAlgorithm {

	// thresholds for generating chunks 	
	private final static int CHUNKING_MIN_MEAN_AVG_DIFF = (int) (USCTeensGlobals.SENSOR_DATA_SCALING_FACTOR * 0.1f);
	private final static int CHUNKING_MAX_MEAN_AVG_DIFF = (int) (USCTeensGlobals.SENSOR_DATA_SCALING_FACTOR * 0.3f);
	private final static int CHUNKING_MEAN_AVG_DISTANCE = 60;	// 1 minute
	private final static int CHUNKING_TINY_VALUE		= 50;
	private final static int CHUNKING_LOW_VALUE		    = (int) (USCTeensGlobals.SENSOR_DATA_SCALING_FACTOR * 0.1f);
	private final static int CHUNKING_SENSITIVITY       = (int) (USCTeensGlobals.SENSOR_DATA_SCALING_FACTOR * 0.3f);
	private final static int CHUNKING_MIN_DISTANCE 	    = 120;	// 2 minutes
	
	// value for no data period
	private final static int NO_SENSOR_DATA = -1;
	
	// singleton
	private static ChunkingAlgorithm sAlgorithm = new ChunkingAlgorithm();
	
	public static ChunkingAlgorithm getInstance() {
		return sAlgorithm;
	}
	
	/**
	 * Create chunks according to the variation of the input data and time period 
	 * @param startTimeInMS	The start time in millisecond
	 * @param stopTimeInMS  The stop time in millisecond
	 * @param dataToChunk   The input data to chunk (accelerometer data in this app)
	 * @return the array stores all chunks' positions from start second to stop second
	 */
	public ArrayList<Integer> doChunking(long startTimeInMS, long stopTimeInMS, final int[] dataToChunk) {
		Date dateStart = new Date(startTimeInMS);
		Date dateStop  = new Date(stopTimeInMS);
		// Convert to second
		int secStop  = dateStop.getHours() * 3600 + dateStop.getMinutes() * 60 + dateStop.getSeconds();
		int secStart = dateStart.getHours() * 3600 + dateStart.getMinutes() * 60 + dateStart.getSeconds();
		
		return doChunking(secStart, secStop, dataToChunk);
	}
	
	/**
	 * Create chunks according to the variation of the input data and time period 
	 * @param startSecond	The start time in second
	 * @param stopSecond    The stop time in second
	 * @param dataToChunk   The input data to chunk (accelerometer data in this app)
	 * @return the array stores all chunks' positions from start second to stop second
	 */
	public ArrayList<Integer> doChunking(int startSecond, int stopSecond, final int[] dataToChunk) {		
		if (dataToChunk == null) {
			return null;
		}
		
		int size = dataToChunk.length;		
		// data should be enough for filling in at least one chunk space
		if (size < CHUNKING_MIN_DISTANCE) {
			return null;
		}
		
		ArrayList<Integer> chunkPos = new ArrayList<Integer>();
		//int[] sensorData = sAccelDataWrap.getDrawableData();
		
		// convolution to the accelerometer data
		int[] convolution = Arrays.copyOf(dataToChunk, size);
		for (int i = 1; i < size - 1; ++i) { // [-2 0 2]
			convolution[i] = Math.abs(dataToChunk[i + 1] - dataToChunk[i - 1]) << 1;
		}

		// calculate mean average of CHUNKING_MIN_DISTANCE points' of data to the left of current position
		int sum = 0;
		int[] meanAverageL = new int[size];
		Arrays.fill(meanAverageL, 0, CHUNKING_MEAN_AVG_DISTANCE, 0);		
		for (int i = 0; i < CHUNKING_MEAN_AVG_DISTANCE; ++i) {
			sum += dataToChunk[i];
		}
		for (int i = CHUNKING_MEAN_AVG_DISTANCE; i < size; ++i) {
			meanAverageL[i] = sum / CHUNKING_MEAN_AVG_DISTANCE;
			sum += dataToChunk[i];
			sum -= dataToChunk[i - CHUNKING_MEAN_AVG_DISTANCE];
		}

		// calculate mean average of CHUNKING_MIN_DISTANCE points' of data to the right of current position
		sum = 0;
		int[] meanAverageR = new int[size];
		Arrays.fill(meanAverageR, size - CHUNKING_MEAN_AVG_DISTANCE, size, 0);		
		for (int i = size - 1; i >= size - CHUNKING_MEAN_AVG_DISTANCE; --i) {
			sum += dataToChunk[i];
		}
		for (int i = size - CHUNKING_MEAN_AVG_DISTANCE - 1; i >= 0; --i) {
			meanAverageR[i] = sum / CHUNKING_MEAN_AVG_DISTANCE;
			sum += dataToChunk[i];
			sum -= dataToChunk[i + CHUNKING_MEAN_AVG_DISTANCE];
		}

		// figure out the possible chunking positions
		int prev = startSecond;
		chunkPos.add(startSecond);
		for (int i = startSecond + 1; i < size - CHUNKING_MIN_DISTANCE; ++i) {
			if (i - prev < CHUNKING_MIN_DISTANCE) {
				continue;
			}
			
			// check the jumped area to determine the possible end of chunk
			if (i - prev == CHUNKING_MIN_DISTANCE) {
				boolean hasBigChange = false;
				for (int j = i; j > i - CHUNKING_MIN_DISTANCE * 0.75f; --j) {
					if (convolution[j] > CHUNKING_SENSITIVITY) {
						hasBigChange = true;
						break;
					}
				}
				if (hasBigChange && meanAverageR[i] < CHUNKING_TINY_VALUE) {
					chunkPos.add(i);
					prev = i;
					continue;
				}
			}
			
			// chunking for no data area
			if (dataToChunk[i] == NO_SENSOR_DATA) {
				if (dataToChunk[i - 1] != NO_SENSOR_DATA || dataToChunk[i + 1] != NO_SENSOR_DATA) {
					chunkPos.add(i);
					prev = i;
					continue;
				}
			}
			
			int next = i + CHUNKING_MIN_DISTANCE;
			// chunking for data change area
			if (convolution[i] > CHUNKING_SENSITIVITY) {
				if (meanAverageL[i] > CHUNKING_LOW_VALUE && meanAverageR[i] < CHUNKING_LOW_VALUE) {
					chunkPos.add(i);
					prev = i;
					continue;
				}
				if ((meanAverageL[i] < CHUNKING_LOW_VALUE && meanAverageR[i] > CHUNKING_LOW_VALUE) ||					 
					Math.abs(meanAverageL[i] - meanAverageR[i]) > CHUNKING_MAX_MEAN_AVG_DIFF) {					
					chunkPos.add(i);
					if (next < size - CHUNKING_MIN_DISTANCE && dataToChunk[next] < CHUNKING_LOW_VALUE) {
						chunkPos.add(next);
						prev = next;
					} else {
						prev = i;
					}
					continue;
				}				
			}
			
			// chunking for isolated sensor data with huge value
			if (dataToChunk[i] > CHUNKING_SENSITIVITY) {				
				if (meanAverageL[i] < CHUNKING_LOW_VALUE && meanAverageR[next] < CHUNKING_LOW_VALUE) {
					chunkPos.add(i);
					if (next < size - CHUNKING_MIN_DISTANCE && dataToChunk[next] < CHUNKING_LOW_VALUE) {
						chunkPos.add(next);
						prev = next;
					} else {
						prev = i;
					}
				}	
				continue;
			}
			
			// sensor data increases gradually
			if (Math.abs(meanAverageL[i] - meanAverageR[i]) > CHUNKING_MIN_MEAN_AVG_DIFF) {
				if (meanAverageL[i] < CHUNKING_TINY_VALUE) {
					chunkPos.add(i);
					if (next < size - CHUNKING_MIN_DISTANCE && dataToChunk[next] < CHUNKING_LOW_VALUE) {
						chunkPos.add(next);
						prev = next;
					} else {
						prev = i;
					}
					continue;
				} else if (meanAverageR[i] < CHUNKING_TINY_VALUE) {
					chunkPos.add(i);
					prev = i;
					continue;
				} else if (meanAverageL[i] < CHUNKING_LOW_VALUE && meanAverageR[i] > CHUNKING_LOW_VALUE) {	
					// if some big change happens in the near future
					boolean hasBigChange = false;
					for (int j = i; j < i + CHUNKING_MIN_DISTANCE / 2 && !hasBigChange; ++j) {						
						if (convolution[j] > CHUNKING_SENSITIVITY) {
							hasBigChange = true;	
							i = j - 1;
						}
					}
					if (hasBigChange) {						
						continue;
					}
										
					chunkPos.add(i);
					if (next < size - CHUNKING_MIN_DISTANCE && dataToChunk[next] < CHUNKING_LOW_VALUE) {
						chunkPos.add(next);
						prev = next;
					} else {
						prev = i;
					}							
				} else if (meanAverageL[i] > CHUNKING_LOW_VALUE && meanAverageR[i] < CHUNKING_LOW_VALUE) {											
					chunkPos.add(i);
					prev = i;
				}
			}			
		}
		chunkPos.add(stopSecond);
		
		return chunkPos;
	}
}
