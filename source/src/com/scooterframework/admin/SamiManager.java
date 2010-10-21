/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.admin;

import java.util.HashSet;
import java.util.Set;

public class SamiManager {
	private static SamiManager me;
	private Set<Class> controllers = new HashSet<Class>();
	private Set<Class> models = new HashSet<Class>();
	
	private SamiManager() {
	}
	
	/**
	 * Returns the singleton instance of the <tt>EventsManager</tt>.
	 * 
	 * @return the singleton instance of the <tt>EventsManager</tt>.
	 */
	public static SamiManager getInstance() {
		if (me == null) me = new SamiManager();
		return me;
	}
	
	/**
	 * Registers a controller class after its complete configuration.
	 * 
	 * @param controllerClass  the fully configured controller class
	 */
	public void registerController(Class controllerClass) {
		controllers.add(controllerClass);
	}
	
	/**
	 * Registers a model class after its complete configuration.
	 * 
	 * @param modelClass  the fully configured model class
	 */
	public void registerModel(Class modelClass) {
		models.add(modelClass);
	}
	
	/**
	 * Checks if the class has been completely configured as a controller.
	 * @param clazz
	 * @return true if the controller class has been configured.
	 */
	public boolean hasConfiguredAsController(Class clazz) {
		return controllers.contains(clazz);
	}
	
	/**
	 * Checks if the class has been completely configured as a model.
	 * @param clazz
	 * @return true if the model class has been configured.
	 */
	public boolean hasConfiguredAsModel(Class clazz) {
		return models.contains(clazz);
	}
	
	/**
	 * Deregisters a controller class.
	 * 
	 * @param controllerClass  the fully configured controller class
	 */
	public void deRegisterController(Class controllerClass) {
		controllers.remove(controllerClass);
	}
	
	/**
	 * Deregisters a model class.
	 * 
	 * @param modelClass  the fully configured model class
	 */
	public void deRegisterModel(Class modelClass) {
		models.remove(modelClass);
	}
}
