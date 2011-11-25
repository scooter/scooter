/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.web.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.scooterframework.admin.ApplicationConfig;
import com.scooterframework.admin.Constants;
import com.scooterframework.admin.EnvConfig;
import com.scooterframework.admin.FilterManager;
import com.scooterframework.admin.FilterManagerFactory;
import com.scooterframework.common.logging.LogUtil;
import com.scooterframework.common.util.Converters;
import com.scooterframework.common.util.CurrentThreadCache;
import com.scooterframework.common.util.CurrentThreadCacheClient;
import com.scooterframework.common.util.DateUtil;
import com.scooterframework.common.util.Message;
import com.scooterframework.common.util.Util;
import com.scooterframework.common.validation.ValidationResults;
import com.scooterframework.orm.activerecord.ActiveRecord;
import com.scooterframework.orm.misc.JdbcPageListSource;
import com.scooterframework.orm.misc.Paginator;

/**
 * <p>
 * ActionControl class serves as a helper class for all controller classes.
 * </p>
 * 
 * @author (Fei) John Chen
 */
public class ActionControl {
	private static LogUtil log = LogUtil.getLogger(ActionControl.class.getName());
    
    /**
     * Returns controller name which handles the request. The controller name 
     * is linked to key 
     * {@link com.scooterframework.admin.Constants#CONTROLLER}.
     */
    public static String getController() {
        return CurrentThreadCacheClient.controller();
    }
    
    /**
     * Returns action name which handles the request. The action name 
     * is linked to key 
     * {@link com.scooterframework.admin.Constants#ACTION}.
     */
    public static String getAction() {
        return CurrentThreadCacheClient.action();
    }

	/**
	 * Returns model name.
	 * 
	 * <p>
	 * Model name must be set in current thread cache before this method is
	 * called.
	 * </p>
	 * 
	 * @return model name
	 */
    public static String getModel() {
        String model = CurrentThreadCacheClient.model();
        if (model == null) throw new IllegalArgumentException("Model name (Constants.MODEL: key.model) must be set first.");
        return model;
    }
    
    /**
     * Returns resource name which is linked to key 
     * {@link com.scooterframework.admin.Constants#RESOURCE}.
     */
    public static String getResource() {
        return CurrentThreadCacheClient.resource();
    }
    
    /**
     * <p>
     * Returns request extension which is linked to key 
     * {@link com.scooterframework.admin.Constants#FORMAT}.</p>
     * 
     * </p>
     * This is obtained from the request path. For example, if the request 
     * path is <tt>/blog/posts/1.xml</tt>, then the format is <tt>xml</tt>, 
     * which means the client wants the response be sent 
     * in <tt>xml</tt> format.</p>
     * 
     * <p>
     * The value of <tt>format()</tt> is what is used as a default format 
     * for response.</p>
     */
    public static String format() {
    	return CurrentThreadCacheClient.format();
    }
    
    /**
     * Returns URI path to a view template of the current controller's action.
     * <pre>
     * Examples:
     *   viewPath("show") : /WEB-INF/views/posts/show.jsp
     *   viewPath("index"): /WEB-INF/views/posts/index.jsp
     * </pre>
     * @param action action name
     * @return path to a view file
     */
    public static String viewPath(String action) {
        return viewPath(getController(), action);
    }
    
    /**
     * Returns URI path to a view template of a controller's action.
     * <pre>
     * Examples:
     *   viewPath("posts", "show") : /WEB-INF/views/posts/show.jsp
     *   viewPath("posts", "index"): /WEB-INF/views/posts/index.jsp
     * </pre>
     * 
     * @param controller controller name
     * @param action action name
     * @return path to a view file
     */
    public static String viewPath(String controller, String action) {
        return EnvConfig.getViewURI(controller, action);
    }
    
    /**
     * Returns model class name related to the underlying controller class name 
     * based on naming convention.
     * 
     * @return string model class name
     */
    public static String getModelClassName(Class<?> controllerClass) {
        return EnvConfig.getInstance().getModelClassNameFromControllerClassName(controllerClass.getName());
    }
    
    /**
     * Returns a paginator for the underlying model. 
     * 
     * <p>
     * Paging control options are constructed based on URL parameters. 
     * Therefore in URL, there must be parameters like <tt>limit</tt>, 
     * <tt>offset</tt>, <tt>npage</tt>, etc.</p>
     * 
     * <p>
     * For a complete list of paging options, see description of 
     * {@link com.scooterframework.orm.misc.Paginator} class.</p>
     * 
     * @param modelClass the model class
     * @return Paginator instance
     */
    public static Paginator jdbcPaginator(Class<? extends ActiveRecord> modelClass) {
    	Map<String, String> pagingOptions = Converters.convertMapToMapSS(ACH.getAC().getParameterDataAsMap());
        return jdbcPaginator(modelClass, pagingOptions, (Map<String, String>)null);
    }
    
    /**
     * Returns a paginator for the underlying model.
     * 
     * <p>
     * For a complete list of paging options, see description of 
     * {@link com.scooterframework.orm.misc.Paginator} class.</p>
     * 
     * @param modelClass the model class
     * @param pagingOptions  paging control options string
     * @return Paginator instance
     */
    public static Paginator jdbcPaginator(Class<? extends ActiveRecord> modelClass, String pagingOptions) {
        return jdbcPaginator(modelClass, Converters.convertStringToMap(pagingOptions), (Map<String, String>)null);
    }
    
    /**
     * Returns a paginator for the underlying model.
     * 
     * <p>
     * For a complete list of paging options, see description of 
     * {@link com.scooterframework.orm.misc.Paginator} class.</p>
     * 
     * <p>
     * For a complete list of SQL options, see description of <tt>options</tt>
     * in {@link com.scooterframework.orm.activerecord.ActiveRecord} 
     * class.</p>
     * 
     * @param modelClass     the model class
     * @param pagingOptions  paging control options string
     * @param sqlOptions     SQL options string
     * @return Paginator instance
     */
    public static Paginator jdbcPaginator(Class<? extends ActiveRecord> modelClass, String pagingOptions, String sqlOptions) {
        return jdbcPaginator(modelClass, Converters.convertStringToMap(pagingOptions), Converters.convertSqlOptionStringToMap(sqlOptions));
    }
    
    /**
     * Returns a paginator for the underlying model.
     * 
     * <p>
     * For a complete list of paging options, see description of 
     * {@link com.scooterframework.orm.misc.Paginator} class.</p>
     * 
     * <p>
     * For a complete list of SQL options, see description of <tt>options</tt>
     * in {@link com.scooterframework.orm.activerecord.ActiveRecord} 
     * class.</p>
     * 
     * @param modelClass     the model class
     * @param pagingOptions  paging control options string
     * @param sqlOptions     SQL options Map
     * @return Paginator instance
     */
    public static Paginator jdbcPaginator(Class<? extends ActiveRecord> modelClass, String pagingOptions, Map<String, String> sqlOptions) {
        return jdbcPaginator(modelClass, Converters.convertStringToMap(pagingOptions), sqlOptions);
    }
    
    /**
     * Returns a paginator for the underlying model.
     * 
     * <p>
     * For a complete list of paging options, see description of 
     * {@link com.scooterframework.orm.misc.Paginator} class.</p>
     * 
     * @param modelClass     the model class
     * @param pagingOptions  paging control options Map
     * @return Paginator instance
     */
    public static Paginator jdbcPaginator(Class<? extends ActiveRecord> modelClass, Map<String, ?> pagingOptions) {
        return jdbcPaginator(modelClass, Converters.convertMapToMapSS(pagingOptions), (Map<String, String>)null);
    }
    
    /**
     * Returns a paginator for the underlying model.
     * 
     * <p>
     * For a complete list of paging options, see description of 
     * {@link com.scooterframework.orm.misc.Paginator} class.</p>
     * 
     * <p>
     * For a complete list of SQL options, see description of <tt>options</tt>
     * in {@link com.scooterframework.orm.activerecord.ActiveRecord} 
     * class.</p>
     * 
     * @param modelClass     the model class
     * @param pagingOptions  paging control options Map
     * @param sqlOptions     SQL options string
     * @return Paginator instance
     */
    public static Paginator jdbcPaginator(Class<? extends ActiveRecord> modelClass, Map<String, String> pagingOptions, String sqlOptions) {
        return jdbcPaginator(modelClass, pagingOptions, Converters.convertSqlOptionStringToMap(sqlOptions));
    }
    
    /**
     * Returns a paginator for the underlying model.
     * 
     * <p>
     * For a complete list of paging options, see description of 
     * {@link com.scooterframework.orm.misc.Paginator} class.</p>
     * 
     * <p>
     * For a complete list of SQL options, see description of <tt>options</tt>
     * in {@link com.scooterframework.orm.activerecord.ActiveRecord} 
     * class.</p>
     * 
     * @param modelClass     the model class
     * @param pagingOptions  paging control options Map
     * @param sqlOptions     SQL options Map
     * @return Paginator instance
     */
    public static Paginator jdbcPaginator(Class<? extends ActiveRecord> modelClass, Map<String, String> pagingOptions, Map<String, String> sqlOptions) {
        return new Paginator(new JdbcPageListSource(modelClass, sqlOptions), pagingOptions);
    }
    
    /**
     * Returns all request parameters as a map. This includes data in both 
     * parameter scope and request scope. 
     * 
     * @return a map of all request parameters
     */
    public static Map<String, Object> params() {
        return ACH.getAC().getAllRequestDataAsMap();
    }
    
    /**
     * Returns all request parameters as a map for those keys that have a
     * prefix.
     * 
     * @param keyPrefix
     * @return a map of request parameters
     */
    public static Map<String, Object> paramsWithPrefix(String keyPrefix) {
        return ACH.getAC().getAllRequestDataAsMap(keyPrefix);
    }
    
    /**
     * Returns a value corresponding to a key in request parameters or request 
     * attributes.
     * 
     * @param key name of a key in the request parameters
     * @return a value corresponding to a key in the request parameters
     */
    public static String params(String key) {
    	Object o = ACH.getAC().getFromAllRequestData(key);
        return Util.getSafeString(o);
    }
    
    /**
     * Alias of method <tt>params(String key)</tt>.
     * 
     * Returns a value corresponding to a key in request parameters or request 
     * attributes.
     * 
     * @param key name of a key in the request parameters
     * @return a value corresponding to a key in the request parameters
     */
    public static String p(String key) {
        return params(key);
    }
    
    /**
     * Returns a string array corresponding to a key in request parameters or 
     * request attributes.
     * 
     * @param key name of a key in the request parameters
     * @return a string array corresponding to a key in the request parameters
     */
    public static String[] pArray(String key) {
    	Object o = ACH.getAC().getFromAllRequestData(key);
    	if (o == null) return new String[0];
    	
    	String[] valueAry = null;
    	if (o instanceof Object[]) {
    		valueAry = (String[])o;
    	}
    	else {
    		valueAry = new String[1];
    		valueAry[0] = (String)o;
    	}
        return valueAry;
    }
    
    /**
     * Returns a date instance corresponding to a key in request parameters or 
     * request attributes.
     * 
     * @param key name of a key in the request parameters
     * @return a date instance corresponding to a key in the request parameters
     */
    public static Date pDate(String key) {
        return DateUtil.parseDate(p(key));
    }
    
    /**
     * Returns a date instance corresponding to a key in request parameters or 
     * request attributes.
     * 
     * @param key      name of a key in the request parameters
     * @param pattern  the pattern describing the date and time format
     * @return a date instance corresponding to a key in the request parameters
     */
    public static Date pDate(String key, String pattern) {
        return DateUtil.parseDate(p(key), pattern);
    }
    
    /**
     * Returns true or false corresponding to a key in request parameters or 
     * request attributes.
     * 
     * Only string of "true"--regardless of case--would return <tt>true</tt>.
     * 
     * @param key name of a key in the request parameters
     * @return true or false corresponding to a key in the request parameters
     */
    public static Boolean pBoolean(String key) {
        return Boolean.valueOf(p(key));
    }
    
    /**
     * Checks if this is an AJAX request.
     * 
     * @return true if this is an AJAX request.
     */
    public static boolean isAjaxRequest() {
    	return (params(Constants.AJAX_REQUEST) != null)?true:false;
    }
    
    /**
     * Checks if this is a file-upload request or multipart request.
     * 
     * @return true if this is a file-upload request or multipart request.
     */
    public static boolean isFileUploadRequest() {
    	return ServletFileUpload.isMultipartContent(ACH.getWAC().getHttpServletRequest());
    }
    
    /**
     * Alias of method <tt>getUploadedFile(String key)</tt>.
     * 
     * Returns an upload file which is an instance of <tt>UploadFile</tt>. 
     * 
     * @return an instance of <tt>UploadFile</tt>
     * @throws Exception
     */
    public static UploadFile paramsFile(String key) throws Exception {
        return getUploadFile(key);
    }
    
    /**
     * Alias of method <tt>getUploadFiles()</tt>.
     * 
     * Returns a list of upload files. 
     * 
     * @return a list of <tt>UploadFile</tt> instances
     * @throws Exception
     */
    public static List<UploadFile> paramsFiles() throws Exception {
        return getUploadFiles();
    }
    
    /**
     * Alias of method <tt>getUploadFilesMap()</tt>.
     * 
     * Returns a map of upload files. In each key/value pair, the key is the 
     * field name in the HTTP form, and the value is a <tt>UploadFile</tt> instance.
     * 
     * @return a map of field/upload file (UploadFile) pairs
     * @throws Exception
     */
    public static Map<String, UploadFile> paramsFilesMap() throws Exception {
        return getUploadFilesMap();
    }
    
    /**
     * Alias of method <tt>paramsFile(String key)</tt>.
     * 
     * Returns an upload file which is an instance of <tt>UploadFile</tt>. 
     * 
     * @return an instance of <tt>UploadFile</tt>
     * @throws Exception
     */
    public static UploadFile pFile(String key) throws Exception {
        return paramsFile(key);
    }
    
    /**
     * Alias of method <tt>paramsFiles()</tt>.
     * Returns a list of upload files. 
     * 
     * @return a list of <tt>UploadFile</tt> instances
     * @throws Exception
     */
    public static List<UploadFile> pFiles() throws Exception {
        return paramsFiles();
    }
    
    /**
     * Alias of method <tt>paramsFilesMap()</tt>.
     * 
     * Returns a map of upload files. In each key/value pair, the key is the 
     * field name in the HTTP form, and the value is a <tt>UploadFile</tt> instance.
     * 
     * @return a map of field/upload file (UploadFile) pairs
     * @throws Exception
     */
    public static Map<String, UploadFile> pFilesMap() throws Exception {
        return paramsFilesMap();
    }
    
    /**
     * Returns a value corresponding to a key in the request parameters. The 
     * case of the key string is ignored.
     * 
     * @param key name of a key in the request parameters
     * @return a value corresponding to a key in the request parameters
     */
    public static String paramsIgnoreCase(String key) {
        Object o = ACH.getAC().getFromParameterDataIgnoreCase(key);
        return Util.getSafeString(o);
    }
    
    /**
     * Returns field values of a route.
     * 
     * @return field values of a route.
     */
	public static Map<String, String> routeFieldValues() {
        return CurrentThreadCacheClient.fieldValues();
    }
    
    /**
     * Returns value of a field for route.
     * 
     * @param field the field
     * @return value of a field for route.
     */
    public static String routeFieldValue(String field) {
        return routeFieldValues().get(field);
    }
    
    public static Object getFromThreadData(String key) {
        return ActionContext.getFromThreadData(key);
    }
    
    public static Object getFromParameterData(String key) {
        return ACH.getAC().getFromParameterData(key);
    }
    
    public static Object getFromRequestData(String key) {
        return ACH.getAC().getFromRequestData(key);
    }
    
    public static Object getFromSessionData(String key) {
        return ACH.getAC().getFromSessionData(key);
    }
    
    public static Object getFromContextData(String key) {
        return ACH.getAC().getFromContextData(key);
    }
    
    public static Object getFromGlobalData(String key) {
        return ActionContext.getFromGlobalData(key);
    }
    
    public static void storeToThread(String key, Object obj) {
        ActionContext.storeToThread(key, obj);
    }
    
    public static void storeToRequest(String key, Object obj) {
        ACH.getAC().storeToRequest(key, obj);
    }
    
    public static void storeToSession(String key, Object obj) {
        ACH.getAC().storeToSession(key, obj);
    }
    
    public static void storeToContext(String key, Object obj) {
        ACH.getAC().storeToContext(key, obj);
    }
    
    public static void storeToGlobal(String key, Object obj) {
        ActionContext.storeToGlobal(key, obj);
    }
    
    public static void removeFromThreadData(String key) {
        ActionContext.removeFromThreadData(key);
    }
    
    public static void removeFromRequestData(String key) {
        ACH.getAC().removeFromRequestData(key);
    }
    
    public static void removeFromSessionData(String key) {
        ACH.getAC().removeFromSessionData(key);
    }
    
    public static void removeFromContextData(String key) {
        ACH.getAC().removeFromContextData(key);
    }
    
    public static void removeFromGlobalData(String key) {
        ActionContext.removeFromGlobalData(key);
    }
    
    public static void remove(String key) {
        ACH.getAC().remove(key);
    }
    
    public static void removeAllSessionData() {
        ACH.getAC().removeAllSessionData();
    }
    
    /**
     * Binds data to view attribute for view rendering.
     * 
     * @param key  a string representing a place holder on view
     * @param data the data value to be filled in the view
     */
    public static void setViewData(String key, Object data) {
    	storeToRequest(key, data);
    }
    
    /**
     * Returns the current HTTP Servlet request instance.
     */
    public static HttpServletRequest getHttpServletRequest() {
    	return ACH.getWAC().getHttpServletRequest();
    }
    
    /**
     * Returns the current HTTP Servlet response instance.
     */
    public static HttpServletResponse getHttpServletResponse() {
    	return ACH.getWAC().getHttpServletResponse();
    }
    
    /**
     * Returns the HTTP ServletContext instance.
     */
    public static ServletContext getServletContext() {
        return ACH.getWAC().getHttpServletRequest().getSession().getServletContext();
    }
    
    /**
     * Returns content type of HTTP request.
     */
    public static String getHttpRequestContentType() {
    	return ACH.getWAC().getHttpServletRequest().getContentType();
    }
    
    /**
     * Records a flash message of a specific <tt>type</tt>. The message can be 
     * either a sentence or a message key in a messages.properties file. 
     * 
     * @param type      flash message type
     * @param message   the message or message key
     */
    public static void flash(String type, String message) {
    	Flash.flash(type, message);
    }
    
    /**
     * Records a flash message of a specific <tt>type</tt>. The message can be 
     * either a sentence or a message key in a messages.properties file. 
     * 
     * @param type      flash message type
     * @param message   the message or message key
     * @param value     a value that can be used in the message
     */
    public static void flash(String type, String message, Object value) {
    	Flash.flash(type, message, value);
    }
    
    /**
     * Records a flash message of a specific <tt>type</tt>. The message can be 
     * either a sentence or a message key in a messages.properties file. 
     * 
     * @param type      flash message type
     * @param message   the message or message key
     * @param value0    a value that can be used in the message
     * @param value1    a value that can be used in the message
     */
    public static void flash(String type, String message, Object value0, Object value1) {
    	Flash.flash(type, message, value0, value1);
    }
    
    /**
     * Records a flash message of a specific <tt>type</tt>. The message can be 
     * either a sentence or a message key in a messages.properties file. 
     * 
     * @param type      flash message type
     * @param message   the message or message key
     * @param value0    a value that can be used in the message
     * @param value1    a value that can be used in the message
     * @param value2    a value that can be used in the message
     */
    public static void flash(String type, String message, Object value0, Object value1, Object value2) {
    	Flash.flash(type, message, value0, value1, value2);
    }
    
    /**
     * Records a flash message of a specific <tt>type</tt>. The <tt>message</tt> 
     * is of type {@link com.scooterframework.common.util.Message} or its subclass. 
     * 
     * @param type      flash message type
     * @param message   a {@link com.scooterframework.common.util.Message} object
     */
    public static void flash(String type, Message message) {
        Flash.flash(type, message);
    }
    
    /**
     * Records a flash message of <tt>error</tt> type. The message can be 
     * either a sentence or a message key in a messages.properties file. 
     * 
     * @param message   the message or message key
     */
    public static void flashError(String message) {
    	Flash.error(message);
    }
    
    /**
     * Records a flash message of <tt>error</tt> type. The message can be 
     * either a sentence or a message key in a messages.properties file. 
     * 
     * @param message   the message or message key
     * @param value     a value that can be used in the message
     */
    public static void flashError(String message, Object value) {
    	Flash.error(message, value);
    }
    
    /**
     * Records a flash message of <tt>error</tt> type. The message can be 
     * either a sentence or a message key in a messages.properties file. 
     * 
     * @param message   the message or message key
     * @param value0    a value that can be used in the message
     * @param value1    a value that can be used in the message
     */
    public static void flashError(String message, Object value0, Object value1) {
    	Flash.error(message, value0, value1);
    }
    
    /**
     * Records a flash message of <tt>error</tt> type. The message can be 
     * either a sentence or a message key in a messages.properties file. 
     * 
     * @param message   the message or message key
     * @param value0    a value that can be used in the message
     * @param value1    a value that can be used in the message
     * @param value2    a value that can be used in the message
     */
    public static void flashError(String message, Object value0, Object value1, Object value2) {
    	Flash.error(message, value0, value1, value2);
    }
    
    /**
     * Records a flash message of <tt>error</tt> type. The <tt>message</tt> 
     * is of type {@link com.scooterframework.common.util.Message} or its subclass. 
     * 
     * @param message   a {@link com.scooterframework.common.util.Message} object
     */
    public static void flashError(Message message) {
        Flash.error(message);
    }
    
    /**
     * Records a flash message of <tt>notice</tt> type. The message can be 
     * either a sentence or a message key in a messages.properties file. 
     * 
     * @param message   the message or message key
     */
    public static void flashNotice(String message) {
    	Flash.notice(message);
    }
    
    /**
     * Records a flash message of <tt>notice</tt> type. The message can be 
     * either a sentence or a message key in a messages.properties file. 
     * 
     * @param message   the message or message key
     * @param value     a value that can be used in the message
     */
    public static void flashNotice(String message, Object value) {
    	Flash.notice(message, value);
    }
    
    /**
     * Records a flash message of <tt>notice</tt> type. The message can be 
     * either a sentence or a message key in a messages.properties file. 
     * 
     * @param message   the message or message key
     * @param value0    a value that can be used in the message
     * @param value1    a value that can be used in the message
     */
    public static void flashNotice(String message, Object value0, Object value1) {
    	Flash.notice(message, value0, value1);
    }
    
    /**
     * Records a flash message of <tt>notice</tt> type. The message can be 
     * either a sentence or a message key in a messages.properties file. 
     * 
     * @param message   the message or message key
     * @param value0    a value that can be used in the message
     * @param value1    a value that can be used in the message
     * @param value2    a value that can be used in the message
     */
    public static void flashNotice(String message, Object value0, Object value1, Object value2) {
    	Flash.notice(message, value0, value1, value2);
    }
    
    /**
     * Records a flash message of <tt>notice</tt> type. The <tt>message</tt> 
     * is of type {@link com.scooterframework.common.util.Message} or its subclass. 
     * 
     * @param message   a {@link com.scooterframework.common.util.Message} object
     */
    public static void flashNotice(Message message) {
    	Flash.notice(message);
    }
    
    /**
     * Returns an instance of Validators. 
     * 
     * <p>
     * Subclass must override this method if a different validator is used.
     * </p>
     * 
     * @return Validators object
     */
    public static ActionValidators validators() {
    	ActionValidators validators = (ActionValidators)CurrentThreadCache.get(KEY_Validators);
    	if (validators == null) {
    		validators = new ActionValidators();
    		CurrentThreadCache.set(KEY_Validators, validators);
    	}
        return validators;
    }
    
    private static final String KEY_Validators = "key.Validators";
    private static final String KEY_ValidationResults = "key.ValidationResults";
    
    /**
     * Returns controller validation results.
     */
    public static ValidationResults currentValidationResults() {
        ValidationResults vr = (ValidationResults)CurrentThreadCache.get(KEY_ValidationResults);
        if (vr == null) {
            vr = new ValidationResults();
            CurrentThreadCache.set(KEY_ValidationResults, vr);
        }
        return vr;
    }
    
    /**
     * Cleans up cached ValidationResults.
     */
    public static void cleanupValidationResults() {
        CurrentThreadCache.set(KEY_ValidationResults, null);
    }
    
    /**
     * Checks if validation failed.
     * 
     * @return true if validation is failed, false otherwise.
     */
    public static boolean validationFailed() {
        return currentValidationResults().failed();
    }
    
    
    /**
     * Returns an error-tagged content string for error output.
     * 
     * @param content the error content
     * @return a error-tagged content string
     */
    public static String error(String content) {
        return ActionResult.error(content);
    }
    
    /**
     * Returns a html-tagged content string for html output.
     * 
     * @param content the html content
     * @return a html-tagged content string
     */
    public static String html(String content) {
        return ActionResult.html(content);
    }
    
    /**
     * Returns a text-tagged content string for plain-text output.
     * 
     * @param content the text content
     * @return a text-tagged content string
     */
    public static String text(String content) {
        return ActionResult.text(content);
    }
    
    /**
     * Returns a xml-tagged content string for xml output.
     * 
     * @param content the xml content
     * @return a xml-tagged content string
     */
    public static String xml(String content) {
        return ActionResult.xml(content);
    }
    
    /**
     * Returns a forward-tagged URI string with a query string. The query string is 
     * formed by listing all primary key and value pairs of the record instance.
     * 
     * @param uri an URI string
     * @param record an ActiveRecord instance
     * @return a formatted forward-tagged URI string
     */
    public static String forwardTo(String uri, ActiveRecord record) {
    	return ActionResult.forwardTo(uri, record);
    }
    
    /**
     * Returns a forward-tagged URI string with a query string.
     * 
     * @param uri an URI string
     * @param nameValuePairs a map of name and value pairs as HTTP query string
     * @return a formatted forward-tagged URI string
     */
    public static String forwardTo(String uri, Map<String, Object> nameValuePairs) {
        return ActionResult.forwardTo(uri, nameValuePairs);
    }
    
    /**
     * Returns a forward-tagged URI string with a query string.
     * 
     * @param uri an URI string
     * @param nameValuePairs a string of name and value pairs as HTTP query string
     * @return a formatted forward-tagged URI string
     */
    public static String forwardTo(String uri, String nameValuePairs) {
        return ActionResult.forwardTo(uri, nameValuePairs);
    }
    
    /**
     * Returns a forward-tagged URI string.
     * 
     * @param uri an URI string
     * @return a formatted forward-tagged URI string
     */
    public static String forwardTo(String uri) {
        return ActionResult.forwardTo(uri);
    }
    
    /**
     * Returns a redirect-tagged URI string with a query string. The query string is 
     * formed by listing all primary key and value pairs of the record instance.
     * 
     * @param uri an URI string
     * @param record an ActiveRecord instance
     * @return a formatted redirect-tagged URI string
     */
    public static String redirectTo(String uri, ActiveRecord record) {
        return ActionResult.redirectTo(uri, record);
    }
    
    /**
     * Returns a redirect-tagged URI string with a query string.
     * 
     * @param uri an URI string
     * @param nameValuePairs a map of name and value pairs as HTTP query string
     * @return a formatted redirect-tagged URI string
     */
    public static String redirectTo(String uri, Map<String, Object> nameValuePairs) {
        return ActionResult.redirectTo(uri, nameValuePairs);
    }
    
    /**
     * Returns a redirect-tagged URI string with a query string.
     * 
     * @param uri an URI string
     * @param nameValuePairs a string of name and value pairs as HTTP query string
     * @return a formatted redirect-tagged URI string
     */
    public static String redirectTo(String uri, String nameValuePairs) {
        return ActionResult.redirectTo(uri, nameValuePairs);
    }
    
    /**
     * Returns a redirect-tagged URI string.
     * 
     * @param uri an URI string
     * @return a formatted redirect-tagged URI string
     */
    public static String redirectTo(String uri) {
        return ActionResult.redirectTo(uri);
    }
    
    /**
     * Returns root path to the application.
     */
    public static String applicationPath() {
    	return ApplicationConfig.getInstance().getApplicationPath();
    }
    
    /**
     * Returns a list of upload files. Each item in the list is an instance 
     * of <tt>java.io.File</tt> instance.
     * 
     * @param fileRepository
     * @return a list of upload files (File)
     * @throws Exception
     */
    public static List<File> getUploadFilesAsFiles(String fileRepository) throws Exception {
    	List<File> files = new ArrayList<File>();
		List<FileItem> items = prepareFileItems();
		for (FileItem item : items) {
			if (!item.isFormField() && !"".equals(item.getName())) {
				File f = new File(fileRepository + File.separator + item.getName());
				files.add(f);
			}
		}
		return files;
    }
    
    /**
     * Returns a list of upload files. Each item in the list is an instance 
     * of <tt>UploadFile</tt> instance.
     * 
     * @return a list of upload files (UploadFile)
     * @throws Exception
     */
    public static List<UploadFile> getUploadFiles() throws Exception {
    	List<UploadFile> files = new ArrayList<UploadFile>();
		List<FileItem> items = prepareFileItems();
		for (FileItem item : items) {
			if (!item.isFormField() && !"".equals(item.getName())) {
				files.add(new UploadFile(item));
			}
		}
		return files;
    }
    
    /**
     * Returns a map of upload files. In each key/value pair, the key is the 
     * field name in the HTTP form, and the value is a <tt>UploadFile</tt> instance.
     * 
     * @return a map of field/upload file (UploadFile) pairs
     * @throws Exception
     */
    public static Map<String, UploadFile> getUploadFilesMap() throws Exception {
    	Map<String, UploadFile> files = new HashMap<String, UploadFile>();
		List<FileItem> items = prepareFileItems();
		for (FileItem item : items) {
			if (!item.isFormField() && !"".equals(item.getName())) {
				files.put(item.getFieldName(), new UploadFile(item));
			}
		}
		return files;
    }
    
    /**
     * Returns an upload file which is an instance of <tt>UploadFile</tt>. 
     * 
     * @return an instance of <tt>UploadFile</tt>
     * @throws Exception
     */
    public static UploadFile getUploadFile(String key) throws Exception {
		return (UploadFile)getUploadFilesMap().get(key);
    }
    
    @SuppressWarnings("unchecked")
	private static List<FileItem> prepareFileItems() throws Exception {
    	List<FileItem> files = (List<FileItem>)CurrentThreadCache.get(Constants.FILE_UPLOAD_REQUEST_FILES);
		return files;
    }
    
    /**
     * Renders content. The format of the response is derived from request 
     * extension. The default format is <tt>html</tt> which is defined by 
     * {@link com.scooterframework.admin.Constants#DEFAULT_RESPONSE_FORMAT}.
     * 
	 * @param content  The content to be sent.
     * @return a token of render
     */
    public static String render(Object content) {
    	return render(content, format());
    }
    
    /**
     * Renders content associated with a specific request format.
     * 
     * The default format is <tt>html</tt> which is defined by 
     * {@link com.scooterframework.admin.Constants#DEFAULT_RESPONSE_FORMAT}.
     * 
	 * @param content  The content to be sent.
	 * @param format  The request format.
     * @return a token of render
     */
    public static String render(Object content, String format) {
    	if (content == null) {
    		log.warn("No rendering for a null content with format " + format);
			getHttpServletRequest().setAttribute(Constants.REQUEST_RENDERED, "true");
			return null;
    	}
    	
    	if (format == null) {
    		format = Constants.DEFAULT_RESPONSE_FORMAT;
    	}
    	
        ContentHandler handler = ContentHandlerFactory.getContentHandler(format);
        if (handler != null) {
        	try {
				handler.handle(
					getHttpServletRequest(), 
					getHttpServletResponse(), 
					content, 
					format);
				getHttpServletRequest()
						.setAttribute(Constants.REQUEST_RENDERED, "true");
			} catch (Exception ex) {
				String error = "Error in render() for format \"" 
					+ format + "\", because " + ex.getMessage();
				throw new RuntimeException(error);
			}
        }
        else {
			throw new IllegalArgumentException(
					"There is no handler found for format \""
							+ format
							+ "\". You may create your own as a plugin by "
							+ "extending the Plugin class and "
							+ "implementing the ContentHandler interface.");
        }
        
        return ActionResult.TAG_RENDER;
    }
    
    /**
     * Publishes a file for display.
     * 
	 * @param file  The file to be published.
     */
    public static void publishFile(File file) {
		publishFile(file, file.getName(), false);
    }
    
    /**
     * Publishes a file for display.
     * 
	 * @param file  The file to be published.
	 * @param mimeType  The content MIME type.
     */
    public static void publishFile(File file, String mimeType) {
		publishFile(file, file.getName(), mimeType, false);
    }
    
    /**
     * Publishes a file. Its MIME type is detected from the file extension.
     * 
     * <p>
     * The file extension detection rule is as follows:
     * 1. If the <tt>file</tt> input has an extension, use it. Otherwise, 
     * 2. If the <tt>displayableName</tt> input has an extension, use it.
     * 
	 * @param file  The file to be published.
	 * @param displayableName  The display name of the file in the download dialog.
	 * @param forDownload  indicates whether this is for file download or display.
     */
    public static void publishFile(File file, String displayableName, boolean forDownload) {
		String extension = null;
		String fn = file.getName();
		if (fn != null && !"".equals(fn) && fn.indexOf('.') != -1) {
			extension = fn.substring(fn.lastIndexOf('.') + 1);
		}
		else if (displayableName != null && displayableName.indexOf('.') != -1) {
			extension = displayableName.substring(displayableName.lastIndexOf('.') + 1);
		}
		
    	String mimeType = "";
    	if (extension != null && !"".equals(extension)) {
    		mimeType = EnvConfig.getInstance().getMimeType(extension);
    	}
		
		publishFile(file, displayableName, mimeType, forDownload);
    }
    
    /**
     * Publishes a file. If the <tt>mimeType</tt> is empty, it is default to 
     * <tt>application/octet-stream</tt>.
     * 
	 * @param file  The file to be published.
	 * @param displayableName  The display name of the file in the download dialog.
	 * @param mimeType  The content MIME type.
	 * @param forDownload  indicates whether this is for file download or display.
     */
    public static void publishFile(File file, String displayableName, String mimeType, boolean forDownload) {
    	try {
    		ContentHandlerHelper.publish(ACH.getWAC().getHttpServletResponse(), file, displayableName, mimeType, forDownload);
			ACH.getWAC().getHttpServletRequest()
					.setAttribute(Constants.REQUEST_RENDERED, "true");
		} catch (Exception ex) {
			String error = "Error in publishFile() for file \"" 
				+ file + "\", because " + ex.getMessage();
			throw new RuntimeException(error);
		}
    }
    
    /**
     * Checks whether a file is a text file. 
     * 
     * @param file  the file to check
     * @return true if it is a text file.
     */
    public static boolean isTextFile(File file) {
    	return EnvConfig.getInstance().isTextFile(file);
    }
    
    /**
     * Returns the file extension.
     * 
     * @param file the file to check
     * @return file extension or null
     */
    public static String getFileExtension(File file) {
    	String fName = file.getName();
    	int lastDot = fName.lastIndexOf('.');
    	return (lastDot != -1)?(fName.substring(lastDot + 1)):null;
    }
    
    /**
     * Returns all view data as a map. This includes data in both 
     * parameter scope and request scope. 
     * 
     * @return a map of all view data
     */
    public static Map<String, Object> getViewDataMap() {
    	return params();
    }
    
    /**
     * Renders a view template file with all properties in the container. 
     * 
     * The file extension of the view template is used to look up related 
     * template engine. If the <tt>view</tt> does not have an extension 
     * specified, the default extension is defined by <tt>view.extension</tt> 
     * property in the <tt>environment.properties</tt> file. 
     * 
     * <p>Examples: </p>
     * <pre>
     *   //render view file show.jsp
     *   renderView("show");
     *   
     *   //render view .../WEB-INF/views/products/show.jsp
     *   renderView("products/show");
     *   
     *   //render view file /home/foo/templates/show.st with StringTemplate engine
     *   renderView("/home/foo/templates/show.st");
     *   
     *   //render view file show.jsp and return result in text format
     *   renderView("show", "text");
     * </pre>
     * 
     * @param view  The render template file
     * @return rendered content
     */
    public static String renderView(String view) {
    	return renderView(view, getViewDataMap());
    }
    
    /**
     * Renders a view template file with all properties in the container. 
     * 
     * The file extension of the view template is used to look up related 
     * template engine. If the <tt>view</tt> does not have an extension 
     * specified, the default extension is defined by <tt>view.extension</tt> 
     * property in the <tt>environment.properties</tt> file. 
     * 
     * <p>Examples: </p>
     * <pre>
     *   //render view file show.jsp
     *   renderView("show");
     *   
     *   //render view .../WEB-INF/views/products/show.jsp
     *   renderView("products/show");
     *   
     *   //render view file /home/foo/templates/show.st with StringTemplate engine
     *   renderView("/home/foo/templates/show.st");
     *   
     *   //render view file show.jsp and return result in text format
     *   renderView("show", "text");
     * </pre>
     * 
     * <p>The response <tt>format</tt> does not apply to <tt>jsp</tt> views.</p>
     * 
     * @param view  The render template file
     * @param format  the response format
     * @return rendered content
     */
    public static String renderView(String view, String format) {
    	return renderView(view, format, getViewDataMap());
    }
    
    /**
     * Renders a view template file with view data in <tt>viewDataMap</tt>. 
     * 
     * The file extension of the view template is used to look up related 
     * template engine. If the <tt>view</tt> does not have an extension 
     * specified, the default extension is defined by <tt>view.extension</tt> 
     * property in the <tt>environment.properties</tt> file. 
     * 
     * @param view  the render template file
     * @param viewDataMap  data (name/value pairs) to be passed to the view
     * @return rendered content
     */
    public static String renderView(String view, Map<String, Object> viewDataMap) {
    	return renderView(view, format(), viewDataMap);
    }
    
    /**
     * Renders a view template file with view data in <tt>viewDataMap</tt>. 
     * 
     * The file extension of the view template is used to look up related 
     * template engine. If the <tt>view</tt> does not have an extension 
     * specified, the default extension is defined by <tt>view.extension</tt> 
     * property in the <tt>environment.properties</tt> file. 
     * 
     * <p>Examples:</p>
     * <pre>
     * //return a view coded in String Template as html
     * return renderView("paged_list.st", "html", map);
     * 
     * //return a view coded in FreeMarker Template as text
     * return renderView("paged_list.ftl", "text", map);
     * </pre>
     * 
     * @param view  the render template file
     * @param format  response format of the render template file
     * @param viewDataMap  data (name/value pairs) to be passed to the view
     * @return rendered content
     */
    public static String renderView(String view, String format, Map<String, Object> viewDataMap) {
    	if (view == null) 
    		throw new IllegalArgumentException("View can not be null for rendering.");
    	
    	if (format == null) {
    		format = Constants.DEFAULT_RESPONSE_FORMAT;
    	}
    	
    	String viewPath = view;
    	String viewExtension = "";
    	String viewFile = "";
    	int lastDot = view.lastIndexOf('.');
    	if (lastDot != -1) {
    		viewExtension = view.substring(lastDot + 1);
    	}
    	else {
    		viewExtension = EnvConfig.getInstance().getViewExtension();
    		viewPath = (viewExtension.startsWith("."))?
    				(view + viewExtension):(view + '.' + viewExtension);
    	}
    	
    	int lastSlash = viewPath.lastIndexOf('/');
    	if (lastSlash == -1 && File.separatorChar != '/') {
    		lastSlash = viewPath.lastIndexOf(File.separatorChar);
    	}
    	if (lastSlash != -1) {
    		File p = new File(viewPath);
    		if (!p.exists()) {
    			viewPath = viewPath("", viewPath);
    			viewFile = applicationPath() + viewPath;
    		}
    	}
    	else {
			viewPath = viewPath(viewPath);
			viewFile = applicationPath() + viewPath;
    	}
    	
    	//handle view
    	try {
            HttpServletRequest request = getHttpServletRequest();
            HttpServletResponse response = getHttpServletResponse();
            
	    	if (viewExtension.equalsIgnoreCase("jsp") || viewExtension.equalsIgnoreCase(".jsp")) {
	    		doForward(viewPath, request, response);
	    	}
	    	else {
	    		TemplateHandler handler = 
	    			TemplateHandlerFactory.getTemplateHandler(viewExtension);
	    		if (handler == null) 
	    			throw new IllegalArgumentException("There is no " + 
	    					"template handler found for view template " + 
	    					"of type \"" + viewExtension + "\".");
	    		String result = handler.handle(new File(viewFile), viewDataMap);
	    		render(result, format);
	    	}
    		request.setAttribute(Constants.REQUEST_RENDERED, "true");
        } catch (Exception ex) {
        	String errorMessage = "Failed to render view \"" + viewFile + "\" because " + ex.getMessage();
        	log.error(errorMessage);
        	throw new NoTemplateHandlerException(errorMessage, viewExtension);
        }
    	
    	return null;
    }
    
    /**
     * <p>Do a forward to specified URI using a <tt>RequestDispatcher</tt>.
     * This method is used by all methods needing to do a forward.</p>
     *
     * @param uri Context-relative URI to forward to
     * @param request HTTP servlet request
     * @param response HTTP servlet response
     * @throws java.io.IOException
     * @throws javax.servlet.ServletException
     */
    public static void doForward(
    		String uri,
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException, ServletException {
        log.debug("doForward: " + uri);
    	if (uri == null) return;
        
        if (uri != null && !uri.startsWith("/")) uri = "/" + uri;
        
        RequestDispatcher rd = request.getSession().getServletContext().getRequestDispatcher(uri);
        
        if (rd == null) {
            uri = "/WEB-INF/views/404.jsp";
            log.error("Unable to locate \"" + uri + "\", forward to " + uri);
            rd = getServletContext().getRequestDispatcher(uri);
        }
        rd.forward(request, response);
    }
    
    /**
     * Returns the FilterManager for a class type.
     */
    public static FilterManager filterManagerFor(Class<?> clazz) {
    	return FilterManagerFactory.getInstance().getFilterManager(clazz);
    }
}
