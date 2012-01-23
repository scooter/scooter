/*
 *   This software is distributed under the terms of the FSF
 *   Gnu Lesser General Public License (see lgpl.txt).
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.activerecord;

import java.util.Collection;

import com.scooterframework.admin.EnvConfig;
import com.scooterframework.cache.Cache;
import com.scooterframework.cache.CacheKey;
import com.scooterframework.cache.CacheProvider;
import com.scooterframework.cache.CacheProviderUtil;
import com.scooterframework.cache.NamedCurrentThreadCache;

/**
 * <p>
 * ModelCacheClient class provides cache methods for a model.
 * </p>
 * 
 * @author (Fei) John Chen
 */
public class ModelCacheClient {

	private Class<? extends ActiveRecord> clazz;

	private ActiveRecord home;

	private boolean useRequestCache = true;
	private boolean useSecondLevelCache = false;
	private boolean flushCacheOnChange = true;
	private Collection<String> localUseCacheExceptions;
	private Collection<String> localFlushCacheExceptions;
	private Cache modelCache;

	/**
	 * Constructs an instance of ModelCacheClient.
	 * 
	 * @param modelHome
	 *            a domain model home instance
	 */
	public ModelCacheClient(ActiveRecord modelHome) {
		if (modelHome == null)
			throw new IllegalArgumentException("modelHome is null.");
		if (!modelHome.isHomeInstance())
			throw new IllegalArgumentException("modelHome must be a home instance.");

		this.clazz = modelHome.getClass();
		this.home = modelHome;
		
		useRequestCache = EnvConfig.getInstance().getUseThreadCache();
		useSecondLevelCache = EnvConfig.getInstance().getUseSecondLevelCache();
		flushCacheOnChange = EnvConfig.getInstance().getFlushCacheOnChange();
		
		localUseCacheExceptions = EnvConfig.getInstance().getLocalUseCacheExceptions(clazz.getName());
		localFlushCacheExceptions = EnvConfig.getInstance().getLocalFlushCacheExceptions(clazz.getName());
	}

	/**
	 * Returns the underlining home instance of this gateway.
	 */
	public ActiveRecord getHomeInstance() {
		return home;
	}

	/**
	 * Returns the underlining model class type of this gateway.
	 */
	public Class<? extends ActiveRecord> getModelClass() {
		return clazz;
	}
	
	public Object getCacheKey(String request, Object... elements) {
		return CacheKey.getCacheKey(clazz.getName(), request, elements);
	}
	
	public boolean useCache(String method) {
		boolean useCheck = useRequestCache || useSecondLevelCache;
		if (useCheck) {
			if (localUseCacheExceptions != null && localUseCacheExceptions.contains(method)) {
				useCheck = false;
			}
		}
		else {
			if (localUseCacheExceptions != null && localUseCacheExceptions.contains(method)) {
				useCheck = true;
			}
		}
		return useCheck;
	}
	
	public void clearCache(String method) {
		if (flushCache(method)) getCache().clear();
	}
	
	public boolean flushCache(String method) {
		boolean flushCheck = flushCacheOnChange;
		if (flushCheck) {
			if (localFlushCacheExceptions != null && localFlushCacheExceptions.contains(method)) {
				flushCheck = false;
			}
		}
		else {
			if (localFlushCacheExceptions != null && localFlushCacheExceptions.contains(method)) {
				flushCheck = true;
			}
		}
		return flushCheck;
	}
	
	public boolean allowCacheAssociatedObjects() {
		return EnvConfig.getInstance().allowCacheAssociatedObjects(clazz.getName());
	}
	
	public Cache getCache() {
		if (modelCache != null) return modelCache;
		
		if (useSecondLevelCache) {
			CacheProvider dcp = CacheProviderUtil.getDefaultCacheProvider();
			if (dcp != null) {
				modelCache = dcp.getCache(clazz.getName());
			}
		}
		else if (useRequestCache) {
			modelCache = new NamedCurrentThreadCache(clazz.getName());
		}
		
		return modelCache;
	}
}
