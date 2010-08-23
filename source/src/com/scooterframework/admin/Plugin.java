/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.admin;

import java.util.Properties;

import com.scooterframework.common.logging.LogUtil;

/**
 * Plugin class has methods to manage a plug-in. Scooter framework 
 * will call these methods. Plug-in implementation classes should implement 
 * these methods if they want framework to manage their life cycles. 
 * 
 * @author (Fei) John Chen
 */
public abstract class Plugin {
    
    protected LogUtil log = LogUtil.getLogger(this.getClass().getName());
    
    /**
     * Key to represent plugin <tt>name</tt> property.
     */
    public static final String KEY_PLUGIN_NAME = "name";
    
    /**
     * Key to represent plugin class name property.
     */
    public static final String KEY_PLUGIN_CLASS_NAME = "plugin_class";
    
	private boolean alive = false;
    
	private String name;
	private String pluginClassName;
	private Properties p = new Properties();
	
	protected Plugin(Properties p) {
		if (p == null) 
			throw new NullPointerException("Input properties for " + 
					this.getClass().getName() + " is null.");
		init();
		this.p = p;
	}
	
	private void init() {
		name = p.getProperty(KEY_PLUGIN_NAME);
		pluginClassName = p.getProperty(KEY_PLUGIN_CLASS_NAME);
		if (pluginClassName == null) {
			throw new NullPointerException("Class name must exist for plugin named " + name);
		}
	}
	
	/**
	 * Starts the plugin.
	 */
	void start() {
		onStart();
		alive = true;
	}
	
	/**
	 * Stops the plugin.
	 */
	void stop() {
		onStop();
		alive = false;
	}
	
	/**
	 * Do something when the plugin is started. Subclass may provide more 
	 * implementation here.
	 */
	public void onStart() {
	}
	
	/**
	 * Do something when the plugin is stopped. Subclass may provide more 
	 * implementation here.
	 */
	public void onStop() {
	}
	
	/**
	 * Returns status of the plugin.
	 */
	public boolean isAlive() {
		return alive;
	}
	
	/**
	 * Returns the plugin name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the plugin class name
	 */
	public String getPluginClassName() {
		return pluginClassName;
	}
	
	/**
	 * Returns the properties of the plugin.
	 */
	public Properties getProperties() {
		return p;
	}
	
	/**
	 * Returns property value.
	 * 
	 * @param name property name
	 * @return value of the property
	 */
	public String getProperty(String name) {
		return p.getProperty(name);
	}
	
	/**
	 * Returns property value.
	 * 
	 * @param name property name
	 * @param defaultValue default value related to the property name
	 * @return value of the property
	 */
	public String getProperty(String name, String defaultValue) {
		return p.getProperty(name, defaultValue);
	}
    
	/**
	 * Returns a string representation of the plugin.
	 */
    public String toString() {
        return p.toString();
    }
}
