<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="
        com.scooterframework.web.controller.ACH,
        com.scooterframework.web.util.M"
%>

<% if (!M.isEmpty(ACH.getAC().getLatestFlashMessage("notice"))) { %>
  <div id="notice" style="color: green"><%= ACH.getAC().getLatestFlashMessage("notice") %></div>
<% } %>
<% if (!M.isEmpty(ACH.getAC().getLatestFlashMessage("error"))) { %>
  <div id="error" style="color: red"><%= ACH.getAC().getLatestFlashMessage("error") %></div>
<% } %>
