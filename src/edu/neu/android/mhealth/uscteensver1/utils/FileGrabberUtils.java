package edu.neu.android.mhealth.uscteensver1.utils;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.apache.http.util.ByteArrayBuffer;

import android.content.Context;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.UserInfo;

import edu.neu.android.mhealth.uscteensver1.TeensGlobals;
import edu.neu.android.wocketslib.Globals;
import edu.neu.android.wocketslib.utils.Log;

public class FileGrabberUtils {
	private static final String TAG = "FileGrabberUtils";

	private static final String SRV_UPLOADS  = "/home/sftpdownload/USCTeensDownloads/";
	private static final String FTP_USERNAME = "sftpdownload";
	private static final String FTP_PASSWORD = "$parRow1ark";
	private static final String FTP_SERVER   = "wockets.ccs.neu.edu";
	
	public static final String FILE_NOT_FOUND = "No such file";
	public static final String EXTRA_FILES_LIST = "EXTRA_FILES_LIST";

	private long ReadTimestampFile(String aFilePath) {
		long result = 0;
		int size = 0;
		char[] buf = new char[200];
		// try to read the content
		try {
			FileReader f;
			f = new FileReader(aFilePath + ".timestamp");
			size = f.read(buf);
			// if (size != 0){
			// size can return -1
			if (size > 0) {
				String aStr = new String(buf, 0, size);
				try {
					result = Long.parseLong(aStr);
				}
				catch (NumberFormatException e) {
					// If we have a number format exception, it probably means
					// the file is corrupt.
					// We'll return 0 to redownload the file and create a new
					// (hopefully correct) timestamp file --TL
					if (f != null) {
						f.close();
					}
					return result;
				}
			}
			f.close();
		}
		catch (IOException e1) {
			// do something if an IOException occurs, which can happen
			// freqeuntly because file not existing
			// Log.e(TAG, "Error reading file: " + aFilePath +
			// ".timestamp\n" + e1.toString());
		}
		return result;
	}

	// Write a timestamp file that includes a single unixtime for when the
	// server reports
	// the file was updated
	private void WriteTimestampFile(String aFilePath, long aUnixTime) {
		// try to write the content
		try {

			FileWriter f;
			f = new FileWriter(aFilePath + ".timestamp", false); // Do not
			// append
			f.write(String.valueOf(aUnixTime));
			f.flush();
			f.close();
		}
		catch (IOException e1) {
			// do something if an IOException occurs.
			Log.e(TAG, "Error writing file: " + aFilePath + ".timestamp\n" + e1.toString());
		}
	}

	// Write a timestamp file that includes a single unixtime for when the
	// server reports
	// the file was updated
	private void WriteMarkUpdatedFile(String aFilePath) {
		// try to write the content
		try {
			FileWriter f;
			f = new FileWriter(aFilePath + ".updated", false); // Do not
			// append
			f.write("Update");
			f.flush();
			f.close();
		}
		catch (IOException e1) {
			// do something if an IOException occurs.
			Log.e(TAG, "Error writing file: " + aFilePath + ".updated\n" + e1.toString());
		}
	}

	public void downloadFile(String webFile, String phoneFile) {
		if (Globals.IS_DEBUG)
			Log.d(TAG, "Check download " + webFile + " to " + phoneFile);

		FileOutputStream fos = null;

		try {
			URL url = new URL(webFile);
			File file = new File(phoneFile);
			File dir = file.getParentFile();
			if (!(dir.mkdirs() || dir.isDirectory())) {
				Log.d(TAG, "Fail to create directory: " + dir);
				return;
			}

			long startTime = System.currentTimeMillis();

			URLConnection ucon = url.openConnection();
			long serverTimeLastModified = ucon.getLastModified();
			long serverFileSize = ucon.getContentLength();
			long tsFileLastTimeModified = ReadTimestampFile(phoneFile);
			File f = new File(phoneFile);
			long unixFileSize = 0;
			if (f.exists()) {
				unixFileSize = f.length();
			}

			// Log.i(TAG, "LastModTimeNew,Ser:" + unixTimeLastModified + ",SD:"
			// + unixTimeWrittenFromTSFile + ",SD2:" + unixTimeWrittenFromFile);
			Log.i(TAG, "LastModTime,Ser:" + serverTimeLastModified + ",SD:"
					+ tsFileLastTimeModified);
			Log.i(TAG, "FileSizeNew,Ser:" + serverFileSize + ",SD:" + unixFileSize);

			if ((serverTimeLastModified == tsFileLastTimeModified)
					&& (serverFileSize == unixFileSize)) {
				// No need to download a new file. Local same as on server
				if (Globals.IS_DEBUG)
					Log.d(TAG, "File up to date (no download): " + phoneFile);
			} else {
				// Something has changed with write time or with file size, so
				// download!

				if (Globals.IS_DEBUG)
					Log.d(TAG, "File not up to date (downloading): " + phoneFile);

				Log.i(TAG, "DOWNLOAD," + phoneFile);

				// Grabbing data

				/*
				 * Define InputStreams to read from the URLConnection.
				 */
				InputStream is = ucon.getInputStream();
				BufferedInputStream bis = new BufferedInputStream(is, 8000);

				fos = new FileOutputStream(phoneFile);

				/*
				 * Read bytes to the Buffer until there is nothing more to
				 * read(-1).
				 */
				ByteArrayBuffer baf = new ByteArrayBuffer(50);
				int current = 0;

				// Saving data: " + getShortName(phoneFile));

				int saveCount = 0;
				while ((current = bis.read()) != -1) {
					baf.append((byte) current);
					saveCount++;

					if (saveCount == 50) {
						fos.write(baf.toByteArray());
						baf = new ByteArrayBuffer(50);
						saveCount = 0;
					}
				}

				if (saveCount != 0) {
					// Save remaining
					fos.write(baf.toByteArray());
				}

				fos.close();

				// Got the file so update the timestamp file
				WriteTimestampFile(phoneFile, serverTimeLastModified);
				WriteMarkUpdatedFile(phoneFile);

				if (Globals.IS_DEBUG)
					Log.d("FileGrabberService", "download ready in"
							+ ((System.currentTimeMillis() - startTime) / 1000) + " sec");
			}
		} catch (IOException e) {
			Log.e("FileGrabberService", "Error: " + e.toString());
		}
	}

	final static String[] RELATIVE_DATE_FILE_LOCATIONS = new String[] {
			"logs/level4foodentry/Level4-FoodEntry.csv",
			"logs/healthymealtracker/HealthyMealTracker.csv", "data/foodquiz/FoodQuiz.csv",
			"data/level3fruits/Level3-Fruits.log.csv", "data/level3pa/Level3-PA.log.csv",
			"data/level3ssb/Level3-SSB.log.csv", "data/level3veggies/Level3-Veggies.log.csv",
			"data/level1goalsetting/Level1-GoalSettingPriorGoals.log.csv",
			"data/level1goalsetting/Level1-GoalSetting.log.csv",
			"data/level3barriers/Level3-Barriers.log.csv", "data/foodquiz/FoodQuiz.log.csv",
			"data/foodquiz/FoodQuizScores.log.csv", "data/surveillance/Surveillance.log.csv",
			"data/level3meat/Level3-Meat.log.csv",
			"data/level2weight/Level2-WeightTracking.log.csv", "ds", };

	public static File getAppInternalFile(Context c, String fileName) {
		return new File(c.getDir(Globals.APP_DIRECTORY, Context.MODE_PRIVATE) + File.pathSeparator + fileName);
	}

	public static String getCurrentPhoneServerDirectory(Context c, String s) {
		String result = SRV_UPLOADS + s + "/";// + "uscteens";
		return result;
	}

	final public static String[] FLAT_FILE_LOCATIONS = new String[] { "test.txt" /*"Reward.csv"*/ };

	final public static String[] OPTIONAL_FILES = new String[] { "Countdown.log.csv", };

	public static String getCurrentPhoneLocalInternalDirectory(Context c) {
		//return c.getDir(Globals.APP_DIRECTORY, Context.MODE_PRIVATE).toString();
		String dirPath = TeensGlobals.DIRECTORY_PATH + File.separator + 
				Globals.APP_DATA_DIRECTORY + File.separator + "Rewards";
		return dirPath;
	}

	public static boolean downloadServerDataFilesWithResult(Context c, String pid) {
		return downloadServerDataFiles(c, pid).length == FLAT_FILE_LOCATIONS.length;
	}

	public static String[] downloadServerDataFiles(Context c, String pid) {
		return downloadServerDataFiles(c, getCurrentPhoneServerDirectory(c, pid),
				getCurrentPhoneLocalInternalDirectory(c));
	}

	public static String[] downloadServerDataFiles(Context c, String currentPhoneServerDirectory,
			String currentPhoneLocalDirectory) {
		if (currentPhoneLocalDirectory == null) {
			String message = "Can't download data files to local storage";
			Log.e(TAG, message);
			return new String[] {};
		}
		ArrayList<String> result = new ArrayList<String>();
		for (String f : FLAT_FILE_LOCATIONS) {
			String s = simpleDownloadSftp(currentPhoneServerDirectory + "/" + f,
					currentPhoneLocalDirectory + File.separator + f, c);

			if (!s.equals("")) {
				result.add(s);
			}
		}
		return result.toArray(new String[] {});
	}

	public static String simpleDownloadSftp(String webFile, String phoneFile, Context context) {

		File f = new File(phoneFile);

		if (f.exists()) {
			return f.toString();
		}

		String result = "";

		JSch jsch = new JSch();

		Session session = null;
		boolean success = true;

		File dir = f.getParentFile();
		if (dir != null && !dir.exists()) {
			dir.mkdirs();
		}

		try {
			f.createNewFile();
			/*
			 * String FTP_SERVER=null;
			 * if(ControllerServiceUtils.getIsTester(context))
			 * FTP_SERVER="phi-city.ccs.neu.edu"; else
			 * FTP_SERVER="cityproject.media.mit.edu";
			 */
			session = jsch.getSession(FTP_USERNAME, FTP_SERVER, 22);
			UserInfo ui = new MyUserInfo();
			session.setUserInfo(ui);
			session.setConfig("StrictHostKeyChecking", "no");
			session.setConfig("UserKnownHostsFile", "/dev/null");
			session.setPassword(ui.getPassword());
			session.setTimeout(20000);
			session.connect();

			ChannelSftp sftpChannel = (ChannelSftp) session.openChannel("sftp");
			sftpChannel.connect();

			try {
				sftpChannel.get(webFile, phoneFile);
			} catch (SftpException e) {
				Log.e(TAG, "SftpException error downloading file: " + webFile + " with exception: "
						+ e.getMessage());
				String temp = e.getMessage();
				if (temp.equals(FILE_NOT_FOUND)) /* file not in server */
				{
					result = FILE_NOT_FOUND;
				}
				success = false;
			}
		} catch (IOException e) {
			Log.e(TAG,
					"IO error downloading file: " + webFile + " with exception: " + e.getMessage());
			success = false;
		} catch (JSchException e) {
			Log.e(TAG,
					"jsch error downloading file: " + webFile + " with exception: "
							+ e.getMessage());
			success = false;
		}

		if (!success) {
			if (f != null) {
				f.delete();
			}
		} else {
			Log.i(TAG, "Success restoring: " + webFile + " from: " + webFile);
			result = phoneFile;
		}

		return result;
	}

	private static class MyUserInfo implements UserInfo {
		public String getPassphrase() {
			return null;
		}

		public boolean promptPassphrase(String message) {
			return true;
		}

		@Override
		public boolean promptPassword(String arg0) {
			return false;
		}

		@Override
		public boolean promptYesNo(String arg0) {
			return false;
		}

		@Override
		public void showMessage(String arg0) {
		}

		@Override
		public String getPassword() {
			return FTP_PASSWORD;
		}

	}

	public static void tryClose(Closeable c) {
		if (c != null) {
			try {
				c.close();
			} catch (IOException e) {
				Log.e(TAG, "Error closing file: " + e.toString());
			}
		}
	}

}
