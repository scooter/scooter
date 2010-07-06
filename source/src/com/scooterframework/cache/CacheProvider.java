/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.cache;

import java.util.Properties;

import com.scooterframework.common.logging.LogUtil;

/**
 * CacheProvider class defines common methods of a cache provider. 
 * 
 * CacheProvier acts as a singleton instance if it is created by 
 * <tt>CacheObjectFactory</tt> class.
 * 
 * @author (Fei) John Chen
 */
public abstract class CacheProvider implements Cache {
    
    protected LogUtil log = LogUtil.getLogger(this.getClass().getName());
    
    /**
     * Key to represent cache provider <tt>name</tt> property.
     */
    public static final String KEY_CACHE_PROVIDER_NAME = "name";
    
    /**
     * Key to represent cache provider class name property.
     */
    public static final String KEY_CACHE_PROVIDER_CLASS_NAME = "provider_class";
    
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
     * Key to represent cache provider <tt>retrieveTimeoutInSeconds</tt> property.
     */
    public static final String KEY_CACHE_PROVIDER_REQUESTTIMEOUTINSECONDS = "requestTimeoutInSeconds";
    
	private String name;
	private String providerClassName;
	private String namespace;
	private String urls;
	private int expiresInSeconds;
	private int requestTimeoutInSeconds;
	private Properties p;
	
	private CacheProvider() {
		;
	}
	
	protected CacheProvider(Properties p) {
		if (p == null) 
			throw new NullPointerException("Input properties for " + 
					this.getClass().getName() + " is null.");
		init(p);
		this.p = p;
	}
	
	private void init(Properties p) {
		name = p.getProperty(KEY_CACHE_PROVIDER_NAME);
		providerClassName = p.getProperty(KEY_CACHE_PROVIDER_CLASS_NAME);
		if (providerClassName == null) {
			throw new NullPointerException("Provider class name must exist in cache provider named " + name);
		}
		namespace = p.getProperty(KEY_CACHE_PROVIDER_NAMESPACE, "");
		urls = p.getProperty(KEY_CACHE_PROVIDER_URLS);
		
		String sExpiresInSeconds = 
			p.getProperty(KEY_CACHE_PROVIDER_EXPIRESINSECONDS);
		if (sExpiresInSeconds != null) {
			expiresInSeconds = Integer.parseInt(sExpiresInSeconds);
		}
		
		String sRequestTimeoutInSeconds = 
			p.getProperty(KEY_CACHE_PROVIDER_REQUESTTIMEOUTINSECONDS);
		if (sRequestTimeoutInSeconds != null) {
			requestTimeoutInSeconds = Integer.parseInt(sRequestTimeoutInSeconds);
		}
	}
	
	/**
	 * Returns properties of this cache provider.
	 */
	public Properties getProperties() {
		return p;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the providerClassName
	 */
	public String getProviderClassName() {
		return providerClassName;
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
    
    public String toString() {
        return p.toString();
    }
    
    private String getFullKey(String key) {
    	return (namespace != null && !"".equals(namespace))?
    			(namespace + "@" + key):key;
    }
    
    /**
     * Returns object from cache based on <tt>key</tt>.
	 * @return the value to which this cache maps the specified key, or null 
	 * if the cache contains no mapping for this key.
     */
	public Object get(String key) {
		return getObject(getFullKey(key));
	}
	
	/**
	 * Stores object into cache based on <tt>key</tt>.
	 * @return true if successful
	 */
	public boolean put(String key, Object value) {
		return putObject(getFullKey(key), value);
	}
	
	/**
	 * Removes the mapping for this key from the cache if present.
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
     * Shut down this provider. Do nothing here because not all providers have 
     * this feature. Subclass should override this method if this feature is 
     * available.
     */
    public void shutDown() {
    	;
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
	 * Puts object into cache based on <tt>key</tt>. The <tt>key</tt> here 
	 * is a combination of <tt>namespace</tt> and key from request. 
	 * 
	 * Subclass must implement this method.
	 * 
	 * @param key   the key of the corresponding value
	 * @param value the object to be cached
	 * @return true if successfully stored
	 */
	protected abstract boolean putObject(String key, Object value);
    
    /**
     * Removes object from cache based on <tt>key</tt>. The <tt>key</tt> here 
	 * is a combination of <tt>namespace</tt> and key from request. 
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
