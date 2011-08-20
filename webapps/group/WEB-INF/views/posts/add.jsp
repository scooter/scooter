<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page contentType="text/html; charset=UTF-8"%>

<%@ page import="
	com.scooterframework.web.util.F,
	com.scooterframework.web.util.O,
	com.scooterframework.web.util.R,
	com.scooterframework.web.util.W"
%>

<h2>Add post</h2>
<%=W.errorMessage("post")%>

<%=F.formForOpen("posts", "post")%>

<p>
    <span class="required">*</span><%=F.label("id")%><br />
  <input type="text" id="post_id" name="id" value="<%=O.hv("post.id")%>" size="80" /> 
</p>

<p>
    <%=F.label("title")%><br />
  <input type="text" id="post_title" name="title" value="<%=O.hv("post.title")%>" size="80" /> 
</p>

<p>
    <%=F.label("content")%><br />
  <input type="text" id="post_content" name="content" value="<%=O.hv("post.content")%>" size="80" /> 
</p>


  <input id="post_submit" name="commit" type="submit" value="Create" />&nbsp;&nbsp;&nbsp;<input type="reset"/>
<%=F.formForClose("posts")%>

<br />

<%=W.labelLink("List", R.resourcePath("posts"))%>|
<%=W.labelLink("Paged List", R.resourcePath("posts") + "?paged=true")%>
