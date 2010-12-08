<%@ page import="
	com.scooterframework.web.util.O,
	com.scooterframework.web.util.W"
%>

<h2>Show post</h2>
<%=W.errorMessage("post")%>


<p>
  <b>Id:</b>
  <%=O.hv("post.id")%>
</p>
<p>
  <b>Name:</b>
  <%=O.hv("post.name")%>
</p>
<p>
  <b>Title:</b>
  <%=O.hv("post.title")%>
</p>
<p>
  <b>Content:</b>
  <%=O.hv("post.content")%>
</p>
<p>
  <b>Created At:</b>
  <%=O.hv("post.created_at")%>
</p>
<p>
  <b>Updated At:</b>
  <%=O.hv("post.updated_at")%>
</p>
