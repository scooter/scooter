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
Paginator paginator = (Paginator)request.getAttribute("post_page");
List records = paginator.getRecordList();
%>

<h2>post List</h2>

<table>
    <tr>
        <td align="left">
            Page <%=paginator.getCurrentPage()%>/<%=paginator.getPageCount()%>. </td>
        <td align="right" class="multilink">
            Showing <%=paginator.getStartIndex()%> - <%=paginator.getEndIndex()%> of <%=paginator.getTotalCount()%>&nbsp;
            <%=W.pageLink("First",    R.resourcePath("posts"), paginator.getQueryStringFirst())%>&nbsp;|&nbsp;
            <%=W.pageLink("Previous", R.resourcePath("posts"), paginator.getQueryStringPrevious())%>&nbsp;|&nbsp;
            <%=W.pageLink("Next",     R.resourcePath("posts"), paginator.getQueryStringNext())%>&nbsp;|&nbsp;
            <%=W.pageLink("Last",     R.resourcePath("posts"), paginator.getQueryStringLast())%>
        </td>
    </tr>
    <tr>
        <td colspan="2">

<table class="sTable">
    <tr>
        <th>Id</th>
        <th>Name</th>
        <th>Title</th>
        <th>Content</th>
        <th>Created At</th>
        <th>Updated At</th>
        <th>&nbsp;</th>
    </tr>

<%for (Iterator it = O.iteratorOf(records); it.hasNext();) {
    RESTified post = (RESTified)it.next();
%>
    <tr class="<%=W.cycle("odd, even")%>">
        <td><%=O.hp(post, "id")%></td>
        <td><%=O.hp(post, "name")%></td>
        <td><%=O.hp(post, "title")%></td>
        <td><%=O.hp(post, "content")%></td>
        <td><%=O.hp(post, "created_at")%></td>
        <td><%=O.hp(post, "updated_at")%></td>
        <td class="multilink" nowrap>
            <%=W.labelLink("show", R.resourceRecordPath("posts", post))%>
            <%=W.labelLink("edit", R.editResourceRecordPath("posts", post))%>
            <%=W.labelLink("delete", R.resourceRecordPath("posts", post), "confirm:'Are you sure?'; method:delete")%>
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
            <%=W.pageLink("First",    R.resourcePath("posts"), paginator.getQueryStringFirst())%>&nbsp;|&nbsp;
            <%=W.pageLink("Previous", R.resourcePath("posts"), paginator.getQueryStringPrevious())%>&nbsp;|&nbsp;
            <%=W.pageLink("Next",     R.resourcePath("posts"), paginator.getQueryStringNext())%>&nbsp;|&nbsp;
            <%=W.pageLink("Last",     R.resourcePath("posts"), paginator.getQueryStringLast())%>
        </td>
    </tr>
</table>

<br />
<%=W.windowStylePageLinks(paginator, R.resourcePath("posts"), 4, 11)%>

<form action="<%=W.getURL(R.resourcePath("posts"))%>" method="GET">
<input type="hidden" name="r" value="page">
<input type="hidden" name="limit" value="<%=W.get("limit", "10")%>">
<input type="hidden" name="paged" value="true">
Go to page <input type="text" id="npage" name="npage" size="2"><input type="submit" value="Go" />
</form>

<%=W.labelLink("Add post", R.addResourcePath("posts"))%>|
<%=W.labelLink("List", R.resourcePath("posts"))%>
