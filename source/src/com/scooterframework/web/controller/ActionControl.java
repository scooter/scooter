/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.web.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.scooterframework.admin.ApplicationConfig;
import com.scooterframework.admin.Constants;
import com.scooterframework.admin.EnvConfig;
import com.scooterframework.admin.FilterManager;
import com.scooterframework.admin.FilterManagerFactory;
import com.scooterframework.common.util.Converters;
import com.scooterframework.common.util.CurrentThreadCache;
import com.scooterframework.common.util.ExpandedMessage;
import com.scooterframework.common.util.Message;
import com.scooterframework.common.util.Util;
import com.scooterframework.common.validation.ValidationResults;
import com.scooterframework.orm.activerecord.ActiveRecord;
import com.scooterframework.orm.misc.JdbcPageListSource;
import com.scooterframework.orm.misc.Paginator;
import com.scooterframework.web.route.RouteConstants;

/**
 * <p>
 * ActionControl class serves as base super class for all controller classes.</p>
 * 
 * <p>
 * Action filters are registered through this class. The order of filters 
 * depends on the order they appear in the filter registration methods:
 * beforeFilter and afterFilter. A subclass may change this order by 
 * implementing either of these methods and placing calls to the same methods 
 * in super class at a different place in their own implemented methods.</p>
 * 
 * <p>
 * A subclass should skip some of its superclass' filters by using 
 * skipBeforeFilter and skipAfterFilter methods.</p>
 * 
 * <p>
 * To use features of this class, you must subclass this class.</p>
 * 
 * @author (Fei) John Chen
 */
public class ActionControl {
    
    /**
     * Returns controller name which handles the request. The controller name 
     * is linked to key 
     * {@link com.scooterframework.admin.Constants#CONTROLLER}.
     */
    public static String getController() {
        return (String)ACH.getAC().getFromRequestData(Constants.CONTROLLER);
    }
    
    /**
     * Returns action name which handles the request. The action name 
     * is linked to key 
     * {@link com.scooterframework.admin.Constants#ACTION}.
     */
    public static String getAction() {
        return (String)ACH.getAC().getFromRequestData(Constants.ACTION);
    }
    
    /**
     * Returns model name. 
     * 
     * <p>Model name must be set in request scope in a processor class before 
     * this method is called. It is mapped to attribute <tt>Constants.MODEL</tt>
     * (<tt>scooter.key.model</tt>) in HTTP servlet request.</p>
     * 
     * @return model name
     */
    public static String getModel() {
        String model = (String) ACH.getAC().getFromRequestData(Constants.MODEL);
        if (model == null) throw new IllegalArgumentException("Model name (Constants.MODEL: key.model) must be set first.");
        return model;
    }
    
    /**
     * Returns resource name which is linked to key 
     * {@link com.scooterframework.admin.Constants#RESOURCE}.
     */
    public static String getResource() {
        return (String)ACH.getAC().getFromRequestData(Constants.RESOURCE);
    }
    
    /**
     * Returns request extension which is linked to key 
     * {@link com.scooterframework.admin.Constants#FORMAT}.
     */
    public static String getRequestExtension() {
    	return (String)ACH.getAC().getFromRequestData(Constants.FORMAT);
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
     * Returns model class name related to the underline controller class name 
     * based on naming convention.
     * 
     * @return string model class name
     */
    public static String getModelClassName(Class controllerClass) {
        return EnvConfig.getInstance().getModelClassNameFromControllerClassName(controllerClass.getName());
    }
    
    /**
     * Returns a paginator for the underline model.
     * 
     * @param modelClass the model class
     * @param sqlOptions SQL options string
     * @return Paginator instance
     */
    public static Paginator jdbcPaginator(Class modelClass, String sqlOptions) {
        return jdbcPaginator(modelClass, Converters.convertSqlOptionStringToMap(sqlOptions));
    }
    
    /**
     * Returns a paginator for the underline model.
     * 
     * @param modelClass the model class
     * @param sqlOptions SQL options map
     * @return Paginator instance
     */
    public static Paginator jdbcPaginator(Class modelClass, Map sqlOptions) {
        Map pagingControl = ACH.getAC().getParameterDataAsMap();
        return new Paginator(new JdbcPageListSource(modelClass, sqlOptions), pagingControl);
    }
    
    /**
     * Returns all request parameters as a map. This includes data in both 
     * parameter scope and request scope. 
     * 
     * @return a map of all request parameters
     */
    public static Map params() {
        return ACH.getAC().getAllRequestDataAsMap();
    }
    
    /**
     * Returns all request parameters as a map for those keys that have a
     * prefix.
     * 
     * @param keyPrefix
     * @return a map of request parameters
     */
    public static Map paramsWithPrefix(String keyPrefix) {
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
    public static Map routeFieldValues() {
        return (Map)ACH.getAC().getFromAllRequestData(RouteConstants.FIELD_VALUES);
    }
    
    /**
     * Returns value of a field for route.
     * 
     * @param field the field
     * @return value of a field for route.
     */
    public static String routeFieldValue(String field) {
        Object o = routeFieldValues().get(field);
        return Util.getSafeString(o);
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
        ACH.getAC().setFlashMessage(type, message);
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
        flash(type, new ExpandedMessage(null, message, value));
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
        flash(type, new ExpandedMessage(null, message, value0, value1));
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
        flash(type, new ExpandedMessage(null, message, value0, value1, value2));
    }
    
    /**
     * Records a flash message of a specific <tt>type</tt>. The <tt>message</tt> 
     * is of type {@link com.scooterframework.common.util.Message} or its subclass. 
     * 
     * @param type      flash message type
     * @param message   a {@link com.scooterframework.common.util.Message} object
     */
    public static void flash(String type, Message message) {
        ACH.getAC().setFlashMessage(type, message);
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
    public static String forwardTo(String uri, Map nameValuePairs) {
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
    public static String redirectTo(String uri, Map nameValuePairs) {
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
    
    private static List<FileItem> prepareFileItems() throws Exception {
    	List<FileItem> files = (List<FileItem>)CurrentThreadCache.get(Constants.FILE_UPLOAD_REQUEST_FILES);
		return files;
    }
    
    /**
     * Renders content associated with a specific request format.
     * 
	 * @param content  The content to be sent.
	 * @param format  The request format.
     */
    public static void render(Object content, String format) {
        ContentHandler handler = ContentHandlerFactory.getContentHandler(format);
        if (handler != null) {
        	try {
				handler.handle(
					ACH.getWAC().getHttpServletRequest(), 
					ACH.getWAC().getHttpServletResponse(), 
					content, 
					format);
				ACH.getWAC().getHttpServletRequest()
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
	 * @param displayableName  The display name of the file.
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
    
    public static Map getViewDataMap() {
    	return null;//TODO
    }
    
    public static void renderView(String viewFile) {
    	renderView(viewFile, getViewDataMap());
    }
    
    public static void renderView(String viewFile, Map viewDataMap) {
    	String format = getRequestExtension();
    	if (format == null) {
    		format = "xx";//TODO: What is it?
    	}
    }
    
    /**
     * Returns the FilterManager for a class type.
     */
    public static FilterManager filterManagerFor(Class clazz) {
    	return FilterManagerFactory.getInstance().getFilterManager(clazz);
    }
}
