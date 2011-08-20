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
 * class ObjectCreationException
 *
 * @author (Fei) John Chen
 */
public class ObjectCreationException extends GenericException {

    /**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = -5756502673652111889L;

	/**
     * Constructs a new ObjectCreationException exception with the specified
     * class name.
     *
     * @param className the class name of the object to bve created.
     */
    public ObjectCreationException(String className) {
        this.className = className;
    }

    /**
     * Constructs a new ObjectCreationException exception with the specified
     * class name and cause.
     *
     * @param className the class name of the object to bve created.
     * @param cause the cause. (A null value is permitted, and indicates
     *                          that the cause is nonexistent or unknown.)
     */
    public ObjectCreationException(String className, Throwable cause) {
        super(cause);
        this.className = className;
    }

    /**
     * Returns the detail message string of this instance.
     *
     * @return  the detail message string of this instance.
     */
    public String getMessage() {
        String parentMessage = null;
        if (super.getCause() != null &&
            super.getCause() instanceof InvocationTargetException) {
            parentMessage = super.getCause().getCause().getMessage();
        }
        else {
            parentMessage = super.getMessage();
        }

        StringBuilder result = new StringBuilder();
        result.append("Failed to instantiate class \"").append(className).append("\".");
        if (parentMessage != null) result.append(" Details: ").append(parentMessage).append(".");
        return result.toString();
    }

    /**
     * The type of class for the object to be created.
     */
    protected String className;
}
