<%@ page import="
	com.scooterframework.web.util.F,
	com.scooterframework.web.util.O,
	com.scooterframework.web.util.W"
%>

<h2>Edit entry</h2>
<%=W.errorMessage("entry")%>

<%=F.formForOpen("entries", "entry", "data-ajax:true; data-target:#entries_list; data-handler:html")%>

<p>
  <%=F.label("subject")%><br />
  <input type="text" id="entry_subject" name="subject" value="<%=O.hv("entry.subject")%>" size="80" /> 
</p>

<p>
  <%=F.label("details")%><br />
  <textarea id="entry_details" name="details" cols="60" rows="10"><%=O.hv("entry.details")%></textarea>
</p>

  <input id="entry_submit" name="commit" type="submit" value="Update" />&nbsp;&nbsp;&nbsp;<input type="reset"/>
<%=F.formForClose("entries")%>
