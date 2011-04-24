/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.common.util;

import java.util.HashMap;
import java.util.Map;

/**
 * CurrentThreadCache class is a local store for current request.
 * 
 * @author (Fei) John Chen
 */
public class CurrentThreadCache {
    private static ThreadLocal<Map<String, Object>> local = new ThreadLocal<Map<String, Object>>();

    private static Map<String, Object> getMap() {
        Map<String, Object> map = local.get();
        if (map == null) {
        	map = new HashMap<String, Object>();
        	local.set(map);
        }
        return map;
    }
    
    /**
     * Retrieves the property for a key.
     * 
     * @param key   key name of the property
     * @return the property
     */
    public static Object get(String key) {
    	return getMap().get(key);
    }
    
    /**
     * Stores a property.
     * 
     * @param key   key name of the property
     * @param value value of the property
     */
    public static void set(String key, Object value) {
        Map<String, Object> map = getMap();
        map.put(key, value);
    }
    
    /**
     * Clears all content of the cache.
     */
    public static void clear() {
        getMap().clear();
    }
    
    /**
     * Clears content of the cache associated with a key.
     */
    public static void clear(String key) {
        getMap().remove(key);
    }
}
