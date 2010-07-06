/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.sqldataexpress.exception;

/**
 * class UnsupportedDataProcessorNameException
 * 
 * @author (Fei) John Chen
 */
public class UnsupportedDataProcessorNameException extends BaseSQLException
{
    /**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = 1570431998157801247L;

	/**
     * Constructs a new UnsupportedDataProcessorNameException exception with null as its detail 
     * message. The cause is not initialized, and may subsequently be 
     * initialized by a call to Throwable.initCause(java.lang.Throwable). 
     */
    public UnsupportedDataProcessorNameException() {
        super();
    }
    
    /**
     * Constructs a new UnsupportedDataProcessorNameException exception with the specified detail 
     * message. The cause is not initialized, and may subsequently be 
     * initialized by a call to Throwable.initCause(java.lang.Throwable). 
     * 
     * @param message the detail message.
     */
    public UnsupportedDataProcessorNameException(String message) {
        super(message);
    }
}
