<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page contentType="text/html; charset=UTF-8"%>

<%@ page import="
	com.scooterframework.orm.sqldataexpress.object.RESTified,
	com.scooterframework.web.util.O,
	com.scooterframework.web.util.R,
	com.scooterframework.web.util.W"
%>

<%
RESTified post = (RESTified)W.request("post");
%>

<h2>Show post</h2>
<%=W.errorMessage("post")%>


<p>
  <b>Id:</b>
  <%=O.hv("post.id")%>
</p>
<p>
  <b>Dateupdated:</b>
  <%=O.hv("post.dateupdated")%>
</p>
<p>
  <b>Title:</b>
  <%=O.hv("post.title")%>
</p>
<p>
  <b>Content:</b>
  <%=O.hv("post.content")%>
</p>

<br />

<%=W.labelLink("Edit", R.editResourceRecordPath("posts", post))%>|
<%=W.labelLink("List", R.resourcePath("posts"))%>|
<%=W.labelLink("Paged List", R.resourcePath("posts") + "?paged=true")%>
