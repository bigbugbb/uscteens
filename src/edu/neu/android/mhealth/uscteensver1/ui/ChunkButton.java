package edu.neu.android.mhealth.uscteensver1.ui;

import edu.neu.android.mhealth.uscteensver1.data.Chunk;
import android.content.res.Resources;


public class ChunkButton extends CustomButton {
	
	protected float mOffsetX = 0;
	protected float mOffsetY = 0;
	protected float mOffsetInChunkX = 0; // offset when the button is about to be out of screen
	protected float mOffsetInChunkY = 0;
	protected Chunk mHost = null;	
	
	public ChunkButton(Resources res, Chunk host) {
		super(res);
		mHost = host;
	}
	
	public Chunk getHost() {
		return mHost;
	}
	
	public void setDisplayOffset(float offsetX, float offsetY) {
		mOffsetX = offsetX;
		mOffsetY = offsetY;
	}
	
	public void setOffsetInChunk(float offsetX, float offsetY) {
		mOffsetInChunkX = offsetX;
		mOffsetInChunkY = offsetY;
	}
	
	public float getOffsetInChunkX() {
		return mOffsetInChunkX;
	}

	@Override
	public boolean contains(float x, float y) {
		if (!mVisible || !mEnable) {
			return false;
		}
		return (x > mX + mOffsetX + mOffsetInChunkX && x <= mX + mWidth + mOffsetX + mOffsetInChunkX) && 
			   (y > mY + mOffsetY && y <= mY + mHeight + mOffsetY);
	}
}
