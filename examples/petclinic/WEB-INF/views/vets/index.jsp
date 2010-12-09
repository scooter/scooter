<%@ page import="
        java.util.Iterator,
        com.scooterframework.common.util.Util,
        com.scooterframework.orm.activerecord.ActiveRecord,
        com.scooterframework.orm.sqldataexpress.object.RESTified,
        com.scooterframework.web.util.O,
        com.scooterframework.web.util.T,
        com.scooterframework.web.util.W"
%>

<h2><%=T.pluralize(O.count("vets"), "vet")%></h2>

<table class="sTable">
    <tr>
        <th>Name</th>
        <th>Specialties</th>
    </tr>
<%
for (Iterator it = O.iteratorOf("vets"); it.hasNext();) {
    RESTified vet = (RESTified)it.next();
%>
    <tr class="<%=W.cycle("odd, even")%>">
        <td><%=O.hp(vet, "first_name")%> <%=O.hp(vet, "last_name")%></td>
        <td><%=Util.ifEmpty(getVetSpecialties((ActiveRecord)vet), "none")%></td>
    </tr>
<%}%>
</table>

<%!
//concatenate all specialies of a vet; no db hit as we used include
private String getVetSpecialties(ActiveRecord vet) {
    String s = "";
    Iterator it = O.iteratorOf(O.allAssociatedRecordsOf(vet, "specialties"));
    while(it.hasNext()) {
        s = s + " " + ((ActiveRecord)it.next()).getField("name");
    }
    return s;
}
%>