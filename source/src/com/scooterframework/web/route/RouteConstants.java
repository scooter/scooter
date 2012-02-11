/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.web.route;

/**
 * RouteConstants class
 * 
 * @author (Fei) John Chen
 */
public class RouteConstants {
    
	public static final String ROUTE_ACTION_LIST_RESOURCES = "index";
	public static final String ROUTE_ACTION_ADD_RESOURCE = "add";
	public static final String ROUTE_ACTION_CREATE_RESOURCE = "create";
	public static final String ROUTE_ACTION_READ_RESOURCE = "show";
	public static final String ROUTE_ACTION_EDIT_RESOURCE = "edit";
	public static final String ROUTE_ACTION_UPDATE_RESOURCE = "update";
	public static final String ROUTE_ACTION_DELETE_RESOURCE = "delete";
    
	public static final String HTTP_METHOD_PATH_GLUE = " ";
    
    public static final String ROUTE_HTTP_METHOD_ANY = "ANY";
	public static final String ROUTE_HTTP_METHOD_GET = "GET";
    public static final String ROUTE_HTTP_METHOD_POST = "POST";
    public static final String ROUTE_HTTP_METHOD_PUT = "PUT";
    public static final String ROUTE_HTTP_METHOD_DELETE = "DELETE";
    public static final String ROUTE_HTTP_METHOD_HEAD = "HEAD";
    public static final String ROUTE_HTTP_ALL_METHODS = 
                            ROUTE_HTTP_METHOD_ANY + HTTP_METHOD_PATH_GLUE + 
                            ROUTE_HTTP_METHOD_GET + HTTP_METHOD_PATH_GLUE + 
                            ROUTE_HTTP_METHOD_POST + HTTP_METHOD_PATH_GLUE + 
                            ROUTE_HTTP_METHOD_PUT + HTTP_METHOD_PATH_GLUE + 
                            ROUTE_HTTP_METHOD_DELETE + HTTP_METHOD_PATH_GLUE + 
                            ROUTE_HTTP_METHOD_HEAD;
	
	public static final String ROUTE_TYPE_NAMED = "named_route";
	public static final String ROUTE_TYPE_DEFAULT = "default_route";
	public static final String ROUTE_TYPE_RGULAR = "regular_route";
	public static final String ROUTE_TYPE_REST = "rest_route";
	public static final String ROUTE_TYPE_ROOT = "root_route";
    
    public static final String FIELD_VALUES = "scooter.field_values";
    
    /**
     * A hyphen "-" is used to link composite primary key fields.
     */
    public static final String PRIMARY_KEY_SEPARATOR = "-";
	
    //************************************************************************
    // Keys that are used by both Route and Resource
    //************************************************************************
	public static final String ROUTE_KEY_URL = "url";
	public static final String ROUTE_KEY_CONTROLLER = "controller";
	public static final String ROUTE_KEY_CONTROLLER_CLASS = "controller_class";
	public static final String ROUTE_KEY_ACTION = "action";
	public static final String ROUTE_KEY_ID = "id";
	public static final String ROUTE_KEY_ALLOWED_FORMATS = "allowed_formats";
	public static final String ROUTE_KEY_ALLOWED_METHODS = "allowed_methods";
    public static final String ROUTE_KEY_SINGULAR = "singular";
	public static final String ROUTE_KEY_NAMESPACE = "namespace";
	public static final String ROUTE_KEY_PATH_PREFIX = "path_prefix";
	public static final String ROUTE_KEY_REQUIREMENTS = "requirements";
	public static final String ROUTE_KEY_CACHEABLE = "cacheable";
	
    //************************************************************************
    // Keys that are used only by Resource
    //************************************************************************
	public static final String ROUTE_KEY_PATH_ALIAS = "path_alias";
	public static final String ROUTE_KEY_ACTION_ALIAS = "action_alias";
	public static final String ROUTE_KEY_ONLY = "only";
	public static final String ROUTE_KEY_EXCEPT = "except";
	public static final String ROUTE_KEY_MEMBER = "member";
	public static final String ROUTE_KEY_COLLECTION = "collection";
	public static final String ROUTE_KEY_ADD = "add";
	public static final String ROUTE_KEY_PARENTS = "parents";
	
    //************************************************************************
    // Some default values
    //************************************************************************
	public static final String ROUTE_DEFAULT_CONTROLLER = "$controller";
	public static final String ROUTE_DEFAULT_ACTION = "$action";
	public static final String ROUTE_DEFAULT_ID = "$id";
	public static final String ROUTE_DEFAULT_FORMAT = "$format";
	public static final String ROUTE_DEFAULT_ALLOWED_FORMATS = null;
	public static final String ROUTE_DEFAULT_ALLOWED_METHODS = ROUTE_HTTP_METHOD_ANY;
    
    //************************************************************************
    // Keys for parsing nested properties
    //************************************************************************
    public static final String PROPERTY_SYMBOL_NAMEVALUESPLITER = ":";
    public static final String PROPERTY_SYMBOL_PROPERTYDELIMITER = ";";
    public static final String PROPERTY_SYMBOL_GROUP = "{}";
    //public static final String PROPERTY_SYMBOL_GROUP_START = "{";
    //public static final String PROPERTY_SYMBOL_GROUP_END = "}";
    public static final String PROPERTY_SYMBOL_GROUP_ITEM_ASSIGN = "=>";
    public static final String PROPERTY_SYMBOL_GROUP_ITEMS_DELIMITER = ",";
    public static final String PROPERTY_SYMBOL_ARRAY = "[]";
    //public static final String PROPERTY_SYMBOL_ARRAY_START = "[";
    //public static final String PROPERTY_SYMBOL_ARRAY_END = "]";
    public static final String PROPERTY_SYMBOL_ARRAY_ITEMS_DELIMITER = "|";
    public static final String PROPERTY_SYMBOL_STRICT_PARENT = "strict";
    public static final String PROPERTY_SYMBOL_PARENTS_CONNECTION = "->";
}
