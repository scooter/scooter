/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.sqldataexpress.exception;

/**
 * class UnsupportedStoredProcedureModeException
 * 
 * @author (Fei) John Chen
 */
public class UnsupportedStoredProcedureModeException extends BaseSQLException
{
    /**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = -4879757101827317651L;

	/**
     * Constructs a new UnsupportedStoredProcedureModeException exception with null as its detail 
     * message. The cause is not initialized, and may subsequently be 
     * initialized by a call to Throwable.initCause(java.lang.Throwable). 
     */
    public UnsupportedStoredProcedureModeException() {
        super();
    }
    
    /**
     * Constructs a new UnsupportedStoredProcedureModeException exception with the specified detail 
     * message. The cause is not initialized, and may subsequently be 
     * initialized by a call to Throwable.initCause(java.lang.Throwable). 
     * 
     * @param message the detail message.
     */
    public UnsupportedStoredProcedureModeException(String message) {
        super(message);
    }
}
