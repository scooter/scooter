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

<h2>Upload file</h2>

<form action="<%=W.getURL("/admin/files/doUpload")%>" method="POST" enctype="multipart/form-data">
  <input type="hidden" name="f" value="<%=W.value("f")%>" />
  <input type="file" name="theFile" id="theFile" value="<%=W.value("theFile")%>" size="60" /><br/><br/>
  <input id="uploadButton" name="uploadButton" type="submit" value="Upload" />&nbsp;&nbsp;&nbsp;<input type="reset"/>
</form>
