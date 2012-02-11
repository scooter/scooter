<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page contentType="text/html; charset=UTF-8"%>

<%@ page import="
        java.util.Iterator,
        java.util.List,
        com.scooterframework.admin.Constants,
        com.scooterframework.common.util.WordUtil,
	com.scooterframework.orm.misc.Paginator,
	com.scooterframework.orm.sqldataexpress.object.RESTified,
        com.scooterframework.web.util.O,
        com.scooterframework.web.util.R,
        com.scooterframework.web.util.W"
%>

<%
String sql = W.value("sql");
String resource = (String)request.getAttribute(Constants.RESOURCE);
String pagingURL = R.resourcePath(resource) + "/execute";

Paginator paginator = (Paginator)request.getAttribute("paged_records");
List records = null;
if (paginator != null) {
    records = paginator.getRecordList();
}

boolean hasMoreThanOneRecord = false;
if (records != null) {
    if (records.size() > 1) hasMoreThanOneRecord = true;
%>

<% if (hasMoreThanOneRecord) {%>
<table>
    <tr>
        <td align="left">
        Page <%=paginator.getCurrentPage()%>/<%=paginator.getPageCount()%>. </td>
        <td align="right" class="multilink">
            Showing <%=paginator.getStartIndex()%> - <%=paginator.getEndIndex()%> of <%=paginator.getTotalCount()%>&nbsp;
            <%=W.pageLink("First",    W.getURL(pagingURL), paginator.getQueryStringFirst(),    "data-ajax:true; data-method:POST; data-target:#result_screen; data-handler:html")%>&nbsp;|&nbsp;
            <%=W.pageLink("Previous", W.getURL(pagingURL), paginator.getQueryStringPrevious(), "data-ajax:true; data-method:POST; data-target:#result_screen; data-handler:html")%>&nbsp;|&nbsp;
            <%=W.pageLink("Next",     W.getURL(pagingURL), paginator.getQueryStringNext(),     "data-ajax:true; data-method:POST; data-target:#result_screen; data-handler:html")%>&nbsp;|&nbsp;
            <%=W.pageLink("Last",     W.getURL(pagingURL), paginator.getQueryStringLast(),     "data-ajax:true; data-method:POST; data-target:#result_screen; data-handler:html")%>
        </td>
    </tr>
    <tr>
        <td colspan="2">
<%}%>
<table class="sTable">
    <tr>
<%for (Iterator it = O.columnNames(records); it.hasNext();) {%>
        <th><%=WordUtil.titleize((String)it.next())%></th>
<%}%>
    </tr>

<%for (Iterator it = O.iteratorOf(records); it.hasNext();) { %>
    <tr class="<%=W.cycle("odd, even")%>">
<%
        RESTified record = (RESTified)it.next();
        for (Iterator it2 = O.columnNames(record); it2.hasNext();) {
            String columnName = (String)it2.next();
            String columnValue = O.hp(record, columnName);
%>
        <td><%=columnValue%></td>
<%      }%>
    </tr>
<%}%>
</table>
<% if (hasMoreThanOneRecord) {%>
        </td>
    </tr>
    <tr>
        <td align="left">
        Page <%=paginator.getCurrentPage()%>/<%=paginator.getPageCount()%>. </td>
        <td align="right" class="multilink">
            Showing <%=paginator.getStartIndex()%> - <%=paginator.getEndIndex()%> of <%=paginator.getTotalCount()%>&nbsp;
            <%=W.pageLink("First",    W.getURL(pagingURL), paginator.getQueryStringFirst(),    "data-ajax:true; data-method:POST; data-target:#result_screen; data-handler:html")%>&nbsp;|&nbsp;
            <%=W.pageLink("Previous", W.getURL(pagingURL), paginator.getQueryStringPrevious(), "data-ajax:true; data-method:POST; data-target:#result_screen; data-handler:html")%>&nbsp;|&nbsp;
            <%=W.pageLink("Next",     W.getURL(pagingURL), paginator.getQueryStringNext(),     "data-ajax:true; data-method:POST; data-target:#result_screen; data-handler:html")%>&nbsp;|&nbsp;
            <%=W.pageLink("Last",     W.getURL(pagingURL), paginator.getQueryStringLast(),     "data-ajax:true; data-method:POST; data-target:#result_screen; data-handler:html")%>
        </td>
    </tr>
</table>

<br />
<%=W.windowStylePageLinks(paginator, W.getURL(pagingURL), 4, 11, "data-ajax:true; data-method:POST; data-target:#result_screen; data-handler:html")%>

<form action="<%=W.getURL(pagingURL)%>" method="POST"
data-ajax="true" data-method="GET" data-target="#result_screen" data-handler"html">
<input type="hidden" name="r" value="page">
<input type="hidden" name="limit" value="<%=W.get("limit", "10")%>">
<input type="hidden" name="paged" value="true">
Go to page <input type="text" id="npage" name="npage" size="2"><input type="submit" value="Go" />
</form>

<%}%>

<br/>

<%} else if (!"".equals(sql)) {%>
There is no record.
<%}%>