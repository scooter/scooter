/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.cache;

import java.util.Properties;

import com.scooterframework.admin.Plugin;

/**
 * CacheProvider class defines common methods of a cache provider.
 * 
 * CacheProvier acts as a singleton instance if it is created by
 * <tt>CacheObjectFactory</tt> class.
 * 
 * @author (Fei) John Chen
 */
public abstract class CacheProvider extends Plugin implements Cache {

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

	private String namespace;
	private String urls;
	private int expiresInSeconds;
	private int requestTimeoutInSeconds;

	protected CacheProvider(Properties p) {
		super(p);
		init();
	}

	private void init() {
		namespace = getProperty(KEY_CACHE_PROVIDER_NAMESPACE, "");
		urls = getProperty(KEY_CACHE_PROVIDER_URLS);

		String sExpiresInSeconds = getProperty(KEY_CACHE_PROVIDER_EXPIRESINSECONDS);
		if (sExpiresInSeconds != null) {
			expiresInSeconds = Integer.parseInt(sExpiresInSeconds);
		}

		String sRequestTimeoutInSeconds = getProperty(KEY_CACHE_PROVIDER_REQUESTTIMEOUTINSECONDS);
		if (sRequestTimeoutInSeconds != null) {
			requestTimeoutInSeconds = Integer
					.parseInt(sRequestTimeoutInSeconds);
		}
	}

	/**
	 * @return the providerClassName
	 */
	public String getProviderClassName() {
		return super.getPluginClassName();
	}

	/**
	 * @return the namespace
	 */
	public String getNamespace() {
		return namespace;
	}

	/**
	 * @return the urls
	 */
	public String getUrls() {
		return urls;
	}

	/**
	 * @return the expiresInSeconds
	 */
	public int expiresInSeconds() {
		return expiresInSeconds;
	}

	/**
	 * @return the requestTimeoutInSeconds
	 */
	public int requestTimeoutInSeconds() {
		return requestTimeoutInSeconds;
	}

	private String getFullKey(String key) {
		return (namespace != null && !"".equals(namespace)) ? (namespace + "@" + key)
				: key;
	}

	/**
	 * Returns object from cache based on <tt>key</tt>.
	 * 
	 * @return the value to which this cache maps the specified key, or null if
	 *         the cache contains no mapping for this key.
	 */
	public Object get(String key) {
		return getObject(getFullKey(key));
	}

	/**
	 * Stores object into cache based on <tt>key</tt>.
	 * 
	 * @return true if successful
	 */
	public boolean put(String key, Object value) {
		return putObject(getFullKey(key), value);
	}

	/**
	 * Removes the mapping for this key from the cache if present.
	 * 
	 * @return true if successful
	 */
	public boolean remove(String key) {
		return removeObject(getFullKey(key));
	}

	/**
	 * Removes all key/value pairs from cache.
	 */
	public void clear() {
		clearObjects();
	}

	/**
	 * Retrieves object from cache based on <tt>key</tt>. The <tt>key</tt> here
	 * is a combination of <tt>namespace</tt> and key from request.
	 * 
	 * Subclass must implement this method.
	 * 
	 * @param key
	 * @return object
	 */
	protected abstract Object getObject(String key);

	/**
	 * Puts object into cache based on <tt>key</tt>. The <tt>key</tt> here is a
	 * combination of <tt>namespace</tt> and key from request.
	 * 
	 * Subclass must implement this method.
	 * 
	 * @param key
	 *            the key of the corresponding value
	 * @param value
	 *            the object to be cached
	 * @return true if successfully stored
	 */
	protected abstract boolean putObject(String key, Object value);

	/**
	 * Removes object from cache based on <tt>key</tt>. The <tt>key</tt> here is
	 * a combination of <tt>namespace</tt> and key from request.
	 * 
	 * Subclass must implement this method.
	 * 
	 * @param key
	 * @return object
	 */
	protected abstract boolean removeObject(String key);

	/**
	 * Removes all objects from cache
	 */
	protected abstract void clearObjects();
}
