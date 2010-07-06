/*
 *   This software is distributed under the terms of the FSF
 *   Gnu Lesser General Public License (see lgpl.txt).
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.tools.creator;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import com.scooterframework.common.util.FileUtil;

/**
 * Main class for a scooter-powered web application.
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
			String scooterHome = System.getProperty("scooter.home");
			if (scooterHome == null) {
				scooterHome = detectRootPath();
				System.setProperty("scooter.home", scooterHome);
			}
	    	String appLogs = scooterHome + File.separator + "logs";
			System.setProperty("app.logs", appLogs);
			
			doTheWork(args);
		}
		catch(Exception ex) {
			log("ERROR ERROR ERROR: " + ex.getMessage());
			ex.printStackTrace();
		}
    }
    
    private static String detectRootPath() {
        String path = "";
        try {
            path = (new File("")).getCanonicalPath();
        }
        catch(Exception ex) {
            String errorMessage = "Failed to detect root path from current directory: " + ex.getMessage();
            System.err.println(errorMessage);
            System.out.println("Stop initializtion process. Exit now ...");
        }
        return path;
    }
    
    private static void doTheWork(String[] args) throws IOException {
    	String appName = "";
    	String dbType = "mysql";
    	String pkgPrefix = null;
    	
    	int length = args.length;
    	if (length > 2) {
    		appName = args[0];
        	appName = appName.toLowerCase();
    		dbType = args[1].toLowerCase();
    		pkgPrefix = args[2] + "." + appName;
    	}
    	else if (length == 2) {
    		appName = args[0];
        	appName = appName.toLowerCase();
    		dbType = args[1].toLowerCase();
    		pkgPrefix = appName;
    	}
    	else if (length == 1) {
    		appName = args[0];
        	appName = appName.toLowerCase();
    		pkgPrefix = appName;
    	}
    	
    	String scooterHome = System.getProperty("scooter.home");
    	log("scooter.home=" + scooterHome);
    	log("Creating " + appName + " ...");
    	
    	String sourceDir = scooterHome + File.separator + 
    							"source" + File.separator + 
    							"webapp";
    	String targetDir = scooterHome + File.separator + 
    							"webapps" + File.separator + appName;
    	
    	FileUtil.copyDir(new File(sourceDir), new File(targetDir));
    	
    	//create all properties
    	Properties allProps = new Properties();
    	allProps.setProperty("scooter.home", scooterHome);
    	allProps.setProperty("app_name", appName);
    	allProps.setProperty("package_prefix", pkgPrefix);
    	setMoreProperties(allProps, dbType);
    	
    	processAllFiles(new File(targetDir), allProps);
    	
    	ControllerApplicationGenerator cag = new ControllerApplicationGenerator(allProps);
    	cag.generate();
    	
		log("");
		
		System.exit(0);
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
    
    private static void processAllFiles(File targetDir, Properties allProps) {
    	File[] files = targetDir.listFiles();
    	if (files == null) return;
    	for (int i = 0; i < files.length; i++) {
    		File file = files[i];
    		if (file.isFile()) {
    			if (FileUtil.isAsciiFile(file)) {
    				FileProcessor fp = new FileProcessor(file, allProps);
    				fp.process();
    			}
    		}
    		else if (file.isDirectory()) {
    			processAllFiles(file, allProps);
    		}
    	}
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