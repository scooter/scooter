<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page contentType="text/html; charset=UTF-8"%>

<%@ page import="
	java.util.Iterator,
	com.scooterframework.orm.sqldataexpress.object.RESTified,
	com.scooterframework.web.util.O,
	com.scooterframework.web.util.R,
	com.scooterframework.web.util.T,
	com.scooterframework.web.util.W"
%>

<h2><%=T.pluralize(O.count("posts"), "post")%></h2>
<%=W.errorMessage("post")%>

<table class="sTable">
    <tr>
        <th>Id</th>
        <th>Dateupdated</th>
        <th>Title</th>
        <th>Content</th>
        <th></th>
    </tr>
<%
for (Iterator it = O.iteratorOf("posts"); it.hasNext();) {
    RESTified post = (RESTified)it.next();
%>
    <tr class="<%=W.cycle("odd, even")%>">
        <td><%=O.hp(post, "id")%></td>
        <td><%=O.hp(post, "dateupdated")%></td>
        <td><%=O.hp(post, "title")%></td>
        <td><%=O.hp(post, "content")%></td>
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
