/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.common.exception;

/**
 * class PropertyFileLoadingException
 * 
 * @author (Fei) John Chen
 */
public class PropertyFileLoadingException extends GenericException 
{
    /**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = 447534826448375262L;

	public PropertyFileLoadingException() {
        super();
    }
    
    public PropertyFileLoadingException(String message) {
        super(message);
    }
}
