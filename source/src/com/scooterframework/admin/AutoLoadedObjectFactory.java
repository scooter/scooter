/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.admin;

import com.scooterframework.autoloader.ClassManager;
import com.scooterframework.common.util.ObjectFactory;

/**
 * AutoLoadedObjectFactory class has helper methods for object creation in web
 * tier.
 * 
 * @author (Fei) John Chen
 */
public class AutoLoadedObjectFactory extends ObjectFactory {
	private static final AutoLoadedObjectFactory me = new AutoLoadedObjectFactory();

	protected AutoLoadedObjectFactory() {
	}

	public static AutoLoadedObjectFactory getInstance() {
		return me;
	}
    
	public Class<?> loadClass(String className) throws ClassNotFoundException {
		Class<?> c = null;
		if (!ApplicationConfig.getInstance().isInProductionEnvironment()
				&& !ApplicationConfig.getInstance().isOrmAlone()) {
			c = ClassManager.getInstance().loadMyClass(className);
		} else {
			c = super.loadClass(className);
		}
		return c;
	}
}
