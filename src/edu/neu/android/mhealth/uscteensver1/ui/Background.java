package edu.neu.android.mhealth.uscteensver1.ui;

import android.content.res.Resources;
import edu.neu.android.mhealth.uscteensver1.pages.AppObject;

public class Background extends AppObject {		

	public Background(Resources res) {
		super(res);	
		setKind(BACKGROUND);
		setZOrder(ZOrders.BACKGROUND);
	}		

}
