<%@ page import="
	com.scooterframework.web.util.F,
	com.scooterframework.web.util.O,
	com.scooterframework.web.util.W"
%>

<h2>Add post</h2>
<%=W.errorMessage("post")%>

<%=F.formForOpen("posts", "post", "data-ajax:true; data-target:#posts_list; data-handler:html")%>

<p>
  <%=F.label("name")%><br />
  <input type="text" id="post_name" name="name" value="<%=O.hv("post.name")%>" size="80" /> 
</p>

<p>
  <%=F.label("title")%><br />
  <input type="text" id="post_title" name="title" value="<%=O.hv("post.title")%>" size="80" /> 
</p>

<p>
  <%=F.label("content")%><br />
  <textarea id="post_content" name="content" cols="60" rows="10"><%=O.hv("post.content")%></textarea>
</p>


  <input id="post_submit" name="commit" type="submit" value="Create" />&nbsp;&nbsp;&nbsp;<input type="reset"/>
<%=F.formForClose("posts")%>
