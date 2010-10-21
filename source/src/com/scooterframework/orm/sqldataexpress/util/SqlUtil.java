/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.sqldataexpress.util;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.scooterframework.common.logging.LogUtil;
import com.scooterframework.common.util.DateUtil;
import com.scooterframework.common.util.StringUtil;
import com.scooterframework.orm.sqldataexpress.config.DatabaseConfig;
import com.scooterframework.orm.sqldataexpress.object.Parameter;
import com.scooterframework.orm.sqldataexpress.object.RowData;

/**
 * SqlUtil class has methods for general sql processing and sql related type 
 * conversion.
 * 
 * @author (Fei) John Chen
 */
public class SqlUtil {
    private static LogUtil log = LogUtil.getLogger(SqlUtil.class.getName());
    
    /**
     * <p>Returns a sql data type int for a sql data type name.</p>
     * 
     * <p>Developers can add types. 
     * See {@link com.scooterframework.orm.sqldataexpress.config.DatabaseConfig} 
     * class for examples.</p>
     * 
     * @param sqlDataTypeName data type name
     * @return sql data type int
     */
    public static int getSqlDataTypeFromDataTypeName(String sqlDataTypeName) {
        String tname = sqlDataTypeName;
        if (tname != null) tname = tname.toUpperCase();
        if (tname.indexOf(' ') != -1) {
            tname = tname.substring(0, tname.indexOf(' '));
        }
        
        Integer sqlDataType = (Integer)DatabaseConfig.getInstance().getSQLDataNameTypesMap().get(tname);
        if (sqlDataType == null) {
            log.warn("No sql data type found in sqlDataTypesMap for sql data type name " + sqlDataTypeName);
            return Parameter.UNKNOWN_SQL_DATA_TYPE;
        }
        
        return sqlDataType.intValue();
    }

    /**
     * Converts from SQL data type to Java class type.
     * 
     * Here is a list of presumed Java class name and its correpsonding sql data type:
     * <pre>
     *  java.sql.Array         public static final int ARRAY 2003 
     *  java.lang.Long         public static final int BIGINT -5 
     *  byte[]                 public static final int BINARY -2 
     *  java.lang.Object       public static final int BIT -7 
     *  java.sql.Blob          public static final int BLOB 2004 
     *  java.lang.Boolean      public static final int BOOLEAN 16 
     *  java.lang.String       public static final int CHAR 1 
     *  java.sql.Clob          public static final int CLOB 2005 
     *  java.lang.Object       public static final int DATALINK 70 
     *  java.sql.TimeStamp     public static final int DATE 91 
     *  java.lang.BigDecimal   public static final int DECIMAL 3 
     *  java.lang.Object       public static final int DISTINCT 2001 
     *  java.lang.Double       public static final int DOUBLE 8 
     *  java.lang.Double       public static final int FLOAT 6 
     *  java.lang.Integer      public static final int INTEGER 4 
     *  java.lang.Object       public static final int JAVA_OBJECT 2000 
     *  byte[]                 public static final int LONGVARBINARY -4 
     *  java.lang.String       public static final int LONGVARCHAR -1 
     *  java.lang.Object       public static final int NULL 0 
     *  java.lang.BigDecimal   public static final int NUMERIC 2 
     *  java.lang.Object       public static final int OTHER 1111 
     *  java.lang.Double       public static final int REAL 7 
     *  java.sql.Ref           public static final int REF 2006 
     *  java.lang.Integer      public static final int SMALLINT 5 
     *  java.sql.Struct        public static final int STRUCT 2002 
     *  java.sql.Time          public static final int TIME 92 
     *  java.sql.TimeStamp     public static final int TIMESTAMP 93 
     *  java.lang.Integer      public static final int TINYINT -6 
     *  byte[]                 public static final int VARBINARY -3 
     *  java.lang.String       public static final int VARCHAR 12
     * </pre>
     * 
     * The following types are simply converted to "java.lang.Object":
     * <ul>
     *   <li>public static final int BIT -7</li>
     *   <li>public static final int DATALINK 70</li>
     *   <li>public static final int DISTINCT 2001</li>
     *   <li>public static final int JAVA_OBJECT 2000</li>
     *   <li>public static final int NULL 0</li>
     *   <li>public static final int OTHER 1111</li>
     * </ul>
     * 
     * <p>Default java type is "java.lang.Object". 
     * 
     * <p>Developers can add types. 
     * See {@link com.scooterframework.orm.sqldataexpress.config.DatabaseConfig} 
     * class for examples.</p>
     * 
     * @param sqlDataType sql data type from java.sql.Types
     * @return The Java class type name.
     */
    public static String getJavaType(int sqlDataType) {
        String javaClassName = null;
        
        switch (sqlDataType){
            case -1: //LONGVARCHAR
            case  1: //CHAR
            case 12: //VARCHAR, VARCHAR2
                javaClassName = "java.lang.String";
                break;
            case  2: //NUMERIC/NUMBER
            case  3: //DECIMAL
                javaClassName = "java.math.BigDecimal";
                break;
            case -6: //TINYINT
            case  4: //INTEGER
            case  5: //SMALLINT
                javaClassName = "java.lang.Integer";
                break;
            case  6: //FLOAT
            case  7: //REAL
            case  8: //DOUBLE
                javaClassName = "java.lang.Double";
                break;
            case -5: //BIGINT
                javaClassName = "java.lang.Long";
                break;
            case 92: //TIME
                javaClassName = "java.sql.Time";
                break;
            case 91: //DATE
            case 93: //TIMESTAMP
                javaClassName = "java.sql.Timestamp";
                break;
            case 16: //BOOLEAN
                javaClassName = "java.lang.Boolean";
                break;
            case 2003: //ARRAY
                javaClassName = "java.sql.Array";
                break;
            case 2004: //BLOB
                javaClassName = "java.sql.Blob";
                break;
            case 2005: //CLOB
                javaClassName = "java.sql.Clob";
                break;
            case 2006: //REF
                javaClassName = "java.sql.Ref";
                break;
            case -2: //BINARY
            case -3: //VARBINARY
            case -4: //LONGVARBINARY
                javaClassName = "byte[]";
                break;
            case 2002: //STRUCT
                javaClassName = "java.sql.Struct";
                break;
            default:
                String jname = (String)DatabaseConfig.getInstance().getSQLTypeJavaNamesMap().get(new Integer(sqlDataType));
                javaClassName = (jname != null)?jname:"java.lang.Object";
        }
        return javaClassName;
    }

    /**
     * Builds a string of sql like string.
     * 
     * Example:
     *      original string: "lower(category) like $keyword$"
     *         words string: "dog, cat, fish"
     *            joinType: OR
     *     converted string: "lower(category) like '%dog%' OR lower(category) like '%cat%' OR lower(category) like '%fish%'"
     * 
     * @param words a string of words separated by either space or commoa
     * @return string of sql like type
     */
    public static String buildDynamicSQLJoinStringForLike(String original, String words, String joinType) {
        if (original == null || original.indexOf(REPLACEMENT_KEY) == -1 ||
            words == null || "".equals(words)) return "";
        
        List list = new ArrayList();
        StringTokenizer st = new StringTokenizer(words, ", ");
        while (st.hasMoreTokens()) {
            list.add(st.nextToken());
        }
        return buildDynamicSQLJoinStringForLike(original, list, joinType);
    }
    
    /**
     * Builds a string of sql like string.
     * 
     * Example:
     *      original string: "lower(category) like $keyword$"
     *           words list: "{dog, cat, fish}"
     *            joinType: OR
     *     converted string: "lower(category) like '%dog%' OR lower(category) like '%cat%' OR lower(category) like '%fish%'"
     * 
     * @param words a string of words separated by either space or commoa
     * @return string of sql like type
     */
    public static String buildDynamicSQLJoinStringForLike(String original, List words, String joinType) {
        if (original == null || original.indexOf(REPLACEMENT_KEY) == -1 ||
            words == null || words.size() == 0) return "";
        
        //find the keyword
        String keyword = "";
        StringTokenizer st = new StringTokenizer(original, ", ");
        while(st.hasMoreTokens()) {
            String token = st.nextToken();
            if (token.startsWith(REPLACEMENT_KEY) && 
                token.endsWith(REPLACEMENT_KEY)) keyword = token;
        }
        
        String converted = "";
        StringBuffer sb = new StringBuffer();
        int size = words.size();
        for (int i=0; i<size; i++) {
            String sqlLikeWord = "'%" + words.get(i) + "%'";
            converted = StringUtil.replace(original, keyword, sqlLikeWord);
            sb.append(converted).append(joinType);
        }
        
        converted = sb.toString();
        
        if (converted.endsWith(joinType)) 
            converted = converted.substring(0, converted.lastIndexOf(joinType));
        
        return converted;
    }
    
    public static java.sql.Date convertStringToSQLDate(String dateStr) {
        java.util.Date date = DateUtil.parseDate(dateStr);
        return (date == null)?null:(new java.sql.Date(date.getTime()));
    }
    
    public static java.sql.Time convertStringToSQLTime(String dateStr) {
        java.util.Date date = DateUtil.parseDate(dateStr);
        return (date == null)?null:(new java.sql.Time(date.getTime()));
    }
    
    public static java.sql.Timestamp convertStringToSQLTimestamp(String dateStr) {
        java.util.Date date = DateUtil.parseDate(dateStr);
        return (date == null)?null:(new java.sql.Timestamp(date.getTime()));
    }
    
    /**
     * Converts to upper case except for characters inside single quote.
     * 
     * @param sql original sql query string
     * @return String in uppercase
     */
    public static String convertToUpperCase(String sql) {
        int length = sql.length();
        int countQuote = 0;
        StringBuffer sb = new StringBuffer(length);
        for (int i=0; i<length; i++) {
            char c = sql.charAt(i);
            if (c == '\'') {
                if (countQuote == 0) {
                    countQuote = 1;
                }
                else {
                    countQuote = 0;
                }
                sb.append(c);
                continue;
            }
            
            if (countQuote == 1) {
                sb.append(c);
            }
            else {
                sb.append(Character.toUpperCase(c));
            }
        }
        return sb.toString();
    }
    
    /**
     * copies data from one row to another
     */
    public static void copyData(RowData fromRowData, RowData toRowData) {
        if ( fromRowData != null ) {
            if (toRowData == null) 
                toRowData = new RowData(fromRowData.getRowInfo(), null);
            
            Object[] currentData = fromRowData.getFields();
            if ( currentData != null ) {
                int dataLength = currentData.length;
                Object[] newObjectAry = new Object[dataLength];
                System.arraycopy(fromRowData.getFields(),0,newObjectAry,0,dataLength);
                toRowData.setFields(newObjectAry);
            }
        }
    }
    
    public static String REPLACE_PART_START = "#";
    
    public static String REPLACE_PART_END = "#";
    
    public static String JOIN_RELATION_OR = " OR ";
    
    public static String JOIN_RELATION_AND = " AND ";
    
    public static String REPLACEMENT_KEY = "$";
}
