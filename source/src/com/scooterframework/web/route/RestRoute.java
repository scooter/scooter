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
 * RestRoute class
 *
 * @author (Fei) John Chen
 */
public class RestRoute extends RegularRoute {

    /**
     * Name of the resource
     */
    private String resourceName;

    /**
     * model name
     */
    private String model;

	protected RestRoute() {
		;
	}

	public RestRoute(String name, Properties p) {
		super(name, p);
	}

    public RouteInfo getRouteInfo(RequestInfo requestInfo) {
        RouteInfo ri = super.getRouteInfo(requestInfo);
        ri.resourceName = getResourceName();
        return ri;
    }

	public String getRouteType() {
		return RouteConstants.ROUTE_TYPE_REST;
	}

	protected boolean isAllowedFormat(String fmat) {
		boolean allowed = false;
        if (allowed_formats == null) {
            allowed = true;
        }
        else {
            allowed = super.isAllowedFormat(fmat);
        }

		return allowed;
	}

	public String getModel() {
        return model;
	}

	protected void setModel(String model) {
        this.model = model;
	}

    /**
     * Returns the name of the resource which generates the route.
     *
     * @return resource name
     */
    public String getResourceName() {
        return resourceName;
    }

    protected void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

	/**
     * Returns a string representation of the object.
     * @return String
     */
    public String toString() {
        StringBuilder returnString = new StringBuilder();
        String SEPARATOR = ", ";

        returnString.append(super.toString()).append(SEPARATOR);
        returnString.append("model = " + getModel()).append(SEPARATOR);
        returnString.append("resourceName = " + resourceName);

        return returnString.toString();
    }
}
