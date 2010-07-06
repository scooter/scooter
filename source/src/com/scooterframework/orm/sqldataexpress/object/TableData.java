/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.sqldataexpress.object;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * TableData class contains table data which consists of both header and body.  
 * 
 * The header, represented by RowInfo, is the meta data about the columns.
 * 
 * The body is a list of RowData objects. 
 * 
 * @author (Fei) John Chen
 */
public class TableData implements Serializable {

    /**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = -4847200394601312721L;
	
	/**
     * Constructor
     */
    public TableData() {}
    
    /**
     * returns table meta data
     */
    public TableInfo getTableInfo() {
        return tableInfo;
    }
    
    /**
     * sets meta data
     */
    void setTableInfo(TableInfo tableInfo) {
        this.tableInfo = tableInfo;
    }
    
    /**
     * gets table name
     */
    public String getTableName() {
        return name;
    }
    
    /**
     * sets table name
     */
    void setTableName(String name) {
        this.name = name;
    }
    
    /**
     * gets body
     */
    public List getBody() {
        return getAllRows();
    }

    /**
     * returns all rows as a list of Map. 
     */
    public List getAllRowsAsMap() {
        int size = tableBody.size();
        List list = new ArrayList(size);
        for (int i=0; i<size; i++) {
            RowData row = (RowData)tableBody.get(i);
            list.add(row.getDataMap());
        }
        return list;
    }

    /**
     * returns all rows as a list of RowData objects. 
     */
    public List getAllRows() {
        return tableBody;
    }
    
    /**
     * returns a row as a RowData object.  
     * The first row's index is 0, the second row's is 1, ... 
     */
    public RowData getRow(int rowIndex) {
        int size = tableBody.size();
        if (size == 0 || rowIndex >= size) return null;
        
        return (RowData)tableBody.get(rowIndex);
    }
    
    /**
     * adds a row
     */
    public void addRow(RowData row) {
        tableBody.add(row);
        tableSize = tableSize + 1;
    }
    
    /**
     * removes a row
     */
    public void removeRow(int rowIndex) {
        tableBody.remove(rowIndex);
        tableSize = tableSize - 1;
    }
    
    /**
     * returns a row as a Map.
     * 
     * If the rowIndex is out of bound, returns an empty Map. 
     * 
     * rowIndex: 0, 1, 2, ...
     */
    public Map getRowAsMap(int rowIndex) {
        RowData rd = getRow(rowIndex);
        if (rd == null) return new HashMap();
        
        return rd.getDataMap();
    }
    
    /**
     * returns a row as a object.  
     * The first row's index is 0, the second row's is 1, ... 
     */
    public Object[] getData(int rowIndex) {
        RowData rd = getRow(rowIndex);
        if (rd == null) return null;
        return rd.getFields();
    }
    
    /**
     * returns first row
     */
    public RowData getFirstRow() {
        return getRow(0);
    }
    
    /**
     * returns first value of first row
     */
    public Object getFirstObject() {
        RowData rd = getRow(0);
        if (rd == null) return null;
        return rd.getField(0);
    }
    
    /**
     * gets header info
     */
    public RowInfo getHeader() {
        return header;
    }
    
    /**
     * sets header info
     */
    public void setHeader(RowInfo rowInfo) {
        if (rowInfo == null) return;
        
        columnSize = rowInfo.getDimension();
        this.header = rowInfo;
        
        if (tableInfo != null) {
            tableInfo.setHeader(header);
        }
        
        if (tableBody != null) {
            for (int i = 0; i < tableBody.size(); i++) {
                RowData rd = (RowData)tableBody.get(i);
                rd.setRowInfo(rowInfo);
            }
        }
    }
    
    /**
     * returns columnSize
     */
    public int getColumnSize() {
        if (columnSize == 0) {
            if (header != null) {
                columnSize = header.getDimension();
            }
            else if (tableBody != null && tableBody.size() >= 1) {
                columnSize = getRow(0).getSize();
            }
        }
        
        return columnSize;
    }
    
    /**
     * returns table size
     */
    public int getTableSize() {
        return tableSize;
    }
    
    /**
     * Returns a string representation of the object.
     * @return String
     */
    public String toString() {
        StringBuffer returnString = new StringBuffer();
        String LINE_BREAK = "\r\n";
        
        returnString.append("columnSize = " + columnSize).append(LINE_BREAK);
        returnString.append("tableSize = " + tableSize).append(LINE_BREAK);
        returnString.append("headers: ");
        String[] headerNames = new String[columnSize];
        if (header != null) {
            headerNames = header.getColumnNames();
            for (int i = 0; i < header.getDimension(); i++) {
                returnString.append(headerNames[i] + " ");
            }
        }
        returnString.append(LINE_BREAK);
        
        returnString.append("headerClassTypes: ");
        String[] headerClassNames = new String[columnSize];
        if (header != null) {
            headerClassNames = header.getColumnJavaClassNames();
            for (int i = 0; i < header.getDimension(); i++) {
                returnString.append(headerClassNames[i] + " ");
            }
        }
        returnString.append(LINE_BREAK);
        
        returnString.append("Row data details:").append(LINE_BREAK);
        
        Iterator it2 = tableBody.iterator();
        int index2 = 0;
        while(it2.hasNext()) {
            index2 = index2 + 1;
            RowData rd = (RowData)it2.next();
            returnString.append("row " + index2).append(" : ");
            returnString.append(rd.toString());
        }
        
        return returnString.toString();
    }

    private TableInfo tableInfo;
    private String name;
    private RowInfo header = new RowInfo();
    private List tableBody = new ArrayList();
    private int columnSize = 0;
    private int tableSize = 0;
}
