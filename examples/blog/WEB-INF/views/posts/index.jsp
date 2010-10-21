<%@ page import="
        java.util.Iterator,
        com.scooterframework.orm.sqldataexpress.object.RESTified,
        com.scooterframework.web.util.O,
        com.scooterframework.web.util.R,
        com.scooterframework.web.util.T,
        com.scooterframework.web.util.W"
%>

<h2><%=T.pluralize(O.count("posts"), "post")%></h2>

<table class="sTable">
    <tr>
        <th>Id</th>
        <th>Name</th>
        <th>Title</th>
        <th>Created At</th>
        <th>Updated At</th>
        <th>Comments</th>
        <th></th>
    </tr>
<%
for (Iterator it = O.iteratorOf("posts"); it.hasNext();) {
    RESTified post = (RESTified)it.next();
%>
    <tr class="<%=W.cycle("odd, even")%>">
        <td><%=O.hp(post, "id")%></td>
        <td><%=O.hp(post, "name")%></td>
        <td><%=O.hp(post, "title")%></td>
        <td><%=O.hp(post, "created_at")%></td>
        <td><%=O.hp(post, "updated_at")%></td>
        <td><%=O.hp(post, "comments_count")%></td>
        <td nowrap>
            <%=W.labelLink("show", R.resourceRecordPath("posts", post))%>
            <%=W.labelLink("edit", R.editResourceRecordPath("posts", post))%>
            <%=W.labelLink("delete", R.resourceRecordPath("posts", post), "confirm:'Are you sure?'; method:delete")%>
        </td>
    </tr>
<%}%>
</table>

<br />

<%=W.labelLink("Add post", R.addResourcePath("posts"))%>|
<%=W.labelLink("Paged List", R.resourcePath("posts") + "?paged=true")%>
