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
 * RegularRoute class
 * 
 * @author (Fei) John Chen
 */
public class RegularRoute extends Route {
	
	protected RegularRoute() {
		;
	}
	
	public RegularRoute(String name, Properties p) {
		super(name, p);
	}
	
	public String getRouteType() {
		return RouteConstants.ROUTE_TYPE_RGULAR;
	}
	
	public boolean isRouteFor(RequestInfo requestInfo) {
		if (segmentCount() != requestInfo.segmentCount()) return false;
		
		if (!isAllowedFormat(requestInfo.getFormat())) return false;
		
		if (!isAllowedMethod(requestInfo.getRequestHttpMethod())) return false;
		
		String[] pathSegments = getPathSegments();
		String[] riPathSegments = requestInfo.getPathSegments();
		for (int i = 0; i < segmentCount(); i++) {
			String segment = pathSegments[i];
			if (!segment.startsWith("$") && !segment.equalsIgnoreCase(riPathSegments[i])) return false;
		}
        
        if (!isAllowedFieldValue(requestInfo)) return false;
        
		return true;
	}
}
