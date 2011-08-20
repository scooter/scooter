<%@ page import="
	java.util.Iterator,
	java.util.List,
	com.scooterframework.orm.misc.Paginator,
	com.scooterframework.orm.sqldataexpress.object.RESTified,
	com.scooterframework.web.util.O,
	com.scooterframework.web.util.R,
	com.scooterframework.web.util.W"
%>

<div id="posts_list">

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
            <%=W.pageLink("First",    R.resourcePath("entries"), paginator.getQueryStringFirst(),    "data-ajax:true; data-method:GET; data-target:#entries_list; data-handler:html")%>&nbsp;|&nbsp;
            <%=W.pageLink("Previous", R.resourcePath("entries"), paginator.getQueryStringPrevious(), "data-ajax:true; data-method:GET; data-target:#entries_list; data-handler:html")%>&nbsp;|&nbsp;
            <%=W.pageLink("Next",     R.resourcePath("entries"), paginator.getQueryStringNext(),     "data-ajax:true; data-method:GET; data-target:#entries_list; data-handler:html")%>&nbsp;|&nbsp;
            <%=W.pageLink("Last",     R.resourcePath("entries"), paginator.getQueryStringLast(),     "data-ajax:true; data-method:GET; data-target:#entries_list; data-handler:html")%>
        </td>
    </tr>
    <tr>
        <td colspan="2">

<table class="sTable">
    <tr>
        <th>Id</th>
        <th>Subject</th>
        <th>Details</th>
        <th>Created At</th>
        <th>Updated At</th>
        <th>&nbsp;</th>
    </tr>

<%for (Iterator it = O.iteratorOf(records); it.hasNext();) {
    RESTified entry = (RESTified)it.next();
%>
    <tr class="<%=W.cycle("odd, even")%>">
        <td><%=O.hp(entry, "id")%></td>
        <td><%=O.hp(entry, "subject")%></td>
        <td><%=O.hp(entry, "details")%></td>
        <td><%=O.hp(entry, "created_at")%></td>
        <td><%=O.hp(entry, "updated_at")%></td>
        <td class="multilink" nowrap>
            <%=W.labelLink("show",   R.resourceRecordPath("entries", entry),     "data-ajax:true; data-method:GET; data-target:#entry; data-handler:html")%>
            <%=W.labelLink("edit",   R.editResourceRecordPath("entries", entry), "data-ajax:true; data-method:GET; data-target:#entry; data-handler:html")%>
            <%=W.labelLink("delete", R.resourceRecordPath("entries", entry),     "data-confirm:Are you sure?; data-ajax:true; data-method:delete; data-target:#entries_list; data-handler:html")%>
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
            <%=W.pageLink("First",    R.resourcePath("entries"), paginator.getQueryStringFirst(),    "data-ajax:true; data-method:GET; data-target:#entries_list; data-handler:html")%>&nbsp;|&nbsp;
            <%=W.pageLink("Previous", R.resourcePath("entries"), paginator.getQueryStringPrevious(), "data-ajax:true; data-method:GET; data-target:#entries_list; data-handler:html")%>&nbsp;|&nbsp;
            <%=W.pageLink("Next",     R.resourcePath("entries"), paginator.getQueryStringNext(),     "data-ajax:true; data-method:GET; data-target:#entries_list; data-handler:html")%>&nbsp;|&nbsp;
            <%=W.pageLink("Last",     R.resourcePath("entries"), paginator.getQueryStringLast(),     "data-ajax:true; data-method:GET; data-target:#entries_list; data-handler:html")%>
        </td>
    </tr>
</table>

<br />
<%=W.windowStylePageLinks(paginator, R.resourcePath("entries"), 4, 11, "data-ajax:true; data-method:GET; data-target:#entries_list; data-handler:html")%>

<form action="<%=W.getURL(R.resourcePath("entries"))%>" method="GET"
data-ajax="true" data-method="GET" data-target="#entries_list" data-handler"html">
<input type="hidden" name="r" value="page">
<input type="hidden" name="limit" value="<%=W.get("limit", "10")%>">
<input type="hidden" name="paged" value="true">
Go to page <input type="text" id="npage" name="npage" size="2"><input type="submit" value="Go" />
</form>

<%=W.labelLink("Add entry", R.addResourcePath("entries"), "data-ajax:true; data-target:#entry; data-handler:html")%>|
<%=W.labelLink("List", R.resourcePath("entries"), "data-ajax:true; data-target:#entries_list; data-handler:html")%>

<br/>

<div id="entry"></div>

</div>
