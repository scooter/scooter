/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.common.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * StringUtil class has methods on string manipulation.
 * 
 * @author (Fei) John Chen
 */
public class StringUtil {

    /**
     * Returns the first few characters of a string. The rest is replaced by 
     * "...".
     * 
     * @param s         the original string
     * @param length    the number of characters to be returned.
     * @return the first <tt>length</tt> characters of a string
     */
    public static String preview(String s, int length) {
        return preview(s, length, "...");
    }

    /**
     * Returns the first few characters of a string. The rest is replaced by 
     * <tt>tail</tt>.
     * 
     * @param s         the original string
     * @param length    the number of characters to be returned.
     * @param tail      the symbol of the remaining part of the string.
     * @return the first <tt>length</tt> characters of a string
     */
    public static String preview(String s, int length, String tail) {
        if (s == null || s.length() <= length) return s;
        return s.substring(0, length-1) + " " + tail;
    }

    /**
     * Returns length of a string. If the string is null, return 0.
     * 
     * @param data input string
     * @return 0 if the data is null or the length of the data string.
     */
    public static int strlen(String data) {
        return (data == null)?0:data.length();
    }
    
    /**
     * Returns a string with all alphabetic characters converted to lowercase.
     * 
     * @param data input string
     * @return a string in lowercse.
     */
    public static String strToLower(String data) {
        return (data == null)?"":data.toLowerCase();
    }
    
    /**
     * Returns a string with all alphabetic characters converted to uppercse.
     * 
     * @param data input string
     * @return a string in uppercase.
     */
    public static String strToUpper(String data) {
        return (data == null)?"":data.toUpperCase();
    }
    
    /**
     * Checks if a string starts with lower case char.
     * @param s the string to check
     * @return true if the string starts with lower case char.
     */
    public static boolean startsWithLowerCaseChar(String s) {
    	int c0 = (int)s.charAt(0);
    	return (c0 >= 97 && c0 <= 122)?true:false; 
    }
    
    /**
     * Replaces old symbols with an empty string.
     * 
     * <pre>
     * Examples:
     *      replace("123te123ch123", "123");    would return "tech"
     * </pre>
     * 
     * @param data the original string
     * @param oldSymbol the substring to be replaced
     * @return a replaced string
     */
    public static String replace(String data, String oldSymbol) {
        return replace(data, oldSymbol, "");
    }
    
    /**
     * Replaces old symbols with new symbols in a string.
     * 
     * <pre>
     * Examples:
     *      replace("123te123ch123", "123", "X"); would return "XteXchX"
     * </pre>
     * 
     * @param s the original string
     * @param oldSymbol the substring to be replaced
     * @param newSymbol the replacement substring
     * @return a replaced string
     */
    public static String replace(String s, String oldSymbol, String newSymbol) {
        // In a string replace one substring with another
        if (s == null || s.indexOf(oldSymbol) == -1) return s;
        
        int oldLength = oldSymbol.length();
        String result = "";
        int i = s.indexOf(oldSymbol,0);
        int lastIndex = 0;
        while (i != -1) {
            result += s.substring(lastIndex,i) + newSymbol;
            lastIndex = i + oldLength;
            i = s.indexOf(oldSymbol, lastIndex);
        }
        result += s.substring(lastIndex);
        
        return result;
    }
    
    /**
     * Replaces the last occurance of an old symbol with a new symbol.
     * 
     * @param s the original string
     * @param oldSymbol the substring to be replaced
     * @param newSymbol the replacement substring
     * @return a replaced string
     */
    public static String replaceLast(String s, String oldSymbol, String newSymbol) {
        if (s == null || s.indexOf(oldSymbol) == -1) return s;
        
        int lastIndex = s.lastIndexOf(oldSymbol);
        int oldLength = oldSymbol.length();
        String result = s.substring(0, lastIndex) + newSymbol + 
                        s.substring(lastIndex + oldLength);
        
        return result;
    }
    
    /**
     * Removes all occurances of the characters in <tt>charsToBeremoved</tt>.
     * 
     * <pre>
     * Examples:
     *      remove("abcabc1234567", "abc");  	would return "1234567"
     * </pre>
     * 
     * @param message the string to be processed
     * @param charsToBeremoved a string containing characters to be removed
     * @return a new string
     */
    public static String remove(String message, String charsToBeremoved) {
        return translate(message, charsToBeremoved, null);
    }
    
    /**
     * <tt>translate</tt> method replaces a sequence of characters in a 
     * string with another set of characters. However, it replaces a single 
     * character at a time. For example, it will replace the 1st character in 
     * the <tt>charsToBeReplaced</tt> with the 1st character in the 
     * <tt>replacementChars</tt>. Then it will replace the 2nd character in 
     * the <tt>charsToBeReplaced</tt> with the 2nd character in the 
     * <tt>replacementChars</tt>, and so on.
     * 
     * The length of <tt>charsToBeReplaced</tt> should be the same as 
     * <tt>replacementChars</tt>. If the <tt>replacementChars</tt> 
     * string is empty, then all occurances of the characters in 
     * <tt>charsToBeReplaced</tt> are replaced with empty character.
     * 
     * <pre>
     * Examples:
     *      translate("Hello!", "eo", "oa");  	would return "Holla!"
     *      translate("123456", "23", "98");  	would return "198456"
     *      translate("abcabc1234567", "abc", "000");  	would return "0000001234567".
     * </pre>
     * 
     * @param message the string to be translated
     * @param charsToBeReplaced characters to be replaced
     * @param replacementChars characters to be used as replacement
     * @return a new translated string
     */
    public static String translate(String message, String charsToBeReplaced, String replacementChars) {
        if (message == null) return message;
        
        boolean useEmpty = false;
        if (replacementChars == null || "".equals(replacementChars)) useEmpty = true;
        
        if (!useEmpty && (replacementChars.length() != charsToBeReplaced.length())) 
            throw new IllegalArgumentException("The replacement characters are not of the same length of the replaced characters.");
        
        String result = "";
        int length = message.length();
        for (int i=0; i<length; i++) {
            char c = message.charAt(i);
            int index = charsToBeReplaced.indexOf(c);
            if (index != -1) {
                if (!useEmpty) {
                    result = result + replacementChars.charAt(index);
                }
            }
            else {
                result = result + c;
            }
        }
        
        return result;
    }
    
    /**
     * Removes slash from a string.
     * 
     * @param s      the original string
     * @return a string without slash
     */
    public static String stripSlashes(String s) {
        if (s == null || s.indexOf('/') == -1) return s;
        return replace(s, "/", "");
    }
    
    /**
     * Converts a string of name and value pairs, separated by a separator to 
     * map. The separator could be comma, or vertical slash or space, etc.
     * 
     * <pre>
     * String data has the following format: 
     *          firstName=John, lastName=Doe, age=10,...
     *       or firstName=John|lastName=Doe|age=10|...
     * </pre>
     * 
     * @param data
     * @param separator
     * @return Map
     */
    public Map explode(String data, String separator) {
        return Converters.convertStringToMap(data, separator);
    }
    
    /**
     * Converts a list of strings into a long string separated by glue.
     * 
     * @param data string array
     * @param glue
     * @return String
     */
    public static String implode(String[] data, String glue) {
        if (data == null || data.length ==0) return "";
        
        StringBuffer sb = new StringBuffer();
        int total = data.length;
        for (int i = 0; i < total; i++) {
            String item = data[i];
            sb.append(item).append(glue);
        }
        
        String result = sb.toString();
        if (result.endsWith(glue)) result = result.substring(0, result.lastIndexOf(glue));
        return result;
    }
    
    /**
     * Converts a list of strings into a long string separated by glue.
     * 
     * @param data a list of strings
     * @param glue
     * @return String
     */
    public static String implode(List data, String glue) {
        if (data == null || data.size() ==0) return "";
        
        StringBuffer sb = new StringBuffer();
        Iterator it = data.iterator();
        while(it.hasNext()) {
            String item = (String)it.next();
            sb.append(item).append(glue);
        }
        
        String result = sb.toString();
        if (result.endsWith(glue)) result = result.substring(0, result.lastIndexOf(glue));
        return result;
    }
    
    /**
     * Converts map into a long string separated by glue.
     * 
     * @param data a map
     * @param glue
     * @return String
     */
    public static String implode(Map data, String glue) {
        if (data == null || data.size() ==0) return "";
        
        StringBuffer sb = new StringBuffer();
        Iterator it = data.keySet().iterator();
        while(it.hasNext()) {
            String name = (String)it.next();
            String value = (String)data.get(name);
            sb.append(name).append("=").append(value).append(glue);
        }
        
        String result = sb.toString();
        if (result.endsWith(glue)) result = result.substring(0, result.lastIndexOf(glue));
        return result;
    }

    /**
     * Converts object array to a big string separated by comma.
     * 
     * @param words
     * @return a flatened string
     */
    public static String flatenArray(Object[] words) {
        return flatenArray(words, ",");
    }
    
    
    /**
     * Converts object array to a big string separated by spliter.
     * 
     * @param words
     * @param spliter
     * @return a flatened string
     */
    public static String flatenArray(Object[] words, String spliter) {
        return flatenArray("", words, spliter);
    }
    
    /**
     * Converts object array to a big string separated by spliter.
     * 
     * @param prefix prefix before each element
     * @param words an array of elements
     * @param spliter a string which separates each element
     * @return a flatened string
     */
    public static String flatenArray(String prefix, Object[] words, String spliter) {
        if (words == null) return null;
        
        String bigString = "";
        int length = words.length;
        for (int i=0; i<length; i++) {
            bigString += prefix + words[i] + spliter;
        }
        
        bigString = removeLastToken(bigString, spliter);
        
        return bigString;
    }
    
    /**
     * Split a string into a list of substrings separated by spliter.
     * 
     * @param bigString  The string to be split
     * @param spliter    The spliter
     * @return a list of strings
     */
    public static List splitString(String bigString, String spliter) {
        if (bigString == null || "".equals(bigString.trim())) return new ArrayList();
        
        List dataList = new ArrayList();
        int spliterLen = spliter.length();
        int index = bigString.indexOf(spliter);
        
        while(index != -1) {
            String frontString = bigString.substring(0, index);
            dataList.add(frontString);
            
            bigString = bigString.substring(index+spliterLen);
            index = bigString.indexOf(spliter);
        }
        
        if (!bigString.equals("")) dataList.add(bigString);
        
        //add the original string to the return list if nothing is splited. 
        if (dataList.size() == 0) dataList.add(bigString);
        
        return dataList;
    }

    /**
     * Check if the string array contains the item. String case is ignored
     * when doing the check. 
     * 
     * @param item
     * @param array
     * @return true if the string array contains the item.
     */
    public static boolean isStringInArray(String item, String[] array) {
        return isStringInArray(item, array, false);
    }

    /**
     * Check if the string array contains the item.
     * 
     * @param item
     * @param array
     * @param ignoreCase true if case is ignored when doing comparison.
     * @return true if the string array contains the item.
     */
    public static boolean isStringInArray(String item, String[] array, boolean ignoreCase) {
        if (array == null) return false;
        
        boolean result = false;
        
        int size = array.length;
        for (int i = 0; i < size; i++) {
            String tmp = array[i];
            if (tmp != null) {
                if (ignoreCase) {
                    if (tmp.equalsIgnoreCase(item)) {
                        result = true;
                        break;
                    }
                }
                else {
                    if (tmp.equals(item)) {
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
    
    public static StringBuffer removeLastToken(StringBuffer word, String token) {
        if (word == null) return word;
        
        String newWord = removeLastToken(word.toString(), token);
        return new StringBuffer(newWord);
    }
    
    public static String removeLastToken(String word, String token) {
        if (word == null) return word;
        
        String newWord = word;
        int pos = word.lastIndexOf(token);
        if (pos != -1) {
            newWord = word.substring(0, pos);
        }
        return newWord;
    }
    
    public static String reverseMapping(String mapping) {
        if (mapping == null) return null;
        
        Map m = Converters.convertStringToMap(mapping);
        StringBuffer sb = new StringBuffer();
        Iterator it = m.keySet().iterator();
        while(it.hasNext()) {
            Object key = it.next();
            sb.append(m.get(key)).append("=").append(key).append(",");
        }
        return removeLastToken(sb.toString(), ",");
    }

    public static String getValuesAsSQLNumber(Collection values) {
        if (values == null) return null;

        String listStr = "";

        int i = 0;
        Object[] data = values.toArray();
        for (i = 0; i < (values.size() - 1); i++) {
            listStr += data[i] + ", ";
        }

        if (values.size() >= 1) listStr += data[i];
        
        return listStr;
    }

    public static String getValuesAsSQLString(Collection values) {
        if (values == null) return null;

        String listStr = "";

        int i = 0;
        Object[] data = values.toArray();
        for (i = 0; i < (values.size() - 1); i++) {
            listStr += "'" + doubleSingleQuoteInString((String) data[i]) + "'"+ ", ";
        }

        listStr += "'" + doubleSingleQuoteInString((String) data[i]) + "'";
        
        return listStr;
    }

    public static String getValuesAsSQLString(String value) {
        if (value == null || value.equals("")) return value;

        StringTokenizer st = new StringTokenizer(value, ", ");
        String listStr = "";
        while (st.hasMoreTokens()) {
            String tmp = st.nextToken();
            listStr += "'" + doubleSingleQuoteInString(tmp) + "'"+ ",";
        }

        // remove the last comma
        if (listStr.length() > 0) listStr = listStr.substring(0, listStr.length()-1);
        
        return listStr;
    }

    public static String getValuesAsSQLLikeString(String field, List values, String type) {
        if (field == null || values == null || values.size() < 1 || type == null) return null;

        String listStr = "";

        int i = 0;
        listStr += " UPPER(" + field + ") LIKE '%" + doubleSingleQuoteInString((String) values.get(i)).toUpperCase() + "%' ";
        
        for (i = 1; i < values.size(); i++) {
            listStr += type + " UPPER(" + field + ") LIKE '%" + doubleSingleQuoteInString((String) values.get(i)).toUpperCase() + "%' ";
        }
        
        return listStr;
    }
    
    /**
     * Adds another single quote if there is already one in the input string.
     */
    public static String doubleSingleQuoteInString(String input) {
        if (input == null || input.equals("")) return input;
        
        String result = "";
        while(input.indexOf("'") != -1) {
            int iQ = input.indexOf("'");
            result += input.substring(0, iQ+1) + "'";
            if ((iQ + 1) <= input.length()) input = input.substring(iQ+1);
        }
        
        result += input;
        return result;
    }

    // add a back slash in front of "
    public static String parseStringForDoubleQuote(String input) {
        if (input == null || input.equals("")) return input;

        String result = "";
        while(input.indexOf("\"") != -1) {
            int iQ = input.indexOf("\"");
            result += input.substring(0, iQ) + "\\\"";
            if ((iQ + 1) <= input.length()) input = input.substring(iQ + 1);
        }

        result += input;
        return result;
    }
    
    /**
     * Converts all keys in a map to upper case. 
     * 
     * @param inputs the original map
     * @return a new map with keys in upper case.
     */
    public static Map convertKeyToUpperCase(Map inputs) {
        if (inputs == null) return null;
        Map tmp = new HashMap(inputs.size());
        Iterator it = inputs.keySet().iterator();
        while(it.hasNext()) {
            Object key = it.next();
            tmp.put(key.toString().toUpperCase(), inputs.get(key));
        }
        return tmp;
    }
}
