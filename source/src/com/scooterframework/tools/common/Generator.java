/*
 *   This software is distributed under the terms of the FSF
 *   Gnu Lesser General Public License (see lgpl.txt).
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.tools.common;

/**
 * Generator interface defines all methods to be implemented by a generator 
 * class.
 * 
 * @author (Fei) John Chen
 *
 */
public interface Generator {
	/**
	 * Generates code. If the code is already generated, overwrite it.
	 */
	public void generate();
	
	/**
	 * Generates code with an option if to overwrite the existing code.
	 * 
	 * @param overwrite
	 */
	public void generate(boolean overwrite);
}
