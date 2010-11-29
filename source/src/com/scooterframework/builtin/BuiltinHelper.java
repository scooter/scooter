/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.builtin;

import static com.scooterframework.web.controller.ActionControl.*;

import java.io.File;
import java.util.StringTokenizer;

import com.scooterframework.admin.ApplicationConfig;
import com.scooterframework.admin.EnvConfig;
import com.scooterframework.web.controller.ActionResult;
import com.scooterframework.web.util.W;

/**
 * BuiltinHelper class has helper methods for built-ins.
 * 
 * @author (Fei) John Chen
 */
public class BuiltinHelper {
	public static String FILE_BROWSER_LINK_PREFIX = "/admin/files";
	public static String FILE_BROWSER_LINK_PREFIX_DIR = FILE_BROWSER_LINK_PREFIX + "/list?f=";
	public static String FILE_BROWSER_LINK_PREFIX_FILE = FILE_BROWSER_LINK_PREFIX + "/show?f=";
	public static String FILE_BROWSER_LINK_ROOT = FILE_BROWSER_LINK_PREFIX_DIR + "/";
	
    /**
     * Checks if the request comes from localhost.
     * 
     * Currently only local requests are valid. 
     * 
     * @return true if the request is valid. 
     */
    public String validateLocalRequest() {
        boolean valid = W.isLocalRequest();
        if (!valid) {
            flash("error", "You are not allowed to browse data because you are not from localhost.");
            return ActionResult.forwardTo(EnvConfig.getInstance().getErrorPageURI());
        }
        
        return null;
    }
    
    /**
     * Returns locator links for all segments of the file path.
     * 
     * @param path  The requested file path
     * @return locator links
     */
    public static String getLocatorLinks(String path) {
    	if (path == null) return "";

    	StringBuilder sb = new StringBuilder();
    	String contextName = ApplicationConfig.getInstance().getContextName();
    	sb.append(W.labelLink(contextName, FILE_BROWSER_LINK_ROOT)).append(" > ");

		String link = FILE_BROWSER_LINK_PREFIX_DIR;
		StringTokenizer st = new StringTokenizer(path, "/");
		int total = st.countTokens();
		int index = 0;
		while(st.hasMoreTokens()) {
			index++;
			String token = st.nextToken();
			if (index != total) {
				link += "/" + token;
				sb.append(W.labelLink(token, link)).append(" > ");
			}
			else {
				sb.append(token);
			}
		}
    	return sb.toString();
    }
    
    /**
     * Returns locator links for all segments of the file path.
     * 
     * @param requestFile  The requested file
     * @return locator links
     */
    public static String getLocatorLinks(File requestFile) {
    	if (requestFile == null) return "";

    	StringBuilder sb = new StringBuilder();
    	String contextName = ApplicationConfig.getInstance().getContextName();
    	sb.append(W.labelLink(contextName, FILE_BROWSER_LINK_ROOT)).append(" > ");
    	
    	String appPath = ApplicationConfig.getInstance().getApplicationPath();
    	String cPath = "";
    	try {
    		cPath = requestFile.getCanonicalPath();
    	} catch (Exception ex) {
    		;
    	}
    	
    	if (cPath.startsWith(appPath)) {
    		String link = "";
    		cPath = cPath.substring(appPath.length());
    		StringTokenizer st = new StringTokenizer(cPath, File.separator);
    		int total = st.countTokens();
    		int index = 0;
    		while(st.hasMoreTokens()) {
    			index++;
    			String token = st.nextToken();
				link += "/" + token;
				if (isDirPath(link)) {
    				sb.append(W.labelLink(token, FILE_BROWSER_LINK_PREFIX_DIR + link));
				}
				else {
					sb.append(W.labelLink(token, FILE_BROWSER_LINK_PREFIX_FILE + link));
				}
				
    			if (index != total) {
    				sb.append(" > ");
    			}
    		}
    	}
    	return sb.toString();
    }
    
    private static boolean isDirPath(String path) {
    	String fullPath = 
    		ApplicationConfig.getInstance().getApplicationPath() + path;
    	File f = new File(fullPath);
    	return f.isDirectory();
    }
    
    /**
     * Returns file name.
     * 
     * @param file  the file
     * @return name of the file
     */
    public static String getFileName(File file) {
    	return (file != null)?file.getName():"";
    }
}
