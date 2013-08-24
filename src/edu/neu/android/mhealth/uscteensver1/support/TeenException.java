package edu.neu.android.mhealth.uscteensver1.support;

import edu.neu.android.wocketslib.utils.Log;

public class TeenException extends Exception {
    private static final long serialVersionUID = 1L;
    private static final String TAG = "TeenException";

    public TeenException() {
        super();
        Log.e(TAG, "TeenException without descriptor");
    }

    public TeenException(String msg) {
        super(msg);
        Log.e(TAG, "TeenException: " + msg);
    }

    public TeenException(String msg, Throwable e) {
        super(msg, e);
        Log.e(TAG, "TeenException: " + msg + " " + e.toString());
    }
}