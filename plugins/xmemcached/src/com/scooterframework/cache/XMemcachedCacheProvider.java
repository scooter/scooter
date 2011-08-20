/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.cache;

import java.util.Properties;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.utils.AddrUtil;

/**
 * XMemcachedCacheProvider uses Xmemcached client. Default request timeout 
 * is 60 seconds.
 * 
 * @author (Fei) John Chen
 */
public class XMemcachedCacheProvider extends CacheProvider {
	private MemcachedClient client;
	private long timeout; //seconds
	
	public XMemcachedCacheProvider(Properties p) {
		super(p);
		
		try {
			MemcachedClientBuilder builder = new XMemcachedClientBuilder(
					AddrUtil.getAddresses(super.getUrls()));
			//builder.setCommandFactory(new BinaryCommandFactory());//use binary protocol
			client = builder.build();
			
			timeout = (super.requestTimeoutInSeconds() > 0)?super.requestTimeoutInSeconds():60;
		} catch (Exception ex) {
			log.error("Error initiating memcached client: " + ex.getMessage());
		}
	}

	protected Object getObject(String key) {
		Object result = null;
		try {
			result = client.get(key, timeout);
		} catch (Exception ex) {
			log.error("Error in getObject(): " + ex.getMessage());
		}
		return result;
	}
	
	protected boolean putObject(String key, Object value) {
		boolean status = false;
		try {
			status = client.set(key, super.expiresInSeconds(), value, timeout);
		} catch (Exception ex) {
			log.error("Error in putObject(): " + ex.getMessage());
		}
		return status;
	}

	protected boolean removeObject(String key) {
		boolean status = false;
		try {
			status = client.delete(key);
		} catch (Exception ex) {
			log.error("Error in removeObject(): " + ex.getMessage());
		}
		return status;
	}
	
	protected void clearObjects() {
		try {
			client.flushAll();
		} catch (Exception ex) {
			log.error("Error in clearObjects(): " + ex.getMessage());
		}
	}
    
    public void onStop() {
    	try {
			client.shutdown();
		} catch (Exception ex) {
			log.error("Error in shutdown(): " + ex.getMessage());
		}
    }
    
    public String getVersion() {
    	return "0.1.0";
    }
}
