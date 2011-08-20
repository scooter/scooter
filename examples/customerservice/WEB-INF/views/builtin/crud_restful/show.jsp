<%@ page import="
        java.util.Iterator,
        com.scooterframework.admin.Constants,
        com.scooterframework.common.util.WordUtil,
        com.scooterframework.orm.sqldataexpress.object.RESTified,
        com.scooterframework.web.util.O,
        com.scooterframework.web.util.R,
        com.scooterframework.web.util.W"
%>

<%
String resource = (String)request.getAttribute(Constants.RESOURCE);
String recordName = (String)request.getAttribute(Constants.MODEL);
RESTified record = (RESTified)request.getAttribute(recordName);
%>

<h2>Show <%=recordName%></h2>

<table class="sTable">
<%for (Iterator it = O.columnNames(record); it.hasNext();) {
    String columnName = (String)it.next();
%>
    <tr>
        <td align="right"><b><%=WordUtil.titleize(columnName)%>:</b></td>
        <td><%=O.hp(record, columnName)%></td>
    </tr>
<%}%>
</table>

<p class="multilink">
<%=W.labelLink("Edit", R.editResourceRecordPath(resource, record))%>|
<%=W.labelLink("List", R.resourcePath(resource))%>|
<%=W.labelLink("Paged List", R.resourcePath(resource) + "?paged=true")%>
</p>
