package edu.neu.android.mhealth.uscteensver1.views;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import edu.neu.android.mhealth.uscteensver1.data.DataSource;
import edu.neu.android.wocketslib.support.DataStorage;
import edu.neu.android.wocketslib.utils.DateHelper;

public class RewardView extends WebView {
	protected final static String DEFAULT_URL = "file:///android_asset/rewards/default.html";

	public RewardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setVisibility(View.GONE);
		
        JavaScriptInterface jsInterface = new JavaScriptInterface();
		getSettings().setJavaScriptEnabled(true);
		addJavascriptInterface(jsInterface, "JSInterface");
       	loadUrl(DEFAULT_URL);        
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		return super.onTouchEvent(event);
	}
	
	public class JavaScriptInterface {

	    public JavaScriptInterface() {
	    }
	    
	    public String getCountOfDayFromStartDate() {
	    	String startDate = DataStorage.getStartDate(getContext(), "");
	    	String selectedDate = DataSource.getCurrentSelectedDate();
	    	
	    	for (int i = 1; i <= 14; ++i) {
	    		try {
	    			Date start = DateHelper.serverDateFormat.parse(startDate);
					Calendar c = Calendar.getInstance();   			  
				    c.setTime(start);   
				    c.add(Calendar.DATE, i - 1);   
				    // compare the date 
				    if (DateHelper.serverDateFormat.format(c.getTime()).equals(selectedDate)) {
				    	return i + "";
				    }
	    		} catch (ParseException e) {
	    			e.printStackTrace();
	    		}
	    	}
	    	
	    	return "unknown date";
	    }
	    
	    public String getDate(int id) {
	    	String result;
	    	String startDate = DataStorage.getStartDate(getContext(), "");
	    	
		    try {
				Date start = DateHelper.serverDateFormat.parse(startDate);
				Calendar c = Calendar.getInstance();   			  
			    c.setTime(start);   
			    c.add(Calendar.DATE, id - 1);   
			    Date d = c.getTime();   
			    result = DateHelper.serverDateFormat.format(d); 
			} catch (ParseException e) {
				e.printStackTrace();
				result = "unknown date";
			}
		    
		    return result;
	    }
	}
}
