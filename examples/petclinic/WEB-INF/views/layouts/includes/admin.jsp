<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page contentType="text/html; charset=UTF-8"%>

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
        <%=W.labelLink("Routes", "/admin/routes")%> | 
        <%=W.labelLink("Databases", "/admin/databases")%> | 
        <%=W.labelLink("SQL Window", "/admin/sqlwindow")%>
    </div>
    <div class="right">
        Welcome <%=W.labelLink(username, "/admin/signon/main")%>! <%=W.labelLink("Logout", "/admin/signon/logout")%>
    </div>
    <div class="left, clearer">
        <%=W.labelLink("All Files", "/admin/files/list")%> | 
        <%=W.labelLink("Config", "/admin/files/list?f=/WEB-INF/config")%> | 
        <%=W.labelLink("Controllers", "/admin/files/list?f=/WEB-INF/src/petclinic/controllers")%> | 
        <%=W.labelLink("Models", "/admin/files/list?f=/WEB-INF/src/petclinic/models")%> | 
        <%=W.labelLink("Views", "/admin/files/list?f=/WEB-INF/views")%> 
    </div>
    <div class="clearer"></div>
</div>
<%}%>
