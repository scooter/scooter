/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.sqldataexpress.object;

import java.io.Serializable;
import java.util.List;

import com.scooterframework.common.util.Converters;

/**
 * PrimaryKey class holds config information about a specific table.
 * 
 * @author (Fei) John Chen
 */
public class PrimaryKey implements Serializable{
    /**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = -1687389705762293877L;
	
	/**
     * Constructor.
     * 
     * @param catalog
     * @param schema
     * @param table
     * @param columnNames
     */
    public PrimaryKey(String catalog, String schema, String table, List columnNames) {
        this.catalog = catalog;
        this.schema = schema;
        this.table = Converters.toUpperCase(table);
        this.columns = (List)Converters.toUpperCase(columnNames);
    }

    /**
     * Returns primary key columns
     */
    public List getColumns() {
        return columns;
    }
    
    /**
     * Sets primary key columns
     */
    public void setColumns(List columns) {
        this.columns = (List)Converters.toUpperCase(columns);
    }
    
    /**
     * Returns catalog
     */
    public String getCatalog() {
        return catalog;
    }
    
    /**
     * Sets catalog
     */
    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }
    
    /**
     * Returns schema
     */
    public String getSchema() {
        return schema;
    }
    
    /**
     * Sets schema
     */
    public void setSchema(String schema) {
        this.schema = schema;
    }
    
    /**
     * Returns table
     */
    public String getTable() {
        return table;
    }
    
    /**
     * Sets table
     */
    public void setTable(String table) {
        this.table = Converters.toUpperCase(table);
    }
    
    public boolean hasColumn(String column) {
        if (columns.contains(Converters.toUpperCase(column))) return true;
        return false;
    }
    
    /**
     * Returns a string representation of the object.
     * 
     * @return String
     */
    public String toString() {
        StringBuffer returnString = new StringBuffer();
        String LINE_BREAK = ", ";
        
        returnString.append("catalog = " + catalog);
        returnString.append(LINE_BREAK);
        returnString.append("schema = " + schema);
        returnString.append(LINE_BREAK);
        returnString.append("table = " + table);
        returnString.append(LINE_BREAK);
        returnString.append("columns = " + columns);
        
        return returnString.toString();
    }

    protected String catalog;
    protected String schema;
    protected String table;
    protected List columns;
}
