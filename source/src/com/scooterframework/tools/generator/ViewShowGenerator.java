/*
 *   This software is distributed under the terms of the FSF
 *   Gnu Lesser General Public License (see lgpl.txt).
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.tools.generator;

import java.util.Iterator;
import java.util.Properties;

import com.scooterframework.common.util.WordUtil;
import com.scooterframework.orm.activerecord.ActiveRecord;
import com.scooterframework.web.util.O;

/**
 * This class generates a specific action view code.
 *
 * @author (Fei) John Chen
 */
public class ViewShowGenerator extends ViewScaffoldGenerator {
	public ViewShowGenerator(Properties props, String controller, String model) {
		super(props, controller, model);
	}

	protected String getAction() {
		return "show";
	}

	protected String getTemplateContent() {
		StringBuffer tpl = new StringBuffer();
		tpl.append("<%@ page import=\"").append(linebreak);
		tpl.append("        com.scooterframework.orm.sqldataexpress.object.RESTified,").append(linebreak);
		tpl.append("        com.scooterframework.web.util.O,").append(linebreak);
		tpl.append("        com.scooterframework.web.util.R,").append(linebreak);
		tpl.append("        com.scooterframework.web.util.W\"").append(linebreak);
		tpl.append("%>").append(linebreak);
		tpl.append("").append(linebreak);
		tpl.append("<%").append(linebreak);
		tpl.append("RESTified {model} = (RESTified)W.request(\"{model}\");").append(linebreak);
		tpl.append("%>").append(linebreak);
		tpl.append("").append(linebreak);
		tpl.append("<h2>Show {model}</h2>").append(linebreak);
		tpl.append("<%=W.errorMessage(\"{model}\")%>").append(linebreak);
		tpl.append("").append(linebreak);

		ActiveRecord recordHome = generateActiveRecordHomeInstance(model);
		Iterator it = O.columnNames(recordHome);
		while(it.hasNext()) {
			String columnName = (String)it.next();
			String columnHeader = WordUtil.titleize(columnName);
			String columnNameLower = columnName.toLowerCase();
			tpl.append("<p>").append(linebreak);
			tpl.append("  <b>").append(columnHeader).append(":</b>").append(linebreak);
			tpl.append("  <%=O.hv(\"{model}.").append(columnNameLower).append("\")%>").append(linebreak);
			tpl.append("</p>").append(linebreak);
			tpl.append("").append(linebreak);
		}

		tpl.append("<br />").append(linebreak);
		tpl.append("").append(linebreak);
		tpl.append("<%=W.labelLink(\"Edit\", R.editResourceRecordPath(\"{resource}\", {model}))%>|").append(linebreak);
		tpl.append("<%=W.labelLink(\"List\", R.resourcePath(\"{resource}\"))%>|").append(linebreak);
		tpl.append("<%=W.labelLink(\"Paged List\", R.resourcePath(\"{resource}\") + \"?paged=true\")%>").append(linebreak);
		return tpl.toString();
	}
}