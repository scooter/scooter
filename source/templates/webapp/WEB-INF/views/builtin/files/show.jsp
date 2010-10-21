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

<h2>Show file</h2>

<% if (requestFile != null) { %>
<h3><%=BuiltinHelper.getFileName(requestFile)%></h3>
<%} %>

<% if (requestFile != null) { %>
<textarea rows="40" cols="80" readonly="readonly">
<%=W.value("fileContent")%>
</textarea>
<%} else {%>
<p>There is no file to show.</p>
<%}%>