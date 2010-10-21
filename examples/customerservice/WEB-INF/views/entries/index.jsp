<%@ page import="
        java.util.Iterator,
        com.scooterframework.orm.sqldataexpress.object.RESTified,
        com.scooterframework.web.util.O,
        com.scooterframework.web.util.R,
        com.scooterframework.web.util.T,
        com.scooterframework.web.util.W"
%>

<h2><%=T.pluralize(O.count("entries"), "entry")%></h2>

<table class="sTable">
    <tr>
        <th>Id</th>
        <th>Name</th>
        <th>Content</th>
        <th>Created At</th>
        <th></th>
    </tr>
<%
for (Iterator it = O.iteratorOf("entries"); it.hasNext();) {
    RESTified entry = (RESTified)it.next();
%>
    <tr class="<%=W.cycle("odd, even")%>">
        <td><%=O.hp(entry, "id")%></td>
        <td><%=O.hp(entry, "name")%></td>
        <td><%=O.hp(entry, "content")%></td>
        <td><%=O.hp(entry, "created_at")%></td>
        <td nowrap>
            <%=W.labelLink("show", R.resourceRecordPath("entries", entry))%>
            <%=W.labelLink("edit", R.editResourceRecordPath("entries", entry))%>
            <%=W.labelLink("delete", R.resourceRecordPath("entries", entry), "confirm:'Are you sure?'; method:delete")%>
        </td>
    </tr>
<%}%>
</table>

<br />

<%=W.labelLink("Add entry", R.addResourcePath("entries"))%>|
<%=W.labelLink("Paged List", R.resourcePath("entries") + "?paged=true")%>
