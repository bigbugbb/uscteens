package edu.neu.android.mhealth.uscteensver1.extra;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import android.util.Log;
import edu.neu.android.mhealth.uscteensver1.TeensAppManager;
import edu.neu.android.mhealth.uscteensver1.TeensGlobals;
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
	
	protected static boolean sCopied = false;
	protected static RewardWrap sRewardWrap = new RewardWrap();
	
	public static void start() {
		loadRewards();
	}
	
	public static void stop() {		
		release();
	}
	
	public static Reward getReward(int daysAfterStarting) {
		return sRewardWrap.get("" + daysAfterStarting);
	}
	
	public static RewardWrap getRewards() {
		return sRewardWrap;
	}
	
	public static int loadRewards() {
		// first clear the action container
		sRewardWrap.clear();
		
		String dirPath = TeensGlobals.DIRECTORY_PATH + File.separator + Globals.APP_DATA_DIRECTORY + TeensGlobals.REWARD_FOLDER;
		String[] rewardDir = FileHelper.getFilePathsDir(dirPath);
		if (rewardDir == null || rewardDir.length == 0 || (TeensGlobals.sUpdateConfig && !sCopied)) {
			sCopied = true;
			copyRewardFromAssets();
			rewardDir = FileHelper.getFilePathsDir(dirPath);
			if (rewardDir == null || rewardDir.length == 0) {
				return ERR_NO_REWARD_DATA;
			}
		}	
		
		// load reward configuration from assets
//	    String[] files = null;
//	    try {
//	        files = TeensAppManager.getAppAssets().list(ASSETS_DIR);
//	    } catch (IOException e) {
//	        Log.e(TAG, "Failed to get asset file list.", e);
//	    }
//	    for (String filename : files) {	    	
//			if (!filename.endsWith(".csv")) {
//				continue;
//			}
//	        InputStream in = null;
//			BufferedReader br = null;
//	        try {
//	        	String filePath = ASSETS_DIR + File.separator + filename;
//				in = TeensAppManager.getAppAssets().open(filePath);
//                br = new BufferedReader(new InputStreamReader(in));
//                String result = br.readLine();
//				while ((result = br.readLine()) != null) {						
//					// parse the line
//					String[] split = result.split("[,]");
//					Reward reward = new Reward(split[0].trim(), "file:///android_asset/rewards/" + split[1].trim(), 
//							split.length == 3 ? split[2].trim() : "");
//					sRewardWrap.put(split[0].trim(), reward);
//				}					
//	        } catch (IOException e) {
//	            Log.e(TAG, "Failed to copy asset file: " + filename, e);
//	        } finally {
//	        	if (br != null)
//					try {
//						br.close();
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//	        	if (in != null) {
//	        		try {
//						in.close();
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//	        	}
//	        }
//	    }
			
		// then load the other configuration that might be modified by user from external storage/uploading
		try {
			File aMappingFile = getMappingFile(dirPath);
			if (aMappingFile == null) {
				return LOADING_SUCCEEDED;
			}
			FileInputStream fis = null;
			BufferedReader br = null;
			try {
				fis = new FileInputStream(aMappingFile);				
				br = new BufferedReader(new InputStreamReader(fis));
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
			if (path.endsWith(".csv")) {
				filePath = path;
				break;
			}
		}
		
		return (filePath != null) ? new File(filePath) : null;
	}
	
	private static void copyRewardFromAssets() {
		String outfilePath = TeensGlobals.DIRECTORY_PATH + File.separator + Globals.APP_DATA_DIRECTORY + TeensGlobals.REWARD_FOLDER;
		// create new directory if doesn't exist
		try {
			FileHelper.createDir(outfilePath);
		} catch (WOCKETSException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// get all file names needed 
	    String[] files = null;
	    try {
	        files = TeensAppManager.getAppAssets().list(ASSETS_DIR);
	    } catch (IOException e) {
	        Log.e(TAG, "Failed to get asset file list.", e);
	    }
	    // copy each file from assets to external storage
	    for (String filename : files) {
	        InputStream in = null;
	        OutputStream out = null;
	        try {
				in  = TeensAppManager.getAppAssets().open(ASSETS_DIR + File.separator + filename);
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
