/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.web.controller;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.scooterframework.admin.EnvConfig;
import com.scooterframework.common.util.CurrentThreadCache;
import com.scooterframework.common.util.CurrentThreadCacheClient;
import com.scooterframework.web.route.MatchMaker;
import com.scooterframework.web.route.RequestInfo;
import com.scooterframework.web.route.RouteConfig;
import com.scooterframework.web.route.RouteConstants;
import com.scooterframework.web.route.RouteInfo;

/**
 * <p><strong>RestfulRequestProcessor</strong> contains the processing logic that
 * the {@link MainActionServlet} performs as it receives each servlet request
 * from the container. You can customize the request processing behavior by
 * subclassing this class and overriding the method(s) whose behavior you are
 * interested in changing.</p>
 * 
 * @author (Fei) John Chen
 */
public class RestfulRequestProcessor extends BaseRequestProcessor {
    
    public static final String DEFAULT_CONTROLLER_CLASS = "com.scooterframework.builtin.RestfulCRUDController";
    
    private static final String CACHE_KEY_ROUTE_TYPE = "cache.key.route_type";
    
    /**
     * Constructor
     */
    public RestfulRequestProcessor() {
    }

    /**
     * <p>Process an <tt>HttpServletRequest</tt>.</p>
     * 
     * @param aps properties of request
     * @param request The servlet request we are processing
     * @param response The servlet response we are creating
     * @return execution result
     * @throws java.io.IOException
     * @throws javax.servlet.ServletException
     */
    public String executeRequest(ActionProperties aps, 
                HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
    {
        CurrentThreadCache.set(CACHE_KEY_ROUTE_TYPE, aps.routeType);
    	return super.executeRequest(aps, request, response);
    }
    
    /**
     * Sets up action properties for the action execution. The properties are 
     * wrapped up in an <tt>ActionProperties</tt> instance.
     * 
     * @param request The servlet request we are processing
     * @return an ActionProperties instance
     */
	public ActionProperties prepareActionProperties(String requestPath,
			String requestHttpMethod, HttpServletRequest request) {
    	ActionProperties aps = super.prepareActionProperties(requestPath, requestHttpMethod, request);
    	
        RequestInfo requestInfo = new RequestInfo(requestPath, requestHttpMethod);
        log.debug("  requestInfo: " + requestInfo);
        
        RouteInfo routeInfo = MatchMaker.getInstance().match(requestInfo);
        log.debug("matched route: " + routeInfo);
        
        //setup field values
        Map<String, String> requiredFieldValues = routeInfo.getRequiredFieldValues();
        if (requiredFieldValues != null) {
        	CurrentThreadCacheClient.cacheFieldValues(requiredFieldValues);
            
            for(Map.Entry<String, String> entry : requiredFieldValues.entrySet()) {
                request.setAttribute(entry.getKey(), entry.getValue());
            }
        }
        
        aps.controller = routeInfo.getController();
        aps.controllerClassName = routeInfo.getControllerClassName();
        aps.action = routeInfo.getAction();
        aps.model = routeInfo.getModel();
        aps.format = routeInfo.getFormat();
        aps.resource = routeInfo.getResourceName();
        aps.routeType = routeInfo.getRouteType();
        
        return aps;
    }
    
    /**
     * Puts some action properties in <tt>request</tt> object.
     */
    protected void registerActionProperties(HttpServletRequest request, ActionProperties aps) {
        super.registerActionProperties(request, aps);
        CurrentThreadCacheClient.cacheResource(aps.resource);
    }
    
    /**
     * Returns class name of default controller. This default controller is used 
     * when application specific controller is not available and the 
     * <tt>auto.rest</tt> property is set to true in the routes.properties 
     * file. Subclass must override this method if a different default 
     * controller class is used. 
     */
    protected String getDefaultControllerClassName() {
    	if (!RouteConstants.ROUTE_TYPE_REST.equals(CurrentThreadCache.get(CACHE_KEY_ROUTE_TYPE))) {
    		return super.getDefaultControllerClassName();
    	}
        return DEFAULT_CONTROLLER_CLASS;
    }
    
    /**
     * Returns default view file directory name. 
     * 
     * @return default view file directory name. 
     */
    protected String getDefaultViewFilesDirectoryName() {
    	if (!RouteConstants.ROUTE_TYPE_REST.equals(CurrentThreadCache.get(CACHE_KEY_ROUTE_TYPE))) {
    		return super.getDefaultViewFilesDirectoryName();
    	}
        
    	String dirName = null;
        if (RouteConfig.getInstance().allowAutoREST()) {
            dirName = EnvConfig.getInstance().getDefaultViewFilesDirectoryForREST();
        }
        return dirName;
    }
}
