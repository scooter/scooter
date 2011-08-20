<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page contentType="text/html; charset=UTF-8"%>

<%@ page import="
        java.io.File,
	java.util.List,
        com.scooterframework.autoloader.CompileErrors,
        com.scooterframework.autoloader.ErrorItem,
        com.scooterframework.autoloader.JavaCompiler,
        com.scooterframework.builtin.FileInfo,
        com.scooterframework.common.util.Util,
        com.scooterframework.web.util.W"
%>

<%
CompileErrors cErrors = JavaCompiler.getCompileErrors();
%>

<div id="compileErrors">

<h2>Total compile errors: <%=cErrors.getErrorCount()%></h2>

<%
for (ErrorItem error : cErrors.getAllErrors()) {
    String filePath = error.getFilePath();
    List<String> codeLines = error.getCodeAroundError();
%>

<table>
  <tr class="compileErrorFile">
    <td>
      <b><tt><%=W.labelLink(FileInfo.getRelativePath(new File(filePath)), new FileInfo(new File(filePath)).getActionURI("edit"))%></tt></b>
    </td>
  </tr>
  <tr class="compileErrorMessage">
    <td>
      Around line <%=error.getErrorLineNumber()%>: <%=W.h(error.getErrorMessage())%>
    </td>
  </tr>
</table>

<pre class="code">
<table>
<%
    for (int i = 0; i < codeLines.size(); i++) {
        int lineNumber = i + error.getBeginLineNumber();
        String codeLine = codeLines.get(i);
%>
  <tr <%=Util.ifTrue((lineNumber == error.getErrorLineNumber()), "style=\"color:red\"", "")%>>
    <td align="right"><%=lineNumber%></td>
    <td><%=codeLine%></td>
  </tr>
<%=Util.ifTrue((lineNumber == error.getErrorLineNumber()), "<tr style=\"color:red\"><td></td><td>" + error.getErrorIndicator() + "</td></tr>", "")%>
<%
    }
%>
</table>
</pre>

<br/>

<%}%>

</div>