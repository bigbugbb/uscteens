package edu.neu.android.mhealth.uscteensver1.main;

import java.util.concurrent.ConcurrentLinkedQueue;

import android.os.Handler;

public class BaseThread extends Thread {
	
	protected boolean mRun = false;	
	protected Handler mHandler = null;	
	protected ConcurrentLinkedQueue<AppEvent> mEventQueue;
	
	protected BaseThread() {
		mEventQueue = new ConcurrentLinkedQueue<AppEvent>();
	}
	
	protected BaseThread(Handler handler) {
		mEventQueue = new ConcurrentLinkedQueue<AppEvent>();
		setHandler(handler);
	}
	
	public void setHandler(Handler handler) {
		mHandler = handler;
	}
	
	public Handler getHandler() {
		return mHandler;
	}
	
	// don't write "synchronized void end()", it may block the drawer thread
	// since both start and end are invoked by ui thread, it's fine just like this.
	public void end() { 
		mRun = false;		
		try {
			join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void start() {
		mRun = true;
		super.start();
	}
	
	public void notifyEvent(AppEvent e) {
		mEventQueue.add(e);		
	}
	
	protected AppEvent pollEvent() {
		return mEventQueue.poll();
	}
	
	protected AppEvent peekEvent() {
		return mEventQueue.peek();
	}
	
	protected void handleEvent(AppEvent e) {
		// do nothing here, subclass can override 
		// this method to deal with specified events
		return;
	}

}
