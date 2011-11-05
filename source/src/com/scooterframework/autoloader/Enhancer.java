/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.autoloader;

import java.io.File;

import com.scooterframework.admin.ApplicationConfig;
import com.scooterframework.admin.Constants;

public class Enhancer {
	protected static void enhance() {
		String appPath = System.getProperty("appPath");
		if (appPath == null) {
			throw new IllegalArgumentException("Please specify -DappPath=...");
		}
		else {
			File dir = new File(appPath);
			if (!dir.exists()) {
				throw new IllegalArgumentException("App path \"" + appPath + "\" does not exist.");
			}
		}
		
		String scooterHome = "";
		try {
			scooterHome = ApplicationConfig.detectRootPath();
			System.setProperty("scooter.home", scooterHome);
		} catch (Exception ex) {
			throw new IllegalArgumentException("Failed to detect root path of scooter.home: " + ex.getMessage());
		}

        String appWebInfPath = appPath + File.separator + "WEB-INF";
    	String appLogs = appWebInfPath + File.separator + "logs";
        System.setProperty("app.logs", appLogs);
        
        String classFiles = appWebInfPath + File.separator + "classes";
        System.setProperty(ApplicationConfig.SYSTEM_KEY_CLASSFILE, classFiles);
        
        String propertyFiles = appWebInfPath + File.separator + "config";
        System.setProperty(ApplicationConfig.SYSTEM_KEY_PROPERTYFILE, propertyFiles);
        
        String sourceFiles = appWebInfPath + File.separator + "src";
        System.setProperty(ApplicationConfig.SYSTEM_KEY_SOURCEFILE, sourceFiles);
        
        String referenceFiles = scooterHome + File.separator + "lib";
        System.setProperty(ApplicationConfig.SYSTEM_KEY_REFERENCEFILE, referenceFiles);

		System.setProperty(Constants.ALLOW_CLASSWORK, "true");
		
		//ApplicationConfig.noConsoleDisplay = true;
		//LogUtil.manualStopOn();
		
		ApplicationConfig ac = ApplicationConfig.configInstanceForApp();
		ac.startApplication();
		ac.endApplication();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		enhance();
	}

}
