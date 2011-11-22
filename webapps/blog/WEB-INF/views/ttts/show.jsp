<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page contentType="text/html; charset=UTF-8"%>

<%@ page import="
	com.scooterframework.orm.sqldataexpress.object.RESTified,
	com.scooterframework.web.util.O,
	com.scooterframework.web.util.R,
	com.scooterframework.web.util.W"
%>

<%
RESTified ttt = (RESTified)W.request("ttt");
%>

<h2>Show ttt</h2>
<%=W.errorMessage("ttt")%>


<p>
  <b>Test:</b>
  <%=O.hv("ttt.test_id")%>
</p>
<p>
  <b>Ishandsome:</b>
  <%=O.hv("ttt.ishandsome")%>
</p>

<br />

<%=W.labelLink("Edit", R.editResourceRecordPath("ttts", ttt))%>|
<%=W.labelLink("List", R.resourcePath("ttts"))%>|
<%=W.labelLink("Paged List", R.resourcePath("ttts") + "?paged=true")%>
