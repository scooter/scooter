/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import com.scooterframework.admin.ApplicationConfig;
import com.scooterframework.admin.Constants;
import com.scooterframework.admin.EnvConfig;
import com.scooterframework.common.http.HTTPClient;
import com.scooterframework.common.http.HTTPResponse;
import com.scooterframework.orm.sqldataexpress.config.DatabaseConfig;
import com.scooterframework.orm.sqldataexpress.util.DAOUtil;
import com.scooterframework.orm.sqldataexpress.util.SqlExpressUtil;

/**
 * <p>
 * FunctionalTestHelper class is super class of all functional test classes for
 * an application. Scooter uses a real, not simulated, web server to run
 * functional tests.
 * 
 * <p>
 * By default, Scooter starts an embedded web server for functional tests on
 * port <tt>8080</tt>. You can change the default testing server port or use an
 * existing external server by specifying the system property appPort or
 * webStarted respectively.
 * 
 * <p>
 * It is very easy to run functional tests with Scooter. Just run one of the
 * following Ant commands:
 * 
 * <p>
 * Run blog application's functional tests on default port 8080:
 * 
 * <pre>
 * >ant app_test_functional -DappPath=webapps/blog
 * </pre>
 * 
 * <p>
 * Run blog application's functional tests on port 9999:
 * 
 * <pre>
 * >ant app_test_functional -DappPath=webapps/blog -DappPort=9999
 * </pre>
 * 
 * <p>
 * Run blog application's functional tests with an existing web server:
 * 
 * <pre>
 * >ant app_test_functional -DappPath=webapps/blog -DwebStarted=true
 * </pre>
 * 
 * 
 * @author (Fei) John Chen
 * 
 */
public class FunctionalTestHelper {
	
    private static final String DEFAULT_TEST_HOST = "http://localhost";
    private static final String DEFAULT_TEST_PORT = "8678";
	private HTTPClient httpclient;
	
	/*
	 * Not available if an external web server is used for testing.
	 */
	protected static ApplicationConfig ac;
	
	/*
	 * Not available if an external web server is used for testing.
	 */
	protected static String contextName;
	
	/*
	 * Not available if an external web server is used for testing.
	 */
	protected static EnvConfig wc;
	
	@BeforeClass
	public static void setUpBeforeClass() {
		initApp();
	}

	@AfterClass
	public static void tearDownAfterClass() {
		if (ac != null) {
			DatabaseConfig.getInstance().restoreDefaultDatabaseConnectionName();
			ac.getInstance().endApplication();
		}
	}
	
	@Before
	public void setUp() {
	}
	
	@After
	public void tearDown() {
		if (httpclient != null) httpclient.shutDown();
	}
	
	public void refreshHttpConnection() {
		if (httpclient != null) httpclient.shutDown();
		httpclient = new HTTPClient();
	}
	
	protected HTTPResponse fireHttpGetRequest(String uri) {
		uri = convert2URL(uri);
		refreshHttpConnection();
		return httpclient.fireHttpGetRequest(uri);
	}
	
	protected HTTPResponse fireHttpPostRequest(String uri) {
		uri = convert2URL(uri);
		refreshHttpConnection();
		return httpclient.fireHttpPostRequest(uri);
	}
	
	protected HTTPResponse fireHttpPostRequest(String uri, Map<String, String> params) {
		uri = convert2URL(uri);
		refreshHttpConnection();
		return httpclient.fireHttpPostRequest(uri, params);
	}
	
	protected HTTPResponse fireHttpPutRequest(String uri) {
		uri = convert2URL(uri);
		refreshHttpConnection();
		return httpclient.fireHttpPutRequest(uri);
	}
	
	protected HTTPResponse fireHttpPutRequest(String uri, Map<String, String> params) {
		uri = convert2URL(uri);
		refreshHttpConnection();
		return httpclient.fireHttpPutRequest(uri, params);
	}
	
	protected HTTPResponse fireHttpDeleteRequest(String uri) {
		uri = convert2URL(uri);
		refreshHttpConnection();
		return httpclient.fireHttpDeleteRequest(uri);
	}
	
	protected static void assertSuccess(HTTPResponse response) {
		assertEquals(200, response.getStatusCode());
	}
	
	protected static void assertRedirectSuccess(HTTPResponse response) {
		assertEquals(302, response.getStatusCode());
	}
	
	private static void initApp() {
		String appPath = System.getProperty("appPath");
		String appPort = getAppPort();
		String webStarted = System.getProperty("webStarted");
		
		if (appPath == null) {
			throw new IllegalArgumentException("Please specify -DappPath=...");
		}
		else {
			File dir = new File(appPath);
			if (!dir.exists()) {
				throw new IllegalArgumentException("App path \"" + appPath + "\" does not exist.");
			}
		}
		
		if (webStarted != null
				&& (!webStarted.startsWith("$") || 
					webStarted.equalsIgnoreCase("true")))
			return;

		System.out.println("Starting functional test for " + appPath + " on port " + appPort);
		
		try {
			startUp(appPath, appPort);
			
			ac = ApplicationConfig.getInstance();
			String runningEnvironment = ac.getRunningEnvironment();
			if (!Constants.RUNNING_ENVIRONMENT_TEST.equals(runningEnvironment)) {
				ac.setRunningEnvironment(Constants.RUNNING_ENVIRONMENT_TEST);
				System.out.println("Scooter automatically switched to TEST environment.");
			}
			
			String testDB = DatabaseConfig.getInstance().tryToUseTestDatabaseConnection();
			if (testDB != null) {
				System.out.println("Scooter automatically switched to test database \"" + testDB + "\" for testing.");
				boolean checkConn = validateConnection(testDB);
				if (!checkConn) {
					throw new IllegalArgumentException("Connection named '" + testDB + "' cannot be established.");
				}
			}
			else {
				System.out.println("Please make sure you are using a test database.");
			}
			
			contextName = ac.getContextName();
			
			wc = EnvConfig.getInstance();
		} catch (Throwable ex) {
			System.out.println("ERROR in starting up test server: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	private static void startUp(String appName, String appPort) throws Exception {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		String jettyMainClassName = "com.scooterframework.tools.webserver.StartServer";
		if (appPort != null) {
			String[] args = { appName, appPort };
			invokeMain(cl, jettyMainClassName, args);
		}
		else {
			String[] args = { appName };
			invokeMain(cl, jettyMainClassName, args);
		}
	}
	
    private static void invokeMain(ClassLoader classloader, String classname, String[] args)
    throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException, ClassNotFoundException
    {
        Class invoked_class = classloader.loadClass(classname);
        Class[] method_param_types = new Class[1];
        method_param_types[0]= args.getClass();
        Method main = invoked_class.getDeclaredMethod("main", method_param_types);
        Object[] method_params = new Object[1];
        method_params[0] = args;

        main.invoke(null, method_params);
    }
    
    private static boolean validateConnection(String connectionName) {
    	boolean check = false;
    	Connection conn = null;
		try {
			conn = SqlExpressUtil.getConnection(connectionName);
			check = true;
		} catch (Exception ex) {}
		finally {
    		DAOUtil.closeConnection(conn);
		}
		return check;
    }
    
    private void validateURI(String uri) {
    	if (uri == null) 
    		throw new IllegalArgumentException("uri cannot be null.");

    	if (!uri.startsWith("/")) 
    		throw new IllegalArgumentException("uri string must start with /.");
    }
    
    private String convert2URL(String uri) {
    	if (uri.startsWith("http")) return uri;
    	
    	validateURI(uri);
    	
		String appPath = System.getProperty("appPath");
		String appPort = getAppPort();
		String appName = "";
		File dir = new File(appPath);
		try {
			String path = dir.getCanonicalPath();
			appName = path.substring(path.lastIndexOf(File.separatorChar) + 1);
		} catch (IOException ex) {
			System.out.println("Error in convert2URL(: " + ex.getMessage());
		}
		StringBuffer sb = new StringBuffer();
		sb.append(DEFAULT_TEST_HOST).append(':').append(appPort).append('/');
		sb.append(appName).append(uri);
		return sb.toString();
    }
    
    private static String getAppPort() {
    	String appPort = System.getProperty("appPort", DEFAULT_TEST_PORT);
    	
    	if (appPort == null || appPort.startsWith("$")) return DEFAULT_TEST_PORT;
    	return appPort;
    }
}
