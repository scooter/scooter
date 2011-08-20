/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.cache;

/**
 * Cache interface defines methods of a cache store.
 * 
 * @author (Fei) John Chen
 */
public interface Cache {
    
    /**
     * Returns object from cache based on <tt>key</tt>.
     * 
	 * @return the value to which this cache maps the specified key, or null 
	 * if the cache contains no mapping for this key.
     */
	public Object get(String key);
	
	/**
	 * Stores object into cache based on <tt>key</tt>.
	 * 
	 * @return true if successful
	 */
	public boolean put(String key, Object value);
	
	/**
	 * Removes the mapping for this key from the cache if present.
	 * @return true if successful
	 */
	public boolean remove(String key);
	
	/**
	 * Removes all key/value pairs from cache.
	 */
	public void clear();
}
