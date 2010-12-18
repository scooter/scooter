<%@ page import="
	java.util.Iterator,
	com.scooterframework.orm.sqldataexpress.object.RESTified,
	com.scooterframework.web.util.O,
	com.scooterframework.web.util.R,
	com.scooterframework.web.util.T,
	com.scooterframework.web.util.W"
%>

<div id="entries_list">

<h2>My TODO list: <%=T.pluralize(O.count("entries"), "entry")%></h2>
<%=W.errorMessage("entry")%>

<table width="100%">
<%
for (Iterator it = O.iteratorOf("entries"); it.hasNext();) {
    RESTified entry = (RESTified)it.next();
%>
    <tr>
        <td><b><%=O.hp(entry, "subject")%></b></td>
        <td align="right">Last edited at <%=O.hp(entry, "updated_at")%></td>
        <td nowrap>
            <%=W.labelLink("show", R.resourceRecordPath("entries", entry),     "data-ajax:true; data-method:GET; data-target:#entry; data-handler:html")%>
            <%=W.labelLink("edit", R.editResourceRecordPath("entries", entry), "data-ajax:true; data-method:GET; data-target:#entry; data-handler:html")%>
            <%=W.labelLink("delete", R.resourceRecordPath("entries", entry),   "data-ajax:true; data-method:DELETE; data-target:#entries_list; data-handler:html; data-confirm:Are you sure?")%>
        </td>
    </tr>
    <tr>
        <td colspan="3"><%=W.markdown(O.hp(entry, "details"))%></td>
    </tr>
    <tr>
        <td colspan="3"></td>
    </tr>
<%}%>
</table>

<br />

<%=W.labelLink("Add entry", R.addResourcePath("entries"), "data-ajax:true; data-method:GET; data-target:#entry; data-handler:html")%>|
<%=W.labelLink("Paged List", R.resourcePath("entries") + "?paged=true", "data-ajax:true; data-method:GET; data-target:#entries_list; data-handler:html")%>

<br/>

<div id="entry"></div>

</div>
