<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.scooterframework.web.util.W,
                 com.scooterframework.security.LoginHelper"%>

<div id="siteName">
    <h2>Customerservice</h2>
</div>

<div id="topLinks">
    <p align="right"><%=W.labelLink("Home", "/")%> | <%=W.labelLink("About", "/about")%> | <%=W.labelLink("Contact", "/contact")%> | 
	<%if (LoginHelper.isLoggedIn()) {%>
	          <a href="<%=request.getContextPath()%>/signon/logout">Sign Out</a>
	<%} else {%>
	          <a href="<%=request.getContextPath()%>/signon/login">Sign In</a>
	<%}%>
    </p>
</div>
