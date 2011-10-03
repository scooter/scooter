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
import com.scooterframework.web.util.O;

/**
 * This class generates a specific action view code.
 *
 * @author (Fei) John Chen
 */
public class ViewPagedGenerator extends ViewScaffoldGenerator {
	public ViewPagedGenerator(String templateFilePath,
			Map<String, String> props, String connName, String controller,
			String model, String table) {
		super(templateFilePath, props, connName, controller, model, table);
	}

	protected String getAction() {
		return "paged_list";
	}

	@Override
	protected Map<String, Object> getTemplateProperties() {
		Map<String, Object> templateProps = new HashMap<String, Object>();

		List<Map<String, String>> columns = new ArrayList<Map<String, String>>();
		Iterator<String> it = O.columnNames(recordHome);
		while(it.hasNext()) {
			Map<String, String> column = new HashMap<String, String>();
			String columnName = it.next();
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