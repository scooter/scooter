<%@ page import="
        java.util.Iterator,
        com.scooterframework.orm.sqldataexpress.object.RESTified,
        com.scooterframework.web.util.O,
        com.scooterframework.web.util.R,
        com.scooterframework.web.util.T,
        com.scooterframework.web.util.W"
%>

<h2><%=T.pluralize(O.count("comments"), "comment")%></h2>

<table class="sTable">
    <tr>
        <th>Id</th>
        <th>Commenter</th>
        <th>Body</th>
        <th>Post</th>
        <th>Created At</th>
        <th></th>
    </tr>
<%
for (Iterator it = O.iteratorOf("comments"); it.hasNext();) {
    RESTified comment = (RESTified)it.next();
%>
    <tr class="<%=W.cycle("odd, even")%>">
        <td><%=O.hp(comment, "id")%></td>
        <td><%=O.hp(comment, "commenter")%></td>
        <td><%=O.hp(comment, "body")%></td>
        <td><%=O.hp(comment, "post_id")%></td>
        <td><%=O.hp(comment, "created_at")%></td>
        <td nowrap>
            <%=W.labelLink("show", R.resourceRecordPath("comments", comment))%>
            <%=W.labelLink("edit", R.editResourceRecordPath("comments", comment))%>
            <%=W.labelLink("delete", R.resourceRecordPath("comments", comment), "confirm:'Are you sure?'; method:delete")%>
        </td>
    </tr>
<%}%>
</table>

<br />

<%=W.labelLink("Add comment", R.addResourcePath("comments"))%>|
<%=W.labelLink("Paged List", R.resourcePath("comments") + "?paged=true")%>
