package edu.neu.android.mhealth.uscteensver1.support;

import edu.neu.android.wocketslib.utils.Log;

public class TeenException extends Exception {
	private static final long serialVersionUID = 1L;
	private static final String TAG = "CITYException";

	public TeenException() {
		super();
		Log.e(TAG, "CITYException without descriptor");
	}

	public TeenException(String msg) {
		super(msg);
		Log.e(TAG, "CITYException: " + msg);
	}

	public TeenException(String msg, Throwable ex) {
		super(msg, ex);
		Log.e(TAG, "CITYException: " + msg + " " + ex.toString());
	}
}