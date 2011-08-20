<%@ page import="
	com.scooterframework.web.util.O,
	com.scooterframework.web.util.W"
%>

<h2>Show entry</h2>
<%=W.errorMessage("entry")%>


<p>
  <b>Id:</b>
  <%=O.hv("entry.id")%>
</p>
<p>
  <b>Subject:</b>
  <%=O.hv("entry.subject")%>
</p>
<p>
  <b>Details:</b>
  <%=O.hv("entry.details")%>
</p>
<p>
  <b>Created At:</b>
  <%=O.hv("entry.created_at")%>
</p>
<p>
  <b>Updated At:</b>
  <%=O.hv("entry.updated_at")%>
</p>
