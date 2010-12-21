<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="
                 com.scooterframework.web.util.W,
                 com.scooterframework.security.LoginHelper"%>

<%
String username = LoginHelper.loginUserId();
%>

<%if (LoginHelper.isAdminLoggedIn()) {%>
<div id="admin">
    <div class="left">
        <%=W.labelLink("Site", "/admin/site")%> | 
        <%=W.labelLink("Databases", "/admin/databases")%> | 
        <%=W.labelLink("Files", "/admin/files/list")%> | 
        <%=W.labelLink("Routes", "/admin/routes")%>
    </div>
    <div class="right">
        Welcome <%=W.labelLink(username, "/admin/signon/main")%>! <%=W.labelLink("Logout", "/admin/signon/logout")%>
    </div>
    <div class="clearer"></div>
</div>
<%}%>