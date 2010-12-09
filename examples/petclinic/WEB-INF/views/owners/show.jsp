<%@ page import="
        java.util.Iterator,
        com.scooterframework.orm.activerecord.ActiveRecord,
        com.scooterframework.orm.sqldataexpress.object.RESTified,
        com.scooterframework.web.util.O,
        com.scooterframework.web.util.R,
        com.scooterframework.web.util.T,
        com.scooterframework.web.util.W"
%>

<%
RESTified owner = (RESTified)W.request("owner");
%>

<h2>Owner Information</h2>
<%=W.errorMessage("owner")%>

<p>
  <b>Name:</b>
  <b><%=O.hv("owner.first_name")%> <%=O.hv("owner.last_name")%></b>
</p>

<p>
  <b>Address:</b>
  <%=O.hv("owner.address")%>
</p>

<p>
  <b>City:</b>
  <%=O.hv("owner.city")%>
</p>

<p>
  <b>Telephone:</b>
  <%=O.hv("owner.telephone")%>
</p>

  <table class="table-buttons">
    <tr>
      <td colspan="2" align="center">
        <%=W.submitButtonLink("Edit Owner", R.editResourceRecordPath("owners", owner))%>
      </td>
      <td>
        <%=W.submitButtonLink("Add New Pet", R.addResourcePath("pets") + "?owner_id=" + owner.getRestfulId())%>
      </td>
    </tr>
  </table>

<h2>Pets and Visits</h2>

<div id="pets_visits">
<%for (Iterator it = O.iteratorOf(O.allAssociatedRecordsOf("owner.pets")); it.hasNext();) {
    ActiveRecord pet = (ActiveRecord)it.next();%>
    <p>
        <b>Name: </b><%=O.property(pet, "name")%>
        <b>Birth Date: </b><%=T.textOfDate(O.getProperty(pet, "birth_date"), "yyyy-MM-dd")%>
        <b>Type: </b><%=O.property(pet, "type.name")%>
        <br/>
        <b>Visits: </b><br/>
        <%for (Iterator it2 = O.iteratorOf(O.allAssociatedRecordsOf(pet, "visits")); it2.hasNext();) {
             Object visit = it2.next();%>
        <b>Visit Date: </b><%=O.property(visit, "visit_date", "yyyy-MM-dd")%>
        <b>Description: </b><%=O.property(visit, "description")%><br/>
        <%}%>
    </p>
    
    <table class="table-buttons">
      <tr>
        <td>
          <%=W.submitButtonLink("Edit Pet", R.editResourceRecordPath("pets", pet))%>
        </td>
        <td>
          <%=W.submitButtonLink("Add Visit", R.addNestedResourcePath("pets", pet, "visits"))%>
        </td>
      </tr>
    </table>
<%}%>
</div>