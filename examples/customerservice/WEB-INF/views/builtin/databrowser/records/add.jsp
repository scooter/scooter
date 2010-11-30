<%@ page import="
        java.util.Iterator,
        com.scooterframework.admin.Constants,
        com.scooterframework.common.util.WordUtil,
        com.scooterframework.orm.sqldataexpress.object.RowData,
        com.scooterframework.orm.sqldataexpress.object.RowInfo,
        com.scooterframework.web.util.O,
        com.scooterframework.web.util.R,
        com.scooterframework.web.util.T,
        com.scooterframework.web.util.W"
%>

<%
String database = (String)request.getAttribute("database");
String table = (String)request.getAttribute("table");
RowData record = (RowData)request.getAttribute("record");
String resource = (String)request.getAttribute(Constants.RESOURCE);
%>

<div id="locator">
    <p><%=W.labelLink("Home", "/")%> > 
       <%=W.labelLink("Databases", R.resourcePath("databases"))%> > 
       <%=W.labelLink(database, R.resourceRecordPath("databases", database))%> > 
       <%=W.labelLink("Tables", R.nestedResourcePath("databases", database, "tables"))%> > 
       <%=W.labelLink(table, R.resourcePath(resource))%> > 
       Add</p>
</div>

<h3>New <%=table%> record</h3>

<%=R.formForAddResource(resource)%>
<table class="sTable">
<%
RowInfo ri = O.rowInfoOf(record);
for (Iterator it = O.columnNames(record); it.hasNext();) {
    String columnName = (String)it.next();
    boolean isAuditedColumn = ri.isAuditedForCreateOrUpdate(columnName);
    if (isAuditedColumn) continue;
    
    Object columnValue = record.getField(columnName);
    boolean isRequired = ri.isRequiredColumn(columnName);
    boolean isLongText = ri.isLongTextColumn(columnName, 255);
    boolean isDateColumn = ri.isDateColumn(columnName);
    boolean isTimestampColumn = ri.isTimestampColumn(columnName);
    int size = 80;
    if (isDateColumn || isTimestampColumn) size = 30;
%>

<%  if (isDateColumn) { %>
    <script type="text/javascript">
        $(function(){$('#<%=columnName%>').datepicker({ dateFormat: 'yy-mm-dd' });});
    </script>
<%  } %>

<%  if (isLongText) { %>
    <tr>
        <td align="right"><%if (isRequired) {%><span class="required">*</span><%}%><b><%=WordUtil.titleize(columnName)%>:</b></td>
        <td><textarea id="<%=columnName%>" name="<%=columnName%>" cols="60" rows="10"><%=T.text(columnValue)%></textarea></td>
    </tr>
<%  } else { %>
    <tr>
        <td align="right"><%if (isRequired) {%><span class="required">*</span><%}%><b><%=WordUtil.titleize(columnName)%>:</b></td>
        <td><input type="TEXT" id="<%=columnName%>" name="<%=columnName%>" value="<%=T.text(columnValue)%>" size="80" /></td>
<%        if (isDateColumn) { %> (yyyy-mm-dd) <% } %>
<%        if (isTimestampColumn) { %> (yyyy-mm-dd hh-mm-ss) <% } %>
    </tr>
<%
    }
}
%>
    <tr>
        <td colspan="2">
            <input type="submit" value="Create" />
            &nbsp;&nbsp;&nbsp;
            <input type="reset"/>
        </td>
    </tr>
</table>
</form>

<p class="multilink">
<%=W.labelLink("List", R.resourcePath(resource))%>&nbsp;|
<%=W.labelLink("Paged List", R.resourcePath(resource) + "?paged=true")%>
</p>
