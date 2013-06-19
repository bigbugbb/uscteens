package edu.neu.android.mhealth.uscteensver1;

import java.util.Date;

import edu.neu.android.mhealth.uscteensver1.data.Labeler;
import edu.neu.android.wocketslib.broadcastreceivers.BroadcastReceiverProcessor;
import edu.neu.android.wocketslib.utils.DateHelper;
import edu.neu.android.wocketslib.utils.Log;

public class USCTeensBroadcastReceiverProcessor extends BroadcastReceiverProcessor {
	private static final String TAG = "USCTeensBroadcastReceiverProcessor";

	private String getDateString() {
		Date aDate = new Date(); 
		String aSDate = DateHelper.serverDateFormatFull.format(aDate); 
		return aSDate; 
	}
	
	//TODO change all the methods below to use addLabel with sending a date (vs a string of a date). Will clean up code. 
	@Override
	public void respondSendSMS() {
		Log.d(TAG, "Respond SendSMS in " + TAG); 
		Labeler.addLabel(getDateString(), "Sent SMS", true); 		
	}
	
	@Override
	public void respondScreenOn() {		
		Log.d(TAG, "Respond Screen ON in " + TAG); 
		Labeler.addLabel(getDateString(), "Screen on", true); 		
	}
	
	@Override
	public void respondPhoneBooted() {
		Log.d(TAG, "Respond Phone Booted in " + TAG); 
		Labeler.addLabel(getDateString(), "Restart phone", true); 		
	}
	
	@Override
	public void respondEndCall() {
		Log.d(TAG, "Respond End Call in " + TAG); 
		Labeler.addLabel(getDateString(), "End call", true); 		
	}
		
	@Override
	public void respondStartCall() {
		Log.d(TAG, "Respond Start Call in " + TAG); 
		Labeler.addLabel(getDateString(), "Start call", true); 		
	}

	@Override
	public void respondCallOut() {
		Log.d(TAG, "Respond Call Out in " + TAG); 
		Labeler.addLabel(getDateString(), "Call out", true); 		
	}

	@Override
	public void respondAirplaneMode() {
		Log.d(TAG, "Respond Airplane Mode in " + TAG); 
		Labeler.addLabel(getDateString(), "Plane mode", true); 		
	}

	@Override
	public void respondCallIn() {
		Log.d(TAG, "Respond Call In in " + TAG); 
		Labeler.addLabel(getDateString(), "Receive call", true); 		
	}

	@Override
	public void respondPowerConnected() {
		Log.d(TAG, "Respond Power Connected in " + TAG); 
		Labeler.addLabel(getDateString(), "Charge phone", true); 		
	}

	@Override
	public void respondPowerDisconnected() {
		Log.d(TAG, "Respond Power Disconnected in " + TAG); 
		Labeler.addLabel(getDateString(), "Stop charging", true); 		
	}

	@Override
	public void respondSMSReceived() {
		Log.d(TAG, "Respond Received SMS in " + TAG); 
		Labeler.addLabel(getDateString(), "Received SMS", true); 		
	}
	
	@Override
	public void respondLocationChanged() {
		Log.d(TAG, "Respond Location Changed in " + TAG); 
		Labeler.addLabel(getDateString(), "Location changed", true); 		
	}

	@Override
	public void respondHeadsetPluggedIn() {
		Log.d(TAG, "Respond Headset Plugged In in " + TAG); 
		Labeler.addLabel(getDateString(), "Plug earphones", true); 		
	}

}
