/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.sqldataexpress.processor;

import com.scooterframework.orm.sqldataexpress.connection.UserDatabaseConnection;
import com.scooterframework.orm.sqldataexpress.exception.UnsupportedDataProcessorNameException;
import com.scooterframework.orm.sqldataexpress.exception.UnsupportedDataProcessorTypeException;
import com.scooterframework.orm.sqldataexpress.object.Function;
import com.scooterframework.orm.sqldataexpress.object.JdbcStatement;
import com.scooterframework.orm.sqldataexpress.object.StoredProcedure;
import com.scooterframework.orm.sqldataexpress.util.DBStore;
import com.scooterframework.orm.sqldataexpress.util.SqlExpressUtil;

/**
 * DataProcessorFactory class.
 * 
 * @author (Fei) John Chen
 */
public class DataProcessorFactory {
    private static final DataProcessorFactory me = new DataProcessorFactory();

    private DataProcessorFactory() {
    }
    
    
    public static DataProcessorFactory getInstance() {
        return me;
    }
    
    public DataProcessor getDataProcessor(UserDatabaseConnection udc, String processorType, String processorName) {
        if (processorType == null) return null;
        
        DataProcessor selectedDataProcessor = null;
        
        if (DataProcessorTypes.STORED_PROCEDURE_PROCESSOR.equals(processorType)) {
            StoredProcedure storedProcedure = DBStore.getInstance().getStoredProcedure(processorName);
            
            if (storedProcedure == null) {
                //discovery
                storedProcedure = SqlExpressUtil.lookupStoredProcedure(udc, processorName);
                
                if (storedProcedure == null) {
                    throw new UnsupportedDataProcessorNameException();
                }
                else {
                    DBStore.getInstance().addStoredProcedure(processorName, storedProcedure);
                }
            }
            
            selectedDataProcessor = new StoredProcedureProcessor(storedProcedure);
        }
        else if (DataProcessorTypes.FUNCTION_PROCESSOR.equals(processorType)) {
            Function function = DBStore.getInstance().getFunction(processorName);
            
            if (function == null) {
                //discovery
                function = SqlExpressUtil.lookupFunction(udc, processorName);
                
                if (function == null) {
                    throw new UnsupportedDataProcessorNameException();
                }
                else {
                    DBStore.getInstance().addFunction(processorName, function);
                }
            }
            
            selectedDataProcessor = new FunctionProcessor(function);
        }
        else if (DataProcessorTypes.NAMED_SQL_STATEMENT_PROCESSOR.equals(processorType)) {
            selectedDataProcessor = DBStore.getInstance().getJdbcStatementProcessor(processorName);
        	if (selectedDataProcessor == null) {
        		JdbcStatement statement = SqlExpressUtil.createJdbcStatement(processorName);
                selectedDataProcessor = new JdbcStatementProcessor(statement);
                DBStore.getInstance().addJdbcStatementProcessor(processorName, 
                		(JdbcStatementProcessor)selectedDataProcessor);
        	}
            setDatabaseMetaData(udc, selectedDataProcessor);
        }
        else if (DataProcessorTypes.DIRECT_SQL_STATEMENT_PROCESSOR.equals(processorType)) {
        	selectedDataProcessor = DBStore.getInstance().getJdbcStatementProcessor(processorName);
        	if (selectedDataProcessor == null) {
        		JdbcStatement statement = SqlExpressUtil.createJdbcStatementDirect(processorName);
                selectedDataProcessor = new JdbcStatementProcessor(statement);
                DBStore.getInstance().addJdbcStatementProcessor(processorName, 
                		(JdbcStatementProcessor)selectedDataProcessor);
        	}
            setDatabaseMetaData(udc, selectedDataProcessor);
        }
        else {
            throw new UnsupportedDataProcessorTypeException();
        }
        
        return selectedDataProcessor;
    }
    
    private void setDatabaseMetaData(UserDatabaseConnection udc, DataProcessor selectedDataProcessor) {
        try {
            ((JdbcStatementProcessor)selectedDataProcessor).setDatabaseMetaData(udc.getConnection().getMetaData());
        } catch (Exception ex) {
            ;
        }
    }
}
