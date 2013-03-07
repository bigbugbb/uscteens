package edu.neu.android.mhealth.uscteensver1.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import edu.neu.android.mhealth.uscteensver1.R;
import edu.neu.android.mhealth.uscteensver1.data.RawActivity;
import edu.neu.android.mhealth.uscteensver1.data.DataSource;
import edu.neu.android.mhealth.uscteensver1.data.WeekdayCalculator;
import edu.neu.android.mhealth.uscteensver1.ui.ListView.ListItem;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.view.MotionEvent;

public class ListViewWeek extends ListView {

	static String[] sWeekdays = {
		"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"
	};
	
	public ListViewWeek(Resources res, int week, String startDate) {
		super(res);
		
		assert(week == 1 || week == 2);
	
		// get current date in String
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");	                           
		String curDate = sf.format(new Date());
							
		initializeItems(week, startDate, curDate);
	}
	
	private void initializeItems(int week, String startDate, String curDate) {						
		String[] split = startDate.split("-");
		Date date1 = new Date(Integer.parseInt(split[0]) - 1900, 
				Integer.parseInt(split[1]) - 1, Integer.parseInt(split[2]));	
		split = curDate.split("-");
		
		int intervalDay = 0;		
		while (intervalDay < 14) {
			String date = WeekdayCalculator.afterNDayFrom(date1, intervalDay);
			if (date.compareToIgnoreCase(curDate) == 0) {
				break;
			}
			intervalDay++;			
		}
		ArrayList<String> dates = new ArrayList<String>();
		for (int i = 0; i < 14; ++i) {
			String date = WeekdayCalculator.afterNDayFrom(date1, i);
			dates.add(date);
		}
		int startWeekday = WeekdayCalculator.getWeekdayInNumber(startDate) - 1; // 0 - 6
		
		if (week == 1) { // left list view
			for (int i = startWeekday; i < startWeekday + 7; ++i) {
				if (i <= startWeekday + intervalDay) {
					if (DataSource.getInstance(null).areAllChunksLabelled(dates.get(i - startWeekday))) {
						addItem(sWeekdays[i % 7], R.drawable.check_mark);
					} else {
						addItem(sWeekdays[i % 7], R.drawable.check_square);
					}
				} else {
					addItem(sWeekdays[i % 7], R.drawable.lock);
				}
			}
		} else { // right list view
			intervalDay -= 7;
			for (int i = startWeekday; i < startWeekday + 7; ++i) {
				if (i <= startWeekday + intervalDay) {
					if (DataSource.getInstance(null).areAllChunksLabelled(dates.get(i + 7 - startWeekday))) {
						addItem(sWeekdays[i % 7], R.drawable.check_mark);
					} else {
						addItem(sWeekdays[i % 7], R.drawable.check_square);
					}
				} else {
					addItem(sWeekdays[i % 7], R.drawable.lock);
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
			li.mWidth  = mItemWidth;
			li.mHeight = mItemHeight;
		}
	}
	
	@Override
	public void onSizeChanged(int width, int height) {
		mWidth  = width / 2;
		mHeight = height - sAppScale.doScaleH(128 + 100) - (2 * mBorderWidth + 1);
		mItemWidth  = (int) mWidth;
		mItemHeight = (int) (mHeight - 3 * mBorderWidth) / 4;
		
		for (int i = 0; i < mItems.size(); ++i) {
			ListItem li = mItems.get(i);
			li.mX = mX;
			li.mY = mY + (mItemHeight + mBorderWidth) * i;	
			li.mWidth  = mItemWidth;
			li.mHeight = mItemHeight;
		}
		
		BitmapFactory.Options options = new BitmapFactory.Options(); 
        options.inPurgeable = true;
        options.inPreferredConfig = Config.RGB_565; 
        mImages.add(Bitmap.createBitmap((int) mWidth, (int) mHeight, Bitmap.Config.RGB_565));
        mInnerCanvas = new Canvas(mImages.get(0));
	}

	
}
