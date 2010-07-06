/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.web.route;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * MatchMaker class
 * 
 * @author (Fei) John Chen
 *
 */
public class MatchMaker {
	
	private static MatchMaker me;

	private static RootRoute rootRoute;
	private static List defaultRoutes = new ArrayList();
	private static List namedRoutes = new ArrayList();
	private static List regularRoutes = new ArrayList();
	private static List restRoutes = new ArrayList();
	
	private Map requestRouteMap = new HashMap();
    private Map resourceMap = new HashMap();
    
    private static List allRoutes = new ArrayList();
	
	private MatchMaker() {
	}
	
	public static synchronized MatchMaker getInstance() {
        if (me == null) {
            me = new MatchMaker();
        }
        return me;
    }
    
    public List getAllRoutes() {
        return allRoutes;
    }
    
    public int countRoutes() {
        return allRoutes.size();
    }
    
    public Route getRoute(String routeName) {
        return getRouteFromList(routeName, allRoutes);
    }
    
    public RootRoute getRootRoute() {
        return rootRoute;
    }
    
    public RootRoute getRootRoute(String routeName) {
        RootRoute r = null;
        if (rootRoute != null) {
            if (rootRoute.getName().equals(routeName)) {
                r = rootRoute;
            }
        }
        return r;
    }
	
	public void setRootRoute(RootRoute route) {
		rootRoute = route;
        allRoutes.add(route);
	}
    
    public DefaultRoute getDefaultRoute(String routeName) {
        return (DefaultRoute)getRouteFromList(routeName, defaultRoutes);
    }
	
	public void addDefaultRoute(DefaultRoute route) {
		defaultRoutes.add(route);
        allRoutes.add(route);
	}
	
	public void addDefaultRoutes(List routes) {
		defaultRoutes.addAll(routes);
        allRoutes.addAll(routes);
	}
    
    public NamedRoute getNamedRoute(String routeName) {
        return  (NamedRoute)getRouteFromList(routeName, namedRoutes);
    }
	
	public void addNamedRoute(NamedRoute route) {
		namedRoutes.add(route);
        allRoutes.add(route);
	}
	
	public void addNamedRoutes(List routes) {
		namedRoutes.addAll(routes);
        allRoutes.addAll(routes);
	}
    
    public RegularRoute getRegularRoute(String routeName) {
        return (RegularRoute)getRouteFromList(routeName, regularRoutes);
    }
	
	public void addRegularRoute(RegularRoute route) {
		regularRoutes.add(route);
        allRoutes.add(route);
	}
	
	public void addRegularRoutes(List routes) {
		regularRoutes.addAll(routes);
        allRoutes.addAll(routes);
	}
    
    public RestRoute getRestRoute(String routeName) {
        return (RestRoute)getRouteFromList(routeName, restRoutes);
    }
	
	public void addRestRoute(RestRoute route) {
		restRoutes.add(route);
        allRoutes.add(route);
	}
	
	public void addRestRoutes(List routes) {
		restRoutes.addAll(routes);
        allRoutes.addAll(routes);
	}
    
    public Map getResourceMap() {
        return resourceMap;
    }
    
    /**
     * Checks if a resource name is already added as a resource.
     * @param resourceName
     * @return true if the <tt>resourceName</tt> is not a resource already added.
     */
    public boolean isAddedResource(String resourceName) {
        return (getResource(resourceName) != null)?true:false;
    }
    
    /**
     * Returns resource corresponding to a resource name.
     * 
     * @param resourceName     resource name
     * @return resource
     */
    public Resource getResource(String resourceName) {
        return (Resource)resourceMap.get(resourceName);
    }
    
    /**
     * Returns resource corresponding to a model name.
     * 
     * @param model     model name
     * @return resource
     */
    public Resource getResourceForModel(String model) {
        Resource resource = null;
        Iterator it = resourceMap.keySet().iterator();
        while(it.hasNext()) {
            Resource res = (Resource)resourceMap.get(it.next());
            if (model.equals(res.getModel())) {
                resource = res;
                break;
            }
        }
        return resource;
    }
    
    public void addResource(String resourceName, Resource resource) {
        resourceMap.put(resourceName, resource);
    }
	
	public void clear() {
		rootRoute = null;
		
		defaultRoutes.clear();
		namedRoutes.clear();
		regularRoutes.clear();
		restRoutes.clear();
        allRoutes.clear();
		
		requestRouteMap.clear();
        resourceMap.clear();
	}
	
	public RouteInfo match(RequestInfo requestInfo) {
        if ("/".equals(requestInfo.getRequestPath())) return null;
        
		String requestKey = requestInfo.getRequestKey();
		RouteInfo routeInfo = (RouteInfo)requestRouteMap.get(requestKey);
		if (routeInfo != null) {
			return routeInfo;
		}
		
		Route route = null;
        int index = -1;
        int size = allRoutes.size();
        for (int i = 0; i < size; i++) {
            index = i;
            Route r = (Route)allRoutes.get(i);
			if (r.isRouteFor(requestInfo)) {
				route = r;
				break;
			}
        }
        
        boolean autoRestified = RouteConfig.getInstance().allowAutoREST();
        if (route == null) {
            if (autoRestified) {
                String name = requestInfo.getAutoResourceName();
                
                if (!isAddedResource(name)) {
                    Resource resource = new Resource(name, Resource.PLURAL, new Properties(), false, true);
                    
                    //make sure the default routes are the last
                    allRoutes.removeAll(defaultRoutes);
                    addRestRoutes(resource.getRoutes());
                    allRoutes.addAll(defaultRoutes);
                    addResource(name, resource);
                    return match(requestInfo);
                }
            }
        }
        
        if (route == null) {
        	throw new NoRouteFoundException(requestKey);
        }
		
		routeInfo = route.getRouteInfo(requestInfo);
        routeInfo.setIndex(index);
		
		requestRouteMap.put(requestKey, routeInfo);
		
		return routeInfo;
	}
    
    private Route getRouteFromList(String routeName, List routes) {
        if (routeName == null || routes == null || routes.size() == 0) return null;
        
        Route route = null;
        Iterator it = routes.iterator();
        while(it.hasNext()) {
            Route r = (Route)it.next();
            if (routeName.equals(r.getName())) {
                route = r;
                break;
            }
        }
        return route;
    }
}
