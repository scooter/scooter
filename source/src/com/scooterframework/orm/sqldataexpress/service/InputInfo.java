/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.sqldataexpress.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.scooterframework.orm.sqldataexpress.connection.DatabaseConnectionContext;

/**
 * InputInfo class contains information for all inputs.
 * 
 * @author (Fei) John Chen
 */
public class InputInfo implements Serializable {
    /**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = 4802250049164084163L;

	public InputInfo(String name) {
        this.name = name;
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
     * returns processorType
     */
    public String getProcessorType() {
        return processorType;
    }
    
    /**
     * sets processorType
     */
    public void setProcessorType(String processorType) {
        this.processorType = processorType;
    }
    
    /**
     * returns processorName
     */
    public String getProcessorName() {
        return processorName;
    }
    
    /**
     * sets processorName
     */
    public void setProcessorName(String processorName) {
        this.processorName = processorName;
    }
    
    /**
     * returns inputs as Map
     */
    public Map getInputs() {
        return inputs;
    }
    
    /**
     * sets the inputs Map
     */
    public void setInputs(Map inputs) {
        this.inputs = inputs;
    }
    
    /**
     * returns the input object for a key
     */
    public Object getInput(String key) {
        return inputs.get(key);
    }
    
    /**
     * sets the input object for a key
     */
    public void setInput(String key, Object value) {
        inputs.put(key, value);
    }
    
    /**
     * returns outputFilters as Map
     */
    public Map getOutputFilters()
    {
        return outputFilters;
    }
    
    /**
     * sets the outputFilters Map
     */
    public void setOutputFilters(Map outputFilters) {
        this.inputs = outputFilters;
    }
    
    /**
     * returns connectionName
     */
    public String getConnectionName() {
        return connectionName;
    }
    
    /**
     * sets connectionName
     */
    public void setConnectionName(String connectionName) {
        this.connectionName = connectionName;
    }
    
    /**
     * returns databaseConnectionContext as DatabaseConnectionContext
     */
    public DatabaseConnectionContext getDatabaseConnectionContext() {
        return dcc;
    }
    
    /**
     * sets the databaseConnectionContext
     */
    public void setDatabaseConnectionContext(DatabaseConnectionContext dcc) {
        this.dcc = dcc;
    }
    
    /**
     * returns childQueryType
     */
    public String getChildQueryType() {
        return childQueryType;
    }
    
    /**
     * sets childQueryType
     */
    public void setChildQueryType(String childQueryType) {
        this.childQueryType = childQueryType;
    }

    /**
     * returns a collection of child InputInfo objects
     */
    public Collection getChildInputInfoObjects() {
        return childInputInfoList;
    }

    /**
     * sets a collection of child InputInfo objects
     */
    public void setChildInputInfoObjects(Collection childInputInfoList) {
        this.childInputInfoList = childInputInfoList;
    }

    /**
     * adds a child InputInfo object
     */
    public void addChildInputInfoObject(InputInfo childInputInfo) {
        childInputInfoList.add(childInputInfo);
    }
    
    /**
     * returns comma delimited string of fk names.
     * 
     * The foreign key names are keys in the inputs starting with "&".
     * 
     * @return String
     */
    public String getFKString() {
        String fkNames = "";
        Iterator it = inputs.keySet().iterator();
        while (it.hasNext()) {
            String key = (String)it.next();
            if (key.startsWith("&")) {
                String fkName = key.substring(1);
                fkNames += fkName + ",";
            }
        }
        
        //remove the last comma
        if (fkNames.endsWith(",")) fkNames = fkNames.substring(0, fkNames.lastIndexOf(','));
        
        return fkNames;
    }
    
    /**
     * returns a list of fk names.
     * 
     * The foreign key names are keys in the inputs starting with "&".
     * 
     * @return List
     */
    public List getFKs() {
        List fkNames = new ArrayList();
        Iterator it = inputs.keySet().iterator();
        while (it.hasNext()) {
            String key = (String)it.next();
            if (key.startsWith("&")) {
                String fkName = key.substring(1);
                fkNames.add(fkName);
            }
        }
        
        return fkNames;
    }

    public static final String CONSTRUCT_CHILD_QUERY_THRU_UNION = "CONSTRUCT_CHILD_QUERY_THRU_UNION";

    public static final String CONSTRUCT_CHILD_QUERY_ADD_TO_WHERE_CLAUSE = "CONSTRUCT_CHILD_QUERY_ADD_TO_WHERE_CLAUSE";
    
    public static final String CONSTRUCT_CHILD_QUERY_MAKE_NEW_WHERE_CLAUSE = "CONSTRUCT_CHILD_QUERY_MAKE_NEW_WHERE_CLAUSE";

    private String name;
    
    private String processorType;
    
    private String processorName;
    
    private Map inputs = new HashMap();
    
    private Map outputFilters;
    
    private String connectionName;
    
    private DatabaseConnectionContext dcc;
    
    private String childQueryType = CONSTRUCT_CHILD_QUERY_THRU_UNION;
    
    //a collection of child InputInfo
    private Collection childInputInfoList = new ArrayList();
}
