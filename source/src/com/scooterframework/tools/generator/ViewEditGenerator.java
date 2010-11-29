/*
 *   This software is distributed under the terms of the FSF
 *   Gnu Lesser General Public License (see lgpl.txt).
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.tools.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.scooterframework.orm.activerecord.ActiveRecord;
import com.scooterframework.orm.sqldataexpress.object.ColumnInfo;
import com.scooterframework.orm.sqldataexpress.object.RowInfo;
import com.scooterframework.web.util.O;

/**
 * This class generates a specific action view code.
 *
 * @author (Fei) John Chen
 */
public class ViewEditGenerator extends ViewScaffoldGenerator {
	public ViewEditGenerator(String templateFilePath, Map props, String controller, String model) {
		super(templateFilePath, props, controller, model);
	}

	protected String getAction() {
		return "edit";
	}

	protected Map getTemplateProperties() {
		Map templateProps = new HashMap();

		List columns = new ArrayList();
		ActiveRecord recordHome = generateActiveRecordHomeInstance(model);
		RowInfo ri = O.rowInfoOf(recordHome);
		Iterator it = O.columns(recordHome);
		while(it.hasNext()) {
			ColumnInfo ci = (ColumnInfo)it.next();
			String columnName = ci.getColumnName();
		    boolean isAuditedColumn = ri.isAuditedForCreateOrUpdate(columnName);
		    if (isAuditedColumn) continue;

		    if (ci.isAutoIncrement()) continue;

			String columnNameLower = columnName.toLowerCase();
		    boolean isPKColumn = ri.isPrimaryKeyColumn(columnName);
		    if (!isPKColumn && ci.isAutoIncrement()) continue;
		    boolean isLongText = ri.isLongTextColumn(columnName, 255);
		    boolean isDateColumn = ri.isDateColumn(columnName);
		    boolean isTimestampColumn = ri.isTimestampColumn(columnName);
		    int size = 80;
		    if (isDateColumn || isTimestampColumn) {
		    	size = 30;
		    }
		    else if (isLongText) {
		    	size = 60;
		    }
		    
		    String columnFormat = "";
		    if (isDateColumn) {
		    	columnFormat = "(yyyy-mm-dd)";
		    }
		    else if (isTimestampColumn) {
		    	columnFormat = "(yyyy-mm-dd hh-mm-ss)";
		    }
		    
		    Map column = new HashMap();
		    if (isPKColumn) column.put("readonly", "readonly");
		    column.put("isPKColumn", isPKColumn);
		    column.put("isLongText", isLongText);
		    column.put("isDateColumn", isDateColumn);
		    column.put("isTimestampColumn", isTimestampColumn);
		    column.put("size", size);
		    column.put("columnFormat", columnFormat);
		    column.put("columnNameLower", columnNameLower);
		    columns.add(column);
		}
		
		templateProps.put("columns", columns);
		templateProps.putAll(super.getTemplateProperties());
		
		return templateProps;
	}
}