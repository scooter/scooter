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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.scooterframework.common.logging.LogUtil;

/**
 * JdbcStatement class.
 * 
 * @author (Fei) John Chen
 */
public class JdbcStatement {
    public JdbcStatement() {}
    
    public JdbcStatement(String name, String jdbcStatementString) {
        this.name = name;
        this.jdbcStatementString = jdbcStatementString;
    }
    
    
    /**
     * Returns name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the original SQL statement string
     */
    public String getOriginalJdbcStatementString() {
        return jdbcStatementString;
    }

    /**
     * Returns executable SQL statement string
     */
    public String getExecutableJdbcStatementString() {
    	if (executableJdbcString == null) {
    		executableJdbcString = convertToExecutable(jdbcStatementString);
    	}
        return executableJdbcString;
    }
    
    /**
     * isInsertStatement
     */
    public boolean isInsertStatement() {
        boolean insertStatement = false;
        
        if ( jdbcStatementString != null && 
             jdbcStatementString.toUpperCase().trim().startsWith("INSERT")) {
            insertStatement = true;
        }
        
        return insertStatement;
    }
    
    /**
     * isSelectStatement
     */
    public boolean isSelectStatement() {
        boolean selectStatement = false;
        
        if ( jdbcStatementString != null && 
             jdbcStatementString.toUpperCase().trim().startsWith("SELECT")) {
            selectStatement = true;
        }
        
        return selectStatement;
    }
    
    /**
     * isUpdateStatement
     */
    public boolean isUpdateStatement() {
        boolean updateStatement = false;
        
        if ( jdbcStatementString != null && 
             jdbcStatementString.toUpperCase().trim().startsWith("UPDATE")) {
            updateStatement = true;
        }
        
        return updateStatement;
    }
    
    /**
     * Returns count of parameters
     */
    public int getParameterCount() {
        return parameters.size();
    }
    
    /**
     * Returns parameters
     */
    public List<Parameter> getParameters() {
        return parameters;
    }
    
    /**
     * Adds a parameter
     */
    public void addParameter(Parameter param) {
        parameters.add(param);
    }
    
    /**
     * Returns cursor
     */
    public Cursor getCursor(String cursorName, ResultSet rs) {
        Cursor cursor = (Cursor)cursors.get(cursorName);
        
        if ( cursor == null ) {
            cursor = new Cursor(cursorName, rs);
            addCursor(cursorName, cursor);
        }
        
        return cursor;
    }
    
    public boolean hasLoadedParameterProperties() {
        return loadedParameterProperties;
    }
    
    public void setLoadedParameterProperties(boolean loadedParameterProperties) {
        this.loadedParameterProperties = loadedParameterProperties;
    }
    
    public boolean hasLoadedParameterMetaData() {
        return loadedParameterMetaData;
    }
    
    public void setLoadedParameterMetaData(boolean loadedParameterMetaData) {
        this.loadedParameterMetaData = loadedParameterMetaData;
    }
       
    /**
     * Returns a string representation of the object.
     * 
     * @return String
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String LINE_BREAK = "\r\n";
        
        sb.append("name = " + name).append(LINE_BREAK);
        sb.append("jdbcStatementString = " + jdbcStatementString).append(LINE_BREAK);
        sb.append("executableJdbcString = " + executableJdbcString).append(LINE_BREAK);
        
        if (parameters != null) {
            int psize = parameters.size();
            sb.append("parameters size = " + psize).append(LINE_BREAK);
            Iterator<Parameter> it = parameters.iterator();
            while (it.hasNext()) {
                Parameter p = it.next();
                if (p == null) continue;
                sb.append(p.toString()).append(LINE_BREAK);
            }
        }
        
        if (cursors != null) {
            int csize = cursors.size();
            sb.append("coursors size = " + csize).append(LINE_BREAK);
            for (Map.Entry<String, Cursor> entry : cursors.entrySet()) {
                sb.append(entry.getKey()).append(": ").append(entry.getValue()).append(LINE_BREAK);
            }
        }
        
        return sb.toString();
    }
    
    
    /**
     * Adds a cursor
     */
    private void addCursor(String cursorName, Cursor cursor) {
        cursors.put(cursorName, cursor);
    }
    
    /**
     * Converts the original jdbc statement string to executable.
     * 
     * <pre>
     * The original jdbc statement string may be like :
     *      <code>SELECT name FROM user WHERE id = ?ID</code>
     *      
     * The corresponding executable jdbc statement string is: 
     *      <code>SELECT name FROM user WHERE id = ?</code>
     * </ptr>
     * 
     * @param original
     * @return 
     */
    private String convertToExecutable(String original) {
        if (original.indexOf('?') == -1) return original;
        
        boolean allowedChar = true;
        int length = original.length();
        char[] newChars = new char[length];
        for (int i = 0; i < length; i++) {
            char c = original.charAt(i);
            newChars[i] = ' ';
            
            if ('?' == c) {
                allowedChar = false;
                newChars[i] = c;
            }
            else if (QuestionMarkStopper.indexOf(c) != -1) {
                allowedChar = true;
            }
            
            if (allowedChar) newChars[i] = c;
        }
        
        return new String(newChars);
    }

    public static final String QuestionMarkStopper = " ,|><=(){}+-*/";
    
    private boolean loadedParameterProperties = false;
    private String name = null;
    private String jdbcStatementString = null;
    private List<Parameter> parameters = new ArrayList<Parameter>();
    private Map<String, Cursor> cursors = new HashMap<String, Cursor>();
    private boolean loadedParameterMetaData = false;
    
    private String executableJdbcString;
    
    protected LogUtil log = LogUtil.getLogger(this.getClass().getName());
}
