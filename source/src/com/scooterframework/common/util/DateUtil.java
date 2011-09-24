/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.scooterframework.web.controller.ACH;

/**
 * DateUtil class has helper methods for rendering date and time. 
 * 
 * <p>
 * Note: By the time of this version, the only allowed date-time patterns 
 * for automatic form processing are listed in description of 
 * {@link #getDateTimePattern(java.lang.String)} method.</p>
 * 
 * @author (Fei) John Chen
 */
public class DateUtil {
    
    /**
     * <p>Returns a date instance from a <tt>dateStr</tt> string. 
     * The date-time pattern needed for parsing will be detected from the input
     * <tt>dateStr</tt> string with the current locale.</p>
     * 
     * <p>
     * Note: By the time of this version, the only allowed date-time patterns 
     * for automatic form processing are listed in description of 
     * {@link #getDateTimePattern(java.lang.String)} method.</p>
     * 
     * @param dateStr  date string
     * @return a date instance
     */
    public static Date parseDate(String dateStr) {
        return parseDate(dateStr, getDateTimePattern(dateStr), ACH.getAC().getLocale());
    }
    
    /**
     * <p>Returns a date instance from a <tt>dateStr</tt> string.</p>
     * 
     * <p>
     * Note: By the time of this version, the only allowed date-time patterns 
     * for automatic form processing are listed in description of 
     * {@link #getDateTimePattern(java.lang.String)} method.</p>
     * 
     * @param dateStr  date string
     * @param pattern  the pattern describing the date and time format
     * @return a date instance
     */
    public static Date parseDate(String dateStr, String pattern) {
        return parseDate(dateStr, pattern, ACH.getAC().getLocale());
    }
    
    /**
     * <p>Returns a date instance from a <tt>dateStr</tt> string. 
     * The date-time pattern needed for parsing will be detected from the input
     * <tt>dateStr</tt> string</p>
     * 
     * <p>
     * Note: By the time of this version, the only allowed date-time patterns 
     * for automatic form processing are listed in description of 
     * {@link #getDateTimePattern(java.lang.String)} method.</p>
     * 
     * @param dateStr  date string
     * @param locale   locale for date
     * @return a date instance
     */
    public static Date parseDate(String dateStr, Locale locale) {
        return parseDate(dateStr, getDateTimePattern(dateStr), locale);
    }
    
    /**
     * <p>Returns a date instance from a <tt>dateStr</tt> string. </p>
     * 
     * <p>
     * Note: By the time of this version, the only allowed date-time patterns 
     * for automatic form processing are listed in description of 
     * {@link #getDateTimePattern(java.lang.String)} method.</p>
     * 
     * @param dateStr  date string
     * @param pattern  the pattern describing the date and time format
     * @param locale   locale for date
     * @return a date instance
     */
    public static Date parseDate(String dateStr, String pattern, Locale locale) {
        if (dateStr == null || "".equals(dateStr)) return null;
        
        Date date = null;
        try {
            if (pattern == null || "".equals(pattern)) {
                pattern = getDateTimePattern(dateStr);
                if ("".equals(pattern)) return null;
            }
            
            if (locale == null) locale = ACH.getAC().getLocale();
            
            SimpleDateFormat sdf = new SimpleDateFormat(pattern, locale);
            date = sdf.parse(dateStr);
        }
        catch(ParseException pex) {
            throw new RuntimeException("Error in parseDate: " + pex.getMessage());
        }
        return date;
    }
    
    /**
     * Returns a data time pattern string.
     * <pre>
     *               slashDate: MM/dd/yyyy  or MM/dd/yy
     *                dashDate: yyyy-MM-dd
     *              hourMinute: HH:mm
     *        hourMinuteSecond: HH:mm:ss
     *   hourMinuteSecondMilli: HH:mm:ss.SSS
     *              dashDateHM: yyyy-MM-dd HH:mm
     *             dashDateHMS: yyyy-MM-dd HH:mm:ss
     *            dashDateHMSM: yyyy-MM-dd HH:mm:ss.SSS
     *             slashDateHM: MM/dd/yyyy HH:mm        or MM/dd/yy HH:mm
     *            slashDateHMS: MM/dd/yyyy HH:mm:ss     or MM/dd/yy HH:mm:ss
     *           slashDateHMSM: MM/dd/yyyy HH:mm:ss.SSS or MM/dd/yy HH:mm:ss.SSS
     * </pre>
     * 
     * @param dt a date time string
     * @return a data time pattern string
     */
    public static final String getDateTimePattern(String dt) {
        String pattern = "";
        
        try {
            if (checkParseable(dt)) {
                dt = dt.trim();
                
                boolean hasDashDate = false;
                boolean hasSlashDate = false;
                boolean hasTime = false;
                
                if (dt.indexOf('-') != -1) hasDashDate = true;
                if (dt.indexOf('/') != -1) hasSlashDate = true;
                if (dt.indexOf(':') != -1) hasTime = true;
                
                if (hasDashDate && hasSlashDate) throw new Exception("Cannot have both dash and slash in the same date-time string.");
                
                if (hasDashDate) {
                    String dateString = dt;
                    String timePattern = "";
                    if (hasTime) {
                        dateString = dt.substring(0, dt.indexOf(' '));
                        String timeString = dt.substring(dt.lastIndexOf(' ') + 1);
                        timePattern = getTimePattern(timeString);
                    }
                    
                    String datePattern = getDatePattern(dateString, '-');
                    if (hasTime) {
                        pattern = datePattern + " " + timePattern;
                    }
                    else {
                        pattern = datePattern;
                    }
                }
                else if (hasSlashDate) {
                    String dateString = dt;
                    String timePattern = "";
                    if (hasTime) {
                        dateString = dt.substring(0, dt.indexOf(' '));
                        String timeString = dt.substring(dt.lastIndexOf(' ') + 1);
                        timePattern = getTimePattern(timeString);
                    }
                    
                    String datePattern = getDatePattern(dateString, '/');
                    if (hasTime) {
                        pattern = datePattern + " " + timePattern;
                    }
                    else {
                        pattern = datePattern;
                    }
                }
                else if (hasTime) {
                    pattern = getTimePattern(dt);
                }
            }
        }
        catch(Exception ex) {
            ;
        }
        
        return pattern;
    }
    
    /**
     * <p>
     * checks if the date time string is parseable by this program.
     * </p>
     * 
     * <p>
     * Note: By the time of this version, the only allowed date-time patterns 
     * are in description of getDateTimePattern(String dt) method.</p>
     * 
     * @param dt an input string
     * @return true if the string is parseable.
     */
    private static boolean checkParseable(String dt) {
        if (dt == null || dt.length() == 0) return false;
        
        boolean state = true;
        int length = dt.length();
        for (int i=0; i < length; i++) {
            char c = dt.charAt(i);
            if (ALLOWED_CHARS_FOR_DATETIME.indexOf(c) == -1) {
                state = false;
                break;
            }
        }
        
        return state;
    }
    
    private static String getTimePattern(String dt) throws Exception {
        int dtLength = (dt != null) ? dt.length() : 0;
        String timePattern = "";
        
		if ((dtLength >= 3) && (dtLength <= 12)) {
            
            boolean hasTimeMilli = false;
            if (dt.indexOf('.') != -1) hasTimeMilli = true;
            
			int colonCount = 0;
			for (int i = 0; i < dtLength; i++) {
				char c = dt.charAt(i);

				if (c == ':') {
					colonCount++;
				}
			}

			if (colonCount == 1) {
                if (hasTimeMilli) throw new Exception("Unparseable date: \"" + dt + "\"");
                
				timePattern = "HH:mm";
			}
            else if (colonCount == 2) {
                timePattern = (hasTimeMilli)? "HH:mm:ss.SSS":"HH:mm:ss";
            }
		}
        
        return timePattern;
    }
    
    /**
     * The spliter must be either '-' or '/'.
     * 
     * @return 
     * @param spliter
     * @param dt
     */
    private static String getDatePattern(String dt, char spliter) {
        int dtLength = (dt != null) ? dt.length() : 0;
        String datePattern = "";
        
		if ((dtLength >= 6) && (dtLength <= 10)) {
			int spliterCount = 0;

			for (int i = 0; i < dtLength; i++) {
				char c = dt.charAt(i);

				if (c == spliter) {
					spliterCount++;
				}
			}

			if (spliterCount == 2) {
                if ('-' == spliter) {
                    String year = dt.substring(0, dt.indexOf('-'));
                    datePattern = (year.length()==2)?"yy-MM-dd":"yyyy-MM-dd";
                }
                else
                if ('/' == spliter) {
                    String year = dt.substring(dt.lastIndexOf('/') + 1);
                    datePattern = (year.length()==2)?"MM/dd/yy":"MM/dd/yyyy";
                }
			}
		}
        
        return datePattern;
    }
    
    private static String ALLOWED_CHARS_FOR_DATETIME = "0123456789-/:. ";
}
