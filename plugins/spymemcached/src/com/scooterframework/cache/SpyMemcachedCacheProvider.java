/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.cache;

import java.util.Properties;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.MemcachedClient;

/**
 * SpyMemcachedCacheProvider uses Spymemcached client. Default request timeout 
 * is 60 seconds.
 * 
 * @author (Fei) John Chen
 */
public class SpyMemcachedCacheProvider extends CacheProvider {
	private MemcachedClient client;
	private long timeout; //seconds
	
	public SpyMemcachedCacheProvider(Properties p) {
		super(p);
		
		try {
			client = new MemcachedClient(AddrUtil.getAddresses(super.getUrls()));
			
			timeout = (super.requestTimeoutInSeconds() > 0)?super.requestTimeoutInSeconds():60;
		} catch (Exception ex) {
			log.error("Error initiating memcached client: " + ex.getMessage());
		}
	}

	protected Object getObject(String key) {
		Object obj = null;
		Future f = client.asyncGet(key);
		try {
			obj = f.get(timeout, TimeUnit.SECONDS);
		} catch (Exception ex) {
			log.error("Error in getObject(): " + ex.getMessage());
			f.cancel(true);
		}
		return obj;
	}
	
	protected boolean putObject(String key, Object value) {
		boolean status = false;
		try {
			Boolean b = (Boolean)(client.set(key, super.expiresInSeconds(), value).get());
			status = b.booleanValue();
		} catch (Exception ex) {
			log.error("Error in putObject(): " + ex.getMessage());
		}
		return status;
	}
	
	protected boolean removeObject(String key) {
		boolean status = false;
		try {
			Boolean b = (Boolean)(client.delete(key)).get();
			status = b.booleanValue();
		} catch (Exception ex) {
			log.error("Error in removeObject(): " + ex.getMessage());
		}
		return status;
	}
	
	protected void clearObjects() {
		try {
			client.flush();
		} catch (Exception ex) {
			log.error("Error in clearObjects(): " + ex.getMessage());
		}
	}
    
    public void shutDown() {
    	client.shutdown(timeout, TimeUnit.SECONDS);
    }
}
