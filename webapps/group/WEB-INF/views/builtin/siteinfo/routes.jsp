<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page contentType="text/html; charset=UTF-8"%>

<%@ page import="
        java.util.Iterator,
        java.util.List,
        com.scooterframework.web.util.O,
        com.scooterframework.web.util.T,
        com.scooterframework.web.util.W,
        com.scooterframework.web.route.Route,
        com.scooterframework.web.route.RestRoute,
        com.scooterframework.web.route.RouteConstants"
%>

<%
List routes = (List)request.getAttribute("routes");
%>

<div id="locator">
    <p><%=W.labelLink("Home", "/")%> > Routes</p>
</div>

<h3>auto-rest: <%=W.get("auto.rest")%>, auto-crud: <%=W.get("auto.crud")%></h3>

<h3><%=T.pluralize(O.count(routes), "route")%> declared for this application</h3>

<table class="sTable">
    <tr>
        <th></th>
        <th>Name</th>
        <th>Type</th>
        <th>Allowed Methods</th>
        <th>URL</th>
        <th>Controller</th>
        <th>Controller Class</th>
        <th>Action</th>
        <th>Id</th>
        <th>Format</th>
        <th>Allowed Formats</th>
        <th>Singular</th>
        <th>Namespace</th>
        <th>Path Prefix</th>
        <th>Requirements</th>
        <th>Resource</th>
    </tr>

<%
int order = -1;
for (Iterator it = O.iteratorOf(routes); it.hasNext();) {
    order++;
%>
    <tr class="<%=W.cycle("odd, even")%>">
<%
        Route route = (Route)it.next();
        String resource = "";
        if (RouteConstants.ROUTE_TYPE_REST.equals(route.getRouteType())) 
            resource = ((RestRoute)route).getResourceName();
%>
        <td><%=order%></td>
        <td nowrap="nowrap" align="right"><%=O.property(route, "name")%></td>
        <td align="right"><%=O.property(route, "routeType")%></td>
        <td align="right"><%=O.property(route, "allowedMethods")%></td>
        <td nowrap="nowrap"><%=O.property(route, "URLPattern")%></td>
        <td><%=O.property(route, "controller")%></td>
        <td><%=O.property(route, "controllerClass")%></td>
        <td align="right"><%=O.property(route, "action")%></td>
        <td><%=O.property(route, "id")%></td>
        <td align="right"><%=O.property(route, "format")%></td>
        <td align="right"><%=O.property(route, "allowedFormats")%></td>
        <td><%=O.property(route, "singular")%></td>
        <td nowrap="nowrap"><%=O.property(route, "namespace")%></td>
        <td nowrap="nowrap"><%=O.property(route, "pathPrefix")%></td>
        <td><%=O.property(route, "requirements")%></td>
        <td><%=resource%></td>
    </tr>
<%}%>
</table>
