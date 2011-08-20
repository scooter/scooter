<%@ page import="
	com.scooterframework.orm.sqldataexpress.object.RESTified,
	com.scooterframework.web.util.F,
	com.scooterframework.web.util.O,
	com.scooterframework.web.util.R,
	com.scooterframework.web.util.W"
%>

<h2>Edit entry</h2>
<%=W.errorMessage("entry")%>

<%=F.formForOpen("entries", "entry")%>

<p>
  <%=F.label("name")%><br />
  <input type="text" id="entry_name" name="name" value="<%=O.hv("entry.name")%>" size="80" /> 
</p>

<p>
  <%=F.label("content")%><br />
  <textarea id="entry_content" name="content" cols="60" rows="10"><%=O.hv("entry.content")%></textarea>
</p>

  <input id="entry_submit" name="commit" type="submit" value="Update" />&nbsp;&nbsp;&nbsp;<input type="reset"/>
<%=F.formForClose("entries")%>

<br />

<%=W.labelLink("Show", R.resourceRecordPath("entries", (RESTified)W.request("entry")))%>|
<%=W.labelLink("List", R.resourcePath("entries"))%>|
<%=W.labelLink("Paged List", R.resourcePath("entries") + "?paged=true")%>
