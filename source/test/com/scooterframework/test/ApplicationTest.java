/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.test;

import junit.framework.TestCase;

import com.scooterframework.admin.ApplicationConfig;
import com.scooterframework.admin.DirChangeMonitor;
import com.scooterframework.admin.EnvConfig;
import com.scooterframework.admin.PropertyFileChangeMonitor;
import com.scooterframework.autoloader.FileMonitor;
import com.scooterframework.common.logging.LogUtil;

/**
 * ApplicationTest class
 * 
 * @author (Fei) John Chen
 *
 */
public class ApplicationTest extends TestCase {
	protected LogUtil log = LogUtil.getLogger(this.getClass().getName());
    
	private boolean applicationInited = false;
	
	protected String rootPath;
	protected String contextName;
	
	protected EnvConfig wc;
	
	protected void setUp() {
		if (!applicationInited) {
			initApp();
		}
	}
	
	protected void initApp() {
		//ApplicationConfig.noConsoleDisplay = true;
		//LogUtil.manualStopOn();
		
		ApplicationConfig ac = ApplicationConfig.configInstanceForApp();
		FileMonitor.turnOff = true;
		rootPath = ac.getApplicationRootPath();
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
