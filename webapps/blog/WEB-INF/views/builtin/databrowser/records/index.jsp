<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page contentType="text/html; charset=UTF-8"%>

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
String database = (String)request.getAttribute("database");
String table = (String)request.getAttribute("table");
List records = (List)request.getAttribute("records");
%>

<div id="locator">
    <p><%=W.labelLink("Home", "/")%> > 
       <%=W.labelLink("Databases", R.resourcePath("databases"))%> > 
       <%=W.labelLink(database, R.resourceRecordPath("databases", database))%> > 
       <%=W.labelLink("Tables", R.nestedResourcePath("databases", database, "tables"))%> > 
       <%=W.labelLink(table, R.resourcePath(resource))%></p>
</div>

<h3><%=T.pluralize(O.count(records), "record")%> in Table <%=table%> </h3>

<table class="sTable">
    <tr>
<%for (Iterator it = O.columnNames(records); it.hasNext();) {%>
        <th><%=WordUtil.titleize((String)it.next())%></th>
<%}%>
        <th></th>
    </tr>

<%for (Iterator it = O.iteratorOf(records); it.hasNext();) { 
        RESTified record = (RESTified)it.next();
        String restfulId = O.encodedRestfulIdOf(record);
%>
    <tr class="<%=W.cycle("odd, even")%>">
<%
        for (Iterator it2 = O.columnNames(record); it2.hasNext();) {
            String columnName = (String)it2.next();
%>
        <td><%=O.hp(record, columnName)%></td>
<%      }%>
        <td class="multilink" nowrap>
            <%=W.labelLink("show", R.resourceRecordPath(resource, restfulId))%>
            <%=W.labelLink("edit", R.editResourceRecordPath(resource, restfulId))%>
            <%=W.labelLink("delete", R.resourceRecordPath(resource, restfulId), "confirm:'Are you sure?'; method:delete")%>
        </td>
    </tr>
<%}%>
</table>

<p class="multilink">
<%=W.labelLink("Add New Record", R.addResourcePath(resource))%>&nbsp;|
<%=W.labelLink("Paged List", R.resourcePath(resource) + "?paged=true")%>
</p>
