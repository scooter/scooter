/*
 *   This software is distributed under the terms of the FSF
 *   Gnu Lesser General Public License (see lgpl.txt).
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.tools.connection;

import java.io.File;
import java.util.HashMap;

import com.scooterframework.admin.ApplicationConfig;
import com.scooterframework.tools.common.AbstractGenerator;
import com.scooterframework.tools.common.ToolsUtil;

/**
 * This is a utility to test database connections.
 * 
 * <p>
 * Usage examples:
 * <pre>
	Usage:
	    java -jar tools/connection-test.jar [app_path] [connection name]
	
	Examples:
	    This page:
	        java -jar tools/connection-test.jar -help
	
	    Test blog app's default connection:
	        java -jar tools/connection-test.jar blog
	
	    Test blog app's db connection named oracle_db:
	        java -jar tools/connection-test.jar blog oracle_db
 * </pre>
 * </p>
 *
 * @author (Fei) John Chen
 */
public class ConnectionCheck {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length > 2 || args.length == 1
				&& args[0].equalsIgnoreCase("-help")) {
			usage();
			return;
		}

		try {
			doTheWork(args);
		}
		catch(Throwable ex) {
			log("ERROR ERROR ERROR: " + ex.getMessage());
			ex.printStackTrace();
		}

		if (AbstractGenerator.frameworkInitiated) 
			ApplicationConfig.configInstanceForApp().endApplication();
	}

    private static void doTheWork(String[] args) throws Exception {
        String connectionName = "";
    	String scooterHome = ToolsUtil.setSystemProperty("scooter.home", ToolsUtil.detectRootPath());
    	String defaultWebappsName = ToolsUtil.setSystemProperty("webapps.name", "webapps");
    	
    	String appName = "";
		String webappsPath = "";
		String webappPath = "";
		if (args.length == 0) {//use implicit app name
	        webappsPath = scooterHome + File.separator + defaultWebappsName;
			appName = ToolsUtil.detectImplicitAppName(webappsPath);
	        webappPath = webappsPath + File.separator + appName;
		}
		else if (args.length > 0) {
			String firstArg = args[0];
			if (ToolsUtil.containsPath(firstArg)) {
				String[] ss = ToolsUtil.getPathAndName(firstArg);
				webappsPath = ss[0];
				webappPath  = ss[1];
				appName     = ss[2];
			}
			else {
				appName = firstArg;
		        webappsPath = scooterHome + File.separator + defaultWebappsName;
		        webappPath = webappsPath + File.separator + appName;
			}
			
			if (args.length == 2) {
				connectionName = args[1];
			}
		}
		appName = appName.toLowerCase();
        System.setProperty("app.name", appName);
        
        webappsPath = ToolsUtil.setSystemProperty("webapps.path", webappsPath);
        ToolsUtil.validatePathExistence(webappsPath);
        
        webappPath = ToolsUtil.setSystemProperty("app.path", webappPath);
        ToolsUtil.validatePathExistence(webappPath);
        
    	String appLogs = ToolsUtil.setSystemProperty("app.logs", (scooterHome + File.separator + "logs"));
        ToolsUtil.validatePathExistence(appLogs);
        
        String classFiles = webappPath + File.separator + "WEB-INF" + File.separator + "classes";
        System.setProperty(ApplicationConfig.SYSTEM_KEY_CLASSFILE, classFiles);
        ToolsUtil.validatePathExistence(classFiles);
        
        String propertyFiles = webappPath + File.separator + "WEB-INF" + File.separator + "config";
        System.setProperty(ApplicationConfig.SYSTEM_KEY_PROPERTYFILE, propertyFiles);
        ToolsUtil.validatePathExistence(propertyFiles);
        
        String sourceFiles = webappPath + File.separator + "WEB-INF" + File.separator + "src";
        System.setProperty(ApplicationConfig.SYSTEM_KEY_SOURCEFILE, sourceFiles);
        ToolsUtil.validatePathExistence(sourceFiles);
        
        String referenceFiles = scooterHome + File.separator + "lib";
        System.setProperty(ApplicationConfig.SYSTEM_KEY_REFERENCEFILE, referenceFiles);
        ToolsUtil.validatePathExistence(referenceFiles);
        
        String pluginFiles = scooterHome + File.separator + "plugins";
        System.setProperty(ApplicationConfig.SYSTEM_KEY_PLUGINFILE, pluginFiles);
        ToolsUtil.validatePathExistence(pluginFiles);
    	
    	new ConnectionGenerator(connectionName, new HashMap<String, String>());
    }

    private static void log(Object o) {
		System.out.println(o);
    }

    private static void usage() {
    	log("Usage:");
    	log("    java -jar tools/connection-test.jar [app_path] [connection name]");
    	log("");
    	log("Examples:");
    	log("    This page:");
    	log("        java -jar tools/connection-test.jar -help");
    	log("");
    	log("    Test blog app's default connection:");
    	log("        java -jar tools/connection-test.jar blog");
    	log("");
    	log("    Test blog app's db connection named oracle_db:");
    	log("        java -jar tools/connection-test.jar blog oracle_db");
    	log("");
    }
}