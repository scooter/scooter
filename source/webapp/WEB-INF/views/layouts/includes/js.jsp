<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    String dirName = "static";
    if ("true".equals(System.getProperty("tomcat", "false"))) {
        dirName = dirName + "/" + dirName;
    }
%>
<script type="text/javascript" src="<%=request.getContextPath()%>/<%=dirName%>/javascripts/ajax.js"></script>