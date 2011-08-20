<%@ page import="
        java.util.Date,
        java.util.Iterator,
        com.scooterframework.orm.sqldataexpress.object.RESTified,
        com.scooterframework.web.util.D,
        com.scooterframework.web.util.O,
        com.scooterframework.web.util.R,
        com.scooterframework.web.util.T,
        com.scooterframework.web.util.W"
%>

<h2><%=W.get("username")%></h2>

<%=W.submitButtonLink("Following This Person", "/accounts/addFollowing?username=" + W.get("username"))%>

<br />

<%
for (Iterator it = O.iteratorOf("user_tweets"); it.hasNext();) {
    RESTified tweet = (RESTified)it.next();
%>
    <%=O.hp(tweet, "message")%><br />
    <%=D.message((Date)O.getProperty(tweet, "created_at"))%><br />
    <br />
<%}%>