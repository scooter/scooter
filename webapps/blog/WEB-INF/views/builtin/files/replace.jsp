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

<h2>Replace file</h2>

<form action="<%=W.getURL("/admin/files/doReplace")%>" method="POST" enctype="multipart/form-data">
  <label>Replace</label><br/><br/>
  <input type="text" name="f" value="<%=W.value("f")%>" /><br/><br/>
  <label>by</label><br/><br/>
  <input type="file" name="theFile" id="theFile" value="<%=W.value("theFile")%>" size="60" /><br/><br/>
  <input id="replaceButton" name="replaceButton" type="submit" value="Replace" />&nbsp;&nbsp;&nbsp;<input type="reset"/>
</form>
