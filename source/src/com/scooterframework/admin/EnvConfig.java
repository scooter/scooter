/*
 *   This software is distributed under the terms of the FSF
 *   Gnu Lesser General Public License (see lgpl.txt).
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.admin;

import java.io.File;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.scooterframework.common.logging.LogUtil;
import com.scooterframework.common.util.Converters;
import com.scooterframework.common.util.PropertyFileUtil;
import com.scooterframework.common.util.WordUtil;
import com.scooterframework.orm.activerecord.ActiveRecord;
import com.scooterframework.orm.activerecord.ActiveRecordUtil;
import com.scooterframework.orm.sqldataexpress.config.DatabaseConfig;
import com.scooterframework.web.controller.ActionContext;
import com.scooterframework.web.controller.NoViewFileException;

/**
 * EnvConfig class configures the application during startup time.
 * 
 * <p>
 * You can add more mappings by using the <tt>additional.mimetypes</tt> 
 * property in the <tt>environment.properties</tt> file. Or you can create 
 * your own content handler plugin by extending the <tt>Plugin</tt> class 
 * and implementing the <tt>ContentHandler</tt> interface.
 *
 * @author (Fei) John Chen
 */
public class EnvConfig implements Observer {
    private static LogUtil log = LogUtil.getLogger(EnvConfig.class.getName());
    
    public static final String EXTENSION_MIMETYPES_RESOURCE = "/com/scooterframework/admin/extension_mimetypes.properties";
    public static final String DATA_PROPERTIES_FILE = "environment.properties";
    public static final String DEFAULT_VALUE_defaultViewFilesDirectory = "builtin/crud";

    //****************************************************
    // Default values for keys in the properties file
    //****************************************************
    public static final String DEFAULT_VALUE_siteAdminUsername = null;
    public static final String DEFAULT_VALUE_siteAdminPassword = null;
    public static final String DEFAULT_VALUE_modelClassPrefix = "models";
    public static final String DEFAULT_VALUE_modelClassSuffix = "";
    public static final String DEFAULT_VALUE_controllerClassPrefix = "controllers";
    public static final String DEFAULT_VALUE_controllerClassSuffix = "Controller";
    public static final String DEFAULT_VALUE_allowDefaultControllerClass = "false";
    public static final String DEFAULT_VALUE_actionExtension = "";
    public static final String DEFAULT_VALUE_defaultActionMethod = "index";
    public static final String DEFAULT_VALUE_allowDefaultActionMethod = "true";
    public static final String DEFAULT_VALUE_webPageDirectoryName = "WEB-INF/views";
    public static final String DEFAULT_VALUE_allowForwardToControllerNameViewWhenControllerNotExist = "true";
    public static final String DEFAULT_VALUE_allowForwardToActionNameViewWhenActionNotExist = "true";
    public static final String DEFAULT_VALUE_runningEnvironment = Constants.RUNNING_ENVIRONMENT_DEVELOPMENT;
    public static final String DEFAULT_VALUE_passwordScheme = "";
    public static final String DEFAULT_VALUE_benchmark = "true";
    public static final String DEFAULT_VALUE_benchmarkInHeader = "true";
    public static final String DEFAULT_VALUE_locale_language = null;
    public static final String DEFAULT_VALUE_locale_country = null;
    public static final String DEFAULT_VALUE_locale_variant = null;
    public static final Locale DEFAULT_VALUE_gloabalLocale = null;
    public static final String DEFAULT_VALUE_messageResourcesFileBase = "messages";
    public static final String DEFAULT_VALUE_viewExtension = "jsp";
    public static final String DEFAULT_VALUE_rootURL = "/WEB-INF/views/index.jsp";
    public static final String DEFAULT_VALUE_errorPageURI = "/WEB-INF/views/error.jsp";
    public static final String DEFAULT_VALUE_compileErrorPageURI = "/WEB-INF/views/compileError.jsp";
    public static final String DEFAULT_VALUE_allowDisplayingErrorDetails = "true";
    public static final String DEFAULT_VALUE_allowDataBrowser = "true";
    public static final String DEFAULT_VALUE_additionalMimeTypes = "";
    public static final String DEFAULT_VALUE_additionalSinglePlural = "";
    public static final String DEFAULT_VALUE_defaultCacheProvider = null;
    public static final String DEFAULT_VALUE_upload_file_repository = null;
    public static final String DEFAULT_VALUE_maximum_bytes_per_uploaded_file_in_memory = "10240";
    public static final String DEFAULT_VALUE_maximum_total_bytes_per_upload_request = "-1";
    public static final String DEFAULT_VALUE_maximum_bytes_per_uploaded_file = "-1";


    private static EnvConfig me;
    private Properties appProperties = null;
    private static Map<String, String> defaultMimeTypesMap = new HashMap<String, String>();

    private String siteAdminUsername = DEFAULT_VALUE_siteAdminUsername;
    private String siteAdminPassword = DEFAULT_VALUE_siteAdminPassword;
    private String modelClassPrefix = DEFAULT_VALUE_modelClassPrefix;
    private String modelClassSuffix = DEFAULT_VALUE_modelClassSuffix;
    private String controllerClassPrefix = DEFAULT_VALUE_controllerClassPrefix;
    private String controllerClassSuffix = DEFAULT_VALUE_controllerClassSuffix;
    private String allowDefaultControllerClass = DEFAULT_VALUE_allowDefaultControllerClass;
    private String defaultActionMethod = DEFAULT_VALUE_defaultActionMethod;
    private String allowDefaultActionMethod = DEFAULT_VALUE_allowDefaultActionMethod;
    private String webPageDirectoryName = DEFAULT_VALUE_webPageDirectoryName;
    private String allowForwardToControllerNameViewWhenControllerNotExist = DEFAULT_VALUE_allowForwardToControllerNameViewWhenControllerNotExist;
    private String allowForwardToActionNameViewWhenActionNotExist = DEFAULT_VALUE_allowForwardToActionNameViewWhenActionNotExist;
    private String runningEnvironment = DEFAULT_VALUE_runningEnvironment;
    private String passwordScheme = DEFAULT_VALUE_passwordScheme;
    private String benchmark = DEFAULT_VALUE_benchmark;
    private String benchmarkInHeader = DEFAULT_VALUE_benchmarkInHeader;
    private Locale gloabalLocale = DEFAULT_VALUE_gloabalLocale;
    private String messageResourcesFileBase = DEFAULT_VALUE_messageResourcesFileBase;
    private String actionExtension = DEFAULT_VALUE_actionExtension;
    private String viewExtension = DEFAULT_VALUE_viewExtension;
    private String rootURL = DEFAULT_VALUE_rootURL;
    private String errorPageURI = DEFAULT_VALUE_errorPageURI;
    private String compileErrorPageURI = DEFAULT_VALUE_compileErrorPageURI;
    private String allowDisplayingErrorDetails = DEFAULT_VALUE_allowDisplayingErrorDetails;
    private String allowDataBrowser = DEFAULT_VALUE_allowDataBrowser;
    private String additionalMimeTypes = DEFAULT_VALUE_additionalMimeTypes;
    private String additionalSinglePlural = DEFAULT_VALUE_additionalSinglePlural;
    private String defaultCacheProvider = DEFAULT_VALUE_defaultCacheProvider;
    private String uploadFileRepository = DEFAULT_VALUE_upload_file_repository;
    private String maximumBytesPerUploadedFileInMemory = DEFAULT_VALUE_maximum_bytes_per_uploaded_file_in_memory;
    private String maximumTotalBytesPerUploadRequest = DEFAULT_VALUE_maximum_total_bytes_per_upload_request;
    private String maximumBytesPerUploadedFile = DEFAULT_VALUE_maximum_bytes_per_uploaded_file;

    private DiskFileItemFactory fileFactory;
    private ServletFileUpload fileUpload;

    private ConcurrentMap<String, Object> cacheProvidersMap = new ConcurrentHashMap<String, Object>();
    private ConcurrentMap<String, String> mimeTypesMap = new ConcurrentHashMap<String, String>();

    static {
        try {
        	Properties extMimeMapping = 
        		PropertyFileUtil.loadPropertiesFromResource(EXTENSION_MIMETYPES_RESOURCE);
        	for (Map.Entry<Object, Object> entry : extMimeMapping.entrySet()) {
        		Object name = entry.getKey();
        		Object value = entry.getValue();
        		if (name != null && value != null) 
				defaultMimeTypesMap.put(name.toString(), value.toString());
        	}
        	
            me = new EnvConfig();
        }catch(Exception ex) {
            log.fatal("Error instantiating EnvConfig: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private EnvConfig() {
        init();
        PropertyFileChangeMonitor.getInstance().registerObserver(this, DATA_PROPERTIES_FILE);
        
        EventsManager.getInstance().registerListener(Constants.EVENT_COMPILE, SoundPlayer.getInstance());
    }

    private void clear() {
    	mimeTypesMap.clear();
    	cacheProvidersMap.clear();
    	PluginManager.getInstance().removePlugins();
    	fileFactory = null;
    	fileUpload = null;
    }

    private void init() {
    	clear();
    	
    	mimeTypesMap.putAll(defaultMimeTypesMap);

        loadProperties();

        siteAdminUsername = getProperty("site.admin.username", DEFAULT_VALUE_siteAdminUsername);
        siteAdminPassword = getProperty("site.admin.password", DEFAULT_VALUE_siteAdminPassword);
        controllerClassPrefix = getProperty("controller.class.prefix", DEFAULT_VALUE_controllerClassPrefix);
        controllerClassSuffix = getProperty("controller.class.suffix", DEFAULT_VALUE_controllerClassSuffix);
        modelClassPrefix = getProperty("model.class.prefix", DEFAULT_VALUE_modelClassPrefix);
        modelClassSuffix = getProperty("model.class.suffix", DEFAULT_VALUE_modelClassSuffix);
        allowDefaultControllerClass = getProperty("auto.crud", DEFAULT_VALUE_allowDefaultControllerClass);
        actionExtension = getProperty("action.extension", DEFAULT_VALUE_actionExtension);
        if (!actionExtension.startsWith(".") && !"".equals(actionExtension)) actionExtension = "." + actionExtension;
        viewExtension = getProperty("view.extension", DEFAULT_VALUE_viewExtension);
        if (!viewExtension.startsWith(".") && !"".equals(viewExtension)) viewExtension = "." + viewExtension;
        defaultActionMethod = getProperty("default.action.method", DEFAULT_VALUE_defaultActionMethod);
        allowDefaultActionMethod = getProperty("allow.default.action.method", DEFAULT_VALUE_allowDefaultActionMethod);
        webPageDirectoryName = getProperty("webpage.directory.name", DEFAULT_VALUE_webPageDirectoryName);
        allowForwardToControllerNameViewWhenControllerNotExist = getProperty("allow.forward.to.controller.name.view.when.controller.not.exist", DEFAULT_VALUE_allowForwardToControllerNameViewWhenControllerNotExist);
        allowForwardToActionNameViewWhenActionNotExist = getProperty("allow.forward.to.action.name.view.when.action.not.exist", DEFAULT_VALUE_allowForwardToActionNameViewWhenActionNotExist);
        rootURL = getProperty("root.url", DEFAULT_VALUE_rootURL);
        if (!rootURL.startsWith("/")) rootURL = "/" + rootURL;

        runningEnvironment = getProperty("running.environment", DEFAULT_VALUE_runningEnvironment);
        ApplicationConfig.getInstance().setRunningEnvironment(runningEnvironment);

        passwordScheme = getProperty("password.scheme", DEFAULT_VALUE_passwordScheme);
        benchmark = getProperty("benchmark", DEFAULT_VALUE_benchmark);
        benchmarkInHeader = getProperty("benchmark.in.header", DEFAULT_VALUE_benchmarkInHeader);

        String language = getProperty("locale.language", DEFAULT_VALUE_locale_language);
        String country = getProperty("locale.country", DEFAULT_VALUE_locale_country);
        String variant = getProperty("locale.variant", DEFAULT_VALUE_locale_variant);
        if (language != null && country != null && variant != null) {
            gloabalLocale = new Locale(language, country, variant);
        }
        else if (language != null && country != null) {
            gloabalLocale = new Locale(language, country);
        }
        else if (language != null) {
            gloabalLocale = new Locale(language);
        }
        else {
            gloabalLocale = DEFAULT_VALUE_gloabalLocale;
        }
        ActionContext.setGlobalLocale(gloabalLocale);

        messageResourcesFileBase = getProperty("message.resources.file.base", DEFAULT_VALUE_messageResourcesFileBase);
        errorPageURI = getProperty("app.error.page.uri", DEFAULT_VALUE_errorPageURI);
        compileErrorPageURI = getProperty("app.compile.error.page.uri", DEFAULT_VALUE_compileErrorPageURI);
        allowDisplayingErrorDetails = getProperty("allow.displaying.error.details", DEFAULT_VALUE_allowDisplayingErrorDetails);
        allowDataBrowser = getProperty("allow.databrowser", DEFAULT_VALUE_allowDataBrowser);
        additionalMimeTypes = getProperty("additional.mimetypes", DEFAULT_VALUE_additionalMimeTypes);
        additionalSinglePlural = getProperty("additional.single.plural", DEFAULT_VALUE_additionalSinglePlural);
        
        Map<String, String> mimeTypes = Converters.convertStringToMap(additionalMimeTypes, ":", ",");
        mimeTypesMap.putAll(mimeTypes);

        Map<String, String> words = Converters.convertStringToMap(additionalSinglePlural, ":", ",");
        for (Map.Entry<String, String> entry : words.entrySet()) {
        	String value = entry.getValue();
            WordUtil.addPlural(entry.getKey(), (value != null)?value:(String)null);
        }
        
        Enumeration<Object> en = appProperties.keys();
        while (en.hasMoreElements()) {
            String key = (String) en.nextElement();
            if (key.startsWith("plugin")) {
                String name = key.substring(key.indexOf('.') + 1);
                Properties p = PropertyFileUtil.parseNestedPropertiesFromLine(getProperty(key), "=", ",");
                p.setProperty(Plugin.KEY_PLUGIN_NAME, name);
                PluginManager.getInstance().registerPlugin(name, p);
                
                if (key.startsWith("plugin.cache.provider.")) {
                	cacheProvidersMap.put(key.substring("plugin.cache.provider.".length()), p);
                }
            }
        }

        defaultCacheProvider = getProperty("default.cache.provider.name", DEFAULT_VALUE_defaultCacheProvider);
        if (defaultCacheProvider != null &&
        	!cacheProvidersMap.keySet().contains(defaultCacheProvider)) {
        	log.error("There is no definition for default cache provider " + defaultCacheProvider);
        }
        
        uploadFileRepository = getProperty("upload.file.repository", DEFAULT_VALUE_upload_file_repository);
        maximumBytesPerUploadedFileInMemory = getProperty("maximum.bytes.per.uploaded.file.in.memory", DEFAULT_VALUE_maximum_bytes_per_uploaded_file_in_memory);
        maximumTotalBytesPerUploadRequest = getProperty("maximum.total.bytes.per.upload.request", DEFAULT_VALUE_maximum_total_bytes_per_upload_request);
        maximumBytesPerUploadedFile = getProperty("maximum.bytes.per.uploaded.file", DEFAULT_VALUE_maximum_bytes_per_uploaded_file);
        configFileUpload();
    }

    private void loadProperties() {
        if (appProperties != null) appProperties.clear();

        appProperties = PropertyReader.loadPropertiesFromFile(DATA_PROPERTIES_FILE);

        if (appProperties == null) appProperties = new Properties();
    }
    
    /*
     * Configures file upload setting.
     */
    private void configFileUpload() {
    	int maxMemorySize = getMaximumBytesPerUploadedFileInMemory();
    	String uploadDirectory = getUploadFileRepository();
    	long maxRequestSize = getMaximumTotalBytesPerUploadRequest();
    	long fileSizeMax = getMaximumBytesPerUploadedFile();
    	
    	fileFactory = (uploadDirectory == null)?
    			new DiskFileItemFactory(maxMemorySize, null):
    			new DiskFileItemFactory(maxMemorySize, new File(uploadDirectory));
    	fileUpload = new ServletFileUpload(fileFactory);
    	fileUpload.setSizeMax(maxRequestSize);
    	fileUpload.setFileSizeMax(fileSizeMax);
    }
	
	private void initializePlugins() {
		PluginManager.getInstance().startPlugins();
	}

    public static EnvConfig getInstance() {
        return me;
    }

    public void update(Observable o, Object arg) {
        init();
        initializePlugins();
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
     * Returns site admin username.
     */
    public String getSiteAdminUsername() {
        return siteAdminUsername;
    }

    /**
     * Returns site admin password.
     */
    public String getSiteAdminPassword() {
        return siteAdminPassword;
    }

    /**
     * Returns action extension
     *
     * @return action extension
     */
    public String getActionExtension() {
        return actionExtension;
    }

    /**
     * Returns view extension
     *
     * @return view extension
     */
    public String getViewExtension() {
        return viewExtension;
    }

    /**
     * Returns root url.
     *
     * @return root url
     */
    public String getRootURL() {
        return rootURL;
    }

    /**
     * Returns controller class name prefix.
     */
    public String getControllerClassPrefix() {
        return controllerClassPrefix;
    }

    /**
     * Returns controller class name suffix.
     */
    public String getControllerClassSuffix() {
        return controllerClassSuffix;
    }

    /**
     * Checks if a built-in default controller class is allowed to use.
     */
    public boolean allowDefaultControllerClass() {
        return ("true".equalsIgnoreCase(allowDefaultControllerClass))?true:false;
    }

    /**
     * Alias of <tt>allowDefaultControllerClass</tt> method.
     */
    public boolean allowAutoCRUD() {
        return allowDefaultControllerClass();
    }

    /**
     * Returns default method name of a controller class.
     */
    public String getDefaultActionMethod() {
        return defaultActionMethod;
    }

    /**
     * Checks if a default action name is used when an action method is not
     * obtained from parsing a url.
     */
    public boolean allowDefaultActionMethod() {
        return ("true".equalsIgnoreCase(allowDefaultActionMethod))?true:false;
    }

    /**
     * Returns model class name prefix.
     */
    public String getModelClassPrefix() {
        return modelClassPrefix;
    }

    /**
     * Returns model class name suffix.
     */
    public String getModelClassSuffix() {
        return modelClassSuffix;
    }

    /**
     * Returns directory name which contains all web pages.
     */
    public String getWebPageDirectoryName() {
        if (webPageDirectoryName == null) return "";
        if (!"".equals(webPageDirectoryName) && !webPageDirectoryName.startsWith("/")) {
            webPageDirectoryName = "/" + webPageDirectoryName;
        }
        return webPageDirectoryName;
    }

    /**
     * Returns action URI for a specific action method.
     *
     * If there is no extension type in the action name, the action is treated
     * as an extension type defined in config properties file. For example,
     * if the action is "show", the action could be treated as "show.do" action if
     * the <tt>actionExtension</tt> property is defined as ".do".
     *
     * <pre>
     * Examples:
     *      getActionUriFor("list") => list.do (extension = .do)
     * </pre>
     * @param action the action method
     * @return action URI string
     */
    public static String getActionUriFor(String action) {
        return getActionUriFor(null, action);
    }

    /**
     * Returns action URI for a controller path with a specific action method.
     *
     * If there is no extension type in the action name, the action is treated
     * as an extension type defined in config properties file. For example,
     * if the action is "show", the action could be treated as "show.do" action if
     * the <tt>actionExtension</tt> property is defined as ".do".
     *
     * <pre>
     * Examples:
     *      getActionUriFor("/posts", "list") => /posts/list.do
     * </pre>
     * @param controllerPath path to the action
     * @param action the action method
     * @return action URI string
     */
    public static String getActionUriFor(String controllerPath, String action) {
        if (action == null || "".equals(action)) {
            if (getInstance().allowDefaultActionMethod()) {
                action = getInstance().getDefaultActionMethod();
            }
            else {
                throw new IllegalArgumentException("The value for action " +
                "input cannot be null or empty unless the default action " +
                "method is specified in property file.");
            }
        }

        if (!action.endsWith(EnvConfig.getInstance().getActionExtension())) {
            action += EnvConfig.getInstance().getActionExtension();
        }
        return (controllerPath == null || "".equals(controllerPath))?action:(controllerPath + "/" + action);
    }

    /**
     * <p>
     * Returns view URI for a controller with a specific action.</p>
     *
     * <p>
     * If there is no extension type in the view name, the view is treated
     * as an extension type defined in config properties file. For example,
     * if the view is "show", the view could be treated as "show.jsp" action if
     * the <tt>view.extension</tt> property is defined as ".jsp". The directory
     * path for view files is set by the <tt>webpage.directory.name</tt>
     * property in properties file.</p>
     *
     * <pre>
     * Examples:
     *      If your JSP web pages are under webapp:
     *      webpage.directory.name=""
     *      getViewURI("posts", "list") => /posts/list.jsp
     *
     *      If your jsp web pages are under WEB-INF/views:
     *      webpage.directory.name="WEB-INF/views" (default)
     *      getViewURI("posts", "list") => /WEB-INF/views/posts/list.jsp
     * </pre>
     * @param controller controller name
     * @param actionOrView the action method name or view name
     * @return view URI.
     */
    public static String getViewURI(String controller, String actionOrView) {
        return getViewURI(controller, actionOrView, null);
    }

    /**
     * Returns a  view URI. This URI is a real view file associated with the
     * controller and the action or view name.
     *
     * @param controller the name of the controller
     * @param actionOrView the action method name or view name
     * @return view URI.
     */
    public static String getViewURI(String controller, String actionOrView, String defaultViewDir) {
        if (controller == null) {
            controller = "";
        }
        else {
            controller = controller.toLowerCase();
        }

        if (!controller.startsWith("/") && !"".equals(controller)) controller = "/" + controller;

        if (actionOrView == null || "".equals(actionOrView)) {
            if (getInstance().allowDefaultActionMethod()) {
                actionOrView = getInstance().getDefaultActionMethod();
            }
            else {
                throw new IllegalArgumentException("The value for action " +
                "input cannot be null or empty unless the default action " +
                "method is specified in property file.");
            }
        }

        if (!actionOrView.endsWith(EnvConfig.getInstance().getViewExtension()) &&
        		actionOrView.indexOf('.') == -1) {
            actionOrView += EnvConfig.getInstance().getViewExtension();
        }
        
        String uri = "";
        if (!actionOrView.startsWith("/")) {
        	uri = EnvConfig.getInstance().getWebPageDirectoryName() + controller + "/" + actionOrView;
        }
        else {
        	uri = EnvConfig.getInstance().getWebPageDirectoryName() + controller + actionOrView;
        }
        
        String realPath = getRealPath();
        String filePath = realPath + uri;
        
        File f = new File(filePath);
        if (!f.exists()) {
            if (defaultViewDir != null) {
                log.warn("File \"" + filePath + "\" does not exist. Use " +
                		"default view file in \"" + defaultViewDir + "\".");
                return getViewURI(defaultViewDir, actionOrView, null);
            }
            else {
            	String errorMsg = "View file \"" + filePath + "\" does not exist.";
                log.error(errorMsg);
                throw new NoViewFileException(errorMsg, filePath);
            }
        }

        return uri;
    }

    /**
     * Returns real path of the application.
     *
     * @return real path of the application.
     */
    public static String getRealPath() {
        return ApplicationConfig.getInstance().getApplicationPath();
    }

    /**
     * Returns directory name which contains default view files.
     *
     * Default view files directory specifies directory name for default view
     * files. These view files are related to the non-restful way of actions.
     * For restful way of requests, the default view files are in
     * {DEFAULT_VALUE_defaultViewFilesDirectory}_restful directory.
     */
    public String getDefaultViewFilesDirectory() {
        return DEFAULT_VALUE_defaultViewFilesDirectory;
    }

    /**
     * Returns directory name which contains default view files for builtin
     * RestfulCRUDController or RestfulRequestProcessor.
     */
    public String getDefaultViewFilesDirectoryForREST() {
        return DEFAULT_VALUE_defaultViewFilesDirectory + "_restful";
    }

    /**
     * Checks if controller name is used as a view when the controller does not exist.
     */
    public boolean allowForwardToControllerNameViewWhenControllerNotExist() {
        return ("true".equalsIgnoreCase(allowForwardToControllerNameViewWhenControllerNotExist))?true:false;
    }

    /**
     * Checks if action name is used as a view when the action does not exist.
     */
    public boolean allowForwardToActionNameViewWhenActionNotExist() {
        return ("true".equalsIgnoreCase(allowForwardToActionNameViewWhenActionNotExist))?true:false;
    }

    /**
     * Returns server type
     */
    public String getServerType() {
    	return ApplicationConfig.getInstance().getConfiguredMode();
    }

    /**
     * Returns password scheme
     */
    public String getPasswordScheme() {
        return passwordScheme;
    }

    public String applyPasswordScheme(String password) {
        if (passwordScheme == null || "".equals(passwordScheme)) return password;
        return passwordScheme + "(" + password + ")";
    }

    /**
     * Returns full controller class name.
     *
     * @param controllerPath controller path
     * @return full java class name
     */
    public String getControllerClassName(String controllerPath) {
        String fullName = Converters.convertToJavaClassLikeString(controllerPath.toLowerCase());
        String prefix = getControllerClassPrefix();
        String suffix = getControllerClassSuffix();

        if (!fullName.startsWith(prefix) &&
            prefix != null &&
            !"".equals(prefix)
        ) {
            fullName = prefix + "." + fullName;
        }

        if (!fullName.endsWith(getControllerClassSuffix()) &&
            suffix != null &&
            !"".equals(suffix)
        ) {
            fullName = fullName + getControllerClassSuffix();
        }

        return fullName;
    }

    /**
     * Returns controller name in lower case.
     *
     * @param fullClassName full class name of the controller
     * @return short controller name (in lower case)
     */
    public String getControllerName(String fullClassName) {
        if (fullClassName.indexOf('.') == -1) return fullClassName;

        String controller = fullClassName.substring(fullClassName.lastIndexOf('.') + 1);

        String suffix = getControllerClassSuffix();
        if (!"".equals(suffix) && controller.indexOf(suffix) != -1) {
            controller = controller.substring(0, controller.indexOf(suffix));
        }
        return controller.toLowerCase();
    }

    /**
     * Returns full model class name.
     *
     * @param model model name
     * @return full java class name
     */
    public String getModelClassName(String model) {
        String fullName = Converters.convertToJavaClassLikeString(model);
        if (!model.startsWith(getModelClassPrefix())) {
            fullName = getModelClassPrefix() + "." + fullName;
        }
        if (!model.endsWith(getModelClassSuffix())) {
            fullName = fullName + getModelClassSuffix();
        }
        return fullName;
    }

    /**
     * Returns model class name based on controller class name.
     */
    public String getModelClassNameFromControllerClassName(String controllerClassName) {
        String controller = getControllerName(controllerClassName);
        String model = (DatabaseConfig.getInstance().usePluralTableName())?WordUtil.singularize(controller):controller;
        return getModelClassName(model);
    }

    /**
     * <p>Returns home instance of a model.</p>
     *
     * <p>A home instance of a record is a read-only instance for a model type.
     * Its main function is to provide meta information of the model and some
     * finder methods.</p>
     *
     * @param model name corresponding to the model home instance
     * @return a home instance of a model
     */
    public ActiveRecord getHomeInstance(String model) {
        return ActiveRecordUtil.getHomeInstance(getModelClassName(model));
    }

    /**
     * Checks if bench mark info is allowed to record.
     *
     * @return true if allowed
     */
    public boolean allowRecordBenchmark() {
        return ("true".equalsIgnoreCase(benchmark))?true:false;
    }

    /**
     * Checks if bench mark info is allowed to record in header.
     *
     * @return true if allowed
     */
    public boolean allowRecordBenchmarkInHeader() {
        return ("true".equalsIgnoreCase(benchmarkInHeader))?true:false;
    }

    /**
     * Returns configured locale.
     */
    public Locale getGlobalLocale() {
        return gloabalLocale;
    }

    /**
     * Returns the language for the configured locale.
     */
    public String getGlobalLanguage() {
        return (gloabalLocale != null)?gloabalLocale.getLanguage():null;
    }

    /**
     * Returns the country for the configured locale.
     */
    public String getGlobalCountry() {
        return (gloabalLocale != null)?gloabalLocale.getCountry():null;
    }

    /**
     * Returns the variant for the configured locale.
     */
    public String getGlobalVariant() {
        return (gloabalLocale != null)?gloabalLocale.getVariant():null;
    }

    /**
     * Returns the base name of message files.
     */
    public String getMessageResourcesFileBase() {
        return messageResourcesFileBase;
    }

    /**
     * Returns error page URI.
     */
    public String getErrorPageURI() {
        return errorPageURI;
    }

    /**
     * Returns compile error page URI.
     */
    public String getCompileErrorPageURI() {
        return compileErrorPageURI;
    }

    /**
     * Checks if displaying error details is allowed.
     *
     * @return true if allowed
     */
    public boolean allowDisplayingErrorDetails() {
        return ("true".equalsIgnoreCase(allowDisplayingErrorDetails))?true:false;
    }

    /**
     * Checks if DataBroser is allowed.
     *
     * @return true if allowed
     */
    public boolean allowDataBrowser() {
        return ("true".equalsIgnoreCase(allowDataBrowser))?true:false;
    }

    /**
     * Returns cache provider properties
     */
    public Properties getPredefinedCacheProviderProperties(String providerName) {
    	Properties p = (Properties)cacheProvidersMap.get(providerName);
        return (p != null)?p:(new Properties());
    }

    /**
     * Returns cache provider names
     */
    public Iterator<String> getPredefinedCacheProviderNames() {
        return cacheProvidersMap.keySet().iterator();
    }

    /**
     * Returns default cache provider name
     */
    public String getDefaultCacheProviderName() {
        return defaultCacheProvider;
    }

    /**
     * Checks if cache is ready.
     *
     * @return true if cache is ready
     */
    public boolean useDefaultCache() {
    	return (defaultCacheProvider != null)?true:false;
    }

    /**
     * Returns default cache provider properties
     */
    public Properties getDefaultCacheProviderProperties() {
        return getPredefinedCacheProviderProperties(getDefaultCacheProviderName());
    }

    /**
     * Returns plugin properties
     */
    public Properties getPluginProperties(String pluginName) {
        return PluginManager.getInstance().getPluginProperties(pluginName);
    }
    
    /**
     * Returns the extension/mimetype mapping.
     */
    public Map<String, String> getMimeTypeMap() {
    	return mimeTypesMap;
    }
    
    /**
     * Returns MIME type corresponding the specific extension.
     * 
     * @param extension The extension string.
     * @return the MIME type string for the extension.
     */
    public String getMimeType(String extension) {
    	Object o = mimeTypesMap.get(extension);
    	return (o != null)?o.toString():(String)null;
    }
    
    /**
     * Checks if the specific <tt>extension</tt> has a registered MIME type.
     * 
     * @param extension The extension string.
     * @return true if there is a MIME type for the <tt>extension</tt>.
     */
    public boolean hasMimeTypeFor(String extension) {
    	return mimeTypesMap.containsKey(extension);
    }
    
    /**
     * Returns file directory for storing uploaded files.
     */
    public String getUploadFileRepository() {
    	if (uploadFileRepository == null || 
    			"null".equals(uploadFileRepository) ||
    			"".equals(uploadFileRepository)) return null;
    	return uploadFileRepository;
    }
    
    /**
     * Returns the threshold, in bytes, below which items will be retained in 
     * memory and above which they will be stored as a file. 
     * The default value is 10240 bytes. 
     */
    public int getMaximumBytesPerUploadedFileInMemory() {
    	return Integer.valueOf(maximumBytesPerUploadedFileInMemory);
    }
    
    /**
     * Returns the maximum allowed bytes of an upload request. 
     * The default value of -1 indicates, that there is no limit.
     */
    public long getMaximumTotalBytesPerUploadRequest() {
    	return Long.valueOf(maximumTotalBytesPerUploadRequest);
    }
    
    /**
     * Returns the maximum allowed bytes of an upload file.
     * The default value of -1 indicates, that there is no limit.
     */
    public long getMaximumBytesPerUploadedFile() {
    	return Long.valueOf(maximumBytesPerUploadedFile);
    }
    
    /**
     * Returns DiskFileItemFactory instance for file upload.
     */
    public DiskFileItemFactory getDiskFileItemFactory() {
    	if (fileFactory == null) {
    		configFileUpload();
    	}
    	return fileFactory;
    }
    
    /**
     * Returns ServletFileUpload instance for file upload.
     */
    public ServletFileUpload getServletFileUpload() {
    	if (fileUpload == null) {
    		configFileUpload();
    	}
    	return fileUpload;
    }
    
    /**
     * Checks whether a file is a text file. 
     * 
     * @param file  the file to check
     * @return true if it is a text file.
     */
    public boolean isTextFile(File file) {
    	if (file == null) return false;
    	String fName = file.getName();
    	String extension = fName.substring(fName.lastIndexOf('.') + 1);
    	return isTextFile(extension);
    }
    
    /**
     * Checks whether a file extension indicates that it is a text file. 
     * 
     * @param extension  the file extension to check
     * @return true if it is a text file.
     */
    public boolean isTextFile(String extension) {
    	if (extension == null) return false;
    	boolean result = false;
    	String mimeType = EnvConfig.getInstance().getMimeType(extension);
    	
    	if ((mimeType != null && (
    			mimeType.startsWith("text") || 
    			mimeType.endsWith("xml"))) ||
    			"csh".equalsIgnoreCase(extension) ||
    			"dtd".equalsIgnoreCase(extension) ||
    			"js".equalsIgnoreCase(extension)
    		) {
    		result = true;
    	}
    	return result;
    }
    
    /**
     * Checks if the content of the file can be highlighted. Only a selected
     * types of file extensions are highlightable.
     * 
     * @param fileExtension
     * @return true if highlightable
     */
    public boolean isHighlightable(String fileExtension) {
    	return isTextFile(fileExtension);
    }
}
