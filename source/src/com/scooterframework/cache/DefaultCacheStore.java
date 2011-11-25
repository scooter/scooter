/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.cache;

import java.util.Collection;

import com.scooterframework.admin.EnvConfig;

/**
 * DefaultCacheStore class provides convenient access to default cache. 
 * 
 * <p>
 * The default cache provider is defined by <tt>default.cache.provider.name</tt>
 * property and the default cache name is defined by <tt>default.cache.name</tt>.
 * 
 * <p>All methods signatures are the same as those defined in <tt>Cache</tt> 
 * interface except that they are all static here.
 * 
 * <p>
 * The default cache and its provider are defined in 
 * <tt>environment.properties</tt> file.
 * 
 * @author (Fei) John Chen
 */
public class DefaultCacheStore {
	/**
	 * Returns the cache name.
	 * 
	 * @return name of the cache
	 */
	public static String getName() {
		return getDefaultCache().getName();
	}
	
	/**
	 * Returns keys in the cache
	 * 
	 * @return a collection of all keys
	 */
	public static Collection<Object> getKeys() {
		return getDefaultCache().getKeys();
	}
    
    /**
     * Returns object from cache based on <tt>key</tt>.
     * 
	 * @return the value to which this cache maps the specified key, or null 
	 * if the cache contains no mapping for this key.
     */
	public static Object get(Object key) {
		return getDefaultCache().get(key);
	}
	
	/**
	 * Stores object into cache based on <tt>key</tt>.
	 * 
	 * @return true if successful
	 */
	public static boolean put(Object key, Object obj) {
		return getDefaultCache().put(key, obj);
	}
	
	/**
	 * Removes the mapping for this key from the cache if present.
	 * @return true if successful
	 */
	public static boolean remove(Object key) {
		return getDefaultCache().remove(key);
	}
	
	/**
	 * Removes all key/value pairs from cache.
	 */
	public static void clear() {
		getDefaultCache().clear();
	}
    
    private static Cache getDefaultCache() {
    	CacheProvider cp = CacheProviderUtil.getDefaultCacheProvider();
    	if (cp == null) 
    		throw new IllegalArgumentException("Default cache provider is not set." + 
			"Set 'default.cache.provider.name' property in environment.properties file.");
    	
    	String name = EnvConfig.getInstance().getDefaultCacheName();
    	if (name == null) 
    		throw new IllegalArgumentException("Default cache name is not set. " + 
    				"Set 'default.cache.name' property in environment.properties file.");
    	return cp.getCache(name);
    }
}
