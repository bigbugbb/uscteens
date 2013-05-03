package edu.neu.android.mhealth.uscteensver1.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.util.Log;
import edu.neu.android.mhealth.uscteensver1.USCTeensGlobals;
import edu.neu.android.mhealth.uscteensver1.pages.AppScale;
import edu.neu.android.wocketslib.Globals;
import edu.neu.android.wocketslib.utils.FileHelper;
import edu.neu.android.wocketslib.utils.WOCKETSException;

public class ActionManager {
	private final static String TAG = "ActionManager";
	// result code
	public final static int LOADING_SUCCEEDED  = 0;
	public final static int ERR_CANCELLED      = 1;
	public final static int ERR_NO_ACTION_DATA = 2;	
	
	private final static String ASSETS_DIR = "activities";
	
	protected static Context sContext = null;
	protected static ActionWrap sActionWrap = new ActionWrap();
	protected static ActionWrap sActivatedActionWrap = new ActionWrap();
	
	public static void initialize(Context context) {
		sContext = context;
		Action.initialize(context);
	}
	
	public static void start() {
		loadActions();
	}
	
	public static void stop() {		
		release();
	}
	
	public static Action getAction(String actID) {
		return sActionWrap.get(actID);
	}
	
	public static ActionWrap getActions() {
		return sActionWrap;
	}
	
	public static ActionWrap getActivatedActions() {
		return sActivatedActionWrap;
	}
	
	public static int loadActions() {
		// first clear the action container
		sActionWrap.clear();
		
		String dirPath = Globals.EXTERNAL_DIRECTORY_PATH + File.separator + 
				Globals.DATA_DIRECTORY + USCTeensGlobals.ACTIVITY_FOLDER;
		String[] actionDir = FileHelper.getFilePathsDir(dirPath);
		if (actionDir == null || actionDir.length == 0) {
			copyActionsFromAssets();
			actionDir = FileHelper.getFilePathsDir(dirPath);
			if (actionDir == null || actionDir.length == 0) {
				return ERR_NO_ACTION_DATA;
			}
		}
				
		Action action = Action.createUnlabelledAction();
		sActionWrap.put(USCTeensGlobals.UNLABELLED_GUID, action);
		try {
			File aMappingFile = getMappingFile(dirPath);			
			FileInputStream fis = null;
			BufferedReader br = null;
			try {
				fis = new FileInputStream(aMappingFile);
				InputStreamReader in = new InputStreamReader(fis);
				br = new BufferedReader(in);
				try {
					// skip the first line
					String result = br.readLine();
					while ((result = br.readLine()) != null) {						
						// parse the line
						String[] split = result.split("[,]");
						action = new Action(split[0], split[1], split[2],
								loadBitmapFromFile(dirPath + split[2].trim()));
						sActionWrap.put(split[0], action);
					}										
				} catch (IOException e) {
					Log.e(TAG, "readStringInternal: problem reading: " + aMappingFile.getAbsolutePath());
					e.printStackTrace();
				}
			} catch (FileNotFoundException e) {
				Log.e(TAG, "readStringInternal: cannot find: " + aMappingFile.getAbsolutePath());
				e.printStackTrace();
			} finally {
				if (br != null)
					try {
						br.close();
					} catch (IOException e) {
						Log.e(TAG, "readStringInternal: cannot close: " + aMappingFile.getAbsolutePath());
						e.printStackTrace();
					}
				if (fis != null)
					try {
						fis.close();
					} catch (IOException e) {
						Log.e(TAG, "readStringInternal: cannot close: " + aMappingFile.getAbsolutePath());
						e.printStackTrace();
					}
			}
		} catch (Exception e) {
			e.printStackTrace();			
		}		
				
		try {
			File aActivatedFile = getUsingFile(dirPath);		
			FileInputStream fis = null;
			BufferedReader br = null;
			try {
				fis = new FileInputStream(aActivatedFile);
				InputStreamReader in = new InputStreamReader(fis);
				br = new BufferedReader(in);
				try {
					// skip the first line
					String result = br.readLine();
					while ((result = br.readLine()) != null) {						
						// parse the line												
						sActivatedActionWrap.put(result, getActions().get(result));
					}										
				} catch (IOException e) {
					Log.e(TAG, "readStringInternal: problem reading: " + aActivatedFile.getAbsolutePath());
					e.printStackTrace();
				}
			} catch (FileNotFoundException e) {
				Log.e(TAG, "readStringInternal: cannot find: " + aActivatedFile.getAbsolutePath());
				e.printStackTrace();
			} finally {
				if (br != null)
					try {
						br.close();
					} catch (IOException e) {
						Log.e(TAG, "readStringInternal: cannot close: " + aActivatedFile.getAbsolutePath());
						e.printStackTrace();
					}
				if (fis != null)
					try {
						fis.close();
					} catch (IOException e) {
						Log.e(TAG, "readStringInternal: cannot close: " + aActivatedFile.getAbsolutePath());
						e.printStackTrace();
					}
			}
		} catch (Exception e) {
			e.printStackTrace();			
		}
		
		return LOADING_SUCCEEDED;
	}
	
	public static void release() {
		sActionWrap.clear();
	}
	
	private static Bitmap loadBitmapFromFile(String icoPath) {		 
		BitmapFactory.Options options = new BitmapFactory.Options(); 
        options.inPurgeable = true;
        options.inPreferredConfig = Config.RGB_565;     
        
        Bitmap image  = null;
    	Bitmap origin = BitmapFactory.decodeFile(icoPath, options);
    	Bitmap scaled = null;
    	// scale the image according to the current screen resolution
    	float dstWidth  = origin.getWidth(),
    	      dstHeight = origin.getHeight();        	
		dstWidth  = AppScale.doScaleW(dstWidth);
		dstHeight = AppScale.doScaleH(dstHeight);
		if (dstWidth != origin.getWidth() || dstHeight != origin.getHeight()) {
			scaled = Bitmap.createScaledBitmap(origin, (int) dstWidth, (int) dstHeight, true);
		}                
		// add to the image list
    	if (scaled != null) {
    		origin.recycle(); // explicit call to avoid out of memory
    		image = scaled;
        } else {
        	image = origin;
        }     	
		return image;
	}
	
	private static File getMappingFile(String dirPath) {
		String[] filePaths = FileHelper.getFilePathsDir(dirPath);
		String filePath = filePaths[0]; // set a default value
		
		for (String path : filePaths) {
			String extName = path.substring(path.lastIndexOf("/") + 1, path.length());
			if (extName.equals("activity_mapping.csv")) {
				filePath = path;
				break;
			}
		}
		
		return new File(filePath);
	}
	
	private static File getUsingFile(String dirPath) {
		String[] filePaths = FileHelper.getFilePathsDir(dirPath);
		String filePath = filePaths[0]; // set a default value
		
		for (String path : filePaths) {
			String extName = path.substring(path.lastIndexOf("/") + 1, path.length());
			if (extName.equals("activity_activated.csv")) {
				filePath = path;
				break;
			}
		}
		
		return new File(filePath);
	}
	
	private static void copyActionsFromAssets() {
		String outfilePath = Globals.EXTERNAL_DIRECTORY_PATH + File.separator + 
				Globals.DATA_DIRECTORY + USCTeensGlobals.ACTIVITY_FOLDER;
		// create new directory if doesn't exist
		try {
			FileHelper.createDir(outfilePath);
		} catch (WOCKETSException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// get all file names needed 
	    AssetManager assetManager = sContext.getAssets();
	    String[] files = null;
	    try {
	        files = assetManager.list(ASSETS_DIR);
	    } catch (IOException e) {
	        Log.e(TAG, "Failed to get asset file list.", e);
	    }
	    // copy each file from assets to external storage
	    for (String filename : files) {
	        InputStream in = null;
	        OutputStream out = null;
	        try {
				in  = assetManager.open(ASSETS_DIR + File.separator + filename);
				out = new FileOutputStream(outfilePath + filename);
				copyFile(in, out);
				in.close();				
				out.flush();
				out.close();				
	        } catch (IOException e) {
	            Log.e(TAG, "Failed to copy asset file: " + filename, e);
	        }       
	    }
	}
	
	private static void copyFile(InputStream in, OutputStream out) throws IOException {
	    byte[] buffer = new byte[1024];
	    int read;
	    while ((read = in.read(buffer)) != -1) {
	    	out.write(buffer, 0, read);
	    }
	}
	
}
