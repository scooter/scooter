/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.common.exception;

import java.lang.reflect.InvocationTargetException;

/**
 * class MethodCreationException
 * 
 * @author (Fei) John Chen
 */
public class MethodCreationException extends GenericException {
    /**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = 9154180006186945746L;
	
	public MethodCreationException(String className, String methodName) {
        this(className, methodName, null);
    }
    
    public MethodCreationException(String className, String methodName, Throwable cause) {
        super(cause);
        this.className = className;
        this.methodName = methodName;
    }
    
    public String getMessage() {
        String parentMessage = null;
        if (super.getCause() instanceof InvocationTargetException) {
            parentMessage = super.getCause().getCause().getMessage();
        }
        else {
            parentMessage = super.getMessage();
        }
            
        StringBuffer result = new StringBuffer();
        result.append("Failed to create method \"").append(methodName).append("\" of class \"").append(className).append("\".");
        if (parentMessage != null) result.append("\r\nDetails: ").append(parentMessage).append(".");
        return result.toString();
    }
    
    public String getClassName() {
        return className;
    }
    
    public String getMethodName() {
        return methodName;
    }
    
    protected String className;
    protected String methodName;
}
