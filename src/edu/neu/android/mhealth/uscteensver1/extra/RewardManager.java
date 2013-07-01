package edu.neu.android.mhealth.uscteensver1.extra;

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
import android.util.Log;
import edu.neu.android.mhealth.uscteensver1.USCTeensGlobals;
import edu.neu.android.wocketslib.Globals;
import edu.neu.android.wocketslib.utils.FileHelper;
import edu.neu.android.wocketslib.utils.WOCKETSException;

public class RewardManager {
	private final static String TAG = "RewardManager";
	// result code
	public final static int LOADING_SUCCEEDED    = 0;
	public final static int ERR_CANCELLED        = -1;
	public final static int ERR_NO_REWARD_DATA   = -2;
	public final static int ERR_NO_EXTERNAL_DATA = -3;
	
	private final static String ASSETS_DIR = "rewards";
	
	protected static Context sContext = null;
	protected static boolean sCopied = false;
	protected static RewardWrap sRewardWrap = new RewardWrap();
	
	public static void initialize(Context context) {
		sContext = context;		
	}
	
	public static void start() {
		loadRewards();
	}
	
	public static void stop() {		
		release();
	}
	
	public static Reward getReward(int daysAfterStarting) {
		return sRewardWrap.get("" + (daysAfterStarting + 1));
	}
	
	public static RewardWrap getRewards() {
		return sRewardWrap;
	}
	
	public static int loadRewards() {
		// first clear the action container
		sRewardWrap.clear();
		
		String dirPath = USCTeensGlobals.DIRECTORY_PATH + File.separator + Globals.APP_DATA_DIRECTORY + USCTeensGlobals.REWARD_FOLDER;
		String[] rewardDir = FileHelper.getFilePathsDir(dirPath);
		if (rewardDir == null || rewardDir.length == 0 || (USCTeensGlobals.sUpdateConfig && !sCopied)) {
			sCopied = true;
			copyRewardFromAssets();
			rewardDir = FileHelper.getFilePathsDir(dirPath);
			if (rewardDir == null || rewardDir.length == 0) {
				return ERR_NO_REWARD_DATA;
			}
		}	
		
		// load reward configuration from assets
		AssetManager assetManager = sContext.getAssets();
	    String[] files = null;
	    try {
	        files = assetManager.list(ASSETS_DIR);
	    } catch (IOException e) {
	        Log.e(TAG, "Failed to get asset file list.", e);
	    }
	    for (String filename : files) {
	    	String extName = filename.substring(filename.lastIndexOf("."), filename.length());
			if (!extName.equals(".csv")) {
				continue;
			}
	        InputStream in = null;
	        InputStreamReader isr = null;
			BufferedReader br = null;
	        try {
	        	String filePath = ASSETS_DIR + File.separator + filename;
				in = assetManager.open(filePath);			
				isr = new InputStreamReader(in); 
                br = new BufferedReader(isr);
                String result = br.readLine();
				while ((result = br.readLine()) != null) {						
					// parse the line
					String[] split = result.split("[,]");
					Reward reward = new Reward(split[0].trim(), 
							"file:///android_asset/rewards/" + split[1].trim(), 
							split.length == 3 ? split[2].trim() : "");
					sRewardWrap.put(split[0].trim(), reward);
				}	
				in.close();
	        } catch (IOException e) {
	            Log.e(TAG, "Failed to copy asset file: " + filename, e);
	        }       
	    }
			
		// then load the other configuration that might be modified by user from external storage
		try {
			File aMappingFile = getMappingFile(dirPath);
			if (aMappingFile == null) {
				return LOADING_SUCCEEDED;
			}
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
						Reward reward = new Reward(split[0].trim(), "file:///" + dirPath + split[1].trim(), 
								split.length == 3 ? split[2].trim() : "");
						sRewardWrap.put(split[0].trim(), reward);
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
						
		return LOADING_SUCCEEDED;
	}
	
	public static void release() {
		sRewardWrap.clear();
	}
	
	private static File getMappingFile(String dirPath) {
		String[] filePaths = FileHelper.getFilePathsDir(dirPath);
		String filePath = null;
		
		for (String path : filePaths) {
			String extName = path.substring(path.lastIndexOf("."), path.length());
			if (extName.equals(".csv")) {
				filePath = path;
				break;
			}
		}
		
		return (filePath != null) ? new File(filePath) : null;
	}
	
	private static void copyRewardFromAssets() {
		String outfilePath = USCTeensGlobals.DIRECTORY_PATH + File.separator + Globals.APP_DATA_DIRECTORY + USCTeensGlobals.REWARD_FOLDER;
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
