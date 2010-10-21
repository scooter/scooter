<%@ page import="
        com.scooterframework.web.util.F,
        com.scooterframework.web.util.O,
        com.scooterframework.web.util.R,
        com.scooterframework.web.util.W"
%>

<h2>Add comment</h2>
<%=W.errorMessage("comment")%>

<%=F.formForOpen("comments", "comment")%>
  <p>
    <%=F.label("commenter")%><br />
    <input type="text" id="comment_commenter" name="commenter" value="<%=O.hv("comment.commenter")%>" size="80" />
  </p>
  <p>
    <%=F.label("body")%><br />
    <textarea id="comment_body" name="body" cols="60" rows="10"><%=O.hv("comment.body")%></textarea>
  </p>
  <p>
    <%=F.label("post_id")%><br />
    <input type="text" id="comment_post_id" name="post_id" value="<%=O.hv("comment.post_id")%>" size="80" />
  </p>
  <input id="comment_submit" name="commit" type="submit" value="Create" />&nbsp;&nbsp;&nbsp;<input type="reset"/>
<%=F.formForClose("comments")%>

<br />

<%=W.labelLink("List", R.resourcePath("comments"))%>|
<%=W.labelLink("Paged List", R.resourcePath("comments") + "?paged=true")%>
