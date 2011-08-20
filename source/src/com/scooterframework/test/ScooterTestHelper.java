/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.test;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import com.scooterframework.admin.ApplicationConfig;
import com.scooterframework.admin.Constants;
import com.scooterframework.admin.EnvConfig;
import com.scooterframework.common.logging.LogUtil;

/**
 * ScooterTestHelper class is super class of all unit test classes for Scooter framework only.
 * 
 * @author (Fei) John Chen
 *
 */
public class ScooterTestHelper {
	protected LogUtil log = LogUtil.getLogger(this.getClass().getName());
	
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
		System.setProperty(Constants.ALLOW_CLASSWORK, "true");
		
		//ApplicationConfig.noConsoleDisplay = true;
		//LogUtil.manualStopOn();
		
		ac = ApplicationConfig.configInstanceForApp();
		ApplicationConfig.getInstance().startApplication();
		
		contextName = ac.getContextName().toLowerCase();
		
		wc = EnvConfig.getInstance();
	}
}
