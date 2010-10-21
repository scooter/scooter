<%@ page import="
        java.util.Iterator,
        com.scooterframework.orm.sqldataexpress.object.RESTified,
        com.scooterframework.web.util.F,
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

<br />
<h2>Comments</h2>
<div id="comments">
<%for (Iterator it = O.iteratorOf(O.allAssociatedRecordsOf("post.comments")); it.hasNext();) 

{
    Object comment = it.next();%>
    <p>
        <b>Commenter: </b><%=O.property(comment, "commenter")%>
        <b>posted on  </b><%=O.property(comment, "created_at")%>
    </p>
    
    <p>
        <b>Comment:</b>
        <%=O.property(comment, "body")%>
    </p>
<%}%>
</div>

<h2>Add comment</h2>
<%=W.errorMessage("comment")%>

<%=F.formForOpen("posts", post, "comments", "comment")%>
  <p>
    <%=F.label("commenter")%><br />
    <input type="text" id="comment_commenter" name="commenter" 
           value="<%=O.hv("comment.commenter")%>" size="80" />
  </p>
  <p>
    <%=F.label("body")%><br />
    <textarea id="comment_body" name="body" cols="60" rows="10">
           <%=O.hv("comment.body")%></textarea>
  </p>
  <input id="comment_submit" name="commit" type="submit" 
           value="Create" />&nbsp;&nbsp;&nbsp;<input type="reset"/>
<%=F.formForClose("comments")%>


<%=W.labelLink("Edit", R.editResourceRecordPath("posts", post))%>|
<%=W.labelLink("List", R.resourcePath("posts"))%>|
<%=W.labelLink("Paged List", R.resourcePath("posts") + "?paged=true")%>
