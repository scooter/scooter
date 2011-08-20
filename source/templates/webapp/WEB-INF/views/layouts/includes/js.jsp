<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page contentType="text/html; charset=UTF-8"%>

<%
    String dirName = "static";
    if ("true".equals(System.getProperty("tomcat", "false"))) {
        dirName = dirName + "/" + dirName;
    }
    String path = request.getContextPath() + "/" + dirName;
%>
<script type="text/javascript" src="<%=path%>/javascripts/jquery-1.4.4.min.js"></script>
<script type="text/javascript" src="<%=path%>/javascripts/jquery-ui-1.8.6.custom.min.js"></script>
<script type="text/javascript" src="<%=path%>/javascripts/jquery.snippet.min.js"></script>
<script type="text/javascript" src="<%=path%>/javascripts/sh_properties.min.js"></script>
<script type="text/javascript" src="<%=path%>/javascripts/scooter.js"></script>
<script type="text/javascript" src="<%=path%>/javascripts/app.js"></script>