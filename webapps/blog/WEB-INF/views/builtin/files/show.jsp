<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page contentType="text/html; charset=UTF-8"%>

<%@ page import="
        java.io.File,
        com.scooterframework.builtin.BuiltinHelper,
        com.scooterframework.builtin.FileInfo,
        com.scooterframework.web.util.W"
%>

<%
File requestFile = (File)W.get("requestFile");
String classCode = (String)W.get("classCode");
%>

<div id="locator">
  <%=BuiltinHelper.getLocatorLinks(requestFile)%>
</div>

<h2>Show file</h2>

<% if (requestFile != null) { %>
<h3><%=BuiltinHelper.getFileName(requestFile)%> (<%=W.labelLink("edit", new FileInfo(requestFile).getActionURI("edit"))%>)</h3>
<%} %>

<% if (requestFile != null) { %>
<pre class="<%=classCode%>">
<%=W.value("fileContent")%>
</pre>
<%} else {%>
<p>There is no file to show.</p>
<%}%>
