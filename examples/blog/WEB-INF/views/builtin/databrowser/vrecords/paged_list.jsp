<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page contentType="text/html; charset=UTF-8"%>

<%@ page import="
        java.util.Iterator,
        java.util.List,
        com.scooterframework.common.util.WordUtil,
        com.scooterframework.orm.misc.Paginator,
        com.scooterframework.orm.sqldataexpress.object.RESTified,
        com.scooterframework.admin.Constants,
        com.scooterframework.web.util.O,
        com.scooterframework.web.util.R,
        com.scooterframework.web.util.T,
        com.scooterframework.web.util.W"
%>

<%
Paginator paginator = (Paginator)request.getAttribute("paged_records");
List records = paginator.getRecordList();
String resource = (String)request.getAttribute(Constants.RESOURCE);
String database = (String)request.getAttribute("database");
String view = (String)request.getAttribute("view");
%>

<div id="locator">
    <p><%=W.labelLink("Home", "/")%> > 
       <%=W.labelLink("Databases", R.resourcePath("databases"))%> > 
       <%=W.labelLink(database, R.resourceRecordPath("databases", database))%> > 
       <%=W.labelLink("Views", R.nestedResourcePath("databases", database, "views"))%> > 
       <%=W.labelLink(view, R.resourcePath(resource))%></p>
</div>

<h3><%=T.pluralize(paginator.getTotalCount(), "record")%> in View <%=view%> </h3>

<table>
    <tr>
        <td align="left">
            Page <%=paginator.getCurrentPage()%>/<%=paginator.getPageCount()%>. </td>
        <td align="right" class="multilink">
            Showing <%=paginator.getStartIndex()%> - <%=paginator.getEndIndex()%> of <%=paginator.getTotalCount()%>&nbsp;
            <%=W.pageLink("First",    R.resourcePath(resource), paginator.getQueryStringFirst())%>&nbsp;|&nbsp;
            <%=W.pageLink("Previous", R.resourcePath(resource), paginator.getQueryStringPrevious())%>&nbsp;|&nbsp;
            <%=W.pageLink("Next",     R.resourcePath(resource), paginator.getQueryStringNext())%>&nbsp;|&nbsp;
            <%=W.pageLink("Last",     R.resourcePath(resource), paginator.getQueryStringLast())%>
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

<%for (Iterator it = O.iteratorOf(records); it.hasNext();) {
    RESTified record = (RESTified)it.next();
    String restfulId = O.encodedRestfulIdOf(record);
%>
    <tr class="<%=W.cycle("odd, even")%>">
<%
    for (Iterator it2 = O.columnNames(record); it2.hasNext();) {
        String columnName = (String)it2.next();
%>
        <td><%=O.hp(record, columnName)%></td>
<%  }%>
        <td class="multilink" nowrap>
            <%=W.labelLink("show",   R.resourceRecordPath(resource, restfulId))%>
            <%=W.labelLink("edit",   R.editResourceRecordPath(resource, restfulId))%>
            <%=W.labelLink("delete", R.resourceRecordPath(resource, restfulId), "confirm:'Are you sure?'; method:delete")%>
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
            <%=W.pageLink("First",    R.resourcePath(resource), paginator.getQueryStringFirst())%>&nbsp;|&nbsp;
            <%=W.pageLink("Previous", R.resourcePath(resource), paginator.getQueryStringPrevious())%>&nbsp;|&nbsp;
            <%=W.pageLink("Next",     R.resourcePath(resource), paginator.getQueryStringNext())%>&nbsp;|&nbsp;
            <%=W.pageLink("Last",     R.resourcePath(resource), paginator.getQueryStringLast())%>
        </td>
    </tr>
</table>

<p align="center">
<%=W.windowStylePageLinks(paginator, R.resourcePath(resource), 4, 11)%>

<form action="<%=W.getURL(R.resourcePath(resource))%>" method="GET">
<input type="hidden" name="r" value="page">
<input type="hidden" name="limit" value="<%=W.get("limit", "10")%>">
<input type="hidden" name="paged" value="true">
Go to page <input type="text" id="npage" name="npage" size="2"><input type="submit" value="Go" />
</form>
</p>

<p class="multilink">
<%=W.labelLink("List", R.resourcePath(resource))%>
</p>