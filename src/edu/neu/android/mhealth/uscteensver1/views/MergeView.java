package edu.neu.android.mhealth.uscteensver1.views;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import edu.neu.android.mhealth.uscteensver1.R;
import edu.neu.android.mhealth.uscteensver1.pages.AppScale;


public class MergeView extends View {
	
	protected Resources mRes = null;
	protected boolean mImageLoaded = false;		
	protected List<Bitmap> mImages = new ArrayList<Bitmap>();
	protected List<String> mActions = null;
	protected Paint mPaintText0 = null;
	protected Paint mPaintText1 = null;
	protected Paint mPaintText2 = null;	
	protected List<RectF> mAreas = new ArrayList<RectF>();
	protected int mSelection = 0;
	protected OnItemClickListener   mItemListener = null;
	protected OnBackClickedListener mBackListener = null;
	protected RectF  mBackArea  = new RectF();
	protected PointF mBackTxtPt = new PointF();
	
	
	public MergeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mRes = context.getResources();
		loadImages(new int[]{ R.drawable.warning_back, R.drawable.arrow_warning, 
				R.drawable.selection, R.drawable.selection_circle });	
			
		mPaintText0 = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaintText0.setColor(Color.WHITE);
		mPaintText0.setStyle(Style.STROKE);
		mPaintText0.setTextSize(AppScale.doScaleT(34));
		mPaintText0.setTypeface(Typeface.SERIF);
		mPaintText0.setFakeBoldText(false);
		
		mPaintText1 = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaintText1.setColor(Color.BLACK);
		mPaintText1.setStyle(Style.STROKE);
		mPaintText1.setTypeface(Typeface.SERIF);
		mPaintText1.setFakeBoldText(false);
		mPaintText1.setTextSize(AppScale.doScaleT(46));
		mPaintText1.setTextAlign(Paint.Align.CENTER);
		
		mPaintText2 = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaintText2.setColor(Color.BLACK);
		mPaintText2.setStyle(Style.STROKE);
		mPaintText2.setTypeface(Typeface.SERIF);
		mPaintText2.setFakeBoldText(false);
		mPaintText2.setTextSize(AppScale.doScaleT(40));
		mPaintText2.setTextAlign(Paint.Align.LEFT);
		
		mBackArea.left   = AppScale.doScaleW(10);
		mBackArea.right  = mBackArea.left + mImages.get(1).getWidth();
		mBackArea.top    = AppScale.doScaleH(5);
		mBackArea.bottom = mBackArea.top + mImages.get(1).getHeight();
		
		mBackTxtPt.x = mBackArea.left + AppScale.doScaleW(33);
		mBackTxtPt.y = mBackArea.bottom - AppScale.doScaleH(52);
	}
	
	public void setActions(ArrayList<String> actions) {
		mActions = actions;		
	}
	
	public void setOnBackClickedListener(OnBackClickedListener listener) {
		mBackListener = listener;
	}
	
	public void setOnItemClickListener(OnItemClickListener listener) {
		mItemListener = listener;
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
        	Bitmap origin = BitmapFactory.decodeResource(mRes, id, options);
        	Bitmap scaled = null;
        	// scale the image according to the current screen resolution
        	float dstWidth  = origin.getWidth(),
        	      dstHeight = origin.getHeight();        	        	
    		dstWidth  = AppScale.doScaleW(dstWidth);
    		dstHeight = AppScale.doScaleH(dstHeight);
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
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawBitmap(mImages.get(0), 0, 0, null);
		canvas.drawBitmap(mImages.get(1), mBackArea.left, mBackArea.top, null);
		canvas.drawText("BACK", mBackTxtPt.x, mBackTxtPt.y, mPaintText0);
		canvas.drawText("Do you want to merge", getWidth() / 2, AppScale.doScaleH(220), mPaintText1);
		canvas.drawText("this activities to:", getWidth() / 2, AppScale.doScaleH(310), mPaintText1);
		
		float left = getWidth() * 0.18f;
		for (int i = 0; i < mAreas.size(); ++i) {
			canvas.drawBitmap(mImages.get(2 + (mSelection == i ? 1 : 0)), left, 
				(AppScale.doScaleH(100) - mImages.get(2).getHeight()) / 2 + mAreas.get(i).top, mPaintText1);
			canvas.drawText(mActions.get(i), left + AppScale.doScaleH(100), 
				mAreas.get(i).top + AppScale.doScaleH(64), mPaintText2);			
		}				
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		for (int i = 0; i < mActions.size(); ++i) {
			int top = h / 2;
			mAreas.add(new RectF(0, top + i * AppScale.doScaleH(100), 
				w, top + (i + 1) * AppScale.doScaleH(100)));
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean ret = false;
		
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			for (int i = 0; i < mAreas.size(); ++i) {
				if (mAreas.get(i).contains(event.getX(), event.getY())) {
					mSelection = i;	
					if (mItemListener != null) {
						mItemListener.onItemClick(this, mSelection);
					}
					ret = true;				
					break;
				}
			}
			
			if (mBackArea.contains(event.getX(), event.getY())) {
				if (mBackListener != null) {
					mBackListener.onBackClicked();
				}
			}
			
			if (ret == true) {
				invalidate();
			}
		} 
//		else if (event.getAction() == MotionEvent.ACTION_UP) {
//			if (mBackArea.contains(event.getX(), event.getY())) {
//				mBackArea.offset(-2, -2);
//				mBackTxtPt.offset(-2, -2);
//				if (mBackListener != null) {
//					mBackListener.onBackClickedListener();
//				}
//			}
//		}
		
		return ret;
	}
	
	public interface OnBackClickedListener {
		void onBackClicked();
	}
	
	public interface OnItemClickListener {
		void onItemClick(View v, int pos);
	}
}
