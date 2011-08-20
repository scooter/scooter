/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.sqldataexpress.object;


/**
 * OracleParameter class has Oracle specific parameter handling methods.
 * 
 * @author (Fei) John Chen
 */
public class OracleParameter extends Parameter {
    public OracleParameter(int index, String name, String mode, int sqlDataType, String sqlDataTypeName) {
        super(index, name, mode, sqlDataType, sqlDataTypeName);
        
        // reset mode values for Oracle: java.sql.Types.OTHER = 1111;
        if ( sqlDataType == java.sql.Types.OTHER ) {
            this.sqlDataType = -10; //-10=oracle.jdbc.driver.OracleTypes.CURSOR;
            this.bIsCursorType = true;
        }
    }
    
    /**
     * Retrieves the the Java class type of the type of SQL type passed in.
     * 
     * @param sqlDataType sql data type 
     * @return The Java class type name.
     */
    public String getJavaType(int sqlDataType) {
        String javaClassName = null;
        
        switch (sqlDataType){
            case 2004: //2004=oracle.jdbc.driver.OracleTypes.BLOB
                javaClassName = "oracle.sql.BLOB";
                break;
            case 2005: //2005=oracle.jdbc.driver.OracleTypes.CLOB
                javaClassName = "oracle.sql.CLOB";
                break;
            default:
                javaClassName = null;
        }
        
        if (javaClassName == null) javaClassName = super.getJavaType(sqlDataType);
        
        return javaClassName;
    }
}
