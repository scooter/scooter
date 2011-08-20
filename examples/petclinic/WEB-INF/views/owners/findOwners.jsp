<%@ page import="
        com.scooterframework.web.util.O,
        com.scooterframework.web.util.R,
        com.scooterframework.web.util.W"
%>

<h2>Find Owners:</h2>
<%=W.errorMessage("owner")%>

<form id="owner" action="<%=W.getURL("/owners/search")%>" method="post">
    Last Name:<br/> 
    <input type="text" id="owner_last_name" name="last_name" value="<%=O.hv("owner.last_name")%>" size="30" maxlength="80"/> <br/>
    <input type="submit" value="Find Owners"/>
</form>

<br/>
<%=W.labelLink("Add Owner", R.addResourcePath("owners"))%>