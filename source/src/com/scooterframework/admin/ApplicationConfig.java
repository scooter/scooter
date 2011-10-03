/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.admin;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.scooterframework.autoloader.AutoLoaderConfig;
import com.scooterframework.autoloader.ClassWorkHelper;
import com.scooterframework.autoloader.FileMonitor;
import com.scooterframework.common.logging.LogConfig;
import com.scooterframework.common.logging.LogUtil;
import com.scooterframework.common.util.FileUtil;
import com.scooterframework.i18n.I18nConfig;
import com.scooterframework.orm.activerecord.ReferenceDataLoader;
import com.scooterframework.orm.sqldataexpress.config.DatabaseConfig;
import com.scooterframework.orm.sqldataexpress.config.SqlConfig;
import com.scooterframework.web.controller.ActionContext;
import com.scooterframework.web.route.RouteConfig;

/**
 * <p>
 * ApplicationConfig class configures an application in one of the three modes.
 * <ol>
 *   <li>WEB</li>
 *   <li>APP</li>
 *   <li>ORM</li>
 * </ol>
 * </p>
 * 
 * <p>
 * In the <tt>WEB</tt> mode, the application runs in a web container. Its file
 * structure follows Scooter's file structure, unless you reset locations of 
 * some files as specified below.
 * </p>
 * 
 * <p>
 * In the <tt>APP</tt> mode, the application runs outside of a web container. 
 * Its file structure follows Scooter's file structure, unless you reset 
 * locations of some files as specified below. 
 * Scooter's JUnit test uses this mode.
 * </p>
 * 
 * <p>
 * In the <tt>ORM</tt> mode, no web container is necessary to use scooter. 
 * Any application can use Scooter's Active Record or SQL Data Express (SDE) 
 * capability in their data access layer. This can be achieved by just using 
 * scooter.jar file and some config property files (<tt>database.properties</tt>, 
 * <tt>environment.properties</tt>, <tt>log4j.properties</tt>, and 
 * <tt>sql.properties</tt>). The config files must be on the classpath of the
 * running application. </p>
 * 
 * <p>
 * In the <tt>ORM</tt> mode, application's file structure does not need to 
 * follow Scooter's file structure. This is good for those applications that 
 * want to include Scooter in their own data access layers.
 * </p>
 * 
 * <p>See sample stand-alone application <tt>scooter-orm</tt> for more details.</p>
 * 
 * <p>
 * For either <tt>WEB</tt> or <tt>APP</tt> mode, application path is 
 * required. It is automatically set by <tt>WebApplicationStartListener</tt> 
 * for web application or detected for non-web application. 
 * </p>
 * 
 * <p>For example, it the application is located in 
 * <tt>c:\>project\petclinic</tt>, then the application path would 
 * be '<tt>c:\>project\petclinic</tt>'.
 * </p>
 * 
 * <p>In either <tt>WEB</tt> or <tt>APP</tt> mode, the following properties can 
 * be set through System property from command line. </p>
 * 
 * <pre>
 * Configurable System Properties and corresponding default values for web application:
 *         class.file.location => {app.path}/WEB-INF/classes
 *      property.file.location => {app.path}/WEB-INF/config
 *        source.file.location => {app.path}/WEB-INF/src
 *     reference.file.location => {scooter.home}/lib
 * </pre>
 * 
 * @author (Fei) John Chen
 */
public class ApplicationConfig {
    private static LogUtil log = null;
    public static boolean noConsoleDisplay = false;
    public static final String SYSTEM_KEY_CLASSFILE = "class.file.location";
    public static final String SYSTEM_KEY_PROPERTYFILE = "property.file.location";
    public static final String SYSTEM_KEY_SOURCEFILE = "source.file.location";
    public static final String SYSTEM_KEY_PLUGINFILE = "plugin.file.location";
    public static final String SYSTEM_KEY_REFERENCEFILE = "reference.file.location";
    
    private static volatile ApplicationConfig me;
    private String configuredMode;
    private String runningEnvironment = Constants.RUNNING_ENVIRONMENT_DEVELOPMENT;
    
    private ApplicationConfig(String configuredMode, String appPath, String contextName) {
        if (appPath == null) {
            throw new IllegalArgumentException("Application path cannot be null.");
        }
        
        this.applicationPath = appPath;
        this.contextName = contextName;
        this.configuredMode = configuredMode;
        
        log("Initializing " + configuredMode + " application ... ");
        
        if (Constants.CONFIGURED_MODE_SCOOTER_WEB.equals(configuredMode)) {
            log("           context name: " + contextName);
            log("               app path: " + applicationPath);
            initializeWeb();
        }
        else if (Constants.CONFIGURED_MODE_SCOOTER_APP.equals(configuredMode)) {
            log("           context name: " + contextName);
            log("               app path: " + applicationPath);
        	initializeApp();
        }
        else if (Constants.CONFIGURED_MODE_SCOOTER_ORM.equals(configuredMode)) {
        	initializeORM();
        }
        else {
            throw new IllegalArgumentException("Configured mode \"" + 
            		configuredMode + "\" is not supported");
        }
    }
	
	private void log(String s) {
		if (!noConsoleDisplay) System.out.println(s);
	}
	
	private void initializePlugins() {
		PluginManager.getInstance().startPlugins();
	}
    
    
    /**
     * Returns an instance of ApplicationConfig for web application. 
     * 
     * This method should be called the first time the application is 
     * accessed or by a start-up method of a web application. 
     */
    public static synchronized ApplicationConfig configInstanceForWeb(
    		String webappPath, String contextName) {
        if (me == null) {
            me = new ApplicationConfig(Constants.CONFIGURED_MODE_SCOOTER_WEB, 
            		webappPath, contextName);
        }
        return me;
    }
    
    /**
     * Returns an instance of ApplicationConfig for non-web application based 
     * on Scooter's directory structure. 
     * 
     * This method should be called in the application start program. 
     * Subsequent use of ApplicationConfig can then use 
     * <tt>ApplicationConfig.getInstance()</tt>.
     * 
     * <p>This method assumes the current directory as the application's root 
     * directory. 
     * </p>
     */
    public static synchronized ApplicationConfig configInstanceForApp() {
        if (me == null) {
            String path = detectRootPath();
            me = new ApplicationConfig(Constants.CONFIGURED_MODE_SCOOTER_APP, 
            		path, detectContextName(path));
        }
        return me;
    }
    
    /**
     * Returns an instance of ApplicationConfig for non-web application using 
     * only Scooter's ORM. 
     * 
     * This method should be called in the application start program. 
     * Subsequent use of ApplicationConfig can then use 
     * <tt>ApplicationConfig.getInstance()</tt>.
     * 
     * <p>This method assumes that Scooter's config files are on classpath of
     * the calling application.
     * </p>
     */
    public static synchronized ApplicationConfig configInstanceForOrmAlone() {
        if (me == null) {
            String path = detectRootPath();
            me = new ApplicationConfig(Constants.CONFIGURED_MODE_SCOOTER_ORM, 
            		path, detectContextName(path));
        }
        return me;
    }
    
    /**
     * Returns an instance of ApplicationConfig. You 
     * should call either <tt>getInstanceForWeb()</tt> or 
     * <tt>getInstanceForApp()</tt> or 
     * <tt>getInstanceForOrmAlone()</tt> method before using this 
     * <tt>getInstance()</tt> method.
     */
    public static ApplicationConfig getInstance() {
        if (me == null) {
            throw new IllegalArgumentException("You should use either " + 
            "'getInstanceForWeb()' or 'getInstanceForApp()' or " + 
            "'getInstanceForOrmAlone()' method before " + 
            "using this 'getInstance()' method. If this is a web app, check your context path.");
        }
        
        return me;
    }
    
    public void startORMApplication() {
        if (applicationStarted) return;
		applicationStartTime = System.currentTimeMillis();
		
		configInstanceForOrmAlone();
        
		LogUtil.enableLogger();
		
		try {
			EnvConfig.getInstance();
		}
		catch(NoClassDefFoundError er) {
			if (!"org/apache/commons/fileupload/FileItemFactory".equals(er.getMessage())) {
				log.error("Error in EnvConfig.getInstance()", er);
			}
		}
		DatabaseConfig dbc = DatabaseConfig.getInstance();
        SqlConfig.getInstance();
        
        //
        //store some important information about the server
        //
        Map<String, Object> props = new HashMap<String, Object>();
        props.put(Constants.APP_KEY_JAVA_VERSION, System.getProperty("java.version"));
        
        props.put(Constants.APP_KEY_SCOOTER_VERSION, Version.CURRENT_VERSION);
        
        props.put(Constants.APP_KEY_RUNNING_ENVIRONMENT, runningEnvironment);
        props.put(Constants.APP_KEY_APPLICATION_START_TIME, new Date(applicationStartTime));
        props.put(Constants.APP_KEY_APPLICATION_ROOT_PATH, applicationPath);
        props.put(Constants.APP_KEY_APPLICATION_CONTEXT_NAME, contextName);
        props.put(Constants.APP_KEY_APPLICATION_DATABASE_NAME, dbc.getDefaultDatabaseConnectionName());
        
        ActionContext.storeToGlobal(Constants.APP_KEY_SCOOTER_PROPERTIES, props);
        
        initializePlugins();
        
        applicationStarted = true;
    }
    
    public void startApplication() {
        if (applicationStarted) return;
        
		applicationStartTime = System.currentTimeMillis();
        
        PropertyFileChangeMonitor.getInstance();
        
        //need to do the following:
        logConfig.enableMonitoring();
        
        EnvConfig.getInstance();
        
        AutoLoaderConfig.getInstance();
        
        if (isInDevelopmentEnvironment() && isWebApp()) {
        	FileMonitor.getInstance().start();
        }
        
        DatabaseConfig dbc = DatabaseConfig.getInstance();
        
        SqlConfig.getInstance();
        
        if (isWebApp()) RouteConfig.getInstance();
        
        if (isWebApp() && !ReferenceDataLoader.isStarted()) {
            rdLoader = new ReferenceDataLoader();
            rdLoader.start();
        }
        
        I18nConfig.getInstance();
        
        //
        //store some important information about the server
        //
        Map<String, Object> props = new HashMap<String, Object>();
        props.put(Constants.APP_KEY_JAVA_VERSION, System.getProperty("java.version"));
        
        props.put(Constants.APP_KEY_SCOOTER_VERSION, Version.CURRENT_VERSION);
        
        props.put(Constants.APP_KEY_RUNNING_ENVIRONMENT, runningEnvironment);
        props.put(Constants.APP_KEY_APPLICATION_START_TIME, new Date(applicationStartTime));
        props.put(Constants.APP_KEY_APPLICATION_ROOT_PATH, applicationPath);
        props.put(Constants.APP_KEY_APPLICATION_CONTEXT_NAME, contextName);
        props.put(Constants.APP_KEY_APPLICATION_DATABASE_NAME, dbc.getDefaultDatabaseConnectionName());

        ActionContext.storeToGlobal(Constants.APP_KEY_SCOOTER_PROPERTIES, props);
        
        if(isApp() && "true".equals(System.getProperty(Constants.ALLOW_CLASSWORK))) {
            try {
                ClassWorkHelper.preloadClasses(sourceFileLocationPath);
			} catch (Exception ex) {
				log.error("Error in preloadClasses: " + ex.getMessage());
			}
        }
        
        initializePlugins();
        
        applicationStarted = true;
        if (log != null) {
        	log.info("Application started with Scooter version: " + Version.CURRENT_VERSION);
        }
    }
    
    public void endApplication() {
        if (rdLoader != null && ReferenceDataLoader.isStarted()) {
            rdLoader.stop();
        }
        
        PropertyFileChangeMonitor.getInstance().stop();
        
        FileMonitor.getInstance().stop();
        
        //started by I18nConfig
        DirChangeMonitor.getInstance().stop();
        
        DatabaseConfig.getInstance().destroy();
        
        applicationStarted = false;
        
        PluginManager.getInstance().stopPlugins();
        
        if (log != null) log.info("Application ended.");
    }
    
    public static String detectRootPath() {
        String path = "";
        try {
            path = (new File("")).getCanonicalPath();
        }
        catch(Exception ex) {
            String errorMessage = "Failed to detect root path from current directory: " + ex.getMessage();
            System.err.println(errorMessage);
            System.out.println("Stop initializtion process. Exit now ...");
        }
        return path;
    }
    
    /**
     * Returns number of sessions in the container.
     */
    public static int getSessionCount() {
    	return WebSessionListener.getSessionCount();
    }
    
    public long getApplicationStartTime() {
        return applicationStartTime;
    }
    
    public String getApplicationPath() {
        return applicationPath;
    }
    
    public String getContextName() {
        return contextName;
    }
    
    public boolean applicationStarted() {
        return applicationStarted;
    }
    
    public String getConfiguredMode() {
        return configuredMode;
    }
    
    /**
     * Return true if the current app is configured as a web app.
     */
    public boolean isWebApp() {
        return Constants.CONFIGURED_MODE_SCOOTER_WEB.equals(configuredMode);
    }
    
    /**
     * Return true if the current app is configured as a regular app.
     */
    public boolean isApp() {
        return Constants.CONFIGURED_MODE_SCOOTER_APP.equals(configuredMode);
    }
    
    /**
     * Return true if the current app is configured to use orm alone.
     */
    public boolean isOrmAlone() {
        return Constants.CONFIGURED_MODE_SCOOTER_ORM.equals(configuredMode);
    }

    /**
     * Returns running environment
     */
    public String getRunningEnvironment() {
        return runningEnvironment;
    }
    
    public void setRunningEnvironment(String runningEnvironment) {
    	this.runningEnvironment = runningEnvironment;
    }

    /**
     * Checks if the current running environment is development environment.
     *
     * @return true if the current running environment is development.
     */
    public boolean isInDevelopmentEnvironment() {
        return Constants.RUNNING_ENVIRONMENT_DEVELOPMENT.equals(runningEnvironment);
    }
    
    public String getClassFileLocationPath() {
        return classFileLocationPath;
    }
    
    public String getPropertyFileLocationPath() {
        return propertyFileLocationPath;
    }
    
    public String getSourceFileLocationPath() {
        return sourceFileLocationPath;
    }
    
    public String getWebappLibPath() {
        return webappLibPath;
    }
    
    public String getPluginsPath() {
        return pluginsPath;
    }
    
    public String getReferencesLibPath() {
        return referencesLibPath;
    }
    
    public void setClassFileLocationPath(String classFileLocationPath) {
        this.classFileLocationPath = classFileLocationPath;
    }
    
    public void setPropertyFileLocationPath(String propertyFileLocationPath) {
        this.propertyFileLocationPath = propertyFileLocationPath;
    }
    
    public void setSourceFileLocationPath(String sourceFileLocationPath) {
        this.sourceFileLocationPath = sourceFileLocationPath;
    }
    
    private void initializeWeb() {
    	webappLibPath = applicationPath 
							+ File.separatorChar + "WEB-INF" 
							+ File.separatorChar + "lib";
        
        String cfl = System.getProperty(SYSTEM_KEY_CLASSFILE, "");
        if ("".equals(cfl)) {
            cfl = applicationPath 
							+ File.separatorChar + "WEB-INF" 
							+ File.separatorChar + "classes";
        }
        classFileLocationPath = cfl;
        
        String pfl = System.getProperty(SYSTEM_KEY_PROPERTYFILE, "");
        if ("".equals(pfl)) {
            pfl = applicationPath 
							+ File.separatorChar + "WEB-INF" 
							+ File.separatorChar + "config";
        }
        propertyFileLocationPath = pfl;
        
        String sfl = System.getProperty(SYSTEM_KEY_SOURCEFILE, "");
        if ("".equals(sfl)) {
            String srcPath = applicationPath 
				            + File.separatorChar + "WEB-INF" 
				            + File.separatorChar + "src";
            
            String unitTestPath = applicationPath 
				            + File.separatorChar + "WEB-INF" 
				            + File.separatorChar + "test" 
				            + File.separatorChar + "unit";
            
            String functionalTestPath = applicationPath 
				            + File.separatorChar + "WEB-INF" 
				            + File.separatorChar + "test" 
				            + File.separatorChar + "functional";
            
            sfl = srcPath + File.pathSeparatorChar + unitTestPath 
            				+ File.pathSeparatorChar + functionalTestPath;
        }
        sourceFileLocationPath = sfl;
        
        String pgl = System.getProperty(SYSTEM_KEY_PLUGINFILE, "");
        if ("".equals(pgl)) {
        	String sh = System.getProperty("scooter.home");
        	if (sh != null) {
            	pgl = sh + File.separatorChar + "plugins";
        	}
        }
        pluginsPath = pgl;
        
        String rfl = System.getProperty(SYSTEM_KEY_REFERENCEFILE, "");
        if ("".equals(rfl)) {
            rfl = System.getProperty("scooter.home") + File.separatorChar + "lib";
            if (!FileUtil.pathExistAndHasFiles(rfl)) {
                rfl = applicationPath 
							+ File.separatorChar + "WEB-INF" 
							+ File.separatorChar + "lib";
            }
        }
        referencesLibPath = rfl;
        
        log("    " + SYSTEM_KEY_CLASSFILE + ": " + classFileLocationPath);
        log(" " + SYSTEM_KEY_PROPERTYFILE + ": " + propertyFileLocationPath);
        log("   " + SYSTEM_KEY_SOURCEFILE + ": " + sourceFileLocationPath);
        log("" + SYSTEM_KEY_REFERENCEFILE + ": " + referencesLibPath);
        
        if (pluginsPath != null  && !"".equals(pluginsPath)) {
            log("   " + SYSTEM_KEY_PLUGINFILE + ": " + pluginsPath);
        }
        else {
        	log("INFO: If jars under scooter/plugins are needed, use -Dscooter.home=... to setup path to the plugin directory.");
        }
        
        //make sure there are files in the property file location
        String[] fileNames = (new File(propertyFileLocationPath)).list();
        if (fileNames == null || fileNames.length == 0) {
            log("ERROR ERROR ERROR => There are no property files in the property file location.");
            log("Stop initializtion process. Exit now ...");
        }
        
        //initialized other stuff if not started
        logConfig = LogConfig.getInstance(propertyFileLocationPath);
        log = LogUtil.getLogger(ApplicationConfig.class.getName());
        
        
    }
    
    private void initializeApp() {
    	webappLibPath = applicationPath + File.separatorChar + "lib";
        
        String cfl = System.getProperty(SYSTEM_KEY_CLASSFILE, "");
        if ("".equals(cfl)) {
            cfl = applicationPath + File.separatorChar + "build" 
                                      + File.separatorChar + "classes";
        }
        classFileLocationPath = cfl;
        
        String pfl = System.getProperty(SYSTEM_KEY_PROPERTYFILE, "");
        if ("".equals(pfl)) {
            pfl = applicationPath + File.separatorChar + "source" 
            					  + File.separatorChar + "test" 
            					  + File.separatorChar + "config";
        }
        propertyFileLocationPath = pfl;
        
        String sfl = System.getProperty(SYSTEM_KEY_SOURCEFILE, "");
        if ("".equals(sfl)) {
            sfl = applicationPath + File.separatorChar + "source" 
            					  + File.separatorChar + "test";
        }
        sourceFileLocationPath = sfl;
        
        String pgl = System.getProperty(SYSTEM_KEY_PLUGINFILE, "");
        if ("".equals(pgl)) {
        	pgl = System.getProperty("scooter.home") + File.separatorChar + "plugins";
        }
        pluginsPath = pgl;
        
        String rfl = System.getProperty(SYSTEM_KEY_REFERENCEFILE, "");
        if ("".equals(rfl)) {
            rfl = webappLibPath;
        }
        referencesLibPath = rfl;
        
        //log("    " + SYSTEM_KEY_CLASSFILE + ": " + classFileLocationPath);
        log(" " + SYSTEM_KEY_PROPERTYFILE + ": " + propertyFileLocationPath);
        //log("   " + SYSTEM_KEY_SOURCEFILE + ": " + sourceFileLocationPath);
        log("" + SYSTEM_KEY_REFERENCEFILE + ": " + referencesLibPath);
        
        //make sure there are files in the property file location
        String[] fileNames = (new File(propertyFileLocationPath)).list();
        if (fileNames == null || fileNames.length == 0) {
            log("ERROR ERROR ERROR => There are no property files in the property file location.");
            log("Stop initializtion process. Exit now ...");
        }
        
        if (!FileUtil.pathExistAndHasFiles(referencesLibPath)) {
            log("ERROR ERROR ERROR => There are no jar files in the lib location.");
            log("Stop initializtion process. Exit now ...");
        }
        
        //initialized other stuff if not started
        logConfig = LogConfig.getInstance(propertyFileLocationPath);
        log = LogUtil.getLogger(ApplicationConfig.class.getName());
    }
    
    private void initializeORM() {
        //initialized other stuff if not started
        log = LogUtil.getLogger(ApplicationConfig.class.getName());
        LogUtil.enableLogger();
    }
    
    private static String detectContextName(String path) {
        if (path.endsWith(File.separator)) path = path.substring(0, path.length() -1);
        return path.substring(path.lastIndexOf(File.separatorChar) + 1);
    }
    
    private LogConfig logConfig;
    private long applicationStartTime = 0L;
    private boolean applicationStarted;
    private String applicationPath;
    private String contextName;
    private String classFileLocationPath;
    private String propertyFileLocationPath;
    private String sourceFileLocationPath;
    private String webappLibPath;
    private String pluginsPath;
    private String referencesLibPath;
    private ReferenceDataLoader rdLoader = null;
}
