/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.sqldataexpress.exception;

/**
 * class FailureDetectingParameterPropertyException
 * 
 * @author (Fei) John Chen
 */
public class FailureDetectingParameterPropertyException extends BaseSQLException
{
    /**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = -8294453903086094392L;

	/**
     * Constructs a new FailureDetectingParameterPropertyException exception with null as its detail 
     * message. The cause is not initialized, and may subsequently be 
     * initialized by a call to Throwable.initCause(java.lang.Throwable). 
     */
    public FailureDetectingParameterPropertyException() {
        super();
    }
    
    /**
     * Constructs a new FailureDetectingParameterPropertyException exception with the specified detail 
     * message. The cause is not initialized, and may subsequently be 
     * initialized by a call to Throwable.initCause(java.lang.Throwable). 
     * 
     * @param message the detail message.
     */
    public FailureDetectingParameterPropertyException(String message) {
        super(message);
    }
}
