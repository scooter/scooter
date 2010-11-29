<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.scooterframework.web.util.W"%>

<div id="siteName">
    <h2>{app_name_title}</h2>
</div>

<div id="topLinks">
    <p align="right"><%=W.labelLink("Home", "/")%> | <%=W.labelLink("About", "/about")%> | <%=W.labelLink("Contact", "/contact")%></p>
</div>