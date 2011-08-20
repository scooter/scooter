/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.sqldataexpress.exception;

/**
 * class UnsupportedDataProcessorTypeException
 * 
 * @author (Fei) John Chen
 */
public class UnsupportedDataProcessorTypeException extends BaseSQLException
{
    /**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = 3993584058604706105L;

	/**
     * Constructs a new UnsupportedDataProcessorTypeException exception with null as its detail 
     * message. The cause is not initialized, and may subsequently be 
     * initialized by a call to Throwable.initCause(java.lang.Throwable). 
     */
    public UnsupportedDataProcessorTypeException() {
        super();
    }
    
    /**
     * Constructs a new UnsupportedDataProcessorTypeException exception with the specified detail 
     * message. The cause is not initialized, and may subsequently be 
     * initialized by a call to Throwable.initCause(java.lang.Throwable). 
     * 
     * @param message the detail message.
     */
    public UnsupportedDataProcessorTypeException(String message) {
        super(message);
    }
}
