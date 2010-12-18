<%@ page import="
        java.util.Iterator,
        com.scooterframework.orm.sqldataexpress.object.ColumnInfo,
        com.scooterframework.orm.sqldataexpress.object.RowInfo,
        com.scooterframework.web.util.O,
        com.scooterframework.web.util.R,
        com.scooterframework.web.util.T,
        com.scooterframework.web.util.W"
%>

<%
String database = (String)request.getAttribute("database");
String table = (String)request.getAttribute("table");
String recordsCount = (String)request.getAttribute("records_count");
String resource = "records";
String[] parentResourceNames = {"databases", "tables"};
String[] parentRestfuls = {database, table};
Iterator cols = O.columns((RowInfo)W.get("header"));
%>

<div id="locator">
    <p><%=W.labelLink("Home", "/")%> > 
       <%=W.labelLink("Databases", R.resourcePath("databases"))%> > 
       <%=W.labelLink(database, R.resourceRecordPath("databases", database))%> >
       <%=W.labelLink("Tables", R.nestedResourcePath("databases", database, "tables"))%> > 
       <%=table%></p>
</div>

<h3><%=T.pluralize(recordsCount, "record")%> in Table <%=table%></h3>

<table class="sTable">
<tr>
  <th>Column</th>
  <th>Index</th>
  <th>PK</th>
  <th>Auto</th>
  <th>Java Class</th>
  <th>SQL Type</th>
  <th>Type Name</th>
  <th>Size</th>
  <th>Scale</th>
  <th>Precision</th>
  <th>Null</th>
</tr>
<%
int index = 0;
while(cols.hasNext()) {
    ColumnInfo ci = (ColumnInfo)cols.next();
    index++;
%>
<tr class="<%=W.cycle("odd, even")%>">
  <td><%=ci.getColumnName().toLowerCase()%></td>
  <td align="right"><%=index%></td>
  <td align="center"><%=T.booleanWord(ci.isPrimaryKey(), "Y", "")%></td>
  <td align="center"><%=T.booleanWord(ci.isAutoIncrement(), "Y", "")%></td>
  <td><%=ci.getColumnClassName()%></td>
  <td align="right"><%=ci.getSQLDataType()%></td>
  <td><%=ci.getColumnTypeName()%></td>
  <td align="right"><%=ci.getColumnDisplaySize()%></td>
  <td align="right"><%=ci.getPrecision()%></td>
  <td align="right"><%=ci.getScale()%></td>
  <td align="center"><%=T.booleanWord(ci.isNull(), "Y", "")%></td>
</tr>
<%}%>
</table>

<p class="multilink">
<%=W.labelLink("Add New Record", R.addNestedResourcePath(parentResourceNames, parentRestfuls, resource))%>&nbsp;|
<%=W.labelLink("List", R.nestedResourcePath(parentResourceNames, parentRestfuls, resource))%>&nbsp;|
<%=W.labelLink("Paged List", R.resourcePath(resource) + "?paged=true")%>
</p>
