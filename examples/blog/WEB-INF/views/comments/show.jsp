<%@ page import="
        com.scooterframework.orm.sqldataexpress.object.RESTified,
        com.scooterframework.web.util.O,
        com.scooterframework.web.util.R,
        com.scooterframework.web.util.W"
%>

<%
RESTified comment = (RESTified)W.request("comment");
%>

<h2>Show comment</h2>
<%=W.errorMessage("comment")%>

<p>
  <b>Id:</b>
  <%=O.hv("comment.id")%>
</p>

<p>
  <b>Commenter:</b>
  <%=O.hv("comment.commenter")%>
</p>

<p>
  <b>Body:</b>
  <%=O.hv("comment.body")%>
</p>

<p>
  <b>Post:</b>
  <%=O.hv("comment.post_id")%>
</p>

<p>
  <b>Created At:</b>
  <%=O.hv("comment.created_at")%>
</p>

<br />

<%=W.labelLink("Edit", R.editResourceRecordPath("comments", comment))%>|
<%=W.labelLink("List", R.resourcePath("comments"))%>|
<%=W.labelLink("Paged List", R.resourcePath("comments") + "?paged=true")%>
