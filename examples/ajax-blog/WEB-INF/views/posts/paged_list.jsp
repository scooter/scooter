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
            <%=W.pageLink("First",    R.resourcePath("posts"), paginator.getQueryStringFirst(),    "data-ajax:true; data-method:GET; data-target:#posts_list; data-handler:html")%>&nbsp;|&nbsp;
            <%=W.pageLink("Previous", R.resourcePath("posts"), paginator.getQueryStringPrevious(), "data-ajax:true; data-method:GET; data-target:#posts_list; data-handler:html")%>&nbsp;|&nbsp;
            <%=W.pageLink("Next",     R.resourcePath("posts"), paginator.getQueryStringNext(),     "data-ajax:true; data-method:GET; data-target:#posts_list; data-handler:html")%>&nbsp;|&nbsp;
            <%=W.pageLink("Last",     R.resourcePath("posts"), paginator.getQueryStringLast(),     "data-ajax:true; data-method:GET; data-target:#posts_list; data-handler:html")%>
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
            <%=W.labelLink("show",   R.resourceRecordPath("posts", post),     "data-ajax:true; data-method:GET; data-target:#entry; data-handler:html")%>
            <%=W.labelLink("edit",   R.editResourceRecordPath("posts", post), "data-ajax:true; data-method:GET; data-target:#entry; data-handler:html")%>
            <%=W.labelLink("delete", R.resourceRecordPath("posts", post),     "data-confirm:Are you sure?; data-ajax:true; data-method:delete; data-target:#posts_list; data-handler:html")%>
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
            <%=W.pageLink("First",    R.resourcePath("posts"), paginator.getQueryStringFirst(),    "data-ajax:true; data-method:GET; data-target:#posts_list; data-handler:html")%>&nbsp;|&nbsp;
            <%=W.pageLink("Previous", R.resourcePath("posts"), paginator.getQueryStringPrevious(), "data-ajax:true; data-method:GET; data-target:#posts_list; data-handler:html")%>&nbsp;|&nbsp;
            <%=W.pageLink("Next",     R.resourcePath("posts"), paginator.getQueryStringNext(),     "data-ajax:true; data-method:GET; data-target:#posts_list; data-handler:html")%>&nbsp;|&nbsp;
            <%=W.pageLink("Last",     R.resourcePath("posts"), paginator.getQueryStringLast(),     "data-ajax:true; data-method:GET; data-target:#posts_list; data-handler:html")%>
        </td>
    </tr>
</table>

<br />
<%=W.windowStylePageLinks(paginator, R.resourcePath("posts"), 4, 11, "data-ajax:true; data-method:GET; data-target:#posts_list; data-handler:html")%>

<form action="<%=W.getURL(R.resourcePath("posts"))%>" method="GET"
data-ajax="true" data-method="GET" data-target="#posts_list" data-handler"html">
<input type="hidden" name="r" value="page">
<input type="hidden" name="limit" value="<%=W.get("limit", "10")%>">
<input type="hidden" name="paged" value="true">
Go to page <input type="text" id="npage" name="npage" size="2"><input type="submit" value="Go" />
</form>

<%=W.labelLink("Add post", R.addResourcePath("posts"), "data-ajax:true; data-target:#entry; data-handler:html")%>|
<%=W.labelLink("List", R.resourcePath("posts"), "data-ajax:true; data-target:#posts_list; data-handler:html")%>

<br/>

<div id="entry"></div>

</div>
