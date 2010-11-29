/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.web.controller;

import com.scooterframework.common.exception.GenericException;

/**
 * class NoTemplateHandlerException
 * 
 * @author (Fei) John Chen
 */
public class NoTemplateHandlerException extends GenericException {
	private String templateType;

	/**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = 0L;

	public NoTemplateHandlerException(String message, String templateType) {
		super(message);
		this.templateType = templateType;
	}
	
	public String getTemplateType() {
		return templateType;
	}
}
