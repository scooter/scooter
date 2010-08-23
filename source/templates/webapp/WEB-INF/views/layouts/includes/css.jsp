<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    String dirName = "static";
    if ("true".equals(System.getProperty("tomcat", "false"))) {
        dirName = dirName + "/" + dirName;
    }
%>
<link href="<%=request.getContextPath()%>/<%=dirName%>/stylesheets/main.css" rel="stylesheet" type="text/css"/>