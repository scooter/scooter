/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.cache;

import java.util.Collection;
import java.util.Properties;

/**
 * Cache interface defines methods of a cache store.
 * 
 * @author (Fei) John Chen
 */
public interface Cache {
	/**
	 * Returns the cache name.
	 * 
	 * @return name of the cache
	 */
	public String getName();
	
	/**
	 * Returns keys in the cache
	 * 
	 * @return a collection of all keys
	 */
	public Collection<Object> getKeys();
    
    /**
     * Returns object from cache based on <tt>key</tt>.
     * 
	 * @return the value to which this cache maps the specified key, or null 
	 * if the cache contains no mapping for this key.
     */
	public Object get(Object key);
	
	/**
	 * Stores object into cache based on <tt>key</tt>.
	 * 
	 * @return true if successful
	 */
	public boolean put(Object key, Object value);
	
	/**
	 * Removes the mapping for this key from the cache if present.
	 * 
	 * @return true if successful
	 */
	public boolean remove(Object key);
	
	/**
	 * Removes all key/value pairs from cache.
	 */
	public void clear();
	
	/**
	 * Returns the Cache statistics.
	 */
	public Properties getStatistics();
}
