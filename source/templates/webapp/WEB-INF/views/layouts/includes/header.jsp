<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.scooterframework.web.util.W"%>

<div id="siteName">
    <h2>{app_name}</h2>
</div>

<%
    StringBuffer printUrl = new StringBuffer();
    printUrl.append("?printable=true");
    if (request.getQueryString()!=null) {
        printUrl.append('&');
        printUrl.append(request.getQueryString());
    }
%>
<div id="topLinks">
    <p align="right"><%=W.labelLink("Home", "/")%> | <%=W.labelLink("Routes", "/routes")%> | <a href="<%= printUrl %>">printable version</a></p>
</div>