package edu.neu.android.mhealth.uscteensver1.ui;


import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.MotionEvent;
import edu.neu.android.mhealth.uscteensver1.USCTeensGlobals;
import edu.neu.android.mhealth.uscteensver1.actions.Action;
import edu.neu.android.mhealth.uscteensver1.data.Chunk;

public class QuestButton extends ChunkButton {	
		
	public QuestButton(Resources res, Chunk host, OnClickListener listener) {
		super(res, host);		
		mWidth  = mHost.getAction().getActionImage().getWidth();
		mHeight = mHost.getAction().getActionImage().getHeight();
		mListener = listener;
		mID = UIID.QUEST;
	}
	
	public boolean isAnswered() {
		Action action = getHost().getAction();
		return !action.getActionID().equals(USCTeensGlobals.UNLABELLED_GUID);
	}
	
	public void setAnswer(Action newAction) {	
		Action oldAction = getHost().getAction();
		
		if (!newAction.getActionID().equals(oldAction.getActionID())) {
			getHost().setAction(newAction);			
		}
	}
	
	public String getStringAnswer() {
		return getHost().getAction().getActionName();
	}
	
	@Override
	public void measureSize(int width, int height) {
		mCanvasWidth  = width;
		mCanvasHeight = height;				
		mY = height * 0.64f;		
	}

	@Override
	public void onSizeChanged(int width, int height) {
		if (mCanvasWidth == width && mCanvasHeight == height) {
			return;
		}		
		mCanvasWidth  = width;
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
