package edu.neu.android.mhealth.uscteensver1.views;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import edu.neu.android.mhealth.uscteensver1.R;
import edu.neu.android.mhealth.uscteensver1.pages.AppScale;

public class MergeHeader extends View {
	
	protected int mExpectedWidth  = 0;
    protected int mExpectedHeight = 0;
    
	protected boolean mImageLoaded = false;
	protected ArrayList<Bitmap> mImages = new ArrayList<Bitmap>();      

	public MergeHeader(Context context, AttributeSet attrs) {
		super(context, attrs);

		loadImages(new int[]{ R.drawable.warning_back });
	}
	
	public int getExpectedWidth() {
        return mExpectedWidth;
    }

    public int getExpectedHeight() {
        return mExpectedHeight;
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
            float dstWidth  = AppScale.doScaleW(origin.getWidth());
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
	
	@Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(mImages.get(0), 0, 0, null); 
    }
}
