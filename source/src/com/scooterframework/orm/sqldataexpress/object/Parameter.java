/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.sqldataexpress.object;

import java.sql.ParameterMetaData;
import java.sql.Types;

import com.scooterframework.orm.sqldataexpress.exception.UnsupportedStoredProcedureModeException;
import com.scooterframework.orm.sqldataexpress.util.SqlUtil;

/**
 * Parameter class.
 * 
 * @author (Fei) John Chen
 */
public class Parameter {
    public static final String MODE_UNKONWN = ParameterMetaData.parameterModeUnknown+"";//"0";
    public static final String MODE_IN      = ParameterMetaData.parameterModeIn+"";//"1";
    public static final String MODE_INOUT   = ParameterMetaData.parameterModeInOut+"";//"2";
    public static final String MODE_OUT     = ParameterMetaData.parameterModeOut+"";//"4";
    public static final String MODE_RETURN  = "5";//indicating this is a function's return
    
    public static final int UNKNOWN_SQL_DATA_TYPE = -9999;
    public static final String UNDEFINED = "UNDEFINED";
    
    public static final String FUNCTION_RETURN = "functionReturn";
    
    public Parameter() {}
    
    public Parameter(int index, String name, String mode, int sqlDataType, String sqlDataTypeName) {
        if (!MODE_IN.equals(mode) &&
            !MODE_OUT.equals(mode) &&
            !MODE_RETURN.equals(mode)
       ) throw new UnsupportedStoredProcedureModeException();
        
        if (MODE_RETURN.equals(mode)) name = FUNCTION_RETURN;
        
        if (name == null || "".equals(name)) 
            throw new IllegalArgumentException("Parameter name can not be null or empty.");
        
        this.index = index;
        this.name = name;
        this.mode = mode;
        this.sqlDataType = sqlDataType;
        this.sqlDataTypeName = sqlDataTypeName;
        bIsCursorType = false;
        
        //Note: java.sql.Types.OTHER = 1111;
        if (Types.OTHER == sqlDataType) bIsCursorType = true;
    }
    

    /**
     * returns index
     */
    public int getIndex() {
        return index;
    }
    
    /**
     * sets index
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * returns name
     */
    public String getName() {
        return name;
    }
    
    /**
     * sets name
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * returns catalog
     */
    public String getCatalog() {
        return catalog;
    }
    
    /**
     * sets catalog
     */
    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }
    
    /**
     * returns schema
     */
    public String getSchema() {
        return schema;
    }
    
    /**
     * sets schema
     */
    public void setSchema(String schema) {
        this.schema = schema;
    }

    /**
     * returns mode
     */
    public String getMode() {
        return mode;
    }
    
    /**
     * sets mode
     */
    public void setMode(String mode) {
        this.mode = mode;
    }
    
    /**
     * returns sqlDataType
     */
    public int getSqlDataType() {
        return sqlDataType;
    }
    
    /**
     * sets sqlDataType
     */
    public void setSqlDataType(int sqlDataType) {
        this.sqlDataType = sqlDataType;
    }
    
    /**
     * returns sqlDataTypeName
     */
    public String getSqlDataTypeName() {
        return sqlDataTypeName;
    }
    
    /**
     * sets sqlDataTypeName
     */
    public void setSqlDataTypeName(String sqlDataTypeName) {
        this.sqlDataTypeName = sqlDataTypeName;
        if (UNKNOWN_SQL_DATA_TYPE == sqlDataType) {
            sqlDataType = SqlUtil.getSqlDataTypeFromDataTypeName(sqlDataTypeName);
        }
    }
    
    /**
     * checks if is cursor type
     */
    public boolean isCursorType() {
        return bIsCursorType;
    }
    
    /**
     * returns javaClassName
     */
    public String getJavaClassName() {
        if (javaClassName == null) {
            javaClassName = getJavaType(sqlDataType);
        }
        return javaClassName;
    }
    
    /**
     * sets javaClassName
     */
    public void setJavaClassName(String javaClassName) {
        this.javaClassName = javaClassName;
    }
    
    /**
     * Retrieves the the Java class type of the type of SQL type passed in.
     * 
     * @param sqlDataType sql data type 
     * @return The Java class type name.
     */
    public String getJavaType(int sqlDataType) {
        return SqlUtil.getJavaType(sqlDataType);
    }
    
    /**
     * returns vendor
     */
    public String getVendor() {
        return vendor;
    }
    
    /**
     * sets vendor
     */
    public void setVendor(String vendor) {
        this.vendor = vendor;
    }
    
    /**
     * Returns a string representation of the object.
     * @return String
     */
    public String toString() {
        StringBuilder sb= new StringBuilder();

        sb.append("index = " + index).append(", ");
        sb.append("catalog = " + catalog).append(", ");
        sb.append("schema = " + schema).append(", ");
        sb.append("name = " + name).append(", ");
        sb.append("mode = " + mode).append(", ");
        sb.append("sqlDataType = " + sqlDataType).append(", ");
        sb.append("sqlDataTypeName = " + sqlDataTypeName).append(", ");
        sb.append("javaClassName = " + javaClassName).append(", ");
        sb.append("bIsCursorType = " + bIsCursorType).append(", ");
        sb.append("vendor = " + vendor);
        
        return sb.toString();
    }


    protected int index;
    protected String catalog = null;
    protected String schema = null;
    protected String name = null;
    protected String mode = null;
    protected int sqlDataType = UNKNOWN_SQL_DATA_TYPE;
    protected String sqlDataTypeName = null;
    protected String javaClassName = null;
    protected boolean bIsCursorType;
    protected String vendor;
}
