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
public class ViewPagedGenerator extends ViewScaffoldGenerator {
	public ViewPagedGenerator(Properties props, String controller, String model) {
		super(props, controller, model);
	}

	protected String getAction() {
		return "paged_list";
	}

	protected String getTemplateContent() {
		StringBuffer tpl = new StringBuffer();
		tpl.append("<%@ page import=\"").append(linebreak);
		tpl.append("        java.util.Iterator,").append(linebreak);
		tpl.append("        java.util.List,").append(linebreak);
		tpl.append("        com.scooterframework.orm.misc.Paginator,").append(linebreak);
		tpl.append("        com.scooterframework.orm.sqldataexpress.object.RESTified,").append(linebreak);
		tpl.append("        com.scooterframework.web.util.O,").append(linebreak);
		tpl.append("        com.scooterframework.web.util.R,").append(linebreak);
		tpl.append("        com.scooterframework.web.util.W\"").append(linebreak);
		tpl.append("%>").append(linebreak);
		tpl.append("").append(linebreak);
		tpl.append("<%").append(linebreak);
		tpl.append("Paginator paginator = (Paginator)request.getAttribute(\"{model}_page\");").append(linebreak);
		tpl.append("List records = paginator.getRecordList();").append(linebreak);
		tpl.append("%>").append(linebreak);
		tpl.append("").append(linebreak);
		tpl.append("<h2>{model} List</h2>").append(linebreak);
		tpl.append("").append(linebreak);
		tpl.append("<table>").append(linebreak);
		tpl.append("    <tr>").append(linebreak);
		tpl.append("        <td align=\"left\">").append(linebreak);
		tpl.append("            Page <%=paginator.getCurrentPage()%>/<%=paginator.getPageCount()%>. </td>").append(linebreak);
		tpl.append("        <td align=\"right\" class=\"multilink\">").append(linebreak);
		tpl.append("            Showing <%=paginator.getStartIndex()%> - <%=paginator.getEndIndex()%> of <%=paginator.getTotalCount()%>&nbsp;").append(linebreak);
		tpl.append("            <%=W.pageLink(\"First\",    R.resourcePath(\"{resource}\"), paginator.getQueryStringFirst())%>&nbsp;|&nbsp;").append(linebreak);
		tpl.append("            <%=W.pageLink(\"Previous\", R.resourcePath(\"{resource}\"), paginator.getQueryStringPrevious())%>&nbsp;|&nbsp;").append(linebreak);
		tpl.append("            <%=W.pageLink(\"Next\",     R.resourcePath(\"{resource}\"), paginator.getQueryStringNext())%>&nbsp;|&nbsp;").append(linebreak);
		tpl.append("            <%=W.pageLink(\"Last\",     R.resourcePath(\"{resource}\"), paginator.getQueryStringLast())%>").append(linebreak);
		tpl.append("        </td>").append(linebreak);
		tpl.append("    </tr>").append(linebreak);
		tpl.append("    <tr>").append(linebreak);
		tpl.append("        <td colspan=\"2\">").append(linebreak);
		tpl.append("").append(linebreak);
		tpl.append("<table class=\"sTable\">").append(linebreak);
		tpl.append("    <tr>").append(linebreak);

		ActiveRecord recordHome = generateActiveRecordHomeInstance(model);
		Iterator it = O.columnNames(recordHome);
		while(it.hasNext()) {
			String columnName = WordUtil.titleize((String)it.next());
			tpl.append("        <th>").append(columnName).append("</th>").append(linebreak);
		}

		tpl.append("        <th>&nbsp;</th>").append(linebreak);
		tpl.append("    </tr>").append(linebreak);
		tpl.append("").append(linebreak);
		tpl.append("<%for (Iterator it = O.iteratorOf(records); it.hasNext();) {").append(linebreak);
		tpl.append("    RESTified {model} = (RESTified)it.next();").append(linebreak);
		tpl.append("%>").append(linebreak);
		tpl.append("    <tr class=\"<%=W.cycle(\"odd, even\")%>\">").append(linebreak);

		Iterator it2 = O.columnNames(recordHome);
		while(it2.hasNext()) {
			String columnNameLower = ((String)it2.next()).toLowerCase();
			tpl.append("        <td><%=O.hp({model}, \"").append(columnNameLower).append("\")%></td>").append(linebreak);
		}

		tpl.append("        <td class=\"multilink\" nowrap>").append(linebreak);
		tpl.append("            <%=W.labelLink(\"show\", R.resourceRecordPath(\"{resource}\", {model}))%>").append(linebreak);
		tpl.append("            <%=W.labelLink(\"edit\", R.editResourceRecordPath(\"{resource}\", {model}))%>").append(linebreak);
		tpl.append("            <%=W.labelLink(\"delete\", R.resourceRecordPath(\"{resource}\", {model}), \"confirm:'Are you sure?'; method:delete\")%>").append(linebreak);
		tpl.append("        </td>").append(linebreak);
		tpl.append("    </tr>").append(linebreak);
		tpl.append("<%}%>").append(linebreak);
		tpl.append("</table>").append(linebreak);
		tpl.append("").append(linebreak);
		tpl.append("        </td>").append(linebreak);
		tpl.append("    </tr>").append(linebreak);
		tpl.append("    <tr>").append(linebreak);
		tpl.append("        <td align=\"left\">").append(linebreak);
		tpl.append("        Page <%=paginator.getCurrentPage()%>/<%=paginator.getPageCount()%>. </td>").append(linebreak);
		tpl.append("        <td align=\"right\" class=\"multilink\">").append(linebreak);
		tpl.append("            Showing <%=paginator.getStartIndex()%> - <%=paginator.getEndIndex()%> of <%=paginator.getTotalCount()%>&nbsp;").append(linebreak);
		tpl.append("            <%=W.pageLink(\"First\",    R.resourcePath(\"{resource}\"), paginator.getQueryStringFirst())%>&nbsp;|&nbsp;").append(linebreak);
		tpl.append("            <%=W.pageLink(\"Previous\", R.resourcePath(\"{resource}\"), paginator.getQueryStringPrevious())%>&nbsp;|&nbsp;").append(linebreak);
		tpl.append("            <%=W.pageLink(\"Next\",     R.resourcePath(\"{resource}\"), paginator.getQueryStringNext())%>&nbsp;|&nbsp;").append(linebreak);
		tpl.append("            <%=W.pageLink(\"Last\",     R.resourcePath(\"{resource}\"), paginator.getQueryStringLast())%>").append(linebreak);
		tpl.append("        </td>").append(linebreak);
		tpl.append("    </tr>").append(linebreak);
		tpl.append("</table>").append(linebreak);
		tpl.append("").append(linebreak);
		tpl.append("<br />").append(linebreak);
		tpl.append("<%=W.diggStylePageLinks(paginator, R.resourcePath(\"{resource}\"), 4, 11)%>").append(linebreak);
		tpl.append("").append(linebreak);
		tpl.append("<form action=\"<%=W.getURL(R.resourcePath(\"{resource}\"))%>\" method=\"GET\">").append(linebreak);
		tpl.append("<input type=\"hidden\" name=\"r\" value=\"page\">").append(linebreak);
		tpl.append("<input type=\"hidden\" name=\"limit\" value=\"<%=W.get(\"limit\", \"10\")%>\">").append(linebreak);
		tpl.append("<input type=\"hidden\" name=\"paged\" value=\"true\">").append(linebreak);
		tpl.append("Go to page <input type=\"text\" id=\"npage\" name=\"npage\" size=\"2\"><input type=\"submit\" value=\"Go\" />").append(linebreak);
		tpl.append("</form>").append(linebreak);
		tpl.append("").append(linebreak);
		tpl.append("<%=W.labelLink(\"Add {model}\", R.addResourcePath(\"{resource}\"))%>|").append(linebreak);
		tpl.append("<%=W.labelLink(\"List\", R.resourcePath(\"{resource}\"))%>").append(linebreak);
		return tpl.toString();
	}
}