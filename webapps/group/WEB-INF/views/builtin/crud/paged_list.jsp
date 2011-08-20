<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page contentType="text/html; charset=UTF-8"%>

<%@ page import="
        java.util.Iterator,
        java.util.List,
        com.scooterframework.admin.Constants,
        com.scooterframework.common.util.WordUtil,
        com.scooterframework.orm.activerecord.ActiveRecord,
        com.scooterframework.orm.misc.Paginator,
        com.scooterframework.web.util.O,
        com.scooterframework.web.util.W"
%>

<%
String recordName = (String)request.getAttribute(Constants.MODEL);

Paginator paginator = (Paginator)request.getAttribute("paged_" + recordName + "_list");
List records = paginator.getRecordList();
%>

<h2>Paged <%=recordName%> List</h2>

<table>
    <tr>
        <td align="left">
            Page <%=paginator.getCurrentPage()%>/<%=paginator.getPageCount()%>. </td>
        <td align="right" class="multilink">
            Showing <%=paginator.getStartIndex()%> - <%=paginator.getEndIndex()%> of <%=paginator.getTotalCount()%>&nbsp;
            <%=W.pageLink("First",    "paged_list", paginator.getQueryStringFirst())%>&nbsp;|&nbsp;
            <%=W.pageLink("Previous", "paged_list", paginator.getQueryStringPrevious())%>&nbsp;|&nbsp;
            <%=W.pageLink("Next",     "paged_list", paginator.getQueryStringNext())%>&nbsp;|&nbsp;
            <%=W.pageLink("Last",     "paged_list", paginator.getQueryStringLast())%>
        </td>
    </tr>
    <tr>
        <td colspan="2">

<table class="sTable">
    <tr>
<%for (Iterator it = O.columnNames(records); it.hasNext();) {%>
        <th><%=WordUtil.titleize((String)it.next())%></th>
<%}%>
        <th></th>
    </tr>

<%for (Iterator it = O.iteratorOf(records); it.hasNext();) { %>
    <tr class="<%=W.cycle("odd, even")%>">
<%
        ActiveRecord record = (ActiveRecord)it.next();
        for (Iterator it2 = O.columnNames(record); it2.hasNext();) {
            String columnName = (String)it2.next();
            String columnValue = O.hp(record, columnName);
            if (columnName.toLowerCase().endsWith("_id")) {
                columnValue = W.simpleForeignKeyRecordShowActionLink(columnName, columnValue);
            }
%>
        <td><%=columnValue%></td>
<%      }%>
        <td class="multilink" nowrap>
            <%=W.labelLinkForRecord("show", "show", record)%>
            <%=W.labelLinkForRecord("edit", "edit", record)%>
            <%=W.labelLinkForRecord("delete", "delete", record)%>
        </td>
    </tr>
<%}%>
</table>

        </td>
    </tr>
    <tr>
        <td align="left">
        Page <%=paginator.getCurrentPage()%>/<%=paginator.getPageCount()%>. </td>
        <td align="right" class="multilink">
        Showing <%=paginator.getStartIndex()%> - <%=paginator.getEndIndex()%> of <%=paginator.getTotalCount()%>&nbsp;
        <%=W.pageLink("First",    "paged_list", paginator.getQueryStringFirst())%>&nbsp;|&nbsp;
        <%=W.pageLink("Previous", "paged_list", paginator.getQueryStringPrevious())%>&nbsp;|&nbsp;
        <%=W.pageLink("Next",     "paged_list", paginator.getQueryStringNext())%>&nbsp;|&nbsp;
        <%=W.pageLink("Last",     "paged_list", paginator.getQueryStringLast())%>
        </td>
    </tr>
</table>

<form action="<%=W.getURL("paged_list")%>" method="POST">
<input type="hidden" name="r" value="page">
<input type="hidden" name="limit" value="<%=request.getParameter("limit")%>">
Go to page <input type="text" id="npage" name="npage" size="2"><input type="submit" value="Go" />
</form>

<p class="multilink">
<%=W.labelLink("Add New ", "add")%>|
<%=W.labelLink("List", "list")%>
</p>

<br/>

<%=W.windowStylePageLinks(paginator, "paged_list", 4, 11)%>
