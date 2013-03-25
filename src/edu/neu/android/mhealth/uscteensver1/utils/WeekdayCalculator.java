package edu.neu.android.mhealth.uscteensver1.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class WeekdayCalculator {

	static String[] sWeekdays = {
		"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"
	};
	
	// date: yyyy-MM-dd
    public static String getWeekday(String date) {
        int weekday = getWeekdayInNumber(date);
	    
	    return sWeekdays[weekday - 1];
    }      	
    
    /**
     * 
     * @param date YYYY-MM-DD
     * @return 1-7 indicates the weekday number from Monday to Sunday
     */
    public static int getWeekdayInNumber(String date) {
    	String time[] = date.split("-");
        
        int year  = Integer.parseInt(time[0]);         
        int month = Integer.parseInt(time[1]);         
        int day   = Integer.parseInt(time[2]);
         
        int total, weekday, i;         
        boolean leap = false;
     
        leap = (year % 400 ==  0) | (year % 100 != 0) & (year % 4 == 0);        
        weekday = 1; // starting day: 1979-12-31 is monday     
        total = year - 1980 + (year - 1980 + 3) / 4; // initial value of total         
     
	    for (i = 1; i <= month - 1; i++) {	         
	        switch(i) {	             
            case 1:	             
            case 3:	             
            case 5:	             
            case 7:	             
            case 8:	             
            case 10:	             
            case 12:	             
            	total += 31;	             
            	break;	             
            case 4:	             
            case 6:	             
            case 9:	             
            case 11:	             
            	total += 30;	             
            	break;	            
            case 2:	  
            	total += (leap ? 29 : 28);
            	break;	            
            }	        
	    }         
	    total = total + day;	         
	    weekday = (weekday + total) % 7;
	    if (weekday == 0) {
	    	weekday = 7;
	    }
	    
	    return weekday;
    }
    
    /**
     * 
     * @param date1 YYYY-MM-DD
     * @param date2 YYYY-MM-DD
     * @return
     */ 
  
	public static boolean areSameWeekdays(Date date1, Date date2) {   
		Calendar cal1 = Calendar.getInstance();   
		Calendar cal2 = Calendar.getInstance();   
		cal1.setTime(date1);   
		cal2.setTime(date2);   
		int subYear = cal1.get(Calendar.YEAR) - cal2.get(Calendar.YEAR); 
		
		if (0 == subYear) {   
			if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))   
				return true;   
		} else if (1 == subYear && 11 == cal2.get(Calendar.MONTH)) {       	     
			if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))   
				return true;   
		} else if (-1 == subYear && 11 == cal1.get(Calendar.MONTH)) {   
			if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))   
				return true;   
		} 
		
		return false;   
	}   
    	        	        	 
	public static String getSeqWeek(){   
	    Calendar c = Calendar.getInstance(Locale.US);   
	    String week = Integer.toString(c.get(Calendar.WEEK_OF_YEAR));   
	    if (week.length() == 1) 
	    	week = "0" + week;   
	    String year = Integer.toString(c.get(Calendar.YEAR));     
	    return year+week;   
	     
	}   
	         	   
	public static String getMonday(Date date){   
	    Calendar c = Calendar.getInstance();   
	    c.setTime(date);   
	    c.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY);   
	    return new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());   
	}   
	     
	public static String getFriday(Date date){   
	    Calendar c = Calendar.getInstance();   
	    c.setTime(date);   
	    c.set(Calendar.DAY_OF_WEEK,Calendar.FRIDAY);      
	    return new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());     
	}   
	
	public static String afterNDay(int n){   
	    Calendar c = Calendar.getInstance();   
	    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");   
	    c.setTime(new Date());   
	    c.add(Calendar.DATE,n);   
	    Date d2 = c.getTime();   
	    String s = df.format(d2);   
	    return s;   
	}   
	 
	public static String afterNDayFrom(Date date, int n){   
	    Calendar c = Calendar.getInstance();   
	    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");   
	    c.setTime(date);   
	    c.add(Calendar.DATE,n);   
	    Date d2 = c.getTime();   
	    String s = df.format(d2);   
	    return s;   
	} 

}
