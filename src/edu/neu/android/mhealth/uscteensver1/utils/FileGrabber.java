package edu.neu.android.mhealth.uscteensver1.utils;

import java.io.File;
import java.io.IOException;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.UserInfo;

import edu.neu.android.wocketslib.utils.Log;

import android.content.Context;

public class FileGrabber {
	private static final String TAG = "FileGrabber";
		
	public static final String FTP_USERNAME = "bigBug";
	public static final String FTP_PASSWORD = "@wazsj@";
	public static final String FTP_SERVER   = "wockets.ccs.neu.edu";
	
	public static final String FILE_NOT_FOUND   = "No such file";
	public static final String EXTRA_FILES_LIST = "EXTRA_FILES_LIST";
	
	protected Context mContext;

	public FileGrabber(Context context) {
		mContext = context;
	}
	
	/**
	 * <p>
	 * Download one file from the server to the local phone.
	 * If the file does exist locally, the downloading will not happen.
	 * </p>
	 * 
	 * @param webFilePath
	 * 			  The remote file path.
	 * @param phoneFilePath
	 * 			  The local file path.
	 * @return
	 */
	public static String downloadFileFromSFTP(String webFilePath, String phoneFilePath) {
		
		File f = new File(phoneFilePath);
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
			
			session = jsch.getSession(FTP_USERNAME, FTP_SERVER, 22);
			UserInfo ui = new SftpUserInfo();
			session.setUserInfo(ui);
			session.setConfig("StrictHostKeyChecking", "no");
			session.setConfig("UserKnownHostsFile", "/dev/null");
			session.setPassword(ui.getPassword());
			session.setTimeout(20000);
			session.connect();

			ChannelSftp sftpChannel = (ChannelSftp) session.openChannel("sftp");
			sftpChannel.connect();

			try {				
				sftpChannel.get(webFilePath, phoneFilePath);
			} catch (SftpException e) {
				Log.e(TAG, "SftpException error downloading file: " + webFilePath + " with exception: "
						+ e.getMessage());
				String temp = e.getMessage();
				if (temp.equals(FILE_NOT_FOUND)) { // file not in server				
					result = FILE_NOT_FOUND;
				}
				success = false;
			}
		} catch (IOException e) {
			Log.e(TAG, "IO error downloading file: " + webFilePath + " with exception: " + e.getMessage());
			success = false;
		} catch (JSchException e) {
			Log.e(TAG, "jsch error downloading file: " + webFilePath + " with exception: " + e.getMessage());
			success = false;
		}

		if (!success) {
			if (f != null) {
				f.delete();
			}
		} else {
			Log.i(TAG, "Success restoring: " + phoneFilePath + " from: " + webFilePath);
			result = phoneFilePath;
		}

		return result;
	}
	
	/**
	 * <p>
	 * Download all files from the given server directory to the local phone directory.
	 * </p>
	 * 
	 * @param webDirPath
	 * 		      The given server directory.
	 * @param phoneDirPath
	 * 			  The given local phone directory.
	 * @return The names of all files download from the server. 
	 * 
	 */
/*	public static String[] downloadFilesFromSFTP(String webDirPath, String phoneDirPath) {
		
		File f = new File(phoneFilePath);
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
			
			session = jsch.getSession(FTP_USERNAME, FTP_SERVER, 22);
			UserInfo ui = new SftpUserInfo();
			session.setUserInfo(ui);
			session.setConfig("StrictHostKeyChecking", "no");
			session.setConfig("UserKnownHostsFile", "/dev/null");
			session.setPassword(ui.getPassword());
			session.setTimeout(20000);
			session.connect();

			ChannelSftp sftpChannel = (ChannelSftp) session.openChannel("sftp");
			sftpChannel.connect();

			try {				
				sftpChannel.get(webFilePath, phoneFilePath);
			} catch (SftpException e) {
				Log.e(TAG, "SftpException error downloading file: " + webFilePath + " with exception: "
						+ e.getMessage());
				String temp = e.getMessage();
				if (temp.equals(FILE_NOT_FOUND)) { // file not in server				
					result = FILE_NOT_FOUND;
				}
				success = false;
			}
		} catch (IOException e) {
			Log.e(TAG, "IO error downloading file: " + webFilePath + " with exception: " + e.getMessage());
			success = false;
		} catch (JSchException e) {
			Log.e(TAG, "jsch error downloading file: " + webFilePath + " with exception: " + e.getMessage());
			success = false;
		}

		if (!success) {
			if (f != null) {
				f.delete();
			}
		} else {
			Log.i(TAG, "Success restoring: " + phoneFilePath + " from: " + webFilePath);
			result = phoneFilePath;
		}

		return result;
	}*/
	
	private static class SftpUserInfo implements UserInfo {
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
}
