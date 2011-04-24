/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.sqldataexpress.object;

import java.sql.Types;

/**
 * JdbcStatementParameter class.
 * 
 * @author (Fei) John Chen
 */
public class JdbcStatementParameter extends Parameter {
	public JdbcStatementParameter() {
		super();
		this.mode = MODE_IN;
		bUsedByCount = false;
	}

	/**
	 * returns tableName
	 */
	public String getTableName() {
		return tableName;
	}

	/**
	 * sets tableName
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	/**
	 * returns columnName
	 */
	public String getColumnName() {
		return columnName;
	}

	/**
	 * sets columnName
	 */
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	/**
	 * sets if the parameter is used by a count() function
	 */
	public void setUsedByCount(boolean usedByCount) {
		if (usedByCount) {
			bUsedByCount = true;
			sqlDataType = Types.NUMERIC;
			sqlDataTypeName = "NUMBER";
			javaClassName = getJavaType(sqlDataType);
		}
	}

	/**
	 * checks if the parameter is used by count() function
	 */
	public boolean isUsedByCount() {
		return bUsedByCount;
	}

	/**
	 * Returns a string representation of the object.
	 * 
	 * @return String
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append(super.toString()).append(", ");

		sb.append("tableName = " + tableName).append(", ");
		sb.append("columnName = " + columnName).append(", ");
		sb.append("bUsedByCount = " + bUsedByCount);

		return sb.toString();
	}

	protected String tableName = null;
	protected String columnName = null;
	protected boolean bUsedByCount = false;
}
