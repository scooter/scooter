/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.web.route;

import java.util.Properties;

/**
 * NamedRoute class
 * 
 * @author (Fei) John Chen
 */
public class NamedRoute extends RegularRoute {
	
	protected NamedRoute() {
		;
	}
	
	public NamedRoute(String name, Properties p) {
		super(name, p);
	}
	
	public String getRouteType() {
		return RouteConstants.ROUTE_TYPE_NAMED;
	}
	
	public boolean isRouteFor(RequestInfo requestInfo) {
		String internalPath = getScreenURLPattern();
		if (!internalPath.startsWith("/")) internalPath = "/" + internalPath;
		if(!internalPath.equalsIgnoreCase(requestInfo.getRequestPath())) return false;
		
		if (!isAllowedMethod(requestInfo.getRequestHttpMethod())) return false;
		
		return true;
	}
	
	public RouteInfo getRouteInfo(RequestInfo requestInfo) {
		RouteInfo ri = new RouteInfo(requestInfo);
		ri.controller = this.controller;
		ri.action = this.action;
		ri.id = this.id;
		ri.format = requestInfo.getFormat();
		ri.controllerClassName = getControllerClassName(ri.controller);
        ri.model = getModel(ri.controller);
        ri.modelClassName = getModelClassName(ri.controller);
		ri.routeType = getRouteType();
		ri.routeName = getName();
        ri.viewPath = getViewPath(ri.controller);
		return ri;
	}
    
    protected void validation() {
		if (dynamicController || (controller == null && controllerClass == null)) {
			throw new IllegalArgumentException("Either controller or controllerClass should be defined in route named " + name + ".");
		}
		
		if (dynamicAction || action == null) {
			throw new IllegalArgumentException("action cannot be empty or undefined in route named " + name + ".");
		}
		
		if (getRequiredFieldPositions().size() != 0) {
			throw new IllegalArgumentException("Dynamic keys are not allowed in route named " + 
					name + ", because its type is " + getRouteType() + ".");
		}
    }
}
