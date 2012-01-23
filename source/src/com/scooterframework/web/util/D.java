/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.web.util;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * D class has helper methods for Date and Time.
 * 
 * @author (Fei) John Chen
 */
public class D {
	public static final long ONE_SECOND = 1000L;
	public static final long ONE_MINUTE = ONE_SECOND * 60;
	public static final long ONE_HOUR = ONE_MINUTE * 60;
	public static final long ONE_DAY = ONE_HOUR * 24;
	public static final long ONE_WEEK = ONE_DAY * 7;
	public static final long ONE_MONTH = ONE_DAY * 30;
	public static final long ONE_YEAR = ONE_DAY * 365;
	
	protected static final String KEY_SECONDS_AFTER = "friendlytime.seconds.after";
	protected static final String KEY_MINUTES_AFTER = "friendlytime.minutes.after";
	protected static final String KEY_HOURS_AFTER = "friendlytime.hours.after";
	protected static final String KEY_DAYS_AFTER = "friendlytime.days.after";
	protected static final String KEY_WEEKS_AFTER = "friendlytime.weeks.after";
	protected static final String KEY_MONTHS_AFTER = "friendlytime.months.after";
	protected static final String KEY_YEARS_AFTER = "friendlytime.years.after";
	protected static final String KEY_MOMENTS_AFTER = "friendlytime.moments.after";
	protected static final String KEY_NOW = "friendlytime.now";
	protected static final String KEY_MOMENTS_AGO = "friendlytime.moments.ago";
	protected static final String KEY_SECONDS_AGO = "friendlytime.seconds.ago";
	protected static final String KEY_MINUTES_AGO = "friendlytime.minutes.ago";
	protected static final String KEY_HOURS_AGO = "friendlytime.hours.ago";
	protected static final String KEY_DAYS_AGO = "friendlytime.days.ago";
	protected static final String KEY_WEEKS_AGO = "friendlytime.weeks.ago";
	protected static final String KEY_MONTHS_AGO = "friendlytime.months.ago";
	protected static final String KEY_YEARS_AGO = "friendlytime.years.ago";
	
	protected static final String COUNT_UNIT_SEPARATOR = "_";
	
    /**
     * Returns a human-friendly date/time message. See <tt>messages.properties</tt> 
     * file for messages. You may override the message by providing your own 
     * messages in a messages properties file of your own locale.
     * 
     * <pre>
     * Sample outputs:
     *   <tt>moments from now</tt>
     *   <tt>right now</tt>
     *   <tt>5 minutes from now</tt>
     *   <tt>2 weeks ago</tt>
     *   <tt>10 years from now</tt>
     * </pre>
     * 
     * @param d a Date instance
     * @return friendly date/time message
     */
	public static String message(Date d) {
		String s = diffFromNow(d);
		String[] sm = s.split(COUNT_UNIT_SEPARATOR);
		return W.message(sm[1], sm);
	}
    
    /**
     * Returns a human-friendly date/time message in a specific locale. 
     * 
     * See <tt>messages.properties</tt> 
     * file for messages. You may override the message by providing your own 
     * messages in a messages properties file of your own locale.
     * 
     * <pre>
     * Sample outputs:
     *   <tt>moments from now</tt>
     *   <tt>right now</tt>
     *   <tt>5 minutes from now</tt>
     *   <tt>2 weeks ago</tt>
     *   <tt>10 years from now</tt>
     * </pre>
     * 
     * @param d a Date instance
     * @return friendly date/time message
     */
    public static String message(Date d, Locale locale) {
		String s = diffFromNow(d);
		String[] sm = s.split(COUNT_UNIT_SEPARATOR);
		return W.message(sm[1], locale, sm);
	}
	
    private static String diffFromNow(Date d) {
    	if (d == null) 
    		throw new IllegalArgumentException("Date cannot be null.");
    	
    	long count = 0L;
    	String key = "";
    	
    	long diff = d.getTime() - Calendar.getInstance().getTimeInMillis();
    	
    	if (diff >= ONE_YEAR) {
    		count = countTime(diff, ONE_YEAR);
    		key = KEY_YEARS_AFTER;
    	}
    	else if (diff >= ONE_MONTH){
    		count = countTime(diff, ONE_MONTH);
    		key = KEY_MONTHS_AFTER;
    	}
    	else if (diff >= ONE_WEEK){
    		count = countTime(diff, ONE_WEEK);
    		key = KEY_WEEKS_AFTER;
    	}
    	else if (diff >= ONE_DAY){
    		count = countTime(diff, ONE_DAY);
    		key = KEY_DAYS_AFTER;
    	}
    	else if (diff >= ONE_HOUR){
    		count = countTime(diff, ONE_HOUR);
    		key = KEY_HOURS_AFTER;
    	}
    	else if (diff >= ONE_MINUTE){
    		count = countTime(diff, ONE_MINUTE);
    		key = KEY_MINUTES_AFTER;
    	}
    	else if (diff >= ONE_SECOND){
    		count = countTime(diff, ONE_SECOND);
    		key = KEY_SECONDS_AFTER;
    	}
    	else if (diff > 0){
    		key = KEY_MOMENTS_AFTER;
    	}
    	else if (diff == 0){
    		key = KEY_NOW;
    	}
    	else if (diff <= (ONE_YEAR * (-1))){
    		count = countTime(diff, ONE_YEAR);
    		key = KEY_YEARS_AGO;
    	}
    	else if (diff <= (ONE_MONTH * (-1))){
    		count = countTime(diff, ONE_MONTH);
    		key = KEY_MONTHS_AGO;
    	}
    	else if (diff <= (ONE_WEEK * (-1))){
    		count = countTime(diff, ONE_WEEK);
    		key = KEY_WEEKS_AGO;
    	}
    	else if (diff <= (ONE_DAY * (-1))){
    		count = countTime(diff, ONE_DAY);
    		key = KEY_DAYS_AGO;
    	}
    	else if (diff <= (ONE_HOUR * (-1))){
    		count = countTime(diff, ONE_HOUR);
    		key = KEY_HOURS_AGO;
    	}
    	else if (diff <= (ONE_MINUTE * (-1))){
    		count = countTime(diff, ONE_MINUTE);
    		key = KEY_MINUTES_AGO;
    	}
    	else if (diff <= (ONE_SECOND * (-1))){
    		count = countTime(diff, ONE_SECOND);
    		key = KEY_SECONDS_AGO;
    	}
    	else if (diff < 0){
    		key = KEY_MOMENTS_AGO;
    	}
    	
    	return count + COUNT_UNIT_SEPARATOR + key;
    }
    
    private static long countTime(long diff, long interval) {
    	return Math.abs(diff)/interval;
    }
}
