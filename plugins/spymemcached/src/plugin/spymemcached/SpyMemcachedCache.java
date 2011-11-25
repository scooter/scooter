/*
 *   This software is distributed under the terms of the FSF
 *   Gnu Lesser General Public License (see lgpl.txt).
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package plugin.spymemcached;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import net.spy.memcached.MemcachedClient;

import com.scooterframework.cache.Cache;
import com.scooterframework.common.logging.LogUtil;

/**
 * SpyMemcachedCache
 *
 * @author (Fei) John Chen
 */
public class SpyMemcachedCache implements Cache {
    private LogUtil log = LogUtil.getLogger(this.getClass().getName());

	private SpyMemcachedCacheProvider cacheProvider;
	private MemcachedClient delegate;
	private long timeout; //seconds

	public SpyMemcachedCache(SpyMemcachedCacheProvider cacheProvider, MemcachedClient delegate) {
		this.cacheProvider = cacheProvider;
		this.delegate = delegate;
	}

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
		Object obj = null;
		@SuppressWarnings("rawtypes")
		Future f = delegate.asyncGet(toString(key));
		try {
			obj = f.get(timeout, TimeUnit.SECONDS);
		} catch (Exception ex) {
			log.error("Error in getObject(): " + ex.getMessage());
			f.cancel(true);
		}
		return obj;
	}

	public boolean put(Object key, Object value) {
		boolean status = false;
		try {
			Boolean b = (Boolean) (delegate.set(toString(key),
					cacheProvider.getExpiresInSecondsProperty(), value).get());
			status = b.booleanValue();
		} catch (Exception ex) {
			throw new RuntimeException("Error in put(): " + ex.getMessage());
		}
		return status;
	}

	public boolean remove(Object key) {
		boolean status = false;
		try {
			Boolean b = (Boolean)(delegate.delete(toString(key))).get();
			status = b.booleanValue();
		} catch (Exception ex) {
			throw new RuntimeException("Error in remove(): " + ex.getMessage());
		}
		return status;
	}

	public void clear() {
		delegate.flush();
	}

	private String toString(Object obj) {
		return (obj != null)?obj.toString():null;
	}

	/**
	 * Returns the Cache statistics.
	 */
	public Properties getStatistics() {
		Properties props = new Properties();
		Map<SocketAddress, Map<String, String>> stats = delegate.getStats();
		for (Map.Entry<SocketAddress, Map<String, String>> entry : stats.entrySet()) {
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
		return props;
	}
}
