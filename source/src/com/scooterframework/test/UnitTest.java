/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.test;

import java.io.File;

import junit.framework.TestCase;

import com.scooterframework.admin.ApplicationConfig;
import com.scooterframework.admin.DirChangeMonitor;
import com.scooterframework.admin.EnvConfig;
import com.scooterframework.admin.PropertyFileChangeMonitor;
import com.scooterframework.common.logging.LogUtil;

/**
 * UnitTest class is super class of all unit test classes for an application.
 * 
 * @author (Fei) John Chen
 *
 */
public class UnitTest extends TestCase {
	protected LogUtil log = LogUtil.getLogger(this.getClass().getName());
    
	protected static boolean applicationInited = false;
	
	protected ApplicationConfig ac;
	
	protected String contextName;
	
	protected EnvConfig wc;
	
	protected void setUp() {
		if (!applicationInited) {
			initApp();
		}
	}
	
	protected void tearDown() {
	}
	
	protected void initApp() {
		String appPath = System.getProperty("appPath");
		System.out.println("Starting unit test for " + appPath);
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
		
		//ApplicationConfig.noConsoleDisplay = true;
		//LogUtil.manualStopOn();
		
		ac = ApplicationConfig.configInstanceForApp();
		ac.getInstance().startApplication();
		
		//FileMonitor.turnOff = true;
		contextName = ac.getContextName().toLowerCase();
		
		wc = EnvConfig.getInstance();
		
		PropertyFileChangeMonitor.getInstance().stop();
		DirChangeMonitor.getInstance().stop();
		
		applicationInited = true;
	}
	
	/**
	 * This is a dummy test method that is used to work around a 
	 * "junit.framework.AssertionFailedError: No tests found" failure when  
	 * using junit3.8
	 */
	public void testDummy() {
		;//do nothing.
	}
}
