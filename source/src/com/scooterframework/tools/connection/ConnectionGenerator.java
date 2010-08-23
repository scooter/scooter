/*
 *   This software is distributed under the terms of the FSF
 *   Gnu Lesser General Public License (see lgpl.txt).
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.tools.connection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import com.scooterframework.orm.sqldataexpress.util.DAOUtil;
import com.scooterframework.orm.sqldataexpress.util.SqlExpressUtil;
import com.scooterframework.tools.common.AbstractGenerator;

/**
 * This class generates controller class code.
 *
 * @author (Fei) John Chen
 */
public class ConnectionGenerator extends AbstractGenerator {
	public ConnectionGenerator(String connectionName, Properties props) throws SQLException {
		super(props);
		
		//test connection
		Connection conn = null;
		try {
	    	if ("".equals(connectionName)) {
	    		log("Connecting to default database connection ...");
	    		log("Connection properties: " + SqlExpressUtil.getDefaultConnectionProperties());
	    		conn = SqlExpressUtil.getConnection();
	    	}
	    	else {
	    		log("Connecting to database connection named: " + connectionName);
	    		log("Connection properties: " + SqlExpressUtil.getConnectionProperties(connectionName));
	    		conn = SqlExpressUtil.getConnection(connectionName);
	    	}
		}
		finally {
    		DAOUtil.closeConnection(conn);
		}
    	log("Connection test successful.");
	}

	protected Properties getTemplateProperties() {
		return null;
	}

	protected String getRelativePathToOutputFile() {
		return null;
	}

	protected String getOutputFileName() {
		return null;
	}
}