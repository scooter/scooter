<%@ page import="
	java.util.Iterator,
	com.scooterframework.orm.sqldataexpress.object.RESTified,
	com.scooterframework.web.util.O,
	com.scooterframework.web.util.R,
	com.scooterframework.web.util.T,
	com.scooterframework.web.util.W"
%>

<div id="posts_list">

<h2><%=T.pluralize(O.count("posts"), "post")%></h2>
<%=W.errorMessage("post")%>

<table class="sTable">
    <tr>
        <th>Id</th>
        <th>Name</th>
        <th>Title</th>
        <th>Content</th>
        <th>Created At</th>
        <th>Updated At</th>
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
        <td><%=O.hp(post, "content")%></td>
        <td><%=O.hp(post, "created_at")%></td>
        <td><%=O.hp(post, "updated_at")%></td>
        <td nowrap>
            <%=W.labelLink("show", R.resourceRecordPath("posts", post),     "data-ajax:true; data-method:GET; data-target:#entry; data-handler:html")%>
            <%=W.labelLink("edit", R.editResourceRecordPath("posts", post), "data-ajax:true; data-method:GET; data-target:#entry; data-handler:html")%>
            <%=W.labelLink("delete", R.resourceRecordPath("posts", post),   "data-ajax:true; data-method:DELETE; data-target:#posts_list; data-handler:html; data-confirm:Are you sure?")%>
        </td>
    </tr>
<%}%>
</table>

<br />

<%=W.labelLink("Add post", R.addResourcePath("posts"), "data-ajax:true; data-method:GET; data-target:#entry; data-handler:html")%>|
<%=W.labelLink("Paged List", R.resourcePath("posts") + "?paged=true", "data-ajax:true; data-method:GET; data-target:#posts_list; data-handler:html")%>

<br/>

<div id="entry"></div>

</div>
