/*
 *   This software is distributed under the terms of the FSF
 *   Gnu Lesser General Public License (see lgpl.txt).
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.common.logging;

import org.apache.log4j.Logger;

import com.scooterframework.security.LoginHelper;

/**
 * LogUtil class. This class uses Apache's Log4J as logger. Configuration
 * information can be found in log4j.properties. You may also create your own
 * log4j properties file.
 *
 * If your application shares the same container with other applications, be
 * careful on the log file name you choose.
 *
 * <PRE>
 * Usage:
 *    1. In your application class, declare the following
 *        LogUtil log = LogUtil.getLogger(this.getClass().getName());
 *
 *    2. Then you can log message with this code:
 *        log.debug(message);
 *
 *        or better with these codes:
 *        if (log.isDebugEnabled()) {
 *            log.debug(message);
 *        }
 *
 *     3. Modify log4j.properties file according to your own application:
 *        This may be necessary if you want log file names do not conflict with
 *        other application's log file names in the same container.
 *
 *     4. Use your own myApp_log4j.properties file: This is optional.
 *        4.1 Create a file named myApp_log4j.properties
 *        4.2 Make sure this file is on your classpath.
 *        4.3 Add this system property on your container start line:
 *            -Dlog4j.configuration=myApp_log4j.properties
 * </PRE>
 *
 * @author (Fei) John Chen
 */
public class LogUtil {
	public static final int TRACE = 0;
	public static final int DEBUG = 1;
	public static final int INFO  = 2;
	public static final int WARN  = 3;
	public static final int ERROR = 4;
	public static final int FATAL = 5;

	public static String m_consoleDebug = "TRUE";
	public static int m_debugLevel = DEBUG;

	private String m_instanceName = "";
	private Logger logger;
	private static boolean loggerEnabled;
	private static boolean manualStop;

	static {
		m_consoleDebug = System.getProperty("ConsoleDebug", m_consoleDebug);

		try {
			m_debugLevel = Integer.parseInt(System.getProperty("DEBUG_LEVEL"));
		} catch (Exception ex) {
			;// use default
		}
	}
    /**
     * Starts a <tt>Logger</tt> for a specific instance.
     *
     * @param instanceName      The instance to log
     * @return this instance
     */
	public static synchronized LogUtil getLogger(String instanceName) {
		return new LogUtil(instanceName);
	}

    /**
     * Enables the logger.
     */
	public static void enableLogger() {
        if (manualStop) return;
		loggerEnabled = true;
	}

    /**
     * Disables the logger.
     */
	public static void disableLogger() {
        if (manualStop) return;
		loggerEnabled = false;
	}

    /**
     * Puts a stop on the logger. Sometimes logger is enabled by other program.
     * Use this method if you want to nullify any calls to the
     * {@link #enableLogger()} method.
     */
    public static void manualStopOn() {
        manualStop = true;
    }

    /**
     * Removes a stop on the logger. See description of {@link #manualStopOn()}
     * method for more details.
     */
    public static void manualStopOff() {
        manualStop = false;
    }


	private LogUtil(String instanceName) {
		m_instanceName = instanceName;
		if (loggerEnabled) {
			logger = Logger.getLogger(m_instanceName);
		}
	}

	/**
	 * Method to log a trace message
	 *
	 */
	public void trace(String message) {
		log(message, TRACE);
	}

	/**
	 * Method to log a trace message
	 *
	 */
	public void trace(Object message) {
		log(message, TRACE);
	}

	/**
	 * Method to log a debug message
	 *
	 */
	public void debug(String message) {
		log(message, DEBUG);
	}

	/**
	 * Method to log a debug message
	 *
	 */
	public void debug(Object message) {
		log(message, DEBUG);
	}

	/**
	 * Method to log an info message
	 *
	 */
	public void info(String message) {
		log(message, INFO);
	}

	/**
	 * Method to log an info message
	 *
	 */
	public void info(Object message) {
		log(message, INFO);
	}

	/**
	 * Method to log a warning message
	 *
	 */
	public void warn(String message) {
		log(message, WARN);
	}

	/**
	 * Method to log a warning message
	 *
	 */
	public void warn(Object message) {
		log(message, WARN);
	}

	/**
	 * Method to log an error message
	 *
	 */
	public void error(String message) {
		log(message, ERROR);
	}

	/**
	 * Method to log an error message
	 *
	 */
	public void error(Object message) {
		log(message, ERROR);
	}

	/**
	 * Method to log a fatal message
	 *
	 */
	public void fatal(String message) {
		log(message, FATAL);
	}

	/**
	 * Method to log a fatal message
	 *
	 */
	public void fatal(Object message) {
		log(message, FATAL);
	}

	/**
	 * Method to log an info message
	 *
	 */
	public void info(int id, String message) {
		log(message, INFO);
	}

	/**
	 * Method to log a warning message
	 *
	 */
	public void warn(int id, String message) {
		log(message, WARN);
	}

	/**
	 * Method to log an error message
	 *
	 */
	public void error(int id, String message) {
		log(message, ERROR);
	}

	/**
	 * Method to log a fatal message
	 *
	 */
	public void fatal(int id, String message) {
		log(message, FATAL);
	}

	/**
	 * Check whether this category is enabled for the TRACE Level.
	 *
	 * @return boolean - <tt>true</tt> if this category is enabled for
	 *         level TRACE, <tt>false</tt> otherwise.
	 */
	public boolean isTraceEnabled() {
		return (logger != null) ? logger.isTraceEnabled() : false;
	}

	/**
	 * Check whether this category is enabled for the DEBUG Level.
	 *
	 * @return boolean - <tt>true</tt> if this category is enabled for
	 *         level DEBUG, <tt>false</tt> otherwise.
	 */
	public boolean isDebugEnabled() {
		return (logger != null) ? logger.isDebugEnabled()
				: (m_debugLevel == DEBUG);
	}

	/**
	 * Check whether this category is enabled for the INFO Level.
	 *
	 * @return boolean - <tt>true</tt> if this category is enabled for
	 *         level INFO, <tt>false</tt> otherwise.
	 */
	public boolean isInfoEnabled() {
		return (logger != null) ? logger.isInfoEnabled() : false;
	}


	private void log(Object object, int iLogLevel) {
		String message = (object == null) ? "null" : object.toString();
		log(message, iLogLevel);
	}

	private void log(String message, int iLogLevel) {
		if (!loggerEnabled)
			return;

		String userInfo = getUserInfo();
		if (userInfo != null && !"".equals(userInfo)) {
			message = "[" + userInfo + "] " + message;
		}

		if (logger != null) {
			switch (iLogLevel) {
			case TRACE:
				logger.trace(message);
				break;
			case DEBUG:
				logger.debug(message);
				break;
			case INFO:
				logger.info(message);
				break;
			case WARN:
				logger.warn(message);
				break;
			case ERROR:
				logger.error(message);
				break;
			case FATAL:
				logger.fatal(message);
				break;
			default:
				logger.debug(message);
			}
		} else {
			String logLevelDesc = getLogLevelDesc(iLogLevel);
			StringBuilder logMessage = new StringBuilder();
			String messageText = (message == null) ? "null" : message;

			// Build the log message.
			logMessage.append("[").append(System.currentTimeMillis()).append(
					"] ");
			logMessage.append("[").append(logLevelDesc).append("] ");
			logMessage.append(getShortName(m_instanceName)).append(": ");
			logMessage.append(messageText);

			if ("TRUE".equalsIgnoreCase(m_consoleDebug)
					&& (iLogLevel >= m_debugLevel)) {
				System.out.println(logMessage);
			}
		}
	}

	private String getShortName(String name) {
		return name.substring(name.lastIndexOf('.') + 1);
	}

	private String getLogLevelDesc(int iLogLevel) {
		String desc = "D"; // default

		switch (iLogLevel) {
		case TRACE:
			desc = "T";
			break;
		case DEBUG:
			desc = "D";
			break;
		case INFO:
			desc = "I";
			break;
		case WARN:
			desc = "W";
			break;
		case ERROR:
			desc = "E";
			break;
		case FATAL:
			desc = "F";
			break;
		default:
			desc = "D";
		}

		return desc;
	}

	private String getUserInfo() {
		String user = LoginHelper.loginUserId();
		return (user != null) ? user : "";
	}
}
