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

<h2>Add file</h2>

<form action="<%=W.getURL("/admin/files/create")%>" method="POST">
  <input type="hidden" name="f" value="<%=W.value("f")%>" />
  <label>Name</label>
  <input type="text" name="name" id="name" value="<%=W.value("name")%>" /><br/><br/>
  <textarea name="fileContent" rows="40" cols="80"><%=W.value("fileContent")%></textarea><br/><br/>
  <input id="addButton" name="addButton" type="submit" value="Add" />&nbsp;&nbsp;&nbsp;<input type="reset"/>
</form>
