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
<h4>Create your application</h4>
<ol>
    <li>Unzip <tt>scooter.zip</tt> to a directory</li>
    <li>Rename <tt>scooter</tt> directory name to your application name.</li>
    <li>Type <tt>java tools/init.jar mysql</tt> to initialize your application.</li>
    <li>Copy jdbc jar to <tt>references</tt> and <tt>WEB-INF/lib</tt> as instructed by console screen message.</li>
    <li>Type <tt>java tools/server.jar</tt> to start up your web server.</li>
    <li>(Optional) Use <tt>java tools/generate.jar</tt> to create your controllers and models</li>
    <li>(Optional) Set up routes in <tt>config/routes.properties</tt></li>
</ol>

<h2>Browse your routes</h2>
<%if (MainActionServlet.isUsingRestfulProcessor()) {%>
<h4>Click <%=W.labelLink("here", "/routes")%> to see all routes supported by this site</h4>
<%} else {%>
<h4>You need to choose <tt>com.scooterframework.web.controller.RestfulRequestProcessor</tt> 
as the processor in <tt>web.xml</tt></h4>
<%}%>

<h2>Browse your data</h2>
<%if (MainActionServlet.isUsingRestfulProcessor() && EnvConfig.getInstance().allowDataBrowser()) {%>
<h4>Click <%=W.labelLink("here", "/databases")%> to see what you have in your data store</h4>
<%} else {%>
<h4>You need to choose <tt>com.scooterframework.web.controller.RestfulRequestProcessor</tt> 
as the processor in <tt>web.xml</tt> and also set <tt>allow.databrowser=true</tt> in environment.properties file.</h4>
<%}%>
