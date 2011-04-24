/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.sqldataexpress.processor;

import java.io.StringReader;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.scooterframework.orm.sqldataexpress.connection.UserDatabaseConnection;
import com.scooterframework.orm.sqldataexpress.exception.BaseSQLException;
import com.scooterframework.orm.sqldataexpress.object.Cursor;
import com.scooterframework.orm.sqldataexpress.object.OmniDTO;
import com.scooterframework.orm.sqldataexpress.object.Parameter;
import com.scooterframework.orm.sqldataexpress.object.RowData;
import com.scooterframework.orm.sqldataexpress.object.RowInfo;
import com.scooterframework.orm.sqldataexpress.object.StoredProcedure;
import com.scooterframework.orm.sqldataexpress.object.TableData;
import com.scooterframework.orm.sqldataexpress.util.DAOUtil;
import com.scooterframework.orm.sqldataexpress.vendor.DBAdapter;
import com.scooterframework.orm.sqldataexpress.vendor.DBAdapterFactory;


/**
 * StoredProcedureProcessor class.
 * 
 * @author (Fei) John Chen
 */
public class StoredProcedureProcessor extends DataProcessorImpl {
    private StoredProcedure sp = null;
    
    public StoredProcedureProcessor(StoredProcedure sp) {
        this.sp = sp;
    }
    
    /**
     * execute with output filter
     */
    public OmniDTO execute(UserDatabaseConnection udc, Map<String, Object> inputs, Map<String, String> outputFilters) 
    throws BaseSQLException {
    	Connection connection = udc.getConnection();
    	DBAdapter dba = DBAdapterFactory.getInstance().getAdapter(udc.getConnectionName());
    	
        OmniDTO returnTO = new OmniDTO();
        CallableStatement cstmt = null;
        
        try {
            cstmt = connection.prepareCall(sp.getJavaAPIString());
            
            // check
            int inputCount = sp.getInputParameterCount();
            
            if ( inputs.size() < inputCount ) throw new Exception("Input parameters insufficient exception.");
            
            // set parameters
            Collection<Parameter> parameters = sp.getParameters();
            Iterator<Parameter> pit = parameters.iterator();
            while(pit.hasNext()) {
                Parameter p = pit.next();
                if (Parameter.MODE_INOUT.equals(p.getMode()) ||
                    Parameter.MODE_OUT.equals(p.getMode()) ||
                    Parameter.MODE_RETURN.equals(p.getMode())) { //function return
                    cstmt.registerOutParameter(p.getIndex(), p.getSqlDataType());
                }
                
                if (Parameter.MODE_IN.equals(p.getMode()) ||
                    Parameter.MODE_INOUT.equals(p.getMode())) {
                    String key = p.getName();
                    if ( !inputs.containsKey(key) ) throw new Exception("There must be a key/value pair corresponding to key " + key + " in input parameters.");
                    
                    Object obj = inputs.get(key);
                    if (obj == null || 
                        "".equals(obj.toString()) && 
                        p.getSqlDataType() != Types.CHAR && 
                        p.getSqlDataType() != Types.VARCHAR && 
                        p.getSqlDataType() != Types.LONGVARCHAR) {
                        setNull(cstmt, p.getIndex(), p.getSqlDataType());
                    }
                    else {
                        if ("oracle.sql.CLOB".equals(p.getJavaClassName())) {
                            String tmp = (String)obj;
                            int strLength = tmp.length();
                            StringReader r = new StringReader(tmp);
                            cstmt.setCharacterStream(p.getIndex(), r, strLength);
                            r.close();
                        }
                        else if (Parameter.UNKNOWN_SQL_DATA_TYPE != p.getSqlDataType()) {
                            setObject(cstmt, obj, p);
                        }
                        else {
                            //It is up to JDBC driver's PreparedStatement implementation 
                            //class to deal with. Usually the class will make a decision 
                            //on which setXXX(Type) method to call based on the type of 
                            //the obj instance.
                            cstmt.setObject(p.getIndex(), obj);
                        }
                    }
                }
            }
            
            cstmt.execute();
            
            // handle out cursors or other outputs if there is any
            if ( outputFilters == null ) {
                handleResultSet(dba, returnTO, cstmt);
            }
            else {
                handleFilteredResultSet(dba, returnTO, cstmt, outputFilters);
            }
        }
        catch (Exception ex) {
            throw new BaseSQLException(ex);
        }
        finally {
            DAOUtil.closeStatement(cstmt);
        }
        
        return returnTO;
    }

    private void handleResultSet(DBAdapter dba, OmniDTO returnTO, CallableStatement cstmt) 
    throws SQLException {
        // handle out cursors or other outputs if there is any
        Iterator<Parameter> pit = sp.getParameters().iterator();
        while(pit.hasNext()) {
            Parameter p = pit.next();
            if (Parameter.MODE_INOUT.equals(p.getMode()) ||
                Parameter.MODE_OUT.equals(p.getMode()) ||
                Parameter.MODE_RETURN.equals(p.getMode())) { //function return
                if ( p.isCursorType() ) {
                    ResultSet rs = (ResultSet)cstmt.getObject(p.getIndex());
                    
                    Cursor cursor = sp.getCursor(p.getName(), rs);
                    int cursorWidth = cursor.getDimension();
                    
                    TableData rt = new TableData();
                    rt.setHeader(cursor);
                    returnTO.addTableData(p.getName(), rt);
                    
                    while(rs.next()) {
                        Object[] cellValues = new Object[cursorWidth];
                        for ( int i = 0; i < cursorWidth; i++ ) {
                            cellValues[i] = dba.getObjectFromResultSetByType(rs, 
                                                                         cursor.getColumnJavaClassName(i), 
                                                                         cursor.getColumnSqlDataType(i),
                                                                         i+1);
                        }
                        rt.addRow(new RowData(cursor, cellValues));
                    }
                    rs.close();
                } 
                else {
                    returnTO.addNamedObject(p.getName(), dba.getObjectFromStatementByType(cstmt, 
                                                                                      p.getJavaClassName(), 
                                                                                      p.getSqlDataType(), 
                                                                                      p.getIndex()));
                }
            }
        }
    }
    
    private void handleFilteredResultSet(DBAdapter dba, OmniDTO returnTO, CallableStatement cstmt, Map<String, String> outputFilter) 
    throws SQLException {
        // handle out cursors or other outputs if there is any
        Iterator<Parameter> pit = sp.getParameters().iterator();
        while(pit.hasNext()) {
            Parameter p = (Parameter) pit.next();
            if (Parameter.MODE_INOUT.equals(p.getMode()) ||
                Parameter.MODE_OUT.equals(p.getMode()) ||
                Parameter.MODE_RETURN.equals(p.getMode())) { //function return
                if (p.isCursorType()) {
                    ResultSet rs = (ResultSet)cstmt.getObject(p.getIndex());
                    
                    Cursor cursor = sp.getCursor(p.getName(), rs);
                    int cursorWidth = cursor.getDimension();
                    
                    Set<String> allowedColumns = getAllowedColumns(outputFilter, cursor);
                    TableData rt = new TableData();
                    RowInfo newHeader = getFilteredHeaderInfo(allowedColumns, cursor);
                    rt.setHeader(newHeader);
                    returnTO.addTableData(p.getName(), rt);
                    
                    while(rs.next()) {
                        ArrayList<Object> cellValues = new ArrayList<Object>();
                        for (int i = 0; i < cursorWidth; i++) {
                            if (outputFilter.containsKey(cursor.getColumnName(i))) {
                                cellValues.add(dba.getObjectFromResultSetByType(rs, 
                                                                            cursor.getColumnJavaClassName(i), 
                                                                            cursor.getColumnSqlDataType(i),
                                                                            i+1));
                            }
                        }
                        
                        if (cellValues.size() > 0) 
                            rt.addRow(new RowData(newHeader, cellValues.toArray()));
                    }
                    rs.close();
                } 
                else {
                    if (outputFilter.containsKey(p.getName())) {
                        returnTO.addNamedObject(p.getName(), dba.getObjectFromStatementByType(cstmt, 
                                                                                          p.getJavaClassName(), 
                                                                                          p.getSqlDataType(), 
                                                                                          p.getIndex()));
                    }
                }
            }
        }
    }
}
