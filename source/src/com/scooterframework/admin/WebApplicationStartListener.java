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
    private static ServletContext servletContext = null;
    private static String realPath = null;
    private static String rootPath = null;
    
    /**
     * <p>Initializes context. Specifically it starts up the application 
     * through <tt>ApplicationConfig.configInstanceForWeb(rootPath, contextName).startApplication()</tt>.</p>
     * 
     * <p>The <tt>rootPath</tt> is derived from the real path of the servlet 
     * context. It should be the parent path of the <tt>webapps</tt> directory.</p>
     * 
     * @param ce ServletContextEvent
     */
	public void	contextInitialized(ServletContextEvent ce) {
        servletContext = ce.getServletContext();
        String rootPath = "";
        String contextName = "";
        try {
        	File rp = new File(servletContext.getRealPath(""));
            realPath = rp.getCanonicalPath();
        	contextName = realPath.substring(realPath.lastIndexOf(File.separatorChar) + 1);
        	rootPath = rp.getParentFile().getParentFile().getCanonicalPath();
        }
        catch(Exception ex) {
        	String errorMessage = "Failed to detect root path and " + 
        	"context name from \"" + realPath + "\": " + ex.getMessage();
        	System.err.println(errorMessage);
            System.out.println("Stop initializtion process. Exit now ...");
        }
        
        ApplicationConfig.configInstanceForWeb(realPath, rootPath, contextName).startApplication();
	}
    
	public void	contextDestroyed(ServletContextEvent ce) {
        servletContext = null;
        ApplicationConfig.getInstance().endApplication();
    }
    
    /**
     * Returns ServletContext of the application.
     * 
     * @return initialized ServletContext
     */
    public static ServletContext getServletContext() {
        if (servletContext == null) throw new IllegalArgumentException("WebApplicationStartListener must be initialized first.");
        return servletContext;
    }
    
    /**
     * Returns real path of the web application. If the listener is not 
     * initialized, an IllegalArgumentException will be thrown. 
     * 
     * <p>The real path of a web application should be the full path of the 
     * <tt>webapps</tt> directory. </p>
     * 
     * @return real path of the web application. 
     */
    public static String getRealPath() {
        if (servletContext == null) throw new IllegalArgumentException("WebApplicationStartListener must be initialized first.");
        return realPath;
    }
    
    /**
     * Returns root path of the web application. If the listener is not 
     * initialized, an IllegalArgumentException will be thrown. 
     * 
     * </p>A root path should be the parent path of the real path as the web 
     * instance runs inside the <tt>webapps</tt> directory.</p>
     * 
     * @return root path of the web application. 
     */
    public static String getRootPath() {
        if (servletContext == null) throw new IllegalArgumentException("WebApplicationStartListener must be initialized first.");
        return rootPath;
    }
    
    private String detectRootPath(String realPath) {
        File parent = (new File(realPath)).getParentFile();
        if ("webaps".equalsIgnoreCase(parent.getName())) { 
            throw new IllegalArgumentException("This web application must " + 
                        "run under a webapps directory.");
        }
        return (new File(realPath)).getParentFile().getParent();
    }
}
