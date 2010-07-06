/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.sqldataexpress.object;

import java.io.Serializable;

/**
 * TabelInfo class holds configure information about a specific table or view.
 * 
 * @author (Fei) John Chen
 */
public class TableInfo implements Serializable{
    /**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = 2483266866559566937L;
	
	public TableInfo() {
    }

    /**
     * Returns table name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Sets table name
     */
    public void setName(String name) {
    	if (isEmpty(name)) return;
        this.name = name;
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
        if (isEmpty(catalog)) return;
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
        if (isEmpty(schema)) return;
        this.schema = schema;
    }
    
    /**
     * Returns table type
     */
    public String getType() {
        return type;
    }
    
    /**
     * Sets table type
     */
    public void setType(String type) {
        this.type = type;
    }
    
    /**
     * Returns table remarks
     */
    public String getRemarks() {
        return remarks;
    }
    
    /**
     * Sets table remarks
     */
    public void setRemarks(String remarks) {
        if (remarks == null) return;
        this.remarks = remarks;
    }
    
    /**
     * Returns RowInfo as table header
     */
    public RowInfo getHeader() {
        return header;
    }
    
    /**
     * Sets RowInfo
     */
    public void setHeader(RowInfo header) {
        if (header != null) this.header = header;
    }
    
    /**
     * Returns table width
     */
    public int getTableWidth() {
        return header.getDimension();
    }
    
    /**
     * Returns a string representation of the object.
     * @return String
     */
    public String toString() {
        StringBuffer returnString = new StringBuffer();
        String LINE_BREAK = "\r\n";
        
        returnString.append("Table name = " + name);
        returnString.append(LINE_BREAK);
        returnString.append("catalog = " + catalog);
        returnString.append(LINE_BREAK);
        returnString.append("schema = " + schema);
        returnString.append(LINE_BREAK);
        returnString.append("type = " + type);
        returnString.append(LINE_BREAK);
        returnString.append("remarks = " + remarks);
        returnString.append(LINE_BREAK);
        
        if (header != null) {
            returnString.append(header.toString());
            returnString.append(LINE_BREAK);
        }
        
        return returnString.toString();
    }
    
    private static boolean isEmpty(String s) {
    	return (s == null || "".equals(s))?true:false;
    }

    public static final String TYPE_SYNONYM = "SYNONYM";
    public static final String TYPE_TABLE = "TABLE";
    public static final String TYPE_VIEW = "VIEW";
    public static final String[] SUPPORTED_TYPES = {TYPE_TABLE, TYPE_VIEW};

    protected String schema = "";
    protected String catalog = "";
    protected String type;
    protected String name;
    protected String remarks = "";
    protected RowInfo header = new RowInfo();
}
