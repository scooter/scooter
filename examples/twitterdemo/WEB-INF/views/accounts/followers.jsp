<%@ page import="
        java.util.Iterator,
        com.scooterframework.orm.sqldataexpress.object.RESTified,
        com.scooterframework.web.util.O,
        com.scooterframework.web.util.T,
        com.scooterframework.web.util.W"
%>

<h2><%=W.get("username")%>'s <%=T.pluralize(O.count("followers"), "follower")%></h2>

<%
for (Iterator it = O.iteratorOf("followers"); it.hasNext();) {
    RESTified account = (RESTified)it.next();
%>
    <b><%=O.hp(account, "username")%></b>
    <br />
<%}%>