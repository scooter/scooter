/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.sqldataexpress.object;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * StoredProcedure class.
 * 
 * @author (Fei) John Chen
 */
public class StoredProcedure {
    public StoredProcedure(String name) {
        if (name != null) name = name.toUpperCase(); // This is fix for Oracle.
        
        this.name = name;
        schema = getSchemaName(name);
        catalog = getCatalogName(name);
        api = getAPIName(name);
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
    public void setCataloge(String catalog) {
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
     * returns api
     */
    public String getApi() {
        return api;
    }
    
    /**
     * sets api
     */
    public void setApi(String api) {
        this.api = api;
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
     * returns javaAPIString
     */
    public String getJavaAPIString() {
        if (javaAPIString == null) javaAPIString = formatJavaAPIString();
        return javaAPIString;
    }
    
    /**
     * sets javaAPIString
     */
    public void setJavaAPIString(String javaAPIString) {
        this.javaAPIString = javaAPIString;
    }
    
    /**
     * returns count of input parameters
     */
    public int getInputParameterCount() {
        return inputParamCount;
    }
    
    /**
     * returns parameters
     */
    public Collection getParameters() {
        return parameters;
    }
    
    /**
     * adds a parameter
     */
    public void addParameter(Parameter param) {
        parameters.add(param);
        if (Parameter.MODE_IN.equals(param.getMode())) inputParamCount = inputParamCount + 1;
    }
    
    /**
     * returns cursor
     */
    public Cursor getCursor(String cursorName, ResultSet rs) {
        Cursor cursor = (Cursor)coursors.get(cursorName);
        
        if (cursor == null && rs != null) {
            cursor = new Cursor(cursorName, rs);
            addCursor(cursorName, cursor);
        }
        
        return cursor;
    }
    
    
    /**
     * adds a cursor
     */
    protected void addCursor(String cursorName, Cursor cursor) {
        coursors.put(cursorName, cursor);
    }
    
    /*
     * Display something like the following: 
     * {call PA_pkg_CaseMgmt.p_UPD_OtherInformation(?,?,?,?,?,?,?,?,?,?,?,?,?,?)}
     */
    protected String formatJavaAPIString() {
        StringBuffer buf = new StringBuffer();
        
        String questionMarkString = "";
        int colCount = parameters.size();
        for (int i = 0; i < colCount; i++) {
            questionMarkString = questionMarkString + "?,";
        }
        if (questionMarkString.endsWith(",")) questionMarkString = questionMarkString.substring(0, questionMarkString.length()-1);
        
        buf.append("{call ");
        if (schema != null) buf.append(schema).append(".");
        if (catalog != null) buf.append(catalog).append(".");
        buf.append(api);
        buf.append("(").append(questionMarkString).append(")}");
        
        return buf.toString();
    }
    
    //parse schema name
    private String getSchemaName(String name) {
        if (name == null) return null;
        
        //Parse schema name only when the input name is of format: XXX.YYY.ZZZ
        //where XXX is the schema name. Otherwise return null;
        String schema = null;
        String catalog = null;
        int lastIndexOfDot = name.lastIndexOf('.');
        if (lastIndexOfDot != -1) {
            String tmp = name.substring(0, lastIndexOfDot);
            int indexOfDot = tmp.lastIndexOf('.');
            if (indexOfDot != -1) {
                schema = tmp.substring(0, indexOfDot);
            }
        }
        
        return schema;
    }
    
    //parse catalog name
    private String getCatalogName(String name) {
        if (name == null) return null;
        
        //If the input name is of format: XXX.YYY.ZZZ, then YYY is the catalog name;
        //If the input name is of format:     YYY.ZZZ, then YYY is the catalog name;
        String catalog = null;
        int lastIndexOfDot = name.lastIndexOf('.');
        if (lastIndexOfDot != -1) {
            String tmp = name.substring(0, lastIndexOfDot);
            int indexOfDot = tmp.lastIndexOf('.');
            if (indexOfDot == -1) {
                catalog = tmp;
            }
            else {
                catalog = tmp.substring(indexOfDot+1, tmp.length());
            }
        }
        
        return catalog;
    }
    
    //parse api name for either spoc or function
    private String getAPIName(String name) {
        if (name == null) return null;
        
        //If the input name is of format: XXX.YYY.ZZZ, then ZZZ is the catalog name;
        //If the input name is of format:     YYY.ZZZ, then ZZZ is the catalog name;
        //If the input name is of format:         ZZZ, then ZZZ is the catalog name;
        String apiName = name;
        int lastIndexOfDot = name.lastIndexOf('.');
        if (lastIndexOfDot != -1) {
            apiName = name.substring(lastIndexOfDot+1, name.length());
        }
        
        return apiName;
    }


    protected String name = null;
    protected String catalog = null;
    protected String schema = null;
    protected String api = null;
    protected String javaAPIString = null;
    protected int inputParamCount = 0;
    protected Collection parameters = new ArrayList();
    protected Map coursors = new HashMap();
}
