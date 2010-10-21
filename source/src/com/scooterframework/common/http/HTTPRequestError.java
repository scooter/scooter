/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.common.http;

/**
 * HTTPRequestError class represents http request error.
 * 
 * @author (Fei) John Chen
 * 
 */
public class HTTPRequestError extends RuntimeException {
	/**
	 * Generated UID
	 */
	private static final long serialVersionUID = -517961715603804667L;

	public HTTPRequestError() {
		super();
	}

	public HTTPRequestError(String s) {
		super(s);
	}

	public HTTPRequestError(Throwable cause) {
		initCause(cause);
	}

	public HTTPRequestError(String message, Throwable cause) {
		super(message);
		initCause(cause);
	}
}
