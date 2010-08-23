<%@ page import="
        com.scooterframework.web.util.W"
%>
<p>Click <%=W.labelLink("here", "/files/index")%> to go back to upload page.</p>

<p>Click <%=W.labelLink("here", "/static/docs/" + W.get("file"))%> view the uploaded file.</p>
