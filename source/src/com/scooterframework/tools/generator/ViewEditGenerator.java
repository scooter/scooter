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
	public ViewEditGenerator(Properties props, String controller, String model) {
		super(props, controller, model);
	}

	protected String getAction() {
		return "edit";
	}

	protected String getTemplateContent() {
		StringBuffer tpl = new StringBuffer();
		tpl.append("<%@ page import=\"").append(linebreak);
		tpl.append("        com.scooterframework.orm.sqldataexpress.object.RESTified,").append(linebreak);
		tpl.append("        com.scooterframework.web.util.F,").append(linebreak);
		tpl.append("        com.scooterframework.web.util.O,").append(linebreak);
		tpl.append("        com.scooterframework.web.util.R,").append(linebreak);
		tpl.append("        com.scooterframework.web.util.W\"").append(linebreak);
		tpl.append("%>").append(linebreak);
		tpl.append("").append(linebreak);
		tpl.append("<h2>Edit {model}</h2>").append(linebreak);
		tpl.append("<%=W.errorMessage(\"{model}\")%>").append(linebreak);
		tpl.append("").append(linebreak);
		tpl.append("<%=F.formForOpen(\"{resource}\", \"{model}\")%>").append(linebreak);

		ActiveRecord recordHome = generateActiveRecordHomeInstance(model);
		RowInfo ri = O.rowInfoOf(recordHome);
		Iterator it = O.columns(recordHome);
		while(it.hasNext()) {
			ColumnInfo ci = (ColumnInfo)it.next();
			String columnName = ci.getColumnName();
		    boolean isAuditedColumn = ri.isAuditedForCreateOrUpdate(columnName);
		    if (isAuditedColumn) continue;

			String columnNameLower = columnName.toLowerCase();
		    boolean isPKColumn = ri.isPrimaryKeyColumn(columnName);
		    if (!isPKColumn && ci.isAutoIncrement()) continue;

		    boolean isLongText = ri.isLongTextColumn(columnName, 255);

		    if (isLongText) {
				tpl.append("  <p>").append(linebreak);
				tpl.append("    <%=F.label(\"").append(columnNameLower).append("\")%><br />").append(linebreak);
				tpl.append("    <textarea id=\"{model}_").append(columnNameLower);
				tpl.append("\" name=\"").append(columnNameLower);
				tpl.append("\" cols=\"60\" rows=\"10\"");
				if (isPKColumn) tpl.append(" readonly=\"readonly\"");
				tpl.append("><%=O.hv(\"{model}.").append(columnNameLower);
				tpl.append("\")%></textarea>").append(linebreak);
				tpl.append("  </p>").append(linebreak);
		    } else {
				tpl.append("  <p>").append(linebreak);
				tpl.append("    <%=F.label(\"").append(columnNameLower).append("\")%><br />").append(linebreak);
				tpl.append("    <input type=\"text\" id=\"{model}_").append(columnNameLower);
				tpl.append("\" name=\"").append(columnNameLower).append("\" value=\"<%=O.hv(\"{model}.");
				tpl.append(columnNameLower).append("\")%>\" size=\"80\"");
				if (isPKColumn) tpl.append(" readonly=\"readonly\"");
				tpl.append("/>").append(linebreak);
				tpl.append("  </p>").append(linebreak);
		    }
		}

		tpl.append("  <input id=\"{model}_submit\" name=\"commit\" type=\"submit\" value=\"Update\" />&nbsp;&nbsp;&nbsp;<input type=\"reset\"/>").append(linebreak);
		tpl.append("<%=F.formForClose(\"{resource}\")%>").append(linebreak);
		tpl.append("").append(linebreak);
		tpl.append("<br />").append(linebreak);
		tpl.append("").append(linebreak);
		tpl.append("<%=W.labelLink(\"Show\", R.resourceRecordPath(\"{resource}\", (RESTified)W.request(\"{model}\")))%>|").append(linebreak);
		tpl.append("<%=W.labelLink(\"List\", R.resourcePath(\"{resource}\"))%>|").append(linebreak);
		tpl.append("<%=W.labelLink(\"Paged List\", R.resourcePath(\"{resource}\") + \"?paged=true\")%>").append(linebreak);
		return tpl.toString();
	}
}