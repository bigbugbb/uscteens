package edu.neu.android.mhealth.uscteensver1.activities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import edu.neu.android.mhealth.uscteensver1.database.DatabaseHandler;
import edu.neu.android.mhealth.uscteensver1.database.RewardState;
import edu.neu.android.wocketslib.support.DataStorage;
import edu.neu.android.wocketslib.utils.BaseActivity;

public class RewardsStateActivity extends BaseActivity {

    protected final static String REWARDS_STATE_URL = "file:///android_asset/rewards_state/state.html";
    private WebView mWebView = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, "RewardsStateActivity");
        mWebView = new WebView(this);
        setContentView(mWebView);

        JavaScriptInterface jsInterface = new JavaScriptInterface(this);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(jsInterface, "JSInterface");
        mWebView.loadUrl(REWARDS_STATE_URL);
    }

    public class JavaScriptInterface {
        private Activity mActivity;
        private DatabaseHandler mDB;

        public JavaScriptInterface(Activity activiy) {
            this.mActivity = activiy;
            this.mDB = new DatabaseHandler(getApplicationContext());
        }

        public String getDate(int id) {
            String result;
            String startDate = DataStorage.getStartDate(getApplicationContext(), "");
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");

            try {
                Date start = fmt.parse(startDate);
                Calendar c = Calendar.getInstance();
                c.setTime(start);
                c.add(Calendar.DATE, id - 1);
                Date d = c.getTime();
                result = fmt.format(d);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                result = "unknown date";
            }

            return result;
        }

        public String getState(String date) {
            // Reading all contacts
            Log.d("Reading: ", "Reading all states..");
            List<RewardState> states = mDB.getAllRewardStates();

            String state = RewardState.LOCKED;
            for (RewardState s : states) {
                if (s.getDate().equals(date)) {
                    state = s.getState();
                    break;
                }
            }

            return state;
        }

        public void clearRecord() {
            mDB.deleteAllRewardState();
        }

        public void goBack() {
            mActivity.finish();
        }
    }
}
