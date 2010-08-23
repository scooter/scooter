/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.test;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import junit.framework.TestCase;

import com.scooterframework.admin.ApplicationConfig;
import com.scooterframework.admin.EnvConfig;
import com.scooterframework.common.logging.LogUtil;


/**
 * FunctionalTest class is super class of all functional test classes for 
 * an application.
 * 
 * @author (Fei) John Chen
 *
 */
public class FunctionalTest extends TestCase {
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
		System.out.println("Starting functional test for " + appPath);
		if (appPath == null) {
			throw new IllegalArgumentException("Please specify -DappPath=...");
		}
		else {
			File dir = new File(appPath);
			if (!dir.exists()) {
				throw new IllegalArgumentException("App path \"" + appPath + "\" does not exist.");
			}
		}
		
		try {
			startUp(appPath);
			applicationInited = true;
		} catch (Throwable ex) {
			log.error("ERROR in starting up server: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	private static void startUp(String appName) throws Exception {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		String jettyMainClassName = "com.scooterframework.tools.webserver.StartServer";
		String[] args = { appName };

		invokeMain(cl, jettyMainClassName, args);
	}
	
    private static void invokeMain(ClassLoader classloader, String classname, String[] args)
    throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException, ClassNotFoundException
    {
        Class invoked_class = classloader.loadClass(classname);
        Class[] method_param_types = new Class[1];
        method_param_types[0]= args.getClass();
        Method main = invoked_class.getDeclaredMethod("main", method_param_types);
        Object[] method_params = new Object[1];
        method_params[0] = args;

        main.invoke(null, method_params);
    }
	
	protected void sendPostRequest(String uri) {
		System.out.println(this.getClass().getName() + "######### sendPostRequest");
		;
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
