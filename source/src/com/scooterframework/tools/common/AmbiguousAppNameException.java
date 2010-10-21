/*
 *   This software is distributed under the terms of the FSF
 *   Gnu Lesser General Public License (see lgpl.txt).
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.tools.common;

/**
 * AmbiguousAppNameException class.
 * 
 * @author (Fei) John Chen
 *
 */
public class AmbiguousAppNameException extends Exception {
	
	/**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = -6810347022280628767L;

	/**
	 * Constructs a new AmbiguousAppNameException exception with the 
	 * specified detail message.
	 * 
	 * @param message the detail message.
	 */
	public AmbiguousAppNameException(String message) {
        super(message);
    }
}
