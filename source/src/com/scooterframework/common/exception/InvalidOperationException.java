/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.common.exception;

/**
 * class InvalidOperationException
 * 
 * @author (Fei) John Chen
 */
public class InvalidOperationException extends GenericException {
    /**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = -7997261655111252545L;

	public InvalidOperationException() {
        super();
    }
    
    public InvalidOperationException(String message) {
        super(message);
    }
    
    public InvalidOperationException(Object object, String operation, String reason) {
        super(operation + " operation on object " + object.getClass().getName() + " is invalid: " + reason + ".");
    }
}
