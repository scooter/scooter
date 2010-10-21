/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.admin;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.scooterframework.common.logging.LogUtil;
import com.scooterframework.common.util.ObjectFactory;

/**
 * PluginManager class manages all plugins. 
 * 
 * @author (Fei) John Chen
 */
public class PluginManager {
	
	private static PluginManager pm;
	
	static {
		pm = new PluginManager();
	}

    private Map pluginConfigMap = Collections.synchronizedMap(new HashMap());
    private Map pluginMap = Collections.synchronizedMap(new HashMap());
    private LogUtil log = LogUtil.getLogger(getClass().getName());
	
	private PluginManager() {
		;
	}
	
	/**
	 * Returns an instance of PluginManager.
	 */
	public static PluginManager getInstance() {
		return pm;
	}
	
	/**
	 * Registers a plugin.
	 * 
	 * @param pluginName plugin name
	 * @param p          properties of the plugin
	 */
	public void registerPlugin(String pluginName, Properties p) {
		if (pluginConfigMap.containsKey(pluginName))
			throw new IllegalArgumentException("The plugin named " + pluginName
					+ " is already registered.");
		pluginConfigMap.put(pluginName, p);
	}

    /**
     * Returns plugin names
     */
    public Iterator getPluginNames() {
        return pluginConfigMap.keySet().iterator();
    }
	
	/**
	 * Returns properties of the plugin.
	 * 
	 * @param pluginName plugin name
	 * @return Properties
	 */
	public Properties getPluginProperties(String pluginName) {
		Properties p = (Properties)pluginConfigMap.get(pluginName);
		return (p != null)?p:(new Properties());
	}
	
	/**
	 * Returns an instance of plugin related to the plugin name.
	 * 
	 * @param pluginName plugin name
	 * @return an instance of plugin
	 */
	public Plugin getPlugin(String pluginName) {
		return (Plugin)pluginMap.get(pluginName);
	}
	
	/**
	 * Starts all plugins.
	 */
	public void startPlugins() {
		Set set = pluginConfigMap.keySet();
		synchronized (pluginConfigMap) {
			Iterator it = set.iterator();
			while (it.hasNext()) {
				String pluginName = (String)it.next();
				Properties p = (Properties)pluginConfigMap.get(pluginName);
				try {
					Plugin plugin = createPlugin(pluginName, p);
					pluginMap.put(pluginName, plugin);
					plugin.start();
				}
				catch(Exception ex) {
					ex.printStackTrace();
					log.error(ex);
				}
			}
		}
	}
	
	/**
	 * Stops all plugins.
	 */
	public void stopPlugins() {
		synchronized (pluginMap) {
			Iterator it = pluginMap.keySet().iterator();
			while (it.hasNext()) {
				Plugin plugin = (Plugin) pluginMap.get(it.next());
				plugin.stop();
			}
		}
	}
	
	/**
	 * Removes all plugins.
	 */
	public void removePlugins() {
		pluginMap.clear();
		pluginConfigMap.clear();
	}
	
	private Plugin createPlugin(String name, Properties p) {
		if (p == null) 
			throw new IllegalArgumentException(
					"There is no properties for plugin with name \"" + name + 
					"\" defined in properties file.");
		String pluginClassName = p.getProperty(Plugin.KEY_PLUGIN_CLASS_NAME);
		if (pluginClassName == null) 
			throw new IllegalArgumentException(
					"There must be a plugin class name defined for plugin \"" + name + "\".");

        Class[] parameterTypes = {Properties.class};
        Object[] initargs = {p};
		Plugin plugin = (Plugin)ObjectFactory.getFactory().newInstance(pluginClassName, parameterTypes, initargs);
		return plugin;
	}
}
