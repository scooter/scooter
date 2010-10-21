/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.sqldataexpress.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import com.mchange.v2.c3p0.PooledDataSource;
import com.scooterframework.common.logging.LogUtil;
import com.scooterframework.common.util.StringUtil;
import com.scooterframework.common.util.Util;
import com.scooterframework.orm.sqldataexpress.config.DatabaseConfig;
import com.scooterframework.orm.sqldataexpress.config.SqlConfig;
import com.scooterframework.orm.sqldataexpress.connection.DatabaseConnectionContext;
import com.scooterframework.orm.sqldataexpress.connection.UserDatabaseConnection;
import com.scooterframework.orm.sqldataexpress.exception.BaseSQLException;
import com.scooterframework.orm.sqldataexpress.exception.CreateConnectionFailureException;
import com.scooterframework.orm.sqldataexpress.exception.TransactionException;
import com.scooterframework.orm.sqldataexpress.exception.UnexpectedDataException;
import com.scooterframework.orm.sqldataexpress.exception.UnsupportedDataProcessorNameException;
import com.scooterframework.orm.sqldataexpress.exception.UnsupportedDataProcessorTypeException;
import com.scooterframework.orm.sqldataexpress.exception.UnsupportedStoredProcedureAPINameException;
import com.scooterframework.orm.sqldataexpress.object.OmniDTO;
import com.scooterframework.orm.sqldataexpress.object.RowData;
import com.scooterframework.orm.sqldataexpress.object.RowInfo;
import com.scooterframework.orm.sqldataexpress.object.TableData;
import com.scooterframework.orm.sqldataexpress.processor.DataProcessor;
import com.scooterframework.orm.sqldataexpress.processor.DataProcessorFactory;
import com.scooterframework.orm.sqldataexpress.processor.DataProcessorTypes;
import com.scooterframework.orm.sqldataexpress.util.SqlUtil;
import com.scooterframework.transaction.ImplicitTransactionManager;
import com.scooterframework.transaction.Transaction;
import com.scooterframework.transaction.TransactionManager;
import com.scooterframework.transaction.TransactionManagerUtil;

/**
 * SqlServiceImpl class implements SqlService interface. 
 * 
 * @author (Fei) John Chen
 */
public class SqlServiceImpl implements SqlService {
    public SqlServiceImpl() {}

    
    /**
     * Begin a transaction of default transaction type.
     * 
     * The default transaction type is JDBC if it is not defined in property file. 
     */
    public void beginTransaction() 
    throws TransactionException {
        getTransactionManager().beginTransaction();
    }
    
    /**
     * Begin a transaction of a specific type.
     */
    public void beginTransaction(String type) 
    throws TransactionException {
        getTransactionManager().beginTransaction(type);
    }
    
    /**
     * Commit a transaction. 
     */
    public void commitTransaction() 
    throws TransactionException {
        getTransactionManager().commitTransaction();
    }
    
    /**
     * Rollback a transaction. 
     */
    public void rollbackTransaction() 
    throws TransactionException {
        getTransactionManager().rollbackTransaction();
    }
    
    /**
     * Release all resources hold by this transaction. 
     */
    public void releaseResources() 
    throws TransactionException {
        getTransactionManager().releaseResources();
    }
    
    /**
     * execute without output filter
     */
    public OmniDTO execute(Map inputs, String processorType, String processorName) 
    throws BaseSQLException {
        return execute(inputs, processorType, processorName, null);
    }
    
    /**
     * execute with output filter
     */
    public OmniDTO execute(Map inputs, String processorType, String processorName, Map outputFilters) 
    throws BaseSQLException {
        if (processorType == null || processorName == null) 
            throw new IllegalArgumentException("processorType or processorName is null.");
        
        if (inputs == null) inputs = new HashMap();
        
        ImplicitTransactionManager tm = TransactionManagerUtil.getImplicitTransactionManager();
        OmniDTO returnTO = null;
        
        try {
            tm.beginTransactionImplicit();
            
            //if (log.isDebugEnabled()) displayDS((String)inputs.get(DataProcessor.input_key_database_connection_name), "beforeConnection");
            
            UserDatabaseConnection connection = findOrCreateConnection(inputs);
            
            //if (log.isDebugEnabled()) displayDS((String)inputs.get(DataProcessor.input_key_database_connection_name), "beforeExecute");
            returnTO = executeKeepConnection(connection, inputs, processorType, processorName, outputFilters);
            //if (log.isDebugEnabled()) displayDS((String)inputs.get(DataProcessor.input_key_database_connection_name), "afterExecute");
            
            tm.commitTransactionImplicit();
            //if (log.isDebugEnabled()) displayDS((String)inputs.get(DataProcessor.input_key_database_connection_name), "afterCommit");
        }
        catch(BaseSQLException bdex) {
            tm.rollbackTransactionImplicit();
            throw bdex;
        }
        finally {
            tm.releaseResourcesImplicit();
            displayDS((String)inputs.get(DataProcessor.input_key_database_connection_name), "afterRelease");
        }
        
        return returnTO;
    }
	
	private void displayDS(String connectionName, String where) {
        try {
            DataSource ds = null;
            if (connectionName == null) {
                ds = (DataSource) DatabaseConfig.getInstance().getPooledDataSource();
            }
            else {
                ds = (DataSource) DatabaseConfig.getInstance().getPooledDataSource(connectionName);
            }
            
            // make sure it's a c3p0 PooledDataSource 
            if (ds != null && (ds instanceof PooledDataSource)) {
                PooledDataSource pds = (PooledDataSource) ds;
                log.debug("displayDS for " + where + " -      num_connections: " + pds.getNumConnectionsDefaultUser());
                log.debug("displayDS for " + where + " - num_busy_connections: " + pds.getNumBusyConnectionsDefaultUser());
                log.debug("displayDS for " + where + " - num_idle_connections: " + pds.getNumIdleConnectionsDefaultUser());
            }
        } catch(Exception ex) {
            log.debug("displayDS for " + where + " - ERROR: " + ex.getMessage());
        }
    }
    
    /**
     * execute a collection of InputInfo objects in one transaction
     */
    public Collection execute(Collection inputInfoList) 
    throws BaseSQLException {
        if (inputInfoList == null) 
            throw new IllegalArgumentException("inputs list is null.");
        
        ImplicitTransactionManager tm = TransactionManagerUtil.getImplicitTransactionManager();
        Collection returnTOList = new ArrayList();
        
        try {
            tm.beginTransactionImplicit();
            
            Iterator it = inputInfoList.iterator();
            while(it.hasNext()) {
                InputInfo ip = (InputInfo)it.next();
                UserDatabaseConnection connection = findOrCreateConnection(ip);
                OmniDTO returnTO = executeKeepConnection(connection, ip.getInputs(), ip.getProcessorType(), ip.getProcessorName(), ip.getOutputFilters());
                returnTOList.add(returnTO);
                
                //now execute child InputInfo
                Collection childList = ip.getChildInputInfoObjects();
                Iterator childIt = childList.iterator();
                while(childIt.hasNext()) {
                    InputInfo childIp = (InputInfo)childIt.next();
                    OmniDTO returnTO2 = executeKeepConnection(connection, childIp.getInputs(), childIp.getProcessorType(), childIp.getProcessorName(), childIp.getOutputFilters());
                    returnTO.addChildrenOmniDTOToList(returnTO2);
                }
            }
            
            tm.commitTransactionImplicit();
        }
        catch(BaseSQLException bdex) {
            tm.rollbackTransactionImplicit();
            throw bdex;
        }
        finally {
            tm.releaseResourcesImplicit();
        }
        
        return returnTOList;
    }
    
    /**
     * execute a collection of InputInfo objects in one transaction
     */
    public OmniDTO retrieveMasterDetails(InputInfo inputInfo) 
    throws BaseSQLException {
        if (inputInfo == null) 
            throw new IllegalArgumentException("inputInfo is null.");
        
        ImplicitTransactionManager tm = TransactionManagerUtil.getImplicitTransactionManager();
        OmniDTO returnTO = null;
        
        try {
            tm.beginTransactionImplicit();
            
            InputInfo ip = inputInfo;
            UserDatabaseConnection udc = findOrCreateConnection(ip);
            udc.getConnection().setReadOnly(true);
            
            returnTO = executeKeepConnection(udc, ip.getInputs(), ip.getProcessorType(), ip.getProcessorName(), ip.getOutputFilters());
            log.debug("parent: " + returnTO);
            
            //now execute child InputInfo
            Collection childList = ip.getChildInputInfoObjects();
            Iterator childIt = childList.iterator();
            while(childIt.hasNext()) {
                InputInfo childIp = (InputInfo)childIt.next();
                
                // find all input parameters in childIp that need data from parent
                List connectorList = new ArrayList();
                Map childInputs = childIp.getInputs();
                childInputs = convertKeyCase(childInputs);
                Iterator it = childInputs.keySet().iterator();
                while(it.hasNext()) {
                    String key = (String)it.next();
                    String value = (String)childInputs.get(key);
                    if (key.startsWith("&")) connectorList.add(value);
                }
                
                // create a select union query
                String query = null;
                if (DataProcessorTypes.NAMED_SQL_STATEMENT_PROCESSOR.equals(childIp.getProcessorType())) {
                    query = SqlConfig.getInstance().getSql(childIp.getProcessorName());
                }
                else 
                if (DataProcessorTypes.DIRECT_SQL_STATEMENT_PROCESSOR.equals(childIp.getProcessorType())) {
                    query = childIp.getProcessorName();
                }
                
                log.debug("child query1: " + query);
                
                // check if parent has data
                boolean parentHasData = false;
                TableData parentRt = null;
                if (returnTO != null) {
                    parentRt = returnTO.getTableData(ip.getProcessorName());
                    if (parentRt != null) {
                        int size = parentRt.getAllRows().size();
                        if (size > 0) parentHasData = true;
                    }
                }
                
                // construct child query
                String childQuery = "";
                if (query != null && connectorList.size() > 0 && parentHasData) {
                    childQuery = getNewChildQuery(query, childIp, parentRt.getAllRows());
                }
                else {
                    childQuery = query;
                }
                
                log.debug("child query2: " + childQuery);
                
                if (parentHasData) {
                    udc = findOrCreateConnection(childIp);
                    
                    OmniDTO returnTO2 = 
                        executeKeepConnection(udc, 
                                              childIp.getInputs(), 
                                              DataProcessorTypes.DIRECT_SQL_STATEMENT_PROCESSOR, 
                                              childQuery, 
                                              childIp.getOutputFilters());
                    
                    // merge child records with corresponding parent record
                    if (returnTO2 != null) {
                        linkParentWithChild(parentRt, 
                                            returnTO2.getTableData(childQuery), 
                                            childIp.getProcessorName(), 
                                            connectorList);
                    }
                    
                    log.debug("returnTO2: " + returnTO2);
                }
            }
        }
        catch(SQLException ex) {
            throw new BaseSQLException(ex);
        }
        catch(BaseSQLException bdex) {
            throw bdex;
        }
        finally {
            tm.releaseResourcesImplicit();
        }
        
        return returnTO;
    }
    
    /**
     * execute
     */
    public OmniDTO execute(Collection inputParameters, String processorType, String processorName) 
    throws BaseSQLException {
        return execute(inputParameters, processorType, processorName, null);
    }
    
    /**
     * execute of InputInfo with output filter
     */
    public OmniDTO execute(Collection inputParameters, String processorType, String processorName, Map outputFilters) 
    throws BaseSQLException {
        if (inputParameters == null) throw new IllegalArgumentException("inputs is null.");
        
        Map inputsMap = new HashMap();
        
        Iterator it = inputParameters.iterator();
        while (it.hasNext()) {
            InputParameter ip = (InputParameter) it.next();
            inputsMap.put(ip.getName(), ip.getValue());
        }
        
        return execute(inputsMap, processorType, processorName, outputFilters);
    }
    
    
    /**
     * Retrieve a single row data from database. If more than one records is
     * returned, an UnexpectedDataException will be thrown. 
     * 
     * If DataProcessor.input_key_records_fixed key has value "true" in inputs, 
     * absolute fixed number of records is required. An UnexpectedDataException 
     * will be thrown if the number of retrieved records is not equal to one.
     * 
     * @param inputs            Map of input data
     * @param processorType     A named sql or direct sql or stored procedure
     * @param processorName     Sql name or sql itself or stored procedure name
     * @return TableData        The row data
     * @throws com.scooterframework.orm.sqldataexpress.exception.BaseSQLException
     */
    public TableData retrieveRow(Map inputs, String processorType, String processorName) 
    throws BaseSQLException {
        return retrieveRows(inputs, processorType, processorName, 1);
    }
    
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
    throws BaseSQLException {
        return retrieveRows(inputs, processorType, processorName, DataProcessor.NO_ROW_LIMIT);
    }
    
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
    throws BaseSQLException {
        return retrieveRows(inputs, processorType, processorName, limitOrFixed, 0);
    }
    
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
    throws BaseSQLException {
        if (processorType == null || processorName == null) 
            throw new IllegalArgumentException("processorType or processorName is null.");
        
        if (inputs == null) inputs = new HashMap();
        
        inputs.put(DataProcessor.input_key_records_offset, new Integer(offset));
        inputs.put(DataProcessor.input_key_records_limit, new Integer(limitOrFixed));
        
        OmniDTO dto = execute(inputs, processorType, processorName);
        TableData td = dto.getTableData(processorName);
        
        if (limitOrFixed != DataProcessor.NO_ROW_LIMIT) {
            if (td.getTableSize() > limitOrFixed) {
                throw new UnexpectedDataException("Failed to retrieveRows: required " + 
                    limitOrFixed + " but retrieved " + td.getTableSize() + ".");
            }
            else {
                boolean requireFixed = Util.getBooleanValue(inputs, DataProcessor.input_key_records_fixed, false);
                if (requireFixed) {
                    if (td.getTableSize() != limitOrFixed) {
                        throw new UnexpectedDataException("Failed to retrieveRows: required only " + 
                            limitOrFixed + " but retrieved " + td.getTableSize() + ".");
                    }
                }
            }
        }
        return td;
    }
    
    
    /**
     * Insert data to database.
     * 
     * @param inputs            Map of input data
     * @param processorType     A named sql or direct sql or stored procedure
     * @param processorName     Sql name or sql itself or stored procedure name
     * @throws com.scooterframework.orm.sqldataexpress.exception.BaseSQLException
     */
    public void insert(Map inputs, String processorType, String processorName) 
    throws BaseSQLException {
        execute(inputs, processorType, processorName);
    }
    
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
    throws BaseSQLException {
        OmniDTO dto = execute(inputs, processorType, processorName);
        return dto.getUpdatedRowCount();
    }
    
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
    throws BaseSQLException {
        OmniDTO dto = execute(inputs, processorType, processorName);
        return dto.getUpdatedRowCount();
    }
    
    
    private TransactionManager getTransactionManager() {
        return TransactionManagerUtil.getTransactionManager();
    }

    /**
     * Find or create a new connection
     * 
     * @param inputs    Map of inputs
     * @return UserDatabaseConnection object
     */
    private UserDatabaseConnection findOrCreateConnection(Map inputs) {
    	UserDatabaseConnection udc = null;
        DatabaseConnectionContext dcc = 
            (DatabaseConnectionContext)inputs.get(DataProcessor.input_key_database_connection_context);
        String connectionName = (String)inputs.get(DataProcessor.input_key_database_connection_name);
        if (dcc != null && connectionName != null) {
            throw new IllegalArgumentException("You cannot have both connection name and dcc in the same inputs map.");
        }
        else {
            if (dcc != null) {
                udc = getConnection(dcc);
            }
            else if (connectionName != null) {
                udc = getConnection(connectionName);
            }
            else {
                udc = getConnection();
            }
        }
        
        return udc;
    }
    
    /**
     * Find or create a new connection
     * 
     * @param ip        InputInfo object
     * @return UserDatabaseConnection object
     */
    private UserDatabaseConnection findOrCreateConnection(InputInfo ip) {
    	UserDatabaseConnection udc = null;
        DatabaseConnectionContext dcc = ip.getDatabaseConnectionContext();
        String connectionName = ip.getConnectionName();
        
        if (dcc != null && connectionName != null) {
            throw new IllegalArgumentException("You cannot have both connection name and dcc in the same InputInfo object.");
        }
        else {
            if (dcc != null) {
                udc = getConnection(dcc);
            }
            else if (connectionName != null) {
                udc = getConnection(connectionName);
            }
            else {
                udc = getConnection();
            }
        }
        
        return udc;
    }

    /**
     * Get a database connection. 
     * 
     * A transaction must have been started before this method is called.
     *
     * @return      UserDatabaseConnection     represents the DB connection.
     * @exception   BaseSQLException		
     */
    private UserDatabaseConnection getConnection()
    throws BaseSQLException {
    	UserDatabaseConnection conn = null;
        
        try {
            Transaction ts = getTransactionManager().getTransaction();
            
            if (ts == null || !ts.isTransactionStarted())  {
                throw new TransactionException("getConnection() failed: no started transaction.");
            }
            
            conn = ts.getConnection();
            
            if (conn == null) throw new CreateConnectionFailureException("getConnection() failed to create a connection.");
        }
        catch(CreateConnectionFailureException ccfex) {
            throw new BaseSQLException(ccfex);
        }
        catch(Exception ex) {
            throw new BaseSQLException(ex);
        }
        
        return conn;
    }

    /**
     * Get a database connection based on connection name. 
     * 
     * A transaction must have been started before this method is called.
     * 
     * @return      UserDatabaseConnection     represents the DB connection.
     * @exception   BaseSQLException		
     */
    private UserDatabaseConnection getConnection(String connectionName)
    throws BaseSQLException {
    	UserDatabaseConnection conn = null;
        
        try {
            Transaction ts = getTransactionManager().getTransaction();
            
            if (ts == null || !ts.isTransactionStarted())  {
                throw new TransactionException("getConnection() failed: no started transaction.");
            }
            
            conn = ts.getConnection(connectionName);
            
            if (conn == null) throw new CreateConnectionFailureException("getConnection() failed to create a connection.");
        }
        catch(CreateConnectionFailureException ccfex) {
            throw new BaseSQLException(ccfex);
        }
        catch(Exception ex) {
            throw new BaseSQLException(ex);
        }
        
        return conn;
    }

    /**
     * Get a database connection based on connection context.
     * 
     * A transaction must have been started before this method is called.
     * 
     * @return      UserDatabaseConnection     represents the DB connection.
     * @exception   BaseSQLException		
     */
    private UserDatabaseConnection getConnection(DatabaseConnectionContext dcc)
    throws BaseSQLException {
    	UserDatabaseConnection conn = null;
        
        try {
            Transaction ts = getTransactionManager().getTransaction();
            
            if (ts == null || !ts.isTransactionStarted())  {
                throw new TransactionException("getConnection() failed: no started transaction.");
            }
            
            conn = ts.getConnection(dcc);
            
            if (conn == null) throw new CreateConnectionFailureException("getConnection() failed to create a connection.");
        }
        catch(CreateConnectionFailureException ccfex) {
            throw new BaseSQLException(ccfex);
        }
        catch(Exception ex) {
            throw new BaseSQLException(ex);
        }
        
        return conn;
    }
    
    /**
     * execute with output filter
     */
    private OmniDTO executeKeepConnection(UserDatabaseConnection udc, 
    		Map inputs, String processorType, String processorName, Map outputFilters) 
    throws BaseSQLException {
        if (udc == null) 
            throw new IllegalArgumentException("UserDatabaseConnection object is null.");
        
        if (processorType == null || processorName == null) 
            throw new IllegalArgumentException("processorType or processorName is null.");
        
        cleanUpInputs(inputs);
        if (inputs == null) inputs = new HashMap();
        
        OmniDTO returnTO = null;
        
        try {
            DataProcessor dp = DataProcessorFactory.getInstance().getDataProcessor(udc, processorType, processorName);
            returnTO = dp.execute(udc, convertKeyCase(inputs), outputFilters);
            if (returnTO != null) {
                returnTO.setProcessorType(processorType);
                returnTO.setProcessorName(processorName);
            }
        }
        catch(UnsupportedDataProcessorTypeException udptEx) {
            throw new BaseSQLException("Unsupported DataProcessor Type: " + processorType);
        }
        catch(UnsupportedDataProcessorNameException udpnEx) {
            throw new BaseSQLException("Unsupported DataProcessor Name: " + processorName);
        }
        catch(UnsupportedStoredProcedureAPINameException udpnEx) {
            throw new BaseSQLException("Unsupported DataProcessor Name: " + processorName);
        }
        
        return returnTO;
    }
    
    private void cleanUpInputs(Map inputs) {
    	if (inputs == null) return;
    	inputs.remove("__sitemesh__filterapplied");
    	inputs.remove("_method");
    	inputs.remove("scooter.field_values");
    	inputs.remove("scooter.key.model");
    	inputs.remove("scooter.key.resource");
    	inputs.remove("scooter.key.controller");
    	inputs.remove("scooter.key.controller.path");
    }
    
    private Map convertKeyCase(Map oldMap) {
        Map newMap = new HashMap();
        Set keys = oldMap.keySet();
        Iterator it = keys.iterator();
        while(it.hasNext()) {
            String key = (String)it.next();
            newMap.put(key.toUpperCase(), oldMap.get(key));
        }
        return newMap;
    }
    
    private String populateChildInputs(int rowIndex, Map childInputs, RowData parent, String childQuery) {
        String newQuery = childQuery;
        
        Map newInputs = new HashMap();
        
        Iterator it = childInputs.keySet().iterator();
        while (it.hasNext()) {
            String key = (String)it.next();
            if (key.startsWith("&")) {
                String fkName = key.substring(1);
                String parentKeyName = (String)childInputs.get(key);
                if (parentKeyName != null) {
                    parentKeyName = parentKeyName.toUpperCase();
                    String keyInQuery = "?" + fkName;
                    if (childQuery.indexOf(keyInQuery) != -1) {
                        Object parentData = parent.getField(parentKeyName);
                        String newKeyName = fkName + "_" + rowIndex;
                        newInputs.put(newKeyName, parentData);
                        newQuery = StringUtil.replace(newQuery, keyInQuery, "?" + newKeyName);
                    }
                }
            }
        }
        
        childInputs.putAll(newInputs);
        
        return newQuery;
    }
    
    //return a string liek this: (?fkName1_rowIndex1, ?fkName2_rowIndex2, ...)
    private String populateConditionPart2(int rowIndex, InputInfo childIp,  RowData parent) {
        String part = "(";
        List fkNames = childIp.getFKs();
        if (fkNames != null && fkNames.size() > 0) {
            Map newInputs = new HashMap();
            Map childInputs = childIp.getInputs();
            Iterator it = fkNames.iterator();
            while(it.hasNext()) {
                String fkName = (String)it.next();
                String parentKeyName = (String)childInputs.get("&"+fkName);
                Object parentData = parent.getField(parentKeyName);
                String newKeyName = fkName + "_" + rowIndex;
                newInputs.put(newKeyName, parentData);
                part = part + "?" + newKeyName + getColumnSqlDataTypeName(parentKeyName, parent) + ",";
            }
            childInputs.putAll(newInputs);
            
            //remove the last comma
            if (part.endsWith(",")) part = part.substring(0, part.lastIndexOf(','));
            part = part + ")";
        }
        
        return part;
    }
    
    private String getColumnSqlDataTypeName(String parentKeyName, RowData parent) {
        String sqlDataTypeName = "";
        RowInfo ri = parent.getRowInfo();
        if (ri != null) {
            sqlDataTypeName = ri.getColmnDataTypeName(ri.getColumnPositionIndex(parentKeyName));
        }
        
        if (sqlDataTypeName != null && !sqlDataTypeName.equals("")) 
            sqlDataTypeName = ":" + sqlDataTypeName;
        
        return sqlDataTypeName;
    }
    
    private void linkParentWithChild(TableData parentRt, TableData childRt, String processorName, List connectorList) {
        if(parentRt == null || childRt == null || childRt.getTableSize() == 0) return;
        
        int size = parentRt.getAllRows().size();
        Map connectorMap = new HashMap();
        for (int i = 0; i < size; i++) {
            RowData parentRow = parentRt.getRow(i);
            populateConnectorMap(connectorMap, parentRow, connectorList);
            parentRow.addChildRowToMap(processorName, getMatchingRowDataList(connectorMap, childRt));
            
            //clear the map for next row
            connectorMap.clear();
        }
    }
    
    private void populateConnectorMap(Map connectorMap, RowData parentRow, List connectorList) {
        for (Iterator it = connectorList.iterator();it.hasNext();) {
            String key = (String)it.next();
            connectorMap.put(key, parentRow.getField(key));
        }
    }
    
    private List getMatchingRowDataList(Map connectorMap, TableData childRt) {
        List filteredList = new ArrayList();
        boolean allPassed = true;
        int size = childRt.getTableSize();
        for (int i=0; i<size; i++) {
            RowData child = childRt.getRow(i);
            
            for (Iterator it = connectorMap.keySet().iterator();it.hasNext();) {
                String key = (String)it.next();
                Object keyData = connectorMap.get(key);
                Object rowData = child.getField(key);
                if (rowData == null || !rowData.toString().equalsIgnoreCase(keyData.toString())) {
                    allPassed = false;
                    break;
                }
            }
            
            if (allPassed) {
                filteredList.add(child);
            }
            
            allPassed = true;
        }
        
        return filteredList;
    }
    
    private String getNewChildQuery(String query, InputInfo childIp, List parentRows) {
        if (query == null || childIp == null || 
            parentRows == null || parentRows.size() == 0) 
            return query;//nothing need to be changed.
        
        query = SqlUtil.convertToUpperCase(query);
        String childQuery = query;
        
        //generate a child query as union
        if (InputInfo.CONSTRUCT_CHILD_QUERY_THRU_UNION.equals(childIp.getChildQueryType())) {
            int size = parentRows.size();
            if (size > 0) {
                int rowIndex = 0;
                for (int i = 0; i < size -1; i++) {
                    rowIndex = i;
                    RowData parentRow = (RowData)parentRows.get(i);
                    childQuery += populateChildInputs(rowIndex, childIp.getInputs(), parentRow, query) + " UNION ";
                }
                
                //last row
                rowIndex = size -1;
                RowData parentRow = (RowData)parentRows.get(rowIndex);
                childQuery += populateChildInputs(rowIndex, childIp.getInputs(), parentRow, query);
            }
        }
        else 
        if (InputInfo.CONSTRUCT_CHILD_QUERY_MAKE_NEW_WHERE_CLAUSE.equals(childIp.getChildQueryType())) {
            int size = parentRows.size();
            if (size > 0) {
                String conditionPart1 = "(" + childIp.getFKString() + ") in ";
                String conditionPart2 = "";
                int rowIndex = 0;
                for (int i = 0; i < size -1; i++) {
                    rowIndex = i;
                    RowData parentRow = (RowData)parentRows.get(i);
                    conditionPart2 += populateConditionPart2(rowIndex, childIp, parentRow) + ", ";
                }
                
                //last row
                rowIndex = size -1;
                RowData parentRow = (RowData)parentRows.get(rowIndex);
                conditionPart2 += populateConditionPart2(rowIndex, childIp, parentRow);
                childQuery += " WHERE " + conditionPart1 + "(" + conditionPart2 + ")";
            }
        }
        else 
        if (InputInfo.CONSTRUCT_CHILD_QUERY_ADD_TO_WHERE_CLAUSE.equals(childIp.getChildQueryType())) {
            int size = parentRows.size();
            if (size > 0) {
                String conditionPart1 = "(" + childIp.getFKString() + ") in ";
                String conditionPart2 = "";
                int rowIndex = 0;
                for (int i = 0; i < size -1; i++) {
                    rowIndex = i;
                    RowData parentRow = (RowData)parentRows.get(i);
                    conditionPart2 += populateConditionPart2(rowIndex, childIp, parentRow) + ", ";
                }
                
                //last row
                rowIndex = size -1;
                RowData parentRow = (RowData)parentRows.get(rowIndex);
                conditionPart2 += populateConditionPart2(rowIndex, childIp, parentRow);
                childQuery += " AND " + conditionPart1 + "(" + conditionPart2 + ")";
            }
        }
        
        return childQuery;
    }
    
    private LogUtil log = LogUtil.getLogger(this.getClass().getName());
}
