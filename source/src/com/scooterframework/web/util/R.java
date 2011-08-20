/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.web.util;

import java.util.HashMap;
import java.util.Map;

import com.scooterframework.admin.Constants;
import com.scooterframework.admin.EnvConfig;
import com.scooterframework.common.util.WordUtil;
import com.scooterframework.orm.activerecord.ActiveRecord;
import com.scooterframework.orm.activerecord.ActiveRecordUtil;
import com.scooterframework.orm.sqldataexpress.object.RESTified;
import com.scooterframework.web.route.MatchMaker;
import com.scooterframework.web.route.Resource;
import com.scooterframework.web.route.Route;
import com.scooterframework.web.route.RouteConstants;

/**
 * <p>R(RestfulHelper) class has helper methods for routes and resources. </p>
 * 
 * @author (Fei) John Chen
 */
public class R {
    
    private static String getRestfulId(Object rest) {
        if (rest == null) return null;
        String restfulId = null;
        if (rest instanceof String) {
            restfulId = (String)rest;
        }
        else if (rest instanceof RESTified) {
            restfulId = ((RESTified)rest).getRestfulId();
        }
        else {
            throw new IllegalArgumentException("Only String or RESTified types " + 
                "are allowed in getRestfulId method.");
        }
        return restfulId;
    }

    @SuppressWarnings("unchecked")
	private static Map<String, String> getFieldValues() {
        return (Map<String, String>)W.request(RouteConstants.FIELD_VALUES);
    }

    /**
     * Returns resource. 
     * 
     * @param resourceName     name of the resource
     * @return resource
     */
    public static Resource resource(String resourceName) {
        Resource resource = MatchMaker.getInstance().getResource(resourceName);
        if (resource == null) {
            //throw new IllegalArgumentException("\"" + resourceName + "\"" + 
            //            " is not defined as a resource.");
            resource = new Resource(resourceName, true);
        }
        return resource;
    }
    
    /**
     * Returns resource corresponding to a model name.
     * 
     * @param model     model name
     * @return resource
     */
    public static Resource resourceForModel(String model) {
        Resource resource = MatchMaker.getInstance().getResourceForModel(model);
        if (resource == null) {
            //throw new IllegalArgumentException("\"" + model + "\"" + 
            //            " is not defined as a model name for a resource.");
            resource = new Resource(model, true);
        }
        return resource;
    }
    
    /**
     * <p>Returns resource path based on resource name. </p>
     * 
     * <p>For example, for resource <tt>"posts"</tt>, the resource path may be 
     * <tt>"/posts"</tt>.</p>
     * 
     * @param resourceName     name of the resource
     * @return resource path
     */
    public static String resourcePath(String resourceName) {
        return _resourcePath(resourceName, getFieldValues());
    }
    
    /**
     * <p>Returns a resource path. If the <tt>record</tt>'s RESTful id is 
     * empty, an empty string is returned.</p>
     * 
     * <p>For example, for record <tt>"Post"</tt> with <tt>post.id=1</tt>, the 
     * resource path may be <tt>"/posts/1"</tt>.</p>
     * 
     * @param resourceName     resource name
     * @param record            a restified record
     * @return resource path
     */
    public static String resourceRecordPath(String resourceName, RESTified record) {
        return resourceRecordPath(resourceName, getRestfulId(record));
    }
    
    /**
     * <p>Returns a resource path for a specific resource. If the 
     * <tt>resourceId</tt> is empty, an empty string is returned.</p>
     * 
     * <p>For example, for record <tt>"Post"</tt> with <tt>post.id=1</tt>, the 
     * resource path may be <tt>"/posts/1"</tt>.</p>
     * 
     * @param resourceName     resource name
     * @param resourceId       a specific resource
     * @return resource path
     */
    public static String resourceRecordPath(String resourceName, String resourceId) {
        return _resourceRecordPath(resourceName, resourceId, getFieldValues());
    }
    
    private static String _resourcePath(String resourceName, Map<String, String> fieldValues) {
        return resource(resourceName).getScreenURL(fieldValues);
    }
    
    private static String _resourceRecordPath(String resourceName, String resourceId, Map<String, String> fieldValues) {
    	if (M.isEmpty(resourceId)) return "";
    	
        Resource resource = resource(resourceName);
        String path = "";
        if (resource.isSingle()) {
            path = resource.getScreenURL(fieldValues);
        }
        else {
            path = resource.getScreenURL(fieldValues) + "/" + resourceId;
        }
        return path;
    }
    
    private static String _addResourcePath(String pathToResource) {
        return pathToResource + "/add";
    }
    
    /**
     * Returns resource path for adding a record. 
     * 
     * For example, for adding a new record <tt>"Post"</tt>, the resource path 
     * may be <tt>"/posts/add"</tt>.
     * 
     * @param resourceName     name of the resource
     * @return resource path for adding a new record
     */
    public static String addResourcePath(String resourceName) {
        return _addResourcePath(resourcePath(resourceName));
    }
    
    private static String _editResourceRecordPath(String pathToResourceId) {
    	return (M.isEmpty(pathToResourceId))?"":(pathToResourceId + "/edit");
    }
    
    /**
     * <p>Returns resource path for editing a record. If the <tt>record</tt>'s 
     * RESTful id is empty, an empty string is returned.</p>
     * 
     * <p>For example, for editing a record <tt>"Post"</tt> with 
     * <tt>post.id=1</tt>, the resource path may be <tt>"/posts/1/edit"</tt>.<p>
     * 
     * @param resourceName     resource name
     * @param record            a restified record
     * @return resource path for editing a new record
     */
    public static String editResourceRecordPath(String resourceName, RESTified record) {
        return _editResourceRecordPath(resourceRecordPath(resourceName, record));
    }
    
    /**
     * <p>Returns resource path for editing a specific resource. If the 
     * <tt>resourceId</tt> is empty, an empty string is returned.</p>
     * 
     * <p>For example, for editing a record <tt>"Post"</tt> with <tt>post.id=1</tt>, 
     * the resource path may be <tt>"/posts/1/edit"</tt>.
     * 
     * @param resourceName     resource name
     * @param resourceId       a specific resource
     * @return resource path for editing a new record
     */
    public static String editResourceRecordPath(String resourceName, String resourceId) {
        return _editResourceRecordPath(resourceRecordPath(resourceName, resourceId));
    }
    
    private static String _formForAddResource(String pathTOResource, String resourceName) {
        Resource resource = resource(resourceName);
        String name = "add_" + resource.getModel();
        String id = name;
        StringBuilder sb = new StringBuilder();
        sb.append("<form action=\"").append(W.getURL(pathTOResource));
        sb.append("\" class=\"").append(name).append("\" id=\"").append(id).append("\" method=\"POST\">");
        return sb.toString();
    }
    
    /**
     * Returns form element for a resource. 
     * 
     * @param resourceName  name of the resource
     * @param restfulId     a specific object in the resource
     * @return form element for a resource record
     */
    public static String formForResource(String resourceName, String restfulId) {
        return (restfulId == null)?
            formForAddResource(resourceName):formForEditResourceRecord(resourceName, restfulId);
    }
    
    private static boolean isAddForm(RESTified record) {
        boolean addForm = false;
        if (record == null || record.getRestfulId() == null || 
            (record instanceof ActiveRecord && 
            		(((ActiveRecord)record).isNewRecord() || 
            				((ActiveRecord)record).isHomeInstance()))
        ) {
            addForm = true;
        }
        return addForm;
    }
    
    /**
     * Returns form element for a resource. 
     * 
     * @param resourceName     name of the resource
     * @param record           a restified record
     * @return form element for a resource record
     */
    public static String formForResource(String resourceName, RESTified record) {
        return (isAddForm(record))?formForAddResource(resourceName):formForEditResourceRecord(resourceName, record);
    }
    
    /**
     * Returns form element for a nested resource. 
     * 
     * @param parentResourceName    name of parent resource
     * @param parentRecord          parent restified record
     * @param resourceName          name of the resource
     * @param record                a restified record
     * @return form element for a resource record
     */
    public static String formForNestedResourceRecord(String parentResourceName, 
            RESTified parentRecord, String resourceName, RESTified record) {
        return (isAddForm(record))?
            formForAddNestedResource(parentResourceName, parentRecord, resourceName):
            formForEditNestedResourceRecord(parentResourceName, parentRecord, resourceName, record);
    }
    
    /**
     * Returns form element for a nested resource. 
     * 
     * @param parentResourceName    name of parent resource
     * @param parentRestfulId       parent restful id
     * @param resourceName          name of the resource
     * @param record                a restified record
     * @return form element for a resource record
     */
    public static String formForNestedResourceRecord(String parentResourceName, 
            String parentRestfulId, String resourceName, RESTified record) {
        return (isAddForm(record))?
            formForAddNestedResource(parentResourceName, parentRestfulId, resourceName):
            formForEditNestedResourceRecord(parentResourceName, parentRestfulId, resourceName, record);
    }
    
    /**
     * Returns form element for a nested resource. <tt>parentResourceNames</tt> 
     * is an array of ancestors. <tt>parentRestfuls</tt> is an array of either 
     * restful id strings or RESTified records of ancestors. 
     * 
     * @param parentResourceNames   names of parent resources
     * @param parentRestfuls        parent restfuls
     * @param resourceName          name of the resource
     * @param record                a restified record
     * @return form element for a resource record
     */
    public static String formForNestedResourceRecord(String[] parentResourceNames, 
            Object[] parentRestfuls, String resourceName, RESTified record) {
        return (isAddForm(record))?
            formForAddNestedResource(parentResourceNames, parentRestfuls, resourceName):
            formForEditNestedResourceRecord(parentResourceNames, parentRestfuls, resourceName, record);
    }
    
    /**
     * Returns form element for a nested resource. 
     * 
     * @param parentResourceName    name of parent resource
     * @param parentRecord          parent restified record
     * @param resourceName          name of the resource
     * @param restfulId             restful id
     * @return form element for a resource record
     */
    public static String formForNestedResourceRecord(String parentResourceName, 
            RESTified parentRecord, String resourceName, String restfulId) {
        return (restfulId == null)?
            formForAddNestedResource(parentResourceName, parentRecord, resourceName):
            formForEditNestedResourceRecord(parentResourceName, parentRecord, resourceName, restfulId);
    }
    
    /**
     * Returns form element for a nested resource. 
     * 
     * @param parentResourceName    name of parent resource
     * @param parentRestfulId       parent restful id
     * @param resourceName          name of the resource
     * @param restfulId             restful id
     * @return form element for a resource record
     */
    public static String formForNestedResourceRecord(String parentResourceName, 
            String parentRestfulId, String resourceName, String restfulId) {
        return (restfulId == null)?
            formForAddNestedResource(parentResourceName, parentRestfulId, resourceName):
            formForEditNestedResourceRecord(parentResourceName, parentRestfulId, resourceName, restfulId);
    }
    
    /**
     * Returns form element for a nested resource. <tt>parentResourceNames</tt> 
     * is an array of ancestors. <tt>parentRestfuls</tt> is an array of either 
     * restful id strings or RESTified records of ancestors. 
     * 
     * @param parentResourceNames   names of parent resources
     * @param parentRestfuls        parent restfuls
     * @param resourceName          name of the resource
     * @param restfulId             restful id
     * @return form element for a resource record
     */
    public static String formForNestedResourceRecord(String[] parentResourceNames, 
            Object[] parentRestfuls, String resourceName, String restfulId) {
        return (restfulId == null)?
            formForAddNestedResource(parentResourceNames, parentRestfuls, resourceName):
            formForEditNestedResourceRecord(parentResourceNames, parentRestfuls, resourceName, restfulId);
    }
    
    /**
     * Returns form element for adding a resource record. 
     * 
     * @param resourceName     name of the resource
     * @return form element for adding a resource record
     */
    public static String formForAddResource(String resourceName) {
        return _formForAddResource(resourcePath(resourceName), resourceName);
    }
    
    /**
     * Returns form element for editing a resource record. 
     * 
     * @param resourceName     resource name
     * @param record            a restified record
     * @return form element for editing a resource record
     */
    public static String formForEditResourceRecord(String resourceName, RESTified record) {
        return formForEditResourceRecord(resourceName, getRestfulId(record));
    }
    
    private static String _formForEditResourceRecord(String pathToResourceId, String resourceName, String resourceId) {
        Resource resource = resource(resourceName);
        String name = "edit_" + resource.getModel();
        String id = name + "_" + resourceId;
        StringBuilder sb = new StringBuilder();
        sb.append("<form action=\"").append(W.getURL(pathToResourceId));
        sb.append("\" class=\"").append(name).append("\" id=\"").append(id).append("\" method=\"POST\">").append("\n");
        sb.append("<div style=\"margin:0;padding:0\"><input name=\"");
        sb.append(Constants.HTTP_METHOD).append("\" type=\"hidden\" value=\"PUT\" /></div>");
        return sb.toString();
    }
    
    /**
     * Returns form element for editing a specific resource. 
     * 
     * @param resourceName     resource name
     * @param resourceId       a specific resource
     * @return form element for editing a resource record
     */
    public static String formForEditResourceRecord(String resourceName, String resourceId) {
        return _formForEditResourceRecord(resourceRecordPath(resourceName, resourceId), resourceName, resourceId);
    }
    
    
    
    private static Resource _parentResource(String parentResourceName) {
        Resource resource = MatchMaker.getInstance().getResource(parentResourceName);
        if (resource == null) {
            resource = new Resource(parentResourceName, true);
        }
        return resource;
    }
    
    private static String getParentResourceIdFormat(String parentResourceName) {
        return Resource.getNesteeIdFormat(_parentResource(parentResourceName));
    }
    
    /**
     * Merges content of <tt>mapTmp</tt> to <tt>mapToKeep</tt>.
     * 
     * @param mapToKeep
     * @param mapTmp
     */
    private static void mergeMap(Map<String, String> mapToKeep, Map<String, String> mapTmp) {
        for(Map.Entry<String, String> entry : mapTmp.entrySet()) {
            String value = entry.getValue();
            if (value != null) {
                mapToKeep.put(entry.getKey(), value);
            }
        }
    }
    
    private static String _nestedResourcePath(String resourceName, Map<String, String> parentsMap) {
        Map<String, String> map = getFieldValues();
        mergeMap(map, parentsMap);
        return _resourcePath(resourceName, map);
    }
    
    private static String _nestedResourceRecordPath(String resourceName, 
                                                    String resourceId, 
                                                    Map<String, String> parentsMap) {
        Map<String, String> map = getFieldValues();
        mergeMap(map, parentsMap);
        return _resourceRecordPath(resourceName, resourceId, map);
    }
    
    public static String nestedResourcePath(String parentResourceName, 
                                            RESTified parentRecord, 
                                            String resourceName) {
        return nestedResourcePath(parentResourceName, getRestfulId(parentRecord), resourceName);
    }
    
    public static String nestedResourcePath(String parentResourceName, 
                                            String parentRestfulId, 
                                            String resourceName) {
        String parentPath = resourceRecordPath(parentResourceName, parentRestfulId);
        if (!parentPath.startsWith("/")) parentPath = "/" + parentPath;
        
        Map<String, String> map = new HashMap<String, String>();
        map.put(getParentResourceIdFormat(parentResourceName), parentRestfulId);
        String childrenPath = _nestedResourcePath(resourceName, map);
        if (!childrenPath.startsWith("/")) childrenPath = "/" + childrenPath;
        return (childrenPath.startsWith(parentPath))?childrenPath:
                    parentPath + childrenPath;
    }
    
    public static String nestedResourcePath(String[] parentResourceNames,  
                                            Object[] parentRestfuls, 
                                            String resourceName) {
        if (parentResourceNames.length != parentRestfuls.length) 
            throw new IllegalArgumentException("The length of parent resource " + 
                "should be the equal to the length of parent resource ids.");
        
        int length = parentResourceNames.length;
        String parentsPath = "";
        Map<String, String> map = new HashMap<String, String>(length);
        for (int i = 0; i < length; i++) {
            String parentResourceName = parentResourceNames[i];
            String parentRestfulId = getRestfulId(parentRestfuls[i]);
            map.put(getParentResourceIdFormat(parentResourceName), parentRestfulId);
            String parentPath = _nestedResourceRecordPath(parentResourceName, parentRestfulId, map);
            if (!parentPath.startsWith("/")) parentPath = "/" + parentPath;
            parentsPath = (parentPath.startsWith(parentsPath))?parentPath:(parentsPath + parentPath);
        }
        
        String childrenPath = _nestedResourcePath(resourceName, map);
        if (!childrenPath.startsWith("/")) childrenPath = "/" + childrenPath;
        return (childrenPath.startsWith(parentsPath))?childrenPath:
                    parentsPath + childrenPath;
    }
    
    public static String nestedResourceRecordPath(String parentResourceName, 
                                                  RESTified parentRecord, 
                                                  String resourceName, 
                                                  RESTified record) {
        return nestedResourceRecordPath(parentResourceName, getRestfulId(parentRecord), resourceName, getRestfulId(record));
    }
    
    public static String nestedResourceRecordPath(String parentResourceName, 
                                                  String parentRestfulId, 
                                                  String resourceName, 
                                                  RESTified record) {
        return nestedResourceRecordPath(parentResourceName, parentRestfulId, resourceName, getRestfulId(record));
    }
    
    public static String nestedResourceRecordPath(String parentResourceName, 
                                                  RESTified parentRecord, 
                                                  String resourceName, 
                                                  String resourceId) {
        return nestedResourceRecordPath(parentResourceName, getRestfulId(parentRecord), resourceName, resourceId);
    }
    
    public static String nestedResourceRecordPath(String parentResourceName, 
                                                  String parentRestfulId, 
                                                  String resourceName, 
                                                  String resourceId) {
        String parentPath = resourceRecordPath(parentResourceName, parentRestfulId);
        if (!parentPath.startsWith("/")) parentPath = "/" + parentPath;
        
        Map<String, String> map = new HashMap<String, String>();
        map.put(getParentResourceIdFormat(parentResourceName), parentRestfulId);
        String childrenPath = _nestedResourceRecordPath(resourceName, resourceId, map);
        if (!childrenPath.startsWith("/")) childrenPath = "/" + childrenPath;
        return (childrenPath.startsWith(parentPath))?childrenPath:
                    parentPath + childrenPath;
    }
    
    public static String nestedResourceRecordPath(String[] parentResourceNames, 
                                                  Object[] parentRestfuls, 
                                                  String resourceName, 
                                                  RESTified record) {
        return nestedResourceRecordPath(parentResourceNames, parentRestfuls, resourceName, getRestfulId(record));
    }
    
    public static String nestedResourceRecordPath(String[] parentResourceNames, 
                                                  Object[] parentRestfuls, 
                                                  String resourceName, 
                                                  String resourceId) {
        if (parentResourceNames.length != parentRestfuls.length) 
            throw new IllegalArgumentException("The length of parent resource " + 
                "should be the equal to the length of parent resource ids.");
        
        int length = parentResourceNames.length;
        String parentsPath = "";
        Map<String, String> map = new HashMap<String, String>(length);
        for (int i = 0; i < length; i++) {
            String parentResourceName = parentResourceNames[i];
            String parentRestfulId = getRestfulId(parentRestfuls[i]);
            map.put(getParentResourceIdFormat(parentResourceName), parentRestfulId);
            String parentPath = _nestedResourceRecordPath(parentResourceName, parentRestfulId, map);
            if (!parentPath.startsWith("/")) parentPath = "/" + parentPath;
            parentsPath = (parentPath.startsWith(parentsPath))?parentPath:(parentsPath + parentPath);
        }
        
        String childrenPath = _nestedResourceRecordPath(resourceName, resourceId, map);
        if (!childrenPath.startsWith("/")) childrenPath = "/" + childrenPath;
        return (childrenPath.startsWith(parentsPath))?childrenPath:
                    parentsPath + childrenPath;
    }
    
    public static String addNestedResourcePath(String parentResourceName, 
                                               RESTified parentRecord, 
                                               String resourceName) {
        return _addResourcePath(nestedResourcePath(parentResourceName, parentRecord, resourceName));
    }
    
    public static String addNestedResourcePath(String parentResourceName, 
                                               String parentRestfulId, 
                                               String resourceName) {
        return _addResourcePath(nestedResourcePath(parentResourceName, parentRestfulId, resourceName));
    }
    
    public static String addNestedResourcePath(String[] parentResourceNames, 
                                               Object[] parentRestfuls, 
                                               String resourceName) {
        return _addResourcePath(nestedResourcePath(parentResourceNames, parentRestfuls, resourceName));
    }
    
    public static String editNestedResourceRecordPath(String parentResourceName, 
                                                RESTified parentRecord, 
                                                String resourceName, 
                                                RESTified record) {
        return _editResourceRecordPath(nestedResourceRecordPath(parentResourceName, parentRecord, resourceName, record));
    }
    
    public static String editNestedResourceRecordPath(String parentResourceName, 
                                                RESTified parentRecord, 
                                                String resourceName, 
                                                String resourceId) {
        return _editResourceRecordPath(nestedResourceRecordPath(parentResourceName, parentRecord, resourceName, resourceId));
    }
    
    public static String editNestedResourceRecordPath(String parentResourceName, 
                                                String parentRestfulId, 
                                                String resourceName, 
                                                RESTified record) {
        return _editResourceRecordPath(nestedResourceRecordPath(parentResourceName, parentRestfulId, resourceName, record));
    }
    
    public static String editNestedResourceRecordPath(String parentResourceName, 
                                                String parentRestfulId, 
                                                String resourceName, 
                                                String resourceId) {
        return _editResourceRecordPath(nestedResourceRecordPath(parentResourceName, parentRestfulId, resourceName, resourceId));
    }
    
    public static String editNestedResourceRecordPath(String[] parentResourceNames, 
                                                Object[] parentRestfuls, 
                                                String resourceName, 
                                                RESTified record) {
        return _editResourceRecordPath(nestedResourceRecordPath(parentResourceNames, parentRestfuls, resourceName, record));
    }
    
    public static String editNestedResourceRecordPath(String[] parentResourceNames, 
                                                Object[] parentRestfuls, 
                                                String resourceName, 
                                                String resourceId) {
        return _editResourceRecordPath(nestedResourceRecordPath(parentResourceNames, parentRestfuls, resourceName, resourceId));
    }
    
    public static String formForAddNestedResource(String parentResourceName, 
                                                  RESTified parentRecord, 
                                                  String resourceName) {
        return _formForAddResource(nestedResourcePath(parentResourceName, parentRecord, resourceName), resourceName);
    }
    
    public static String formForAddNestedResource(String parentResourceName, 
                                                  String parentRestfulId, 
                                                  String resourceName) {
        return _formForAddResource(nestedResourcePath(parentResourceName, parentRestfulId, resourceName), resourceName);
    }
    
    public static String formForAddNestedResource(String[] parentResourceNames, 
                                                  Object[] parentRestfuls, 
                                                  String resourceName) {
        return _formForAddResource(nestedResourcePath(parentResourceNames, parentRestfuls, resourceName), resourceName);
    }
    
    public static String formForEditNestedResourceRecord(String parentResourceName, 
                                                   RESTified parentRecord, 
                                                   String resourceName, 
                                                   RESTified record) {
        return _formForEditResourceRecord(nestedResourceRecordPath(parentResourceName, parentRecord, resourceName, record), resourceName, getRestfulId(record));
    }
    
    public static String formForEditNestedResourceRecord(String parentResourceName, 
                                                   RESTified parentRecord, 
                                                   String resourceName, 
                                                   String resourceId) {
        return _formForEditResourceRecord(nestedResourceRecordPath(parentResourceName, parentRecord, resourceName, resourceId), resourceName, resourceId);
    }
    
    public static String formForEditNestedResourceRecord(String parentResourceName, 
                                                   String parentRestfulId, 
                                                   String resourceName,  
                                                   RESTified record) {
        return _formForEditResourceRecord(nestedResourceRecordPath(parentResourceName, parentRestfulId, resourceName, record), resourceName, getRestfulId(record));
    }
    
    public static String formForEditNestedResourceRecord(String parentResourceName, 
                                                   String parentRestfulId, 
                                                   String resourceName, 
                                                   String resourceId) {
        return _formForEditResourceRecord(nestedResourceRecordPath(parentResourceName, parentRestfulId, resourceName, resourceId), resourceName, resourceId);
    }
    
    public static String formForEditNestedResourceRecord(String[] parentResourceNames, 
                                                   Object[] parentRestfuls, 
                                                   String resourceName, 
                                                   RESTified record) {
        return _formForEditResourceRecord(nestedResourceRecordPath(parentResourceNames, parentRestfuls, resourceName, record), resourceName, getRestfulId(record));
    }
    
    public static String formForEditNestedResourceRecord(String[] parentResourceNames, 
                                                   Object[] parentRestfuls, 
                                                   String resourceName, 
                                                   String resourceId) {
        return _formForEditResourceRecord(nestedResourceRecordPath(parentResourceNames, parentRestfuls, resourceName, resourceId), resourceName, resourceId);
    }
    
    
    
    
    /**
     * Returns route. 
     * 
     * @param routeName     route name
     * @return route
     */
    public static Route route(String routeName) {
        //validate the routeName
        Route route = MatchMaker.getInstance().getRoute(routeName);
        if (route == null) {
            throw new IllegalArgumentException("\"" + routeName + "\"" + 
                        " is not defined as a route.");
        }
        return route;
    }
    
    /**
     * Returns route path based on a route name. 
     * 
     * For example, for named route <tt>"login"</tt>, the route path may be <tt>"/login"</tt>.
     * 
     * @param routeName     route name
     * @return route path
     */
    public static String routePath(String routeName) {
        return route(routeName).getScreenURL(getFieldValues());
    }
    
    /**
     * <p>Returns route path based on a record instance. If the record's 
     * RESTful id is empty, an empty string is returned.</p>
     * 
     * <p>For example, for record <tt>"Post"</tt> with <tt>post.getRestfulId()=1</tt>, 
     * the route path is <tt>"/posts/1"</tt>.</p>
     * 
     * @param routeName     route name
     * @param record        a restified record
     * @return route path
     */
    public static String routeRecordPath(String routeName, RESTified record) {
    	String s = getRestfulId(record);
    	if (M.isEmpty(s)) return "";
        return routePath(routeName) + "/" + s;
    }
    
    
    /**
     * <p>Returns a label link on the <tt>columnName</tt> for <tt>columnValue</tt>.</p>
     * 
     * <p>There are several restrictions of the use of this method. </p>
     * <p>First, the column name must be of the <tt>'{referencedModelName}_id'</tt> 
     * format where the <tt>referencedModelName</tt> is the model name of the 
     * referenced entity. Second, the primary key of the referenced model must 
     * be <tt>id</tt>. </p>
     * 
     * <p>If the above conditions are violated, the original 
     * <tt>columnName</tt> is returned.</p>
     * 
     * <pre>
     * Examples:
     *      column    value    link
     *      ------    -----    ----
     *      user_id   10       <a http="/blog/users/10">10</a>
     * </pre>
     * 
     * <p>See method 
     * {@link com.scooterframework.web.util.W#simpleForeignKeyRecordShowActionLink(String, String)} 
     * for action (non resource) case.</p>
     * 
     * @param columnName  a column name ended with "_id"
     * @param columnValue the value on the column
     * @return a label link
     */
    public static String simpleForeignKeyResourceRecordLink(String columnName, String columnValue) {
        if (columnName == null || 
            !columnName.toLowerCase().endsWith("_id")) return columnName;
        
        String modelName = columnName.toLowerCase().substring(0, (columnName.length() - 3));
        String modelClassName = EnvConfig.getInstance().getModelClassName(modelName);
        
        ActiveRecord foreignRecordHome = ActiveRecordUtil.getHomeInstance(modelClassName, modelName, ActiveRecordUtil.DEFAULT_RECORD_CLASS);
        String[] pkNames = foreignRecordHome.getPrimaryKeyNames();
        if (pkNames == null || pkNames.length > 1 || 
            !"id".equalsIgnoreCase(pkNames[0])) return columnValue;
        
        String resourceName = WordUtil.pluralize(modelName);
        
        return W.labelLink(columnValue, R.resourceRecordPath(resourceName, columnValue));
    }
}
