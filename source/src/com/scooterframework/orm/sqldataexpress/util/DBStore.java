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
import com.scooterframework.orm.sqldataexpress.vendor.DBAdapter;

/**
 * DBStore class.
 * 
 * @author (Fei) John Chen
 */
public class DBStore {

    private static DBStore me;
    
    static {
        me = new DBStore();
    }

    private DBStore() {}
    
    public static synchronized DBStore getInstance() {
        return me;
    }
    
    public Map getStoredProcedures() {
        return storedProcedures;
    }
    
    public StoredProcedure getStoredProcedure(String name) {
        if (name == null) return null;
        
        StoredProcedure spoc = null;
        String spocKey = getSpocKeyForCurrentThreadCache(name);
        
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

        String spocKey = getSpocKeyForCurrentThreadCache(name);
        if (DatabaseConfig.getInstance().isInDevelopmentEnvironment()) {
            CurrentThreadCache.set(spocKey, spoc);
            return;
        }
        
        storedProcedures.put(spocKey, spoc);
    }
    
    public Map getFunctions() {
        return functions;
    }
    
    public Function getFunction(String name) {
        if (name == null) return null;

        String functionKey = getFunctionKeyForCurrentThreadCache(name);
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

        String functionKey = getFunctionKeyForCurrentThreadCache(name);
        if (DatabaseConfig.getInstance().isInDevelopmentEnvironment()) {
            CurrentThreadCache.set(functionKey, function);
            return;
        }
        
        functions.put(functionKey, function);
    }
    
    public Map getJdbcStatements() {
        return jdbcStatements;
    }
    
    public JdbcStatement getJdbcStatement(String name) {
        if (name == null) return null;

        String jdbcKey = getJdbcKeyForCurrentThreadCache(name);
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

        String jdbcKey = getJdbcKeyForCurrentThreadCache(name);
        if (DatabaseConfig.getInstance().isInDevelopmentEnvironment()) {
            CurrentThreadCache.set(jdbcKey, jdbcStmt);
            return;
        }
        
        jdbcStatements.put(jdbcKey, jdbcStmt);
    }
    
    public Map getTableInfos() {
        return tables;
    }
    
    public TableInfo getTableInfo(String tableName) {
        if (tableName == null) return null;

        String tableKey = getTableKeyForCurrentThreadCache(tableName);
        TableInfo ti = null;
        
        if (DatabaseConfig.getInstance().isInDevelopmentEnvironment()) {
            ti = (TableInfo)CurrentThreadCache.get(tableKey);
            return ti;
        }
        
        ti = (TableInfo) tables.get(tableKey);
        return ti;
    }
    
    public void addTableInfo(String tableName, TableInfo ti) {
        if (tableName == null || ti == null) {
            throw new IllegalArgumentException("addTableInfo: Neither TableName nor TableInfo can be null: " + 
                                               "tableName is " + tableName + "; " + 
                                               "tableInfo is " + ti + ".");
        }

        String tableKey = getTableKeyForCurrentThreadCache(tableName);
        if (DatabaseConfig.getInstance().isInDevelopmentEnvironment()) {
            CurrentThreadCache.set(tableKey, ti);
            return;
        }
        
        tables.put(tableKey, ti);
    }
    
    public String getTableName(String className) {
        if (className == null) return null;
        
        String tableName = null;

        String classKey = getClassKeyForCurrentThreadCache(className);
        if (DatabaseConfig.getInstance().isInDevelopmentEnvironment()) {
            tableName = (String)CurrentThreadCache.get(classKey);
            return tableName;
        }
        
        tableName = (String)classTableMap.get(classKey);
        return tableName;
    }
    
    public void addClassTableMapping(String className, String tableName) {
        if (className == null || tableName == null) 
            throw new IllegalArgumentException("addClassTableMapping: Neither className nor tableName can be null: " + 
                                               "className is " + className + "; " + 
                                               "tableName is " + tableName + ".");

        String classKey = getClassKeyForCurrentThreadCache(className);
        if (DatabaseConfig.getInstance().isInDevelopmentEnvironment()) {
            CurrentThreadCache.set(classKey, tableName.toUpperCase());
            return;
        }
        
        classTableMap.put(classKey, tableName.toUpperCase());
    }
    
    public PrimaryKey getPrimaryKey(String catalog, String schema, String table) {
        if (table == null) return null;
        
        PrimaryKey pk = null;
        
        String pkKey = getPKKey(catalog, schema, table);
        if (DatabaseConfig.getInstance().isInDevelopmentEnvironment()) {
            pk = (PrimaryKey)CurrentThreadCache.get(pkKey);
            return pk;
        }
        
        pk = (PrimaryKey)pkMap.get(pkKey);
        return pk;
    }
    
    public void addPrimaryKey(String catalog, String schema, String table, PrimaryKey pk) {
        if (table == null || pk == null) 
            throw new IllegalArgumentException("addPrimaryKey: Neither tableName nor pk can be null: " + 
                                               "table is " + table + "; " + 
                                               "pk is " + pk + ".");
        
        String pkKey = getPKKey(catalog, schema, table);
        if (DatabaseConfig.getInstance().isInDevelopmentEnvironment()) {
            CurrentThreadCache.set(pkKey, pk);
            return;
        }
        
        pkMap.put(pkKey, pk);
    }
    
    public Map getAdapters() {
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
        
        dba = (DBAdapter)adapterMap.get(dbaKey);
        return dba;
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
        
        storedProcedures.put(dbaKey, dba);
    }

    private String getSpocKeyForCurrentThreadCache(String name) {
        return "spoc." + name;
    }

    private String getFunctionKeyForCurrentThreadCache(String name) {
        return "function." + name;
    }

    private String getJdbcKeyForCurrentThreadCache(String name) {
        return "jdbc." + name;
    }

    private String getTableKeyForCurrentThreadCache(String tableName) {
        return "table." + tableName.toUpperCase();
    }

    private String getClassKeyForCurrentThreadCache(String className) {
        return "class." + className.toUpperCase();
    }

    private String getPKKey(String catalog, String schema, String table) {
        String key = "pk.";
        if (catalog != null && !"".equals(catalog)) key += catalog.toUpperCase() + ".";
        if (schema != null && !"".equals(schema)) key += schema.toUpperCase() + ".";
        if (table != null && !"".equals(table)) key += table.toUpperCase();
        return key;
    }

    private String getDbaKeyForCurrentThreadCache(String connName) {
        return "dba." + connName;
    }

    private Map storedProcedures = new HashMap();
    private Map functions = new HashMap();
    private Map jdbcStatements = new HashMap();
    private Map tables = new HashMap();
    private Map classTableMap = new HashMap();
    private Map pkMap = new HashMap();
    private Map adapterMap = new HashMap();
}
