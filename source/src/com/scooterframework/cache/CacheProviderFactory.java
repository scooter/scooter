/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.cache;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.scooterframework.admin.AutoLoadedObjectFactory;
import com.scooterframework.admin.EnvConfig;


/**
 * CacheProviderFactory class creates a cache provider instance.
 * 
 * @author (Fei) John Chen
 */
public class CacheProviderFactory {
	private static CacheProviderFactory me;
	private Map cacheProviders;
	
	static {
		me = new CacheProviderFactory();
    }
    
    private CacheProviderFactory() {
    	cacheProviders = Collections.synchronizedMap(new HashMap());
    }
	
    public static synchronized CacheProviderFactory getInstance() {
        return me;
    }
    
    /**
     * Returns a reusable cache instance.
     * 
     * @param name cache provider name
     * @return a single cache instance
     */
    public Cache getCache(String name) {
    	if (name == null) 
    		throw new IllegalArgumentException("Cache provider name cannot be null.");
    	
    	CacheProvider cp = (CacheProvider)cacheProviders.get(name);
    	if (cp == null) {
    		Properties p = EnvConfig.getInstance().getPredefinedCacheProviderProperties(name);
    		if (p == null) 
    			throw new IllegalArgumentException(
    					"There is no cache provider with name \"" + name + 
    					"\" defined in properties file.");
    		String providerClassName = p.getProperty(CacheProvider.KEY_CACHE_PROVIDER_CLASS_NAME);
    		if (providerClassName == null) 
    			throw new IllegalArgumentException(
    					"There must be a cache provider class name defined for cache provider \"" + name + "\".");

            Class[] parameterTypes = {Properties.class};
            Object[] initargs = {p};
    		cp = (CacheProvider)AutoLoadedObjectFactory.getInstance().newInstance(providerClassName, parameterTypes, initargs);
    	}
    	return cp;
    }
    
    /**
     * Returns default cache provider instance.
     * @return a single cache instance
     */
    public Cache getDefaultCache() {
    	String name = EnvConfig.getInstance().getDefaultCacheProviderName();
    	return getCache(name);
    }
    
    /**
     * Removes all cache providers from factory's cache.
     */
    public void clear() {
    	cacheProviders.clear();
    }
    
    /**
     * Shut down each individual cache provider by calling its <tt>shutDown</tt>
     * method.
     */
    public void shutDown() {
    	Set s = cacheProviders.keySet();
    	synchronized(cacheProviders) {
    		Iterator it = s.iterator();
    		while(it.hasNext()) {
    			CacheProvider cp = (CacheProvider)cacheProviders.get(it.next());
    			cp.shutDown();
    		}
    	}
    }
}
