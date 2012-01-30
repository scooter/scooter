/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.sqldataexpress.processor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import com.scooterframework.common.logging.LogUtil;
import com.scooterframework.common.util.StringUtil;
import com.scooterframework.common.util.Util;
import com.scooterframework.orm.sqldataexpress.connection.UserDatabaseConnection;
import com.scooterframework.orm.sqldataexpress.exception.BaseSQLException;
import com.scooterframework.orm.sqldataexpress.object.Cursor;
import com.scooterframework.orm.sqldataexpress.object.JdbcStatement;
import com.scooterframework.orm.sqldataexpress.object.OmniDTO;
import com.scooterframework.orm.sqldataexpress.object.Parameter;
import com.scooterframework.orm.sqldataexpress.object.RowData;
import com.scooterframework.orm.sqldataexpress.object.RowInfo;
import com.scooterframework.orm.sqldataexpress.object.TableData;
import com.scooterframework.orm.sqldataexpress.parser.JdbcStatementParser;
import com.scooterframework.orm.sqldataexpress.util.DAOUtil;
import com.scooterframework.orm.sqldataexpress.util.SqlExpressUtil;
import com.scooterframework.orm.sqldataexpress.util.SqlUtil;
import com.scooterframework.orm.sqldataexpress.vendor.DBAdapter;
import com.scooterframework.orm.sqldataexpress.vendor.DBAdapterFactory;


/**
 * JdbcStatementProcessor class.
 * 
 * @author (Fei) John Chen
 */
public class JdbcStatementProcessor extends DataProcessorImpl {
    private JdbcStatement st = null;
    
    public JdbcStatementProcessor(JdbcStatement st) {
        this.st = st;
    }

    
    /**
     * execute with output filter
     */
    public OmniDTO execute(UserDatabaseConnection udc, Map<String, Object> inputs, Map<String, String> outputFilters) 
    throws BaseSQLException {
    	Connection connection = udc.getConnection();
    	DBAdapter dba = DBAdapterFactory.getInstance().getAdapter(udc.getConnectionName());
    	
        OmniDTO returnTO = new OmniDTO();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            String stName = st.getName();
            autoFill(udc, inputs);
            
            JdbcStatement jstat = st;
            String originalSql = st.getOriginalJdbcStatementString();
            if(checkPagination(inputs)) {
            	String pagedSql = dba.preparePaginationSql(originalSql, inputs, outputFilters);
            	jstat = SqlExpressUtil.createJdbcStatementDirect(pagedSql);
            }
            
            String executableSql = jstat.getExecutableJdbcStatementString();
            executableSql = autoReplace(executableSql, inputs);
            
            log.debug("execute - parsed expecutable sql: " + executableSql);
            log.debug("execute - parsed inputs: " + inputs);
            log.debug("execute - outputFilters: " + outputFilters);
            
            boolean supportsGetGeneratedKeys = supportsGetGeneratedKeys();
            if (supportsGetGeneratedKeys && !jstat.isSelectStatement()) {
                pstmt = connection.prepareStatement(executableSql, Statement.RETURN_GENERATED_KEYS);
            }
            else {
                pstmt = connection.prepareStatement(executableSql);
            }
            
            // check if need to load parameter properties
//            if (supportParameterMetaData()) {
//                if (!jstat.hasLoadedParameterMetaData()) {
//                    //get parameter meta data if it has not been loaded
//                    ParameterMetaData pmd = pstmt.getParameterMetaData();
//                    ParameterMetaDataLoader pmdl = new ParameterMetaDataLoader(pmd, jstat);
//                    pmdl.loadParameterMetaData();
//                }
//            }
//            else {
                if (!jstat.hasLoadedParameterProperties()) {
                	synchronized(jstat) {
                		if (!jstat.hasLoadedParameterProperties()) {
                            JdbcStatementParser parser = new JdbcStatementParser(udc, jstat);
                            parser.parse();
                		}
                	}
                }
//            }
            
            Collection<Parameter> parameters = jstat.getParameters();
            log.debug("execute - parameters: " + parameters);
            Iterator<Parameter> pit = parameters.iterator();
            while(pit.hasNext()) {
                Parameter p = pit.next();
                
                String key = p.getName();
                if (!inputs.containsKey(key)) {
                	throw new Exception("There " + 
                    "must be a key/value pair corresponding to key named " + key + 
                    " in input parameters: " + inputs.keySet());
                }
                
                if (Parameter.MODE_IN.equals(p.getMode())) {
                    Object obj = inputs.get(key);
                    if (obj == null || 
                        "".equals(obj.toString().trim()) && 
                        p.getSqlDataType() != Types.CHAR && 
                        p.getSqlDataType() != Types.VARCHAR && 
                        p.getSqlDataType() != Types.LONGVARCHAR) {
                        setNull(pstmt, p.getIndex(), p.getSqlDataType());
                    }
                    else {
                        if(!dba.vendorSpecificSetObject(pstmt, obj, p, inputs)) {
                            if (Parameter.UNKNOWN_SQL_DATA_TYPE != p.getSqlDataType()) {
                                setObject(pstmt, obj, p);
                            }
                            else {
                                //It is up to JDBC driver's PreparedStatement implementation 
                                //class to deal with. Usually the class will make a decision 
                                //on which setXXX(Type) method to call based on the java 
                                //class type of the obj instance. 
                                pstmt.setObject(p.getIndex(), obj);
                            }
                        }
                    }
                }
            }
            
            if (jstat.isSelectStatement()) {
                rs = pstmt.executeQuery();
                
                // handle out cursors or other outputs if there is any
                if (rs != null) {
                    if (outputFilters == null || outputFilters.size() == 0) {
                        handleResultSet(jstat, dba, stName, returnTO, rs, inputs);
                    }
                    else {
                        handleFilteredResultSet(jstat, dba, stName, returnTO, rs, inputs, outputFilters);
                    }
                }
            }
            else {
                int rowCount = pstmt.executeUpdate();
                returnTO.setUpdatedRowCount(rowCount);
                
                //get generated key if the underlying database permitted
                if (supportsGetGeneratedKeys) {
                    ResultSet rsg = null;
                    try {
                        rsg = pstmt.getGeneratedKeys();
                        if(rsg.next()) {
                            returnTO.setGeneratedKey(rsg.getLong(1));
                        }
                    }
                    catch(Throwable ex) {
                        DAOUtil.closeResultSet(rsg);
                    }
                }
            }
        }
        catch (Exception ex) {
        	log.error("Error in execute(): " + ex.getMessage(), ex);
            throw new BaseSQLException(ex);
        }
        finally {
            DAOUtil.closeResultSet(rs);
            DAOUtil.closeStatement(pstmt);
        }
        
        return returnTO;
    }

    protected boolean checkPagination(Map<String, Object> inputs) {
        boolean usePagination = false;
        if(st.isSelectStatement()) {
            usePagination = Util.getBooleanValue(inputs, DataProcessor.input_key_use_pagination, false);
            if (!usePagination && inputs != null && !inputs.containsKey(DataProcessor.input_key_use_pagination)) {
                int limit = Util.getIntValue(inputs, DataProcessor.input_key_records_limit, DataProcessor.NO_ROW_LIMIT);
                if (limit != DataProcessor.NO_ROW_LIMIT && limit > 0) {
                    usePagination = true;
                }
            }
        }
        return usePagination;
    }


    //auto fill some values
    private void autoFill(UserDatabaseConnection udc, Map<String, Object> inputs) {
        String jdbcStatementString = st.getOriginalJdbcStatementString();
        
        if (jdbcStatementString.indexOf("?@") == -1) return;//nothing to fill
        
        StringTokenizer sti = new StringTokenizer(jdbcStatementString, " ,><=(){}");
        while(sti.hasMoreTokens()) 
        {
            String token = sti.nextToken();
            
            //replace all occurrences of token by '?'
            if (token.length()>2 && token.startsWith("?@")) {
                String key = token.substring(2);
                
                DataProcessor dp = 
                    DataProcessorFactory.getInstance().getDataProcessor( 
                            udc, 
                            DataProcessorTypes.NAMED_SQL_STATEMENT_PROCESSOR, 
                            key);
                OmniDTO returnTO = dp.execute(udc, inputs);
                Object result = returnTO.getTableData(key).getFirstObject();
                
                log.debug("autoFill: result for key " + key + ": " + result);
                inputs.put("@"+key, result);
            }
        }
    }

    /**
     * Replaces some tokens in the SQL string with data from input map. The 
     * parts that need to be replaced are parts in the SQL string that start 
     * with SqlUtil.REPLACE_PART_START and end with SqlUtil.REPLACE_PART_END.
     * 
     * @param original
     * @param inputs
     * @return an updated SQL String with no replacement part
     */
    private String autoReplace(String original, Map<String, Object> inputs) {
        if (original.indexOf(SqlUtil.REPLACE_PART_START) == -1 &&
            original.indexOf(SqlUtil.REPLACE_PART_END) == -1) return original;
        String replaced = original;
        List<String> replaceKeys = new ArrayList<String>();
        StringTokenizer st = new StringTokenizer(original, " ,");
        while(st.hasMoreTokens()) {
            String token = st.nextToken();
            if (token.startsWith(SqlUtil.REPLACE_PART_START) && token.endsWith(SqlUtil.REPLACE_PART_END)) {
                replaceKeys.add(token);
            }
        }
        
        for(String token : replaceKeys) {
            String key = token.substring(1, token.length()-1);
            Object replaceStr = inputs.get(key);
            if (replaceStr == null) throw new IllegalArgumentException("There is no input data to replace " + token + ".");
            replaced = StringUtil.replace(replaced, token, replaceStr.toString());
        }
        
        return replaced;
    }

	private void handleResultSet(JdbcStatement jstat, DBAdapter dba,
			String stName, OmniDTO returnTO, ResultSet rs, Map<String, ?> inputs)
    throws SQLException {
        Cursor cursor = jstat.getCursor(stName, rs);
        int cursorWidth = cursor.getDimension();
        
        TableData rt = new TableData();
        rt.setHeader(cursor);
        returnTO.addTableData(stName, rt);
        
        while(rs.next()) {
            Object[] cellValues = new Object[cursorWidth];
            for (int i = 0; i < cursorWidth; i++) {
				cellValues[i] = dba.getObjectFromResultSetByType(rs,
						cursor.getColumnJavaClassName(i),
						cursor.getColumnSqlDataType(i), i + 1);
            }
            rt.addRow(new RowData(cursor, cellValues));
        }
        rs.close();
    }
    
	private void handleFilteredResultSet(JdbcStatement jstat, DBAdapter dba,
			String stName, OmniDTO returnTO, ResultSet rs,
			Map<String, Object> inputs, Map<String, String> outputs)
    throws SQLException {
        Cursor cursor = jstat.getCursor(stName, rs);
        int cursorWidth = cursor.getDimension();
        
        Set<String> allowedColumns = getAllowedColumns(outputs, cursor);
        TableData rt = new TableData();
        RowInfo newHeader = getFilteredHeaderInfo(allowedColumns, cursor);
        rt.setHeader(newHeader);
        returnTO.addTableData(stName, rt);
        
        while(rs.next()) {
            ArrayList<Object> cellValues = new ArrayList<Object>();
            for (int i = 0; i < cursorWidth; i++) {
                if (allowedColumns.contains(cursor.getColumnName(i))) {
					cellValues.add(dba.getObjectFromResultSetByType(rs,
							cursor.getColumnJavaClassName(i),
							cursor.getColumnSqlDataType(i), i + 1));
                }
            }
            
            if (cellValues.size() > 0) 
                rt.addRow(new RowData(newHeader, cellValues.toArray()));
        }
        rs.close();
    }
    
    /**
     * Oracle doesn't support ParameterMetaData. 
     * MYSQL doesn't fully support ParameterMetaData.
     * 
     */
    protected boolean supportParameterMetaData() {
        return false;
    }
    
    protected LogUtil log = LogUtil.getLogger(this.getClass().getName());
}
