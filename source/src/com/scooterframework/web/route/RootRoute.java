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
 * RootRoute class
 * 
 * @author (Fei) John Chen
 */
public class RootRoute extends NamedRoute {
	
	public RootRoute(String name, Properties p) {
		super(name, p);
	}
	
	public RootRoute(String name, NamedRoute route) {
        if (route == null) 
            throw new IllegalArgumentException("The input named route cannot be null for the root route.");
        
        if (name == null) name = "root";
        
		if (!name.equals(route.getName())) 
			throw new IllegalArgumentException("The assigned route's name does not match the root route's name.");
		
		copy(route);
		
		this.name = name;
	}
	
	public String getRouteType() {
		return RouteConstants.ROUTE_TYPE_ROOT;
	}
	
	public boolean isRouteFor(RequestInfo requestInfo) {
		return "".equals(requestInfo.getRequestPath()) || "/".equals(requestInfo.getRequestPath());
	}
}
