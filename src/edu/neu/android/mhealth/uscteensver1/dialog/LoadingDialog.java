package edu.neu.android.mhealth.uscteensver1.dialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.KeyEvent;
import edu.neu.android.mhealth.uscteensver1.data.DataSource;

public class LoadingDialog extends ProgressDialog {

    public LoadingDialog(Context context) {
        super(context);
    }

    public LoadingDialog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            DataSource.cancelLoading();
            dismiss();
        }
        return super.onKeyDown(keyCode, event);
    }
}
