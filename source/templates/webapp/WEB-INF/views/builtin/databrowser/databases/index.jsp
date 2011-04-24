<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page contentType="text/html; charset=UTF-8"%>

<%@ page import="
        java.util.Iterator,
        java.util.List,
        com.scooterframework.admin.Constants,
        com.scooterframework.web.util.O,
        com.scooterframework.web.util.R,
        com.scooterframework.web.util.T,
        com.scooterframework.web.util.W"
%>

<%
String resource = (String)request.getAttribute(Constants.RESOURCE);
String recordName = (String)request.getAttribute(Constants.MODEL);
List records = (List)request.getAttribute("databases");
%>

<div id="locator">
    <p><%=W.labelLink("Home", "/")%> > Databases</p>
</div>

<h3><%=T.pluralize(O.count(records), "database")%></h3>

<table class="sTable">
<%for (Iterator it = O.iteratorOf(records); it.hasNext();) { %>
    <tr>
<%
        String db = (String)it.next();
%>
        <td><%=db%></td>
        <td><%=W.labelLink("show", R.resourceRecordPath(resource, db))%></td>
    </tr>
<%}%>
</table>