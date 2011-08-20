<%@ page import="
        com.scooterframework.web.util.W"
%>
<p><b>Name of the uploaded file</b>: <%=W.get("file")%></p>

<p>Click <%=W.labelLink("here", "/files/index")%> to go back to upload page.</p>

<p>Click <%=W.labelLink("here", "/static/docs/" + W.get("file"))%> to view the uploaded file.</p>
