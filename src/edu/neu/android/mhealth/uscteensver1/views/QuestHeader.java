package edu.neu.android.mhealth.uscteensver1.views;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import edu.neu.android.mhealth.uscteensver1.R;
import edu.neu.android.mhealth.uscteensver1.TeensAppManager;
import edu.neu.android.mhealth.uscteensver1.data.DataSource;
import edu.neu.android.mhealth.uscteensver1.pages.AppScale;
import edu.neu.android.wocketslib.utils.WeekdayHelper;

public class QuestHeader extends View {

    protected boolean mImageLoaded = false;
    protected ArrayList<Bitmap> mImages = new ArrayList<Bitmap>();
    
    protected String mDate;
    protected String mTime;
    protected Paint  mPaintDate;
    protected Paint  mPaintTime;
    
    protected int mWidth  = 0;
    protected int mHeight = 0;
    protected int mExpectedWidth  = 0;
    protected int mExpectedHeight = 0;

    public QuestHeader(Context context, AttributeSet attrs) {
        super(context, attrs);

        loadImages(new int[]{ R.drawable.popup_win_background });

        Typeface tf = Typeface.createFromAsset(TeensAppManager.getAppAssets(), "font/arial.ttf");

        mPaintDate = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintDate.setColor(Color.WHITE);
        mPaintDate.setStyle(Style.FILL);
        mPaintDate.setTextSize(AppScale.doScaleW(45));
        mPaintDate.setTypeface(Typeface.createFromAsset(TeensAppManager.getAppAssets(), "font/arial_bold.ttf"));
        mPaintDate.setTextAlign(Align.CENTER);

        mPaintTime = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintTime.setColor(Color.WHITE);
        mPaintTime.setStyle(Style.FILL);
        mPaintTime.setTypeface(tf);
        mPaintTime.setTextAlign(Align.CENTER);
        mPaintTime.setFakeBoldText(false);

        mTime = "";
        mDate = convertDateToDisplayFormat(DataSource.getCurrentSelectedDate());                
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(mImages.get(0), 0, 0, null); 
        canvas.drawText(mDate, getWidth() / 2, AppScale.doScaleH(65), mPaintDate);
        mPaintTime.setTextSize(AppScale.doScaleT(35));
        canvas.drawText(mTime, getWidth() / 2, AppScale.doScaleH(115), mPaintTime);
    }

    public void loadImages(int[] resIDs) {
        if (mImageLoaded) {
            return;
        }
        mImageLoaded = true;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPurgeable = true;
        options.inPreferredConfig = Config.RGB_565;
        for (int id : resIDs) {
            Bitmap origin = BitmapFactory.decodeResource(getContext().getResources(), id, options);
            Bitmap scaled = null;
            // scale the image according to the current screen resolution         
            float dstWidth  = Math.max(AppScale.doScaleW(origin.getWidth()), 400);
            float dstHeight = AppScale.doScaleH(origin.getHeight());
            if (dstWidth != origin.getWidth() || dstHeight != origin.getHeight()) {
                scaled = Bitmap.createScaledBitmap(origin, (int) dstWidth, (int) dstHeight, true);
            }
            // add to the image list
            if (scaled != null) {
                origin.recycle(); // explicit call to avoid out of memory
                mImages.add(scaled);
            } else {
                mImages.add(origin);
            }
        }

        mExpectedWidth  = mImages.get(0).getWidth();
        mExpectedHeight = mImages.get(0).getHeight();
    }

    public void setTime(int start, int stop) {
        String s[]  = { "", "" };
        int hour    = 0;
        int minute  = 0;
        int times[] = {start, stop};
        
        for (int i = 0; i < 2; ++i) {
            hour = times[i] / 3600;
            minute = (times[i] - hour * 3600) / 60;
            s[i] = (hour > 12 ? hour - 12 : hour == 0 ? 12 : hour) + ":" +
                    (minute > 9 ? minute : "0" + minute);
            s[i] += (hour > 11) ? " PM" : " AM";
        }
        mTime = s[0] + " - " + s[1];
    }

    public int getExpectedWidth() {
        return mExpectedWidth;
    }

    public int getExpectedHeight() {
        return mExpectedHeight;
    }

    private String convertDateToDisplayFormat(String date) {
        String[] months = {
            "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JULY", "AUG", "SEPT", "OCT", "NOV", "DEC"
        };
        String[] times = date.split("-");
        String weekday = WeekdayHelper.getWeekday(date);
        String month = months[Integer.parseInt(times[1]) - 1];
        String day = times[2];

        return " " + weekday.toUpperCase() + "  " + month + "  " + day;
    }
}
