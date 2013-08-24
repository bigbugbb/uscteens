package edu.neu.android.mhealth.uscteensver1.ui;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import edu.neu.android.mhealth.uscteensver1.R;
import edu.neu.android.mhealth.uscteensver1.TeensGlobals;
import edu.neu.android.mhealth.uscteensver1.data.DataSource;
import edu.neu.android.mhealth.uscteensver1.pages.AppScale;
import edu.neu.android.wocketslib.utils.DateHelper;
import edu.neu.android.wocketslib.utils.WeekdayHelper;


public class DateListView extends ListView {

    static String[] sWeekdays = {
            "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"
    };

    public DateListView(Resources res, int week, String startDate) {
        super(res);

        assert (week == 1 || week == 2);

        String curDate = DateHelper.serverDateFormat.format(new Date());

        initializeItems(week, startDate, curDate);
    }

    private void initializeItems(int week, String startDate, String curDate) {
        // get Date objects for start date and current date
        Date aStartDate = null, aCurDate = null;
        try {
            aStartDate = DateHelper.serverDateFormat.parse(startDate);
            aCurDate = DateHelper.serverDateFormat.parse(curDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // if the start date is in the future
        if (aStartDate.compareTo(aCurDate) > 0) {
            for (int i = 0; i < 7; ++i) {
                addItem(sWeekdays[i], R.drawable.lock);
            }
            return;
        }

        // start date is less than or equal to the current date
        int daysAfterStarting = 0;
        while (daysAfterStarting < 14) {
            String date = WeekdayHelper.afterNDayFrom(aStartDate, daysAfterStarting);
            if (date.compareToIgnoreCase(curDate) == 0) {
                break;
            }
            daysAfterStarting++;
        }
        // put all date to the list as Strings YYYY-MM-dd
        ArrayList<String> dates = new ArrayList<String>();
        for (int i = 0; i < 14; ++i) {
            String date = WeekdayHelper.afterNDayFrom(aStartDate, i);
            dates.add(date);
        }
        // fill each item
        int startWeekday = WeekdayHelper.getWeekdayInNumber(startDate) - 1; // 0 - 6
        if (week == 1) { // left list view
            for (int i = startWeekday; i < startWeekday + 7; ++i) {
                if (i <= startWeekday + daysAfterStarting) {
                    if (i <= startWeekday + daysAfterStarting - TeensGlobals.MAX_LABEL_WINDOW) {
                        addItem(sWeekdays[i % 7], R.drawable.not_available);
                    } else {
                        if (DataSource.areAllChunksLabelled(dates.get(i - startWeekday))) {
                            addItem(sWeekdays[i % 7], R.drawable.check_mark);
                        } else {
                            addItem(sWeekdays[i % 7], R.drawable.check_square);
                        }
                    }
                } else {
                    addItem(sWeekdays[i % 7], R.drawable.lock);
                }
            }
        } else { // right list view
            daysAfterStarting -= 7;
            for (int i = startWeekday; i < startWeekday + 7; ++i) {
                if (i <= startWeekday + daysAfterStarting) {
                    if (i <= startWeekday + daysAfterStarting - TeensGlobals.MAX_LABEL_WINDOW) {
                        addItem(sWeekdays[i % 7], R.drawable.not_available);
                    } else {
                        if (DataSource.areAllChunksLabelled(dates.get(i + 7 - startWeekday))) {
                            addItem(sWeekdays[i % 7], R.drawable.check_mark);
                        } else {
                            addItem(sWeekdays[i % 7], R.drawable.check_square);
                        }
                    }
                } else {
                    addItem(sWeekdays[i % 7], R.drawable.lock);
                }
            }
        }
    }

    public void refresh(int week, String startDate, String curDate) {
        String[] dateSplit = null;

        // get Date objects for start date and current date
        dateSplit = startDate.split("-");
        Date aStartDate = DateHelper.getDate(
                Integer.parseInt(dateSplit[0]), Integer.parseInt(dateSplit[1]), Integer.parseInt(dateSplit[2]));
        dateSplit = curDate.split("-");
        Date aCurDate = DateHelper.getDate(
                Integer.parseInt(dateSplit[0]), Integer.parseInt(dateSplit[1]), Integer.parseInt(dateSplit[2]));

        // if the start date is in the future
        if (aStartDate.compareTo(aCurDate) > 0) {
            for (int i = 0; i < 7; ++i) {
                getItem(i).setItemImage(R.drawable.lock);
            }
            return;
        }

        // start date is less than or equal to the current date
        int daysAfterStarting = 0;
        while (daysAfterStarting < 14) {
            String date = WeekdayHelper.afterNDayFrom(aStartDate, daysAfterStarting);
            if (date.compareToIgnoreCase(curDate) == 0) {
                break;
            }
            daysAfterStarting++;
        }
        // put all date to the list as Strings YYYY-MM-dd
        ArrayList<String> dates = new ArrayList<String>();
        for (int i = 0; i < 14; ++i) {
            String date = WeekdayHelper.afterNDayFrom(aStartDate, i);
            dates.add(date);
        }
        // fill each item
        int startWeekday = WeekdayHelper.getWeekdayInNumber(startDate) - 1; // 0 - 6
        if (week == 1) { // left list view
            for (int i = startWeekday; i < startWeekday + 7; ++i) {
                if (i <= startWeekday + daysAfterStarting) {
                    if (i <= startWeekday + daysAfterStarting - TeensGlobals.MAX_LABEL_WINDOW) {
                        getItem(i - startWeekday).setItemImage(R.drawable.not_available);
                    } else {
                        if (DataSource.areAllChunksLabelled(dates.get(i - startWeekday))) {
                            getItem(i - startWeekday).setItemImage(R.drawable.check_mark);
                        } else {
                            getItem(i - startWeekday).setItemImage(R.drawable.check_square);
                        }
                    }
                } else {
                    getItem(i - startWeekday).setItemImage(R.drawable.lock);
                }
            }
        } else { // right list view
            daysAfterStarting -= 7;
            startWeekday += 7;
            for (int i = startWeekday; i < startWeekday + 7; ++i) {
                if (i <= startWeekday + daysAfterStarting) {
                    if (i <= startWeekday + daysAfterStarting - TeensGlobals.MAX_LABEL_WINDOW) {
                        getItem(i - startWeekday).setItemImage(R.drawable.not_available);
                    } else {
                        if (DataSource.areAllChunksLabelled(dates.get(i + 7 - startWeekday))) {
                            getItem(i - startWeekday).setItemImage(R.drawable.check_mark);
                        } else {
                            getItem(i - startWeekday).setItemImage(R.drawable.check_square);
                        }
                    }
                } else {
                    getItem(i - startWeekday).setItemImage(R.drawable.lock);
                }
            }
        }

        // scroll to the current day if possible
        if (daysAfterStarting > 0) {
            daysAfterStarting = daysAfterStarting > 3 ? 3 : daysAfterStarting;

            mOffsetY += -(mItemHeight + mBorderWidth) * daysAfterStarting;
            if (mOffsetY > 0) {
                mOffsetY = (int) Math.min(mOffsetY, mItemHeight * 1.3f);
                if (mOnBoundaryListener != null) {
                    mOnBoundaryListener.onBoundary(this, true, false);
                }
            } else if (mOffsetY + (mItemHeight + mBorderWidth) * mItems.size() - mBorderWidth <= mHeight) {
                mOffsetY = (int) Math.max(mOffsetY,
                        -(mItemHeight + mBorderWidth) * (mItems.size() - 4) - mItemHeight * 1.3f);
                if (mOnBoundaryListener != null) {
                    mOnBoundaryListener.onBoundary(this, false, false);
                }
            }
        }
    }

    public void setPosn(float x, float y) {
        mX = x;
        mY = y;

        for (int i = 0; i < mItems.size(); ++i) {
            ListItem li = mItems.get(i);
            li.mX = mX;
            li.mY = mY + (mItemHeight + mBorderWidth) * i;
            li.mWidth = mItemWidth;
            li.mHeight = mItemHeight;
        }
    }

    @Override
    public void onSizeChanged(int width, int height) {
        if (mCanvasWidth == width && mCanvasHeight == height) {
            return;
        }
        mCanvasWidth = width;
        mCanvasHeight = height;

        mWidth = width / 2;
        mHeight = height - AppScale.doScaleH(128 + 100) - (2 * mBorderWidth + 1);
        mItemWidth = (int) mWidth;
        mItemHeight = (int) (mHeight - 3 * mBorderWidth) / 4;

        for (int i = 0; i < mItems.size(); ++i) {
            ListItem li = mItems.get(i);
            li.mX = mX;
            li.mY = mY + (mItemHeight + mBorderWidth) * i;
            li.mWidth = mItemWidth;
            li.mHeight = mItemHeight;
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPurgeable = true;
        options.inPreferredConfig = Config.RGB_565;
        mImages.add(Bitmap.createBitmap((int) mWidth, (int) mHeight, Bitmap.Config.RGB_565));
        mInnerCanvas = new Canvas(mImages.get(0));
    }

}
