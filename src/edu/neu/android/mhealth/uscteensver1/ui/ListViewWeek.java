package edu.neu.android.mhealth.uscteensver1.ui;

import java.util.ArrayList;

import edu.neu.android.mhealth.uscteensver1.R;
import edu.neu.android.mhealth.uscteensver1.data.ActivityData;
import edu.neu.android.mhealth.uscteensver1.data.DataSource;
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
	
	public ListViewWeek(Resources res, int week) {
		super(res);
		
		ArrayList<ActivityData> actList = DataSource.getInstance(null).getActList(week);
		int[] weekday = new int[7];
		for (int i = 0; i < 7; ++i) {
			weekday[i] = 0;
		}
		for (ActivityData date : actList) {
			for (int i = 0; i < 7; ++i) {
				if (date.getWeekday().compareToIgnoreCase(sWeekdays[i]) == 0) {
					weekday[i] = 1;
					break;
				}				
			}
		}
		
		for (int i = 0; i < 7; ++i) {			
			if (weekday[i] != 0) {
				addItem(sWeekdays[i], R.drawable.check_square);
			} else {
				addItem(sWeekdays[i], R.drawable.lock);
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
