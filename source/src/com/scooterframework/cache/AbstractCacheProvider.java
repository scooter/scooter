/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.scooterframework.admin.Plugin;
import com.scooterframework.common.logging.LogUtil;

/**
 * AbstractCacheProvider class provides common methods of a cache provider.
 * 
 * <p>
 * Please notice that not all <tt>getXXXProperty()</tt> would return a value. 
 * Whether a property is set depends on settings in the 
 * <tt>environment.properties</tt> file.
 * 
 * @author (Fei) John Chen
 */
public abstract class AbstractCacheProvider extends Plugin 
implements CacheProvider {
    private LogUtil log = LogUtil.getLogger(this.getClass().getName());

	private String namespace;
	private String urls;
	private int expiresInSeconds = 3600;
	private int requestTimeoutInSeconds = 60;

	protected AbstractCacheProvider(Properties p) {
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
			requestTimeoutInSeconds = Integer.parseInt(sRequestTimeoutInSeconds);
		}
	}

	/**
	 * Returns the providerClassName
	 */
	public String getProviderClassName() {
		return super.getPluginClassName();
	}

	/**
	 * Returns the namespace property
	 */
	public String getNamespaceProperty() {
		return namespace;
	}

	/**
	 * Returns the urls property
	 */
	public String getUrlsProperty() {
		return urls;
	}

	/**
	 * Returns the expiresInSeconds property
	 */
	public int getExpiresInSecondsProperty() {
		return expiresInSeconds;
	}

	/**
	 * Returns the requestTimeoutInSeconds property
	 */
	public int getRequestTimeoutInSecondsProperty() {
		return requestTimeoutInSeconds;
	}

	@Override
	public String getName() {
		return super.getName();
	}
	
	/**
	 * Returns statistics of a cache.
	 */
	public Properties getStatistics(String name) {
		return getCache(name).getStatistics();
	}
	
	/**
	 * Returns statistics of all caches.
	 */
	public Map<String, Properties> getStatistics() {
		Map<String, Properties> statsMap = new HashMap<String, Properties>();
		try {
			for (String name : getCacheNames()) {
				statsMap.put(name, getStatistics(name));
			}
		}
		catch(UnsupportedOperationException ex) {
			log.info("getStatistics() is not supported on " + super.getName());
		}
		return statsMap;
	}
}
