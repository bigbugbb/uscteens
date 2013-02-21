package edu.neu.android.mhealth.uscteensver1.data;

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
     
	    for(i = 1; i <= month - 1; i++) {	         
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
}