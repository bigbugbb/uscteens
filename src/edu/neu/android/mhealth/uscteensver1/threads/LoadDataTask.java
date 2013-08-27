package edu.neu.android.mhealth.uscteensver1.threads;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import edu.neu.android.mhealth.uscteensver1.TeensGlobals;
import edu.neu.android.mhealth.uscteensver1.data.DataSource;
import edu.neu.android.mhealth.uscteensver1.dialog.LoadingDialog;
import edu.neu.android.mhealth.uscteensver1.pages.AppCmd;
import edu.neu.android.wocketslib.support.DataStorage;


public class LoadDataTask extends AsyncTask<String, Void, Void> {
    private Context mContext;
    private int mResult;
    private Handler mHandler;
    private LoadingDialog mLoadingDialog = null;
    private static final Object sLock = new Object();

    public LoadDataTask(Context context, Handler handler) {
        mContext = context;
        mResult  = DataSource.LOADING_SUCCEEDED;
        mHandler = handler;

        mLoadingDialog = new LoadingDialog(mContext);
        mLoadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mLoadingDialog.setTitle("Loading your motion data");
//		mLoadingDialog.setIcon(R.drawable.icon);
        mLoadingDialog.setMessage("It may take a few seconds to load your data.\nJust a moment...");
        mLoadingDialog.setIndeterminate(false);
        mLoadingDialog.setCancelable(false);
        mLoadingDialog.setButton("Never mind", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DataSource.cancelLoading();
                dialog.cancel();
            }
        });
    }

    @Override
    protected void onPreExecute() {
        mLoadingDialog.show();
        super.onPreExecute();

    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

        Message msg = mHandler.obtainMessage();

        if (mResult == DataSource.LOADING_SUCCEEDED) {
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

        DataStorage.SetValue(mContext, TeensGlobals.DATA_LOADING_RESULT, mResult);

        mHandler.sendMessage(msg);
        mLoadingDialog.dismiss();
    }

    @Override
    protected Void doInBackground(String[] params) {
        android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND +
                Process.THREAD_PRIORITY_MORE_FAVORABLE);
        synchronized (sLock) {
            mResult = DataSource.loadRawData(params[0]);
        }
        return null;
    }
}
