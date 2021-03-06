package edu.neu.android.mhealth.uscteensver1.activities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import edu.neu.android.mhealth.uscteensver1.R;
import edu.neu.android.mhealth.uscteensver1.TeensAppManager;
import edu.neu.android.mhealth.uscteensver1.TeensGlobals;
import edu.neu.android.wocketslib.support.DataStorage;
import edu.neu.android.wocketslib.utils.BaseActivity;
import edu.neu.android.wocketslib.utils.DateHelper;

public class StartDateSetupActivity extends BaseActivity implements OnClickListener {

    private static final String TAG = "StartDateSetupActivity";
    private static final int TOTAL_DAYS = 14;
    // buttons
    private Button   mBtnSet;
    private Button   mBtnCancel;
    // date selection spinner
    private Spinner  mSpinnerYears;
    private Spinner  mSpinnerMonths;
    private Spinner  mSpinnerDays;
    private Spinner  mSpinnerWindows;
    // years, months and days
    private String[] mYears  = {"2013", "2014", "2015", "2016", "2017", "2018", "2019", "2020", "2021", "2022", "2023"};
    private String[] mMonths = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
    private String[] mDays   = {};
    private String[] mLabelWindows = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14"};
    // adapter for days
    ArrayAdapter<String> mAdapterDays = null;
    // today
    private String[] mDateSplit = null;
    // start date
    private String mStartDate = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, TAG);
        setContentView(R.layout.activity_startdate);

        loadPreferences();
        // decide the date choice
        if (mStartDate.equals("")) { // haven't set the start date yet
            mStartDate = DateHelper.getServerDateString(Calendar.getInstance().getTime());
        }
        mDateSplit = mStartDate.split("-");
        mDays = generateMonthDays(mDateSplit[0], mDateSplit[1]);

        // get views and set listeners
        setupViews();
    }

    @Override
    public void onResume() {

        for (int i = 0; i < mYears.length; ++i) {
            String year = mYears[i];
            if (year.compareTo(mDateSplit[0]) == 0) {
                mSpinnerYears.setSelection(i);
            }
        }
        for (int i = 0; i < mMonths.length; ++i) {
            String month = mMonths[i];
            if (month.compareTo(mDateSplit[1]) == 0) {
                mSpinnerMonths.setSelection(i);
            }
        }
        for (int i = 0; i < mDays.length; ++i) {
            String day = mDays[i];
            if (day.compareTo(mDateSplit[2]) == 0) {
                mSpinnerDays.setSelection(i);
            }
        }
        for (int i = 0; i < mLabelWindows.length; ++i) {
            String window = mLabelWindows[i];
            if (window.compareTo(TeensGlobals.MAX_LABEL_WINDOW + "") == 0) {
                mSpinnerWindows.setSelection(i);
            }
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void loadPreferences() {
        mStartDate = DataStorage.getStartDate(getApplicationContext(), "");
    }

    private void savePreferences() {
        for (int i = 0; i < TOTAL_DAYS; ++i) {
            DataStorage.SetValue(getApplicationContext(), TeensGlobals.LAST_SELECTED_CHUNK + i, 0);
            DataStorage.SetValue(getApplicationContext(), TeensGlobals.LAST_DISPLAY_OFFSET_X + i, 0);
        }
        DataStorage.SetValue(getApplicationContext(), DataStorage.KEY_START_DATE, mStartDate);
    }

    private void setupViews() {
        // get buttons
        mBtnSet    = (Button) findViewById(R.id.setstartdate_set);
        mBtnCancel = (Button) findViewById(R.id.setstartdate_cancel);
        mBtnSet.setOnClickListener(this);
        mBtnCancel.setOnClickListener(this);
        // get spinners
        mSpinnerYears   = (Spinner) findViewById(R.id.spinner_years);
        mSpinnerMonths  = (Spinner) findViewById(R.id.spinner_months);
        mSpinnerDays    = (Spinner) findViewById(R.id.spinner_days);
        mSpinnerWindows = (Spinner) findViewById(R.id.spinner_labeling_windows);
        // set years
        ArrayAdapter<String> mAdapterYears =
                new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mYears);
        mAdapterYears.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerYears.setAdapter(mAdapterYears);
        mSpinnerYears.setOnItemSelectedListener(new SpinnerSelectedListener());
        mSpinnerYears.setVisibility(View.VISIBLE);
        // set months
        ArrayAdapter<String> mAdapterMonths =
                new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mMonths);
        mAdapterMonths.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerMonths.setAdapter(mAdapterMonths);
        mSpinnerMonths.setOnItemSelectedListener(new SpinnerSelectedListener());
        mSpinnerMonths.setVisibility(View.VISIBLE);
        // set days
        mAdapterDays = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mDays);
        mAdapterDays.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerDays.setAdapter(mAdapterDays);
        mSpinnerDays.setOnItemSelectedListener(new SpinnerSelectedListener());
        // set labeling windows
        ArrayAdapter<String> mAdapterWindows =
                new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mLabelWindows);
        mAdapterWindows.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerWindows.setAdapter(mAdapterWindows);
        mSpinnerWindows.setOnItemSelectedListener(new SpinnerSelectedListener());
        //mSpinnerDays.setEnabled(false);
        mSpinnerDays.setVisibility(View.VISIBLE);
    }

    private String[] generateMonthDays(String year, String month) {
        ArrayList<String> monthDays = new ArrayList<String>();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, Integer.parseInt(year));
        cal.set(Calendar.MONTH, Integer.parseInt(month) - 1); // java starts from 0
        int dayOfMonth = cal.getActualMaximum(Calendar.DATE);

        for (int i = 1; i <= dayOfMonth; ++i) {
            monthDays.add(i < 10 ? "0" + i : "" + i);
        }

        String[] days = new String[monthDays.size()];
        monthDays.toArray(days);

        return days;
    }

    private void resetSpinnerDays() {
        mAdapterDays = new ArrayAdapter<String>(StartDateSetupActivity.this, android.R.layout.simple_spinner_item, mDays);
        mAdapterDays.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerDays.setAdapter(mAdapterDays);
        for (int i = 0; i < mDays.length; ++i) {
            String day = mDays[i];
            if (day.compareTo(mDateSplit[2]) == 0) {
                mSpinnerDays.setSelection(i);
            }
        }
    }

    public static Date convertDate(String dateString, String format) {
        Date date = null;

        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
            date = simpleDateFormat.parse(dateString);
        } catch (Exception ex) {
            // nothing now
        }

        return date;
    }

    private void displayToastMessage(String msg) {
        Toast aToast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
        aToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        aToast.show();
    }

    class SpinnerSelectedListener implements OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

            if (parent == mSpinnerYears) {
                mDateSplit[0] = (String) parent.getSelectedItem();
                mDays = generateMonthDays(mDateSplit[0], mDateSplit[1]);
                resetSpinnerDays();
            } else if (parent == mSpinnerMonths) {
                mDateSplit[1] = (String) parent.getSelectedItem();
                mDays = generateMonthDays(mDateSplit[0], mDateSplit[1]);
                resetSpinnerDays();
            } else if (parent == mSpinnerDays) {
                mDateSplit[2] = (String) parent.getSelectedItem();
            } else if (parent == mSpinnerWindows) {
                String labelWindow = (String) parent.getSelectedItem();
                TeensGlobals.MAX_LABEL_WINDOW = Integer.parseInt(labelWindow);
                DataStorage.SetValue(TeensAppManager.getAppContext(), "KEY_LABEL_WINDOW", TeensGlobals.MAX_LABEL_WINDOW);
            }

            mStartDate = mDateSplit[0] + "-" + mDateSplit[1] + "-" + mDateSplit[2];
        }

        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    @Override
    public void onClick(View v) {
        if (v == mBtnSet) {
            // check whether the start date is nearer than the current date
            Date aStartDate = convertDate(mStartDate, "yyyy-MM-dd");
            Date aCloneDate = (Date) aStartDate.clone();
            Date aCurDate = Calendar.getInstance().getTime();

            aCloneDate.setYear(aCurDate.getYear());
            aCloneDate.setMonth(aCurDate.getMonth());
            aCloneDate.setDate(aCurDate.getDate());
            aCurDate = aCloneDate;

            if (aStartDate.compareTo(aCurDate) < 0) {
                displayToastMessage("The start date is less than the current date!");
            }
            // save start date
            savePreferences();
        } else if (v == mBtnCancel) {
            // do nothing
        }

        finish();
    }

}
