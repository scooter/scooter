/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.sqldataexpress.util;

import com.scooterframework.admin.ApplicationConfig;
import com.scooterframework.autoloader.ClassManager;
import com.scooterframework.common.util.ObjectFactory;

/**
 * OrmObjectFactory class has helper methods for object creation in db tier. 
 * 
 * @author (Fei) John Chen
 */
public class OrmObjectFactory extends ObjectFactory
{
	private static OrmObjectFactory me;
	
	static {
		me = new OrmObjectFactory();
    }
    
    protected OrmObjectFactory() {
    }
	
    public static synchronized OrmObjectFactory getInstance() {
        return me;
    }
    
    public Class<?> loadClass(String className) 
    throws ClassNotFoundException {
        Class<?> c = null;
        if (!ApplicationConfig.getInstance().isOrmAlone()) {
            c = ClassManager.getInstance().loadMyClass(className);
        }
        else {
            c = super.loadClass(className);
        }
        return c;
    }
}
