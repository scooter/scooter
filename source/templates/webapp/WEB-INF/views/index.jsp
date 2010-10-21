<%@ page import="
        java.util.Date,
        java.util.Map,
        com.scooterframework.admin.Constants,
        com.scooterframework.admin.EnvConfig,
        com.scooterframework.web.controller.ActionContext,
        com.scooterframework.web.controller.MainActionServlet,
        com.scooterframework.web.util.D,
        com.scooterframework.web.util.W"
%>
<h2><%=W.label("welcome.message")%></h2>
<h4>Have a fun drive!</h4>

<h5>Application environment</h5>
<div id="appenv">
    <table class="sTable">
        <tr>
            <td>Java Version</td><td><%=((Map)ActionContext.getFromGlobalData(Constants.APP_KEY_SCOOTER_PROPERTIES)).get(Constants.APP_KEY_JAVA_VERSION)%></td>
        </tr>
        <tr>
            <td>Scooter Version</td><td><%=((Map)ActionContext.getFromGlobalData(Constants.APP_KEY_SCOOTER_PROPERTIES)).get(Constants.APP_KEY_SCOOTER_VERSION)%></td>
        </tr>
        <tr>
            <td>Running Environment</td><td><%=((Map)ActionContext.getFromGlobalData(Constants.APP_KEY_SCOOTER_PROPERTIES)).get(Constants.APP_KEY_RUNNING_ENVIRONMENT)%></td>
        </tr>
        <tr>
            <td>Context Name</td><td><%=((Map)ActionContext.getFromGlobalData(Constants.APP_KEY_SCOOTER_PROPERTIES)).get(Constants.APP_KEY_APPLICATION_CONTEXT_NAME)%></td>
        </tr>
        <tr>
            <td>Application Root</td><td><%=((Map)ActionContext.getFromGlobalData(Constants.APP_KEY_SCOOTER_PROPERTIES)).get(Constants.APP_KEY_APPLICATION_ROOT_PATH)%></td>
        </tr>
        <tr>
            <td>Default Database</td><td><%=((Map)ActionContext.getFromGlobalData(Constants.APP_KEY_SCOOTER_PROPERTIES)).get(Constants.APP_KEY_APPLICATION_DATABASE_NAME)%></td>
        </tr>
        <tr>
            <td>Server Startup Time</td><td><%=((Map)ActionContext.getFromGlobalData(Constants.APP_KEY_SCOOTER_PROPERTIES)).get(Constants.APP_KEY_APPLICATION_START_TIME)%> 
                                            (<%=D.message((Date)(((Map)ActionContext.getFromGlobalData(Constants.APP_KEY_SCOOTER_PROPERTIES)).get(Constants.APP_KEY_APPLICATION_START_TIME)))%>)</td>
        </tr>
    </table>
</div>

<h2>Getting started</h2>
<h4>Create application</h4>
<ol>
    <li>Type <tt>java -jar tools/create.jar blog</tt> to create an application named blog.</li>
    <li>Type <tt>java -jar tools/server.jar blog</tt> to start up your web server.</li>
</ol>

<h2>Browse deployed files and folders</h2>
<h4>Click <%=W.labelLink("here", "/files/list")%> to see what you have in your container</h4>

<h2>Browse routes</h2>
<%if (MainActionServlet.isUsingRestfulProcessor()) {%>
<h4>Click <%=W.labelLink("here", "/routes")%> to see all routes supported by this site</h4>
<%} else {%>
<h4>You need to choose <tt>com.scooterframework.web.controller.RestfulRequestProcessor</tt> 
as the processor in <tt>web.xml</tt></h4>
<%}%>

<h2>Browse databases</h2>
<%if (MainActionServlet.isUsingRestfulProcessor() && EnvConfig.getInstance().allowDataBrowser()) {%>
<h4>Click <%=W.labelLink("here", "/databases")%> to see what you have in your data store</h4>
<%} else {%>
<h4>You need to choose <tt>com.scooterframework.web.controller.RestfulRequestProcessor</tt> 
as the processor in <tt>web.xml</tt> and also set <tt>allow.databrowser=true</tt> in environment.properties file.</h4>
<%}%>