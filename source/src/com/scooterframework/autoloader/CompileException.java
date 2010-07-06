/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.autoloader;

import com.scooterframework.common.exception.GenericException;

/**
 * class CompileException
 * 
 * @author (Fei) John Chen
 */
public class CompileException extends GenericException 
{
    /**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = -2413761995973889098L;

	public CompileException() {
        super();
    }
    
    public CompileException(String message) {
        super(message);
    }
}
