/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.web.util;

import java.text.DecimalFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.scooterframework.common.util.DateUtil;
import com.scooterframework.common.util.Util;
import com.scooterframework.common.util.WordUtil;
import com.scooterframework.web.controller.ACH;

/**
 * T(TextHelper) class has helper methods for rendering a text or munipulating 
 * a string. <br/>
 * 
 * This class is for generic objects. It does not render an ActiveRecord 
 * object. Use O(ObjectHelper) for that purpose.<br/>
 * 
 * 
 * @author (Fei) John Chen
 */
public class T {

    //=========================================================
    // Generic
    //=========================================================
    
	/**
	 * Returns a string based on true or false of <tt>choice</tt>.
	 * 
	 * @return <tt>wordTrue</tt> if choice is true.
	 */
	public static String booleanWord(boolean choice, String wordTrue, String wordFalse) {
		return (choice)?wordTrue:wordFalse;
	}
    
    /**
     * Returns a pluralized word if the count is more than one.
     * 
     * @return pluralized string
     */
    public static String pluralize(int count, String word) {
        String plform = (count <=1 || word == null)?word:(WordUtil.pluralize(word));
        return pluralize(count, word, plform);
    }
    
    /**
     * Returns a pluralized word if the count is more than one.
     * 
     * @return pluralized string
     */
    public static String pluralize(int count, String word, String plform) {
        return count + " " + ((count > 1)?plform:word);
    }
    
    /**
     * Returns a pluralized word if the count is more than one.
     * 
     * @return pluralized string
     */
    public static String pluralize(Object count, String word) {
        return pluralize(Util.getSafeIntValue(count), word);
    }
    
    /**
     * Returns a pluralized word if the count is more than one.
     * 
     * @return pluralized string
     */
    public static String pluralize(Object count, String word, String plform) {
        return pluralize(Util.getSafeIntValue(count), word, plform);
    }
    
    //=========================================================
    // Object Related
    //=========================================================
    
    /**
     * Returns text of an object with default locale.
     * 
     * @param data the object
     * @return text of data
     */
    public static String text(Object data) {
        return text(data, null);
    }
    
    /**
     * Returns text of an object in a certain pattern with default locale.
     * 
     * @param data the object
     * @param pattern the pattern of result text
     * @return text of data
     */
    public static String text(Object data, String pattern) {
        return text(data, pattern, ACH.getAC().getLocale());
    }
    
    /**
     * Returns text of an object in a certain pattern and locale.
     * 
     * @param data the object
     * @param pattern the pattern of result text
     * @param locale the locale of result text
     * @return text of data
     */
    public static String text(Object data, String pattern, Locale locale) {
        if (data == null) return "";
        
        String result = "";
        
        if (data instanceof String) {
            result = (String)data;
        }
        else
        if (data instanceof Date) {
            result = textOfDate(data, pattern, locale);
        }
        else 
        if (data instanceof Number) {
            result = textOfNumber(data, pattern, locale);
        }
        else if (data instanceof Object[]) {
            throw new IllegalArgumentException("text() does not handle array type.");
        }
        else {
            result = data.toString();
        }
        
        return (result != null)?result:"";
    }
    
    /**
     * Returns text of an object of a type.
     * 
     * @param data the object
     * @param type data type (1=Currency, 2=Date, 3=Number)
     * @return text of data
     */
    public static String text(Object data, int type) {
        return text(data, type, null);
    }
    
    /**
     * Returns text of an object of a type in a certain pattern with default locale.
     * 
     * @param data the object
     * @param type data type (1=Currency, 2=Date, 3=Number)
     * @param pattern the pattern of result text
     * @return text of data
     */
    public static String text(Object data, int type, String pattern) {
        return text(data, type, pattern, ACH.getAC().getLocale());
    }
    
    /**
     * Returns text of an object of a type in a certain pattern and locale.
     * 
     * @param data the object
     * @param type data type (1=Currency, 2=Date, 3=Number)
     * @param pattern the pattern of result text
     * @param locale the locale of result text
     * @return text of data
     */
    public static String text(Object data, int type, String pattern, Locale locale) {
        if (data == null) return "";
        
        String result = "";
        
        switch (type) {
            case NUMBER:
                result = textOfNumber(data, pattern, locale);
                break;
            case DATE:
                result = textOfDate(data, pattern, locale);
                break;
            case CURRENCY:
                result = textOfCurrency(data, locale);
                break;
            default:
                result = text(data, pattern, locale);
        }
        
        return (result != null)?result:"";
    }
    
    
    //=========================================================
    // Number Related
    //=========================================================
    
    /**
     * Checks if a data object is a numberic data. A string value can be numeric 
     * too if it can be converted to a number.
     * 
     * @param data
     * @return true if the data represents a number.
     */
    public static boolean isNumeric(Object data) {
        if (data == null) return false;
        
        if (data instanceof Number) return true;
        
        if (data instanceof Object[]) 
            throw new IllegalArgumentException("isNumeric(s) does not handle array type.");
        
        boolean numeric = true;
        try {
            new Double(data.toString());
        }
        catch(Exception ex) {
            numeric = false;
        }
        
        return numeric;
    }
    
    /**
     * Returns a text of number with a certain precision.
     * 
     * First group the number into thousands and round it to the number of 
     * decimal places specified by precision.
     * 
     * This method is exactly the same as <tt>textOfNumber(Object number, int precision)</tt>.
     * 
     * @param data the object
     * @param precision number of decimal needed
     * @return a number text 
     */
    public static String numberFormat(Object data, int precision) {
        return textOfNumber(data, precision);
    }
    
    /**
     * Returns a text of number with a certain precision.
     * 
     * First group the number into thousands and round it to the number of 
     * decimal places specified by precision.
     * 
     * @param number the object
     * @param precision number of decimal needed
     * @return a number text 
     */
    public static String textOfNumber(Object number, int precision) {
        String formatString = "#,###";
        if (precision > 0) formatString += ".";
        for (int i=0; i<precision; i++) {
            formatString += "0";
        }
        return textOfNumber(number, formatString);
    }
    
    /**
     * Returns a text of number.
     * 
     * @param number the object
     * @return a number text 
     */
    public static String textOfNumber(Object number) {
        return textOfNumber(number, null, ACH.getAC().getLocale());
    }
    
    /**
     * Returns a text of number.
     * 
     * @param number the object
     * @param pattern the pattern format of the result
     * @return a number text 
     */
    public static String textOfNumber(Object number, String pattern) {
        return textOfNumber(number, pattern, ACH.getAC().getLocale());
    }
    
    /**
     * Returns a text of number.
     * 
     * @param number the object
     * @param pattern the pattern format of the result
     * @param locale the locale of the result
     * @return a number text 
     */
    public static String textOfNumber(Object number, String pattern, Locale locale) {
        if (number == null) return "";
        
        if (pattern == null || "".equals(pattern)) return number.toString();
        
        if (locale == null) locale = ACH.getAC().getLocale();
        
        String formattedValue = "";
        try {
            Format nf = NumberFormat.getNumberInstance(locale);            
            if (pattern != null && !"".equals(pattern)) {
                ((DecimalFormat)nf).applyPattern(pattern);
            }
            
            if (number instanceof Number) {
                formattedValue = nf.format(number);
            }
            else {
                if (number instanceof String) {
                    formattedValue = nf.format(new Double((String)number));
                }
            }
        }
        catch(NumberFormatException ex) {
            ;
        }
        return formattedValue;
    }
    
    
    //=========================================================
    // Currency Related
    //=========================================================
    
    /**
     * Returns a text of currency.
     * 
     * @param number the number
     * @return a currency text 
     */
    public static String textOfCurrency(Object number) {
        return textOfCurrency(number, ACH.getAC().getLocale());
    }
    
    /**
     * Returns a text of currency.
     * 
     * @param number the number
     * @param locale the locale of the result
     * @return a currency text 
     */
    public static String textOfCurrency(Object number, Locale locale) {
        if (number == null) return "";
        if (number instanceof Object[]) 
            throw new IllegalArgumentException("textOfCurrency() does not handle array type.");
        return textOfCurrency(number.toString(), locale);
    }
    
    /**
     * Returns a text of currency.
     * 
     * @param number the number
     * @param locale the locale of the result
     * @return a currency text 
     */
    public static String textOfCurrency(String number, Locale locale) {
        if (number == null || "".equals(number.trim())) return "";
        return textOfCurrency((new Double(number)).doubleValue(), locale);
    }
    
    /**
     * Returns a text of currency.
     * 
     * @param number the number
     * @return a currency text 
     */
    public static String textOfCurrency(float number) {
        return textOfCurrency(number, ACH.getAC().getLocale());
    }
    
    /**
     * Returns a text of currency.
     * 
     * @param number the number
     * @param locale the locale of the result
     * @return a currency text 
     */
    public static String textOfCurrency(float number, Locale locale) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);
        return formatter.format(number);
    }
    
    /**
     * Returns a text of currency.
     * 
     * @param number the number
     * @return a currency text 
     */
    public static String textOfCurrency(double number) {
        return textOfCurrency(number, ACH.getAC().getLocale());
    }
    
    /**
     * Returns a text of currency.
     * 
     * @param number the number
     * @param locale the locale of the result
     * @return a currency text 
     */
    public static String textOfCurrency(double number, Locale locale) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);
        return formatter.format(number);
    }
    
    /**
     * Returns a text of currency.
     * 
     * @param number the number
     * @return a currency text 
     */
    public static String textOfCurrency(int number) {
        return textOfCurrency(number, ACH.getAC().getLocale());
    }
    
    /**
     * Returns a text of currency.
     * 
     * @param number the number
     * @param locale the locale of the result
     * @return a currency text 
     */
    public static String textOfCurrency(int number, Locale locale) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);
        return formatter.format(number);
    }
    
    /**
     * Returns a text of currency.
     * 
     * @param number the number
     * @return a currency text 
     */
    public static String textOfCurrency(long number) {
        return textOfCurrency(number, ACH.getAC().getLocale());
    }
    
    /**
     * Returns a text of currency.
     * 
     * @param number the number
     * @param locale the locale of the result
     * @return a currency text 
     */
    public static String textOfCurrency(long number, Locale locale) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);
        return formatter.format(number);
    }
    
    
    //=========================================================
    // Date Related
    //=========================================================
    
    /**
     * Returns a map of current date and time. Keys in the map are
     * <tt>year</tt>, <tt>mon</tt>, <tt>month</tt>, <tt>mday</tt>, <tt>weekday</tt>, 
     * <tt>hours</tt>, <tt>minutes</tt>, <tt>seconds</tt>, <tt>mills</tt> and <tt>timeinmillis</tt>.
     * 
     * <pre>
     * Example:
     * Key          Value
     * -----        -------
     * year         2007
     * mon          7
     * month        July
     * mday         18
     * weekday      Wednesday
     * hours        0
     * minutes      39
     * seconds      24
     * mills        354
     * timeinmillis 1184776764354
     * </pre>
     * 
     * @return Map
     */
    public static Map<String, String> getDateAsMap() {
        String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        String[] weekdays = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        
        Map<String, String> map = new HashMap<String, String>();
        Calendar calendar = Calendar.getInstance();
        map.put("year", "" + calendar.get(Calendar.YEAR));
        map.put("mon", "" + (calendar.get(Calendar.MONTH) + 1));
        map.put("month", months[calendar.get(Calendar.MONTH)]);
        map.put("mday", "" + calendar.get(Calendar.DATE));
        map.put("weekday", "" + weekdays[calendar.get(Calendar.DAY_OF_WEEK)-1]);
        map.put("hours", "" + calendar.get(Calendar.HOUR));
        map.put("minutes", "" + calendar.get(Calendar.MINUTE));
        map.put("seconds", "" + calendar.get(Calendar.SECOND));
        map.put("mills", "" + calendar.get(Calendar.MILLISECOND));
        map.put("timeinmillis", "" + calendar.getTimeInMillis());
        return map;
    }
    
    /**
     * Returns a property from a map of current date and time. Keys in the map are
     * year, mon, month, mday, weekday, hours, minutes, seconds, mills.
     * 
     * <ptr>
     * Example:
     * Key          Value
     * -----        -------
     * year         2007
     * mon          7
     * month        July
     * mday         18
     * weekday      Wednesday
     * hours        0
     * minutes      39
     * seconds      24
     * mills        354
     * timeinmillis 1184776764354
     * </pre>
     * 
     * @return Map
     */
    public static String getDateProperty(String key) {
        return (String)getDateAsMap().get(key);
    }
    
    /**
     * Returns text of a date object.
     * 
     * @param date the object
     * @return text of date
     */
    public static String textOfDate(Object date) {
        return textOfDate(date, null, ACH.getAC().getLocale());
    }
    
    /**
     * <p>Returns text of a date object for a certain pattern.</p>
     * 
     * <p>If the result of this method is used in an html form and will be 
     * parsed automatically by Scooter, not all patterns are allowed. 
     * 
     * The allowed patterns are listed in the description of 
     * {@link com.scooterframework.common.util.DateUtil#getDateTimePattern(String)} 
     * method. Other kind of patterns may result in null value.</p>
     * 
     * @param date the object
     * @param pattern the pattern of result text
     * @return text of date
     */
    public static String textOfDate(Object date, String pattern) {
        return textOfDate(date, pattern, ACH.getAC().getLocale());
    }
    
    /**
     * <p>Returns text of a date object for a certain pattern and locale.</p>
     * 
     * <p>If the result of this method is used in an html form and will be 
     * parsed automatically by Scooter, not all patterns are allowed. 
     * 
     * The allowed patterns are listed in the description of 
     * {@link com.scooterframework.common.util.DateUtil#getDateTimePattern(String)} 
     * method. Other kind of patterns may result in null value.</p>
     * 
     * <p>If the gived pattern is empty and the date object is of Date type, 
     * then a default pattern "yyyy-MM-dd HH:mm:ss" is used. </p>
     * 
     * @param date the object
     * @param pattern the pattern of result text
     * @param locale the locale of result text
     * @return text of date
     */
    public static String textOfDate(Object date, String pattern, Locale locale) {
        if (date == null) return "";
        
        if (locale == null) locale = ACH.getAC().getLocale();
        
        Object theValue = null;
        
        if (date instanceof String) {
            theValue = DateUtil.parseDate((String)date, pattern, locale);
            if (theValue == null) return "";
        }
        else if (date instanceof Date) {
            theValue = date;
        }
        else if (date instanceof Object[]) {
            throw new IllegalArgumentException("textOfDate() does not handle array type.");
        }
        else {
            return date.toString();
        }
        
        if (pattern == null || "".equals(pattern)) {
            //return DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.LONG, locale).format(theValue);
            //This caused automcatic form processing to fail because DateUtil can not parse this date pattern.
            
            pattern = "yyyy-MM-dd HH:mm:ss";
        }
        
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, locale);
        return sdf.format((Date)theValue);
    }
    
    /**
     * int constant represents unknown data type.
     */
    public static final int UNKNOWN = 0;
    
    /**
     * int constant represents currency data type.
     */
    public static final int CURRENCY = 1;
    
    /**
     * int constant represents date data type.
     */
    public static final int DATE = 2;
    
    /**
     * int constant represents numberic data type.
     */
    public static final int NUMBER = 3;
}
