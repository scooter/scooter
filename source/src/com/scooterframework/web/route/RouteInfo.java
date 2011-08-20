/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.web.route;

import java.util.Map;

/**
 * RouteInfo class
 * 
 * @author (Fei) John Chen
 */
public class RouteInfo {
    private int index;
	String controller;
	String action;
	String id;
	String format;
	String controllerClassName;
    String model;
    String modelClassName;
	Map<String, String> requiredFieldValues;
	String routeType;
	RequestInfo requestInfo;
	String routeName;
    String viewPath;
    String resourceName;
	
	public RouteInfo(RequestInfo requestInfo) {
		this.requestInfo = requestInfo;
	}
	
	public RequestInfo getRequestInfo() {
		return requestInfo;
	}
    
    public int getIndex() {
        return index;
    }
    
    public void setIndex(int index) {
        this.index = index;
    }
	
	public String getController() {
		return controller;
	}
	
	public String getControllerClassName() {
		return controllerClassName;
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
	
	public String getModel() {
        return model;
	}
	
	public String getModelClassName() {
		return modelClassName;
	}
	
	public Map<String, String> getRequiredFieldValues() {
		return requiredFieldValues;
	}
	
	public String getRouteType() {
		return routeType;
	}
	
	public String getRouteName() {
		return routeName;
	}
	
	public String getViewPath() {
		return viewPath;
	}
    
    public String getResourceName() {
        return resourceName;
    }
	
	/**
     * Returns a string representation of the object.
     * @return String
     */
    public String toString() {
        StringBuilder returnString = new StringBuilder();
        String SEPARATOR = ", ";
        
        returnString.append("index = " + index).append(SEPARATOR);
        returnString.append("routeName = " + routeName).append(SEPARATOR);
        returnString.append("viewPath = " + viewPath).append(SEPARATOR);
        returnString.append("routeType = " + getRouteType()).append(SEPARATOR);
        returnString.append("resourceName = " + resourceName).append(SEPARATOR);
        returnString.append("controller = " + controller).append(SEPARATOR);
        returnString.append("controllerClassName = " + controllerClassName).append(SEPARATOR);
        returnString.append("model = " + model).append(SEPARATOR);
        returnString.append("modelClassName = " + modelClassName).append(SEPARATOR);
        returnString.append("action = " + action).append(SEPARATOR);
        returnString.append("id = " + id).append(SEPARATOR);
        returnString.append("format = " + format).append(SEPARATOR);
        returnString.append("requiredFieldValues = " + requiredFieldValues).append(SEPARATOR);
        returnString.append("requestInfo {" + requestInfo + "}");
        
        return returnString.toString();
    }
}
