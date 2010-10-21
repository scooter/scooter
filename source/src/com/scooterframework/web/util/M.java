/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.web.util;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

/**
 * M (MiscHelper) class has helper methods for all other situations. 
 * 
 * 
 * @author (Fei) John Chen
 */
public class M {

    /**
     * Finds if the string has any value other than null, or an empty string.
     * 
     * @param data String 
     * @return true for if the string is null or empty.
     */
    public static boolean isEmpty(String data) {
        if (data == null || "".equals(data)) return true;
        return false;
    }

    /**
     * Finds if the data has any value other than null, including an empty 
     * string.
     * 
     * @param data Object 
     * @return true for if the data is null or empty.
     */
    public static boolean isEmpty(Object data) {
        boolean result = false;
        
        if (data == null || "".equals(data)) return true;
        
        if (data instanceof Collection) {
            result = ((Collection)data).isEmpty();
        }
        else if (data instanceof Map) {
            result = ((Map)data).isEmpty();
        }
        else if (data.getClass().isArray() && 
                 Array.getLength(data) == 0) {
            result = true;
        }
        
        return result;
    }
    
    /**
     * Finds if the data is not null or not.
     * 
     * @param data a data object
     * @return true for not null.
     */
    public static boolean exist(Object data) {
        if (data == null) return false;
        return true;
    }
    
    /**
     * Checks if the two strings are equal.
     * 
     * @param str1
     * @param str2
     * @return true if they are equal.
     */
    public static boolean isEqual(String str1, String str2) {
        if (str1 != null && str1.equals(str2)) return true;
        if (str1 == null && str2 == null) return true;
        return false;
    }
    
    /**
     * Finds if the data has any value other than null, including an empty 
     * string.
     * 
     * @param data Object 
     * @return true for having value, false for not having a value.
     */
    public static boolean isset(Object data) {
        if (data == null || "".equals(data.toString())) return false;
        return true;
    }
    
    /**
     * Finds if the data has any value other than 0.
     * 
     * @param value int 
     * @return true if the value is not 0.
     */
    public static boolean isset(int value) {
        return isset(value, 0);
    }
    
    /**
     * Finds if the data has any value other than the default.
     * 
     * @param value int 
     * @return true if the value is not the same as default.
     */
    public static boolean isset(int value, int deflt) {
        if (value == deflt) return false;
        return true;
    }
    
    /**
     * Finds if the data has any value other than 0F.
     * 
     * @param value float 
     * @return true if the value is not 0F.
     */
    public static boolean isset(float value) {
        return isset(value, 0F);
    }
    
    /**
     * Finds if the data has any value other than the default.
     * 
     * @param value float 
     * @return true if the value is not the same as default.
     */
    public static boolean isset(float value, float deflt) {
        if (value == deflt) return false;
        return true;
    }
    
    /**
     * Finds if the data has any value other than 0D.
     * 
     * @param value double 
     * @return true if the value is not 0D.
     */
    public static boolean isset(double value) {
        return isset(value, 0D);
    }
    
    /**
     * Finds if the data has any value other than the default.
     * 
     * @param value double 
     * @return true if the value is not the same as default.
     */
    public static boolean isset(double value, double deflt) {
        if (value == deflt) return false;
        return true;
    }
    
    /**
     * Finds if the data has any value other than 0L.
     * 
     * @param value long 
     * @return true if the value is not 0L.
     */
    public static boolean isset(long value) {
        return isset(value, 0L);
    }
    
    /**
     * Finds if the data has any value other than the default.
     * 
     * @param value long 
     * @return true if the value is not the same as default.
     */
    public static boolean isset(long value, long deflt) {
        if (value == deflt) return false;
        return true;
    }
    
    
    public static boolean isArray(Object o) {
        boolean status = false;
        if (o instanceof Object[]) status = true;
        return status;
    }
}
