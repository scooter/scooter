/*
 *   This software is distributed under the terms of the FSF
 *   Gnu Lesser General Public License (see lgpl.txt).
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.tools.creator;

import java.io.File;
import java.util.Properties;

import com.scooterframework.common.util.FileUtil;
import com.scooterframework.tools.common.ToolsUtil;

/**
 * AppCreator creates a web application. The web application can be created 
 * outside of Scooter installation directory.
 * 
 * <p>
 * Usage examples:
 * <pre>  
	Usage:
	    java -jar tools/create.jar app_name [database_type (h2, hsqldb, mysql, oracle) [app_domain]]
	
	Examples:
	    This page:
	        java -jar tools/create.jar -help
	
	    Create a blog application backed by default database:
	        java -jar tools/create.jar blog
	
	    Create a blog application in user home directory:
	        java -jar tools/create.jar /home/john/projects/blog
	
	    Create a blog application backed by H2 database:
	        java -jar tools/create.jar blog h2
	
	    Create a blog application backed by HSQLDB:
	        java -jar tools/create.jar blog hsqldb
	
	    Create a blog application backed by Oracle:
	        java -jar tools/create.jar blog oracle com.example.web
	    Generated package prefix will be com.example.web.blog.  
 * </pre>
 * </p>
 * 
 * @author (Fei) John Chen
 */
public class AppCreator {
    /**
     * @param args
     */
    public static void main(String[] args) {
    	if (args.length < 1 || args[0].equalsIgnoreCase("-help")) {
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
		
		System.exit(0);
    }
    
    private static void doTheWork(String[] args) throws Throwable {
    	String scooterHome = ToolsUtil.setSystemProperty("scooter.home", ToolsUtil.detectRootPath());
    	String defaultWebappsName = ToolsUtil.setSystemProperty("webapps.name", "webapps");
		
		String appName = "";
		String webappsPath = "";
		String webappPath = "";
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
		appName = appName.toLowerCase();

        System.setProperty("app.name", appName);
        webappsPath = ToolsUtil.setSystemProperty("webapps.path", webappsPath);
        webappPath = ToolsUtil.setSystemProperty("app.path", webappPath);
        
    	String targetDir = webappPath;
    	String dbType = "mysql";
    	String pkgPrefix = null;
    	
    	int length = args.length;
    	if (length > 2) {
    		dbType = args[1].toLowerCase();
    		pkgPrefix = args[2] + "." + appName;
    	}
    	else if (length == 2) {
    		dbType = args[1].toLowerCase();
    		pkgPrefix = appName;
    	}
    	else if (length == 1) {
    		pkgPrefix = appName;
    	}
    	
    	log("scooter.home: " + scooterHome);
    	log("Creating " + appName + " ...");
    	log("Target dir: " + targetDir);
    	
    	String templateRoot = scooterHome + File.separator + 
							"source" + File.separator + 
							"templates";
    	
    	String sourceDir = templateRoot + File.separator + "webapp";
    	sourceDir = ToolsUtil.setSystemProperty("source.dir", sourceDir);
    	
    	FileUtil.copyDir(new File(sourceDir), new File(targetDir));
    	
    	//create all properties
    	Properties allProps = new Properties();
    	allProps.setProperty("scooter.home", scooterHome);
    	allProps.setProperty("app_name", appName);
    	allProps.setProperty("app_path", webappPath);
    	allProps.setProperty("package_prefix", pkgPrefix);
    	setMoreProperties(allProps, dbType);
    	
    	ToolsUtil.processAllFiles(new File(targetDir), allProps);
    	
    	String acPath = templateRoot + File.separator + 
							"controller" + File.separator + 
							"ApplicationController.tmpl";
    	ControllerApplicationGenerator cag = new ControllerApplicationGenerator(acPath, allProps);
    	cag.generate();
    	
		log("");
	}
    
    private static void setMoreProperties(Properties templateProps, String databaseType) {
		
		String appName = templateProps.getProperty("app_name");
		
		String dbDriver = "";
		String developmentDbURL = "";
		String testDbURL = "";
		String productionDbURL = "";
		String username = "";
		String commonProperties = "";
		if ("h2".equals(databaseType)) {
			dbDriver = "org.h2.Driver";
			developmentDbURL = "jdbc:h2:tcp://localhost/~/" + appName + "_development" + commonProperties;
			testDbURL        = "jdbc:h2:tcp://localhost/~/" + appName + "_test" + commonProperties;
			productionDbURL  = "jdbc:h2:tcp://localhost/~/" + appName + "_production" + commonProperties;
			username = "sa";
		}
		else if ("hsqldb".equals(databaseType)) {
			dbDriver = "org.hsqldb.jdbcDriver";
			developmentDbURL = "jdbc:hsqldb:hsql://localhost/" + appName + "_development" + commonProperties;
			testDbURL        = "jdbc:hsqldb:hsql://localhost/" + appName + "_test" + commonProperties;
			productionDbURL  = "jdbc:hsqldb:hsql://localhost/" + appName + "_production" + commonProperties;
			username = "sa";
		}
		else if ("mysql".equals(databaseType)) {
			commonProperties = "?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull";
			dbDriver = "com.mysql.jdbc.Driver";
			developmentDbURL = "jdbc:mysql://localhost:3306/" + appName + "_development" + commonProperties;
			testDbURL        = "jdbc:mysql://localhost:3306/" + appName + "_test" + commonProperties;
			productionDbURL  = "jdbc:mysql://localhost:3306/" + appName + "_production" + commonProperties;
			username = "root";
		}
		else if ("oracle".equals(databaseType)) {
			dbDriver = "oracle.jdbc.driver.OracleDriver";
			developmentDbURL = "jdbc:oracle:thin:@127.0.0.1:1521:" + appName + "_development" + commonProperties;
			testDbURL        = "jdbc:oracle:thin:@127.0.0.1:1521:" + appName + "_test" + commonProperties;
			productionDbURL  = "jdbc:oracle:thin:@127.0.0.1:1521:" + appName + "_production" + commonProperties;
		}
		else if ("postgresql".equals(databaseType)) {
			dbDriver = "org.postgresql.Driver";
			developmentDbURL = "jdbc:postgresql://localhost:5432/" + appName + "_development" + commonProperties;
			testDbURL        = "jdbc:postgresql://localhost:5432/" + appName + "_test" + commonProperties;
			productionDbURL  = "jdbc:postgresql://localhost:5432/" + appName + "_production" + commonProperties;
		}
		else if ("sybase".equals(databaseType)) {
			dbDriver = "com.sybase.jdbc2.jdbc.SybDriver";
			developmentDbURL = "jdbc:sybase:Tds://localhost/" + appName + "_development" + commonProperties;
			testDbURL        = "jdbc:sybase:Tds://localhost/" + appName + "_test" + commonProperties;
			productionDbURL  = "jdbc:sybase:Tds://localhost/" + appName + "_production" + commonProperties;
		}
		else {
			dbDriver = "MyDB_DriverClassName";
		}
		
		templateProps.setProperty("app_name", appName);
		templateProps.setProperty("db_driver", dbDriver);
		templateProps.setProperty("development_db_url", developmentDbURL);
		templateProps.setProperty("test_db_url", testDbURL);
		templateProps.setProperty("production_db_url", productionDbURL);
		templateProps.setProperty("username", username);
    }
	
    private static void log(Object o) {
		System.out.println(o);
    }
	
    private static void usage() {
    	log("Summary:");
    	log("    This is a utility that creates a Scooter-powered web application.");
    	log("");
    	log("Usage:");
    	log("    java -jar tools/create.jar app_name [database_type (h2, hsqldb, mysql, oracle) [app_domain]]");
    	log("");
    	log("Examples:");
    	log("    This page:");
    	log("        java -jar tools/create.jar -help");
    	log("");
    	log("    Create a blog application backed by default database:");
    	log("        java -jar tools/create.jar blog");
    	log("");
    	log("    Create a blog application in user home directory:");
    	log("        java -jar tools/create.jar /home/john/projects/blog");
    	log("");
    	log("    Create a blog application backed by H2 database:");
    	log("        java -jar tools/create.jar blog h2");
    	log("");
    	log("    Create a blog application backed by HSQLDB:");
    	log("        java -jar tools/create.jar blog hsqldb");
    	log("");
    	log("    Create a blog application backed by Oracle:");
    	log("        java -jar tools/create.jar blog oracle com.example.web");
    	log("    Generated package prefix will be com.example.web.blog.");
    	log("");
    }
}