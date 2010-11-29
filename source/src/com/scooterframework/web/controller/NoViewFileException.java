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
 * class ViewFileNotFoundException
 * 
 * @author (Fei) John Chen
 */
public class NoViewFileException extends GenericException {
	private String targetView;

	/**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = 802418755823460528L;

	public NoViewFileException(String message, String targetView) {
		super(message);
		this.targetView = targetView;
	}
	
	public String getTargetView() {
		return targetView;
	}
}
