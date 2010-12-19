/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.common.util;

import java.util.ArrayList;
import java.util.List;

/**
 * CurrentThreadCacheClient class.
 * 
 * @author (Fei) John Chen
 */
public class CurrentThreadCacheClient {
	private static final String KEY_ERROR = "key.error";
	
	public static Exception getFirstError() {
		List<Exception> errors = (List)CurrentThreadCache.get(KEY_ERROR);
		return (errors != null && errors.size() > 0)?errors.get(0):null;
	}
	
	public static void storeError(Exception ex) {
		List<Exception> errors = (List)CurrentThreadCache.get(KEY_ERROR);
		if (errors == null) {
			errors = new ArrayList<Exception>();
			CurrentThreadCache.set(KEY_ERROR, errors);
		}
		errors.add(ex);
	}
	
	public static boolean hasError() {
		List<Exception> errors = (List)CurrentThreadCache.get(KEY_ERROR);
		return (errors != null && errors.size() > 0)?true:false;
	}
}
