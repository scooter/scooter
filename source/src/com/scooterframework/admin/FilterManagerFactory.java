/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.admin;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


/**
 * FilterManagerFactory manages FilterManager of controller classes.
 * 
 * @author (Fei) John Chen
 */
public class FilterManagerFactory {
	private static FilterManagerFactory me;
	private Map<Class, FilterManager> filterManagersMap = 
		Collections.synchronizedMap(new HashMap<Class, FilterManager>());
	
	private FilterManagerFactory() {
	}
	
	/**
	 * Returns the singleton instance of the <tt>FilterManager</tt>.
	 * 
	 * @return the singleton instance of the <tt>FilterManager</tt>.
	 */
	public static FilterManagerFactory getInstance() {
		if (me == null) me = new FilterManagerFactory();
		return me;
	}
	
	/**
	 * Returns the filter manager for the owner class type.
	 * 
	 * @param ownerClass
	 * @return the FilterManager for the owner class type
	 */
	public FilterManager getFilterManager(Class ownerClass) {
		FilterManager fm = filterManagersMap.get(ownerClass);
		if (fm == null) {
			fm = new FilterManager(ownerClass);
			filterManagersMap.put(ownerClass, fm);
		}
		return fm;
	}
	
	/**
	 * Removes the filter manager for the owner class type.
	 * 
	 * @param ownerClass
	 */
	public void removeFilterManager(Class ownerClass) {
		filterManagersMap.remove(ownerClass);
	}
}
