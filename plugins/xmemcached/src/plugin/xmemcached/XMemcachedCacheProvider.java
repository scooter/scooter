/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package plugin.xmemcached;

import java.io.IOException;
import java.util.Collection;
import java.util.Properties;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.utils.AddrUtil;

import com.scooterframework.cache.AbstractCacheProvider;
import com.scooterframework.cache.Cache;
import com.scooterframework.common.logging.LogUtil;

/**
 * XMemcachedCacheProvider uses Xmemcached client. Default request timeout 
 * is 60 seconds.
 * 
 * @author (Fei) John Chen
 */
public class XMemcachedCacheProvider extends AbstractCacheProvider {
    private LogUtil log = LogUtil.getLogger(this.getClass().getName());

	private MemcachedClient client;
	private long timeout; //seconds
	private XMemcachedCache xmemcachedCache;
	
	public XMemcachedCacheProvider(Properties p) {
		super(p);
		
		try {
			MemcachedClientBuilder builder = new XMemcachedClientBuilder(
					AddrUtil.getAddresses(super.getUrlsProperty()));
			//builder.setCommandFactory(new BinaryCommandFactory());//use binary protocol
			client = builder.build();
			
			timeout = (super.getRequestTimeoutInSecondsProperty() > 0)?super.getRequestTimeoutInSecondsProperty():60;
			xmemcachedCache = new XMemcachedCache(this, client);
		} catch (Exception ex) {
			log.error("Error initiating memcached client: " + ex.getMessage(), ex);
		}
	}
	
	long getTimeout() {
		return timeout;
	}
    
    public void onStop() {
    	try {
			client.shutdown();
		} catch (IOException ex) {
			log.error("Error in shutdown(): " + ex.getMessage());
		}
    }
    
    public String getVersion() {
    	return "0.1.0";
    }

	public Cache getCache(String name) {
		return xmemcachedCache;
	}

	public Collection<String> getCacheNames() {
		throw new UnsupportedOperationException("getCacheNames() is not supported.");
	}
}
