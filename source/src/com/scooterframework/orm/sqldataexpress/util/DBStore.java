/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.sqldataexpress.util;

import java.util.HashMap;
import java.util.Map;

import com.scooterframework.common.util.CurrentThreadCache;
import com.scooterframework.orm.sqldataexpress.config.DatabaseConfig;
import com.scooterframework.orm.sqldataexpress.object.Function;
import com.scooterframework.orm.sqldataexpress.object.JdbcStatement;
import com.scooterframework.orm.sqldataexpress.object.PrimaryKey;
import com.scooterframework.orm.sqldataexpress.object.StoredProcedure;
import com.scooterframework.orm.sqldataexpress.object.TableInfo;
import com.scooterframework.orm.sqldataexpress.processor.JdbcStatementProcessor;
import com.scooterframework.orm.sqldataexpress.vendor.DBAdapter;

/**
 * DBStore class serves as an in-memory cache for some frequently used objects.
 * 
 * @author (Fei) John Chen
 */
public class DBStore {

    private static final DBStore me = new DBStore();

    private DBStore() {}
    
    public static DBStore getInstance() {
        return me;
    }
    
    public Map<String, StoredProcedure> getStoredProcedures() {
        return storedProcedures;
    }
    
    public StoredProcedure getStoredProcedure(String name) {
        if (name == null) return null;
        
        StoredProcedure spoc = null;
        String spocKey = getSpocKey(name);
        
        if (DatabaseConfig.getInstance().isInDevelopmentEnvironment()) {
            spoc = (StoredProcedure)CurrentThreadCache.get(spocKey);
            return spoc;
        }
        
        spoc = (StoredProcedure)storedProcedures.get(spocKey);
        return spoc;
    }
    
    public void addStoredProcedure(String name, StoredProcedure spoc) {
        if (name == null || spoc == null) 
            throw new IllegalArgumentException("addStoredProcedure: Neither name nor spoc can be null: " + 
                                               "name is " + name + "; " + 
                                               "spoc is " + spoc + ".");

        String spocKey = getSpocKey(name);
        if (DatabaseConfig.getInstance().isInDevelopmentEnvironment()) {
            CurrentThreadCache.set(spocKey, spoc);
            return;
        }
        
        storedProcedures.put(spocKey, spoc);
    }
    
    public Map<String, Function> getFunctions() {
        return functions;
    }
    
    public Function getFunction(String name) {
        if (name == null) return null;

        String functionKey = getFunctionKey(name);
        Function function = null;
        
        if (DatabaseConfig.getInstance().isInDevelopmentEnvironment()) {
            function = (Function)CurrentThreadCache.get(functionKey);
            return function;
        }
        
        function = (Function)functions.get(functionKey);
        return function;
    }
    
    public void addFunction(String name, Function function) {
        if (name == null || function == null) 
            throw new IllegalArgumentException("addFunction: Neither name nor function can be null: " + 
                                               "name is " + name + "; " + 
                                               "function is " + function + ".");

        String functionKey = getFunctionKey(name);
        if (DatabaseConfig.getInstance().isInDevelopmentEnvironment()) {
            CurrentThreadCache.set(functionKey, function);
            return;
        }
        
        functions.put(functionKey, function);
    }
    
    public Map<String, JdbcStatement> getJdbcStatements() {
        return jdbcStatements;
    }
    
    public JdbcStatement getJdbcStatement(String name) {
        if (name == null) return null;

        String jdbcKey = getJdbcKey(name);
        JdbcStatement stmt = null;
        if (DatabaseConfig.getInstance().isInDevelopmentEnvironment()) {
            stmt = (JdbcStatement)CurrentThreadCache.get(jdbcKey);
            return stmt;
        }
        
        stmt = (JdbcStatement)jdbcStatements.get(jdbcKey);
        return stmt;
    }
    
    public void addJdbcStatement(String name, JdbcStatement jdbcStmt) {
        if (name == null || jdbcStmt == null) 
            throw new IllegalArgumentException("addJdbcStatement: Neither name nor jdbcStmt can be null: " + 
                                               "name is " + name + "; " + 
                                               "jdbcStmt is " + jdbcStmt + ".");

        String jdbcKey = getJdbcKey(name);
        if (DatabaseConfig.getInstance().isInDevelopmentEnvironment()) {
            CurrentThreadCache.set(jdbcKey, jdbcStmt);
            return;
        }
        
        jdbcStatements.put(jdbcKey, jdbcStmt);
    }
    
    public Map<String, JdbcStatementProcessor> getJdbcStatementProcessors() {
        return jdbcStatementProcessors;
    }
    
    public JdbcStatementProcessor getJdbcStatementProcessor(String name) {
        if (name == null) return null;

        String jdbcKey = getJdbcKey(name);
        JdbcStatementProcessor stmt = null;
        if (DatabaseConfig.getInstance().isInDevelopmentEnvironment()) {
            stmt = (JdbcStatementProcessor)CurrentThreadCache.get(jdbcKey);
            return stmt;
        }
        
        stmt = (JdbcStatementProcessor)jdbcStatementProcessors.get(jdbcKey);
        return stmt;
    }
    
    public void addJdbcStatementProcessor(String name, JdbcStatementProcessor jdbcProcessor) {
        if (name == null || jdbcProcessor == null) 
            throw new IllegalArgumentException("addJdbcStatementProcessor: Neither name nor jdbcProcessor can be null: " + 
                                               "name is " + name + "; " + 
                                               "jdbcProcessor is " + jdbcProcessor + ".");

        String jdbcKey = getJdbcKey(name);
        if (DatabaseConfig.getInstance().isInDevelopmentEnvironment()) {
            CurrentThreadCache.set(jdbcKey, jdbcProcessor);
            return;
        }
        
        jdbcStatementProcessors.put(jdbcKey, jdbcProcessor);
    }
    
    public Map<String, TableInfo> getTableInfos() {
        return tables;
    }
    
    /**
     * Retrieves a stored <tt>TableInfo</tt> instance.
     * 
     * @param connName   database connection name
     * @param catalog    catalog name
     * @param schema     schema name
     * @param tableName  table name
     * @return instance of <tt>TableInfo</tt>
     */
    public TableInfo getTableInfo(String connName, String catalog, String schema, String tableName) {
    	if (connName == null) {
    		throw new IllegalArgumentException("getTableInfo: connName cannot be null");
    	}
    	
        if (tableName == null) return null;
        
        String fullTableName = getFullTableName(connName, catalog, schema, tableName);
        String tableKey = getTableKey(connName, fullTableName);
        TableInfo ti = null;
        if (DatabaseConfig.getInstance().isInDevelopmentEnvironment()) {
        	ti = (TableInfo)CurrentThreadCache.get(tableKey);
            return ti;
        }
        
        ti = (TableInfo) tables.get(tableKey);
        return ti;
    }
    
    /**
     * Adds an instance of <tt>TableInfo</tt>
     * 
     * @param connName   database connection name
     * @param catalog    catalog name
     * @param schema     schema name
     * @param tableName  table name
     * @param ti         instance of <tt>TableInfo</tt>
     */
    public void addTableInfo(String connName, String catalog, String schema, String tableName, TableInfo ti) {
    	if (connName == null) {
    		throw new IllegalArgumentException("addTableInfo: connName cannot be null");
    	}
    	
        if (tableName == null || ti == null) {
            throw new IllegalArgumentException("addTableInfo: Neither TableName nor TableInfo can be null: " + 
                                               "tableName is '" + tableName + "'; " + 
                                               "tableInfo is '" + ti + "'.");
        }
        
        String fullTableName = getFullTableName(connName, catalog, schema, tableName);
        String tableKey = getTableKey(connName, fullTableName);
        if (DatabaseConfig.getInstance().isInDevelopmentEnvironment()) {
            CurrentThreadCache.set(tableKey, ti);
            return;
        }
        
        tables.put(tableKey, ti);
    }
    
    /**
     * Retrieves a stored <tt>TableInfo</tt> instance.
     * 
     * @param connName   database connection name
     * @param tableName  table name
     * @return instance of <tt>TableInfo</tt>
     */
    public TableInfo getTableInfo(String connName, String tableName) {
    	if (connName == null) {
    		throw new IllegalArgumentException("getTableInfo: connName cannot be null");
    	}
    	
        if (tableName == null) return null;
        
        String tableKey = getTableKey(connName, tableName);
        TableInfo ti = null;
        if (DatabaseConfig.getInstance().isInDevelopmentEnvironment()) {
        	ti = (TableInfo)CurrentThreadCache.get(tableKey);
            return ti;
        }
        
        ti = (TableInfo) tables.get(tableKey);
        return ti;
    }
    
    /**
     * Adds an instance of <tt>TableInfo</tt>
     * 
     * @param connName   database connection name
     * @param tableName  table name
     * @param ti         instance of <tt>TableInfo</tt>
     */
    public void addTableInfo(String connName, String tableName, TableInfo ti) {
    	if (connName == null) {
    		throw new IllegalArgumentException("addTableInfo: connName cannot be null");
    	}
    	
        if (tableName == null || ti == null) {
            throw new IllegalArgumentException("addTableInfo: Neither TableName nor TableInfo can be null: " + 
                                               "tableName is '" + tableName + "'; " + 
                                               "tableInfo is '" + ti + "'.");
        }
        
        String tableKey = getTableKey(connName, tableName);
        if (DatabaseConfig.getInstance().isInDevelopmentEnvironment()) {
            CurrentThreadCache.set(tableKey, ti);
            return;
        }
        
        tables.put(tableKey, ti);
    }
    
    /**
     * Retrieves a stored <tt>PrimaryKey</tt> instance.
     * 
     * @param connName   database connection name
     * @param catalog    catalog name
     * @param schema     schema name
     * @param tableName  table name
     * @return instance of <tt>PrimaryKey</tt>
     */
    public PrimaryKey getPrimaryKey(String connName, String catalog, String schema, String tableName) {
    	if (connName == null) {
    		throw new IllegalArgumentException("getPrimaryKey: connName cannot be null");
    	}
    	
        if (tableName == null) return null;
        
        String fullTableName = getFullTableName(connName, catalog, schema, tableName);
        String pkKey = getPKKey(connName, fullTableName);
        PrimaryKey pk = null;
        if (DatabaseConfig.getInstance().isInDevelopmentEnvironment()) {
        	pk = (PrimaryKey)CurrentThreadCache.get(pkKey);
            return pk;
        }
        
        pk = (PrimaryKey) pkMap.get(pkKey);
        return pk;
    }
    
    /**
     * Adds an instance of <tt>PrimaryKey</tt>
     * 
     * @param connName   database connection name
     * @param catalog    catalog name
     * @param schema     schema name
     * @param tableName  table name
     * @param pk         instance of <tt>PrimaryKey</tt>
     */
    public void addPrimaryKey(String connName, String catalog, String schema, String tableName, PrimaryKey pk) {
    	if (connName == null) {
    		throw new IllegalArgumentException("addPrimaryKey: connName cannot be null");
    	}
    	
        if (tableName == null || pk == null) {
            throw new IllegalArgumentException("addPrimaryKey: Neither TableName nor PrimaryKey can be null: " + 
                                               "tableName is '" + tableName + "'; " + 
                                               "pk is '" + pk + "'.");
        }
        
        String fullTableName = getFullTableName(connName, catalog, schema, tableName);
        String pkKey = getPKKey(connName, fullTableName);
        if (DatabaseConfig.getInstance().isInDevelopmentEnvironment()) {
            CurrentThreadCache.set(pkKey, pk);
            return;
        }
        
        pkMap.put(pkKey, pk);
    }
    
    /**
     * Retrieves a stored <tt>PrimaryKey</tt> instance.
     * 
     * @param connName   database connection name
     * @param tableName  table name
     * @return instance of <tt>PrimaryKey</tt>
     */
    public PrimaryKey getPrimaryKey(String connName, String tableName) {
    	if (connName == null) {
    		throw new IllegalArgumentException("getPrimaryKey: connName cannot be null");
    	}
    	
        if (tableName == null) return null;
        
        String pkKey = getPKKey(connName, tableName);
        PrimaryKey pk = null;
        if (DatabaseConfig.getInstance().isInDevelopmentEnvironment()) {
        	pk = (PrimaryKey)CurrentThreadCache.get(pkKey);
            return pk;
        }
        
        pk = (PrimaryKey) pkMap.get(pkKey);
        return pk;
    }
    
    /**
     * Adds an instance of <tt>PrimaryKey</tt>
     * 
     * @param connName   database connection name
     * @param tableName  table name
     * @param pk         instance of <tt>PrimaryKey</tt>
     */
    public void addPrimaryKey(String connName, String tableName, PrimaryKey pk) {
    	if (connName == null) {
    		throw new IllegalArgumentException("addPrimaryKey: connName cannot be null");
    	}
    	
        if (tableName == null || pk == null) {
            throw new IllegalArgumentException("addPrimaryKey: Neither TableName nor PrimaryKey can be null: " + 
                                               "tableName is '" + tableName + "'; " + 
                                               "pk is '" + pk + "'.");
        }
        
        String pkKey = getPKKey(connName, tableName);
        if (DatabaseConfig.getInstance().isInDevelopmentEnvironment()) {
            CurrentThreadCache.set(pkKey, pk);
            return;
        }
        
        pkMap.put(pkKey, pk);
    }
    
    
    
    public Map<String, DBAdapter> getAdapters() {
        return adapterMap;
    }
    
    public DBAdapter getDBAdapter(String connName) {
        if (connName == null) return null;

        String dbaKey = getDbaKeyForCurrentThreadCache(connName);
        DBAdapter dba = null;
        
        if (DatabaseConfig.getInstance().isInDevelopmentEnvironment()) {
            dba = (DBAdapter)CurrentThreadCache.get(dbaKey);
            return dba;
        }
        
        return adapterMap.get(dbaKey);
    }
    
    public void addDBAdapter(String connName, DBAdapter dba) {
        if (connName == null || dba == null) 
            throw new IllegalArgumentException("addDBAdapter: Neither connName nor dba can be null: " + 
                                               "connName is " + connName + "; " + 
                                               "dba is " + dba + ".");

        String dbaKey = getDbaKeyForCurrentThreadCache(connName);
        if (DatabaseConfig.getInstance().isInDevelopmentEnvironment()) {
            CurrentThreadCache.set(dbaKey, dba);
            return;
        }
        
        adapterMap.put(dbaKey, dba);
    }
    
    private String getFullTableName(String connName, String catalog, String schema, String tableName) {
    	StringBuilder sb = new StringBuilder();
    	if (catalog != null) sb.append(catalog).append(".");
    	if (schema != null) sb.append(schema).append(".");
    	sb.append(tableName);
    	return sb.toString();
    }

    private String getSpocKey(String name) {
        return "spoc:" + name;
    }

    private String getFunctionKey(String name) {
        return "function:" + name;
    }

    private String getJdbcKey(String name) {
        return "jdbc:" + name;
    }

    private String getTableKey(String connName, String fullTableName) {
        return "table:" + fullTableName.toUpperCase() + "@" + connName;
    }

    private String getPKKey(String connName, String fullTableName) {
        return "pk:" + fullTableName.toUpperCase() + "@" + connName;
    }

    private String getDbaKeyForCurrentThreadCache(String connName) {
        return "dba:" + connName;
    }

    private Map<String, StoredProcedure> storedProcedures = new HashMap<String, StoredProcedure>();
    private Map<String, Function> functions = new HashMap<String, Function>();
    private Map<String, JdbcStatement> jdbcStatements = new HashMap<String, JdbcStatement>();
    private Map<String, JdbcStatementProcessor> jdbcStatementProcessors = new HashMap<String, JdbcStatementProcessor>();
    private Map<String, TableInfo> tables = new HashMap<String, TableInfo>();
    private Map<String, PrimaryKey> pkMap = new HashMap<String, PrimaryKey>();
    private Map<String, DBAdapter> adapterMap = new HashMap<String, DBAdapter>();
}
