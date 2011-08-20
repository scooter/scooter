<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
  "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page contentType="text/html; charset=UTF-8"%>

<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>

<%@ page import="com.scooterframework.web.util.W"%>

<html>
  <head>
    <meta http-equiv="Content-type" content="text/html; charset=utf-8" />
    <title><%=W.getContextName()%> - <decorator:title default="Welcome!" /></title>
    <decorator:head />
    <jsp:include page="/WEB-INF/views/layouts/includes/css.jsp"/>
    <jsp:include page="/WEB-INF/views/layouts/includes/js.jsp"/>
  </head>
  <body>
    <div id="container">
      <div id="header">
        <jsp:include page="/WEB-INF/views/layouts/includes/header.jsp"/>
      </div>
      <div id="content">
        <div id="main">
          <jsp:include page="/WEB-INF/views/layouts/includes/status.jsp"/>
          <decorator:body />
        </div>
      </div>
      <div id="footer">
        <jsp:include page="/WEB-INF/views/layouts/includes/footer.jsp"/>
      </div>
    </div>
  </body>
</html>