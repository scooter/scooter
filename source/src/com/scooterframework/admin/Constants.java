/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.admin;

/**
 * Constants class holds some constants.
 * 
 * @author (Fei) John Chen
 */
public class Constants {

//************************************************************************    
// Internal Keys: internal keys are used somewhere in the application. 
//************************************************************************
    public static final String CONTROLLER = "scooter.key.controller";
    public static final String CONTROLLER_PATH = "scooter.key.controller.path";
    public static final String CONTROLLER_CLASS = "scooter.key.controller.class";
    public static final String CURRENT_LOCALE = "scooter.key.current.locale";
    public static final String ACTION = "scooter.key.action";
    public static final String MODEL = "scooter.key.model";
    public static final String FORMAT = "scooter.key.format";
    public static final String RESOURCE = "scooter.key.resource";
    
    public static final String REQUEST_RENDERED = "scooter.key.rendered";
    
    //
    //Keys for request information
    //
    public static final String SKIP_PATH = "scooter.key.skip.path";
    public static final String REQUEST_HEADER = "scooter.key.request.header";
    public static final String REQUEST_PATH = "scooter.key.request.path";
    public static final String REQUEST_PATH_REST = "scooter.key.request.path.restful";
    public static final String REQUEST_URI = "scooter.key.request.uri";
    public static final String LOCAL_REQUEST = "scooter.key.local.request";
    public static final String FILE_UPLOAD_REQUEST = "scooter.key.fileupload.request";
    public static final String FILE_UPLOAD_REQUEST_FILES = "scooter.key.fileupload.request.files";
    
    //Error related
    public static final String ERROR_MESSAGE = "scooter.key.error.message";
    public static final String ERROR_EXCEPTION = "scooter.key.error.exception";
    
    //configured mode
    public static final String CONFIGURED_MODE_SCOOTER_WEB = "SCOOTER WEB";
    public static final String CONFIGURED_MODE_SCOOTER_APP = "SCOOTER APP";
    public static final String CONFIGURED_MODE_SCOOTER_ORM = "SCOOTER ORM";
    
    public static final String ALLOW_CLASSWORK = "scooter.key.allow.classwork";
    
    /**
     * A hyphen "-" is used to link composite primary key fields.
     */
    public static final String PRIMARY_KEY_SEPARATOR = "-";
    
    /**
     * Default value when there is no format derived from request uri.
     */
    public static final String DEFAULT_RESPONSE_FORMAT = "html";
    
    /**
     * Default running environment is "DEVELOPMENT".
     */
    public static final String RUNNING_ENVIRONMENT_DEVELOPMENT = "DEVELOPMENT";
    public static final String RUNNING_ENVIRONMENT_TEST        = "TEST";
    public static final String RUNNING_ENVIRONMENT_PRODUCTION  = "PRODUCTION";
    
//*************************************************************************
// External Keys: external keys are sent to the application from somewhere.
//*************************************************************************
    public static final String HTTP_METHOD = "_method";
    public static final String AJAX_REQUEST = "_ajax";
    
    /**
     * Http key "<tt>paged</tt>" indicates whether to use paginator to display 
     * a list. If "<tt>paged=true</tt>", a paginator should be used.
     */
    public static final String PAGED = "paged";
    

//************************************************************************    
// App Property Keys: Property keys related to app server metainfo.
//************************************************************************
    public static final String APP_KEY_JAVA_VERSION               = "scooter.java.version";
    public static final String APP_KEY_SCOOTER_VERSION            = "scooter.version";
    public static final String APP_KEY_RUNNING_ENVIRONMENT        = "scooter.running.environment";
    public static final String APP_KEY_APPLICATION_START_TIME     = "scooter.application.start.time";
    public static final String APP_KEY_APPLICATION_ROOT_PATH      = "scooter.application.root.path";
    public static final String APP_KEY_APPLICATION_CONTEXT_NAME   = "scooter.application.context.name";
    public static final String APP_KEY_APPLICATION_DATABASE_NAME  = "scooter.application.database.name";
    
    public static final String APP_KEY_SCOOTER_PROPERTIES         = "scooter.properties";
    
//************************************************************************    
// Global values of the app
//************************************************************************
    public static String SCOOTER_VERSION = "";
    
//************************************************************************    
// Internal values of the app
//************************************************************************
    public static final String LOCAL_HOST_URL_PREFIX_1 = "http://localhost";
    public static final String LOCAL_HOST_URL_PREFIX_2 = "https://localhost";
    public static final String LOCAL_HOST_URL_PREFIX_3 = "http://127.0.0.1";
    public static final String LOCAL_HOST_URL_PREFIX_4 = "https://127.0.0.1";
    public static final String LOCAL_HOST_REMOTE_ADDRESS = "127.0.0.1";
    public static final String LOCAL_HOST_REMOTE_HOST_1 = "127.0.0.1";
    public static final String LOCAL_HOST_REMOTE_HOST_2 = "localhost";
    
    
    public static final String VALUE_FOR_FILE_UPLOAD_REQUEST = "true";    
    public static final String VALUE_FOR_LOCAL_REQUEST = "true";
    
//************************************************************************    
// Events
//************************************************************************
    public static final String EVENT_COMPILE = "EVENT_COMPILE";
}
