<%@ page import="
        java.util.Iterator,
        java.util.List,
        com.scooterframework.admin.Constants,
        com.scooterframework.common.util.WordUtil,
        com.scooterframework.orm.sqldataexpress.object.RESTified,
        com.scooterframework.web.util.O,
        com.scooterframework.web.util.R,
        com.scooterframework.web.util.T,
        com.scooterframework.web.util.W"
%>

<%
String resource = (String)request.getAttribute(Constants.RESOURCE);
String recordName = (String)request.getAttribute(Constants.MODEL);
List records = (List)request.getAttribute(recordName + "_list");
%>

<h2><%=T.pluralize(O.count(records), "record")%> in <%=recordName%> List</h2>

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
        RESTified record = (RESTified)it.next();
        for (Iterator it2 = O.columnNames(record); it2.hasNext();) {
            String columnName = (String)it2.next();
            String columnValue = O.hp(record, columnName);
            if (columnName.toLowerCase().endsWith("_id")) {
                columnValue = R.simpleForeignKeyResourceRecordLink(columnName, columnValue);
            }
%>
        <td><%=columnValue%></td>
<%      }%>
        <td class="multilink" nowrap>
            <%=W.labelLink("show", R.resourceRecordPath(resource, record))%>
            <%=W.labelLink("edit", R.editResourceRecordPath(resource, record))%>
            <%=W.labelLink("delete", R.resourceRecordPath(resource, record), "confirm:'Are you sure?'; method:delete")%>
        </td>
    </tr>
<%}%>
</table>

<p class="multilink">
<%=W.labelLink("Add New " + recordName, R.addResourcePath(resource))%>|
<%=W.labelLink("Paged List", R.resourcePath(resource) + "?paged=true")%>
</p>
