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

<h2>Rename file or directory</h2>

<form action="<%=W.getURL("/admin/files/doRename")%>" method="POST">
  <label>Old Name</label>
  <input type="text" name="f" value="<%=W.value("f")%>" readonly="readonly" size="60" /><br/><br/>
  <label>New Name</label>
  <input type="text" name="target" id="target" value="<%=W.value("target")%>" size="60" /><br/><br/>
  <input id="renameButton" name="renameButton" type="submit" value="Rename" />&nbsp;&nbsp;&nbsp;<input type="reset"/>
</form>
