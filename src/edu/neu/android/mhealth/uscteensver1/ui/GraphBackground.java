package edu.neu.android.mhealth.uscteensver1.ui;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import edu.neu.android.mhealth.uscteensver1.R;

public class GraphBackground extends Background {

    protected int mCanvasWidth = 0;
    protected int mCanvasHeight = 0;

    public GraphBackground(Resources res) {
        super(res);
        loadImages(new int[]{R.drawable.menubar_background});
    }

    @Override
    public void onSizeChanged(int width, int height) {
        if (mCanvasWidth == width && mCanvasHeight == height) {
            return;
        }
        float radio = mImages.get(0).getHeight() / (float) mImages.get(0).getWidth();
        float dstWidth = width;
        float dstHeight = width * radio;

        Bitmap newImage =
                Bitmap.createScaledBitmap(mImages.get(0), (int) dstWidth, (int) dstHeight, true);
        mImages.get(0).recycle(); // explicit call to avoid out of memory
        mImages.set(0, newImage);

        mWidth = mImages.get(0).getWidth();
        mHeight = mImages.get(0).getHeight();
        mCanvasWidth = width;
        mCanvasHeight = height;
        mX = 0;
        mY = mCanvasHeight - mHeight;
    }

    @Override
    public void onDraw(Canvas c) {
        c.drawBitmap(mImages.get(0), mX, mY, null);
    }

}
