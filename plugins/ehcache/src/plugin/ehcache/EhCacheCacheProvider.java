/*
 *   This software is distributed under the terms of the FSF
 *   Gnu Lesser General Public License (see lgpl.txt).
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package plugin.ehcache;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import com.scooterframework.admin.ApplicationConfig;
import com.scooterframework.common.logging.LogUtil;
import com.scooterframework.common.util.Converters;
import com.scooterframework.cache.AbstractCacheProvider;
import com.scooterframework.cache.Cache;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;

/**
 * EhCacheCacheProvider class is a CacheProvider based on EhCache. 
 * 
 * Its configuration file is searched based on the following orders:
 * <ul>
 *   <li>A file name under <tt>WEB-INF/config</tt> specified by the <tt>configFile</tt> property.</li>
 *   <li>A full file name specified by the <tt>configFile</tt> property.</li>
 *   <li>File <tt>ehcache.xml</tt> under <tt>WEB-INF/config</tt>.</li>
 *   <li>File <tt>ehcache.xml</tt> on classpath.</li>
 * </ul>
 *
 * @author (Fei) John Chen
 */
public class EhCacheCacheProvider extends AbstractCacheProvider {
    private LogUtil log = LogUtil.getLogger(this.getClass().getName());

	private final ConcurrentHashMap<String, Cache> chm = new ConcurrentHashMap<String, Cache>();
	private CacheManager cacheManager;
	private boolean useSerialization;

	public EhCacheCacheProvider(Properties p) {
		super(p);

		String propertyFileName = super.getProperty(KEY_CACHE_PROVIDER_CONFIGFILE);
		if (propertyFileName == null) 
			propertyFileName = "ehcache.xml";
		
		try {
			String appPath = ApplicationConfig.getInstance().getApplicationPath();
			File f = new File(appPath + File.separatorChar + "WEB-INF/config"
					+ File.separatorChar + propertyFileName);
			if (f.exists()) {
				cacheManager = CacheManager.create(new FileInputStream(f));
				log.debug("Created cacheManager based on config file 'WEB-INF/config/" + f.getName() + "'.");
			}
			else {
				f = new File(propertyFileName);
				if (f.exists()) {
					cacheManager = CacheManager.create(new FileInputStream(f));
					log.debug("Created cacheManager based on config file '" + f.getCanonicalPath() + "'.");
				}
				else {
					cacheManager = CacheManager.create(CacheManager.class.getResourceAsStream("/" + propertyFileName));
					log.debug("Created cacheManager based on '" + propertyFileName + "' from classpath.");
				}
			}
		}
		catch(IOException ex) {
			throw new IllegalArgumentException("Failed to load EhCache config file '" + propertyFileName + "' : " + ex.getMessage());
		}
		
		if ("true".equals(super.getProperty("useSerialization"))) {
			useSerialization = true;
		}
	}

	/**
	 * Returns the cache for the name.
	 *
	 * @param name  name of the cache
	 * @return the cache associated with the name
	 */
	public Cache getCache(String name) {
		Cache cache = chm.get(name);
		if (cache == null) {
			Ehcache ehcache = cacheManager.getEhcache(name);
			if (ehcache == null) {
				log.debug("There is no cache registered with name '" + name
					+ "' in ehcache.xml. Will create a cache for it.");
				if (name != null) {
					ehcache = cacheManager.addCacheIfAbsent(name);
				}
				
				if (ehcache == null) {
					String error = "There is no cache registered with name '" + name + "' in ehcache.xml.";
					log.error(error);
					throw new IllegalArgumentException(error);
				}
			}
			cache = new EhCacheCache(name, ehcache, useSerialization);
			chm.put(name, cache);
		}
		return cache;
	}

	/**
	 * Returns a list of cache names.
	 */
	public Collection<String> getCacheNames() {
		String[] names = cacheManager.getCacheNames();
		return Converters.convertArrayToList(names);
	}

	public void onStop() {
		try {
			String[] names = cacheManager.getCacheNames();
			if (names != null) {
				for (String name : names) {
					Ehcache ehcache = cacheManager.getEhcache(name);
					ehcache.flush();
				}
			}
		} catch (Exception ex) {
			;
		}
		cacheManager.shutdown();
	}

	public String getVersion() {
		return "0.1.0";
	}
}
