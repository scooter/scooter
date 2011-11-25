/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.cache;


/**
 * CacheKey class
 * 
 * @author (Fei) John Chen
 */
public class CacheKey {
	
	/**
	 * Returns cache key
	 * 
	 * @param namespace
	 * @param name
	 * @param elements
	 * @return cache key
	 */
	public static Object getCacheKey(String namespace, String name, Object... elements) {
		StringBuilder sb = new StringBuilder();
		sb.append(namespace).append('.').append(name);
		if (elements != null && elements.length > 0) {
			sb.append(" - ");
			for (Object object : elements) {
				sb.append(object).append("|");
			}
		}
		return sb.toString();
	}
}
