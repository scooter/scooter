<%@ page import="
        java.io.File,
        com.scooterframework.builtin.BuiltinHelper,
        com.scooterframework.builtin.FileInfo,
        com.scooterframework.web.util.W"
%>

<%
File requestFile = (File)W.get("requestFile");
%>

<div id="locator">
  <%=BuiltinHelper.getLocatorLinks(requestFile)%>
</div>

<h2>Edit file</h2>

<% if (requestFile != null) { %>
<h3><%=BuiltinHelper.getFileName(requestFile)%> (<%=W.labelLink("show", new FileInfo(requestFile).getActionURI("show"))%>)</h3>
<%} %>

<% if (requestFile != null) { %>
<form action="<%=W.getURL("/admin/files/update")%>" method="POST">
  <input type="hidden" name="f" value="<%=W.value("f")%>" />
  <textarea name="fileContent" rows="40" cols="80"><%=W.value("fileContent")%></textarea><br/><br/>
  <input id="updateButton" name="updateButton" type="submit" value="Update" />&nbsp;&nbsp;&nbsp;<input type="reset"/>
</form>
<%} else {%>
<p>There is no file to edit.</p>
<%}%>
