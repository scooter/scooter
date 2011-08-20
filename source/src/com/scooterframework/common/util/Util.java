/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.common.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

/**
 * Util class has helper methods not included in other util classes.
 * 
 * @author (Fei) John Chen
 */
public class Util {
    
    /**
     * Checks sign of a number. 
     * 
     * <pre>
     *  If number < 0, then sign returns -1.
     *  If number = 0, then sign returns 0.
     *  If number > 0, then sign returns 1.
     * </pre>
     * @param number    the number to test its sign.
     * @return sign of a number
     */
    public static int sign(double number) {
        if (number > 1) return 1;
        if (number < 1) return -1;
        return 0;
    }
    
    /**
     * Checks sign of a number. 
     * 
     * <pre>
     *  If number < 0, then sign returns -1.
     *  If number = 0, then sign returns 0.
     *  If number > 0, then sign returns 1.
     * </pre>
     * @param number    the number to test its sign.
     * @return sign of a number
     */
    public static int sign(float number) {
        if (number > 1) return 1;
        if (number < 1) return -1;
        return 0;
    }
    
    /**
     * Checks sign of a number. 
     * 
     * <pre>
     *  If number < 0, then sign returns -1.
     *  If number = 0, then sign returns 0.
     *  If number > 0, then sign returns 1.
     * </pre>
     * @param number    the number to test its sign.
     * @return sign of a number
     */
    public static int sign(int number) {
        if (number > 1) return 1;
        if (number < 1) return -1;
        return 0;
    }
    
    /**
     * Checks sign of a number. 
     * 
     * <pre>
     *  If number < 0, then sign returns -1.
     *  If number = 0, then sign returns 0.
     *  If number > 0, then sign returns 1.
     * </pre>
     * @param number    the number to test its sign.
     * @return sign of a number
     */
    public static int sign(long number) {
        if (number > 1) return 1;
        if (number < 1) return -1;
        return 0;
    }
    
    /**
     * Checks sign of a number. 
     * 
     * <pre>
     *  If number < 0, then sign returns -1.
     *  If number = 0, then sign returns 0.
     *  If number > 0, then sign returns 1.
     * </pre>
     * @param number    the number to test its sign.
     * @return sign of a number
     */
    public static short sign(short number) {
        if (number > 1) return 1;
        if (number < 1) return -1;
        return 0;
    }
    
    /**
     * Checks sign of a number. 
     * 
     * <pre>
     *  If number < 0, then sign returns -1.
     *  If number = 0, then sign returns 0.
     *  If number > 0, then sign returns 1.
     * </pre>
     * @param number    the number to test its sign.
     * @return sign of a number
     */
    public static int sign(Number number) {
        if (number.doubleValue() > 1) return 1;
        if (number.doubleValue() < 1) return -1;
        return 0;
    }
    
    /**
     * <tt>decode</tt> method has the functionality of an IF-THEN-ELSE statement.
     * See description in <tt>decode(Object expression, Map searchResults, Object defaultValue)</tt>
     * 
     * @param expression the value to compare.
     * @param searchResults a string of search and result pair. 
     * @param defaultValue default value if a search returns no match.
     * @return result of decode
     */
    public static Object decode(int expression, String searchResults, Object defaultValue) {
        return decode(expression, Converters.convertStringToMap(searchResults), defaultValue);
    }
    
    /**
     * <tt>decode</tt> method has the functionality of an IF-THEN-ELSE statement.
     * See description in <tt>decode(Object expression, Map searchResults, Object defaultValue)</tt>
     * 
     * @param expression the value to compare.
     * @param searchResults a map of search and result pair. 
     * @param defaultValue default value if a search returns no match.
     * @return result of decode
     */
    public static Object decode(int expression, Map<String, Object> searchResults, Object defaultValue) {
        return decode(Integer.valueOf(expression), searchResults, defaultValue);
    }
    
    /**
     * <tt>decode</tt> method has the functionality of an IF-THEN-ELSE statement.
     * See description in <tt>decode(Object expression, Map searchResults, Object defaultValue)</tt>
     * 
     * @param expression the value to compare.
     * @param searchResults a string of search and result pair. 
     * @param defaultValue default value if a search returns no match.
     * @return result of decode
     */
    public static Object decode(long expression, String searchResults, Object defaultValue) {
        return decode(expression, Converters.convertStringToMap(searchResults), defaultValue);
    }
    
    /**
     * <tt>decode</tt> method has the functionality of an IF-THEN-ELSE statement.
     * See description in <tt>decode(Object expression, Map searchResults, Object defaultValue)</tt>
     * 
     * @param expression the value to compare.
     * @param searchResults a map of search and result pair. 
     * @param defaultValue default value if a search returns no match.
     * @return result of decode
     */
    public static Object decode(long expression, Map<String, Object> searchResults, Object defaultValue) {
        return decode(Long.valueOf(expression), searchResults, defaultValue);
    }
    
    /**
     * <tt>decode</tt> method has the functionality of an IF-THEN-ELSE statement.
     * See description in <tt>decode(Object expression, Map searchResults, Object defaultValue)</tt>
     * 
     * @param expression the value to compare.
     * @param searchResults a string of search and result pair. 
     * @param defaultValue default value if a search returns no match.
     * @return result of decode
     */
    public static Object decode(Object expression, String searchResults, Object defaultValue) {
        return decode(expression, searchResults, defaultValue, false);
    }
    
    /**
     * <tt>decode</tt> method has the functionality of an IF-THEN-ELSE statement.
     * See description in <tt>decode(Object expression, Map searchResults, Object defaultValue)</tt>
     * 
     * @param expression the value to compare.
     * @param searchResults a string of search and result pair. 
     * @param defaultValue default value if a search returns no match.
     * @param ignoreKeyCase true if cases of keys in searchResults are ignored.
     * @return result of decode
     */
    public static Object decode(Object expression, String searchResults, Object defaultValue, boolean ignoreKeyCase) {
        return decode(expression, Converters.convertStringToMap(searchResults), defaultValue);
    }
    
    /**
     * <tt>decode</tt> method has the functionality of an IF-THEN-ELSE statement.
     * See description in <tt>decode(Object expression, Map searchResults, Object defaultValue)</tt>
     * 
     * @param expression the value to compare.
     * @param searchResults a map of search and result pair. 
     * @param defaultValue default value if a search returns no match.
     * @return result of decode
     */
    
    public static Object decode(Object expression, Map<String, ?> searchResults, Object defaultValue) {
        return decode(expression, searchResults, defaultValue, false);
    }
    
    /**
     * <tt>decode</tt> method has the functionality of an IF-THEN-ELSE statement.
     * 
     * The value search is compared against expression. If expression is equal 
     * to search, then the result is returned. Otherwise, the default value is 
     * returned. If default is omitted, then the decode statement will return 
     * null (if no matches are found).
     * 
     * <pre>
     * Examples:
     *      decode(status, "1=Open, 0=Close", "-1=Unknown") would return "Open" if status has value 1.
     *      decode(sex, "m=Male, f=Female") would return "Male" if sex has value "m".
     *      decode(state, "CA=California, VA=Virginia", "Please select a state") would 
     *              return "California" if state has value "CA".
     * </pre>
     * 
     * <p><tt>decode()</tt> can be combined with other methods too. </p>
     * <pre>
     * Examples:
     *      decode((date1 - date2) - Math.abs(date1 - date2), "0=date2", date1) would 
     *              return date2 if date1 > date2. Otherwise, the decode function returns date1.
     *      
     *      The following has the same effect:
     *      decode(sign(date1-date2), 1, date2, date1)
     * </pre>
     * 
     * @param expression the value to compare.
     * @param searchResults a map of search and result pair. 
     * @param defaultValue default value if a search returns no match.
     * @param ignoreKeyCase true if cases of keys in searchResults are ignored.
     * @return result of decode
     */
    public static Object decode(Object expression, Map<String, ?> searchResults, Object defaultValue, boolean ignoreKeyCase) {
        if (expression == null || searchResults == null) return defaultValue;
        
        Object value = null;
        String expr = expression.toString();
        for (Map.Entry<String, ?> entry : searchResults.entrySet()) {
        	String key = entry.getKey();
            if (expr.equals(key) || (ignoreKeyCase && expr.equalsIgnoreCase(key))) {
                value = searchResults.get(key);
                break;
            }
        }
        
        return (value != null)?value:defaultValue;
    }
    
    /**
     * <tt>nvl</tt> method lets you substitute a value when a null value is 
     * encountered.
     * 
     * @param value                 the value to test
     * @param substituteWhenNull    substitute value when value is null
     * @return substitute when value is null
     */
    public static Object nvl(Object value, Object substituteWhenNull) {
        return (value != null)?value:substituteWhenNull;
    }
    
    /**
     * <tt>nvl</tt> method lets you substitute a value when a null value is 
     * encountered.
     * 
     * @param value                 the value to test
     * @param substituteWhenNull    substitute value when value is null
     * @return substitute when value is null
     */
    public static String nvl(String value, String substituteWhenNull) {
        return (value != null)?value:substituteWhenNull;
    }
    
    /**
     * <tt>nvl</tt> method lets you substitute a value when a null value is 
     * encountered as well as when a non-null value is encountered.
     * 
     * @param value                 the value to test
     * @param substituteWhenNotNull substitute value when value is not null
     * @param substituteWhenNull    substitute value when value is null
     * @return substitute when value is null
     */
    public static Object nvl(Object value, Object substituteWhenNotNull, Object substituteWhenNull) {
        return (value != null)?substituteWhenNotNull:substituteWhenNull;
    }
    
    /**
     * <tt>nvl</tt> method lets you substitute a value when a null value is 
     * encountered as well as when a non-null value is encountered.
     * 
     * @param value                 the value to test
     * @param substituteWhenNotNull substitute value when value is not null
     * @param substituteWhenNull    substitute value when value is null
     * @return substitute when value is null
     */
    public static String nvl(String value, String substituteWhenNotNull, String substituteWhenNull) {
        return (value != null)?substituteWhenNotNull:substituteWhenNull;
    }
    
    /**
     * <tt>ifNull</tt> method lets you substitute a value when a null value is 
     * encountered.
     * 
     * This method is the same as the <tt>nvl</tt> method.
     * 
     * @param value                 the value to test
     * @param substituteWhenNull    substitute value when value is null
     * @return substitute when value is null
     */
    public static Object ifNull(Object value, Object substituteWhenNull) {
        return (value != null)?value:substituteWhenNull;
    }
    
    /**
     * <tt>ifNull</tt> method lets you substitute a value when a null value is 
     * encountered.
     * 
     * This method is the same as the <tt>nvl</tt> method.
     * 
     * @param value                 the value to test
     * @param substituteWhenNull    substitute value when value is null
     * @return substitute when value is null
     */
    public static String ifNull(String value, String substituteWhenNull) {
        return (value != null)?value:substituteWhenNull;
    }
    
    /**
     * <tt>ifNull</tt> method lets you substitute a value when a null value is 
     * encountered as well as when a non-null value is encountered.
     * 
     * This method is the same as the <tt>nvl</tt> method.
     * 
     * @param value                 the value to test
     * @param substituteWhenNotNull substitute value when value is not null
     * @param substituteWhenNull    substitute value when value is null
     * @return substitute when value is null
     */
    public static Object ifNull(Object value, Object substituteWhenNotNull, Object substituteWhenNull) {
        return (value != null)?substituteWhenNotNull:substituteWhenNull;
    }
    
    /**
     * <tt>ifNull</tt> method lets you substitute a value when a null value is 
     * encountered as well as when a non-null value is encountered.
     * 
     * This method is the same as the <tt>nvl</tt> method.
     * 
     * @param value                 the value to test
     * @param substituteWhenNotNull substitute value when value is not null
     * @param substituteWhenNull    substitute value when value is null
     * @return substitute when value is null
     */
    public static String ifNull(String value, String substituteWhenNotNull, String substituteWhenNull) {
        return (value != null)?substituteWhenNotNull:substituteWhenNull;
    }
    
    /**
     * <tt>ifTrue</tt> method lets you choose a value when a <tt>true</tt> 
     * value is encountered as well as when a <tt>false</tt> value is encountered.
     * 
     * @param state          the state to test
     * @param valueForTrue   value when state is true
     * @param valueForFalse  value when state is false
     * @return valueForTrue if state is true, otherwise valueForFalse.
     */
    public static Object ifTrue(boolean state, Object valueForTrue, Object valueForFalse) {
        return (state)?valueForTrue:valueForFalse;
    }
    
    /**
     * <tt>ifEmpty</tt> method lets you substitute a value when an empty 
     * value is encountered. An empty object is either null or an 
     * empty string.
     * 
     * @param value                  the value to test
     * @param substituteWhenEmpty    substitute value when value is empty
     * @return substitute when value is empty
     */
    public static Object ifEmpty(Object value, Object substituteWhenEmpty) {
        return (isEmpty(value))?substituteWhenEmpty:value;
    }
    
    /**
     * <tt>ifEmpty</tt> method lets you substitute a value when an empty 
     * value is encountered as well as when a non-null value is encountered. An 
     * empty object is either null or an empty string.
     * 
     * @param value                  the value to test
     * @param substituteWhenNotEmpty substitute value when value is not empty
     * @param substituteWhenEmpty    substitute value when value is empty
     * @return substitute when value is empty
     */
    public static Object ifEmpty(Object value, Object substituteWhenNotEmpty, Object substituteWhenEmpty) {
        return (isEmpty(value))?substituteWhenEmpty:substituteWhenNotEmpty;
    }
    
    /**
     * Checks if a data object is empty. An empty object is either null or an 
     * empty string.
     * 
     * @param data      the data to check
     * @return true if the data object is not empty
     */
    public static boolean isEmpty(Object data) {
        if (data == null || "".equals(data.toString())) return true;
        return false;
    }
    
    /**
     * Checks if an object exists in an array.
     * 
     * @param item  the object to check
     * @param items an array of objects
     * @return true if the array contains the object.
     */
    public static boolean isInArray(Object item, Object[] items) {
        return isInArray(item,items, false);
    }
    
    /**
     * Checks if an object exists in an array.
     * 
     * @param item  the object to check
     * @param items an array of objects
     * @param ignoreCase indicates whether to ignore the string case or not when checking
     * @return true if the array contains the object.
     */
    public static boolean isInArray(Object item, Object[] items, boolean ignoreCase) {
        if (items == null || items.length == 0) return false;
        
        boolean result = false;
        
        int size = items.length;
        for (int i = 0; i < size; i++) {
            Object tmp = items[i];
            if (tmp != null) {
                if (item == null) continue;
                
                if (tmp instanceof String || item instanceof String) {
                    if (((String)tmp).equals(item) || 
                         (ignoreCase && ((String)tmp).equalsIgnoreCase((String)item))) {
                        result = true;
                        break;
                    }
                }
                else {
                    if (tmp == item || tmp.equals(item)) {
                        result = true;
                        break;
                    }
                }
            }
            else {
                if (item == null) {
                    result = true;
                    break;
                }
            }
        }
        
        return result;
    }
    
    /**
     * returns short version of class name. 
     * 
     * The short version of a class name does not have its package name 
     * included in the class name.
     * 
     * @return String
     */
    public static String getShortClassName(Class<?> c) {
        if (c == null) return "";
        
        String className = c.getName();
        int lastDot = className.lastIndexOf('.');
        if (lastDot != -1) className = className.substring(lastDot+1);
        
        return className;
    }
    
    /**
     * returns short version of class name. 
     * 
     * The short version of a class name does not have its package name 
     * included in the class name.
     * 
     * @return String
     */
    public static String getShortClassNameInLowerCase(Class<?> c) {
        return getShortClassName(c).toLowerCase();
    }
    
    /**
     * returns the full class name
     * 
     * @return String
     */
    public static String getFullClassName(Class<?> c) {
        if (c == null) return "";
        
        return c.getName();
    }

    public static String getCurrencyDisplay (Object amount) {
        String display = "";
        try {
          if (amount != null && !isEmpty(amount)) display = NumberFormat.getCurrencyInstance().format(amount);
        }
        catch (IllegalArgumentException ie) {
        }
        return display;
    }

    public static String getCurrencyDisplay (double amount) {
        return getCurrencyDisplay(new Double(amount));
    }

    /**
     * helper method used to safely acquire a Date
     *
     * @param date  the original date object
     * @return a safe date
     */
    public static Date getSafeDate(Date date) {
        Date d = date;
        
        if (date != null && date instanceof Timestamp) {
            try {
                d = new Date(date.getTime());
            }
            catch(NumberFormatException ex) {
            }
        }
        
        return d;
    }

    /**
     * helper method used to safely acquire a Date
     *
     * @param date  the original date object
     * @return a safe date
     */
    public static Date getSafeDate(Object date) {
        Date d = null;
        
        if (!isEmpty(date) && date instanceof Date) {
            d = (Date)date;
        }
        
        return getSafeDate(d);
    }

    /**
     * helper method used to safely acquire a float value
     *
     * @param input     input data
     * @return a float value converted from the input
     */
    public static float getSafeFloatValue(Object input) {
        Float f = getSafeFloat(input);
        return (f != null)?f.floatValue():0.0f;
    }

    /**
     * helper method used to safely acquire a double value
     *
     * @param input     input data
     * @return a double value converted from the input
     */
    public static double getSafeDoubleValue(Object input) {
        Double d = getSafeDouble(input);
        return (d != null)?d.doubleValue():0.0d;
    }

    /**
     * helper method used to safely acquire a int value
     *
     * @param input     input data
     * @return an int value converted from the input
     */
    public static int getSafeIntValue(Object input) {
        Integer i = getSafeInteger(input);
        return (i != null)?i.intValue():0;
    }

    /**
     * helper method used to safely acquire a long value
     *
     * @param input     input data
     * @return a long value converted from the input
     */
    public static long getSafeLongValue(Object input) {
        Long l = getSafeLong(input);
        return (l!= null)?l.longValue():0;
    }

    /**
     * helper method used to safely acquire a Float object
     *
     * @param input     an input BigDecimal instance
     * @return a Float object converted from the input
     */
    public static Float getSafeFloat(BigDecimal input) {
        return (input != null)?Float.valueOf(input.floatValue()):null;
    }

    /**
     * helper method used to safely acquire a Double object
     *
     * @param input     an input BigDecimal instance
     * @return a Double object converted from the input
     */
    public static Double getSafeDouble(BigDecimal input) {
        return (input != null)?Double.valueOf(input.doubleValue()):null;
    }

    /**
     * helper method used to safely acquire a Integer object
     *
     * @param input     an input BigDecimal instance
     * @return an Integer object converted from the input
     */
    public static Integer getSafeInteger(BigDecimal input) {
        return (input != null)?Integer.valueOf(input.intValue()):null;
    }

    /**
     * helper method used to safely acquire a Long object
     *
     * @param input     an input BigDecimal instance
     * @return a Long object converted from the input
     */
    public static Long getSafeLong(BigDecimal input) {
        return (input != null)?Long.valueOf(input.longValue()):null;
    }
    
    /**
     * helper method used to safely acquire a Float object
     *
     * @param input     input data
     * @return a Float object converted from the input
     */
    public static Float getSafeFloat(Object input) {
        Float f = null;
        
        if (input != null && !isEmpty(input)) {
            try {
                f = Float.valueOf(input.toString());
            }
            catch(NumberFormatException ex) {
            }
        }
        
        return f;
    }
    
    /**
     * helper method used to safely acquire a Double object
     *
     * @param input     input data
     * @return a Double object converted from the input
     */
    public static Double getSafeDouble(Object input) {
        Double d = null;
        
        if (input != null && !isEmpty(input)) {
            try {
                d = Double.valueOf(input.toString());
            }
            catch(NumberFormatException ex) {
            }
        }
        
        return d;
    }
    
    /**
     * helper method used to safely acquire a Integer object
     *
     * @param input     input data
     * @return an Integer object converted from the input
     */
    public static Integer getSafeInteger(Object input) {
        Integer i = null;
        
        if (input != null && !isEmpty(input)) {
            try {
                i = Integer.valueOf(input.toString());
            }
            catch(NumberFormatException ex) {
            }
        }
        
        return i;
    }

    /**
     * helper method used to safely acquire a Long object
     *
     * @param input     input data
     * @return a Long object converted from the input
     */
    public static Long getSafeLong(Object input) {
        Long l = null;
        
        if (input != null && !isEmpty(input)) {
            try {
                l = Long.valueOf(input.toString());
            }
            catch(NumberFormatException ex) {
            }
        }
        
        return l;
    }

    /**
     * helper method used to safely acquire a Character from the first char of 
     * the input string.
     *
     * @param sValue an input string
     * @return a Character object
     */
    public static Character getSafeCharacter(String sValue) {
        Character c = null;
        
        if (sValue != null && !sValue.equals("")) {
            try {
                c = Character.valueOf(sValue.charAt(0));
            }
            catch(NumberFormatException ex) {
            }
        }
        
        return(c);
    }

    /**
     * helper method used to safely acquire a Character from the first char of 
     * the input object.
     *
     * @param sValue an input object
     * @return a Character object
     */
    public static Character getSafeCharacter(Object sValue) {
        return getSafeCharacter(getSafeString(sValue));
    }
    
    /**
     * helper method used to safely acquire a String
     *
     * @param input the input string
     * @return a string of the input
     */
    public static String getSafeString(Object input) {
        return (input != null)?input.toString():null;
    }

    public static Calendar getTomorrowCalendar() {
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DATE, 1);
        return tomorrow;
    }

    public static Date getDate(String dateString, String format, TimeZone tz) throws Exception {
        if (dateString == null || format == null ||
             dateString.equals("") || tz == null) return null;
         
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        formatter.setTimeZone(tz);
        ParsePosition pos = new ParsePosition(0);
        return formatter.parse(dateString, pos);
    }

    public static Date getDate(String dateString, String format) throws Exception {
        return getDate(dateString, format, TimeZone.getDefault());
    }

    public static Calendar getCalendar(String dateString, String format, TimeZone tz) throws Exception {
        if (dateString == null || format == null ||
             dateString.equals("") || tz == null) return null;
             
        Calendar cal = null;
        try {
            Date date = getDate(dateString, format, tz);
            cal = Calendar.getInstance();
            cal.setTime(date);
        }
        catch (Exception ex) {
            throw new Exception("Error parsing date " + dateString + " with this format " + format);
        }
        
        return cal;
    }

    public static Calendar getCalendar(String dateString, String format) throws Exception {
        if (dateString == null || format == null ||
             dateString.equals("")) return null;
             
        Calendar cal = null;
        try {
            Date date = getDate(dateString, format);
            cal = Calendar.getInstance();
            cal.setTime(date);
        }
        catch (Exception ex) {
            throw new Exception("Error parsing date " + dateString + " with this format " + format);
        }
        
        return cal;
    }

    public static Timestamp getTimestamp(String dateString, String format) throws Exception {
        Date date =  getDate(dateString, format);
        if (date == null) return null;
        
        return new Timestamp(date.getTime());
    }

    public static Calendar getCurrentCalendarInGMT() throws Exception {
        return Calendar.getInstance(TimeZone.getTimeZone("GMT"));
    }

    public static String getDateString (Date date, String format) throws Exception {
        if (date == null || format == null) return null;
        
        String rtnStr = null;
        try {
            rtnStr = new SimpleDateFormat(format).format(date);
        }
        catch (Exception ex) {
            throw new Exception("Error parsing date " + date + " with this format " + format);
        }
        
        return rtnStr;
    }

    public static Date parseDateByFormat(String dateString, String format) {
        if (dateString == null || format == null ||
             dateString.equals("")) return null;
             
        SimpleDateFormat formatter = new SimpleDateFormat (format);
        ParsePosition pos = new ParsePosition(0);
        return formatter.parse(dateString, pos);
    }
    
	public static boolean getBooleanValue(Map<String, ?> inputs, String key,
			boolean defaultValue) {
		if (inputs == null) return defaultValue;
		
		boolean value = defaultValue;
		Object sValue = inputs.get(key);
		if (sValue != null
				&& ("TRUE".equalsIgnoreCase(sValue.toString())
						|| "T".equalsIgnoreCase(sValue.toString())
						|| "Y".equalsIgnoreCase(sValue.toString()) || "YES"
						.equalsIgnoreCase(sValue.toString())))
			value = true;
		return value;
	}
    
    public static boolean getBooleanValueForKey(Map<String, ?> inputs, String key) {
        return getBooleanValue(inputs, key, false);
    }
    
    public static int getIntValue(Map<String, ?> inputs, String key, int defaultValue) {
    	if (inputs == null) return defaultValue;
    	
        int value = defaultValue;
        try {
            Object tmp = inputs.get(key);
            if (tmp != null) value = Integer.parseInt(tmp.toString());
        }
        catch(Exception ex) {
            value = defaultValue;
        }
        return value;
    }
    
    public static int getIntValueForKey(Map<String, ?> inputs, String key) {
        return getIntValue(inputs, key, 0);
    }
    
    public static long getLongValue(Map<String, ?> inputs, String key, long defaultValue) {
    	if (inputs == null) return defaultValue;
    	
    	long value = defaultValue;
        try {
            Object tmp = inputs.get(key);
            if (tmp != null) value = Long.parseLong(tmp.toString());
        }
        catch(Exception ex) {
            value = defaultValue;
        }
        return value;
    }
    
    public static long getLongValueForKey(Map<String, ?> inputs, String key) {
        return getLongValue(inputs, key, 0L);
    }
    
    public static String getStringValue(Map<String, ?> inputs, String key, String defaultValue) {
    	if (inputs == null) return defaultValue;
    	
        String value = defaultValue;
        Object sValue = inputs.get(key);
        if (sValue != null) value = sValue.toString();
        return value;
    }
    
    public static String getStringValueForKey(Map<String, ?> inputs, String key) {
        return getStringValue(inputs, key, (String)null);
    }
    
    /**
     * Returns a MD5 digest string of the input string.
     * 
     * @param input the input string
     * @return the MD5 digest string
     */
	public static String md5(String input) {
        try
        {
            MessageDigest md = MessageDigest.getInstance("MD5");
            BigInteger number = new BigInteger(1, md.digest(input.getBytes()));
            String hashtext = number.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }
        catch(NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Creates a copy of string array.
     * 
     * @param array an int array
     * @return a cloned int array
     */
    public static int[] cloneArray(int[] array) {
    	if (array == null) return null;
    	int[] a = new int[array.length];
    	for (int i = 0; i < array.length; i++) {
    		a[i] = array[i];
    	}
    	return a;
    }
    
    /**
     * Creates a copy of string array.
     * 
     * @param array a String array
     * @return a cloned String array
     */
    public static String[] cloneArray(String[] array) {
    	if (array == null) return null;
    	String[] a = new String[array.length];
    	for (int i = 0; i < array.length; i++) {
    		a[i] = array[i];
    	}
    	return a;
    }
}
