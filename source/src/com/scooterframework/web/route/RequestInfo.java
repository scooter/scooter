/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.web.route;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import com.scooterframework.common.logging.LogUtil;


/**
 * RequestInfo class
 * 
 * @author (Fei) John Chen
 *
 */
public class RequestInfo {
	private static LogUtil log = LogUtil.getLogger(RequestInfo.class.getName());
	
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
		
		decode();
	}
	
	private void decode() {
		requestPath = decode(requestPath);
		
		for (int i = 0; i < pathSegments.length; i++) {
			pathSegments[i] = decode(pathSegments[i]);
		}
	}
	
	private String decode(String s) {
		String ss = s;
		try {
			ss = URLDecoder.decode(s, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			log.warn("Failed to decode \"" + s + "\" because " + e.getMessage());
		}
		return ss;
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
		return pathSegments;
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
        StringBuffer returnString = new StringBuffer();
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
    	}
		else {
			String s = path;
	    	if (path.startsWith("/")) s = path.substring(1);
	    	
	    	int lastDot = s.lastIndexOf('.');
	    	int lastSlash = s.lastIndexOf('/');
	    	if (lastDot > lastSlash) {
	    		int lastPKSeparator = s.lastIndexOf(RouteConstants.PRIMARY_KEY_SEPARATOR);
	    		if (lastDot > lastPKSeparator) {
	    			String tmp = s.substring(lastDot + 1);
	    			if (!RouteConfig.isSupportedRequestFormat(tmp)) {
	    				format = tmp;
	    	    		s = s.substring(0, lastDot);
	    			}
	    		}
	    	}
	    	
	    	pathSegments = s.split("/");
	    	segmentCount = pathSegments.length;
		}
	}
}
