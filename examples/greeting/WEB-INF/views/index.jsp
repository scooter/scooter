<%@ page import="
        com.scooterframework.admin.EnvConfig,
        com.scooterframework.web.controller.MainActionServlet,
        com.scooterframework.web.util.W"
%>
<h2><%=W.label("welcome.message")%></h2>
<h4>Have a fun drive! Try <%=W.labelLink("these examples", "/examples")%></h4>

<h2 class="sectionTitle">Getting started</h2>

<h4>Create your database (if you use database)</h4>
<ul style="margin-top: -10px;">
    <li>Edit config/database.properties with your own database properties</li>
    <li>Type <tt>java -jar tools/connection-test.jar app_name</tt> to test connections</li>
    <li>Enter data to database</li>
</ul>

<h4>Generate complete AJAX-based CRUD app</h4>
<ul style="margin-top: -10px;">
    <li>Type <tt>java -jar tools/generate.jar -help</tt> for examples</li>
</ul>

<h4>Generate controllers, models and views</h4>
<ul style="margin-top: -10px;">
    <li>Type <tt>java -jar tools/generate.jar -help</tt> for examples</li>
</ul>

<h4>Generate signon classes and views</h4>
<ul style="margin-top: -10px;">
    <li>Type <tt>java -jar tools/generate-signon.jar -help</tt> for examples</li>
</ul>

<h2 class="sectionTitle">More fun</h2>

<b><%=W.labelLink("Site info", "/admin/site")%></b>: view application environment information
<br/><br/>

<b><%=W.labelLink("Site manager", "/admin/files/list")%></b>: manage deployed files and folders (add/view/edit/replace/copy/delete/rename)
<br/><br/>

<%if (MainActionServlet.isUsingRestfulProcessor()) {%>
<b><%=W.labelLink("Browse routes", "/admin/routes")%></b>: view all routes supported by this site
<%} else {%>
<b>Browse routes</b>: You need to choose <tt>com.scooterframework.web.controller.RestfulRequestProcessor</tt> 
as the processor in <tt>web.xml</tt>
<%}%>
<br/><br/>

<%if (MainActionServlet.isUsingRestfulProcessor() && EnvConfig.getInstance().allowDataBrowser()) {%>
<b><%=W.labelLink("Browse databases", "/admin/databases")%></b>: see what you have in your data store
<%} else {%>
<b>Browse databases</b>: You need to choose <tt>com.scooterframework.web.controller.RestfulRequestProcessor</tt> 
as the processor in <tt>web.xml</tt> and also set <tt>allow.databrowser=true</tt> in environment.properties file.
<%}%>
<br/><br/>

<%if (MainActionServlet.isUsingRestfulProcessor() && EnvConfig.getInstance().allowDataBrowser()) {%>
<b><%=W.labelLink("SQL Window", "/admin/sqlwindow")%></b>: run ad-hoc SQL statement
<%} else {%>
<b>Run ad-hoc SQL</b>: You need to choose <tt>com.scooterframework.web.controller.RestfulRequestProcessor</tt> 
as the processor in <tt>web.xml</tt> and also set <tt>allow.databrowser=true</tt> in environment.properties file.
<%}%>
<br/><br/>