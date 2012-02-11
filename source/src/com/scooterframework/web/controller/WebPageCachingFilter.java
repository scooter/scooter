/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.web.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.constructs.web.PageInfo;
import net.sf.ehcache.constructs.web.filter.CachingFilter;

import com.scooterframework.admin.ApplicationConfig;
import com.scooterframework.common.logging.LogUtil;
import com.scooterframework.common.util.Converters;
import com.scooterframework.common.util.CurrentThreadCacheClient;

/**
 * WebPageCachingFilter uses Ehcache to cache web pages.
 * 
 * The following init-params are supported:
 * <ol>
 * <li>configFile - the name of Ehcache config file used by the filter; default is ehcache.xml under the web-inf directory.</li>
 * <li>cacheablePaths - a list of request paths separated by comma that are suitable for caching, such as <tt>/about, /contact</tt></li>
 * <li>uncacheablePaths - a list of request paths separated by comma that are not suitable for caching, such as <tt>/news, /timeline</tt></li>
 * <li>cacheName - the cache name in ehcache.xml used by the filter.</li>
 * <li>blockingTimeoutMillis - the time, in milliseconds, to wait for the filter
 * chain to return with a response on a cache miss. This is useful to fail fast
 * in the event of an infrastructure failure.</li>
 * </ol>
 * 
 * @author (Fei) John Chen
 */
public class WebPageCachingFilter extends CachingFilter {
    private static LogUtil log = LogUtil.getLogger(WebPageCachingFilter.class.getName());
    
    protected Set<String> uncacheablePaths;
    protected Set<String> cacheablePaths;
    protected CacheManager cacheManager;
    protected ConcurrentHashMap<String, Set<String>> chm = new ConcurrentHashMap<String, Set<String>>();
    
    @Override
    public void doInit(FilterConfig filterConfig) throws CacheException {
    	String propertyFileName = filterConfig.getInitParameter("configFile");
    	if (propertyFileName == null) propertyFileName = "ehcache.xml";
		
		try {
			String appPath = ApplicationConfig.getInstance().getApplicationPath();
			File f = new File(appPath + File.separatorChar + "WEB-INF/config"
					+ File.separatorChar + propertyFileName);
			if (f.exists()) {
				cacheManager = CacheManager.create(new FileInputStream(f));
				log.debug("Created cacheManager based on config file 'WEB-INF/config/" + f.getName() + "'.");
			}
			else {
				throw new IOException("There is no file named " + propertyFileName + " under WEB-INF/config directory.");
			}
		}
		catch(IOException ex) {
			throw new IllegalArgumentException("Failed to load EhCache config file '" + propertyFileName + "' : " + ex.getMessage());
		}
		
    	String uncacheablePathsStr = filterConfig.getInitParameter("uncacheablePaths");
    	if (uncacheablePathsStr != null) {
    		uncacheablePaths = Converters.convertStringToSet(uncacheablePathsStr);
    	}
    	String cacheablePathsStr = filterConfig.getInitParameter("cacheablePaths");
    	if (cacheablePathsStr != null) {
    		cacheablePaths = Converters.convertStringToSet(cacheablePathsStr);
    	}
    	
		super.doInit(filterConfig);
    }
    
    /**
     * buildPageInfo overrides the same method in super class.
     */
    @Override
    protected PageInfo buildPageInfo(final HttpServletRequest request,
            final HttpServletResponse response, final FilterChain chain)
            throws Exception {
    	PageInfo pageInfo = super.buildPageInfo(request, response, chain);
    	verifyCaching(request);
        return pageInfo;
    }
    
    /**
     * Checks whether it is fine to cache a request.
     * 
     * @param request
     */
    protected void verifyCaching(final HttpServletRequest request) {
    	String cacheKey = calculateKey(request);
    	String requestPath = CurrentThreadCacheClient.requestPath();
    	
    	if (uncacheablePaths != null && uncacheablePaths.contains(requestPath)) {
    		blockingCache.remove(cacheKey);
        	log.debug("Removed cached element with key: " + cacheKey);
    		return;
    	}
    	
    	if (cacheablePaths != null && cacheablePaths.contains(requestPath)) {
    		return;
    	}

    	if (isStaticContentRequest(request)) return;

    	Set<String> paths = null;
    	String model = CurrentThreadCacheClient.model();
    	if (model != null) {
    		paths = chm.get(model);
    		if (paths == null) {
    			paths = new HashSet<String>();
    			chm.put(model, paths);
    		}
    	}
    	
    	String cacheable = CurrentThreadCacheClient.cacheable();
    	if ((cacheable == null && !hasCached(chm, cacheKey)) || 
    			cacheable != null && !"true".equals(cacheable)) {
    		blockingCache.remove(cacheKey);
        	log.debug("Removed cache key: " + cacheKey);
    		
    		if (paths != null && paths.size() > 0) {
    			blockingCache.removeAll(paths);
    			chm.remove(model);
            	log.debug("Removed all cached elements related to model: " + model);
    		}
    	}
    	else if (paths != null) {
    		paths.add(cacheKey);
    	}
    }
    
    protected boolean hasCached(ConcurrentHashMap<String, Set<String>> chm, String cacheKey) {
    	if (chm.size() > 0) {
    		for (Map.Entry<String, Set<String>> entry : chm.entrySet()) {
    			if (entry.getValue().contains(cacheKey)) return true;
    		}
    	}
    	return false;
    }
    
    protected boolean isStaticContentRequest(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String staticPath = request.getContextPath() + "/" + "static";
        return (requestURI.startsWith(staticPath))?true:false;
    }

	@Override
	protected String calculateKey(HttpServletRequest request) {
		String key = CurrentThreadCacheClient.requestPathKey();
        String queryString = request.getQueryString();
        if (queryString != null) key += "?" + queryString;
		return key;
	}

	@Override
	protected CacheManager getCacheManager() {
		return cacheManager;
	}
}
