/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.web.controller;

import com.scooterframework.common.exception.ObjectCreationException;

/**
 * class NoControllerFoundException
 * 
 * @author (Fei) John Chen
 */
public class NoControllerFoundException extends ObjectCreationException {
    /**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = -410111088784655716L;

	/**
     * Constructs a new NoControllerFoundException exception with the specified 
     * class name.
     * 
     * @param className the class name of the object to bve created.
     */
    public NoControllerFoundException(String className) {
        super(className);
    }
    
    /**
     * Returns the detail message string of this instance.
     *
     * @return  the detail message string of this instance.
     */
    public String getMessage() {
        StringBuffer result = new StringBuffer();
        result.append("Failed to instantiate contoller class \"").append(className).append("\".");
        return result.toString();
    }
}
