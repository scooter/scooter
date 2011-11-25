/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.cache;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.scooterframework.common.util.CurrentThreadCache;

/**
 * NamedCurrentThreadCache provides a named cache for current request thread. 
 * 
 * <p>
 * The scope of a <tt>NamedCurrentThreadCache</tt> is just the current request. 
 * If the current thread completes, all objects cached in it will be removed.</p>
 * 
 * 
 * @author (Fei) John Chen
 */
public class NamedCurrentThreadCache implements Cache {
	private String name;
	
	public NamedCurrentThreadCache(String name) {
		this.name = name;
		init();
	}
	
	private Map<Object, Object> init() {
		Map<Object, Object> map = new HashMap<Object, Object>();
		CurrentThreadCache.set(name, map);
		return map;
	}
	
	@SuppressWarnings("unchecked")
	private Map<Object, Object> getCache() {
		Map<Object, Object> map = (Map<Object, Object>) CurrentThreadCache.get(name);
		if (map == null) {
			map = init();
		}
		return map;
	}
	
	/**
	 * Returns the underlying cache.
	 * 
	 * @return the cache instance from vendor
	 */
	public Map<Object, Object> getDelegateCache() {
		return getCache();
	}
	
	/**
	 * Returns the cache name.
	 * 
	 * @return name of the cache
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns keys in the cache
	 * 
	 * @return a collection of all keys
	 */
	public Collection<Object> getKeys() {
		return getCache().keySet();
	}
    
    /**
     * Returns object from cache based on <tt>key</tt>.
     * 
	 * @return the value to which this cache maps the specified key, or null 
	 * if the cache contains no mapping for this key.
     */
	public Object get(Object key) {
		return getCache().get(key);
	}
	
	/**
	 * Stores object into cache based on <tt>key</tt>.
	 * 
	 * @return true if successful
	 */
	public boolean put(Object key, Object value) {
		getCache().put(key, value);
		return true;
	}
	
	/**
	 * Removes the mapping for this key from the cache if present.
	 * @return true if successful
	 */
	public boolean remove(Object key) {
		getCache().remove(key);
		return true;
	}
	
	/**
	 * Removes all key/value pairs from cache.
	 */
	public void clear() {
		getCache().clear();
	}
	
	/**
	 * Returns the Cache statistics.
	 */
	public Properties getStatistics() {
		throw new UnsupportedOperationException("getStatistics() is not supported for class NamedCurrentThreadCache.");
	}
}
