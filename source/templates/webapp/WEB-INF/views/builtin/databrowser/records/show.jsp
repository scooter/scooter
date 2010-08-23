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
String database = (String)request.getAttribute("database");
String table = (String)request.getAttribute("table");
RESTified record = (RESTified)request.getAttribute("record");
String resource = (String)request.getAttribute(Constants.RESOURCE);
%>

<div id="locator">
    <p><%=W.labelLink("Home", "/")%> > 
       <%=W.labelLink("Databases", R.resourcePath("databases"))%> > 
       <%=W.labelLink(database, R.resourceRecordPath("databases", database))%> > 
       <%=W.labelLink("Tables", R.nestedResourcePath("databases", database, "tables"))%> > 
       <%=W.labelLink(table, R.resourcePath(resource))%> > 
       <%=O.restfulIdOf(record)%></p>
</div>

<h3>Show <%=table%> record</h3>

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
<%=W.labelLink("Edit", R.editResourceRecordPath(resource, O.encodedRestfulIdOf(record)))%>&nbsp;|
<%=W.labelLink("List", R.resourcePath(resource))%>&nbsp;|
<%=W.labelLink("Paged List", R.resourcePath(resource) + "?paged=true")%>
</p>