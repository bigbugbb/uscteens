package edu.neu.android.mhealth.uscteensver1.ui;

import java.util.ArrayList;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.view.MotionEvent;
import edu.neu.android.mhealth.uscteensver1.R;
import edu.neu.android.mhealth.uscteensver1.TeensAppManager;
import edu.neu.android.mhealth.uscteensver1.data.Chunk;
import edu.neu.android.mhealth.uscteensver1.pages.AppScale;


public class ClockButton extends ChunkButton {

    protected static Paint sPaint = new Paint();
    protected static Paint sTimePaint = new Paint();
    protected static boolean sPaintCreated = false;

    protected static void createPaint() {
        if (sPaintCreated) {
            return;
        }
        sPaintCreated = true;

        sPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        sPaint.setColor(Color.argb(255, 156, 156, 156));
        sPaint.setStrokeWidth(AppScale.doScaleW(8.0f));
        sPaint.setStyle(Style.STROKE);
        sPaint.setFakeBoldText(true);

        sTimePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        sTimePaint.setColor(Color.BLACK);
        sTimePaint.setStyle(Style.STROKE);
        sTimePaint.setTypeface(Typeface.createFromAsset(TeensAppManager.getAppAssets(), "font/arial_bold.ttf"));
        sTimePaint.setTextSize(AppScale.doScaleT(36));
    }

    protected static boolean sImageLoaded = false;
    protected static ArrayList<Bitmap> sImages = new ArrayList<Bitmap>();

    static protected void loadImages(Resources res, int[] resIDs) {
        if (sImageLoaded) {
            return;
        }
        sImageLoaded = true;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPurgeable = true;
        options.inPreferredConfig = Config.RGB_565;
        for (int id : resIDs) {
            Bitmap origin = BitmapFactory.decodeResource(res, id, options);
            Bitmap scaled = null;
            // scale the image according to the current screen resolution
            float dstWidth = origin.getWidth(),
                    dstHeight = origin.getHeight();
            dstWidth = AppScale.doScaleW(dstWidth);
            dstHeight = AppScale.doScaleH(dstHeight);
            if (dstWidth != origin.getWidth() || dstHeight != origin.getHeight()) {
                scaled = Bitmap.createScaledBitmap(origin, (int) dstWidth, (int) dstHeight, true);
            }
            // add to the image list
            if (scaled != null) {
                origin.recycle(); // explicit call to avoid out of memory
                sImages.add(scaled);
            } else {
                sImages.add(origin);
            }
        }
    }

    public ClockButton(Resources res, Chunk host, OnClickListener listener) {
        super(res, host);
        loadImages(res, new int[]{R.drawable.clock});
        mWidth = sImages.get(0).getWidth();
        mHeight = sImages.get(0).getHeight();
        mID = UIID.CLOCK;
        mListener = listener;
        mVisible = false;

        createPaint();
    }

    @Override
    public void measureSize(int width, int height) {
        mCanvasWidth = width;
        mCanvasHeight = height;
        mY = height * 0.083f;
    }

    @Override
    public void onSizeChanged(int width, int height) {
        if (mCanvasWidth == width && mCanvasHeight == height) {
            return;
        }
        mCanvasWidth = width;
        mCanvasHeight = height;
        mY = height * 0.083f;
    }

    @Override
    public void onDraw(Canvas c) {
        if (mVisible) {
            c.drawBitmap(sImages.get(0), mX + mOffsetX, mY + mOffsetY, null);
            // choose to draw left or right based on the clock button position
            if (mX + mOffsetX < mCanvasWidth * 0.83f) {
                // draw on the right side
                if (mX + mWidth / 2 + mOffsetX > AppScale.doScaleW(-120)) {
                    sTimePaint.setTextAlign(Align.LEFT);
                    c.drawText(mHost.getChunkRealStartTimeInString(),
                            mX + mWidth / 2 + mOffsetX + AppScale.doScaleW(50),
                            mY + mHeight / 2 + AppScale.doScaleH(10), sTimePaint);
                }
            } else {
//				Chunk chunk = ChunkManager.getPrevChunk(mHost);
//				if (chunk.isSelected() && chunk.getChunkWidth() > Chunk.MINIMUM_CHUNK_SPACE * 1.75f) {
                // draw on the left side
                if (mX + mWidth / 2 + mOffsetX < mCanvasWidth + AppScale.doScaleW(120)) {
                    sTimePaint.setTextAlign(Align.RIGHT);
                    c.drawText(mHost.getChunkRealStartTimeInString(),
                            mX - mWidth / 2 + mOffsetX + AppScale.doScaleW(30),
                            mY + mHeight / 2 + AppScale.doScaleH(10), sTimePaint);
                }
//				} 
//				else {
//					// draw on the right side
//					if (mX + mWidth / 2 + mOffsetX > AppScale.doScaleW(-120)) {
//						sTimePaint.setTextAlign(Align.LEFT);
//						c.drawText(mHost.getChunkRealStartTimeInString(), 
//							mX + mWidth / 2 + mOffsetX + AppScale.doScaleW(50),
//							mY + mHeight / 2 + AppScale.doScaleH(10), sTimePaint);
//					}
//				}
            }
            //Log.d("clock button", mX + " " + mY + " " + mOffsetX);
            if (isSelected()) {
                c.drawCircle(mX + mWidth / 2 + mOffsetX,
                        mY + mHeight / 2 + mOffsetY, AppScale.doScaleH(47), sPaint);
            }
        }
    }

    @Override
    public boolean onDown(MotionEvent e) {
        if (mListener != null) {
            mListener.onClick(this);
        }
        return true;
    }

    @Override
    public boolean onUp(MotionEvent e) {
        return true;
    }


}
