package edu.neu.android.mhealth.uscteensver1;

import java.util.ArrayList;
import java.util.List;

import edu.neu.android.mhealth.uscteensver1.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


public class WarningView extends View {
	
	protected Resources mRes = null;
	protected boolean mImageLoaded = false;	
	protected List<Bitmap> mImages = new ArrayList<Bitmap>();
	protected List<String> mActions = null;
	protected Paint mPaintText1 = null;
	protected Paint mPaintText2 = null;
	protected List<RectF> mAreas = new ArrayList<RectF>();
	protected int mSelection = 0;
	protected OnItemClickListener mListener = null;
	
	public WarningView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mRes = context.getResources();
		loadImages(new int[]{ R.drawable.warning_back, R.drawable.arrow_warning, 
				R.drawable.selection, R.drawable.selection_circle });		
		
		mPaintText1 = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaintText1.setColor(Color.BLACK);
		mPaintText1.setStyle(Style.STROKE);
		mPaintText1.setTypeface(Typeface.SERIF);
		mPaintText1.setFakeBoldText(false);
		mPaintText1.setTextSize(46);
		mPaintText1.setTextAlign(Paint.Align.CENTER);
		
		mPaintText2 = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaintText2.setColor(Color.BLACK);
		mPaintText2.setStyle(Style.STROKE);
		mPaintText2.setTypeface(Typeface.SERIF);
		mPaintText2.setFakeBoldText(false);
		mPaintText2.setTextSize(40);
		mPaintText2.setTextAlign(Paint.Align.LEFT);
	}
	
	protected void setActions(ArrayList<String> actions) {
		mActions = actions;		
	}
	
	protected void setOnItemClickListener(OnItemClickListener listener) {
		mListener = listener;
	}

	protected void loadImages(int[] resIDs) {
		if (mImageLoaded) {
			return;
		}
		mImageLoaded = true;
		
		BitmapFactory.Options options = new BitmapFactory.Options(); 
        options.inPurgeable = true;
        options.inPreferredConfig = Config.RGB_565; 
        for (int id : resIDs) {
        	mImages.add(BitmapFactory.decodeResource(mRes, id, options));
        }
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawBitmap(mImages.get(0), 0, 0, null);
		canvas.drawText("Do you want to merge", getWidth() / 2, 220, mPaintText1);
		canvas.drawText("this activities to:", getWidth() / 2, 310, mPaintText1);
		
		float left = getWidth() * 0.18f;
		for (int i = 0; i < mAreas.size(); ++i) {
			canvas.drawBitmap(mImages.get(2 + (mSelection == i ? 1 : 0)), 
				left, (100 - mImages.get(2).getHeight()) / 2 + mAreas.get(i).top, mPaintText1);
			canvas.drawText(mActions.get(i), 
				left + 100, mAreas.get(i).top + 64, mPaintText2);			
		}				
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		for (int i = 0; i < mActions.size(); ++i) {
			int top = h / 2;
			mAreas.add(new RectF(0, top + i * 100, w, top + (i + 1) * 100));
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean ret = false;
		
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			for (int i = 0; i < mAreas.size(); ++i) {
				if (mAreas.get(i).contains(event.getX(), event.getY())) {
					mSelection = i;	
					if (mListener != null) {
						mListener.onItemClick(this, mSelection);
					}
					ret = true;				
					break;
				}
			}
			
			if (ret == true) {
				invalidate();
			}
		}
		
		return ret;
	}
	
	public interface OnItemClickListener {
		void onItemClick(View v, int pos);
	}
}
