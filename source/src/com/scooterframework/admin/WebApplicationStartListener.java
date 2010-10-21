/*
 *   This software is distributed under the terms of the FSF
 *   Gnu Lesser General Public License (see lgpl.txt).
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.admin;

import java.io.File;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * This class kicks off <tt>ApplicationConfig</tt> class.
 *
 * @author (Fei) John Chen
 */
public class WebApplicationStartListener implements ServletContextListener	{
	private boolean acInitialized = false;

    /**
     * <p>Initializes context. Specifically it starts up the application
     * through <tt>ApplicationConfig.configInstanceForWeb(rootPath, contextName).startApplication()</tt>.</p>
     *
     * <p>The <tt>rootPath</tt> is derived from the real path of the servlet
     * context. </p>
     *
     * @param ce ServletContextEvent
     */
	public void	contextInitialized(ServletContextEvent ce) {
		if (acInitialized) return;
		
		ServletContext servletContext = ce.getServletContext();
        String contextName = "";
        String realPath = null;
        try {
        	File rp = new File(servletContext.getRealPath(""));
            realPath = rp.getCanonicalPath();
        	contextName = getContextName(realPath);
        	String appName = System.getProperty("app.name");
        	if (appName == null) {
        		throw new IllegalArgumentException(
        				"ERROR ERROR ERROR: You need to add -Dapp.name=" + 
        				contextName + " on command line.");
        	}
        	
        	if (contextName.equals(appName)) {
	        	ApplicationConfig ac = ApplicationConfig.configInstanceForWeb(realPath, contextName);
	        	ac.startApplication();
	        	acInitialized = true;
        	}
        }
        catch(Exception ex) {
        	String errorMessage = "Failed to detect root path and " +
        	"context name from \"" + realPath + "\": " + ex.getMessage();
        	System.err.println(errorMessage);
            System.out.println("Stop initializtion process. Exit now ...");
        }
	}

	public void	contextDestroyed(ServletContextEvent ce) {
		ServletContext servletContext = ce.getServletContext();
        String contextName = "";
        String realPath = null;
        try {
        	File rp = new File(servletContext.getRealPath(""));
            realPath = rp.getCanonicalPath();
        	contextName = getContextName(realPath);
        	String appName = System.getProperty("app.name");
        	if (contextName.equals(appName)) {
        		ApplicationConfig.getInstance().endApplication();
	        	acInitialized = false;
        	}
        }
        catch(Exception ex) {
        	String errorMessage = "Failed to detect root path and " +
        	"context name from \"" + realPath + "\": " + ex.getMessage();
        	System.err.println(errorMessage);
            System.out.println("Stop initializtion process. Exit now ...");
        }
    }
    
    private String getContextName(String realPath) {
    	String s = "";
		File f = new File(realPath);
		try {
			String filePath = f.getCanonicalPath();
			String parentPath = (new File(filePath)).getParentFile().getCanonicalPath();
			s = (parentPath.endsWith(File.separator))?
					filePath.substring(parentPath.length()):
					filePath.substring(parentPath.length() + 1);
		}
		catch(Exception ex) {
			;
		}
		return s;
    }
}
