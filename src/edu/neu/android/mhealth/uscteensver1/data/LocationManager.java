package edu.neu.android.mhealth.uscteensver1.data;

import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import au.com.bytecode.opencsv.CSVReader;
import edu.neu.android.mhealth.uscteensver1.TeensGlobals;
import edu.neu.android.wocketslib.Globals;
import edu.neu.android.wocketslib.utils.DateHelper;
import edu.neu.android.wocketslib.utils.FileHelper;

public class LocationManager {
	
	private final static String TAG = "LocationManager";
    // result code
    public final static int LOADING_SUCCEEDED    = 0;
    public final static int ERR_CANCELLED        = -1;
    public final static int ERR_NO_LOCATION_DATA = -2;
    public final static int ERR_IO_EXCEPTION     = -3;
    
    private final static float THRESHOLD_LONGITUDE = 0.004f;
    private final static float THRESHOLD_LATITUDE  = 0.004f;
	
	private static LocationData sOldLocation;
	private static LocationData sNewLocation;
	
	private static ArrayList<LocationData> sLocations = new ArrayList<LocationData>(); 

	public static boolean isLocationShifted() {
		
		String date = DateHelper.serverDateFormat.format(new Date());
		loadLocations(date);
		
		if (sOldLocation == null || sNewLocation == null) {
			return false;
		}
		
		if (Math.abs(sNewLocation.getLongitude() - sOldLocation.getLongitude()) > THRESHOLD_LONGITUDE ||
			Math.abs(sNewLocation.getLatitude() - sNewLocation.getLatitude()) > THRESHOLD_LATITUDE) {
			return true;
		}
		
		return false;
	}
	
	public static int loadLocations(String date) {
		String[] hourDirs = FileHelper.getFilePathsDir(
            TeensGlobals.DIRECTORY_PATH + File.separator + Globals.DATA_DIRECTORY + TeensGlobals.SENSOR_FOLDER + date
        );
		
		sLocations.clear();

        // load the daily data from csv files hour by hour
        for (String hourDir : hourDirs) {        	        	
            String[] fileNames = new File(hourDir).list(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    return filename.endsWith(".csv") && filename.startsWith(Globals.SENSOR_TYPE_GPS);
                }
            });
            if (fileNames == null || fileNames.length == 0) {
                continue;
            }
            String filePath = hourDir + File.separator + fileNames[0];
            // load the hourly gps data
            CSVReader csvReader = null;
            try {
                csvReader = new CSVReader(new FileReader(filePath));
                String[] row = csvReader.readNext();
                while ((row = csvReader.readNext()) != null) {
                    // hack the split position
                    LocationData data = new LocationData(
                    	row[0], Float.parseFloat(row[1]), Float.parseFloat(row[2]), Float.parseFloat(row[3])
                    );
                    sLocations.add(data);
                }
            } catch (IOException e) {
                e.printStackTrace();
                return ERR_IO_EXCEPTION;
            } finally {
                try {
                    if (csvReader != null) {
                        csvReader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
        if (sLocations.size() == 0) {            
        	return ERR_NO_LOCATION_DATA;            
        } else {
        	sOldLocation = sNewLocation != null ? sNewLocation.clone() : null;
        	sNewLocation = sLocations.get(sLocations.size() - 1).clone();
        }

        return LOADING_SUCCEEDED;
	}
	
	public static void release() {
		sLocations.clear();
	}
}
