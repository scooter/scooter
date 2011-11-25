/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package plugin.spymemcached;

import java.util.Collection;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.MemcachedClient;

import com.scooterframework.cache.AbstractCacheProvider;
import com.scooterframework.cache.Cache;
import com.scooterframework.common.logging.LogUtil;

/**
 * SpyMemcachedCacheProvider uses Spymemcached client. Default request timeout 
 * is 60 seconds.
 * 
 * @author (Fei) John Chen
 */
public class SpyMemcachedCacheProvider extends AbstractCacheProvider {
    private LogUtil log = LogUtil.getLogger(this.getClass().getName());

	private MemcachedClient client;
	private long timeout; //seconds
	private SpyMemcachedCache spyMemcachedCache;
	
	public SpyMemcachedCacheProvider(Properties p) {
		super(p);
		
		try {
			client = new MemcachedClient(AddrUtil.getAddresses(super.getUrlsProperty()));
			timeout = (super.getRequestTimeoutInSecondsProperty() > 0)?super.getRequestTimeoutInSecondsProperty():60;
			spyMemcachedCache = new SpyMemcachedCache(this, client);
		} catch (Exception ex) {
			log.error("Error initiating memcached client: " + ex.getMessage());
		}
	}
    
    public void onStop() {
    	client.shutdown(timeout, TimeUnit.SECONDS);
    }
    
    public String getVersion() {
    	return "0.1.0";
    }

	public Cache getCache(String name) {
		return spyMemcachedCache;
	}

	public Collection<String> getCacheNames() {
		throw new UnsupportedOperationException("getCacheNames() is not supported.");
	}
}
