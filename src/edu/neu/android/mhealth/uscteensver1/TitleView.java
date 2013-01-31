package edu.neu.android.mhealth.uscteensver1;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

public class TitleView extends ImageView {
	
	protected boolean mImageLoaded = false;
	protected ArrayList<Bitmap> mImages = new ArrayList<Bitmap>();
	
	protected int mWidthArrow  = 0;
	protected int mHeightArrow = 0;
	protected RectF  mBackArea  = new RectF();
	protected PointF mBackTxtPt = new PointF();
	protected Paint mPaintText = null;	
	
	protected OnBackClickedListener mListener = null;
	
	public void setOnBackClickedListener(OnBackClickedListener listener) {
		mListener = listener;
	}

	public TitleView(Context context, AttributeSet attrs) {
		super(context, attrs);	
		
		mPaintText = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaintText.setColor(Color.WHITE);
		mPaintText.setStyle(Style.STROKE);
		mPaintText.setTextSize(34);
		mPaintText.setTypeface(Typeface.SERIF);
		mPaintText.setFakeBoldText(false);				
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
        	mImages.add(BitmapFactory.decodeResource(
        		getContext().getResources(), id, options)
        	);
        }
        
        mWidthArrow  = mImages.get(1).getWidth();
		mHeightArrow = mImages.get(1).getHeight();
		
		mBackArea.left   = 10;
		mBackArea.right  = mBackArea.left + mImages.get(1).getWidth();
		mBackArea.top    = 5;
		mBackArea.bottom = mBackArea.top + mImages.get(1).getHeight();
				
		mBackTxtPt.x = mBackArea.left + 33;
		mBackTxtPt.y = mBackArea.bottom - 52;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		//super.onDraw(canvas);
		canvas.drawBitmap(mImages.get(0), 0, 0, null);
		canvas.drawBitmap(mImages.get(1), mBackArea.left, mBackArea.top, null);
		canvas.drawText("BACK", mBackTxtPt.x, mBackTxtPt.y, mPaintText);
	}

	public interface OnBackClickedListener {
		void OnBackClicked();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if (mBackArea.contains(event.getX(), event.getY())) {
				if (mListener != null) {
					mListener.OnBackClicked();
				}
			}
		}
		return super.onTouchEvent(event);
	}
}
