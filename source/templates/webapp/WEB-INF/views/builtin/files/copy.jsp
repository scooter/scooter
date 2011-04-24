<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page contentType="text/html; charset=UTF-8"%>

<%@ page import="
        java.io.File,
        com.scooterframework.builtin.BuiltinHelper,
        com.scooterframework.web.util.W"
%>

<%
File requestFile = (File)W.get("requestFile");
%>

<div id="locator">
  <%=BuiltinHelper.getLocatorLinks(requestFile)%>
</div>

<h2>Copy file or directory</h2>

<form action="<%=W.getURL("/admin/files/doCopy")%>" method="POST">
  <label>Source</label>
  <input type="text" name="f" value="<%=W.value("f")%>" readonly="readonly" size="60" /><br/><br/>
  <label>Target</label>
  <input type="text" name="target" id="target" value="<%=W.value("target")%>" size="60" /><br/><br/>
  <input id="copyButton" name="copyButton" type="submit" value="Copy" />&nbsp;&nbsp;&nbsp;<input type="reset"/>
</form>