package edu.neu.android.mhealth.uscteensver1.data;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Date;

import edu.neu.android.mhealth.uscteensver1.TeensGlobals;
import edu.neu.android.wocketslib.Globals;
import edu.neu.android.wocketslib.algorithm.ChunkingAlgorithm;
import edu.neu.android.wocketslib.algorithm.MotionDetectAlgorithm;
import edu.neu.android.wocketslib.algorithm.MotionInfo;
import edu.neu.android.wocketslib.utils.DateHelper;
import edu.neu.android.wocketslib.utils.FileHelper;

public class AccelDataChecker {
    public final static String TAG = "AccelDataChecker";

    public static MotionInfo checkDataState(long startTime, long stopTime) {

        // Get start/stop time
        if (startTime == -1) {
            startTime = MotionDetectAlgorithm.getInstance().getStartTime();
        }
        if (stopTime == -1) {
            stopTime = System.currentTimeMillis() - 60000; // make sure we have the data to analyze
        }
        if (startTime >= stopTime || Math.abs(startTime - stopTime) > Globals.HOURS24_MS) {
            return new MotionInfo(MotionInfo.ERROR, null, null);
        }

        // Get data first
        int[] sensorData = getData(startTime, stopTime);
        if (sensorData == null) {
            return new MotionInfo(MotionInfo.ERROR, null, null);
        }

        // Chunk the data just got
        ArrayList<Integer> chunkPos = ChunkingAlgorithm.getInstance().doChunking(startTime, stopTime, sensorData);
        if (chunkPos == null) {
            return new MotionInfo(MotionInfo.ERROR, null, null);
        }

        // Analyze data to get the state for context sensitive prompt
        MotionDetectAlgorithm mda = MotionDetectAlgorithm.getInstance();
        mda.MOTION_DURATION_THRESHOLD = 15 * 60;
        return mda.doMotionDetection(sensorData, chunkPos, startTime, stopTime);
    }

    private static int[] getData(long from, long to) {
        Date dateFrom = new Date(from);
        Date dateTo   = new Date(to);
        // read the whole piece of data according to the input time
        String date = DateHelper.getServerDateString(new Date());
        String[] hourDirs = FileHelper.getFilePathsDir(
            TeensGlobals.DIRECTORY_PATH + File.separator + Globals.DATA_DIRECTORY + File.separator + TeensGlobals.SENSOR_FOLDER + date
        );
        if (hourDirs == null) {
            // no data to get
            return null;
        }

        int hourFrom = dateFrom.getHours();
        int hourTo   = dateTo.getHours();
        AccelDataWrap accelDataWrap = new AccelDataWrap();

        for (String hourDir : hourDirs) {
            File file = new File(hourDir);
            if (file.isFile()) {
                continue;
            }
            String hour = hourDir.substring(hourDir.lastIndexOf('/') + 1);
            int targetHour = Integer.valueOf(hour);
            if (targetHour < hourFrom || targetHour > hourTo) {
                continue;
            }

            try {
                // each hour corresponds to one .csv file
                String[] fileNames = new File(hourDir).list(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String filename) {
                        return filename.endsWith(".csv") &&
                                filename.startsWith(Globals.SENSOR_TYPE_PHONE_ACCELEROMETER);
                    }
                });
                if (fileNames == null || fileNames.length == 0) {
                    continue;
                }
                String filePath = hourDir + File.separator + fileNames[0];
                // load the hourly data from .bin file
                ArrayList<AccelData> hourlyAccelData = new ArrayList<AccelData>();
                DataSource.loadHourlyRawAccelData(filePath, hourlyAccelData);
                accelDataWrap.add(hourlyAccelData);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        accelDataWrap.updateDrawableData();

        return accelDataWrap.getDrawableData();
    }
}
