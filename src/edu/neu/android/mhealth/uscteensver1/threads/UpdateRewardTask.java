package edu.neu.android.mhealth.uscteensver1.threads;

import java.io.File;

import android.content.Context;
import android.os.AsyncTask;
import android.view.Gravity;
import android.widget.Toast;
import edu.neu.android.mhealth.uscteensver1.TeensGlobals;
import edu.neu.android.wocketslib.Globals;
import edu.neu.android.wocketslib.utils.FileGrabber;
import edu.neu.android.wocketslib.utils.SftpFileGrabber;

/**
 * Set the update button on/off depending on whether the task is completed.  
 */
public class UpdateRewardTask extends AsyncTask<Void, Void, Boolean> {
	private final static String TAG = "UpdateAppDataTask";
	
	private Context mContext;
	private String  mLocalDirPath;
	private String  mRemoteDirPath;
	
	public UpdateRewardTask(Context context) {
		mContext = context;
		mLocalDirPath  = TeensGlobals.DIRECTORY_PATH + File.separator + Globals.APP_DATA_DIRECTORY + TeensGlobals.REWARD_FOLDER;
		//mRemoteDirPath = "/home/sftpdownload/USCTeensDownloads/" + TeensAppManager.getParticipantId(context) + File.separator + "download";
		mRemoteDirPath = "/home/bigBug/100012/download/";
	}
	
	@Override
	protected Boolean doInBackground(Void... params) {
		SftpFileGrabber fileGrabber = new SftpFileGrabber();
		String result = fileGrabber.downloadFiles(mRemoteDirPath, mLocalDirPath, true, true);
		return !result.startsWith(FileGrabber.ERROR_PREFIX);
	}

	protected void onPostExecute(Boolean isSuccessful) {
		String msg = isSuccessful ? "Update complete." : "Fail to update, please try it later.";
		Toast toast = Toast.makeText(mContext, msg, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
		toast.show();	
	}
}
