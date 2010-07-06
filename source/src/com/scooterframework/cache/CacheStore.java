/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.cache;

/**
 * CacheStore class provides convenient access to default cache provider.
 * 
 * All methods signatures are the same as those defined in <tt>Cache</tt> 
 * interface except that they are all static here.
 * 
 * @author (Fei) John Chen
 */
public class CacheStore {
    
    /**
     * Returns object from cache based on <tt>key</tt>.
     * 
	 * @return the value to which this cache maps the specified key, or null 
	 * if the cache contains no mapping for this key.
     */
	public static Object get(String key) {
		Cache c = CacheProviderFactory.getInstance().getDefaultCache();
		return c.get(key);
	}
	
	/**
	 * Stores object into cache based on <tt>key</tt>.
	 * 
	 * @return true if successful
	 */
	public static boolean put(String key, Object obj) {
		Cache c = CacheProviderFactory.getInstance().getDefaultCache();
		return c.put(key, obj);
	}
	
	/**
	 * Removes the mapping for this key from the cache if present.
	 * @return true if successful
	 */
	public static boolean remove(String key) {
		Cache c = CacheProviderFactory.getInstance().getDefaultCache();
		return c.remove(key);
	}
	
	/**
	 * Removes all key/value pairs from cache.
	 */
	public static void clear() {
		Cache c = CacheProviderFactory.getInstance().getDefaultCache();
		c.clear();
	}
}
