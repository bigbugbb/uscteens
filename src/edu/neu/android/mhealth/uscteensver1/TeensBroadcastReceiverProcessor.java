package edu.neu.android.mhealth.uscteensver1;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import edu.neu.android.mhealth.uscteensver1.data.Labeler;
import edu.neu.android.wocketslib.broadcastreceivers.BroadcastReceiverProcessor;
import edu.neu.android.wocketslib.utils.Log;

public class TeensBroadcastReceiverProcessor extends BroadcastReceiverProcessor {
	private static final String TAG = "TeensBroadcastReceiverProcessor";

	private String getDateString() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()); 
	}
	
	@Override
	public void setContext(Context context) {}
	
	//TODO change all the methods below to use addLabel with sending a date (vs a string of a date). Will clean up code. 
	@Override
	public void respondSendSMS() {		
		Log.d(TAG, "Respond SendSMS in " + TAG); 
		Labeler.getInstance().addLabel(getDateString(), "Sent SMS"); 		
	}
	
	@Override
	public void respondScreenOn() {		
		Log.d(TAG, "Respond Screen ON in " + TAG); 
		Labeler.getInstance().addLabel(getDateString(), "Screen on"); 		
	}
	
	@Override
	public void respondPhoneBooted() {
		Log.d(TAG, "Respond Phone Booted in " + TAG); 
		Labeler.getInstance().addLabel(getDateString(), "Restart phone"); 		
	}
	
	@Override
	public void respondEndCall() {
		Log.d(TAG, "Respond End Call in " + TAG); 
		Labeler.getInstance().addLabel(getDateString(), "End call"); 		
	}
		
	@Override
	public void respondStartCall() {
		Log.d(TAG, "Respond Start Call in " + TAG); 
		Labeler.getInstance().addLabel(getDateString(), "Start call"); 		
	}

	@Override
	public void respondCallOut() {
		Log.d(TAG, "Respond Call Out in " + TAG); 
		Labeler.getInstance().addLabel(getDateString(), "Call out"); 		
	}

	@Override
	public void respondAirplaneMode() {
		Log.d(TAG, "Respond Airplane Mode in " + TAG); 
		Labeler.getInstance().addLabel(getDateString(), "Plane mode"); 		
	}

	@Override
	public void respondCallIn() {
		Log.d(TAG, "Respond Call In in " + TAG); 
		Labeler.getInstance().addLabel(getDateString(), "Receive call"); 		
	}

	@Override
	public void respondPowerConnected() {
		Log.d(TAG, "Respond Power Connected in " + TAG); 
		Labeler.getInstance().addLabel(getDateString(), "Charge phone"); 		
	}

	@Override
	public void respondPowerDisconnected() {
		Log.d(TAG, "Respond Power Disconnected in " + TAG); 
		Labeler.getInstance().addLabel(getDateString(), "Stop charging"); 		
	}

	@Override
	public void respondSMSReceived() {
		Log.d(TAG, "Respond Received SMS in " + TAG); 
		Labeler.getInstance().addLabel(getDateString(), "Received SMS"); 		
	}
	
	@Override
	public void respondLocationChanged() {
		Log.d(TAG, "Respond Location Changed in " + TAG); 
		Labeler.getInstance().addLabel(getDateString(), "Location changed"); 		
	}

	@Override
	public void respondHeadsetPluggedIn() {
		Log.d(TAG, "Respond Headset Plugged In in " + TAG); 
		Labeler.getInstance().addLabel(getDateString(), "Plug earphones"); 		
	}

}
