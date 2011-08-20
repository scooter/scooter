/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.web.route;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;

import com.scooterframework.admin.PropertyFileChangeMonitor;
import com.scooterframework.admin.PropertyReader;
import com.scooterframework.common.logging.LogUtil;
import com.scooterframework.common.util.Converters;
import com.scooterframework.common.util.PropertyFileUtil;
import com.scooterframework.orm.sqldataexpress.config.DatabaseConfig;


/**
 * RouteConfig class configs the web application during startup time.
 * 
 * @author (Fei) John Chen
 */
public class RouteConfig implements Observer {
    private static LogUtil log = LogUtil.getLogger(RouteConfig.class.getName());
    
    public static final String DATA_PROPERTIES_FILE = "routes.properties";
    
    public static final String DEFAULT_VALUE_autoRest = "false";
    
    private static RouteConfig me;
    private Properties appProperties = null;
    private boolean usePluralTableName = DatabaseConfig.getInstance().usePluralTableName();
    
    static {
        try {
            me = new RouteConfig();
        }catch(Exception ex) {
            log.fatal("Error instantiating RouteConfig: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    private RouteConfig() {
        init();
        
        PropertyFileChangeMonitor.getInstance().registerObserver(this, DATA_PROPERTIES_FILE);
        
        DatabaseConfig.getInstance().addObserver(this);
    }
    
    private void init() {
        loadProperties();
        
        try {
            loadRoutes();
        }
        catch(Exception ex) {
            String errorMessage = "ERROR ERROR ERROR -- Error loading routes: " + ex.getMessage();
            log.error(errorMessage);
        }
    }
    
    private void loadProperties() {
        if (appProperties != null) appProperties.clear();
        
        appProperties = PropertyReader.loadOrderedPropertiesFromFile(DATA_PROPERTIES_FILE);
        
        if (appProperties == null) appProperties = new Properties();
    }
    
    private void loadRoutes() {
        MatchMaker.getInstance().clear();
        String nameValueSpliter = RouteConstants.PROPERTY_SYMBOL_NAMEVALUESPLITER;
        String propertyDelimiter = RouteConstants.PROPERTY_SYMBOL_PROPERTYDELIMITER;
        
        Enumeration<?> en = appProperties.keys();
        while (en.hasMoreElements()) {
            String key = (String) en.nextElement();
            if (key.startsWith("routes.name")) {
                String name = key.substring(key.lastIndexOf('.') +1);
                Properties p = PropertyFileUtil.parseNestedPropertiesFromLine(getProperty(key), nameValueSpliter, propertyDelimiter);
                NamedRoute route = new NamedRoute(name, p);
                MatchMaker.getInstance().addNamedRoute(route);
            }
            else 
            if (key.startsWith("routes.regular")) {
                String name = key.substring(key.lastIndexOf('.') +1);
                Properties p = PropertyFileUtil.parseNestedPropertiesFromLine(getProperty(key), nameValueSpliter, propertyDelimiter);
                RegularRoute route = new RegularRoute(name, p);
                MatchMaker.getInstance().addRegularRoute(route);
            }
            else 
            if (key.startsWith("routes.default")) {
                String name = key.substring(key.lastIndexOf('.') +1);
                Properties p = PropertyFileUtil.parseNestedPropertiesFromLine(getProperty(key), nameValueSpliter, propertyDelimiter);
                DefaultRoute route = new DefaultRoute(name, p);
                MatchMaker.getInstance().addDefaultRoute(route);
            }
            else 
            if (key.startsWith("resources.name")) {
                String name = key.substring(key.lastIndexOf('.') +1);
                Properties p = PropertyFileUtil.parseNestedPropertiesFromLine(getProperty(key), nameValueSpliter, propertyDelimiter);
                Resource resource = new Resource(name, Resource.PLURAL, p);
                MatchMaker.getInstance().addRestRoutes(resource.getRoutes());
                MatchMaker.getInstance().addResource(name, resource);
            }
            else 
            if (key.startsWith("resource.name")) {
                String name = key.substring(key.lastIndexOf('.') +1);
                Properties p = PropertyFileUtil.parseNestedPropertiesFromLine(getProperty(key), nameValueSpliter, propertyDelimiter);
                Resource resource = new Resource(name, Resource.SINGLE, p);
                MatchMaker.getInstance().addRestRoutes(resource.getRoutes());
                MatchMaker.getInstance().addResource(name, resource);
            }
            else 
            if (key.equals("resources.list")) {
                String resourceStr = getProperty(key);
                if (resourceStr != null) {
                    String[] resourceArray = Converters.convertStringToStringArray(resourceStr, ",");
                    int length = resourceArray.length;
                    for (int i = 0; i < length; i++) {
                        String name = resourceArray[i];
                        Resource resource = new Resource(name, Resource.PLURAL);
                        MatchMaker.getInstance().addRestRoutes(resource.getRoutes());
                        MatchMaker.getInstance().addResource(name, resource);
                    }
                }
            }
            else 
            if (key.equals("resource.list")) {
                String resourceStr = getProperty(key);
                if (resourceStr != null) {
                    String[] resourceArray = Converters.convertStringToStringArray(resourceStr, ",");
                    int length = resourceArray.length;
                    for (int i = 0; i < length; i++) {
                        String name = resourceArray[i];
                        Resource resource = new Resource(name, Resource.SINGLE);
                        MatchMaker.getInstance().addRestRoutes(resource.getRoutes());
                        MatchMaker.getInstance().addResource(name, resource);
                    }
                }
            }
            else 
            if (key.equals("routes.root")) {
                String rootRoute = getProperty(key);
                if (rootRoute != null) {
                    if (rootRoute.indexOf(nameValueSpliter) != -1) {
                        Properties p = PropertyFileUtil.parseNestedPropertiesFromLine(rootRoute, nameValueSpliter, propertyDelimiter);
                        RootRoute route = new RootRoute("root", p);
                        MatchMaker.getInstance().setRootRoute(route);
                    }
                    else {
                        String name = rootRoute;
                        NamedRoute nroute = MatchMaker.getInstance().getNamedRoute(name);
                        if (nroute == null) {
                            throw new IllegalArgumentException("A route named \"" + name + "\" must be defined before it can be used as a root route.");
                        }
                        RootRoute route = new RootRoute(name, nroute);
                        MatchMaker.getInstance().setRootRoute(route);
                    }
                }
            }
        }
        
        //display routes
        if (log.isDebugEnabled()) {
            List<Route> allRoutes = MatchMaker.getInstance().getAllRoutes();
            Iterator<Route> it = allRoutes.iterator();
            while(it.hasNext()) {
                Route route = it.next();
                log.debug("========== route type: " + route.getRouteType() + ", name: " + route.getName());
                log.debug("==> " + route);
                log.debug("");
            }
        }
        
        log.info("total routes = " + MatchMaker.getInstance().countRoutes());
    }

    public static synchronized RouteConfig getInstance() {
        return me;
    }
    
    public void update(Observable o, Object arg) {
        if (o instanceof DatabaseConfig) {
            boolean up = DatabaseConfig.getInstance().usePluralTableName();
            if (usePluralTableName != up) {
                init();
                usePluralTableName = up;
            }
        }
        else {
            init();
        }
    }
    
    /**
     * Returns all properties. 
     */
    public Properties getProperties() {
        return appProperties;
    }
    
    /**
     * Returns a String property corresponding to a key.
     */
    public String getProperty(String key) {
        return appProperties.getProperty(key);
    }
    
    /**
     * Returns a String property corresponding to a key. The method returns the
     * default value argument if the property is not found.
     */
    public String getProperty(String key, String defaultValue) {
        return appProperties.getProperty(key, defaultValue);
    }
    
    /**
     * Checks if automatically generating restful routes with default built-in 
     * controller class.
     */
    public boolean allowAutoREST() {
        String autoRest = getProperty("auto.rest", DEFAULT_VALUE_autoRest);
        return ("true".equalsIgnoreCase(autoRest))?true:false;
    }
}
