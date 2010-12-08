<%@ page import="
        java.util.Iterator,
        java.util.List,
        java.util.Properties,
        com.scooterframework.admin.Constants,
        com.scooterframework.builtin.databrowser.Database,
        com.scooterframework.common.util.NamedProperties,
        com.scooterframework.orm.sqldataexpress.object.TableInfo,
        com.scooterframework.web.util.O,
        com.scooterframework.web.util.R,
        com.scooterframework.web.util.W"
%>

<%
String resource = (String)request.getAttribute(Constants.RESOURCE);
Properties connInfo = (Properties)request.getAttribute("database");
String database = O.property(connInfo, NamedProperties.KEY_NAME);
String[] s2 = Database.getCatalogAndSchema(database);
String schema = (String)W.get("schema", s2[1]);
%>

<div id="locator">
    <p><%=W.labelLink("Home", "/")%> > 
       <%=W.labelLink("Databases", R.resourcePath(resource))%> > 
       <%=database%></p>
</div>

<table class="sTable">
<%for (Iterator it = O.iteratorOf(connInfo); it.hasNext();) { %>
    <tr>
<%
        String key = (String)it.next();
        String value = connInfo.getProperty(key);
        if ("password".equalsIgnoreCase(key)) value = "******";
%>
        <td><b><%=key%></b></td>
        <td><%=value%></td>
    </tr>
<%}%>
</table>

<p><%=W.labelLink("Tables", R.nestedResourcePath(resource, database, "tables"))%> | 
   <%=W.labelLink("Views", R.nestedResourcePath(resource, database, "views"))%>
</p>

<%if (Database.isOracle(database)) {%>
<form action="<%=W.getURL(R.resourceRecordPath(resource, database))%>">
<table>
    <tr>
        <td align="right"><b>Enter schema name:</b></td>
        <td><input type="TEXT" name="schema" value="<%=schema%>" size="20" />&nbsp;</td>
        <td colspan="2">
            <input name="submit"  type="submit" value="Tables" />
            <input name="submit"  type="submit" value="Views" />
        </td>
    </tr>
</table>
</form>
<%}%>
