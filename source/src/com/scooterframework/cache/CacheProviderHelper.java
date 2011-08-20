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
 * CacheProviderHelper class
 * 
 * @author (Fei) John Chen
 */
public class CacheProviderHelper {
	
	private static final String CACHE_PROVIDER_PLUGIN_PREFIX = "cache.provider.";
    
    /**
     * Returns a reusable cache instance.
     * 
     * @param providerName cache provider name
     * @return a single cache instance
     */
    public static Cache getCache(String providerName) {
    	if (providerName == null) 
    		throw new IllegalArgumentException("Cache provider name cannot be null.");
    	
    	String pluginName = CACHE_PROVIDER_PLUGIN_PREFIX + providerName;
    	CacheProvider cp = (CacheProvider)PluginManager.getInstance().getPlugin(pluginName);
		if (cp == null) {
			throw new IllegalArgumentException(
					"There is no cache provider with name \"" + providerName
							+ "\". Check the environment properties file.");
		}
    	return cp;
    }
    
    /**
     * Returns default cache provider instance.
     * @return a single cache instance
     */
    public static Cache getDefaultCache() {
    	String name = EnvConfig.getInstance().getDefaultCacheProviderName();
    	return getCache(name);
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
}
