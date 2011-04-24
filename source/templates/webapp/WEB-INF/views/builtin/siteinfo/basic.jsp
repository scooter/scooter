<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page contentType="text/html; charset=UTF-8"%>

<%@ page import="
        java.util.Date,
        java.util.Map,
        com.scooterframework.admin.Constants,
        com.scooterframework.admin.WebSessionListener,
        com.scooterframework.web.controller.ActionContext,
        com.scooterframework.web.util.D,
        com.scooterframework.web.util.W"
%>

<div id="appenv">
<h4>Application environment</h4>
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
            <td>Current Sessions</td><td><%=WebSessionListener.getSessionCount()%></td>
        </tr>
        <tr>
            <td>Maximum Sessions</td><td><%=WebSessionListener.getSessionCountMax()%> (<%=WebSessionListener.getSessionCountMaxDate()%> - <%=D.message(WebSessionListener.getSessionCountMaxDate())%>)</td>
        </tr>
        <tr>
            <td>Application Root</td><td><%=((Map)ActionContext.getFromGlobalData(Constants.APP_KEY_SCOOTER_PROPERTIES)).get(Constants.APP_KEY_APPLICATION_ROOT_PATH)%></td>
        </tr>
        <tr>
            <td>Default Database</td><td><%=((Map)ActionContext.getFromGlobalData(Constants.APP_KEY_SCOOTER_PROPERTIES)).get(Constants.APP_KEY_APPLICATION_DATABASE_NAME)%></td>
        </tr>
        <tr>
            <td>Server Startup Time</td><td><%=((Map)ActionContext.getFromGlobalData(Constants.APP_KEY_SCOOTER_PROPERTIES)).get(Constants.APP_KEY_APPLICATION_START_TIME)%> -&nbsp;
                                            <%=D.message((Date)(((Map)ActionContext.getFromGlobalData(Constants.APP_KEY_SCOOTER_PROPERTIES)).get(Constants.APP_KEY_APPLICATION_START_TIME)))%></td>
        </tr>
    </table>
</div>