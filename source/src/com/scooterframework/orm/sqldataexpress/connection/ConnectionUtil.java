/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.sqldataexpress.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import com.scooterframework.common.logging.LogUtil;
import com.scooterframework.orm.sqldataexpress.config.DatabaseConfig;
import com.scooterframework.orm.sqldataexpress.exception.CreateConnectionFailureException;
import com.scooterframework.orm.sqldataexpress.util.OrmObjectFactory;
import com.scooterframework.security.LoginHelper;

/**
 * ConnectionUtil class
 * 
 * @author (Fei) John Chen
 *
 */
public class ConnectionUtil {
	/**
	 * Helper method to create a connection to database with given
	 * DataSourceConnectionContext instance.
	 * 
	 * @return Connection
	 * @exception CreateConnectionFailureException
	 */
	public static Connection createConnection(DataSourceConnectionContext dcc)
			throws CreateConnectionFailureException {
		if (dcc == null)
			throw new CreateConnectionFailureException(
					"createConnection failure: dcc is null.");

		beforeConnection(dcc);

		Connection connection = null;
		
		if (dcc.useLoginForConnection()) {
			connection = createConnection(dcc.getDataSourceName(), 
					loginUsername(), loginPassword(), dcc.getLoginTimeout());
		}
		else {
			if (dcc.getUsername() == null) {
				connection = createConnection(dcc.getDataSourceName(), dcc
						.getLoginTimeout());
			} else {
				connection = createConnection(dcc.getDataSourceName(), dcc
						.getUsername(), dcc.getPassword(), dcc.getLoginTimeout());
			}
		}

		if (connection == null)
			throw new CreateConnectionFailureException(
					"createConnection() failed for connection name: "
							+ dcc.getConnectionName());

		checkReadonly(connection, dcc);
		checkAutoCommit(connection, dcc);
		checkTransactionIsolationLevel(connection, dcc);

		afterConnection(connection, dcc);

		return connection;
	}

	/**
	 * Helper method to create a connection to database with given data source
	 * name.
	 * 
	 * @return Connection
	 * @exception CreateConnectionFailureException
	 */
	public static Connection createConnection(String jndiDataSourceName,
			Integer loginTimeout) throws CreateConnectionFailureException {
		Connection connection = null;

		try {
			log.debug("connecting to datasource " + jndiDataSourceName);

			InitialContext ctx = new InitialContext();
			DataSource ds = (DataSource) ctx.lookup(jndiDataSourceName);
			if (loginTimeout != null)
				ds.setLoginTimeout(loginTimeout.intValue());

			connection = ds.getConnection();
		} catch (Exception ex) {
            String errorMessage = "ConnectionUtil.createConnection " + 
                    "failed for dataSourceName \"" + jndiDataSourceName + "\"";
			throw new CreateConnectionFailureException(errorMessage + 
                    " because "	+ ex.getMessage(), ex);
		}

		return connection;
	}

	/**
	 * Helper method to create a connection to database with given data source
	 * name, username and password.
	 * 
	 * @return Connection
	 * @exception CreateConnectionFailureException
	 */
	public static Connection createConnection(String jndiDataSourceName,
			String username, String password, Integer loginTimeout) {
		Connection connection = null;

		try {
			log.debug("connecting to datasource " + jndiDataSourceName
					+ " for " + username);

			InitialContext ctx = new InitialContext();
			DataSource ds = (DataSource) ctx.lookup(jndiDataSourceName);
			if (loginTimeout != null)
				ds.setLoginTimeout(loginTimeout.intValue());

			connection = ds.getConnection(username, password);
		} catch (Exception ex) {
            String errorMessage = "ConnectionUtil.createConnection " + 
                    "failed for dataSourceName \"" + jndiDataSourceName + 
                    "\"  and user \"" + username + "\"";
			throw new CreateConnectionFailureException(errorMessage
					+ " because " + ex.getMessage(), ex);
		}
		
		return connection;
	}

	/**
	 * Helper method to create a connection to database with given
	 * JdbcConnectionContext instance.
	 * 
	 * @return Connection
	 * @exception CreateConnectionFailureException
	 */
	public static Connection createConnection(JdbcConnectionContext dcc) {
		if (dcc == null)
			throw new CreateConnectionFailureException(
					"createConnection failure: dcc is null.");

		beforeConnection(dcc);

		Connection connection = null;

		if (dcc.useLoginForConnection()) {
			connection = createConnection(dcc.getDriverClassName(), dcc.getUrl(), 
					loginUsername(), loginPassword(), dcc.getLoginTimeout());
		}
		else {
			if (dcc.getUsername() == null) {
				connection = createConnection(dcc.getDriverClassName(), dcc
						.getUrl(), dcc.getLoginTimeout());
			} else {
				connection = createConnection(dcc.getDriverClassName(), dcc
						.getUrl(), dcc.getUsername(), dcc.getPassword(), dcc
						.getLoginTimeout());
			}
		}

		if (connection == null)
			throw new CreateConnectionFailureException(
					"createConnection() failed for connection name: "
							+ dcc.getConnectionName());

		checkReadonly(connection, dcc);
		checkAutoCommit(connection, dcc);
		checkTransactionIsolationLevel(connection, dcc);

		afterConnection(connection, dcc);

		return connection;
	}

	/**
	 * Helper method to create a connection to database with given url, username
	 * and password.
	 * 
	 * @return Connection
	 * @exception CreateConnectionFailureException
	 */
	public static Connection createConnection(String driver, String url, Integer loginTimeout) {
		Connection connection = null;

		try {
			log.debug("connecting to database " + url);

			try {
				Class.forName(driver);
			} catch (Exception ex) {
				throw new Exception("Failed to load driver \"" + driver + "\".");
			}

			if (loginTimeout != null)
				DriverManager.setLoginTimeout(loginTimeout.intValue());

			connection = DriverManager.getConnection(url);
		} catch (Exception ex) {
			throw new CreateConnectionFailureException(
					"ConnectionUtil.createConnection failed for url \"" + url
							+ "\" because " + ex.getMessage(), ex);
		}

		return connection;
	}

	/**
	 * Helper method to create a connection to database with given url, username
	 * and password.
	 * 
	 * @return Connection
	 * @exception CreateConnectionFailureException
	 */
	public static Connection createConnection(String driver, String url,
			String username, String password, Integer loginTimeout) {
		Connection connection = null;

		try {
			log.debug("connecting to database " + url + " for " + username);

			try {
				Class.forName(driver);
			} catch (Exception ex) {
				throw new Exception("Failed to load driver \"" + driver + "\".");
			}

			if (loginTimeout != null)
				DriverManager.setLoginTimeout(loginTimeout.intValue());

			connection = DriverManager.getConnection(url, username, password);
		} catch (Exception ex) {
			throw new CreateConnectionFailureException(
					"ConnectionUtil.createConnection failed for url \"" + url
							+ "\" and user \"" + username + "\" because "
							+ ex.getMessage(), ex);
		}

		return connection;
	}

	/**
	 * Helper method to create a pooled connection to database with given
	 * JdbcConnectionContext instance.
	 * 
	 * @return Connection
	 * @exception CreateConnectionFailureException
	 */
	public static Connection createPooledConnection(JdbcConnectionContext dcc) {
		if (dcc == null)
			throw new CreateConnectionFailureException(
					"createPooledConnection failure: dcc is null.");

		beforeConnection(dcc);

		Connection connection = null;

		if (dcc.useLoginForConnection()) {
			connection = createPooledConnection(dcc.getConnectionName(), 
		            loginUsername(), loginPassword(), dcc.getLoginTimeout());
		}
		else {
			if (dcc.getUsername() == null) {
				connection = createPooledConnection(dcc.getConnectionName(), dcc.getLoginTimeout());
			} else {
				connection = createPooledConnection(dcc.getConnectionName(), 
	            dcc.getUsername(), dcc.getPassword(), dcc.getLoginTimeout());
			}
		}

		if (connection == null)
			throw new CreateConnectionFailureException(
					"createPooledConnection() failed for connection name: "
							+ dcc.getConnectionName());

		checkReadonly(connection, dcc);
		checkAutoCommit(connection, dcc);
		checkTransactionIsolationLevel(connection, dcc);

		afterConnection(connection, dcc);

		return connection;
	}

	/**
	 * Helper method to create a pooled connection to database with given connection name.
	 * 
	 * @return Connection
	 * @exception CreateConnectionFailureException
	 */
	public static Connection createPooledConnection(String connectionName, Integer loginTimeout) {
		Connection connection = null;

		try {
			log.debug("pool connecting to database represented by " + connectionName);

			DataSource ds = (DataSource) DatabaseConfig.getInstance().getPooledDataSource(connectionName);
            if (ds == null) throw new IllegalArgumentException("No data source for " + connectionName);
            
			if (loginTimeout != null)
				ds.setLoginTimeout(loginTimeout.intValue());

			connection = ds.getConnection();
		} catch (SQLException ex) {
			throw new CreateConnectionFailureException(
					"ConnectionUtil.createPooledConnection failed for conectionName \"" + connectionName
							+ "\" because " + ex.getMessage(), ex);
		}

		return connection;
	}

	/**
	 * Helper method to create a pooled connection to database with given connection name.
	 * 
	 * @return Connection
	 * @exception CreateConnectionFailureException
	 */
	public static Connection createPooledConnection(String connectionName, 
            String username, String password, Integer loginTimeout) {
		Connection connection = null;

		try {
			log.debug("pool connecting to database represented by " + connectionName);

			DataSource ds = (DataSource) DatabaseConfig.getInstance().getPooledDataSource(connectionName);
            if (ds == null) throw new IllegalArgumentException("No data source for " + connectionName);
            
			if (loginTimeout != null)
				ds.setLoginTimeout(loginTimeout.intValue());

			connection = ds.getConnection(username, password);
		} catch (SQLException ex) {
			throw new CreateConnectionFailureException(
					"ConnectionUtil.createPooledConnection failed for conectionName \"" + connectionName
							+ "\" because " + ex.getMessage(), ex);
		}

		return connection;
	}
    
	public static String getSetRoleStatement(Properties roles) {
		String roleStr = "";
		for(Map.Entry<Object, Object> entry : roles.entrySet()) {
			String key = (String) entry.getKey();
			String pwd = (String) entry.getValue();
			if (pwd != null) {
				roleStr = roleStr + " " + key + " identified by ? ,";
			} else {
				roleStr = roleStr + " " + key + ",";
			}
		}
		// remove the last ,
		roleStr = roleStr.substring(0, roleStr.length() - 1);
		return "SET ROLE " + roleStr;
	}
	
	/**
	 * If the database connection context specifies <tt>readonly</tt>, set the 
	 * <tt>connection</tt> to be read only.
	 * 
	 * @param connection  a database connection instance
	 * @param dcc         a <tt>DatabaseConnectionContext</tt> instance
	 */
	public static void checkReadonly(Connection connection,
			DatabaseConnectionContext dcc) {
		try {
			if (dcc.isReadonly())
				connection.setReadOnly(true);
		} catch (SQLException ex) {
			throw new CreateConnectionFailureException(
					"Faied to set readonly property for connection \""
							+ dcc.getConnectionName() + "\" because " + ex.getMessage(), ex);
		}
	}
	
	/**
	 * If the database connection context specifies <tt>autocommit</tt> to be 
	 * true, set the <tt>connection</tt> to be auto commit.
	 * 
	 * @param connection  a database connection instance
	 * @param dcc         a <tt>DatabaseConnectionContext</tt> instance
	 */
	public static void checkAutoCommit(Connection connection,
			DatabaseConnectionContext dcc) {
		try {
			if (!dcc.isAutoCommit())
				connection.setAutoCommit(false);
		} catch (SQLException ex) {
			throw new CreateConnectionFailureException(
					"Faied to set readonly property for connection \""
							+ dcc.getConnectionName() + "\" because " + ex.getMessage(), ex);
		}
	}

	/**
	 * If the database connection context specifies <tt>transaction_isolation_level</tt>, 
	 * set the transaction isolation level of the <tt>connection</tt>.
	 * 
	 * @param connection  a database connection instance
	 * @param dcc         a <tt>DatabaseConnectionContext</tt> instance
	 */
	public static void checkTransactionIsolationLevel(Connection connection,
			DatabaseConnectionContext dcc) {
		try {
			if (dcc.hasSpecifiedTransactionIsolationLevel())
				connection.setTransactionIsolation(dcc.getTransactionIsolationLevel());
		} catch (SQLException ex) {
			throw new CreateConnectionFailureException(
					"Faied to set transaction isolation property for connection \""
							+ dcc.getConnectionName() + "\" because " + ex.getMessage(), ex);
		}
	}

	/**
	 * If the database connection context specifies <tt>beforeConnection</tt>
	 * class name, execute the <tt>beforeConnectionMethodName</tt>.
	 * 
	 * @param dcc  a <tt>DatabaseConnectionContext</tt> instance
	 */
	public static void beforeConnection(DatabaseConnectionContext dcc) {
		String beforeClassName = dcc.getBeforeConnectionClassName();
		if (beforeClassName != null) {
			OrmObjectFactory.getInstance().execute(beforeClassName,
					dcc.getBeforeConnectionMethodName(), null);
		}
	}

	/**
	 * If the database connection context specifies <tt>afterConnection</tt>
	 * class name, execute the <tt>afterConnectionMethodName</tt>.
	 * 
	 * @param dcc  a <tt>DatabaseConnectionContext</tt> instance
	 */
	public static void afterConnection(Connection connection,
			DatabaseConnectionContext dcc) {
		String afterClassName = dcc.getAfterConnectionClassName();
		if (afterClassName != null) {
			OrmObjectFactory.getInstance().execute(afterClassName,
					dcc.getAfterConnectionMethodName(),
					new Object[] { connection });
		}
	}
	
	private static String loginUsername() {
		return LoginHelper.loginUserId();
	}
	
	private static String loginPassword() {
		return LoginHelper.loginPassword();
	}

	private static LogUtil log = LogUtil.getLogger(ConnectionUtil.class
			.getName());
}
