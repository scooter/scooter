<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page contentType="text/html; charset=UTF-8"%>

<%@ page import="com.scooterframework.web.util.W"%>

<div id="siteName">
    <h2>Group</h2>
</div>

<div id="topLinks">
    <p align="right"><%=W.labelLink(W.label("header.label.home"), "/")%> | <%=W.labelLink(W.label("header.label.about"), "/about")%> | <%=W.labelLink(W.label("header.label.contact"), "/contact")%></p>
</div>
