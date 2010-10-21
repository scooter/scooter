/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.sqldataexpress.exception;

/**
 * class LookupFailureException
 * 
 * @author (Fei) John Chen
 */
public class LookupFailureException extends BaseSQLException
{
    /**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = 2895914602432640911L;

	/**
     * Constructs a new LookupFailureException exception with null as its detail 
     * message. The cause is not initialized, and may subsequently be 
     * initialized by a call to Throwable.initCause(java.lang.Throwable). 
     */
    public LookupFailureException() {
        super();
    }
    
    /**
     * Constructs a new LookupFailureException exception with the specified detail 
     * message. The cause is not initialized, and may subsequently be 
     * initialized by a call to Throwable.initCause(java.lang.Throwable). 
     * 
     * @param message the detail message.
     */
    public LookupFailureException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new LookupFailureException exception with the specified 
     * cause. 
     * 
     * This constructor is useful for runtime exceptions that are little more 
     * than wrappers for other throwables. 
     * 
     * @param cause the wrapped exception.
     */
    public LookupFailureException(Throwable cause) {
        super(cause);
    }
    
    /**
     * Constructs a new LookupFailureException exception with the specified 
     * detail message and cause. 
     * 
     * @param message the detail message.
     * @param cause the cause. (A null value is permitted, and indicates 
     *                          that the cause is nonexistent or unknown.)
     */
    public LookupFailureException(String message, Throwable cause) {
        super(message, cause);
    }
}
