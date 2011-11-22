<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page contentType="text/html; charset=UTF-8"%>

<%@ page import="
	java.util.Iterator,
	com.scooterframework.orm.sqldataexpress.object.RESTified,
	com.scooterframework.web.util.O,
	com.scooterframework.web.util.R,
	com.scooterframework.web.util.T,
	com.scooterframework.web.util.W"
%>

<h2><%=T.pluralize(O.count("ttts"), "ttt")%></h2>
<%=W.errorMessage("ttt")%>

<table class="sTable">
    <tr>
        <th>Test</th>
        <th>Ishandsome</th>
        <th></th>
    </tr>
<%
for (Iterator it = O.iteratorOf("ttts"); it.hasNext();) {
    RESTified ttt = (RESTified)it.next();
%>
    <tr class="<%=W.cycle("odd, even")%>">
        <td><%=O.hp(ttt, "test_id")%></td>
        <td><%=O.hp(ttt, "ishandsome")%></td>
        <td nowrap>
            <%=W.labelLink("show", R.resourceRecordPath("ttts", ttt))%>
            <%=W.labelLink("edit", R.editResourceRecordPath("ttts", ttt))%>
            <%=W.labelLink("delete", R.resourceRecordPath("ttts", ttt), "confirm:'Are you sure?'; method:delete")%>
        </td>
    </tr>
<%}%>
</table>

<br />

<%=W.labelLink("Add ttt", R.addResourcePath("ttts"))%>|
<%=W.labelLink("Paged List", R.resourcePath("ttts") + "?paged=true")%>
