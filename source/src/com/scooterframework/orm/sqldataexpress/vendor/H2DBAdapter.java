/*
 *   This software is distributed under the terms of the FSF
 *   Gnu Lesser General Public License (see lgpl.txt).
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.sqldataexpress.vendor;

import java.util.Map;
import java.util.Properties;

import com.scooterframework.common.util.Util;
import com.scooterframework.orm.sqldataexpress.config.DatabaseConfig;
import com.scooterframework.orm.sqldataexpress.processor.DataProcessor;
import com.scooterframework.orm.sqldataexpress.util.SqlExpressUtil;

/**
 * H2DBAdapter class applies to H2 database.
 *
 * @author (Fei) John Chen
 */
public class H2DBAdapter extends DBAdapter {

	@Override
	public String[] getCatalogAndSchema(String connName) {
		String s = getH2Schema(connName);
        if (s != null) s = s.toUpperCase();
        String[] s2 = new String[2];
		s2[0] = null;
        s2[1] = s;
		return s2;
	}

	protected String getH2Schema(String connName) {
		Properties p = SqlExpressUtil.getConnectionProperties(connName);
		String schema = p.getProperty(DatabaseConfig.KEY_DB_CONNECTION_SCHEMA);
		if (isEmpty(schema)) {
			if (useLoginAsSchema(connName)) {
				schema = getLoginUserId();
			}

			if (isEmpty(schema)) {
				String url = p
						.getProperty(DatabaseConfig.KEY_DB_CONNECTION_URL);
				if (url == null || url.indexOf("SCHEMA=") == -1)
					return "PUBLIC";
				int schemaToken = url.indexOf("SCHEMA=");
				int split = url.indexOf(';', schemaToken);
				schema = (split == -1) ? url.substring(schemaToken + 1) : url
						.substring(schemaToken + 1, split);
			}
		}
		return schema;
	}

	@Override
    public String getOneRowSelectSQL(String catalog, String schema, String tableName) {
    	String selectSQL = "SELECT TOP 1 * FROM ";
        return selectSQL + getExpandedTableName(catalog, schema, tableName);
    }

	@Override
	public String preparePaginationSql(String selectSql, Map<String, Object> inputs, Map<String, String> outputFilters) {
		if (selectSql == null)
			throw new IllegalArgumentException("Input selectSql is null.");

		if (!selectSql.trim().toUpperCase().startsWith("SELECT")) {
			throw new IllegalArgumentException("Input selectSql must start with SELECT: " + selectSql);
		}

        int offset = Util.getIntValue(inputs, DataProcessor.input_key_records_offset, 0);
        boolean hasOffset = (offset > 0)?true:false;
        int limit = Util.getIntValue(inputs, DataProcessor.input_key_records_limit, DataProcessor.DEFAULT_PAGINATION_LIMIT);
        if (limit == DataProcessor.NO_ROW_LIMIT) limit = DataProcessor.DEFAULT_PAGINATION_LIMIT;

        StringBuilder newSelectSqlBF = new StringBuilder(selectSql.length());
        newSelectSqlBF.append(selectSql);
        newSelectSqlBF.append(" LIMIT ?").append(DataProcessor.input_key_records_limit).append(":INTEGER");
        inputs.put(DataProcessor.input_key_records_limit, Integer.valueOf(limit));

        if (hasOffset) {
            newSelectSqlBF.append(" OFFSET ?").append(DataProcessor.input_key_records_offset).append(":INTEGER");
            inputs.put(DataProcessor.input_key_records_offset, Integer.valueOf(offset));
        }

        return newSelectSqlBF.toString();
    }
}
