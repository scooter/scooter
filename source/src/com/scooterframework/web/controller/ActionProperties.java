/*
 *   This software is distributed under the terms of the FSF
 *   Gnu Lesser General Public License (see lgpl.txt).
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.web.controller;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * ActionProperties contains infomation for action execution.
 *
 * @author (Fei) John Chen
 */
public class ActionProperties {
    public String controllerPath;
    public String controller;
    public String controllerClassName;
    public String action;
    public String model;
    public String format;
    public String resource;
    
    public boolean controllerCreated;
    public Object controllerInstance;
    public boolean methodCreated;
    public Method methodInstance;
    
    public String routeType;
    
    public Map<String, String> requiredFieldValues;

	/**
     * Returns a string representation of the object.
     * @return String
     */
    public String toString() {
        StringBuilder returnString = new StringBuilder();
        String SEPARATOR = ", ";

        returnString.append("controllerPath = " + controllerPath).append(SEPARATOR);
        returnString.append("controller = " + controller).append(SEPARATOR);
        returnString.append("controllerClassName = " + controllerClassName).append(SEPARATOR);
        returnString.append("action = " + action).append(SEPARATOR);
        returnString.append("model = " + model).append(SEPARATOR);
        returnString.append("format = " + format).append(SEPARATOR);
        returnString.append("resource = " + resource).append(SEPARATOR);
        returnString.append("routeType = " + routeType).append(SEPARATOR);
        returnString.append("controllerCreated = " + controllerCreated).append(SEPARATOR);
        returnString.append("methodCreated = " + methodCreated).append(SEPARATOR);
        returnString.append("requiredFieldValues = " + requiredFieldValues);

        return returnString.toString();
    }
}
