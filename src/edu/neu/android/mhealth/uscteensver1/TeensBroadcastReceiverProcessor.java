package edu.neu.android.mhealth.uscteensver1;

import java.util.Date;

import edu.neu.android.mhealth.uscteensver1.data.Labeler;
import edu.neu.android.wocketslib.broadcastreceivers.BroadcastReceiverProcessor;
import edu.neu.android.wocketslib.utils.Log;

public class TeensBroadcastReceiverProcessor extends BroadcastReceiverProcessor {
	private static final String TAG = "TeensBroadcastReceiverProcessor";
	
	@Override
	public void respondSendSMS() {		
		Log.d(TAG, "Respond SendSMS in " + TAG); 
		Labeler.getInstance().addLabel(new Date(), "Sent SMS"); 		
	}
	
	@Override
	public void respondScreenOn() {		
		Log.d(TAG, "Respond Screen ON in " + TAG); 
		Labeler.getInstance().addLabel(new Date(), "Screen on"); 		
	}
	
	@Override
	public void respondPhoneBooted() {
		Log.d(TAG, "Respond Phone Booted in " + TAG); 
		Labeler.getInstance().addLabel(new Date(), "Restart phone"); 		
	}
	
	@Override
	public void respondEndCall() {
		Log.d(TAG, "Respond End Call in " + TAG); 
		Labeler.getInstance().addLabel(new Date(), "End call"); 		
	}
		
	@Override
	public void respondStartCall() {
		Log.d(TAG, "Respond Start Call in " + TAG); 
		Labeler.getInstance().addLabel(new Date(), "Start call"); 		
	}

	@Override
	public void respondCallOut() {
		Log.d(TAG, "Respond Call Out in " + TAG); 
		Labeler.getInstance().addLabel(new Date(), "Call out"); 		
	}

	@Override
	public void respondAirplaneMode() {
		Log.d(TAG, "Respond Airplane Mode in " + TAG); 
		Labeler.getInstance().addLabel(new Date(), "Plane mode"); 		
	}

	@Override
	public void respondCallIn() {
		Log.d(TAG, "Respond Call In in " + TAG); 
		Labeler.getInstance().addLabel(new Date(), "Receive call"); 		
	}

	@Override
	public void respondPowerConnected() {
		Log.d(TAG, "Respond Power Connected in " + TAG); 
		Labeler.getInstance().addLabel(new Date(), "Charge phone"); 		
	}

	@Override
	public void respondPowerDisconnected() {
		Log.d(TAG, "Respond Power Disconnected in " + TAG); 
		Labeler.getInstance().addLabel(new Date(), "Stop charging"); 		
	}

	@Override
	public void respondSMSReceived() {
		Log.d(TAG, "Respond Received SMS in " + TAG); 
		Labeler.getInstance().addLabel(new Date(), "Received SMS"); 		
	}
	
	@Override
	public void respondLocationChanged() {
		Log.d(TAG, "Respond Location Changed in " + TAG); 
		Labeler.getInstance().addLabel(new Date(), "Location changed"); 		
	}

	@Override
	public void respondHeadsetPluggedIn() {
		Log.d(TAG, "Respond Headset Plugged In in " + TAG); 
		Labeler.getInstance().addLabel(new Date(), "Plug earphones"); 		
	}

}
