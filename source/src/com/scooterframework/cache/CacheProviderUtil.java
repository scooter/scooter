/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.cache;

import java.util.Properties;

import com.scooterframework.admin.EnvConfig;
import com.scooterframework.admin.PluginManager;


/**
 * CacheProviderUtil class
 * 
 * @author (Fei) John Chen
 */
public class CacheProviderUtil {
	private static final String CACHE_PROVIDER_PLUGIN_PREFIX = "cache.provider.";
    
    /**
     * Returns a reusable cache instance.
     * 
     * @param providerName cache provider name
     * @return a single cache instance
     */
    public static CacheProvider getCacheProvider(String providerName) {
    	if (providerName == null) 
    		throw new IllegalArgumentException("Cache provider name cannot be null.");
    	
    	String pluginName = CACHE_PROVIDER_PLUGIN_PREFIX + providerName;
    	CacheProvider cp = (CacheProvider)PluginManager.getInstance().getPlugin(pluginName);
		if (cp == null) {
			throw new IllegalArgumentException(
					"There is no cache provider with name \"" + providerName
							+ "\". Check the environment.properties file.");
		}
    	return cp;
    }
    
    /**
     * Returns default cache provider instance.
     * @return a single cache instance
     */
    public static CacheProvider getDefaultCacheProvider() {
    	String name = EnvConfig.getInstance().getDefaultCacheProviderName();
    	return getCacheProvider(name);
    }
    
    /**
     * Returns default cache instance. If the default cache name is undefined, 
     * "<tt>default</tt>" will be used as the name.
     * 
     * @return a single cache instance
     */
    public static Cache getDefaultCache() {
    	CacheProvider cp = getDefaultCacheProvider();
    	if (cp == null) return null;
    	
    	String name = EnvConfig.getInstance().getDefaultCacheName();
    	if (name == null) 
    		throw new IllegalArgumentException("Default cache name is null. " + 
    				"Set 'default.cache.name' property in environment.properties file.");
    	return cp.getCache(name);
    }
    
    /**
     * Returns properties of a cache provider.
     * 
     * @param providerName cache provider name
     * @return Properties
     */
    public static Properties getCacheProviderProperties(String providerName) {
    	if (providerName == null) 
    		throw new IllegalArgumentException("Cache provider name cannot be null.");
    	
    	String pluginName = CACHE_PROVIDER_PLUGIN_PREFIX + providerName;
    	Properties p = PluginManager.getInstance().getPluginProperties(pluginName);
    	return p;
    }
	
	/**
	 * Returns cache key
	 * 
	 * @param name
	 * @param elements
	 * @return cache key
	 */
	public static String getCacheKey(String namespace, String name, Object... elements) {
		StringBuilder sb = new StringBuilder();
		sb.append(namespace).append('.').append(name);
		if (elements != null && elements.length > 0) {
			sb.append(" - ");
			for (Object object : elements) {
				sb.append(object).append("|");
			}
		}
		return sb.toString();
	}
}
