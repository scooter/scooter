<%@ page import="
        java.util.Iterator,
        java.util.List,
        java.util.Properties,
        com.scooterframework.admin.Constants,
        com.scooterframework.common.util.NamedProperties,
        com.scooterframework.orm.sqldataexpress.object.TableInfo,
        com.scooterframework.web.util.O,
        com.scooterframework.web.util.R,
        com.scooterframework.web.util.W"
%>

<%
String resource = (String)request.getAttribute(Constants.RESOURCE);
String database = (String)request.getAttribute("database");
List tableInfos = (List)request.getAttribute("views");
%>

<div id="locator">
    <p><%=W.labelLink("Home", "/")%> > 
       <%=W.labelLink("Databases", R.resourcePath("databases"))%> > 
       <%=W.labelLink(database, R.resourceRecordPath("databases", database))%> > 
       <%=W.labelLink("Views", R.resourcePath(resource))%></p>
</div>

<h3>Views (<%=O.count(tableInfos)%>)</h3>

<table class="sTable">
    <tr>
        <th>Name</th>
        <th>Catalog</th>
        <th>Schema</th>
    </tr>
<%for (Iterator it = O.iteratorOf(tableInfos); it.hasNext();) { 
        TableInfo ti = (TableInfo)it.next();
%>
    <tr class="<%=W.cycle("odd, even")%>">
        <td><%=W.labelLink(ti.getName(), R.nestedResourceRecordPath("databases", database, "views", ti.getName()))%></td>
        <td><%=ti.getCatalog()%></td>
        <td><%=ti.getSchema()%></td>
    </tr>
<%}%>
</table>