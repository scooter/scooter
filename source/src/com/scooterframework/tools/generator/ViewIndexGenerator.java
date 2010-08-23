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
public class ViewIndexGenerator extends ViewScaffoldGenerator {
	public ViewIndexGenerator(Properties props, String controller, String model) {
		super(props, controller, model);
	}

	protected String getAction() {
		return "index";
	}

	protected String getTemplateContent() {
		StringBuffer tpl = new StringBuffer();
		tpl.append("<%@ page import=\"").append(linebreak);
		tpl.append("        java.util.Iterator,").append(linebreak);
		tpl.append("        com.scooterframework.orm.sqldataexpress.object.RESTified,").append(linebreak);
		tpl.append("        com.scooterframework.web.util.O,").append(linebreak);
		tpl.append("        com.scooterframework.web.util.R,").append(linebreak);
		tpl.append("        com.scooterframework.web.util.T,").append(linebreak);
		tpl.append("        com.scooterframework.web.util.W\"").append(linebreak);
		tpl.append("%>").append(linebreak);
		tpl.append("").append(linebreak);
		tpl.append("<h2><%=T.pluralize(O.count(\"{list_key}\"), \"{model}\")%></h2>").append(linebreak);
		tpl.append("").append(linebreak);
		tpl.append("<table class=\"sTable\">").append(linebreak);
		tpl.append("    <tr>").append(linebreak);

		ActiveRecord recordHome = generateActiveRecordHomeInstance(model);
		Iterator it = O.columnNames(recordHome);
		while(it.hasNext()) {
			String columnName = WordUtil.titleize((String)it.next());
			tpl.append("        <th>").append(columnName).append("</th>").append(linebreak);
		}

		tpl.append("        <th></th>").append(linebreak);
		tpl.append("    </tr>").append(linebreak);
		tpl.append("<%").append(linebreak);
		tpl.append("for (Iterator it = O.iteratorOf(\"{list_key}\"); it.hasNext();) {").append(linebreak);
		tpl.append("    RESTified {model} = (RESTified)it.next();").append(linebreak);
		tpl.append("%>").append(linebreak);
		tpl.append("    <tr class=\"<%=W.cycle(\"odd, even\")%>\">").append(linebreak);

		Iterator it2 = O.columnNames(recordHome);
		while(it2.hasNext()) {
			String columnNameLower = ((String)it2.next()).toLowerCase();
			tpl.append("        <td><%=O.hp({model}, \"").append(columnNameLower).append("\")%></td>").append(linebreak);
		}

		tpl.append("        <td nowrap>").append(linebreak);
		tpl.append("            <%=W.labelLink(\"show\", R.resourceRecordPath(\"{resource}\", {model}))%>").append(linebreak);
		tpl.append("            <%=W.labelLink(\"edit\", R.editResourceRecordPath(\"{resource}\", {model}))%>").append(linebreak);
		tpl.append("            <%=W.labelLink(\"delete\", R.resourceRecordPath(\"{resource}\", {model}), \"confirm:'Are you sure?'; method:delete\")%>").append(linebreak);
		tpl.append("        </td>").append(linebreak);
		tpl.append("    </tr>").append(linebreak);
		tpl.append("<%}%>").append(linebreak);
		tpl.append("</table>").append(linebreak);
		tpl.append("").append(linebreak);
		tpl.append("<br />").append(linebreak);
		tpl.append("").append(linebreak);
		tpl.append("<%=W.labelLink(\"Add {model}\", R.addResourcePath(\"{resource}\"))%>|").append(linebreak);
		tpl.append("<%=W.labelLink(\"Paged List\", R.resourcePath(\"{resource}\") + \"?paged=true\")%>").append(linebreak);
		return tpl.toString();
	}

	protected Properties getTemplateProperties() {
		Properties templateProps = super.getTemplateProperties();
		templateProps.setProperty("list_key", resource);
		return templateProps;
	}
}