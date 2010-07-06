/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.sqldataexpress.exception;

/**
 * class UnsupportedVendorException
 * 
 * @author (Fei) John Chen
 */
public class UnsupportedVendorException extends BaseSQLException
{
    /**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = -6822929113198030865L;

	/**
     * Constructs a new UnsupportedVendorException exception with null as its detail 
     * message. The cause is not initialized, and may subsequently be 
     * initialized by a call to Throwable.initCause(java.lang.Throwable). 
     */
    public UnsupportedVendorException() {
        super();
    }
    
    /**
     * Constructs a new UnsupportedVendorException exception with the specified detail 
     * message. The cause is not initialized, and may subsequently be 
     * initialized by a call to Throwable.initCause(java.lang.Throwable). 
     * 
     * @param message the detail message.
     */
    public UnsupportedVendorException(String message) {
        super(message);
    }
}
