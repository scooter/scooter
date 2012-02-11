<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page contentType="text/html; charset=UTF-8"%>

<%@ page import="
        java.util.List,
        com.scooterframework.admin.Constants,
        com.scooterframework.web.util.R,
        com.scooterframework.web.util.W"
%>

<%
String resource = (String)request.getAttribute(Constants.RESOURCE);
List databases = (List)request.getAttribute("databases");
String sql = W.value("sql");
String pagingURL = R.resourcePath(resource) + "/execute";
%>

<div id="locator">
    <p><%=W.labelLink("Home", "/")%> > SQL Window</p>
</div>

<form name="sql_window_form" action="<%=W.getURL(pagingURL)%>" method="POST" data-ajax=true data-method=POST data-target=#result_screen data-handler=html>
<input type="hidden" name="paged" value="true" />
<div>
  database: 
  <%=W.displayHtmlSelect("database", databases, "selectedId:" + W.value("database"))%>
  &nbsp;&nbsp;&nbsp;limit: 
  <%=W.displayHtmlSelect("limit", "10,20,30,50,100,200,500,1000,5000,10000,100000", "selectedId:" + W.value("limit"))%>
  &nbsp;&nbsp;&nbsp;
  <input name="submit" type="submit" value="Run" />
</div>
<div>
<textarea name="sql" id="sql" rows="5" cols="90" style="margin: 10px 0px;"><%=W.value("sql")%></textarea>
</div>
</form>

<h2>Result:</h2>

<div id="result_screen" />