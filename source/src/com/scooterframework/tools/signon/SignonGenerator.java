/*
 *   This software is distributed under the terms of the FSF
 *   Gnu Lesser General Public License (see lgpl.txt).
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.tools.signon;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.scooterframework.admin.ApplicationConfig;
import com.scooterframework.tools.common.AbstractGenerator;
import com.scooterframework.tools.common.AmbiguousAppNameException;
import com.scooterframework.tools.common.Generator;
import com.scooterframework.tools.common.ToolsUtil;

/**
 * This is the main class of signon generator.
 * 
 * <p>
 * Usage examples:
 * <pre>
	Usage:
	    java -jar tools/generate-signon.jar [app_path] [custom signon controller name]
	
	Examples:
	    This page:
	        java -jar tools/generate-signon.jar -help
	
	    Generate scaffold controller and views of signon for blog app:
	        java -jar tools/generate-signon.jar blog
	
	    Generate scaffold controller and views of signon for blog app in user home directory:
	        java -jar tools/generate-signon.jar /home/john/projects/blog
	
	    Generate scaffold controller and views of signon for blog app, and blog is the only app under webapps:
	        java -jar tools/generate-signon.jar
	
	    Generate scaffold controller and views of signon with admin as controller name:
	        java -jar tools/generate-signon.jar blog admin
 * </pre>
 * </p>
 *
 * @author (Fei) John Chen
 */
public class SignonGenerator {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
    	if (args.length > 2 || args.length == 1 && args[0].equalsIgnoreCase("-help")) {
			usage();
			return;
		}

		try {
			doTheWork(args);
		}
		catch(AmbiguousAppNameException ax) {
			log("ERROR ERROR ERROR: " + ax.getMessage());
			return;
		}
		catch(Throwable ex) {
			ex.printStackTrace();
			log("ERROR ERROR ERROR: " + ex.getMessage());
		}

		if (AbstractGenerator.frameworkInitiated) 
			ApplicationConfig.configInstanceForApp().endApplication();
	}

    private static void doTheWork(String[] args) throws Throwable {
        String signonControllerName = "signon";
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
				signonControllerName = args[1].toLowerCase();
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
        
    	String targetDir = webappPath;

		log("Generating code for app named " + appName + ":");
    	log("scooter.home: " + scooterHome);
    	log("Target dir: " + targetDir);
		
		//create all properties
    	Map<String, String> allProps = new HashMap<String, String>();
    	allProps.put("scooter.home", scooterHome);
    	allProps.put("webapps.path", webappsPath);
    	allProps.put("app.path", webappPath);
    	allProps.put("app.name", appName);
    	
    	String templateRoot = scooterHome + File.separator + 
							"source" + File.separator + 
							"templates";
    	
    	String vliPath = templateRoot + File.separator + 
							"view" + File.separator + 
							"security" + File.separator + 
							"login.tmpl";
		Generator vli = new ViewLoginGenerator(vliPath, allProps, signonControllerName, "login");
		vli.generate();
    	
    	String vloPath = templateRoot + File.separator + 
							"view" + File.separator + 
							"security" + File.separator + 
							"logout.tmpl";
		Generator vlo = new ViewLogoutGenerator(vloPath, allProps, signonControllerName, "logout");
		vlo.generate();
    	
    	String vmaPath = templateRoot + File.separator + 
							"view" + File.separator + 
							"security" + File.separator + 
							"signon_main.tmpl";
		Generator vma = new ViewMainGenerator(vmaPath, allProps, signonControllerName, "main");
		vma.generate();
    	
    	String scPath = templateRoot + File.separator + 
							"controller" + File.separator + 
							"SignonController.tmpl";
		Generator sc = new ControllerSignonGenerator(scPath, allProps, signonControllerName);
		sc.generate();
    	
    	String shdPath = templateRoot + File.separator + 
							"view" + File.separator + 
							"security" + File.separator + 
							"header.tmpl";
		Generator shd = new SecureHeaderGenerator(shdPath, allProps);
		shd.generate();
    	
    	String sdePath = templateRoot + File.separator + 
							"view" + File.separator + 
							"security" + File.separator + 
							"decorators.xml.tmpl";
		Generator sde = new SecureXMLDecoratorGenerator(sdePath, allProps);
		sde.generate();
    }

    private static void log(Object o) {
		System.out.println(o);
    }

    private static void usage() {
    	log("Summary:");
    	log("    This is a utility that generates signon codes.");
    	log("");
    	log("Usage:");
    	log("    java -jar tools/generate-signon.jar [app_path] [custom signon controller name]");
    	log("");
    	log("Examples:");
    	log("    This page:");
    	log("        java -jar tools/generate-signon.jar -help");
    	log("");
    	log("    Generate scaffold controller and views of signon for blog app:");
    	log("        java -jar tools/generate-signon.jar blog");
    	log("");
    	log("    Generate scaffold controller and views of signon for blog app in user home directory:");
    	log("        java -jar tools/generate-signon.jar /home/john/projects/blog");
    	log("");
    	log("    Generate scaffold controller and views of signon for blog app, and blog is the only app under webapps:");
    	log("        java -jar tools/generate-signon.jar");
    	log("");
    	log("    Generate scaffold controller and views of signon with admin as controller name:");
    	log("        java -jar tools/generate-signon.jar blog admin");
    	log("");
    }
}