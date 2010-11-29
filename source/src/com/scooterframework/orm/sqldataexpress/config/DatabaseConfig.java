/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.sqldataexpress.config;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;
import java.util.Set;

import javax.sql.DataSource;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.DataSources;
import com.scooterframework.admin.ApplicationConfig;
import com.scooterframework.admin.Constants;
import com.scooterframework.admin.PropertyFileChangeMonitor;
import com.scooterframework.admin.PropertyReader;
import com.scooterframework.common.logging.LogUtil;
import com.scooterframework.common.util.Converters;
import com.scooterframework.common.util.NamedProperties;
import com.scooterframework.common.util.PropertyFileUtil;
import com.scooterframework.common.util.Util;
import com.scooterframework.transaction.Transaction;

/**
 * <p>DatabaseConfig class configs the data access application during startup 
 * time. After instantiated, it is a readonly object. </p>
 * 
 * <p>Mappings between sql data type name, sql data typ code and java type name 
 * are specified here. By default, all entries in java.sql.Type class are listed here.</p>
 * 
 * <p>Developers can override or add more here. See below for examples.</p>
 * 
 * <p>All items are of the following format separated by comma. </p>
 * <pre>
 *   Format: {sql data type name}:{sql data type}:{java class name}
 *   
 *   ARRAY:2003:java.sql.Array           //Types.ARRAY
 *   BIGINT:-5:java.lang.Long            //Types.BIGINT
 *   BINARY:-2:byte[]                    //Types.BINARY
 *   BIT:-7:java.lang.Object             //Types.BIT
 *   BLOB:2004:java.sql.Blob             //Types.BLOB
 *   BOOLEAN:16:java.lang.Boolean        //Types.BOOLEAN
 *   CHAR:1:java.lang.String             //Types.CHAR
 *   CLOB:2005:java.sql.Clob             //Types.CLOB
 *   DATALINK:70:java.lang.Object        //Types.DATALINK
 *   DATE:91:java.sql.TimeStamp          //Types.DATE
 *   DECIMAL:3:java.lang.BigDecimal      //Types.DECIMAL
 *   DISTINCT:2001:java.lang.Object      //Types.DISTINCT
 *   DOUBLE:8:java.lang.Double           //Types.DOUBLE
 *   FLOAT:6:java.lang.Double            //Types.FLOAT
 *   INTEGER:4:java.lang.Integer         //Types.INTEGER
 *   JAVA_OBJECT:2000:java.lang.Object   //Types.JAVA_OBJECT
 *   LONGVARBINARY:-4:byte[]             //Types.LONGVARBINARY
 *   LONGVARCHAR:-1:java.lang.String     //Types.LONGVARCHAR
 *   NULL:0:java.lang.Object             //Types.NULL
 *   NUMERIC:2:java.lang.BigDecimal      //Types.NUMERIC
 *   OTHER:1111:java.lang.Object         //Types.OTHER
 *   REAL:7:java.lang.Double             //Types.REAL
 *   REF:2006:java.sql.Ref               //Types.REF
 *   SMALLINT:5:java.lang.Integer        //Types.SMALLINT
 *   STRUCT:2002:java.sql.Struct         //Types.STRUCT
 *   TIME:92:java.sql.Time               //Types.TIME
 *   TIMESTAMP:93:java.sql.TimeStamp     //Types.TIMESTAMP
 *   TINYINT:-6:java.lang.Integer        //Types.TINYINT
 *   VARBINARY:-3:byte[]                 //Types.VARBINARY
 *   VARCHAR:12:java.lang.String         //Types.VARCHAR

 *  Special for ORACLE:
 *   NUMBER:2:java.lang.BigDecimal       //Types.NUMERIC
 *   VARCHAR2:12:java.lang.String        //Types.VARCHAR
 * </pre>
 * 
 * <p>Developers can add more types at startup time by adding those types 
 *    in database property file.</p>
 * 
 * @author (Fei) John Chen
 */
public class DatabaseConfig extends Observable implements Observer {
    private LogUtil log = LogUtil.getLogger(this.getClass().getName());
    
    /**
     * A hyphen "-" is used to link composite primary key fields.
     */
    public static final String PRIMARY_KEY_SEPARATOR = Constants.PRIMARY_KEY_SEPARATOR;
    
    /**
     * Property file for database properties.
     */
    public static final String DATA_PROPERTIES_FILE = "database.properties";
    
    /**
     * All allowed transaction types: "JDBC JTA CMT"
     */
    public static final String ALLOWED_TRANSACTION_TYPES = "JDBC JTA CMT";
    
    /**
     * Key to represent a property in database connection properties.
     */
    public static final String KEY_DB_CONNECTION_NAME_PREFIX = "database.connection";
    
    /**
     * Key to represent connection <tt>name</tt> property in database connection properties.
     */
    public static final String KEY_DB_CONNECTION_NAME = "name";
    
    /**
     * Key to represent <tt>transactionType</tt> property in database connection properties.
     */
    public static final String KEY_DB_CONNECTION_TRANSACTION_TYPE = "transaction.type";
    
    /**
     * Key to represent <tt>dataSourceName</tt> property in database connection properties.
     */
    public static final String KEY_DB_CONNECTION_DATASOURCE_NAME = "data_source_name";
    
    /**
     * Key to represent <tt>beforeConenction</tt> property in database connection properties.
     */
    public static final String KEY_DB_CONNECTION_BEFORE = "before_connection";
    
    /**
     * Key to represent <tt>afterConnection</tt> property in database connection properties.
     */
    public static final String KEY_DB_CONNECTION_AFTER = "after_connection";
    
    /**
     * Key to represent <tt>timeout</tt> property in database connection properties.
     */
    public static final String KEY_DB_CONNECTION_TIMEOUT = "timeout";
    
    /**
     * Key to represent <tt>username</tt> property in database connection properties.
     */
    public static final String KEY_DB_CONNECTION_USERNAME = "username";
    
    /**
     * Key to represent <tt>password</tt> property in database connection properties.
     */
    public static final String KEY_DB_CONNECTION_PASSWORD = "password";
    
    /**
     * Key to represent <tt>driver</tt> property in database connection properties.
     */
    public static final String KEY_DB_CONNECTION_DRIVER = "driver";
    
    /**
     * Key to represent <tt>url</tt> property in database connection properties.
     */
    public static final String KEY_DB_CONNECTION_URL = "url";
    
    /**
     * Key to represent <tt>readonly</tt> property in database connection properties.
     */
    public static final String KEY_DB_CONNECTION_READONLY = "readonly";
    
    /**
     * Key to represent <tt>transactionIsolationLevel</tt> property in database connection properties.
     */
    public static final String KEY_DB_CONNECTION_TRANSACTION_ISOLATION_LEVEL = "transaction_isolation_level";
    
    /**
     * Key to represent <tt>vendor</tt> property in database connection properties.
     */
    public static final String KEY_DB_CONNECTION_VENDOR = "vendor";
    
    /**
     * Key to represent <tt>schema</tt> property in database connection properties.
     */
    public static final String KEY_DB_CONNECTION_SCHEMA = "schema";
    
    /**
     * Key to represent <tt>use_login_as_schema</tt> property in database connection properties.
     */
    public static final String KEY_DB_CONNECTION_USE_LOGIN_AS_SCHEMA = "use_login_as_schema";
    
    /**
     * Key to represent <tt>use_login_for_connection</tt> property in database connection properties.
     */
    public static final String KEY_DB_CONNECTION_USE_LOGIN_FOR_CONNECTION = "use_login_for_connection";
    
    /**
     * Key to represent <tt>adapterClassName</tt> property in database connection properties.
     */
    public static final String KEY_DB_CONNECTION_ADAPTER_CLASS_NAME = "adapterClassName";
    
    /**
     * Key to represent <tt>max_pool_size</tt> property in database connection properties.
     */
    public static final String KEY_DB_CONNECTION_MAX_POOL_SIZE = "max_pool_size";
    
    /**
     * Key to represent <tt>min_pool_size</tt> property in database connection properties.
     */
    public static final String KEY_DB_CONNECTION_MIN_POOL_SIZE = "min_pool_size";
    
    /**
     * Key to represent <tt>acquire_increment</tt> property in database connection properties.
     */
    public static final String KEY_DB_CONNECTION_ACQUIRE_INCREMENT = "acquire_increment";
    
    /**
     * Key to represent <tt>initial_pool_size</tt> property in database connection properties.
     */
    public static final String KEY_DB_CONNECTION_INITIAL_POOL_SIZE = "initial_pool_size";
    
    /**
     * Key to represent <tt>max_idle_time</tt> property in database connection properties.
     */
    public static final String KEY_DB_CONNECTION_MAX_IDLE_TIME = "max_idle_time";
    
    public static final String BUILTIN_DATABASE_NAME_HSQLDB     = "HsqlDB";
    public static final String BUILTIN_DATABASE_NAME_H2         = "H2";
    public static final String BUILTIN_DATABASE_NAME_MYSQL      = "MySQL";
    public static final String BUILTIN_DATABASE_NAME_POSTGRESQL = "PostgreSQL";
    public static final String BUILTIN_DATABASE_NAME_ORACLE     = "Oracle";
    public static final String BUILTIN_DATABASE_NAME_SYBASE     = "Sybase";
    public static final Set<String> ALL_BUILTIN_DATABASE_VENDORS = new HashSet<String>();
    
    //****************************************************
    // Default values for keys in the properties file
    //****************************************************
    public static final String DEFAULT_VALUE_defaultTransactionType = Transaction.JDBC_TRANSACTION_TYPE;
    public static final String DEFAULT_VALUE_defaultDatabaseConnection = null;
    public static final String DEFAULT_VALUE_gloablTableNamingPrefix = "";
    public static final String DEFAULT_VALUE_gloablTableNamingSuffix = "";
    public static final String DEFAULT_VALUE_usePluralTableName = "true";
    public static final String DEFAULT_VALUE_autoAuditCreate = null;
    public static final String DEFAULT_VALUE_autoAuditUpdate = null;
    public static final String DEFAULT_VALUE_additionalSQLDataTypeMapping = null;
    public static final int DEFAULT_VALUE_max_pool_size = 5;
    public static final int DEFAULT_VALUE_min_pool_size = 3;
    public static final int DEFAULT_VALUE_acquire_increment = 3;
    public static final int DEFAULT_VALUE_initial_pool_size = 3;
    public static final int DEFAULT_VALUE_max_idle_time = 0;
    
    private static DatabaseConfig me;
    private Properties appProperties = null;
    
    private String defaultTransactionType = DEFAULT_VALUE_defaultTransactionType;
    private String defaultDatabaseConnection = DEFAULT_VALUE_defaultDatabaseConnection;
    private String gloablTableNamingPrefix = DEFAULT_VALUE_gloablTableNamingPrefix;
    private String gloablTableNamingSuffix = DEFAULT_VALUE_gloablTableNamingSuffix;
    private String usePluralTableName = DEFAULT_VALUE_usePluralTableName;
    private String autoAuditCreate = DEFAULT_VALUE_autoAuditCreate;
    private String autoAuditUpdate = DEFAULT_VALUE_autoAuditUpdate;
    private String additionalSQLDataTypeMapping = DEFAULT_VALUE_additionalSQLDataTypeMapping;
    
    private List<String> autoAuditListForCreate = null;
    private List<String> autoAuditListForUpdate = null;
    private Map<String, NamedProperties> databaseConnectionsMap = new HashMap<String, NamedProperties>();
    private List<String> referenceDataNames = new ArrayList<String>();
    private Map<String, NamedProperties> referenceDataMap = new HashMap<String, NamedProperties>();
    private Map<String, ComboPooledDataSource> connectionPoolDataSourcesMap = new HashMap<String, ComboPooledDataSource>();

    /**
     * <p>A map of SQL data type name and its corresponding type (Integer).</p>
     */
    public static Map<String, Integer> allSQLDataNameTypesMap = new HashMap<String, Integer>();
    
    /**
     * <p>A map of SQL data type code and its corresponding java class type name.</p>
     */
    public static Map<Integer, String> allSQLTypeJavaNamesMap = new HashMap<Integer, String>();
    
    static {
        me = new DatabaseConfig();
    }
    
    private DatabaseConfig() {
        init();
        PropertyFileChangeMonitor.getInstance().registerObserver(this, DATA_PROPERTIES_FILE);
    }
    
    private void clear() {
        if (autoAuditListForCreate != null) autoAuditListForCreate.clear();
        if (autoAuditListForUpdate != null) autoAuditListForUpdate.clear();
        databaseConnectionsMap.clear();
        referenceDataNames.clear();
        referenceDataMap.clear();
        allSQLDataNameTypesMap.clear();
        allSQLTypeJavaNamesMap.clear();
        connectionPoolDataSourcesMap.clear();
    }
    
    private void init() {
        clear();
        
        ALL_BUILTIN_DATABASE_VENDORS.add(BUILTIN_DATABASE_NAME_H2.toUpperCase());
        ALL_BUILTIN_DATABASE_VENDORS.add(BUILTIN_DATABASE_NAME_HSQLDB.toUpperCase());
        ALL_BUILTIN_DATABASE_VENDORS.add(BUILTIN_DATABASE_NAME_MYSQL.toUpperCase());
        ALL_BUILTIN_DATABASE_VENDORS.add(BUILTIN_DATABASE_NAME_POSTGRESQL.toUpperCase());
        ALL_BUILTIN_DATABASE_VENDORS.add(BUILTIN_DATABASE_NAME_ORACLE.toUpperCase());
        ALL_BUILTIN_DATABASE_VENDORS.add(BUILTIN_DATABASE_NAME_SYBASE.toUpperCase());
        
        List<String> allSQLTypesList = new ArrayList<String>();
        allSQLTypesList.add("ARRAY:2003:java.sql.Array");           //Types.ARRAY
        allSQLTypesList.add("BIGINT:-5:java.lang.Long");            //Types.BIGINT
        allSQLTypesList.add("BINARY:-2:byte[]");                    //Types.BINARY
        allSQLTypesList.add("BIT:-7:java.lang.Object");             //Types.BIT
        allSQLTypesList.add("BLOB:2004:java.sql.Blob");             //Types.BLOB
        allSQLTypesList.add("BOOLEAN:16:java.lang.Boolean");        //Types.BOOLEAN
        allSQLTypesList.add("CHAR:1:java.lang.String");             //Types.CHAR
        allSQLTypesList.add("CLOB:2005:java.sql.Clob");             //Types.CLOB
        allSQLTypesList.add("DATALINK:70:java.lang.Object");        //Types.DATALINK
        allSQLTypesList.add("DATE:91:java.sql.TimeStamp");          //Types.DATE
        allSQLTypesList.add("DECIMAL:3:java.lang.BigDecimal");      //Types.DECIMAL
        allSQLTypesList.add("DISTINCT:2001:java.lang.Object");      //Types.DISTINCT
        allSQLTypesList.add("DOUBLE:8:java.lang.Double");           //Types.DOUBLE
        allSQLTypesList.add("FLOAT:6:java.lang.Double");            //Types.FLOAT
        allSQLTypesList.add("INTEGER:4:java.lang.Integer");         //Types.INTEGER
        allSQLTypesList.add("JAVA_OBJECT:2000:java.lang.Object");   //Types.JAVA_OBJECT
        allSQLTypesList.add("LONGVARBINARY:-4:byte[]");             //Types.LONGVARBINARY
        allSQLTypesList.add("LONGVARCHAR:-1:java.lang.String");     //Types.LONGVARCHAR
        allSQLTypesList.add("NULL:0:java.lang.Object");             //Types.NULL
        allSQLTypesList.add("NUMERIC:2:java.lang.BigDecimal");      //Types.NUMERIC
        allSQLTypesList.add("OTHER:1111:java.lang.Object");         //Types.OTHER
        allSQLTypesList.add("REAL:7:java.lang.Double");             //Types.REAL
        allSQLTypesList.add("REF:2006:java.sql.Ref");               //Types.REF
        allSQLTypesList.add("SMALLINT:5:java.lang.Integer");        //Types.SMALLINT
        allSQLTypesList.add("STRUCT:2002:java.sql.Struct");         //Types.STRUCT
        allSQLTypesList.add("TIME:92:java.sql.Time");               //Types.TIME
        allSQLTypesList.add("TIMESTAMP:93:java.sql.TimeStamp");     //Types.TIMESTAMP
        allSQLTypesList.add("TINYINT:-6:java.lang.Integer");        //Types.TINYINT
        allSQLTypesList.add("VARBINARY:-3:byte[]");                 //Types.VARBINARY
        allSQLTypesList.add("VARCHAR:12:java.lang.String");         //Types.VARCHAR
        allSQLTypesList.add("NUMBER:2:java.lang.BigDecimal");       //Types.NUMERIC
        allSQLTypesList.add("VARCHAR2:12:java.lang.String");        //Types.VARCHAR
        
        loadProperties();
        
        defaultTransactionType = getProperty("default.transaction.type", DEFAULT_VALUE_defaultTransactionType);
        if (ALLOWED_TRANSACTION_TYPES.indexOf(defaultTransactionType) == -1) {
            throw new IllegalArgumentException("The value of default.transaction.type " + 
                    "\"" + defaultTransactionType + "\" in properties file is " + 
                    "not valid. The allowed transaction types are \"" + ALLOWED_TRANSACTION_TYPES + "\".");
        }
        
        gloablTableNamingPrefix = getProperty("global.table.naming.prefix", DEFAULT_VALUE_gloablTableNamingPrefix);
        gloablTableNamingSuffix = getProperty("global.table.naming.suffix", DEFAULT_VALUE_gloablTableNamingSuffix);
        usePluralTableName = getProperty("use.plural.table.name", DEFAULT_VALUE_usePluralTableName);
        
        String nameValueSpliter = "=";
        String propertyDelimiter = ",";
        
        Enumeration en = appProperties.keys();
        while (en.hasMoreElements()) {
            String key = (String) en.nextElement();
            if (key.startsWith("database.connection")) {
                String name = key.substring(key.lastIndexOf('.') + 1);
                Properties p = PropertyFileUtil.parseNestedPropertiesFromLine(getProperty(key), nameValueSpliter, propertyDelimiter);
                p.setProperty(KEY_DB_CONNECTION_NAME, name);
                checkConnectionPoolProperties(name, p);
                if (databaseConnectionsMap.containsKey(name)) {
                    NamedProperties np = databaseConnectionsMap.get(name);
                    np.setProperties(p);
                }
                else {
                    NamedProperties np = new NamedProperties(name, p);
                    databaseConnectionsMap.put(name, np);
                }
            }
            else 
            if (key.startsWith("reference.data")) {
                String name = key.substring(key.lastIndexOf('.') + 1);
                Properties p = PropertyFileUtil.parseNestedPropertiesFromLine(getProperty(key), nameValueSpliter, propertyDelimiter);
                if (referenceDataMap.containsKey(name)) {
                    NamedProperties np = referenceDataMap.get(name);
                    np.setProperties(p);
                }
                else {
                    NamedProperties np = new NamedProperties(name, p);
                    referenceDataNames.add(name);
                    referenceDataMap.put(name, np);
                }
            }
        }
        
        defaultDatabaseConnection = getProperty("default.database.connection.name", DEFAULT_VALUE_defaultDatabaseConnection);
        if (defaultDatabaseConnection != null && !databaseConnectionsMap.keySet().contains(defaultDatabaseConnection)) {
        	log.error("There is no definition for default database connection " + defaultDatabaseConnection);
        }
        
        autoAuditCreate = getProperty("autoaudit.create.timestamp.fields", DEFAULT_VALUE_autoAuditCreate);
        if (autoAuditCreate != null) {
            autoAuditListForCreate = Converters.convertStringToList(autoAuditCreate.toLowerCase());
        }
        
        autoAuditUpdate = getProperty("autoaudit.update.timestamp.fields", DEFAULT_VALUE_autoAuditUpdate);
        if (autoAuditUpdate != null) {
            autoAuditListForUpdate = Converters.convertStringToList(autoAuditUpdate.toLowerCase());
        }
        
        //populate sql maps
        additionalSQLDataTypeMapping = getProperty("additional_sql_data_type_mapping", DEFAULT_VALUE_additionalSQLDataTypeMapping);
        allSQLTypesList.addAll(Converters.convertStringToList(additionalSQLDataTypeMapping));
        
        allSQLDataNameTypesMap.clear();
        allSQLTypeJavaNamesMap.clear();
        
        int totalTypes = allSQLTypesList.size();
        for (int i = 0; i < totalTypes; i++) {
            String entry = allSQLTypesList.get(i);
            String[] items = entry.split(":");
            String typeName = items[0].trim();
            String typeCode = items[1].trim();
            String javaName = items[2].trim();
            Integer sqlDataTypeCode = Util.getSafeInteger(typeCode);
            allSQLDataNameTypesMap.put(typeName, sqlDataTypeCode);
            allSQLTypeJavaNamesMap.put(sqlDataTypeCode, javaName);
        }
    }
    
    private void checkConnectionPoolProperties(String connectionName, Properties p) {
        String maxPoolSize = p.getProperty(KEY_DB_CONNECTION_MAX_POOL_SIZE, "" + DEFAULT_VALUE_max_pool_size);
        if (Integer.parseInt(maxPoolSize) == 0) {
            p.setProperty(KEY_DB_CONNECTION_MAX_POOL_SIZE, maxPoolSize);
            log.debug("No connection pool for " + connectionName + ", because max_pool_size is " + maxPoolSize);
            return;
        }
        
        Properties c3p0props = new Properties();
        c3p0props.setProperty("maxPoolSize", maxPoolSize);
        
        String minPoolSize = p.getProperty(KEY_DB_CONNECTION_MIN_POOL_SIZE, "" + DEFAULT_VALUE_min_pool_size);
        p.setProperty(KEY_DB_CONNECTION_MIN_POOL_SIZE, minPoolSize);
        c3p0props.setProperty("minPoolSize", minPoolSize);
        
        String acquireIncrement = p.getProperty(KEY_DB_CONNECTION_ACQUIRE_INCREMENT, "" + DEFAULT_VALUE_acquire_increment);
        p.setProperty(KEY_DB_CONNECTION_ACQUIRE_INCREMENT, acquireIncrement);
        c3p0props.setProperty("acquireIncrement", acquireIncrement);
        
        String initialPoolSize = p.getProperty(KEY_DB_CONNECTION_INITIAL_POOL_SIZE, "" + DEFAULT_VALUE_initial_pool_size);
        p.setProperty(KEY_DB_CONNECTION_INITIAL_POOL_SIZE, initialPoolSize);
        c3p0props.setProperty("initialPoolSize", initialPoolSize);
        
        String maxIdleTime = p.getProperty(KEY_DB_CONNECTION_MAX_IDLE_TIME, "" + DEFAULT_VALUE_max_idle_time);
        p.setProperty(KEY_DB_CONNECTION_MAX_IDLE_TIME, maxIdleTime);
        c3p0props.setProperty("maxIdleTime", maxIdleTime);
        
        try {
            String driver = p.getProperty(KEY_DB_CONNECTION_DRIVER);
            if (driver == null) throw new IllegalArgumentException("Driver class is not specified for connection " + connectionName);
            
            String url = p.getProperty(KEY_DB_CONNECTION_URL);
            if (url == null) throw new IllegalArgumentException("Database url is not specified for connection " + connectionName);
            
            String username = p.getProperty(KEY_DB_CONNECTION_USERNAME);
            String password = p.getProperty(KEY_DB_CONNECTION_PASSWORD);
            
            System.setProperty("com.mchange.v2.c3p0.management.ManagementCoordinator", "com.mchange.v2.c3p0.management.NullManagementCoordinator");
            
            ComboPooledDataSource cpds = new ComboPooledDataSource(connectionName);
			cpds.setDriverClass(driver);
            cpds.setJdbcUrl(url);
			cpds.setUser(username);
			cpds.setPassword(password);
			
			cpds.setMaxPoolSize(Integer.parseInt(maxPoolSize));
			cpds.setMinPoolSize(Integer.parseInt(minPoolSize));
			cpds.setAcquireIncrement(Integer.parseInt(acquireIncrement));
			cpds.setInitialPoolSize(Integer.parseInt(initialPoolSize));
			cpds.setMaxIdleTime(Integer.parseInt(maxIdleTime));
            
            connectionPoolDataSourcesMap.put(connectionName, cpds);
            log.debug("created ds for " + connectionName + " with properties: " + c3p0props);
		} catch (Throwable e) {
			log.error("Failed to create data source for " + connectionName + " with properties: " + c3p0props + ": " + e.getMessage());
		}
    }
    
    private void loadProperties() {
        if (appProperties != null) appProperties.clear();
        
        appProperties = PropertyReader.loadPropertiesFromFile(DATA_PROPERTIES_FILE);
        
        if (appProperties == null) appProperties = new Properties();
    }


    public static synchronized DatabaseConfig getInstance() {
        return me;
    }
    
    /**
     * Returns map of sql data type name / data type code. 
     * @return map
     */
    public Map<String, Integer> getSQLDataNameTypesMap() {
        return allSQLDataNameTypesMap;
    }
    
    /**
     * Returns map of sql data type code / java type name. 
     * @return map
     */
    public Map<Integer, String> getSQLTypeJavaNamesMap() {
        return allSQLTypeJavaNamesMap;
    }
    
    /**
     * Required method by <tt>Observer</tt> interface.
     */
    public void update(Observable o, Object arg) {
        init();
        
        super.setChanged();
        super.notifyObservers();
    }
    
    /**
     * Returns all properties. 
     */
    public Properties getProperties() {
        return appProperties;
    }
    
    /**
     * Returns a String property corresponding to a key.
     */
    public String getProperty(String key) {
        return appProperties.getProperty(key);
    }
    
    /**
     * Returns a String property corresponding to a key. If the returned 
     * property is null, the default value of the property is returned.
     */
    public String getProperty(String key, String defaultValue) {
        return appProperties.getProperty(key, defaultValue);
    }
    
    /**
     * Returns default transaction type
     */
    public String getDefaultTransactionType() {
        return defaultTransactionType;
    }
    
    /**
     * Returns global table naming prefix
     */
    public String getGlobalTableNamingPrefix() {
        return gloablTableNamingPrefix;
    }
    
    /**
     * Returns global table naming suffix
     */
    public String getGlobalTableNamingSuffix() {
        return gloablTableNamingSuffix;
    }
    
    /**
     * Checks if using plural nones as table name
     */
    public boolean usePluralTableName() {
        return ("true".equalsIgnoreCase(usePluralTableName))?true:false;
    }
    
    /**
     * Returns a full table name. A full table name includes global table name 
     * prefix and suffix. 
     * 
     * <p>
     * For example, in a crm schema, all tables might have prefix "CRM_". 
     * Therefore, a <tt>USER</tt> table's full name is <tt>CRM_USER</tt>.
     * </p>
     * 
     * @param table a slim table name
     * @return a full table name
     */
    public String getFullTableName(String table) {
        String tablePrefix = getGlobalTableNamingPrefix().toUpperCase();
        String tableSuffix = getGlobalTableNamingSuffix().toUpperCase();
        
        String fullTableName = table.toUpperCase();
        if (!fullTableName.startsWith(tablePrefix)) fullTableName = tablePrefix + fullTableName;
        if (!fullTableName.endsWith(tableSuffix)) fullTableName = fullTableName + tableSuffix;
        
        return fullTableName;
    }
    
    /**
     * Returns a simple table name. A simple table name does not include global 
     * table name prefix and suffix. 
     * 
     * <p>
     * For example, if the table name is "CRM_users_US" which has a prefix 
     * "CRM_" and a suffix "_US", the returned slim table name is just 
     * "users". Both the prefix and the suffix are removed in the return value.
     * </p>
     * 
     * @param fullTableName a full table name
     * @return a simple table name
     */
    public String getSimpleTableName(String fullTableName) {
        String tablePrefix = getGlobalTableNamingPrefix().toUpperCase();
        String tableSuffix = getGlobalTableNamingSuffix().toUpperCase();
        
        String slimName = fullTableName.toUpperCase();
        if (slimName.startsWith(tablePrefix)) slimName = slimName.substring(tablePrefix.length());
        if (slimName.endsWith(tableSuffix)) slimName = slimName.substring(0, slimName.length() - tableSuffix.length());
        
        return slimName;
    }
    
    /**
     * Checks if the current running environment is development environment. 
     * 
     * @return true if the current running environment is development.
     */
    public boolean isInDevelopmentEnvironment() {
        return ApplicationConfig.getInstance().isInDevelopmentEnvironment();
    }
    
    /**
     * Returns default database connection name
     */
    public String getDefaultDatabaseConnectionName() {
        return defaultDatabaseConnection;
    }
    
    public String tryToUseTestDatabaseConnection() {
    	String testDatabaseConnectionName = null;
    	if (defaultDatabaseConnection.endsWith("_development")) {
    		int lastUnderscore = defaultDatabaseConnection.indexOf('_');
    		testDatabaseConnectionName = defaultDatabaseConnection.substring(0, lastUnderscore);
    		testDatabaseConnectionName = testDatabaseConnectionName + "_test";
    		if (getPredefinedDatabaseConnectionProperties(testDatabaseConnectionName).size() > 0) {
    			defaultDatabaseConnection = testDatabaseConnectionName;
    		}
    	}
    	return testDatabaseConnectionName;
    }
    
    public void restoreDefaultDatabaseConnectionName() {
    	defaultDatabaseConnection = getProperty("default.database.connection.name", DEFAULT_VALUE_defaultDatabaseConnection);
    }
    
    /**
     * Returns default database connection properties
     */
    public Properties getDefaultDatabaseConnectionProperties() {
        return getPredefinedDatabaseConnectionProperties(getDefaultDatabaseConnectionName());
    }
    
    /**
     * Returns database connection properties
     */
    public Properties getPredefinedDatabaseConnectionProperties(String connectionName) {
        NamedProperties np = databaseConnectionsMap.get(connectionName);
        return (np != null)?np.getProperties():(new Properties());
    }
    
    /**
     * Returns database connection names
     */
    public Iterator<String> getPredefinedDatabaseConnectionNames() {
        return databaseConnectionsMap.keySet().iterator();
    }
    
    /**
     * Returns properties of reference data specified by the name
     */
    public Properties getReferenceDataProperties(String referenceDataName) {
        NamedProperties np = referenceDataMap.get(referenceDataName);
        return (np != null)?np.getProperties():(new Properties());
    }
    
    /**
     * Returns a list of all reference data names
     * 
     * @return list of reference data names
     */
    public List<String> getReferenceDataNames() {
        return referenceDataNames;
    }
    
    /**
     * Returns a map of all reference data
     * 
     * @return map of reference data
     */
    public Map<String, NamedProperties> getReferenceDataMap() {
        return referenceDataMap;
    }
    
    /**
     * Checks if auto audit is allowed for create operation.
     */
    public boolean allowAutoAuditCreate() {
        return (autoAuditCreate != null)?true:false;
    }
    
    /**
     * Checks if auto audit is allowed for update operation.
     */
    public boolean allowAutoAuditUpdate() {
        return (autoAuditUpdate != null)?true:false;
    }
    
    /**
     * Returns list of potential column names that are automatically audited
     * for create operation.
     */
    public List<String> getAutoAuditListForCreate() {
        return autoAuditListForCreate;
    }
    
    /**
     * Returns list of potential column names that are automatically audited 
     * for update operation.
     */
    public List<String> getAutoAuditListForUpdate() {
        return autoAuditListForUpdate;
    }
    
    /**
     * Checks if a column is auto audited for create operation.
     */
    public boolean isAutoAuditCreate(String column) {
        if (autoAuditListForCreate != null && column != null) {
            return autoAuditListForCreate.contains(column.toLowerCase());
        }
        return false;
    }
    
    /**
     * Checks if a column is auto audited for update operation.
     */
    public boolean isAutoAuditUpdate(String column) {
        if (autoAuditListForUpdate != null && column != null) {
            return autoAuditListForUpdate.contains(column.toLowerCase());
        }
        return false;
    }
    
    /**
     * Checks if a vendor name is allowed.
     */
    public static boolean isBuiltInVendorName(String vendor) {
    	if (vendor == null) 
    		throw new IllegalArgumentException("Vendor name is empty.");
        return ALL_BUILTIN_DATABASE_VENDORS.contains(vendor.toUpperCase());
    }
    
    /**
     * Returns pooled data source for a connection name
     */
    public DataSource getPooledDataSource(String connectionName) {
    	return connectionPoolDataSourcesMap.get(connectionName);
    }
    
    /**
     * Returns pooled data source for default connection name
     */
    public DataSource getPooledDataSource() {
        return getPooledDataSource(getDefaultDatabaseConnectionName());
    }
	
    /**
     * Destroies all connection pools if there is any.
     */
	public void destroy() {
		try {
            Iterator<String> it = connectionPoolDataSourcesMap.keySet().iterator();
            while(it.hasNext()) {
                DataSources.destroy(connectionPoolDataSourcesMap.get(it.next()));
            }
		} catch (SQLException ex) {
			log.error("ERROR ERROR ERROR failed to close connection pool: " + ex.getMessage());
		}
	}
}
