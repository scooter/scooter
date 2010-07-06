/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.common.exception;

/**
 * class GenericException
 * 
 * @author (Fei) John Chen
 */
public class GenericException extends RuntimeException
{

	/**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = -7668457753876791759L;

	/**
     * Constructs a new GenericException exception with null as its detail 
     * message. The cause is not initialized, and may subsequently be 
     * initialized by a call to Throwable.initCause(java.lang.Throwable). 
     */
    public GenericException() {
        super();
    }
    
    /**
     * Constructs a new GenericException exception with the specified 
     * detail message and cause. 
     * 
     * @param message the detail message.
     * @param cause the cause. (A null value is permitted, and indicates 
     *                          that the cause is nonexistent or unknown.)
     */
    public GenericException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new GenericException exception with the specified detail 
     * message. The cause is not initialized, and may subsequently be 
     * initialized by a call to Throwable.initCause(java.lang.Throwable). 
     * 
     * @param message the detail message.
     */
    public GenericException(String message) {
        super(message);
    }

    /**
     * Constructs a new GenericException exception with the specified cause. 
     * This constructor is useful for runtime exceptions that are little more 
     * than wrappers for other throwables. 
     * 
     * @param cause the wrapped exception.
     */
    public GenericException (Throwable cause) {
        super(cause);
    }
}
