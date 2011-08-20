<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page contentType="text/html; charset=UTF-8"%>

<%@ page import="
        java.util.Iterator,
        java.util.List,
        com.scooterframework.common.util.WordUtil,
        com.scooterframework.orm.sqldataexpress.object.RESTified,
        com.scooterframework.admin.Constants,
        com.scooterframework.web.util.O,
        com.scooterframework.web.util.R,
        com.scooterframework.web.util.T,
        com.scooterframework.web.util.W"
%>

<%
String resource = (String)request.getAttribute(Constants.RESOURCE);
String database = (String)request.getAttribute("database");
String view = (String)request.getAttribute("view");
List records = (List)request.getAttribute("vrecords");
%>

<div id="locator">
    <p><%=W.labelLink("Home", "/")%> > 
       <%=W.labelLink("Databases", R.resourcePath("databases"))%> > 
       <%=W.labelLink(database, R.resourceRecordPath("databases", database))%> > 
       <%=W.labelLink("Views", R.nestedResourcePath("databases", database, "views"))%> > 
       <%=W.labelLink(view, R.resourcePath(resource))%></p>
</div>

<h3><%=T.pluralize(O.count(records), "vrecord")%> in View <%=view%> </h3>

<table class="sTable">
    <tr>
<%for (Iterator it = O.columnNames(records); it.hasNext();) {%>
        <th><%=WordUtil.titleize((String)it.next())%></th>
<%}%>
        <th></th>
    </tr>

<%for (Iterator it = O.iteratorOf(records); it.hasNext();) { 
        RESTified record = (RESTified)it.next();
%>
    <tr class="<%=W.cycle("odd, even")%>">
<%
        for (Iterator it2 = O.columnNames(record); it2.hasNext();) {
            String columnName = (String)it2.next();
%>
        <td><%=O.hp(record, columnName)%></td>
<%      }%>
    </tr>
<%}%>
</table>

<p class="multilink">
<%=W.labelLink("Paged List", R.resourcePath(resource) + "?paged=true")%>
</p>
