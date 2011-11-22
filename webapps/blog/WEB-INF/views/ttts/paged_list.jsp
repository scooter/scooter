<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page contentType="text/html; charset=UTF-8"%>

<%@ page import="
	java.util.Iterator,
	java.util.List,
	com.scooterframework.orm.misc.Paginator,
	com.scooterframework.orm.sqldataexpress.object.RESTified,
	com.scooterframework.web.util.O,
	com.scooterframework.web.util.R,
	com.scooterframework.web.util.W"
%>

<%
Paginator paginator = (Paginator)request.getAttribute("ttt_page");
List records = paginator.getRecordList();
%>

<h2>ttt List</h2>

<table>
    <tr>
        <td align="left">
            Page <%=paginator.getCurrentPage()%>/<%=paginator.getPageCount()%>. </td>
        <td align="right" class="multilink">
            Showing <%=paginator.getStartIndex()%> - <%=paginator.getEndIndex()%> of <%=paginator.getTotalCount()%>&nbsp;
            <%=W.pageLink("First",    R.resourcePath("ttts"), paginator.getQueryStringFirst())%>&nbsp;|&nbsp;
            <%=W.pageLink("Previous", R.resourcePath("ttts"), paginator.getQueryStringPrevious())%>&nbsp;|&nbsp;
            <%=W.pageLink("Next",     R.resourcePath("ttts"), paginator.getQueryStringNext())%>&nbsp;|&nbsp;
            <%=W.pageLink("Last",     R.resourcePath("ttts"), paginator.getQueryStringLast())%>
        </td>
    </tr>
    <tr>
        <td colspan="2">

<table class="sTable">
    <tr>
        <th>Test</th>
        <th>Ishandsome</th>
        <th>&nbsp;</th>
    </tr>

<%for (Iterator it = O.iteratorOf(records); it.hasNext();) {
    RESTified ttt = (RESTified)it.next();
%>
    <tr class="<%=W.cycle("odd, even")%>">
        <td><%=O.hp(ttt, "test_id")%></td>
        <td><%=O.hp(ttt, "ishandsome")%></td>
        <td class="multilink" nowrap>
            <%=W.labelLink("show", R.resourceRecordPath("ttts", ttt))%>
            <%=W.labelLink("edit", R.editResourceRecordPath("ttts", ttt))%>
            <%=W.labelLink("delete", R.resourceRecordPath("ttts", ttt), "confirm:'Are you sure?'; method:delete")%>
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
            <%=W.pageLink("First",    R.resourcePath("ttts"), paginator.getQueryStringFirst())%>&nbsp;|&nbsp;
            <%=W.pageLink("Previous", R.resourcePath("ttts"), paginator.getQueryStringPrevious())%>&nbsp;|&nbsp;
            <%=W.pageLink("Next",     R.resourcePath("ttts"), paginator.getQueryStringNext())%>&nbsp;|&nbsp;
            <%=W.pageLink("Last",     R.resourcePath("ttts"), paginator.getQueryStringLast())%>
        </td>
    </tr>
</table>

<br />
<%=W.windowStylePageLinks(paginator, R.resourcePath("ttts"), 4, 11)%>

<form action="<%=W.getURL(R.resourcePath("ttts"))%>" method="GET">
<input type="hidden" name="r" value="page">
<input type="hidden" name="limit" value="<%=W.get("limit", "10")%>">
<input type="hidden" name="paged" value="true">
Go to page <input type="text" id="npage" name="npage" size="2"><input type="submit" value="Go" />
</form>

<%=W.labelLink("Add ttt", R.addResourcePath("ttts"))%>|
<%=W.labelLink("List", R.resourcePath("ttts"))%>
