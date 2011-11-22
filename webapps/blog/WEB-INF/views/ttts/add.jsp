<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page contentType="text/html; charset=UTF-8"%>

<%@ page import="
	com.scooterframework.web.util.F,
	com.scooterframework.web.util.O,
	com.scooterframework.web.util.R,
	com.scooterframework.web.util.W"
%>

<h2>Add ttt</h2>
<%=W.errorMessage("ttt")%>

<%=F.formForOpen("ttts", "ttt")%>

<p>
    <span class="required">*</span><%=F.label("test_id")%><br />
  <input type="text" id="ttt_test_id" name="test_id" value="<%=O.hv("ttt.test_id")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("ishandsome")%><br />
  <input type="text" id="ttt_ishandsome" name="ishandsome" value="<%=O.hv("ttt.ishandsome")%>" size="80" /> 
</p>


  <input id="ttt_submit" name="commit" type="submit" value="Create" />&nbsp;&nbsp;&nbsp;<input type="reset"/>
<%=F.formForClose("ttts")%>

<br />

<%=W.labelLink("List", R.resourcePath("ttts"))%>|
<%=W.labelLink("Paged List", R.resourcePath("ttts") + "?paged=true")%>
