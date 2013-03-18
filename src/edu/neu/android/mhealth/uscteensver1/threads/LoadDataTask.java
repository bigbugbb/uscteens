package edu.neu.android.mhealth.uscteensver1.threads;

import edu.neu.android.mhealth.uscteensver1.data.DataSource;
import edu.neu.android.mhealth.uscteensver1.pages.AppCmd;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;


public class LoadDataTask extends AsyncTask<String, Void, Void>{
	private Context mContext;
	private int 	mResult;	
	private Handler mHandler;
	private ProgressDialog mProgDlg;
	
	public LoadDataTask(Context context, Handler handler) {
		mContext = context;
		mResult  = DataSource.LOADING_SUCCEEDED;
		mHandler = handler;
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		mProgDlg = ProgressDialog.show(mContext, "", "loading data... ", true, false);
	}

	@Override
	protected void onPostExecute(Void result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		
		if (mResult == DataSource.LOADING_SUCCEEDED){
			Message msg = mHandler.obtainMessage();	
			msg.arg1 = mResult;
			msg.what = AppCmd.END_LOADING;
			mHandler.sendMessage(msg);
		} else if (mResult == DataSource.ERR_NO_SENSOR_DATA) {
			Message msg = mHandler.obtainMessage();	
			msg.arg1 = mResult;
			msg.what = AppCmd.END_LOADING;
			mHandler.sendMessage(msg); 
		} else if (mResult == DataSource.ERR_NO_CHUNK_DATA) {
			Message msg = mHandler.obtainMessage();	
			msg.arg1 = mResult;
			msg.what = AppCmd.END_LOADING;
			mHandler.sendMessage(msg); 
		}
		
		mProgDlg.dismiss();
	}

	@Override
	protected Void doInBackground(String[] params) {
		mResult = DataSource.loadRawData(params[0]);
		return null;
	}

}
