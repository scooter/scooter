<%@ page import="
        java.util.Date,
        java.util.Iterator,
        com.scooterframework.orm.sqldataexpress.object.RESTified,
        com.scooterframework.web.util.D,
        com.scooterframework.web.util.F,
        com.scooterframework.web.util.O,
        com.scooterframework.web.util.W"
%>

<h2>What are you doing?</h2>
<%=W.errorMessage("tweet")%>

<%=F.formForOpen("tweets", "tweet")%>
  <p>
    <%=F.label("message")%><br />
    <textarea id="tweet_message" name="message" cols="60" rows="3"><%=O.hv("tweet.message")%></textarea>
  </p>
  <input id="tweet_submit" name="commit" type="submit" value="Update" />
<%=F.formForClose("tweets")%>

<br />

<%
for (Iterator it = O.iteratorOf("followings_tweets"); it.hasNext();) {
    RESTified tweet = (RESTified)it.next();
%>
    <b><%=W.labelLink(O.hp(tweet, "account.username"), "/" + O.hp(tweet, "account.username"))%></b> <%=O.hp(tweet, "message")%><br />
    <%=D.message((Date)O.getProperty(tweet, "created_at"))%><br />
    <br />
<%}%>