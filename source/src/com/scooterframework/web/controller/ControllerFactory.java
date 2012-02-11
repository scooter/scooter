/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.web.controller;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.scooterframework.admin.ApplicationConfig;
import com.scooterframework.admin.AutoLoadedObjectFactory;
import com.scooterframework.admin.EnvConfig;
import com.scooterframework.common.exception.MethodCreationException;
import com.scooterframework.common.logging.LogUtil;
import com.scooterframework.common.util.BeanUtil;
import com.scooterframework.web.route.RouteConfig;

/**
 * ControllerFactory manages instances of controller classes.
 * 
 * @author (Fei) John Chen
 */
public class ControllerFactory {
    
    /**
     * Creates a controller instance of the type specified by <tt>controllerClassName</tt>.
     * 
     * If it fails to create a controller of type <tt>controllerClassName</tt>, 
     * this method will try to create a controller of type <tt>defaultControllerClass</tt>
     * when either <tt>auto.crud</tt> or <tt>auto.rest</tt> is set to true in 
     * properties files. 
     * 
     * @param controllerClassName    controller class name
     * @param defaultControllerClass default controller class name
     * @return controller instance object
     */
    public static Object createController(String controllerClassName, String defaultControllerClass) {
        Object controller = controllerMap.get(controllerClassName);
        
        if (controller == null || ApplicationConfig.getInstance().isInDevelopmentEnvironment()) {
            try {
                controller = AutoLoadedObjectFactory.getInstance().newInstance(controllerClassName);
            } catch (Exception ex) {
            	log.debug("Error in createController(): " + ex.getMessage());
                if (EnvConfig.getInstance().allowAutoCRUD() || 
                    RouteConfig.getInstance().allowAutoREST()) {
                    log.debug("No controller class of \"" + controllerClassName + 
                    		"\" created. Use default controller class \"" + defaultControllerClass + "\".");
                    controller = AutoLoadedObjectFactory.getInstance().newInstance(defaultControllerClass);
                }
                else {
                    return null;
                }
            }
            controllerMap.put(controllerClassName, controller);
        }
        
        return controller;
    }

    /**
     * Returns method of an object.
     * 
     * @param clz the class type
     * @param methodName the method name of the object
     * @return the method object
     * @exception MethodCreationException if <tt>bean</tt> or
     *  <tt>method</tt> is null
     */
    public static Method getMethod(Class<?> clz, String methodName) {
        if (clz == null) {
            throw new IllegalArgumentException("No bean class specified.");
        }
        if (methodName == null) {
            throw new IllegalArgumentException("No method name specified.");
        }
        
        String methodKey = clz.getName() + "." + methodName.toLowerCase();
        Method method = allMethodsMap.get(methodKey);
        
        if (method == null || ApplicationConfig.getInstance().isInDevelopmentEnvironment()) {
        	method = BeanUtil.getMethod(clz, methodName);
    		
    		if (method == null) {
        		throw new MethodCreationException(clz.getName(), methodName);
        	}
    		
    		allMethodsMap.put(methodKey, method);
        }
        
        return method;
    }
    
    private static Map<String, Object> controllerMap = new HashMap<String, Object>();
	private static Map<String, Method> allMethodsMap = new HashMap<String, Method>();

    private static LogUtil log = LogUtil.getLogger(ControllerFactory.class.getName());
}
