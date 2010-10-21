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

import com.scooterframework.common.util.WordUtil;
import com.scooterframework.orm.activerecord.ActiveRecord;
import com.scooterframework.web.util.O;

/**
 * This class generates a specific action view code.
 *
 * @author (Fei) John Chen
 */
public class ViewPagedGenerator extends ViewScaffoldGenerator {
	public ViewPagedGenerator(String templateFilePath, Map props, String controller, String model) {
		super(templateFilePath, props, controller, model);
	}

	protected String getAction() {
		return "paged_list";
	}

	protected Map getTemplateProperties() {
		Map templateProps = new HashMap();
		
		List columns = new ArrayList();
		ActiveRecord recordHome = generateActiveRecordHomeInstance(model);
		Iterator it = O.columnNames(recordHome);
		while(it.hasNext()) {
			Map column = new HashMap();
			String columnName = (String)it.next();
			column.put("columnName", columnName);
			column.put("columnHeader", WordUtil.titleize(columnName));
			column.put("columnNameLower", columnName.toLowerCase());
		    columns.add(column);
		}
		
		templateProps.put("columns", columns);
		templateProps.putAll(super.getTemplateProperties());
		
		return templateProps;
	}
}