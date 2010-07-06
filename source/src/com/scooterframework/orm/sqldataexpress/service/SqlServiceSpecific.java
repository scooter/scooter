/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.sqldataexpress.service;

import java.util.Map;

import com.scooterframework.orm.sqldataexpress.exception.BaseSQLException;
import com.scooterframework.orm.sqldataexpress.object.TableData;

/**
 * SqlServiceSpecific interface specified services that are specific. 
 * 
 * @author (Fei) John Chen
 */
public interface SqlServiceSpecific
{
    /**
     * Retrieve a single row data from database.
     * 
     * @param inputs            Map of input data
     * @param processorType     A named sql or direct sql or stored procedure
     * @param processorName     Sql name or sql itself or stored procedure name
     * @return TableData        The row data
     * @throws com.scooterframework.orm.sqldataexpress.exception.BaseSQLException
     */
    public TableData retrieveRow(Map inputs, String processorType, String processorName) 
    throws BaseSQLException;
    
    /**
     * Retrieve a list of rows from database.
     * 
     * @param inputs            Map of input data
     * @param processorType     A named sql or direct sql or stored procedure
     * @param processorName     Sql name or sql itself or stored procedure name
     * @return TableData        The list of row data
     * @throws com.scooterframework.orm.sqldataexpress.exception.BaseSQLException
     */
    public TableData retrieveRows(Map inputs, String processorType, String processorName) 
    throws BaseSQLException;
    
    /**
     * Retrieve a list of rows from database with a certain limit range. If the 
     * number of returned records is more than the preset limit range, an
     * UnexpectedDataException will be thrown. 
     * 
     * If DataProcessor.input_key_records_fixed key has value "true" in inputs, 
     * absolute fixed number of records is required. An UnexpectedDataException 
     * will be thrown if the number of retrieved records is not equal to 
     * limitOrFixed.
     * 
     * If the limitOrFixed = -1, all records are retrieved. 
     * 
     * @param inputs            Map of input data
     * @param processorType     A named sql or direct sql or stored procedure
     * @param processorName     Sql name or sql itself or stored procedure name
     * @param limitOrFixed      Number of desired (limit) or fixed records to retrieve
     * @return TableData        The row data
     * @throws com.scooterframework.orm.sqldataexpress.exception.BaseSQLException
     */
    public TableData retrieveRows(Map inputs, String processorType, String processorName, int limitOrFixed) 
    throws BaseSQLException;
    
    /**
     * Retrieve a list of rows from database with a certain limit range. If the 
     * number of returned records is more than the preset limit range, an
     * UnexpectedDataException will be thrown. 
     * 
     * If DataProcessor.input_key_records_fixed key has value "true" in inputs, 
     * absolute fixed number of records is required. An UnexpectedDataException 
     * will be thrown if the number of retrieved records is not equal to 
     * limitOrFixed.
     * 
     * If the limitOrFixed = -1, all records are retrieved. 
     * 
     * offset defaults to 0. 
     * 
     * @param inputs            Map of input data
     * @param processorType     A named sql or direct sql or stored procedure
     * @param processorName     Sql name or sql itself or stored procedure name
     * @param limitOrFixed      Number of desired (limit) or fixed records to retrieve
     * @param offset            int for offset
     * @return TableData        The row data
     * @throws com.scooterframework.orm.sqldataexpress.exception.BaseSQLException
     */
    public TableData retrieveRows(Map inputs, 
                                  String processorType, 
                                  String processorName, 
                                  int limitOrFixed,
                                  int offset) 
    throws BaseSQLException;
    
    
    /**
     * Insert data to database.
     * 
     * @param inputs            Map of input data
     * @param processorType     A named sql or direct sql or stored procedure
     * @param processorName     Sql name or sql itself or stored procedure name
     * @throws com.scooterframework.orm.sqldataexpress.exception.BaseSQLException
     */
    public void insert(Map inputs, String processorType, String processorName) 
    throws BaseSQLException;
    
    /**
     * Delete data from database.
     * 
     * @param inputs            Map of input data
     * @param processorType     A named sql or direct sql or stored procedure
     * @param processorName     Sql name or sql itself or stored procedure name
     * @return int              number of rows deleted
     * @throws com.scooterframework.orm.sqldataexpress.exception.BaseSQLException
     */
    public int delete(Map inputs, String processorType, String processorName) 
    throws BaseSQLException;
    
    /**
     * Update data in database.
     * 
     * @param inputs            Map of input data
     * @param processorType     A named sql or direct sql or stored procedure
     * @param processorName     Sql name or sql itself or stored procedure name
     * @return int              number of rows updated
     * @throws com.scooterframework.orm.sqldataexpress.exception.BaseSQLException
     */
    public int update(Map inputs, String processorType, String processorName) 
    throws BaseSQLException;
}
