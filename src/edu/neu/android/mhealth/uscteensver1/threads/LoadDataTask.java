package edu.neu.android.mhealth.uscteensver1.threads;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import edu.neu.android.mhealth.uscteensver1.data.DataSource;
import edu.neu.android.mhealth.uscteensver1.pages.AppCmd;


public class LoadDataTask extends AsyncTask<String, Void, Void>{
	private Context mContext;
	private int 	mResult;	
	private Handler mHandler;

	
	public LoadDataTask(Context context, Handler handler) {
		mContext = context;
		mResult  = DataSource.LOADING_SUCCEEDED;
		mHandler = handler;
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		// mProgDlg = ProgressDialog.show(mContext, "", "loading data... ", true, false);
	}

	@Override
	protected void onPostExecute(Void result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		
		Message msg = mHandler.obtainMessage();	
		
		if (mResult == DataSource.LOADING_SUCCEEDED){			
			msg.arg1 = mResult;
			msg.what = AppCmd.END_LOADING;			
		} else if (mResult == DataSource.ERR_CANCELLED) { 
			msg.arg1 = mResult;
			msg.what = AppCmd.END_LOADING;
		} else if (mResult == DataSource.ERR_NO_SENSOR_DATA) {
			msg.arg1 = mResult;
			msg.what = AppCmd.END_LOADING;
		} else if (mResult == DataSource.ERR_WAITING_SENSOR_DATA) {
			msg.arg1 = mResult;
			msg.what = AppCmd.END_LOADING;
		} else if (mResult == DataSource.ERR_NO_CHUNK_DATA) {	
			msg.arg1 = mResult;
			msg.what = AppCmd.END_LOADING;
		}
		
		mHandler.sendMessage(msg);
		// mProgDlg.dismiss();
	}

	@Override
	protected Void doInBackground(String[] params) {
		mResult = DataSource.loadRawData(params[0]);
		return null;
	}

	@Override
	protected void onCancelled() {
		DataSource.cancelLoading();
		super.onCancelled();
	}

}
