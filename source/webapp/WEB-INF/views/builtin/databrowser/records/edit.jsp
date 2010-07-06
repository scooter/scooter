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
       <%=W.labelLink(O.restfulIdOf(record), R.resourceRecordPath(resource, O.encodedRestfulIdOf(record)))%></p>
</div>

<h3>Edit <%=table%> record</h3>

<%=R.formForEditResourceRecord(resource, record)%>
<table class="sTable">
<%
RowInfo ri = O.rowInfoOf(record);
for (Iterator it = O.columnNames(record); it.hasNext();) {
    String columnName = (String)it.next();
    boolean isAuditedColumn = ri.isAuditedForCreateOrUpdate(columnName);
    if (isAuditedColumn) continue;
    
    Object columnValue = record.getField(columnName);
    boolean isPKColumn = ri.isPrimaryKeyColumn(columnName);
    boolean isRequired = ri.isRequiredColumn(columnName);
    boolean isLongText = ri.isLongTextColumn(columnName, 255);
    if (isLongText) {
%>
    <tr>
        <td align="right"><%if (isRequired) {%><span class="required">*</span><%}%><b><%=WordUtil.titleize(columnName)%>:</b></td>
        <td><textarea name="<%=columnName%>" cols="60" rows="10" <%if (isPKColumn) {%> readonly="readonly" <%}%> ><%=W.h(T.text(columnValue))%></textarea></td>
    </tr>
<%  } else { %>
    <tr>
        <td align="right"><%if (isRequired) {%><span class="required">*</span><%}%><b><%=WordUtil.titleize(columnName)%>:</b></td>
        <td><input type="TEXT" name="<%=columnName%>" value="<%=W.h(T.text(columnValue))%>" size="80" <%if (isPKColumn) {%> readonly="readonly" <%}%> /></td>
    </tr>
<%
    }
}
%>
    <tr>
        <td colspan="2">
            <input type="submit" value="Update" />
            &nbsp;&nbsp;&nbsp;
            <input type="reset"/>
        </td>
    </tr>
</table>
</form>

<p class="multilink">
<%=W.labelLink("Show", R.resourceRecordPath(resource, O.encodedRestfulIdOf(record)))%>&nbsp;|
<%=W.labelLink("List", R.resourcePath(resource))%>&nbsp;|
<%=W.labelLink("Paged List", R.resourcePath(resource) + "?paged=true")%>
</p>