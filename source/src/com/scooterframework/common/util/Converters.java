/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.common.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Converters class has all kinds of convert methods.
 * 
 * @author (Fei) John Chen
 */
public class Converters {

    /**
     * Converts name list string to List object
     * 
     * The default delimiter string to separate name-value pairs is ",|&". 
     * 
     * String name list string has the following format: 
     *          firstName, lastName, age,...
     *       or firstName|lastName|age|...
     *       or firstName&lastName&age&...
     * 
     */
    public static List convertStringToList(String nameString) {
        return convertStringToList(nameString, ",|&");
    }

    /**
     * Converts name list string to List object
     * 
     * The default delimiter string to separate name-value pairs is ",|&". 
     * 
     * String name list string has the following format: 
     *          firstName, lastName, age,...
     *       or firstName|lastName|age|...
     *       or firstName&lastName&age&...
     * 
     */
    public static List convertStringToList(String nameString, boolean allowTrim) {
        return convertStringToList(nameString, ",|&", true);
    }
    
    /**
     * Converts name list string separated by a delimiter to List object
     * 
     * String name list string has the following format: 
     *          firstName, lastName, age,...
     *       or firstName|lastName|age|...
     * 
     */
    public static List convertStringToList(String nameString, String delimiter) {
    	return convertStringToList(nameString, delimiter, true);
    }
    
    /**
     * Converts name list string separated by a delimiter to List object
     * 
     * String name list string has the following format: 
     *          firstName, lastName, age,...
     *       or firstName|lastName|age|...
     * 
     */
    public static List convertStringToList(String nameString, String delimiter, boolean allowTrim) {
        if (nameString == null || "".equals(nameString.trim())) return new ArrayList();
        
        StringTokenizer st = new StringTokenizer(nameString, delimiter);
        String name = "";
        ArrayList dataList = new ArrayList();
        while (st.hasMoreTokens()) {
            name = (allowTrim)?st.nextToken().trim():st.nextToken();
            dataList.add(name);
        }
        
        //add the original string to the return list if nothing is splited. 
        if (dataList.size() == 0) dataList.add(nameString);
        
        return dataList;
    }
    
    /**
     * Converts name list string to List object which only retains unique items.
     * 
     * The default delimiter string to separate name-value pairs is ",|&". 
     * 
     * String name list string has the following format: 
     *          firstName, lastName, age,...
     *       or firstName|lastName|age|...
     *       or firstName&lastName&age&...
     * 
     */
    public static List convertStringToUniqueList(String nameString) {
        return convertStringToUniqueList(nameString, ",|&");
    }
    
    /**
     * Converts name list string separated by a delimiter to List object which 
     * only retains unique items.
     * 
     * String name list string has the following format: 
     *          firstName, lastName, age,...
     *       or firstName|lastName|age|...
     * 
     */
    public static List convertStringToUniqueList(String nameString, String delimiter) {
        if (nameString == null || "".equals(nameString.trim())) return new ArrayList();
        
        StringTokenizer st = new StringTokenizer(nameString, delimiter);
        String name = "";
        ArrayList dataList = new ArrayList();
        while (st.hasMoreTokens()) {
            name = st.nextToken().trim();
            if (!dataList.contains(name)) dataList.add(name);
        }
        
        //add the original string to the return list if nothing is splited. 
        if (dataList.size() == 0) dataList.add(nameString);
        
        return dataList;
    }
    
    /**
     * Converts name list string to Set object
     * 
     * The default delimiter string to separate name-value pairs is ",|&". 
     * 
     * String name list string has the following format: 
     *          firstName, lastName, age,...
     *       or firstName|lastName|age|...
     *       or firstName&lastName&age&...
     * 
     */
    public static Set convertStringToSet(String nameString) {
        return convertStringToSet(nameString, ",|&");
    }
    
    /**
     * Converts name list string to Set object
     * 
     * The default delimiter string to separate name-value pairs is ",|&". 
     * 
     * String name list string has the following format: 
     *          firstName, lastName, age,...
     *       or firstName|lastName|age|...
     *       or firstName&lastName&age&...
     * 
     */
    public static Set convertStringToSet(String nameString, boolean allowTrim) {
        return convertStringToSet(nameString, ",|&", allowTrim);
    }
    
    /**
     * Converts name list string separated by a delimiter to Set object
     * 
     * String name set string has the following format: 
     *          firstName, lastName, age,...
     *       or firstName|lastName|age|...
     * 
     */
    public static Set convertStringToSet(String nameString, String delimiter) {
        return convertStringToSet(nameString, delimiter, true);
    }
    
    /**
     * Converts name list string separated by a delimiter to Set object
     * 
     * String name set string has the following format: 
     *          firstName, lastName, age,...
     *       or firstName|lastName|age|...
     * 
     */
    public static Set convertStringToSet(String nameString, String delimiter, boolean allowTrim) {
        if (nameString == null || "".equals(nameString.trim())) return new HashSet();
        
        StringTokenizer st = new StringTokenizer(nameString, delimiter);
        String name = "";
        Set dataSet = new HashSet();
        while (st.hasMoreTokens()) {
            name = (allowTrim)?st.nextToken().trim():st.nextToken();
            dataSet.add(name);
        }
        
        //add the original string to the return list if nothing is splited. 
        if (dataSet.size() == 0) dataSet.add(nameString);
        
        return dataSet;
    }
    
    /**
     * Converts a string of name and value pairs, separated by either comma, 
     * or vertical slash or space, to a Map object.
     * 
     * Name and value strings are separated by "=" sign. 
     * 
     * The default delimiter string to separate name-value pairs is ",|&". 
     * 
     * String nameValuePairs has the following format: 
     *          firstName=John, lastName=Doe, age=10,...
     *       or firstName=John|lastName=Doe|age=10|...
     *       or firstName=John&lastName=Doe&age=10&...
     * 
     */
    public static Map convertStringToMap(String nameValuePairs) {
        return convertStringToMap(nameValuePairs, ",|&");
    }
    
    /**
     * Converts a string of name and value pairs separated by delimiter string 
     * to a Map object.
     * 
     * Name and value strings are separated by "=" sign. 
     * 
     * String nameValuePairs has the following format: 
     *          firstName=John, lastName=Doe, age=10,...
     *       or firstName=John|lastName=Doe|age=10|...
     * 
     */
    public static Map convertStringToMap(String nameValuePairs, String propertyDelimiter) {
        return convertStringToMap(nameValuePairs, "=", propertyDelimiter);
    }
    
    /**
     * <p>
     * Converts a string of name and value pairs separated by delimiter string 
     * to a Map object.</p>
     * 
     * Example: 
     * An example property string may be like the following:
     * <pre>
     * condition : role_type = 1, id = 100; orderby : first_name desc
     * </pre>
     * 
     * <p>Here the nameValueSpliter is ":", and the propertyDelimiter is ";".</p>
     * 
     * @param nameValuePairs a string of properties
     * @param nameValueSpliter a short string that separates name and value elements in a pair
     * @param propertyDelimiter a char that separates pairs in a string line
     * @return properties 
     */
    public static Map convertStringToMap(String nameValuePairs, String nameValueSpliter, String propertyDelimiter) {
        if (nameValuePairs == null || "".equals(nameValuePairs.trim())) return new HashMap();
        
        int spliterLength = nameValueSpliter.length();
        
        StringTokenizer st = new StringTokenizer(nameValuePairs, propertyDelimiter);
        String name = "";
        String value = "";
        Map dataMap = new HashMap();
        while (st.hasMoreTokens()) {
            String tmp = st.nextToken().trim();
            int equalIndex = tmp.indexOf(nameValueSpliter);
            if (equalIndex == -1) continue;
            name = tmp.substring(0, equalIndex).trim();
            value = tmp.substring(equalIndex + spliterLength).trim();
            dataMap.put(name, value);
        }
        
        return dataMap;
    }
    
    /**
     * <p>
     * Converts a string of name and value pairs separated by delimiter string 
     * to a Properties object.</p>
     * 
     * Example: 
     * An example property string may be like the following:
     * <pre>
     * condition : role_type = 1, id = 100; orderby : first_name desc
     * </pre>
     * 
     * <p>Here the nameValueSpliter is ":", and the propertyDelimiter is ";".</p>
     * 
     * @param nameValuePairs a string of properties
     * @param nameValueSpliter a short string that separates name and value elements in a pair
     * @param propertyDelimiter a char that separates pairs in a string line
     * @return properties 
     */
    public static Properties convertStringToProperties(String nameValuePairs, String nameValueSpliter, String propertyDelimiter) {
        if (nameValuePairs == null || "".equals(nameValuePairs.trim())) return new Properties();
        
        Properties dataProperties = new Properties();
        Map dataMap = convertStringToMap(nameValuePairs, nameValueSpliter, propertyDelimiter);
        Iterator it = dataMap.keySet().iterator();
        while(it.hasNext()) {
            String key = (String)it.next();
            String value = (String)dataMap.get(key);
            dataProperties.put(key, value);
        }
        
        return dataProperties;
    }
    
    /**
     * Converts a string separated by a delimiter to a string array.
     */
    public static String[] convertStringToStringArray(String s, String delimiter) {
        return convertStringToStringArray(s, delimiter, true);
    }
    
    /**
     * Converts a string separated by a delimiter to a string array.
     */
    public static String[] convertStringToStringArray(String s, String delimiter, boolean allowTrim) {
        return convertListToStringArray(convertStringToList(s, delimiter, allowTrim));
    }
    
    /**
     * Converts sql option string which contains name and value pairs to a 
     * Map object.
     * 
     * In an option string, each name-value pair is separated by ';' 
     * character, while within each name-value pair, name and value strings 
     * are separated by ':' character. 
     * 
     * For example, an option string like the following 
     * <blockquote><pre>
     *      conditions_sql: id in (1, 2, 3); include: category, user; 
     *      order_by: first_name, salary desc; cascade: delete
     * </pre></blockquote>
     * 
     * will be converted to a HashMap with the following entries:
     * <blockquote><pre>
     *      key                 value
     *      --------------      -----
     *      conditions_sql  =>  id in (1, 2, 3)
     *      include         =>  category, user
     *      order_by        =>  first_name, salary desc
     *      cascade         =>  delete
     * </pre></blockquote>
     */
    public static Map convertSqlOptionStringToMap(String options) {
        return convertStringToMap(options, ":", ";");
    }
    
    
    
    /**
     * Converts a Map object to string.
     * 
     * For example, if the key/value pairs in the map are 
     * {title=test page, shape=rect}, 
     * and the pairSpliter = ":", and the separator = ";", 
     * 
     * the converted string will be
     * title:test page;shape:rect
     */
    public static String convertMapToString(Map map, 
                    String pairSpliter, String separator) {
        return convertMapToString(map, null, pairSpliter, separator, false);
    }
    
    /**
     * Converts a Map object to string. The list filters contains allowable keys. 
     * If filters is null, all keys are allowed. 
     * 
     * For example, if the key/value pairs in the map are 
     * {title=test page, shape=rect}, 
     * and the pairSpliter = ":", and the separator = ";", 
     * 
     * the converted string will be
     * title:"test page";shape:"rect" if doubleQuoteValue = true
     * or
     * title:test page;shape:rect if doubleQuoteValue = false
     */
    public static String convertMapToString(Map map, List filters, 
                    String pairSpliter, String separator, boolean doubleQuoteValue) {
        if (map == null || map.size() == 0) return "";
        
        String returnStr = "";
        StringBuffer sb = new StringBuffer();
        Iterator it = map.keySet().iterator();
        while (it.hasNext()) {
            Object key = it.next();
            if (filters == null || filters.contains(key)) {
                Object value = map.get(key);
                if (doubleQuoteValue) value = "\"" + value + "\"";
                sb.append(key).append(pairSpliter).append(value).append(separator);
            }
        }
        returnStr = sb.toString();
        
        //remove the last separator
        if (returnStr.endsWith(separator)) {
            returnStr = returnStr.substring(0, returnStr.lastIndexOf(separator));
        }
        return returnStr;
    }
    
    /**
     * converts a Map object to URL-like string.
     * 
     * The final url-like string has the following format: 
     *          firstName=John&lastName=Doe&age=10...
     * 
     */
    public static String convertMapToUrlString(Map map) {
        return convertMapToString(map, null, "=", "&", false);
    }
    
    /**
     * Converts a string array to list.
     * 
     * @param values string array
     * @return a list of string values
     */
    public static List convertArrayToList(String[] values) {
        if (values == null || values.length == 0) return new ArrayList();
        List l = new ArrayList();
        for (int i = 0; i < values.length; i++) {
            l.add(values[i]);
        }
        return l;
    }
    
    /**
     * Converts a list to a string array. Each item in the list is converted to 
     * a string if the item is not null.
     * 
     * @param values list of objects
     * @return array of strings
     */
    public static String[] convertListToStringArray(List values) {
        if (values == null || values.size() == 0) return null;
        int total = values.size();
        String[] results = new String[total];
        for (int i = 0; i < total; i++) {
            Object o = values.get(i);
            results[i] = (o != null)?o.toString():null;
        }
        return results;
    }
    
    /**
     * Converts a list to an array.
     * 
     * @param values list of objects
     * @return array of objects
     */
    public static Object[] convertListToArray(List values) {
        if (values == null || values.size() == 0) return null;
        int total = values.size();
        Object[] results = new Object[total];
        for (int i = 0; i < total; i++) {
            results[i] = values.get(i);
        }
        return results;
    }
    
    /**
     * Returns a Java class-name-like string.
     * 
     * <pre>
     * Examples: 
     *   "admin/posts"  --> "admin.Posts"
     *   "posts"        --> "Posts"
     *   "/posts"       --> "Posts"
     *   "role_users"   --> "RoleUsers"
     * </pre>
     * 
     * @param word
     * @return a Java class-name-like string
     */
    public static String convertToJavaClassLikeString(String word) {
        String clazz = word;
        if (word.startsWith("/")) clazz = word.substring(1);
        clazz = clazz.replace('/', '.');
        int lastDot = clazz.lastIndexOf('.');
        if (lastDot != -1) {
            clazz = clazz.substring(0, lastDot + 1) + WordUtil.camelize(clazz.substring(lastDot + 1));
        }
        else {
            clazz = WordUtil.camelize(clazz);
        }
        return clazz;
    }
    
    /**
     * <tt>convertCharSet</tt> converts a string from one character set to another.
     * 
     * @param s the string to be converted.
     * @param fromCharSet the character set to convert from.
     * @param toCharSet the character set to convert to.
     * @return a new string in a new character char set.
     */
    public static String convertCharSet(String s, String fromCharSet, String toCharSet) {
        if (s == null || s.length() == 0) return s;
        
        String newstr = null;
        try {
            byte[] bs = s.getBytes(fromCharSet);
            newstr = new String(bs, toCharSet);
        }
        catch(UnsupportedEncodingException ex) {
            throw new IllegalArgumentException("Failed to conver string [" + s + 
            "] from charset " + fromCharSet + " to " + toCharSet + 
            " because of unsupported encoding: " + ex.getMessage());
        }
        catch(Exception ex) {
            throw new IllegalArgumentException("Failed to conver string [" + s + 
            "] from charset " + fromCharSet + " to " + toCharSet + 
            " because " + ex.getMessage());
        }
        
        return newstr;
    }
    
    /**
     * Converts a string to uppercase.
     * 
     * @param s
     * @return a string in uppercase.
     */
    public static String toUpperCase(String s) {
        return (s != null)?s.toUpperCase():s;
    }
    
    /**
     * Converts to uppercase.
     * 
     * @param o an object
     */
    public static Object toUpperCase(Object o) {
        if (o == null) return o;
        if (o instanceof String) return toUpperCase((String)o);
        if (o instanceof Collection) {
            return toUpperCase((Collection)o);
        }
        return o;
    }
    
    /**
     * Converts to uppercase.
     * 
     * @param items a collection
     */
    public static Collection toUpperCase(Collection items) {
        if (items == null || items.isEmpty()) return items;
        
        Collection c = null;
        if (items instanceof List) {
        	c = new ArrayList();
        }
        else if (items instanceof Set) {
        	c = new HashSet();
        }
        else {
        	c = new ArrayList();
        }
        
        Iterator it = items.iterator();
        while(it.hasNext()) {
            c.add(toUpperCase(it.next()));
        }
        return c;
    }
    
    /**
     * Reverses key/value pairs of a map.
     * 
     * A new map instance is returned with the value field of the original map 
     * as key and the key field of the original map as value.
     * 
     * @param m the original map
     * @return a new map
     */
    public static Map reverseMap(Map m) {
        if (m == null) return m;
        Map n = new HashMap(m.size());
        Iterator it = m.keySet().iterator();
        while(it.hasNext()) {
            Object key = it.next();
            Object value = m.get(key);
            n.put(value, key);
        }
        return n;
    }
}
