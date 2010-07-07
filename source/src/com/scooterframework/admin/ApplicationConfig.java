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
import com.scooterframework.autoloader.ClassManager;
import com.scooterframework.autoloader.FileMonitor;
import com.scooterframework.cache.CacheProviderFactory;
import com.scooterframework.common.logging.LogConfig;
import com.scooterframework.common.logging.LogUtil;
import com.scooterframework.common.util.FileUtil;
import com.scooterframework.i18n.I18nConfig;
import com.scooterframework.orm.activerecord.ReferenceDataLoader;
import com.scooterframework.orm.sqldataexpress.config.DatabaseConfig;
import com.scooterframework.orm.sqldataexpress.config.SqlConfig;
import com.scooterframework.web.controller.ActionContext;

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
 * calling application. </p>
 * 
 * <p>
 * In the <tt>ORM</tt> mode, application's file structure does not need to 
 * follow Scooter's file structure. This is good for those applications that 
 * want to include Scooter in their own data access layers.
 * </p>
 * 
 * <p>See sample standalone application <tt>scooter-orm</tt> for more details.</p>
 * 
 * <p>
 * For either <tt>WEB</tt> or <tt>APP</tt> mode, application root path is 
 * required. It is set by  <tt>WebApplicationStartListener</tt> for web 
 * application or automatically detected for non-web application. 
 * </p>
 * 
 * <p>For example, it the application is located in <tt>c:\>project\petclinic</tt>, 
 * then the application root path would be '<tt>c:\>project\petclinic</tt>'.
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
    protected static LogUtil log = null;
    public static boolean noConsoleDisplay = false;
    public static final String SYSTEM_KEY_CLASSFILE = "class.file.location";
    public static final String SYSTEM_KEY_PROPERTYFILE = "property.file.location";
    public static final String SYSTEM_KEY_SOURCEFILE = "source.file.location";
    public static final String SYSTEM_KEY_REFERENCEFILE = "reference.file.location";
    
    private static ApplicationConfig me;
    private String configuredMode;
    
    private ApplicationConfig(String configuredMode, String applicationRootPath, String contextName) {
        this(configuredMode, "", applicationRootPath, contextName);
    }
    
    private ApplicationConfig(String configuredMode, String realPath, String applicationRootPath, String contextName) {
        if (applicationRootPath == null) {
            throw new IllegalArgumentException("Application root path cannot be null.");
        }
        
        this.applicationRootPath = applicationRootPath;
        this.contextName = contextName;
        this.configuredMode = configuredMode;
        this.realPath = realPath;
        
    	String appLogs = System.getProperty("app.logs");
    	if (appLogs == null) {
    		appLogs = applicationRootPath + File.separator + "logs";
    		System.setProperty("app.logs", appLogs);
    	}
        
        log("Initializing " + configuredMode + " application ... ");
        
        if (Constants.CONFIGURED_MODE_SCOOTER_WEB.equals(configuredMode)) {
            log("           context name: " + contextName);
            log("              root path: " + applicationRootPath);
            initializeWeb();
        }
        else if (Constants.CONFIGURED_MODE_SCOOTER_APP.equals(configuredMode)) {
            log("           context name: " + contextName);
            log("              root path: " + applicationRootPath);
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
    
    
    /**
     * Returns an instance of ApplicationConfig for web application. 
     * 
     * This method should be called the first time the application is 
     * accessed or by a start-up method of a web application. 
     */
    public static ApplicationConfig configInstanceForWeb(String realPath, 
    		String applicationRootPath, String contextName) {
        if (me == null) {
            me = new ApplicationConfig(Constants.CONFIGURED_MODE_SCOOTER_WEB, 
            		realPath, applicationRootPath, contextName);
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
    public static ApplicationConfig configInstanceForApp() {
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
    public static ApplicationConfig configInstanceForOrmAlone() {
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
    public static synchronized ApplicationConfig getInstance() {
        if (me == null) {
            throw new IllegalArgumentException("You should use either " + 
            "'getInstanceForWeb()' or 'getInstanceForApp()' or " + 
            "'getInstanceForOrmAlone()' method before " + 
            "using this 'getInstance()' method.");
        }
        return me;
    }
    
    public void startORMApplication() {
        if (applicationStarted) return;
		applicationStartTime = System.currentTimeMillis();
        
		LogUtil.enableLogger();
		EnvConfig wc = EnvConfig.getInstance();
		DatabaseConfig dbc = DatabaseConfig.getInstance();
        SqlConfig.getInstance();
        
        //
        //store some important information about the server
        //
        Map props = new HashMap();
        props.put(Constants.APP_KEY_JAVA_VERSION, System.getProperty("java.version"));
        
        props.put(Constants.APP_KEY_SCOOTER_VERSION, Version.CURRENT_VERSION);
        Constants.SCOOTER_VERSION = Version.CURRENT_VERSION;
        
        String runningEnv = "";
        if (wc != null) {
            runningEnv = wc.getRunningEnvironment();
            props.put(Constants.APP_KEY_RUNNING_ENVIRONMENT, runningEnv);
            Constants.RUNNING_ENVIRONMENT = runningEnv;
        }
        
        props.put(Constants.APP_KEY_APPLICATION_START_TIME, new Date(applicationStartTime));
        props.put(Constants.APP_KEY_APPLICATION_ROOT_PATH, applicationRootPath);
        props.put(Constants.APP_KEY_APPLICATION_CONTEXT_NAME, contextName);
        props.put(Constants.APP_KEY_APPLICATION_DATABASE_NAME, dbc.getDefaultDatabaseConnectionName());
        
        ActionContext.storeToGlobal(Constants.APP_KEY_SCOOTER_PROPERTIES, props);
        
        applicationStarted = true;
    }
    
    public void startApplication() {
        if (applicationStarted) return;
		applicationStartTime = System.currentTimeMillis();
        
        PropertyFileChangeMonitor.getInstance();
        
        //need to do the following:
        logConfig.enableMonitoring();
        
        EnvConfig wc = EnvConfig.getInstance();
        
        AutoLoaderConfig.getInstance();
        
        DatabaseConfig dbc = DatabaseConfig.getInstance();
        SqlConfig.getInstance();
        
        if (!ReferenceDataLoader.isStarted()) {
            rdLoader = new ReferenceDataLoader();
            rdLoader.start();
        }
        
        I18nConfig.getInstance();
        
        ClassManager.getInstance();
        
        //
        //store some important information about the server
        //
        Map props = new HashMap();
        props.put(Constants.APP_KEY_JAVA_VERSION, System.getProperty("java.version"));
        
        props.put(Constants.APP_KEY_SCOOTER_VERSION, Version.CURRENT_VERSION);
        Constants.SCOOTER_VERSION = Version.CURRENT_VERSION;
        
        String runningEnv = "";
        if (wc != null) {
            runningEnv = wc.getRunningEnvironment();
            props.put(Constants.APP_KEY_RUNNING_ENVIRONMENT, runningEnv);
            Constants.RUNNING_ENVIRONMENT = runningEnv;
        }
        
        props.put(Constants.APP_KEY_APPLICATION_START_TIME, new Date(applicationStartTime));
        props.put(Constants.APP_KEY_APPLICATION_ROOT_PATH, applicationRootPath);
        props.put(Constants.APP_KEY_APPLICATION_CONTEXT_NAME, contextName);
        props.put(Constants.APP_KEY_APPLICATION_DATABASE_NAME, dbc.getDefaultDatabaseConnectionName());
        
        ActionContext.storeToGlobal(Constants.APP_KEY_SCOOTER_PROPERTIES, props);
        
        applicationStarted = true;
    }
    
    public void endApplication() {
        if (rdLoader != null && ReferenceDataLoader.isStarted()) {
            rdLoader.stop();
        }
        
        PropertyFileChangeMonitor.getInstance().stop();
        
        //started by ClassManager
        FileMonitor.getInstance().stop();
        
        //started by I18nConfig
        DirChangeMonitor.getInstance().stop();
        
        DatabaseConfig.getInstance().destroy();
        
        CacheProviderFactory.getInstance().shutDown();
        
        applicationStarted = false;
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
    
    public long getApplicationStartTime() {
        return applicationStartTime;
    }
    
    public String getApplicationRootPath() {
        return applicationRootPath;
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
        webappLibPath = realPath 
							+ File.separatorChar + "WEB-INF" 
							+ File.separatorChar + "lib";
        
        String cfl = System.getProperty(SYSTEM_KEY_CLASSFILE, "");
        if ("".equals(cfl)) {
            cfl = realPath 
							+ File.separatorChar + "WEB-INF" 
							+ File.separatorChar + "classes";
        }
        classFileLocationPath = cfl;
        
        String pfl = System.getProperty(SYSTEM_KEY_PROPERTYFILE, "");
        if ("".equals(pfl)) {
            pfl = realPath 
							+ File.separatorChar + "WEB-INF" 
							+ File.separatorChar + "config";
        }
        propertyFileLocationPath = pfl;
        
        String sfl = System.getProperty(SYSTEM_KEY_SOURCEFILE, "");
        if ("".equals(sfl)) {
            sfl = realPath 
				            + File.separatorChar + "WEB-INF" 
				            + File.separatorChar + "src";
        }
        sourceFileLocationPath = sfl;
        
        String rfl = System.getProperty(SYSTEM_KEY_REFERENCEFILE, "");
        if ("".equals(rfl)) {
            rfl = System.getProperty("scooter.home") + File.separatorChar + "lib";
            if (!FileUtil.pathExistAndHasFiles(rfl)) {
                rfl = realPath 
							+ File.separatorChar + "WEB-INF" 
							+ File.separatorChar + "lib";
            }
        }
        referencesLibPath = rfl;
        
        log("    " + SYSTEM_KEY_CLASSFILE + ": " + classFileLocationPath);
        log(" " + SYSTEM_KEY_PROPERTYFILE + ": " + propertyFileLocationPath);
        log("   " + SYSTEM_KEY_SOURCEFILE + ": " + sourceFileLocationPath);
        log("" + SYSTEM_KEY_REFERENCEFILE + ": " + referencesLibPath);
        
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
        webappLibPath = applicationRootPath + File.separatorChar + "lib";
        
        String cfl = System.getProperty(SYSTEM_KEY_CLASSFILE, "");
        if ("".equals(cfl)) {
            cfl = applicationRootPath + File.separatorChar + "build" 
                                      + File.separatorChar + "classes";
        }
        classFileLocationPath = cfl;
        
        String pfl = System.getProperty(SYSTEM_KEY_PROPERTYFILE, "");
        if ("".equals(pfl)) {
            pfl = applicationRootPath + File.separatorChar + "source" 
            						  + File.separatorChar + "test" 
            						  + File.separatorChar + "config";
        }
        propertyFileLocationPath = pfl;
        
        String sfl = System.getProperty(SYSTEM_KEY_SOURCEFILE, "");
        if ("".equals(sfl)) {
            sfl = applicationRootPath + File.separatorChar + "source" 
            						  + File.separatorChar + "src";
        }
        sourceFileLocationPath = sfl;
        
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
            log("ERROR ERROR ERROR => There are jar files in the lib location.");
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
    private String realPath;
    private String applicationRootPath;
    private String contextName;
    private String classFileLocationPath;
    private String propertyFileLocationPath;
    private String sourceFileLocationPath;
    private String webappLibPath;
    private String referencesLibPath;
    private ReferenceDataLoader rdLoader = null;
}
