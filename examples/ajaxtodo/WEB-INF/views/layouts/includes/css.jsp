<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    String dirName = "static";
    if ("true".equals(System.getProperty("tomcat", "false"))) {
        dirName = dirName + "/" + dirName;
    }
    String path = request.getContextPath() + "/" + dirName;
%>

<link rel="shortcut icon" type="image/x-icon" href="<%=path%>/images/favicon.ico" />
<link rel="icon" type="image/x-icon" href="<%=path%>/images/favicon.ico" />
<link rel="apple-touch-icon" href="<%=path%>/images/favicon.ico" />

<link rel="stylesheet" type="text/css" href="<%=path%>/stylesheets/jquery-ui-1.8.6.custom.css" />
<link rel="stylesheet" type="text/css" href="<%=path%>/stylesheets/jquery.snippet.min.css" />
<link rel="stylesheet" type="text/css" href="<%=path%>/stylesheets/main.css" />
