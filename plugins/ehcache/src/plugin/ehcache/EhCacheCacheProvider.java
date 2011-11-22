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
import com.scooterframework.admin.EnvConfig;
import com.scooterframework.common.logging.LogUtil;
import com.scooterframework.common.util.Converters;
import com.scooterframework.cache.AbstractCacheProvider;
import com.scooterframework.cache.Cache;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;

/**
 * EhCacheCacheProvider class is a CacheProvider based on EhCache.
 *
 * @author (Fei) John Chen
 */
public class EhCacheCacheProvider extends AbstractCacheProvider {
    private LogUtil log = LogUtil.getLogger(this.getClass().getName());

	private final ConcurrentHashMap<String, Cache> chm = new ConcurrentHashMap<String, Cache>();
	private CacheManager cacheManager;

	public EhCacheCacheProvider(Properties p) {
		super(p);

		String propertyFileName = super.getProperty(KEY_CACHE_PROVIDER_CONFIGFILE);
		if (propertyFileName != null) {
			try {
				File f = new File(propertyFileName);
				if (!f.exists()) {
					String appPath = ApplicationConfig.getInstance().getApplicationPath();
					f = new File(appPath + File.separatorChar + "WEB-INF/config"
							+ File.separatorChar + propertyFileName);
					if (!f.exists()) {
						if (!propertyFileName.startsWith(File.separator)) {
							propertyFileName = File.separator + propertyFileName;
						}
						cacheManager = CacheManager.create(new FileInputStream(f));
					}
					else {
						cacheManager = CacheManager.create(new FileInputStream(f));
					}
				}
				else {
					cacheManager = CacheManager.create(new FileInputStream(f));
				}
			}
			catch(IOException ex) {
				throw new IllegalArgumentException("Failed to load EhCache config file '" + propertyFileName + "'.");
			}
		}
		else {
			cacheManager = CacheManager.create(CacheManager.class.getResourceAsStream("/ehcache.xml"));
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
				log.warn("There is no cache registered with name '" + name
					+ "' in ehcache.xml. Will create a default cache.");
				String useDefaultCacheNameIfAbsent = super.getProperty("useDefaultCacheNameIfAbsent");
				if ("true".equals(useDefaultCacheNameIfAbsent)) {
					String defaultCacheName = super.getProperty("defaultCacheName", EnvConfig.getInstance().getDefaultCacheName());
				    log.warn("Default cache name: " + defaultCacheName);
					ehcache = cacheManager.addCacheIfAbsent(defaultCacheName);
				}

				if (ehcache == null) {
					String error = "There is no cache registered with name '" + name + "' in ehcache.xml.";
					log.error(error);
					throw new IllegalArgumentException(error);
				}
			}
			cache = new EhCacheCache(ehcache);
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
