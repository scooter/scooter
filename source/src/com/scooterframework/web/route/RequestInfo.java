/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.web.route;

import com.scooterframework.admin.EnvConfig;
import com.scooterframework.common.util.Util;

/**
 * RequestInfo class
 * 
 * @author (Fei) John Chen
 *
 */
public class RequestInfo {
	private String requestPath;
	private String requestHttpMethod;
	private String requestKey;
	private String format;
	
	private String[] pathSegments;
	private int segmentCount;
	
	public RequestInfo(String requestPath, String requestHttpMethod) {
		if (requestPath == null) 
			throw new IllegalArgumentException("requestPath cannot be empty.");
		
		if (requestHttpMethod == null) 
			throw new IllegalArgumentException("requestHttpMethod cannot be empty.");
		
		this.requestPath = requestPath;
		this.requestHttpMethod = requestHttpMethod;
		
		this.requestKey = generateRequestKey(requestPath, requestHttpMethod);
		
		parsePath(requestPath);
	}
	
	public String getRequestPath() {
		return requestPath;
	}
	
	public String getRequestHttpMethod() {
		return requestHttpMethod;
	}
	
	public String getRequestKey() {
		return requestKey;
	}
	
	public String getFormat() {
		return format;
	}
	
	public boolean hasFormat() {
		return (format != null)?true:false;
	}
	
	public String[] getPathSegments() {
		return Util.cloneArray(pathSegments);
	}
	
	public int segmentCount() {
		return segmentCount;
	}
    
    public String getAutoResourceName() {
        String name = "";
        if (segmentCount > 0) {
            name = pathSegments[0];
        }
        return name;
    }
    
    public static String generateRequestKey(String requestPath, String requestHttpMethod) {
    	return requestHttpMethod + RouteConstants.HTTP_METHOD_PATH_GLUE + requestPath;
    }
	
	/**
     * Returns a string representation of the object.
     * @return String
     */
    public String toString() {
        StringBuilder returnString = new StringBuilder();
        String SEPARATOR = ", ";
        
        returnString.append("requestPath = " + requestPath).append(SEPARATOR);
        returnString.append("requestHttpMethod = " + requestHttpMethod).append(SEPARATOR);
        returnString.append("format = " + format).append(SEPARATOR);
        returnString.append("segmentCount = " + segmentCount);
        
        return returnString.toString();
    }
	
	private void parsePath(String path) {
		if ("".equals(path) || "/".equals(path)) {
			segmentCount = 0;
		} else {
			String s = path;

			int lastDot = path.lastIndexOf('.');
			int lastSlash = path.lastIndexOf('/');
			if (lastDot > lastSlash) {
				int lastPKSeparator = path.lastIndexOf(RouteConstants.PRIMARY_KEY_SEPARATOR);
				if (lastDot > lastPKSeparator) {
					format = path.substring(lastDot + 1);
		            if (!EnvConfig.getInstance().hasMimeTypeFor(format)) {
		            	format = null;
		            }
		            else {
						s = s.substring(0, lastDot);
						requestPath = s;
		            }
				}
			}
			
			if (s.startsWith("/")) s = s.substring(1);

			pathSegments = s.split("/");
			segmentCount = pathSegments.length;
		}
	}
}
