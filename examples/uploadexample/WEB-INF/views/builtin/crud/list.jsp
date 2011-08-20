<%@ page import="
        java.util.Iterator,
        java.util.List,
        com.scooterframework.admin.Constants,
        com.scooterframework.common.util.WordUtil,
        com.scooterframework.orm.activerecord.ActiveRecord,
        com.scooterframework.web.util.O,
        com.scooterframework.web.util.W"
%>

<%
String recordName = (String)request.getAttribute(Constants.MODEL);
List records = (List)request.getAttribute(recordName + "_list");
%>

<h2><%=recordName%> List</h2>

<table class="sTable">
    <tr>
<%for (Iterator it = O.columnNames(records); it.hasNext();) {%>
        <th><%=WordUtil.titleize((String)it.next())%></th>
<%}%>
        <th></th>
    </tr>

<%for (Iterator it = O.iteratorOf(records); it.hasNext();) { %>
    <tr class="<%=W.cycle("odd, even")%>">
<%
        ActiveRecord record = (ActiveRecord)it.next();
        for (Iterator it2 = O.columnNames(record); it2.hasNext();) {
            String columnName = (String)it2.next();
            String columnValue = O.hp(record, columnName);
            if (columnName.toLowerCase().endsWith("_id")) {
                columnValue = W.simpleForeignKeyRecordShowActionLink(columnName, columnValue);
            }
%>
        <td><%=columnValue%></td>
<%      }%>
        <td class="multilink" nowrap>
            <%=W.labelLinkForRecord("show", "show", record)%>
            <%=W.labelLinkForRecord("edit", "edit", record)%>
            <%=W.labelLinkForRecord("delete", "delete", record)%>
        </td>
    </tr>
<%}%>
</table>

<p class="multilink">
<%=W.labelLink("Add New " + recordName, "add")%>|
<%=W.labelLink("Paged List", "paged_list")%>
</p>
