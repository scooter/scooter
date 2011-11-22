/*
 *   This software is distributed under the terms of the FSF
 *   Gnu Lesser General Public License (see lgpl.txt).
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package plugin.ehcache;

import java.util.Collection;
import java.util.Properties;

import com.scooterframework.cache.Cache;
import com.scooterframework.cache.CacheStatisticsConstats;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.Statistics;

public class EhCacheCache implements Cache {
	private Ehcache delegate;

	public EhCacheCache(Ehcache delegate) {
		this.delegate = delegate;
	}

	public String getName() {
		return delegate.getName();
	}

	@SuppressWarnings("unchecked")
	public Collection<Object> getKeys() {
		return delegate.getKeys();
	}

	public Object get(Object key) {
		Element element = delegate.get(key);
		return (element != null) ? element.getObjectValue() : null;
	}

	public boolean put(Object key, Object value) {
		Element element = new Element(key, value);
		delegate.put(element);
		return true;
	}

	public boolean remove(Object key) {
		return delegate.remove(key);
	}

	public void clear() {
		delegate.removeAll();
	}

	/**
	 * Returns the Cache statistics.
	 */
	public Properties getStatistics() {
		Properties props = new Properties();
		Statistics stats = delegate.getStatistics();
		props.setProperty(CacheStatisticsConstats.KEY_CACHE_STATS_AverageGetTime, stats.getAverageGetTime() + "");
		props.setProperty(CacheStatisticsConstats.KEY_CACHE_STATS_AverageSearchTime, stats.getAverageSearchTime() + "");
		props.setProperty(CacheStatisticsConstats.KEY_CACHE_STATS_CacheHits, stats.getCacheHits() + "");
		props.setProperty(CacheStatisticsConstats.KEY_CACHE_STATS_CacheMisses, stats.getCacheMisses() + "");
		props.setProperty(CacheStatisticsConstats.KEY_CACHE_STATS_DiskStoreObjectCount, stats.getDiskStoreObjectCount() + "");
		props.setProperty(CacheStatisticsConstats.KEY_CACHE_STATS_EvictionCount, stats.getEvictionCount() + "");
		props.setProperty(CacheStatisticsConstats.KEY_CACHE_STATS_InMemoryHits, stats.getInMemoryHits() + "");
		props.setProperty(CacheStatisticsConstats.KEY_CACHE_STATS_InMemoryMisses, stats.getInMemoryMisses() + "");
		props.setProperty(CacheStatisticsConstats.KEY_CACHE_STATS_MemoryStoreObjectCount, stats.getMemoryStoreObjectCount() + "");
		props.setProperty(CacheStatisticsConstats.KEY_CACHE_STATS_ObjectCount, stats.getObjectCount() + "");
		props.setProperty(CacheStatisticsConstats.KEY_CACHE_STATS_OffHeapHits, stats.getOffHeapHits() + "");
		props.setProperty(CacheStatisticsConstats.KEY_CACHE_STATS_OffHeapMisses, stats.getOffHeapMisses() + "");
		props.setProperty(CacheStatisticsConstats.KEY_CACHE_STATS_OffHeapStoreObjectCount, stats.getOffHeapStoreObjectCount() + "");
		props.setProperty(CacheStatisticsConstats.KEY_CACHE_STATS_OnDiskHits, stats.getOnDiskHits() + "");
		props.setProperty(CacheStatisticsConstats.KEY_CACHE_STATS_OnDiskMisses, stats.getOnDiskMisses() + "");
		props.setProperty(CacheStatisticsConstats.KEY_CACHE_STATS_SearchesPerSecond, stats.getSearchesPerSecond() + "");
		return props;
	}
}
