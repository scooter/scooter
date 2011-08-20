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
	public static final String TEMPLATE_PARSER_TYPE = "template_parser_type";
	public static final String TEMPLATE_PARSER_Q = "Q";
	public static final String TEMPLATE_PARSER_ST = "ST";
	
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
