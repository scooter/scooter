/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.web.controller;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.scooterframework.admin.ApplicationConfig;
import com.scooterframework.admin.Constants;
import com.scooterframework.admin.EnvConfig;
import com.scooterframework.common.util.Converters;
import com.scooterframework.common.util.CurrentThreadCache;
import com.scooterframework.common.util.ExpandedMessage;
import com.scooterframework.common.util.Message;
import com.scooterframework.common.validation.ValidationResults;
import com.scooterframework.orm.activerecord.ActiveRecord;
import com.scooterframework.orm.activerecord.ActiveRecordUtil;
import com.scooterframework.orm.misc.JdbcPageListSource;
import com.scooterframework.orm.misc.Paginator;
import com.scooterframework.orm.sqldataexpress.util.OrmObjectFactory;
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

    public ActionControl() {
        initialize();
    }
    
    /**
     * Returns controller name in request scope linked to key 
     * {@link com.scooterframework.admin.Constants#CONTROLLER}.
     */
    protected String getController() {
        return (String)ACH.getAC().getFromRequestData(Constants.CONTROLLER);
    }
    
    /**
     * Returns model name. 
     * 
     * <p>Model name must be set in request scope in a processor class before 
     * this method is called. It is mapped to attribute <tt>Constants.MODEL</tt>
     * (<tt>key.model</tt>) in http servlet request.</p>
     * 
     * @return model name
     */
    protected String getModel() {
        String model = (String) ACH.getAC().getFromRequestData(Constants.MODEL);
        if (model == null) throw new IllegalArgumentException("Model name (Constants.MODEL: key.model) must be set first.");
        return model;
    }
    
    /**
     * Returns resource name in request scope linked to key 
     * {@link com.scooterframework.admin.Constants#RESOURCE}.
     */
    protected String getResource() {
        return (String)ACH.getAC().getFromRequestData(Constants.RESOURCE);
    }
    
    /**
     * Returns uri path to a view of the current controller's aciton.
     * <pre>
     * Examples:
     *   viewPath("show") : /WEB-INF/views/posts/show.jsp
     *   viewPath("index"): /WEB-INF/views/posts/index.jsp
     * </pre>
     * @param action action name
     * @return path to a view file
     */
    protected String viewPath(String action) {
        return viewPath(getController(), action);
    }
    
    /**
     * Returns uri path to a view of a controller's aciton.
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
    protected String viewPath(String controller, String action) {
        return EnvConfig.getViewURI(controller, action);
    }
    
    /**
     * Returns a list of before ActionFilter objects for an action. 
     * 
     * @param action action name
     * @return list of ActionFilter objects
     */
    protected List getBeforeFiltersForAction(String action) {
        return getFiltersForAction(action, FILTER_TYPE_BEFORE);
    }
    
    /**
     * Returns a list of after ActionFilter objects for an action. 
     * 
     * @param action action name
     * @return list of ActionFilter objects
     */
    protected List getAfterFiltersForAction(String action) {
        return getFiltersForAction(action, FILTER_TYPE_AFTER);
    }
    
    /**
     * Returns a map of an action and its related before filters.
     * 
     * @return map of an action and its related before filters.
     */
    protected Map getActionBeforeFiltersMap() {
        return actionBeforeFiltersMap;
    }
    
    /**
     * Returns a map of an action and its related after filters.
     * 
     * @return map of an action and its related after filters.
     */
    protected Map getActionAfterFiltersMap() {
        return actionAfterFiltersMap;
    }
    
    /**
     * Returns model class name related to the underline controller class name 
     * based on naming convention.
     * 
     * @return string model class name
     */
    protected String getModelClassName() {
        return EnvConfig.getInstance().getModelClassNameFromControllerClassName(getClass().getName());
    }
    
    /**
     * Returns a record related to the primary key. The primary key should be 
     * a single field. 
     * 
     * @param modelClass
     * @param primaryKeyFieldName
     * @param primaryKeyValue
     * @return a record instance
     */
    protected ActiveRecord findRecordByPrimaryKey(Class modelClass, String primaryKeyFieldName, Object primaryKeyValue) {
        Map pkDataMap = new HashMap(1);
        pkDataMap.put(primaryKeyFieldName, primaryKeyValue);
        return findRecordByPrimaryKey(modelClass, pkDataMap);
    }
    
    /**
     * Returns a record related to the primary key. The primary key can be a 
     * composite key which consists of more than one field.
     * 
     * @param modelClass
     * @param pkDataMap
     * @return a record instance
     */
    protected ActiveRecord findRecordByPrimaryKey(Class modelClass, Map pkDataMap) {
        if (pkDataMap != null && pkDataMap.size() > 0) {
            Iterator it = pkDataMap.keySet().iterator();
            while(it.hasNext()) {
                Object key = it.next();
                Object value = pkDataMap.get(key);
                if (value == null) {
                    throw new IllegalArgumentException("Cannot find " + getModel() + " without value for field \"" + key + "\".");
                }
            }
        }
        return ActiveRecordUtil.getGateway(modelClass).findFirst(pkDataMap);
    }
    
    /**
     * Returns an instance of a specific class type.
     * 
     * @param modelClass
     * @param conditions
     * @return a record instance
     */
    protected ActiveRecord findRecord(Class modelClass, Map conditions) {
        return ActiveRecordUtil.getGateway(modelClass).findFirst(conditions);
    }
    
    protected ActiveRecord findRecord(Class modelClass, String conditions) {
        return ActiveRecordUtil.getGateway(modelClass).findFirst(conditions);
    }
    
    protected ActiveRecord findRecord(Class modelClass, String conditions, String options) {
        return ActiveRecordUtil.getGateway(modelClass).findFirst(conditions, options);
    }
    
    protected ActiveRecord findRecord(Class modelClass, Map conditions, Map options) {
        return ActiveRecordUtil.getGateway(modelClass).findFirst(conditions, options);
    }
    
    protected List findAll(Class modelClass) {
        return ActiveRecordUtil.getGateway(modelClass).findAll();
    }
    
    protected List findAll(Class modelClass, String conditionsSQL) {
        return ActiveRecordUtil.getGateway(modelClass).findAll(conditionsSQL);
    }
    
    protected List findAll(Class modelClass, String conditions, String options) {
        return ActiveRecordUtil.getGateway(modelClass).findAll(conditions, options);
    }
    
    protected List findAll(Class modelClass, String conditions, Map options) {
        return ActiveRecordUtil.getGateway(modelClass).findAll(conditions, options);
    }
    
    protected List findAll(Class modelClass, Map conditions) {
        return ActiveRecordUtil.getGateway(modelClass).findAll(conditions);
    }
    
    protected List findAll(Class modelClass, Map conditions, Map options) {
        return ActiveRecordUtil.getGateway(modelClass).findAll(conditions, options);
    }
    
    /**
     * Returns model home instance related to the underline controller class 
     * based on naming convention.
     * 
     * @return model home instance
     */
    protected ActiveRecord homeInstance() {
        return homeInstance(getModelClassName());
    }
    
    /**
     * Returns model home instance of a class name.
     * 
     * @param fullClassName full class name of a model
     * @return model home instance
     */
    protected ActiveRecord homeInstance(String fullClassName) {
        return ActiveRecordUtil.getHomeInstance(fullClassName);
    }
    
    /**
     * Returns model home instance of a model class.
     * 
     * @param modelClass model class type
     * @return model home instance
     */
    protected ActiveRecord homeInstance(Class modelClass) {
        return ActiveRecordUtil.getHomeInstance(modelClass);
    }
    
    /**
     * Returns a new instance of a model class.
     * 
     * @param modelClass model class type
     * @return new instance of a model class 
     */
    protected ActiveRecord newRecord(Class modelClass) {
        return newRecord(modelClass, (String)null);
    }
    
    /**
     * Returns a new instance of a model class with the given data.
     * 
     * @param modelClass model class type
     * @param nameValuePairs a string of name and value pairs as record data
     * @return new instance of a model class 
     */
    protected ActiveRecord newRecord(Class modelClass, String nameValuePairs) {
        ActiveRecord record = (ActiveRecord)OrmObjectFactory.getInstance().newInstance(modelClass.getName());
        record.setData(nameValuePairs);
        return record;
    }
    
    /**
     * Returns a new instance of a model class with the given data.
     * 
     * @param modelClass model class type
     * @param nameValuePairs a map of name and value pairs as record data
     * @return new instance of a model class 
     */
    protected ActiveRecord newRecord(Class modelClass, Map nameValuePairs) {
        ActiveRecord record = (ActiveRecord)OrmObjectFactory.getInstance().newInstance(modelClass.getName());
        record.setData(nameValuePairs);
        return record;
    }
    
    /**
     * Updates a record instance based on its request data.
     * 
     * @param modelClass model class type
     * @param requestParameters a map of name and value pairs as request data
     * @return number of records updated
     */
    protected int updateRecord(Class modelClass, Map requestParameters) {
        ActiveRecord record = findRecordByPrimaryKey(modelClass, pkparams(modelClass));
        record.setData(requestParameters);
        return record.updateChanged();
    }
    
    /**
     * Deletes a record instance based on its primary key data.
     * 
     * @param modelClass
     * @param primaryKeyFieldName
     * @param primaryKeyValue
     * @return number of records deleted
     */
    protected int deleteRecordByPrimaryKey(Class modelClass, String primaryKeyFieldName, Object primaryKeyValue) {
        ActiveRecord record = findRecordByPrimaryKey(modelClass, primaryKeyFieldName, primaryKeyValue);
        return record.delete();
    }
    
    /**
     * Deletes a record instance based on its primary key data. The primary key 
     * can be a composite key which consists of more than one field.
     * 
     * @param modelClass
     * @param pkDataMap
     * @return number of records deleted
     */
    protected int deleteRecordByPrimaryKey(Class modelClass, Map pkDataMap) {
        ActiveRecord record = findRecordByPrimaryKey(modelClass, pkDataMap);
        return record.delete();
    }
    
    protected Paginator jdbcPaginator(Class modelClass, String sqlOptions) {
        return jdbcPaginator(modelClass, Converters.convertSqlOptionStringToMap(sqlOptions));
    }
    
    protected Paginator jdbcPaginator(Class modelClass, Map sqlOptions) {
        Map pagingControl = ACH.getAC().getParameterDataAsMap();
        return new Paginator(new JdbcPageListSource(modelClass, sqlOptions), pagingControl);
    }
    
    /**
     * Returns all request parameters as a map. This includes data in both 
     * parameter scope and request scope. 
     * 
     * @return a map of all request parameters
     */
    protected Map params() {
        return ACH.getAC().getAllRequestDataAsMap();
    }
    
    /**
     * Returns all request parameters as a map for those keys that have a
     * prefix.
     * 
     * @param keyPrefix
     * @return a map of request parameters
     */
    protected Map paramsWithPrefix(String keyPrefix) {
        return ACH.getAC().getAllRequestDataAsMap(keyPrefix);
    }
    
    /**
     * Returns a value corresponding to a key in request parameters or request 
     * attributes.
     * 
     * @param key name of a key in the request parameters
     * @return a value corresponding to a key in the request parameters
     */
    protected String params(String key) {
        return (String)ACH.getAC().getFromAllRequestData(key);
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
    protected String p(String key) {
        return params(key);
    }
    
    /**
     * Alias of method <tt>getUploadedFile(String key)</tt>.
     * 
     * Returns an upload file which is an instance of <tt>UploadFile</tt>. 
     * 
     * @return an instance of <tt>UploadFile</tt>
     * @throws Exception
     */
    protected UploadFile paramsFile(String key) throws Exception {
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
    protected List paramsFiles() throws Exception {
        return getUploadFiles();
    }
    
    /**
     * Alias of method <tt>getUploadFilesMap()</tt>.
     * 
     * Returns a map of upload files. In each key/value pair, the key is the 
     * field name in the http form, and the value is a <tt>UploadFile</tt> instance.
     * 
     * @return a map of field/upload file (UploadFile) pairs
     * @throws Exception
     */
    protected Map paramsFilesMap() throws Exception {
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
    protected UploadFile pFile(String key) throws Exception {
        return paramsFile(key);
    }
    
    /**
     * Alias of method <tt>paramsFiles()</tt>.
     * Returns a list of upload files. 
     * 
     * @return a list of <tt>UploadFile</tt> instances
     * @throws Exception
     */
    protected List pFiles() throws Exception {
        return paramsFiles();
    }
    
    /**
     * Alias of method <tt>paramsFilesMap()</tt>.
     * 
     * Returns a map of upload files. In each key/value pair, the key is the 
     * field name in the http form, and the value is a <tt>UploadFile</tt> instance.
     * 
     * @return a map of field/upload file (UploadFile) pairs
     * @throws Exception
     */
    protected Map pFilesMap() throws Exception {
        return paramsFilesMap();
    }
    
    /**
     * Returns a value corresponding to a key in the request parameters. The 
     * case of the key string is ignored.
     * 
     * @param key name of a key in the request parameters
     * @return a value corresponding to a key in the request parameters
     */
    protected String paramsIgnoreCase(String key) {
        return (String)ACH.getAC().getFromParameterDataIgnoreCase(key);
    }
    
    /**
     * Returns field values of a route.
     * 
     * @return field values of a route.
     */
    protected Map routeFieldValues() {
        return (Map)ACH.getAC().getFromAllRequestData(RouteConstants.FIELD_VALUES);
    }
    
    /**
     * Returns value of a field for route.
     * 
     * @param field the field
     * @return value of a field for route.
     */
    protected String routeFieldValue(String field) {
        return (String)routeFieldValues().get(field);
    }
    
    /**
     * Returns a map of primary key data for a model class type.
     * 
     * @param modelClass model class type
     * @return map of primary key data
     */
    protected Map pkparams(Class modelClass) {
        return pkparams("", modelClass);
    }
    
    /**
     * Returns a map of primary key data for a model class type.
     * 
     * @param keyPrefix prefix of request parameters
     * @param modelClass model class type
     * @return map of primary key data
     */
    protected Map pkparams(String keyPrefix, Class modelClass) {
        ActiveRecord recordHome = homeInstance(modelClass);
        return ACH.getAC().retrievePrimaryKeyDataMapFromRequest(keyPrefix, recordHome.getPrimaryKeyNames());
    }
    
    protected Object getFromThreadData(String key) {
        return ActionContext.getFromThreadData(key);
    }
    
    protected Object getFromParameterData(String key) {
        return ACH.getAC().getFromParameterData(key);
    }
    
    protected Object getFromRequestData(String key) {
        return ACH.getAC().getFromRequestData(key);
    }
    
    protected Object getFromSessionData(String key) {
        return ACH.getAC().getFromSessionData(key);
    }
    
    protected Object getFromContextData(String key) {
        return ACH.getAC().getFromContextData(key);
    }
    
    protected Object getFromGlobalData(String key) {
        return ActionContext.getFromGlobalData(key);
    }
    
    protected void storeToThread(String key, Object obj) {
        ActionContext.storeToThread(key, obj);
    }
    
    protected void storeToRequest(String key, Object obj) {
        ACH.getAC().storeToRequest(key, obj);
    }
    
    protected void storeToSession(String key, Object obj) {
        ACH.getAC().storeToSession(key, obj);
    }
    
    protected void storeToContext(String key, Object obj) {
        ACH.getAC().storeToContext(key, obj);
    }
    
    protected void storeToGlobal(String key, Object obj) {
        ActionContext.storeToGlobal(key, obj);
    }
    
    protected void removeFromThreadData(String key) {
        ActionContext.removeFromThreadData(key);
    }
    
    protected void removeFromRequestData(String key) {
        ACH.getAC().removeFromRequestData(key);
    }
    
    protected void removeFromSessionData(String key) {
        ACH.getAC().removeFromSessionData(key);
    }
    
    protected void removeFromContextData(String key) {
        ACH.getAC().removeFromContextData(key);
    }
    
    protected void removeFromGlobalData(String key) {
        ActionContext.removeFromGlobalData(key);
    }
    
    protected void remove(String key) {
        ACH.getAC().remove(key);
    }
    
    protected void removeAllSessionData() {
        ACH.getAC().removeAllSessionData();
    }
    
    /**
     * Binds data to view attribute for view rendering.
     * 
     * @param key  a string representing a place holder on view
     * @param data the data value to be filled in the view
     */
    protected void setViewData(String key, Object data) {
    	storeToRequest(key, data);
    }
    
    /**
     * Returns content type of http request.
     * @return http content type
     */
    protected String getHttpRequestContentType() {
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
     * Executes filters before an action is run.
     * 
     * A not-null return value indicates that the action fails in a filter.
     * 
     * @param action the action
     * @return result of filtering
     */
    protected String executeBeforeFiltersOn(String action) {
        return executeFiltersOn(action, FILTER_TYPE_BEFORE);
    }
    
    /**
     * Executes filters after an action is run.
     * 
     * A not-null return value indicates that the action fails in a filter.
     * 
     * @param action the action
     * @return result of filtering
     */
    protected String executeAfterFiltersOn(String action) {
        return executeFiltersOn(action, FILTER_TYPE_AFTER);
    }

    /**
     * Executes filters before an action is run.
     * 
     * A not-null return value indicates that the action fails in a filter.
     * 
     * @param action the action
     * @param type   filter type
     * @return result of filtering
     */
    protected String executeFiltersOn(String action, String type) {
        String ret = null;
        List filters = getFiltersForAction(action, type);
        if (filters != null) {
            Iterator it = filters.iterator();
            while(it.hasNext() && (ret == null)) {
                ActionControlFilter af = (ActionControlFilter)it.next();
                ret = af.execute();
            }
        }
        return ret;
    }
    
    /**
     * Returns a list of ActionFilter objects for an action. 
     * 
     * @param action action name
     * @param type filter type
     * @return list of ActionFilter objects
     */
    private List getFiltersForAction(String action, String type) {
        List l = null;
        if (FILTER_TYPE_BEFORE.equalsIgnoreCase(type)) {
            l = (List)actionBeforeFiltersMap.get(action);
        }
        else 
        if (FILTER_TYPE_AFTER.equalsIgnoreCase(type)) {
            l = (List)actionAfterFiltersMap.get(action);
        }
        return l;
    }
    
    private void prepareFilter(List filterDataList, String filterType, Class filterClz, String filters) {
        String key = fileterKey(filterType, filterClz, filters);
        Object filter = null;
        if (!allFiltersMap.containsKey(key)) {
            filter = new ActionFilterData(filterClz, filterType, filters);
            allFiltersMap.put(key, filter);
        }
        else {
            filter = allFiltersMap.get(key);
        }
        filterDataList.add(filter);
    }
    
    private void prepareFilter(List filterDataList, String filterType, Class filterClz, String filters, String option, String actions) {
        String key = fileterKey(filterType, filterClz, filters, option, actions);
        Object filter = null;
        if (!allFiltersMap.containsKey(key)) {
            filter = new ActionFilterData(filterClz, filterType, option, filters, actions);
            allFiltersMap.put(key, filter);
        }
        else {
            filter = allFiltersMap.get(key);
        }
        filterDataList.add(filter);
    }
    
    /**
     * Specifies 'before' filters that apply to all actions of this class.
     * 
     * 'before' filters are executed before real action is executed.
     * 
     * @param filters   method names that act as filters separated by comma.
     */
    protected void beforeFilter(String filters) {
        beforeFilter(getClass(), filters);
    }
    
    /**
     * Specifies 'before' filters that apply to all actions of the controller class.
     * 
     * 'before' filters are executed before real action is executed.
     * 
     * @param filterClz the declaring class of filter methods
     * @param filters   method names that act as filters separated by comma
     */
    protected void beforeFilter(Class filterClz, String filters) {
        prepareFilter(beforeFilterDataList, FILTER_TYPE_BEFORE, filterClz, filters);
    }
    
    /**
     * Specifies 'before' filters that apply to specific actions of this class
     * under special conditions.
     * 
     * 'before' filters are executed before real action is executed.
     * 
     * Only two options are supported: only and except.
     * 
     * @param filters   method names that act as filters separated by comma
     * @param option    condition for the filter
     * @param actions   method names that act as actions separated by comma
     */
    protected void beforeFilter(String filters, String option, String actions) {
        beforeFilter(getClass(), filters, option, actions);
    }
    
    /**
     * Specifies 'before' filters that apply to specific actions of the 
     * controller class under special conditions.
     * 
     * 'before' filters are executed before real action is executed.
     * 
     * Only two options are supported: only and except.
     * 
     * @param filterClz the declaring class of filter methods
     * @param filters   method names that act as filters separated by comma
     * @param option    condition for the filter
     * @param actions   method names that act as actions separated by comma
     */
    protected void beforeFilter(Class filterClz, String filters, String option, String actions) {
        prepareFilter(beforeFilterDataList, FILTER_TYPE_BEFORE, filterClz, filters, option, actions);
    }
    
    /**
     * Specifies 'after' filters that apply to all actions of this class.
     * 
     * 'after' filters are executed after real action is executed.
     * 
     * @param filters   method names that act as filters separated by comma
     */
    protected void afterFilter(String filters) {
        afterFilter(getClass(), filters);
    }
    
    /**
     * Specifies 'after' filters that apply to all actions of the controller class.
     * 
     * 'after' filters are executed after real action is executed.
     * 
     * @param filterClz the declaring class of filter methods
     * @param filters   method names that act as filters separated by comma
     */
    protected void afterFilter(Class filterClz, String filters) {
        prepareFilter(afterFilterDataList, FILTER_TYPE_AFTER, filterClz, filters);
    }
    
    /**
     * Specifies 'after' filters that apply to specific actions of this class
     * under special conditions.
     * 
     * 'after' filters are executed after real action is executed.
     * 
     * Only two options are supported: only and except.
     * 
     * @param filters   method names that act as filters separated by comma
     * @param option    condition for the filter
     * @param actions   method names that act as actions separated by comma
     */
    protected void afterFilter(String filters, String option, String actions) {
        afterFilter(getClass(), filters, option, actions);
    }
    
    /**
     * Specifies 'after' filters that apply to specific actions of the 
     * controller class under special conditions.
     * 
     * 'after' filters are executed after real action is executed.
     * 
     * Only two options are supported: only and except.
     * 
     * @param filterClz the declaring class of filter methods
     * @param filters   method names that act as filters separated by comma
     * @param option    condition for the filter
     * @param actions   method names that act as actions separated by comma
     */
    protected void afterFilter(Class filterClz, String filters, String option, String actions) {
        prepareFilter(afterFilterDataList, FILTER_TYPE_AFTER, filterClz, filters, option, actions);
    }
    
    /**
     * Skips 'before' filters that apply to all actions of this class.
     * 
     * 'before' filters are executed before real action is executed.
     * 
     * @param filters   method names that act as filters separated by comma
     */
    protected void skipBeforeFilter(String filters) {
        skipBeforeFilter(getClass(), filters);
    }
    
    /**
     * Skips 'before' filters that apply to all actions of the controller class.
     * 
     * 'before' filters are executed before real action is executed.
     * 
     * @param filterClz the declaring class of filter methods
     * @param filters   method names that act as filters separated by comma
     */
    protected void skipBeforeFilter(Class filterClz, String filters) {
        prepareFilter(skipBeforeFilterDataList, FILTER_TYPE_SKIP_BEFORE, filterClz, filters);
    }
    
    /**
     * Skips 'before' filters that apply to specific actions of this class
     * under special conditions.
     * 
     * 'before' filters are executed before real action is executed.
     * 
     * Only two options are supported: only and except.
     * 
     * @param filters   method names that act as filters separated by comma
     * @param option    condition for the filter
     * @param actions   method names that act as actions separated by comma
     */
    protected void skipBeforeFilter(String filters, String option, String actions) {
        skipBeforeFilter(getClass(), filters, option, actions);
    }
    
    /**
     * Skips 'before' filters that apply to specific actions of the controller 
     * class under special conditions.
     * 
     * 'before' filters are executed before real action is executed.
     * 
     * Only two options are supported: only and except.
     * 
     * @param filterClz the declaring class of filter methods
     * @param filters   method names that act as filters separated by comma
     * @param option    condition for the filter
     * @param actions   method names that act as actions separated by comma
     */
    protected void skipBeforeFilter(Class filterClz, String filters, String option, String actions) {
        prepareFilter(skipBeforeFilterDataList, FILTER_TYPE_SKIP_BEFORE, filterClz, filters, option, actions);
    }
    
    /**
     * Skips 'after' filters that apply to all actions of this class.
     * 
     * 'after' filters are executed after real action is executed.
     * 
     * @param filters   method names that act as filters separated by comma
     */
    protected void skipAfterFilter(String filters) {
        skipAfterFilter(getClass(), filters);
    }
    
    /**
     * Skips 'after' filters that apply to all actions of the controller class.
     * 
     * 'after' filters are executed after real action is executed.
     * 
     * @param filterClz the declaring class of filter methods
     * @param filters   method names that act as filters separated by comma
     */
    protected void skipAfterFilter(Class filterClz, String filters) {
        prepareFilter(skipAfterFilterDataList, FILTER_TYPE_SKIP_AFTER, filterClz, filters);
    }
    
    /**
     * Skips 'after' filters that apply to specific actions of this class
     * under special conditions.
     * 
     * 'after' filters are executed after real action is executed.
     * 
     * Only two options are supported: only and except.
     * 
     * @param filters   method names that act as filters separated by comma
     * @param option    condition for the filter
     * @param actions   method names that act as actions separated by comma
     */
    protected void skipAfterFilter(String filters, String option, String actions) {
        skipAfterFilter(getClass(), filters, option, actions);
    }
    
    /**
     * Skips 'after' filters that apply to specific actions of the controller 
     * class under special conditions.
     * 
     * 'after' filters are executed after real action is executed.
     * 
     * Only two options are supported: only and except.
     * 
     * @param filterClz the declaring class of filter methods
     * @param filters   method names that act as filters separated by comma
     * @param option    condition for the filter
     * @param actions   method names that act as actions separated by comma
     */
    protected void skipAfterFilter(Class filterClz, String filters, String option, String actions) {
        prepareFilter(skipAfterFilterDataList, FILTER_TYPE_SKIP_AFTER, filterClz, filters, option, actions);
    }

    /**
     * Subclass need to override this method by calling proper filter 
     * setup methods: beforeFilter, afterFilter, etc.
     * 
     * The order of those filter-setup methods in the body of this method 
     * determines the order the filter execution.
     */
    protected void registerFilters() {
        ;
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
    protected ActionValidators validators() {
        return (validators != null)?validators:(new ActionValidators(this));
    }
    
    private ActionValidators validators = null;
    
    private static final String KEY_ValidationResults = "key.ValidationResults";

    protected ValidationResults currentValidationResults() {
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
    protected boolean validationFailed() {
        return currentValidationResults().failed();
    }
    
    
    private void initialize() {
        if (!initialized || 
            !allConfiguredClasses.contains(getClass().getName())) {
            
            //load
            registerFilters();
            
            //config
            configFilters();
            
            initialized = true;
            allConfiguredClasses.add(getClass().getName());
        }
    }
    
    //create action filters map
    private void configFilters() {
        //get action list
        List actions = getAllActionMethods();
        
        actionBeforeFiltersMap = constructActionFiltersMap(actions, beforeFilterDataList, skipBeforeFilterDataList);
        actionAfterFiltersMap  = constructActionFiltersMap(actions, afterFilterDataList, skipAfterFilterDataList);
    }
    
    /**
     * Action method must be public accessible, returns a string and takes no
     * arguments. 
     * 
     * @return list of action method names
     */
    private List getAllActionMethods() {
        if (actionMethods != null) return actionMethods;
        
        actionMethods = new ArrayList();
        Method[] ms = this.getClass().getMethods();//returns all public methods
        for (int i=0; i<ms.length; i++) {
            Method m = ms[i];
            if ((m.getReturnType().isInstance("")) &&
                (!m.getDeclaringClass().getName().equals(Object.class.getName())) &&
                (m.getParameterTypes().length == 0)) 
            {
                actionMethods.add(m.getName());
            }
        }
        return actionMethods;
    }
    
    //construct a map of actions and their corresponding filter list
    private Map constructActionFiltersMap(List actions, List filterDataList, List skipFilterDataList) {
        Map m = new HashMap();
        Iterator it = actions.iterator();
        while(it.hasNext()) {
            String action = (String)it.next();
            List filters = constructFiltersListForAction(action, filterDataList, skipFilterDataList);
            if (filters != null && filters.size() > 0) {
                m.put(action, filters);
            }
        }
        return m;
    }
    
    //construct filter list for a specific action
    private List constructFiltersListForAction(String action, List appendFilterDataList, List skipFilterDataList) {
        List al = _constructFiltersListForAction(action, appendFilterDataList);//append filter list
        if (al == null || al.size() == 0) return null;//nothing to append
        
        List sl = _constructFiltersListForAction(action, skipFilterDataList);//skip filter list
        if (sl == null || sl.size() == 0) return al;//nothing to skip
        
        //Note: At this stage, al or sl may contain duplicated filters.
        
        //declare a list of unique items
        List l = new ArrayList();
        Map m = new HashMap();//contains unique filters
        Iterator it = al.iterator();
        while(it.hasNext()) {
            ActionControlFilter acf = (ActionControlFilter)it.next();
            
            if (skipActionControlFilter(sl, acf)) continue;
            
            //make sure that the l list contains non-duplicated filters.
            if (!m.containsKey(acf.getACFKey())) {
                m.put(acf.getACFKey(), acf);
                l.add(acf);
            }
        }
        return l;
    }
    
    //construct filter list for a specific action
    private List _constructFiltersListForAction(String action, List filterDataList) {
        List l = new ArrayList();
        Iterator it = filterDataList.iterator();
        while(it.hasNext()) {
            ActionFilterData afd = (ActionFilterData)it.next();
            List acfList = afd.getFilters(action);
            if (acfList != null && acfList.size() > 0) {
                l.addAll(acfList);
            }
        }
        return l;
    }
    
    private boolean skipActionControlFilter(List data, ActionControlFilter acf) {
        boolean check = false;
        Iterator it = data.iterator();
        while(it.hasNext()) {
            ActionControlFilter tmp = (ActionControlFilter)it.next();
            if (tmp.getACFKey().equals(acf.getACFKey())) {
                check = true;
                break;
            }
        }
        return check;
    }
    
    private String fileterKey(String filterType, Class filterClz, String filters) {
        StringBuffer key = new StringBuffer();
        key.append(filterClz.getName()).append(FILTER_KEY_SEPARATOR);
        key.append(filterType).append(FILTER_KEY_SEPARATOR);
        key.append(filters);
        return key.toString();
    }
    
    private String fileterKey(String filterType, Class filterClz, String filters, String option, String actions) {
        StringBuffer key = new StringBuffer();
        key.append(filterClz.getName()).append(FILTER_KEY_SEPARATOR);
        key.append(filterType).append(FILTER_KEY_SEPARATOR);
        key.append(filters).append(FILTER_KEY_SEPARATOR);
        key.append(option).append(FILTER_KEY_SEPARATOR);
        key.append(actions);
        return key.toString();
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
     * Returns a forward-tagged uri string with a query string. The query string is 
     * formed by listing all primary key and value pairs of the record instance.
     * 
     * @param uri an uri string
     * @param record an ActiveRecord instance
     * @return a formatted forward-tagged uri string
     */
    public static String forwardTo(String uri, ActiveRecord record) {
    	return ActionResult.forwardTo(uri, record);
    }
    
    /**
     * Returns a forward-tagged uri string with a query string.
     * 
     * @param uri an uri string
     * @param nameValuePairs a map of name and value pairs as http query string
     * @return a formatted forward-tagged uri string
     */
    public static String forwardTo(String uri, Map nameValuePairs) {
        return ActionResult.forwardTo(uri, nameValuePairs);
    }
    
    /**
     * Returns a forward-tagged uri string with a query string.
     * 
     * @param uri an uri string
     * @param nameValuePairs a string of name and value pairs as http query string
     * @return a formatted forward-tagged uri string
     */
    public static String forwardTo(String uri, String nameValuePairs) {
        return ActionResult.forwardTo(uri, nameValuePairs);
    }
    
    /**
     * Returns a forward-tagged uri string.
     * 
     * @param uri an uri string
     * @return a formatted forward-tagged uri string
     */
    public static String forwardTo(String uri) {
        return ActionResult.forwardTo(uri);
    }
    
    /**
     * Returns a redirect-tagged uri string with a query string. The query string is 
     * formed by listing all primary key and value pairs of the record instance.
     * 
     * @param uri an uri string
     * @param record an ActiveRecord instance
     * @return a formatted redirect-tagged uri string
     */
    public static String redirectTo(String uri, ActiveRecord record) {
        return ActionResult.redirectTo(uri, record);
    }
    
    /**
     * Returns a redirect-tagged uri string with a query string.
     * 
     * @param uri an uri string
     * @param nameValuePairs a map of name and value pairs as http query string
     * @return a formatted redirect-tagged uri string
     */
    public static String redirectTo(String uri, Map nameValuePairs) {
        return ActionResult.redirectTo(uri, nameValuePairs);
    }
    
    /**
     * Returns a redirect-tagged uri string with a query string.
     * 
     * @param uri an uri string
     * @param nameValuePairs a string of name and value pairs as http query string
     * @return a formatted redirect-tagged uri string
     */
    public static String redirectTo(String uri, String nameValuePairs) {
        return ActionResult.redirectTo(uri, nameValuePairs);
    }
    
    /**
     * Returns a redirect-tagged uri string.
     * 
     * @param uri an uri string
     * @return a formatted redirect-tagged uri string
     */
    public static String redirectTo(String uri) {
        return ActionResult.redirectTo(uri);
    }
    
    /**
     * Returns root path to the application.
     */
    protected String applicationPath() {
    	return ApplicationConfig.getInstance().getApplicationPath();
    }
    
    /**
     * Configures file upload setting. There is no limit in terms of maximum 
     * size of files.
     */
    protected void configFileUpload() {
    	fileFactory = new DiskFileItemFactory();
    	fileUpload = new ServletFileUpload(fileFactory);
    }
    
    /**
     * Configures file upload setting.
     * 
     * @param maxMemorySize The threshold, in bytes, below which items will be retained in memory and above which they will be stored as a file.
     * @param tempDirectory The data repository, which is the directory in which files will be created, should the item size exceed the threshold.
     * @param maxRequestSize The maximum allowed size, in bytes. The default value of -1 indicates, that there is no limit.
     * @param fileSizeMax Maximum size of a single upload file. The default value of -1 indicates, that there is no limit.
     */
    protected void configFileUpload(int maxMemorySize, String tempDirectory, long maxRequestSize, long fileSizeMax) {
    	fileFactory = new DiskFileItemFactory(maxMemorySize, new File(tempDirectory));
    	fileUpload = new ServletFileUpload(fileFactory);
    	fileUpload.setSizeMax(maxRequestSize);
    	fileUpload.setFileSizeMax(fileSizeMax);
    }
    
    /**
     * Returns a list of upload files. Each item in the list is an instance 
     * of <tt>java.io.File</tt> instance.
     * 
     * @param fileRepository
     * @return a list of upload files (File)
     * @throws Exception
     */
    protected List getUploadFilesAsFiles(String fileRepository) throws Exception {
    	List files = new ArrayList();
		Iterator iter = prepareFileItems();
		while (iter.hasNext()) {
			FileItem item = (FileItem) iter.next();
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
    protected List getUploadFiles() throws Exception {
    	List files = new ArrayList();
		Iterator iter = prepareFileItems();
		while (iter.hasNext()) {
			FileItem item = (FileItem) iter.next();
			if (!item.isFormField() && !"".equals(item.getName())) {
				files.add(new UploadFile(item));
			}
		}
		return files;
    }
    
    /**
     * Returns a map of upload files. In each key/value pair, the key is the 
     * field name in the http form, and the value is a <tt>UploadFile</tt> instance.
     * 
     * @return a map of field/upload file (UploadFile) pairs
     * @throws Exception
     */
    protected Map getUploadFilesMap() throws Exception {
    	Map files = new HashMap();
		Iterator iter = prepareFileItems();
		while (iter.hasNext()) {
			FileItem item = (FileItem) iter.next();
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
    protected UploadFile getUploadFile(String key) throws Exception {
		return (UploadFile)getUploadFilesMap().get(key);
    }
    
    private Iterator prepareFileItems() throws Exception {
    	if (fileUpload == null) {
    		configFileUpload();
    	}
    	
		List /* FileItem */items = fileUpload.parseRequest(ACH.getWAC().getHttpServletRequest());
		return items.iterator();
    }

    public static final String FILTER_TYPE_BEFORE = "before";
    public static final String FILTER_TYPE_AFTER = "after";
    public static final String FILTER_TYPE_SKIP_BEFORE = "skip_before";
    public static final String FILTER_TYPE_SKIP_AFTER = "skip_after";

    public static final String FILTER_OPTION_EXCEPT = "except";
    public static final String FILTER_OPTION_ONLY = "only";
    
    private List afterFilterDataList = new ArrayList();
    private List beforeFilterDataList = new ArrayList();
    private List skipAfterFilterDataList = new ArrayList();
    private List skipBeforeFilterDataList = new ArrayList();
    
    private boolean initialized = false;
    private static List allConfiguredClasses = new ArrayList();
    
    private List actionMethods = null;
    private Map actionBeforeFiltersMap = new HashMap();
    private Map actionAfterFiltersMap = new HashMap();
    
    private static Map allFiltersMap = new HashMap();
    private static final String FILTER_KEY_SEPARATOR = "-";
    
    private DiskFileItemFactory fileFactory;
    private ServletFileUpload fileUpload;
}
