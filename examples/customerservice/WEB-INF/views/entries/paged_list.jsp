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
Paginator paginator = (Paginator)request.getAttribute("entry_page");
List records = paginator.getRecordList();
%>

<h2>entry List</h2>

<table>
    <tr>
        <td align="left">
            Page <%=paginator.getCurrentPage()%>/<%=paginator.getPageCount()%>. </td>
        <td align="right" class="multilink">
            Showing <%=paginator.getStartIndex()%> - <%=paginator.getEndIndex()%> of <%=paginator.getTotalCount()%>&nbsp;
            <%=W.pageLink("First",    R.resourcePath("entries"), paginator.getQueryStringFirst())%>&nbsp;|&nbsp;
            <%=W.pageLink("Previous", R.resourcePath("entries"), paginator.getQueryStringPrevious())%>&nbsp;|&nbsp;
            <%=W.pageLink("Next",     R.resourcePath("entries"), paginator.getQueryStringNext())%>&nbsp;|&nbsp;
            <%=W.pageLink("Last",     R.resourcePath("entries"), paginator.getQueryStringLast())%>
        </td>
    </tr>
    <tr>
        <td colspan="2">

<table class="sTable">
    <tr>
        <th>Id</th>
        <th>Name</th>
        <th>Content</th>
        <th>Created At</th>
        <th>&nbsp;</th>
    </tr>

<%for (Iterator it = O.iteratorOf(records); it.hasNext();) {
    RESTified entry = (RESTified)it.next();
%>
    <tr class="<%=W.cycle("odd, even")%>">
        <td><%=O.hp(entry, "id")%></td>
        <td><%=O.hp(entry, "name")%></td>
        <td><%=O.hp(entry, "content")%></td>
        <td><%=O.hp(entry, "created_at")%></td>
        <td class="multilink" nowrap>
            <%=W.labelLink("show", R.resourceRecordPath("entries", entry))%>
            <%=W.labelLink("edit", R.editResourceRecordPath("entries", entry))%>
            <%=W.labelLink("delete", R.resourceRecordPath("entries", entry), "confirm:'Are you sure?'; method:delete")%>
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
            <%=W.pageLink("First",    R.resourcePath("entries"), paginator.getQueryStringFirst())%>&nbsp;|&nbsp;
            <%=W.pageLink("Previous", R.resourcePath("entries"), paginator.getQueryStringPrevious())%>&nbsp;|&nbsp;
            <%=W.pageLink("Next",     R.resourcePath("entries"), paginator.getQueryStringNext())%>&nbsp;|&nbsp;
            <%=W.pageLink("Last",     R.resourcePath("entries"), paginator.getQueryStringLast())%>
        </td>
    </tr>
</table>

<br />
<%=W.windowStylePageLinks(paginator, R.resourcePath("entries"), 4, 11)%>

<form action="<%=W.getURL(R.resourcePath("entries"))%>" method="GET">
<input type="hidden" name="r" value="page">
<input type="hidden" name="limit" value="<%=W.get("limit", "10")%>">
<input type="hidden" name="paged" value="true">
Go to page <input type="text" id="npage" name="npage" size="2"><input type="submit" value="Go" />
</form>

<%=W.labelLink("Add entry", R.addResourcePath("entries"))%>|
<%=W.labelLink("List", R.resourcePath("entries"))%>
