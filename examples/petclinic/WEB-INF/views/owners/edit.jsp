<%@ page import="
        com.scooterframework.orm.sqldataexpress.object.RESTified,
        com.scooterframework.web.util.F,
        com.scooterframework.web.util.O,
        com.scooterframework.web.util.R,
        com.scooterframework.web.util.W"
%>

<h2>Edit owner</h2>
<%=W.errorMessage("owner")%>

<%=F.formForOpen("owners", "owner")%>
  <input type="hidden" id="owner_id" name="id" value="<%=O.hv("owner.id")%>" />
  <p>
    <%=F.label("first_name")%><br />
    <input type="text" id="owner_first_name" name="first_name" value="<%=O.hv("owner.first_name")%>" size="30"/>
  </p>
  <p>
    <%=F.label("last_name")%><br />
    <input type="text" id="owner_last_name" name="last_name" value="<%=O.hv("owner.last_name")%>" size="30"/>
  </p>
  <p>
    <%=F.label("address")%><br />
    <input type="text" id="owner_address" name="address" value="<%=O.hv("owner.address")%>" size="30"/>
  </p>
  <p>
    <%=F.label("city")%><br />
    <input type="text" id="owner_city" name="city" value="<%=O.hv("owner.city")%>" size="30"/>
  </p>
  <p>
    <%=F.label("telephone")%><br />
    <input type="text" id="owner_telephone" name="telephone" value="<%=O.hv("owner.telephone")%>" size="20"/>
  </p>
  <input id="owner_submit" name="commit" type="submit" value="Update" />&nbsp;&nbsp;&nbsp;<input type="reset"/>
<%=F.formForClose("owners")%>

<br />

<%=W.labelLink("Show", R.resourceRecordPath("owners", (RESTified)W.request("owner")))%>|
<%=W.labelLink("List", R.resourcePath("owners"))%>|
<%=W.labelLink("Paged List", R.resourcePath("owners") + "?paged=true")%>