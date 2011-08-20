<%@ page import="
        java.util.Iterator,
        com.scooterframework.orm.sqldataexpress.object.RESTified,
        com.scooterframework.web.util.F,
        com.scooterframework.web.util.O,
        com.scooterframework.web.util.W"
%>

<%
System.out.println("$$$$$ " + O.hv("visit.description"));
%>

<h2>New Visit</h2>
<%=W.errorMessage("visit")%>

  <b>Pet:</b>
  <table width="333">
    <tr>
    <thead>
      <th>Name</th>
      <th>Birth Date</th>
      <th>Type</th>
      <th>Owner</th>
    </thead>
    </tr>
    <tr>
      <td><%=O.hv("pet.name")%></td>
      <td><%=O.value("pet.birth_date", "yyyy-MM-dd")%></td>
      <td><%=O.hv("pet.type.name")%></td>
      <td><%=O.hv("pet.owner.first_name")%> <%=O.hv("pet.owner.last_name")%></td>
    </tr>
  </table>
  
  <%=F.formForOpen("pets", "pet", "visits", "visit")%>
  <input type="hidden" id="pet_owner_id" name="owner_id" value="<%=O.hv("pet.owner.id")%>" />
  <table width="333">
    <tr>
      <th>
        <%=F.label("visit_date")%>:
      </th>
      <td>
        <input type="text" id="visit_visit_date" name="visit_date" value="<%=O.value("visit.visit_date", "yyyy-MM-dd")%>" size="10" maxlength="10"/> (yyyy-mm-dd)
      </td>
    <tr/>
    <tr>
      <th valign="top">
        <%=F.label("description")%>:
      </th>
      <td>
        <textarea id="visit_description" name="description" rows="10" cols="25"><%=O.hv("visit.description")%></textarea>
      </td>
    </tr>
    <tr>
      <td colspan="2">
        <input id="visit_submit" name="commit" type="submit" value="Add Visit" />&nbsp;&nbsp;&nbsp;<input type="reset"/>
      </td>
    </tr>
  </table>
  <%=F.formForClose("visits")%>

<br />
<b>Previous Visits:</b>
<table class="sTable">
    <tr>
        <th>Date</th>
        <th>Description</th>
    </tr>
<%
for (Iterator it = O.iteratorOf("visits"); it.hasNext();) {
    RESTified visit = (RESTified)it.next();
%>
    <tr class="<%=W.cycle("odd, even")%>">
        <td><%=O.property(visit, "visit_date", "yyyy-MM-dd")%></td>
        <td><%=O.hp(visit, "description")%></td>
    </tr>
<%}%>
</table>