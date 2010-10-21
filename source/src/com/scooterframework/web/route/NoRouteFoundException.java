/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.web.route;

/**
 * NoRouteFoundException class
 * 
 * @author (Fei) John Chen
 *
 */
public class NoRouteFoundException extends RuntimeException {
    /**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = 1448451864217379703L;
	
	private String requestKey;
    
    public NoRouteFoundException(String requestKey) {
    	this(requestKey, "No route is found for \"" + requestKey + "\"");
    }
    
    public NoRouteFoundException(String requestKey, String message) {
        super(message);
        this.requestKey = requestKey;
    }
    
    public String getRequestKey() {
    	return requestKey;
    }
}
