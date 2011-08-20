<%@ page import="
        java.util.Iterator,
        com.scooterframework.orm.sqldataexpress.object.RESTified,
        com.scooterframework.web.util.O,
        com.scooterframework.web.util.T,
        com.scooterframework.web.util.W"
%>

<h2><%=W.get("username")%> follows <%=T.pluralize(O.count("followings"), "person")%></h2>

<%
for (Iterator it = O.iteratorOf("followings"); it.hasNext();) {
    RESTified account = (RESTified)it.next();
%>
    <b><%=O.hp(account, "username")%></b>
    <br />
<%}%>