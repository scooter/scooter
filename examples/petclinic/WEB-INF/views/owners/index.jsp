<%@ page import="
        java.util.Iterator,
        com.scooterframework.orm.activerecord.ActiveRecord,
        com.scooterframework.orm.sqldataexpress.object.RESTified,
        com.scooterframework.web.util.O,
        com.scooterframework.web.util.R,
        com.scooterframework.web.util.T,
        com.scooterframework.web.util.W"
%>

<h2><%=T.pluralize(O.count("owners"), "owner")%></h2>

<table class="sTable">
    <tr>
        <th>Name</th>
        <th>Address</th>
        <th>City</th>
        <th>Telephone</th>
        <th>Pets</th>
    </tr>
<%
for (Iterator it = O.iteratorOf("owners"); it.hasNext();) {
    RESTified owner = (RESTified)it.next();
%>
    <tr class="<%=W.cycle("odd, even")%>">
        <td><%=W.labelLink(O.hp(owner, "first_name") + " " + O.hp(owner, "last_name"), R.resourceRecordPath("owners", owner))%></td>
        <td><%=O.hp(owner, "address")%></td>
        <td><%=O.hp(owner, "city")%></td>
        <td><%=O.hp(owner, "telephone")%></td>
        <td><%=getOwnedPetNames((ActiveRecord)owner)%></td>
    </tr>
<%}%>
</table>

<br />

<%=W.labelLink("Add owner", R.addResourcePath("owners"))%>

<%!
private String getOwnedPetNames(ActiveRecord owner) {
    String s = "";
    Iterator it = O.iteratorOf(O.allAssociatedRecordsOf(owner, "pets"));
    while(it.hasNext()) {
        s = s + " " + ((ActiveRecord)it.next()).getField("name");
    }
    return s;
}
%>