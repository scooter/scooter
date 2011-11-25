/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package plugin.xmemcached;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;

import net.rubyeye.xmemcached.MemcachedClient;

import com.scooterframework.cache.Cache;
import com.scooterframework.common.logging.LogUtil;

/**
 * SpyMemcachedCache
 * 
 * @author (Fei) John Chen
 */
public class XMemcachedCache implements Cache {
    private LogUtil log = LogUtil.getLogger(this.getClass().getName());
    
	private XMemcachedCacheProvider cacheProvider;
	private MemcachedClient delegate;
	private long timeout; //seconds
	
	public XMemcachedCache(XMemcachedCacheProvider cacheProvider, MemcachedClient delegate) {
		this.cacheProvider = cacheProvider;
		this.delegate = delegate;
		timeout = cacheProvider.getTimeout();
	}
	
	/**
	 * Returns the underlying cache.
	 * 
	 * @return the cache instance from vendor
	 */
	public MemcachedClient getDelegateCache() {
		return delegate;
	}

	public String getName() {
		return cacheProvider.getName();
	}
	
	public Collection<Object> getKeys() {
		throw new UnsupportedOperationException("getKeys() is not supported.");
	}

	public Object get(Object key) {
		Object result = null;
		try {
			result = delegate.get(toString(key), timeout);
		} catch (Exception ex) {
			log.error("Error put(): " + ex.getMessage(), ex);
		}
		return result;
	}
	
	public boolean put(Object key, Object value) {
		boolean status = false;
		try {
			status = delegate.set(toString(key), cacheProvider.getExpiresInSecondsProperty(), value, timeout);
		} catch (Exception ex) {
			log.error("Error put(): " + ex.getMessage(), ex);
		}
		return status;
	}
	
	public boolean remove(Object key) {
		boolean status = false;
		try {
			status = delegate.delete(toString(key));
		} catch (Exception ex) {
			log.error("Error remove(): " + ex.getMessage(), ex);
		}
		return status;
	}
	
	public void clear() {
		try {
			delegate.flushAll();
		} catch (Exception ex) {
			log.error("Error clear(): " + ex.getMessage(), ex);
		}
	}
	
	private String toString(Object obj) {
		return (obj != null)?obj.toString():null;
	}

	/**
	 * Returns the Cache statistics.
	 */
	public Properties getStatistics() {
		Properties props = new Properties();
		Map<InetSocketAddress, Map<String, String>> stats;
		try {
			stats = delegate.getStats();
			for (Map.Entry<InetSocketAddress, Map<String, String>> entry : stats.entrySet()) {
				InetSocketAddress sa = (InetSocketAddress)entry.getKey();
				String host = sa.getHostName();
				int port = sa.getPort();
				String url = host + ":" + port;
				Map<String, String> values = entry.getValue();
				for (Map.Entry<String, String> entry2 : values.entrySet()) {
					String key = entry2.getKey();
					String value = entry2.getValue();
					props.setProperty(key + "@" + url, value);
				}
			}
		} catch (Exception ex) {
			log.error("Error getStatistics(): " + ex.getMessage(), ex);
		}
		return props;
	}
}
