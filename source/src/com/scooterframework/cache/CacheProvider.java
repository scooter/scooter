/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.cache;

import java.util.Collection;
import java.util.Map;
import java.util.Properties;


/**
 * CacheProvider interface defines common methods of a cache provider.
 * 
 * @author (Fei) John Chen
 */
public interface CacheProvider {

	/**
	 * Key to represent cache provider <tt>configFile</tt> property.
	 */
	public static final String KEY_CACHE_PROVIDER_CONFIGFILE = "configFile";

	/**
	 * Key to represent cache provider <tt>namespace</tt> property.
	 */
	public static final String KEY_CACHE_PROVIDER_NAMESPACE = "namespace";

	/**
	 * Key to represent cache provider <tt>urls</tt> property.
	 */
	public static final String KEY_CACHE_PROVIDER_URLS = "urls";

	/**
	 * Key to represent cache provider <tt>expiresInSeconds</tt> property.
	 */
	public static final String KEY_CACHE_PROVIDER_EXPIRESINSECONDS = "expiresInSeconds";

	/**
	 * Key to represent cache provider <tt>retrieveTimeoutInSeconds</tt>
	 * property.
	 */
	public static final String KEY_CACHE_PROVIDER_REQUESTTIMEOUTINSECONDS = "requestTimeoutInSeconds";

	/**
	 * Returns the cache provider name.
	 */
	public String getName();

	/**
	 * Returns the implementation cache provider class name.
	 */
	public String getProviderClassName();

	/**
	 * Returns the namespace
	 */
	public String getNamespaceProperty();
	
	/**
	 * Returns the urls if defined
	 */
	public String getUrlsProperty();

	/**
	 * Returns the expiresInSeconds
	 */
	public int getExpiresInSecondsProperty();

	/**
	 * Returns the requestTimeoutInSeconds
	 */
	public int getRequestTimeoutInSecondsProperty();
	
	/**
	 * Returns properties defined for the cache provider.
	 */
	public Properties getProperties();
	
	/**
	 * Returns value of a property.
	 * 
	 * @param name  property name
	 * @return value of the property.
	 */
	public String getProperty(String name);
	
	/**
	 * Returns the cache for the name.
	 * 
	 * @param name  name of the cache
	 * @return the cache associated with the name
	 */
	public Cache getCache(String name);
	
	/**
	 * Returns a list of cache names.
	 */
	public Collection<String> getCacheNames();
	
	/**
	 * Returns statistics of a cache.
	 */
	public Properties getStatistics(String name);
	
	/**
	 * Returns statistics of all caches.
	 */
	public Map<String, Properties> getStatistics();
}
