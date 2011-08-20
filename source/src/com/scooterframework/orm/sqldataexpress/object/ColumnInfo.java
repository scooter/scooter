/*
 *   This software is distributed under the terms of the FSF
 *   Gnu Lesser General Public License (see lgpl.txt).
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.sqldataexpress.object;

import java.io.Serializable;
import java.sql.ResultSetMetaData;
import java.sql.Types;

/**
 * ColumnInfo class.
 * This class is a mirror of JDK's ResultSetMetaData class.
 *
 * @author (Fei) John Chen
 */
public class ColumnInfo implements Serializable {
    /**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = 8726915213111824504L;

	public ColumnInfo() {}

    public String getSchemaName() {
        return m_sSchemaName;
    }

    void setSchemaName(String schemaName) {
        if ( schemaName != null ) schemaName = schemaName.toUpperCase();
        m_sSchemaName = schemaName;
    }

    public String getCatalogName() {
        return m_sCatalogName;
    }

    void setCatalogName(String catalogName) {
        if ( catalogName != null ) catalogName = catalogName.toUpperCase();
        m_sCatalogName = catalogName;
    }

    public String getTableName() {
        return m_sTableName;
    }

    void setTableName(String tableName) {
        if ( tableName != null ) tableName = tableName.toUpperCase();
        m_sTableName = tableName;
    }

    public boolean isPrimaryKey() {
        return m_bPrimaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        m_bPrimaryKey = primaryKey;
    }

    public String getColumnClassName() {
        return m_sColumnClassName;
    }

    void setColumnClassName(String columnClassName) {
        m_sColumnClassName = columnClassName;
    }

    public String getColumnName() {
        return m_sColumnName;
    }

    void setColumnName(String columnName) {
        if ( columnName != null ) columnName = columnName.toUpperCase();
        m_sColumnName = columnName;
    }

    public String getColumnDefault() {
        return m_ColumnDefault;
    }

    void setColumnDefault(String columnDefault) {
        m_ColumnDefault = columnDefault;
    }
    
    public String getColumnDefaultForEntryScreen() {
    	if (m_ColumnDefault != null) {
    		if (m_ColumnDefault.equalsIgnoreCase("CURRENT_TIMESTAMP") || 
    				m_ColumnDefault.toUpperCase().startsWith("SYS")) {
    			m_ColumnDefault = "";
    		}
    	}
        return m_ColumnDefault;
    }

    public String getColumnTypeName() {
        return m_sColumnTypeName;
    }

    void setColumnTypeName(String columnTypeName) {
        if ( columnTypeName != null ) columnTypeName = columnTypeName.toUpperCase();
        m_sColumnTypeName = columnTypeName;
    }

    public int getColumnDisplaySize() {
        return m_iColumnDisplaySize;
    }

    void setColumnDisplaySize(int displaySize) {
        m_iColumnDisplaySize = displaySize;
    }

    public int getSQLDataType() {
        return m_iDataType;
    }

    void setSQLDataType(int sqlDataType) {
        m_iDataType = sqlDataType;
    }

    public int getPrecision() {
        return m_iPrecision;
    }

    void setPrecision(int precision) {m_iPrecision = precision;}

    public int getScale() { return m_iScale; }

    void setScale(int scale) {m_iScale = scale;}

    public boolean isNull() { return m_bNullable; }
    public boolean isNotNull() { return m_bNotNullable; }
    public boolean isNullUnknown() { return m_bNullableUnknown; }

    void setNull(int nullable) {
        if ( ResultSetMetaData.columnNullable == nullable ) {
            m_bNullable = true;
        }
        else if ( ResultSetMetaData.columnNoNulls == nullable ) {
            m_bNotNullable = true;
        }
        else if ( ResultSetMetaData.columnNullableUnknown == nullable ) {
            m_bNullableUnknown = true;
        }
    }

    public boolean isAutoIncrement() { return m_bAutoIncrement; }

    void setAutoIncrement(boolean autoIncrement) {
        m_bAutoIncrement = autoIncrement;
        if (autoIncrement) m_bPrimaryKey = true;
    }

    public boolean isCaseSensitive() { return m_bCaseSensitive; }

    void setCaseSensitive(boolean caseSensitive) {m_bCaseSensitive = caseSensitive;}

    public boolean isCurrency() { return m_bCurrency; }

    void setCurrency(boolean currency) {m_bCurrency = currency;}

    public boolean isDefinitelyWritable() { return m_bDefinitelyWritable; }

    void setDefinitelyWritable(boolean definitelyWritable) {m_bDefinitelyWritable = definitelyWritable;}

    public boolean isReadOnly() { return m_bReadOnly; }

    void setReadOnly(boolean readOnly) {m_bReadOnly = readOnly;}

    public boolean isSearchable() { return m_bSearchable; }

    void setSearchable(boolean searchable) {m_bSearchable = searchable;}

    public boolean isSigned() { return m_bSigned; }

    void setSigned(boolean signed) {m_bSigned = signed;}

    public boolean isWritable() { return m_bWritable; }

    void setWritable(boolean writable) {m_bWritable = writable;}

    /**
     * Returns a string representation of the object.
     * @return String
     */
    public String toString() {
        StringBuilder returnString = new StringBuilder();
        String LINE_BREAK = "\r\n";

        returnString.append( "SchemaName = " + m_sSchemaName).append( LINE_BREAK );
        returnString.append( "CatalogName = " + m_sCatalogName).append( LINE_BREAK );
        returnString.append( "TableName = " + m_sTableName).append( LINE_BREAK );
        returnString.append( "isPrimaryKey = " + m_bPrimaryKey ).append( LINE_BREAK );
        returnString.append( "ColumnClassName = " + m_sColumnClassName).append( LINE_BREAK );
        returnString.append( "ColumnName = " + m_sColumnName).append( LINE_BREAK );
        returnString.append( "ColumnDefault = " + m_ColumnDefault).append( LINE_BREAK );
        returnString.append( "ColumnTypeName = " + m_sColumnTypeName).append( LINE_BREAK );
        returnString.append( "ColumnDisplaySize = " + m_iColumnDisplaySize).append( LINE_BREAK );
        returnString.append( "DataType = " + m_iDataType).append( LINE_BREAK );
        returnString.append( "Precision = " + m_iPrecision).append( LINE_BREAK );
        returnString.append( "Scale = " + m_iScale).append( LINE_BREAK );
        returnString.append( "Nullable = " + m_bNullable).append( LINE_BREAK );
        returnString.append( "NotNullable = " + m_bNotNullable).append( LINE_BREAK );
        returnString.append( "NullableUnknown = " + m_bNullableUnknown).append( LINE_BREAK );
        returnString.append( "isAutoIncrement = " + m_bAutoIncrement).append( LINE_BREAK );
        returnString.append( "isCaseSensitive = " + m_bCaseSensitive).append( LINE_BREAK );
        returnString.append( "isCurrency = " + m_bCurrency).append( LINE_BREAK );
        returnString.append( "isDefinitelyWritable = " + m_bDefinitelyWritable).append( LINE_BREAK );
        returnString.append( "isReadOnly = " + m_bReadOnly).append( LINE_BREAK );
        returnString.append( "isSearchable = " + m_bSearchable).append( LINE_BREAK );
        returnString.append( "isSigned = " + m_bSigned).append( LINE_BREAK );
        returnString.append( "isWritable = " + m_bWritable).append( LINE_BREAK );

        return returnString.toString();
    }

    /**
     * Checks if this column is of date type.
     *
     * <p>
     * The following types from java.sql.Types are treated as date type:
     * <ul>
     *   <li>java.sql.Types.DATE       91 </li>
     *   <li>java.sql.Types.TIMESTAMP  93 </li>
     * </ul>
     * </p>
     *
     * @return true if it is of date type
     */
    public boolean isDate() {
        return m_iDataType == Types.DATE;
    }

    /**
     * Checks if this column is of timestamp type.
     *
     * <p>
     * The following types from java.sql.Types are treated as timestamp type:
     * <ul>
     *   <li>java.sql.Types.TIMESTAMP  93 </li>
     * </ul>
     * </p>
     *
     * @return true if it is of timestamp type
     */
    public boolean isTimestamp() {
        return m_iDataType == Types.TIMESTAMP;
    }

    /**
     * Checks if a column is of numeric type.
     *
     * <p>
     * The following types from java.sql.Types are treated as numeric type:
     * <ul>
     *   <li>java.sql.Types.BIGINT  -5 </li>
     *   <li>java.sql.Types.BIT     -7 </li>
     *   <li>java.sql.Types.DECIMAL  3 </li>
     *   <li>java.sql.Types.DOUBLE   8 </li>
     *   <li>java.sql.Types.FLOAT    6 </li>
     *   <li>java.sql.Types.INTEGER  4 </li>
     *   <li>java.sql.Types.NUMERIC  2 </li>
     *   <li>java.sql.Types.REAL     7 </li>
     *   <li>java.sql.Types.SMALLINT 5 </li>
     *   <li>java.sql.Types.TINYINT -6 </li>
     * </ul>
     * </p>
     *
     * @param dataType
     * @return true if it is of numeric type
     */
    public static boolean isNumeric(int dataType) {
        boolean result = false;
        switch(dataType) {
            case Types.BIGINT:// -5
                result = true; break;
            case Types.BIT:// -7
                result = true; break;
            case Types.DECIMAL:// 3
                result = true; break;
            case Types.DOUBLE:// 8
                result = true; break;
            case Types.FLOAT:// 6
                result = true; break;
            case Types.INTEGER:// 4
                result = true; break;
            case Types.NUMERIC:// 2
                result = true; break;
            case Types.REAL:// 7
                result = true; break;
            case Types.SMALLINT:// 5
                result = true; break;
            case Types.TINYINT:// -6
                result = true; break;
        }
        return result;
    }

    /**
     * Checks if this column is of numeric type.
     *
     * <p>
     * The following types from java.sql.Types are treated as numeric type:
     * <ul>
     *   <li>java.sql.Types.BIGINT  -5 </li>
     *   <li>java.sql.Types.BIT     -7 </li>
     *   <li>java.sql.Types.DECIMAL  3 </li>
     *   <li>java.sql.Types.DOUBLE   8 </li>
     *   <li>java.sql.Types.FLOAT    6 </li>
     *   <li>java.sql.Types.INTEGER  4 </li>
     *   <li>java.sql.Types.NUMERIC  2 </li>
     *   <li>java.sql.Types.REAL     7 </li>
     *   <li>java.sql.Types.SMALLINT 5 </li>
     *   <li>java.sql.Types.TINYINT -6 </li>
     * </ul>
     * </p>
     *
     * @return true if it is of numeric type
     */
    public boolean isNumeric() {
        return isNumeric(m_iDataType);
    }

    private String m_sSchemaName = "";
    private String m_sCatalogName = "";
    private String m_sTableName = "";
    private boolean m_bPrimaryKey = false;
    private String m_sColumnClassName = "";
    private String m_sColumnName = "";
    private String m_ColumnDefault;
    private String m_sColumnTypeName = "";
    private int m_iColumnDisplaySize;
    private int m_iDataType;
    private int m_iPrecision;
    private int m_iScale;
    private boolean m_bNullable = false;
    private boolean m_bNotNullable = false;
    private boolean m_bNullableUnknown = false;
    private boolean m_bAutoIncrement = false;
    private boolean m_bCaseSensitive = false;
    private boolean m_bCurrency = false;
    private boolean m_bDefinitelyWritable = false;
    private boolean m_bReadOnly = false;
    private boolean m_bSearchable = true;
    private boolean m_bSigned = true;
    private boolean m_bWritable = true;
}
