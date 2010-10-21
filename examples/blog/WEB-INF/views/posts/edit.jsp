<%@ page import="
        com.scooterframework.orm.sqldataexpress.object.RESTified,
        com.scooterframework.web.util.F,
        com.scooterframework.web.util.O,
        com.scooterframework.web.util.R,
        com.scooterframework.web.util.W"
%>

<h2>Edit post</h2>
<%=W.errorMessage("post")%>

<%=F.formForOpen("posts", "post")%>
  <p>
    <%=F.label("id")%><br />
    <input type="text" id="post_id" name="id" value="<%=O.hv("post.id")%>" size="80" readonly="readonly"/>
  </p>
  <p>
    <%=F.label("name")%><br />
    <input type="text" id="post_name" name="name" value="<%=O.hv("post.name")%>" size="80"/>
  </p>
  <p>
    <%=F.label("title")%><br />
    <input type="text" id="post_title" name="title" value="<%=O.hv("post.title")%>" size="80"/>
  </p>
  <p>
    <%=F.label("content")%><br />
    <textarea id="post_content" name="content" cols="60" rows="10"><%=O.hv("post.content")%></textarea>
  </p>
  <input id="post_submit" name="commit" type="submit" value="Update" />&nbsp;&nbsp;&nbsp;<input type="reset"/>
<%=F.formForClose("posts")%>

<br />

<%=W.labelLink("Show", R.resourceRecordPath("posts", (RESTified)W.request("post")))%>|
<%=W.labelLink("List", R.resourcePath("posts"))%>|
<%=W.labelLink("Paged List", R.resourcePath("posts") + "?paged=true")%>
