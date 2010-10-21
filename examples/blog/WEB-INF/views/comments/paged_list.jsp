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
Paginator paginator = (Paginator)request.getAttribute("comment_page");
List records = paginator.getRecordList();
%>

<h2>comment List</h2>

<table>
    <tr>
        <td align="left">
            Page <%=paginator.getCurrentPage()%>/<%=paginator.getPageCount()%>. </td>
        <td align="right" class="multilink">
            Showing <%=paginator.getStartIndex()%> - <%=paginator.getEndIndex()%> of <%=paginator.getTotalCount()%>&nbsp;
            <%=W.pageLink("First",    R.resourcePath("comments"), paginator.getQueryStringFirst())%>&nbsp;|&nbsp;
            <%=W.pageLink("Previous", R.resourcePath("comments"), paginator.getQueryStringPrevious())%>&nbsp;|&nbsp;
            <%=W.pageLink("Next",     R.resourcePath("comments"), paginator.getQueryStringNext())%>&nbsp;|&nbsp;
            <%=W.pageLink("Last",     R.resourcePath("comments"), paginator.getQueryStringLast())%>
        </td>
    </tr>
    <tr>
        <td colspan="2">

<table class="sTable">
    <tr>
        <th>Id</th>
        <th>Commenter</th>
        <th>Body</th>
        <th>Post</th>
        <th>Created At</th>
        <th>&nbsp;</th>
    </tr>

<%for (Iterator it = O.iteratorOf(records); it.hasNext();) {
    RESTified comment = (RESTified)it.next();
%>
    <tr class="<%=W.cycle("odd, even")%>">
        <td><%=O.hp(comment, "id")%></td>
        <td><%=O.hp(comment, "commenter")%></td>
        <td><%=O.hp(comment, "body")%></td>
        <td><%=O.hp(comment, "post_id")%></td>
        <td><%=O.hp(comment, "created_at")%></td>
        <td class="multilink" nowrap>
            <%=W.labelLink("show", R.resourceRecordPath("comments", comment))%>
            <%=W.labelLink("edit", R.editResourceRecordPath("comments", comment))%>
            <%=W.labelLink("delete", R.resourceRecordPath("comments", comment), "confirm:'Are you sure?'; method:delete")%>
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
            <%=W.pageLink("First",    R.resourcePath("comments"), paginator.getQueryStringFirst())%>&nbsp;|&nbsp;
            <%=W.pageLink("Previous", R.resourcePath("comments"), paginator.getQueryStringPrevious())%>&nbsp;|&nbsp;
            <%=W.pageLink("Next",     R.resourcePath("comments"), paginator.getQueryStringNext())%>&nbsp;|&nbsp;
            <%=W.pageLink("Last",     R.resourcePath("comments"), paginator.getQueryStringLast())%>
        </td>
    </tr>
</table>

<br />
<%=W.diggStylePageLinks(paginator, R.resourcePath("comments"), 4, 11)%>

<form action="<%=W.getURL(R.resourcePath("comments"))%>" method="GET">
<input type="hidden" name="r" value="page">
<input type="hidden" name="limit" value="<%=W.get("limit", "10")%>">
<input type="hidden" name="paged" value="true">
Go to page <input type="text" id="npage" name="npage" size="2"><input type="submit" value="Go" />
</form>

<%=W.labelLink("Add comment", R.addResourcePath("comments"))%>|
<%=W.labelLink("List", R.resourcePath("comments"))%>
