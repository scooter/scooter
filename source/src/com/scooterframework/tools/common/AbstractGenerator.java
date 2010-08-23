/*
 *   This software is distributed under the terms of the FSF
 *   Gnu Lesser General Public License (see lgpl.txt).
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.tools.common;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.scooterframework.admin.ApplicationConfig;
import com.scooterframework.admin.EnvConfig;
import com.scooterframework.autoloader.FileMonitor;
import com.scooterframework.builtin.CRUDController;
import com.scooterframework.common.logging.LogUtil;
import com.scooterframework.orm.activerecord.ActiveRecord;
import com.scooterframework.orm.activerecord.ActiveRecordUtil;

/**
 * The is the super class of all app generator classes that need initiation of 
 * the application.
 *
 * @author (Fei) John Chen
 */
public abstract class AbstractGenerator extends GeneratorImpl {
	protected EnvConfig wc;
	public static boolean frameworkInitiated = false;

	public AbstractGenerator(Properties props) {
		super(props);
		initApp();
	}

	public AbstractGenerator(String templateFilePath, Properties props) {
		super(templateFilePath, props);
		initApp();
	}
	
	private void initApp() {
		frameworkInitiated = true;
		
		ApplicationConfig.noConsoleDisplay = true;
		LogUtil.manualStopOn();

		ApplicationConfig ac = ApplicationConfig.configInstanceForApp();
		FileMonitor.turnOff = true;

        ac.startApplication();

		wc = EnvConfig.getInstance();

		//ac.endApplication();
	}
	
	protected String getRootPath() {
		return getProperty("app.path") + File.separator + "WEB-INF";
	}

    /**
     * Generates an ActiveRecord home instance of a model model
     *
     * @param model model model
     * @return an ActiveRecord home instance of the model model
     */
    protected ActiveRecord generateActiveRecordHomeInstance(String model) {
        ActiveRecord record = (ActiveRecord)modelHomes.get(model);
        if (record == null) {
	        record = ActiveRecordUtil.generateActiveRecordInstance(CRUDController.DEFAULT_RECORD_CLASS, model);
	        if (record != null) record.freeze();
	        ActiveRecordUtil.setHomeInstance(record);
	        modelHomes.put(model, record);
        }
        return record;
    }

    private static Map modelHomes = new HashMap();
}