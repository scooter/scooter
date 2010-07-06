/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.web.route;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import com.scooterframework.admin.EnvConfig;
import com.scooterframework.common.util.Converters;
import com.scooterframework.common.util.PropertyFileUtil;
import com.scooterframework.common.util.StringUtil;
import com.scooterframework.common.util.WordUtil;
import com.scooterframework.orm.sqldataexpress.config.DatabaseConfig;

/**
 * Route class
 * 
 * @author (Fei) John Chen
 */
abstract public class Route {
    
	protected String name;
	protected String urlPattern;
	protected String controller;
	protected String controllerClass;
	protected String action;
	protected String id;
	protected String format;
	protected String allowed_formats = RouteConstants.ROUTE_DEFAULT_ALLOWED_FORMATS;
	protected String allowed_methods = RouteConstants.ROUTE_DEFAULT_ALLOWED_METHODS;
	protected String singular;
	protected String namespace;
	protected String pathPrefix;
    protected String requirements;
	
	protected boolean dynamicController;
	protected boolean dynamicAction;
	protected boolean dynamicFormat;
	private Map requiredFieldPositions = new HashMap();
	private String[] pathSegments;
	private int segmentCount;
    private String screenURLPattern;
    private Properties requirementsProperties;
	
	protected Route() {
		;
	}
	
	public Route(String name, Properties p) {
		this.name = name;
		if (name == null) 
			throw new IllegalArgumentException("Name cannot be empty in route constructor(String, Properties).");
		
		populateProperties(p);
	}
	
	abstract public String getRouteType();
	
	abstract protected boolean isRouteFor(RequestInfo requestInfo);
	
	public RouteInfo getRouteInfo(RequestInfo requestInfo) {
		RouteInfo ri = new RouteInfo(requestInfo);
		
		Map fieldValues = new HashMap();
		if (requiredFieldPositions.size() > 0) {
			Iterator it = requiredFieldPositions.keySet().iterator();
			while(it.hasNext()) {
				String field = (String)it.next();
				int position = ((Integer)requiredFieldPositions.get(field)).intValue();
				String value = requestInfo.getPathSegments()[position];
                
                if (field.indexOf(RouteConstants.PRIMARY_KEY_SEPARATOR) != -1) {
                    fieldValues.putAll(getFieldValueMapForCompositeKey(field, value));
                }
                else {
                    fieldValues.put(field, value);
                }
			}
		}
		String tmp = "";
        tmp = (String)fieldValues.get(RouteConstants.ROUTE_KEY_CONTROLLER);
		tmp = (tmp != null)?tmp:this.controller;
        tmp = (namespace != null)?(namespace + "/" + tmp):tmp;
        ri.controller = tmp;
        
        tmp = (String)fieldValues.get(RouteConstants.ROUTE_KEY_ACTION);
		ri.action = (tmp != null)?tmp:this.action;
        
        tmp = (String)fieldValues.get(RouteConstants.ROUTE_KEY_ID);
		ri.id = (tmp != null)?tmp:this.id;
        
        ri.requiredFieldValues = fieldValues;
        
		ri.controllerClassName = getControllerClassName(ri.controller);
        ri.model = getModel(ri.controller);
        ri.modelClassName = getModelClassName(ri.controller);
		ri.format = requestInfo.getFormat();
		ri.routeType = getRouteType();
		ri.routeName = getName();
        ri.viewPath = getViewPath(ri.controller);
		return ri;
	}
    
    private static Map getFieldValueMapForCompositeKey(String compositeFields, String restfulId) {
        if (restfulId == null) 
            throw new IllegalArgumentException("restfulId cannot be null in getFieldValueMapForCompositeKey().");
        
        String[] fields = Converters.convertStringToStringArray(compositeFields, DatabaseConfig.PRIMARY_KEY_SEPARATOR);
        String[] values = Converters.convertStringToStringArray(restfulId, DatabaseConfig.PRIMARY_KEY_SEPARATOR, false);
        
        if (fields.length != values.length) {
        	if (fields.length == 1) {
        		values[0] = restfulId;
        	}
        	else {
	            throw new IllegalArgumentException("Input restfulId value \"" + 
	                restfulId + "\" with length " + values.length + " does not " + 
	                "match key fields of its related table with length " + 
	                fields.length + ".");
        	}
        }
        
        int total = fields.length;
        Map map = new HashMap(total);
        for (int i = 0; i < total; i++) {
            String field = fields[i];
            String value = values[i];
            
            if (field.startsWith("$")) field = field.substring(1);
            map.put(field.toUpperCase(), value);
        }
        
        return map;
    }
	
	public String getName() {
		return name;
	}
	
	public String getURLPattern() {
		return urlPattern;
	}
	
	public String getController() {
		return controller;
	}
	
	public String getControllerClass() {
		return controllerClass;
	}
	
	protected String getControllerClassName(String controller) {
        String ccn = "";
		if (controllerClass != null) {
			ccn = controllerClass;
		}
		else {
            ccn = controller.replace('/', '.');
			ccn = (namespace != null)?(namespace + "." + ccn):ccn;
            ccn = EnvConfig.getInstance().getControllerClassName(ccn);
		}
		return ccn;
	}
	
	public String getAction() {
		return action;
	}
	
	public String getId() {
		return id;
	}
	
	public String getFormat() {
		return format;
	}
	
	public boolean hasFormat() {
		return (format != null)?true:false;
	}
	
	public String getAllowedFormats() {
		return allowed_formats;
	}
	
	public String[] allowedFormats() {
		return (allowed_formats != null)?Converters.convertStringToStringArray(allowed_formats, RouteConstants.PROPERTY_SYMBOL_ARRAY_ITEMS_DELIMITER):null;
	}
	
	protected boolean isAllowedFormat(String fmat) {
		boolean allowed = false;
        if (allowed_formats != null) {
            if (allowed_formats.indexOf(fmat) != -1) {
                allowed = true;
            }
        }
        else {
            if (format == null) {
                if (fmat == null) {
                    allowed = true;
                }
            }
            else {
                if (fmat != null) {
                    if (fmat.equals(format) || dynamicFormat) allowed = true;
                }
            }
        }
        
		return allowed;
	}
	
	public String getAllowedMethods() {
		return allowed_methods;
	}
	
	public String[] allowedMethods() {
		String[] sa = Converters.convertStringToStringArray(allowed_methods, RouteConstants.PROPERTY_SYMBOL_ARRAY_ITEMS_DELIMITER);
		return sa;
	}
    
    public static void validateMethods(String httpMethods) {
        String[] sa = Converters.convertStringToStringArray(httpMethods, RouteConstants.PROPERTY_SYMBOL_ARRAY_ITEMS_DELIMITER);
        if (sa == null) return;
        int length = sa.length;
        for (int i = 0; i < length; i++) {
            String m = sa[i];
            if (m != null && RouteConstants.ROUTE_HTTP_ALL_METHODS.indexOf(m.toUpperCase()) == -1) {
                throw new IllegalArgumentException("Method \"" + m + "\" in string \"" + httpMethods + "\" is not a supported HTTP methods.");
            }
        }
    }
	
	protected boolean isAllowedMethod(String method) {
        boolean allowed = false;
        if (allowed_methods != null) {
            if (allowed_methods.toUpperCase().indexOf(RouteConstants.ROUTE_HTTP_METHOD_ANY) != -1) {
                allowed = true;
            }
            else 
            if (allowed_methods.toUpperCase().indexOf(method.toUpperCase()) != -1) {
                allowed = true;
            }
        }
        else {
            allowed = true;
        }
		return allowed;
	}
	
	public String getSingular() {
		return singular;
	}
	
	protected String getModel(String controller) {
        String model = null;
        if (singular != null) {
            model = singular;
        }
        else {
            model = controller;
            int lastSlash = controller.lastIndexOf('/');
            if (lastSlash != -1) {
                model = model.substring(lastSlash + 1);
            }
            int lastDot = controller.lastIndexOf('.');
            if (lastDot != -1) {
                model = model.substring(lastDot + 1);
            }
            model = (DatabaseConfig.getInstance().usePluralTableName())?WordUtil.singularize(model):model;
        }
        return model;
	}
	
	public String getModelClassName(String controller) {
        return EnvConfig.getInstance().getModelClassName(getModel(controller));
	}
	
	public String getNamespace() {
		return namespace;
	}
	
	public String getPathPrefix() {
		return pathPrefix;
	}
	
	public String getRequirements() {
		return requirements;
	}
    
    /**
     * Returns screen URL which is a combination of <tt>path_prefix</tt> and
     * <tt>url</tt>.
     */
    protected String getScreenURLPattern() {
        return screenURLPattern;
    }
    
    public String getScreenURL(Map fieldValues) {
        return resolveURL(getScreenURLPattern(), fieldValues);
    }
    
    static String resolveURL(String urlPattern, Map fieldValues) {
        if (fieldValues == null || fieldValues.size() == 0 ||
            urlPattern == null || urlPattern.indexOf("$") == -1) return urlPattern;
        
        Iterator it = fieldValues.keySet().iterator();
        while(it.hasNext()) {
            String field = (String)it.next();
            String value = (String)fieldValues.get(field);
            if (urlPattern.indexOf(field) == -1) continue;
            
            value = StringUtil.replace(value, "$", "\\$");
            
            if (value == null) 
                throw new IllegalArgumentException("There is no value " + 
                    "provided for field \"" + field + "\" in url \"" + 
                    urlPattern + "\". Provided field/value pairs are " + fieldValues + ".");
            
            urlPattern = urlPattern.replaceAll("\\$" + field, value);
        }
        return urlPattern;
    }
    
    /**
     * Path to the view file
     */
    public String getViewPath(String controller) {
        return controller;
    }
	
	public String[] getPathSegments() {
		return pathSegments;
	}
	
	public int segmentCount() {
		return segmentCount;
	}
	
	public Map getRequiredFieldPositions() {
		return requiredFieldPositions;
	}
    
    protected boolean isAllowedFieldValue(RequestInfo requestInfo) {
        //no restrictions
        if (requirementsProperties == null || requirementsProperties.size() == 0) return true;
        
		if (requiredFieldPositions.size() > 0) {
			Iterator it = requiredFieldPositions.keySet().iterator();
			while(it.hasNext()) {
				String field = (String)it.next();
                String requirementStr = requirementsProperties.getProperty(field);
                if (requirementStr == null) continue;
				int position = ((Integer)requiredFieldPositions.get(field)).intValue();
				String value = requestInfo.getPathSegments()[position];
                if (!matchRequirement(requirementStr, value)) return false;
			}
		}
        return true;
    }
    
    private boolean matchRequirement(String requirementStr, String input) {
        if (requirementStr == null) return true;
        if (input == null) return false;
        
        boolean result = false;
        if (requirementStr.startsWith("/") && requirementStr.endsWith("/")) {
            //apply Pattern
            requirementStr = requirementStr.substring(1, requirementStr.length() -1);
            result = Pattern.matches(requirementStr, input);
        }
        return result;
    }
	
	public String getURLSegment(String key, String path) {
		int position = ((Integer)requiredFieldPositions.get(key)).intValue();
		String s = path;
    	if (path.startsWith("/")) s = path.substring(1);
    	String[] segs = s.split("/");
    	if (segmentCount != segs.length) 
    		throw new IllegalArgumentException("The number of segments of the input path does not match what is required by this route.");
    	return segs[position];
	}
	
	public void copy(Route route) {
		name = route.getName();
		urlPattern = route.getURLPattern();
		controller = route.getController();
		controllerClass = route.getControllerClass();
		action = route.getAction();
		id = route.getId();
		format = route.getFormat();
		allowed_formats = route.getAllowedFormats();
		allowed_methods = route.getAllowedMethods();
		singular = route.getSingular();
		namespace = route.getNamespace();
		pathPrefix = route.getPathPrefix();
		requirements = route.getRequirements();
		
		dynamicController = route.dynamicController;
		dynamicAction = route.dynamicAction;
		dynamicFormat = route.dynamicFormat;
		requiredFieldPositions = route.getRequiredFieldPositions();
		pathSegments = route.getPathSegments();
		segmentCount = route.segmentCount();
	}
	
	/**
     * Returns a string representation of the object.
     * @return String
     */
    public String toString() {
        StringBuffer returnString = new StringBuffer();
        String SEPARATOR = ", ";
        
        returnString.append("name = " + name).append(SEPARATOR);
        returnString.append("routeType = " + getRouteType()).append(SEPARATOR);
        returnString.append("url = " + urlPattern).append(SEPARATOR);
        returnString.append("controller = " + controller).append(SEPARATOR);
        returnString.append("controllerClass = " + controllerClass).append(SEPARATOR);
        returnString.append("dynamicController = " + dynamicController).append(SEPARATOR);
        returnString.append("action = " + action).append(SEPARATOR);
        returnString.append("dynamicAction = " + dynamicAction).append(SEPARATOR);
        returnString.append("id = " + id).append(SEPARATOR);
        returnString.append("format = " + format).append(SEPARATOR);
        returnString.append("dynamicFormat = " + dynamicFormat).append(SEPARATOR);
        returnString.append("allowed_formats = " + allowed_formats).append(SEPARATOR);
        returnString.append("allowed_methods = " + allowed_methods).append(SEPARATOR);
        returnString.append("singular = " + singular).append(SEPARATOR);
        returnString.append("namespace = " + namespace).append(SEPARATOR);
        returnString.append("pathPrefix = " + pathPrefix).append(SEPARATOR);
        returnString.append("requirements = " + requirements).append(SEPARATOR);
        returnString.append("requiredFieldPositions = " + requiredFieldPositions).append(SEPARATOR);
        returnString.append("segmentCount = " + segmentCount);
        
        return returnString.toString();
    }
	
	protected void populateProperties(Properties p) {
		urlPattern = p.getProperty(RouteConstants.ROUTE_KEY_URL);
		if (urlPattern == null) 
			throw new IllegalArgumentException("url cannot be empty in route named " + name + ".");
		
		controller = p.getProperty(RouteConstants.ROUTE_KEY_CONTROLLER);
		controllerClass = p.getProperty(RouteConstants.ROUTE_KEY_CONTROLLER_CLASS);
		action = p.getProperty(RouteConstants.ROUTE_KEY_ACTION);
		id = p.getProperty(RouteConstants.ROUTE_KEY_ID);
        
		allowed_formats = p.getProperty(RouteConstants.ROUTE_KEY_ALLOWED_FORMATS, allowed_formats);
        allowed_formats = StringUtil.remove(allowed_formats, RouteConstants.PROPERTY_SYMBOL_ARRAY);
        
		allowed_methods = p.getProperty(RouteConstants.ROUTE_KEY_ALLOWED_METHODS, allowed_methods);
        allowed_methods = StringUtil.remove(allowed_methods, RouteConstants.PROPERTY_SYMBOL_ARRAY);
        
		singular = p.getProperty(RouteConstants.ROUTE_KEY_SINGULAR);
		namespace = p.getProperty(RouteConstants.ROUTE_KEY_NAMESPACE);
		pathPrefix = p.getProperty(RouteConstants.ROUTE_KEY_PATH_PREFIX);
        
        //
        //parse requirements properties
        //
        requirements = p.getProperty(RouteConstants.ROUTE_KEY_REQUIREMENTS);
        requirements = StringUtil.remove(requirements, RouteConstants.PROPERTY_SYMBOL_GROUP);
        if (requirements != null) {
            requirementsProperties = 
                PropertyFileUtil.parseNestedPropertiesFromLine(requirements, 
                        RouteConstants.PROPERTY_SYMBOL_GROUP_ITEM_ASSIGN, 
                        RouteConstants.PROPERTY_SYMBOL_GROUP_ITEMS_DELIMITER);
        }
		
        screenURLPattern = urlPattern;
        if (pathPrefix != null && !"".equals(pathPrefix)) {
            if (!screenURLPattern.startsWith("/")) screenURLPattern = "/" + screenURLPattern;
            screenURLPattern = pathPrefix + screenURLPattern;
        }
		parsePath(screenURLPattern);
		
		populateRequiredFields();
		
		validation();
	}
    
    protected void parsePath(String path) {
		if ("".equals(path) || "/".equals(path)) {
			segmentCount = 0;
    	}
		else {
			String s = path;
	    	if (path.startsWith("/")) s = path.substring(1);
	    	
	    	int lastDot = s.lastIndexOf('.');
	    	int lastSlash = s.lastIndexOf('/');
	    	if (lastDot > lastSlash) {
	    		format = s.substring(lastDot + 1);
	    		s = s.substring(0, lastDot);
	    	}
	    	
	    	pathSegments = s.split("/");
	    	segmentCount = pathSegments.length;
		}
		
		if (RouteConstants.ROUTE_DEFAULT_FORMAT.equals(format)) dynamicFormat = true;
	}
    
    protected void populateRequiredFields() {
    	int length = segmentCount;
    	for (int i = 0; i < length; i++) {
			String element = pathSegments[i];
			if (element.startsWith("$")) {
				String elementName = element.substring(1);
				if (RouteConstants.ROUTE_KEY_CONTROLLER.equals(elementName)) {
					if (controller != null) {
						throw new IllegalArgumentException("Wrong route definition: controller is already specified.");
					}
					else {
						dynamicController = true;
						requiredFieldPositions.put(RouteConstants.ROUTE_KEY_CONTROLLER, new Integer(i));
					}
				}
				else 
				if (RouteConstants.ROUTE_KEY_ACTION.equals(elementName)) {
					if (action != null) {
						throw new IllegalArgumentException("Wrong route definition: action is already specified.");
					}
					else {
						dynamicAction = true;
						requiredFieldPositions.put(RouteConstants.ROUTE_KEY_ACTION, new Integer(i));
					}
				}
				else {
					requiredFieldPositions.put(elementName, new Integer(i));
				}
			}
		}
    }
    
    protected void validation() {
		if (!dynamicController && (controller == null && controllerClass == null)) {
			throw new IllegalArgumentException("controller cannot be empty in route named " + name + ".");
		}
		
		if (!dynamicAction && action == null) {
			throw new IllegalArgumentException("action cannot be empty in route named " + name + ".");
		}
        
        validateMethods(allowed_methods);
    }
}
