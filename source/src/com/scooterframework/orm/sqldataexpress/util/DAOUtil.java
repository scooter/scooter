/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.sqldataexpress.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.scooterframework.common.logging.LogUtil;
import com.scooterframework.orm.sqldataexpress.connection.UserDatabaseConnection;

/**
 * DAOUtil contains helper methods for database access.
 * 
 * @author (Fei) John Chen
 *
 */
public class DAOUtil 
{
    public static void closeConnection( UserDatabaseConnection udc )
    {
    	if (udc != null) closeConnection(udc.getConnection());
    }
    
    public static void closeConnection( Connection connection )
    {
        try {
            if (connection != null) {
                connection.close();
                connection = null;
            }
        }
        catch (SQLException sex) {
            log.error("Error in closeConnection: " + sex.getMessage());
            connection = null;
        }
    }

    public static void closeResultSet( ResultSet rs )
    {
        try {
            if (rs != null) {
                rs.close();
                rs = null;
            }
        }
        catch (SQLException sex) {
            log.error("Error in closeResultSet: " + sex.getMessage());
            rs = null;
        }
    }

    public static void closeStatement( Statement stmt )
    {
        try {
            if (stmt != null) {
                stmt.close();
                stmt = null;
            }
        }
        catch (SQLException sex) {
            log.error("Error in closeStatement: " + sex.getMessage());
            stmt = null;
        }
    }
    
    public static void commit( Connection conn )
    {
        try {
            if (conn != null) {
                conn.commit();
            }
        }
        catch (SQLException sex) {
            log.error("Error in commit: " + sex.getMessage());
        }
    }

    public static void rollback( Connection conn )
    {
        try {
            if (conn != null) {
                conn.rollback();
            }
        }
        catch (SQLException sex) {
            log.error("Error in rollback: " + sex.getMessage());
        }
    }

    public static boolean updateFailed( int[] updateCounts ) 
    {        
        boolean bError = false;
        
        // determine operation result
        int iProcessed = 0;
        int totalExecution = updateCounts.length;
        for (int i = 0; i < totalExecution; i++)
        {
            iProcessed = updateCounts[i];
            if( iProcessed > 0 || iProcessed == Statement.SUCCESS_NO_INFO )
            {
                // statement was successful
            }
            else
            {
                // error on statement
                bError = true;
                break;
            }
        } // end for

        return bError;
    }

    private static LogUtil log = LogUtil.getLogger(DAOUtil.class.getName());
}
