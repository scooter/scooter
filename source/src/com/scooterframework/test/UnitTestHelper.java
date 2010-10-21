/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.test;

import java.io.File;
import java.sql.Connection;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import com.scooterframework.admin.ApplicationConfig;
import com.scooterframework.admin.Constants;
import com.scooterframework.admin.EnvConfig;
import com.scooterframework.orm.sqldataexpress.config.DatabaseConfig;
import com.scooterframework.orm.sqldataexpress.util.DAOUtil;
import com.scooterframework.orm.sqldataexpress.util.SqlExpressUtil;

/**
 * UnitTestHelper class is super class of all unit test classes for an application.
 * 
 * @author (Fei) John Chen
 *
 */
public class UnitTestHelper {
	
	protected static ApplicationConfig ac;
	
	protected static String contextName;
	
	protected static EnvConfig wc;
	
	@BeforeClass
	public static void setUpBeforeClass() {
		initApp();
	}

	@AfterClass
	public static void tearDownAfterClass() {
		if (ac != null) ac.endApplication();
	}
	
	@Before
	public void setUp() {
	}
	
	@After
	public void tearDown() {
	}
	
	protected static void initApp() {
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

		System.setProperty(Constants.ALLOW_CLASSWORK, "true");
		
		//ApplicationConfig.noConsoleDisplay = true;
		//LogUtil.manualStopOn();
		
		ac = ApplicationConfig.configInstanceForApp();
		ac.getInstance().startApplication();
		
		contextName = ac.getContextName().toLowerCase();
		
		wc = EnvConfig.getInstance();
		
		try {
			String runningEnvironment = ac.getRunningEnvironment();
			if (!Constants.RUNNING_ENVIRONMENT_TEST.equals(runningEnvironment)) {
				ac.setRunningEnvironment(Constants.RUNNING_ENVIRONMENT_TEST);
				System.out.println("Scooter automatically switched to TEST environment.");
			}
			
			String testDB = DatabaseConfig.getInstance().tryToUseTestDatabaseConnection();
			if (testDB != null) {
				System.out.println("Scooter automatically switched to test database \"" + testDB + "\" for testing.");
				boolean checkConn = validateConnection(testDB);
				if (!checkConn) {
					throw new IllegalArgumentException("Connection named '" + testDB + "' cannot be established.");
				}
			}
			else {
				System.out.println("Please make sure you are using a test database.");
			}
		} catch (Throwable ex) {
			System.out.println("ERROR in starting up test server: " + ex.getMessage());
			ex.printStackTrace();
		}
	}
    
    private static boolean validateConnection(String connectionName) {
    	boolean check = false;
    	Connection conn = null;
		try {
			conn = SqlExpressUtil.getConnection(connectionName);
			check = true;
		} catch (Exception ex) {}
		finally {
    		DAOUtil.closeConnection(conn);
		}
		return check;
    }
}
