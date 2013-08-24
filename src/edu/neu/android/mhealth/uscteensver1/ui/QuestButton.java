package edu.neu.android.mhealth.uscteensver1.ui;


import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.MotionEvent;
import edu.neu.android.mhealth.uscteensver1.TeensGlobals;
import edu.neu.android.mhealth.uscteensver1.data.Chunk;
import edu.neu.android.mhealth.uscteensver1.extra.Action;

public class QuestButton extends ChunkButton {

    public QuestButton(Resources res, Chunk host, OnClickListener listener) {
        super(res, host);
        Bitmap actImage = mHost.getAction().getActionImage();
        if (actImage == null) {
            mHost.getAction().loadIcon();
            actImage = mHost.getAction().getActionImage();
        }
        mWidth = actImage.getWidth();
        mHeight = actImage.getHeight();
        mListener = listener;
        mID = UIID.QUEST;
    }

    public boolean isAnswered() {
        Action action = getHost().getAction();
        return !action.getActionID().equals(TeensGlobals.UNLABELLED_GUID);
    }

    public void setAnswer(Action newAction) {
        Action oldAction = getHost().getAction();
        if (oldAction == null) {
            return;
        }
        if (!newAction.getActionID().equals(oldAction.getActionID())) {
            getHost().setAction(newAction);
        }
    }

    public String getStringAnswer() {
        String actName = getHost().getAction().getActionName();
        String actSubName = getHost().getAction().getActionSubName();
        return actName + (actSubName != null ? "|" + actSubName : "");
    }

    @Override
    public void measureSize(int width, int height) {
        mCanvasWidth = width;
        mCanvasHeight = height;
        mY = height * 0.64f;
    }

    @Override
    public void onSizeChanged(int width, int height) {
        if (mCanvasWidth == width && mCanvasHeight == height) {
            return;
        }
        mCanvasWidth = width;
        mCanvasHeight = height;

        mY = height * 0.64f;
    }

    @Override
    public void onDraw(Canvas c) {
        if (mVisible) {
            c.drawBitmap(mHost.getAction().getActionImage(),
                    mX + mOffsetX + mOffsetInChunkX, mY + mOffsetY + mOffsetInChunkY, null);
        }
    }

    @Override
    public boolean onDown(MotionEvent e) {
        mX += 3;
        mY += 3;
        return true;
    }

    @Override
    public boolean onUp(MotionEvent e) {
        if (mListener != null) {
            mListener.onClick(this);
        }
        mX -= 3;
        mY -= 3;
        return true;
    }

    @Override
    public void onCancelSelection(MotionEvent e) {
        mX -= 3;
        mY -= 3;
    }

}
