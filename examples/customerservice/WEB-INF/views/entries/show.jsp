<%@ page import="
	com.scooterframework.orm.sqldataexpress.object.RESTified,
	com.scooterframework.web.util.O,
	com.scooterframework.web.util.R,
	com.scooterframework.web.util.W"
%>

<%
RESTified entry = (RESTified)W.request("entry");
%>

<h2>Show entry</h2>
<%=W.errorMessage("entry")%>


<p>
  <b>Id:</b>
  <%=O.hv("entry.id")%>
</p>
<p>
  <b>Name:</b>
  <%=O.hv("entry.name")%>
</p>
<p>
  <b>Content:</b>
  <%=O.hv("entry.content")%>
</p>
<p>
  <b>Created At:</b>
  <%=O.hv("entry.created_at")%>
</p>

<br />

<%=W.labelLink("Edit", R.editResourceRecordPath("entries", entry))%>|
<%=W.labelLink("List", R.resourcePath("entries"))%>|
<%=W.labelLink("Paged List", R.resourcePath("entries") + "?paged=true")%>
