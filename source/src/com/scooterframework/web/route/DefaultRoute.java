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
 * DefaultRoute class
 * 
 * @author (Fei) John Chen
 *
 */
public class DefaultRoute extends RegularRoute {
	
	public DefaultRoute(String name, Properties p) {
		super(name, p);
	}
	
	public String getRouteType() {
		return RouteConstants.ROUTE_TYPE_DEFAULT;
	}
}
