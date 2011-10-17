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
import java.util.Map;

import com.scooterframework.admin.Constants;
import com.scooterframework.web.route.RouteConstants;

/**
 * CurrentThreadCacheClient class.
 * 
 * @author (Fei) John Chen
 */
public class CurrentThreadCacheClient {
	private static final String KEY_ERROR = "key.error";
	
	@SuppressWarnings("unchecked")
	public static Exception getFirstError() {
		List<Exception> errors = (List<Exception>)CurrentThreadCache.get(KEY_ERROR);
		return (errors != null && errors.size() > 0)?errors.get(0):null;
	}
	
	@SuppressWarnings("unchecked")
	public static void storeError(Exception ex) {
		List<Exception> errors = (List<Exception>)CurrentThreadCache.get(KEY_ERROR);
		if (errors == null) {
			errors = new ArrayList<Exception>();
			CurrentThreadCache.set(KEY_ERROR, errors);
		}
		errors.add(ex);
	}
	
	@SuppressWarnings("unchecked")
	public static boolean hasError() {
		List<Exception> errors = (List<Exception>)CurrentThreadCache.get(KEY_ERROR);
		return (errors != null && errors.size() > 0)?true:false;
	}
	
	public static void cacheUserID(String userID) {
		CurrentThreadCache.set(Constants.USER_ID_VALUE, userID);
		CurrentThreadCache.set(Constants.USER_ID_RETRIEVED, "Y");
	}
	
	public static String getUserID() {
		return (String)CurrentThreadCache.get(Constants.USER_ID_VALUE);
	}
	
	public static boolean userIDRetrieved() {
		return "Y".equals((String)CurrentThreadCache.get(Constants.USER_ID_RETRIEVED));
	}
	
	
	
	/*
	 * 
	 * The following are action properties.
	 * 
	 */
	
	
	public static void cacheHttpMethod(String httpMethod) {
		CurrentThreadCache.set(Constants.HTTP_METHOD, httpMethod);
	}
	
	public static String httpMethod() {
		return (String)CurrentThreadCache.get(Constants.HTTP_METHOD);
	}
	
	public static void cacheRequestPath(String requestPath) {
		CurrentThreadCache.set(Constants.REQUEST_PATH, requestPath);
	}
	
	public static String requestPath() {
		return (String)CurrentThreadCache.get(Constants.REQUEST_PATH);
	}
	
	public static void cacheRequestPathKey(String requestPathKey) {
		CurrentThreadCache.set(Constants.REQUEST_PATH_KEY, requestPathKey);
	}
	
	public static String requestPathKey() {
		return (String)CurrentThreadCache.get(Constants.REQUEST_PATH_KEY);
	}
	
	public static void cacheFieldValues(Map<String, String> fieldValues) {
		CurrentThreadCache.set(RouteConstants.FIELD_VALUES, fieldValues);
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, String> fieldValues() {
		return (Map<String, String>)CurrentThreadCache.get(RouteConstants.FIELD_VALUES);
	}
	
	public static void cacheController(String controller) {
		CurrentThreadCache.set(Constants.CONTROLLER, controller);
	}
	
	public static String controller() {
		return (String)CurrentThreadCache.get(Constants.CONTROLLER);
	}
	
	public static void cacheControllerClass(String controllerClass) {
		CurrentThreadCache.set(Constants.CONTROLLER_CLASS, controllerClass);
	}
	
	public static String controllerClass() {
		return (String)CurrentThreadCache.get(Constants.CONTROLLER_CLASS);
	}
	
	public static void cacheControllerPath(String controllerPath) {
		CurrentThreadCache.set(Constants.CONTROLLER_PATH, controllerPath);
	}
	
	public static String controllerPath() {
		return (String)CurrentThreadCache.get(Constants.CONTROLLER_PATH);
	}
	
	public static void cacheAction(String action) {
		CurrentThreadCache.set(Constants.ACTION, action);
	}
	
	public static String action() {
		return (String)CurrentThreadCache.get(Constants.ACTION);
	}
	
	public static void cacheModel(String model) {
		CurrentThreadCache.set(Constants.MODEL, model);
	}
	
	public static String model() {
		return (String)CurrentThreadCache.get(Constants.MODEL);
	}
	
	public static void cacheFormat(String format) {
		CurrentThreadCache.set(Constants.FORMAT, format);
	}
	
	public static String format() {
		return (String)CurrentThreadCache.get(Constants.FORMAT);
	}
	
	public static void cacheResource(String resource) {
		CurrentThreadCache.set(Constants.RESOURCE, resource);
	}
	
	public static String resource() {
		return (String)CurrentThreadCache.get(Constants.RESOURCE);
	}
}
