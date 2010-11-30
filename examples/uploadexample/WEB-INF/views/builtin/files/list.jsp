<%@ page import="
        java.io.File,
        java.util.List,
        com.scooterframework.builtin.BuiltinHelper,
        com.scooterframework.builtin.FileInfo,
        com.scooterframework.web.util.T,
        com.scooterframework.web.util.W"
%>

<%
File requestFile = (File)request.getAttribute("requestFile");
List<FileInfo> files = (List)request.getAttribute("files");
%>

<div id="locator">
  <%=BuiltinHelper.getLocatorLinks(requestFile)%>
</div>

<table class="sTable">
    <tr>
        <th>Name</th>
        <th>Type</th>
        <th>Size</th>
        <th>Date Modified</th>
        <th>Actions</th>
    </tr>

<%for (FileInfo fi : files) {%>
    <tr class="<%=W.cycle("odd, even")%>">
    <%if (fi.isDirectory()) { %>
        <td><img title="folder" src="../../static/images/folder.gif" alt="folder "/><%=W.labelLink(fi.getName(), fi.getActionURI("list"))%></td>
    <%} else { %>
        <td><img title="file" src="../../static/images/file.gif" alt="file "/><%=W.labelLink(fi.getName(), fi.getActionURI("show"))%></td>
    <%} %>
        <td><%=fi.getType()%></td>
        <td align="right"><%=fi.getSizeDisplay()%></td>
        <td><%=T.textOfDate(fi.getLastModified())%></td>
        <td class="multilink" nowrap>
            <%=W.labelLink((fi.isDirectory())?"add":"edit", (fi.isDirectory())?fi.getActionURI("add"):fi.getActionURI("edit"))%>
            <%=W.labelLink("delete", fi.getActionURI("delete"), "confirm:'Are you sure?'")%>
            <%=W.labelLink("copy", fi.getActionURI("copy"))%>
            <%=W.labelLink("rename", fi.getActionURI("rename"))%>
            <%=W.labelLink((fi.isDirectory())?"upload":"replace", (fi.isDirectory())?fi.getActionURI("upload"):fi.getActionURI("replace"))%>
        </td>
    </tr>
<%}%>
</table>
