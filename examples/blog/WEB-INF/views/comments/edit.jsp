<%@ page import="
        com.scooterframework.orm.sqldataexpress.object.RESTified,
        com.scooterframework.web.util.F,
        com.scooterframework.web.util.O,
        com.scooterframework.web.util.R,
        com.scooterframework.web.util.W"
%>

<h2>Edit comment</h2>
<%=W.errorMessage("comment")%>

<%=F.formForOpen("comments", "comment")%>
  <p>
    <%=F.label("id")%><br />
    <input type="text" id="comment_id" name="id" value="<%=O.hv("comment.id")%>" size="80" readonly="readonly"/>
  </p>
  <p>
    <%=F.label("commenter")%><br />
    <input type="text" id="comment_commenter" name="commenter" value="<%=O.hv("comment.commenter")%>" size="80"/>
  </p>
  <p>
    <%=F.label("body")%><br />
    <textarea id="comment_body" name="body" cols="60" rows="10"><%=O.hv("comment.body")%></textarea>
  </p>
  <p>
    <%=F.label("post_id")%><br />
    <input type="text" id="comment_post_id" name="post_id" value="<%=O.hv("comment.post_id")%>" size="80"/>
  </p>
  <input id="comment_submit" name="commit" type="submit" value="Update" />&nbsp;&nbsp;&nbsp;<input type="reset"/>
<%=F.formForClose("comments")%>

<br />

<%=W.labelLink("Show", R.resourceRecordPath("comments", (RESTified)W.request("comment")))%>|
<%=W.labelLink("List", R.resourcePath("comments"))%>|
<%=W.labelLink("Paged List", R.resourcePath("comments") + "?paged=true")%>
