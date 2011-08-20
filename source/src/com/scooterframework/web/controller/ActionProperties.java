/*
 *   This software is distributed under the terms of the FSF
 *   Gnu Lesser General Public License (see lgpl.txt).
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.web.controller;

/**
 * ActionProperties contains infomation for action execution.
 *
 * @author (Fei) John Chen
 */
public class ActionProperties {
    public String controllerPath;
    public String controller;
    public String controllerClassName;
    public String resource;
    public String action;
    public String model;
    public String format;

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
        returnString.append("resource = " + resource).append(SEPARATOR);
        returnString.append("action = " + action).append(SEPARATOR);
        returnString.append("model = " + model).append(SEPARATOR);
        returnString.append("format = " + format);

        return returnString.toString();
    }
}
