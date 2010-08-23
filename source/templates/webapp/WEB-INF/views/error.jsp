<%@ page import="
        java.io.PrintWriter,
        com.scooterframework.admin.Constants,
        com.scooterframework.admin.EnvConfig,
        com.scooterframework.web.controller.ActionContext"
%>

<script language="javascript"> 
function toggleStackTrace() {
    var trace = document.getElementById("stackTraceText");
    var choice = document.getElementById("displayChoice");
    if(trace.style.display == "block") {
        trace.style.display = "none";
        choice.innerHTML = "show";
    }
    else {
        trace.style.display = "block";
        choice.innerHTML = "hide";
    }
} 
</script>


<h3>Default Error Page</h3>

<p>An error happened. Please contact your site admin for details.</p>

<%
if (EnvConfig.getInstance().allowDisplayingErrorDetails()) {
    String detailedMessage = (String)ActionContext.getFromThreadData(Constants.ERROR_MESSAGE);
    Throwable ex = ((Throwable)ActionContext.getFromThreadData(Constants.ERROR_EXCEPTION));
    if (detailedMessage != null && !"".equals(detailedMessage.trim())) {
%>
<div id="error">
<h4>Error Message:</h4>
<%=detailedMessage%>

<%if (ex != null) {%>
<h4>StackTrace (<a id= "displayChoice" href="javascript:toggleStackTrace();">show</a>):</h4>
    <div id = "stackTraceText" style="display: none">
        <%ex.printStackTrace(new PrintWriter(out));%>
    </div>
</div>
<%}
    }
}%>