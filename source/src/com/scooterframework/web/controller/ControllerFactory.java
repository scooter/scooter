/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.web.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.scooterframework.admin.ApplicationConfig;
import com.scooterframework.admin.AutoLoadedObjectFactory;
import com.scooterframework.admin.EnvConfig;
import com.scooterframework.common.logging.LogUtil;
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
        Object controller = null;
        
        if ((ApplicationConfig.getInstance().isInDevelopmentEnvironment()) || 
            controllerMap.get(controllerClassName) == null) {
            try {
                controller = AutoLoadedObjectFactory.getInstance().newInstance(controllerClassName);
            } catch (Exception ex) {
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
        else {
            controller = controllerMap.get(controllerClassName);
        }
        return controller;
    }
    
    private static Map<String, Object> controllerMap = Collections.synchronizedMap(new HashMap<String, Object>());

    private static LogUtil log = LogUtil.getLogger(ControllerFactory.class.getName());
}
