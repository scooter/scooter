package org.apache.jsp.WEB_002dINF.views;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import java.io.PrintWriter;
import com.scooterframework.admin.Constants;
import com.scooterframework.admin.EnvConfig;
import com.scooterframework.web.controller.ActionContext;

public final class error_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static java.util.List _jspx_dependants;

  public Object getDependants() {
    return _jspx_dependants;
  }

  public void _jspService(HttpServletRequest request, HttpServletResponse response)
        throws java.io.IOException, ServletException {

    JspFactory _jspxFactory = null;
    PageContext pageContext = null;
    HttpSession session = null;
    ServletContext application = null;
    ServletConfig config = null;
    JspWriter out = null;
    Object page = this;
    JspWriter _jspx_out = null;
    PageContext _jspx_page_context = null;


    try {
      _jspxFactory = JspFactory.getDefaultFactory();
      response.setContentType("text/html");
      pageContext = _jspxFactory.getPageContext(this, request, response,
      			null, true, 8192, true);
      _jspx_page_context = pageContext;
      application = pageContext.getServletContext();
      config = pageContext.getServletConfig();
      session = pageContext.getSession();
      out = pageContext.getOut();
      _jspx_out = out;

      out.write("\r\n");
      out.write("\r\n");
      out.write("<script language=\"javascript\"> \r\n");
      out.write("function toggleStackTrace() {\r\n");
      out.write("    var trace = document.getElementById(\"stackTraceText\");\r\n");
      out.write("    var choice = document.getElementById(\"displayChoice\");\r\n");
      out.write("    if(trace.style.display == \"block\") {\r\n");
      out.write("        trace.style.display = \"none\";\r\n");
      out.write("        choice.innerHTML = \"show\";\r\n");
      out.write("    }\r\n");
      out.write("    else {\r\n");
      out.write("        trace.style.display = \"block\";\r\n");
      out.write("        choice.innerHTML = \"hide\";\r\n");
      out.write("    }\r\n");
      out.write("} \r\n");
      out.write("</script>\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("<h3>Default Error Page</h3>\r\n");
      out.write("\r\n");
      out.write("<p>An error happened. Please contact your site admin for details.</p>\r\n");
      out.write("\r\n");

if (EnvConfig.getInstance().allowDisplayingErrorDetails()) {
    String detailedMessage = (String)ActionContext.getFromThreadData(Constants.ERROR_MESSAGE);
    Throwable ex = ((Throwable)ActionContext.getFromThreadData(Constants.ERROR_EXCEPTION));
    if (detailedMessage != null && !"".equals(detailedMessage.trim())) {

      out.write("\r\n");
      out.write("<div id=\"error\">\r\n");
      out.write("<h4>Error Message:</h4>\r\n");
      out.print(detailedMessage);
      out.write("\r\n");
      out.write("\r\n");
if (ex != null) {
      out.write("\r\n");
      out.write("<h4>StackTrace (<a id= \"displayChoice\" href=\"javascript:toggleStackTrace();\">show</a>):</h4>\r\n");
      out.write("    <div id = \"stackTraceText\" style=\"display: none\">\r\n");
      out.write("        ");
ex.printStackTrace(new PrintWriter(out));
      out.write("\r\n");
      out.write("    </div>\r\n");
      out.write("</div>\r\n");
}
    }
}
      out.write('\r');
      out.write('\n');
    } catch (Throwable t) {
      if (!(t instanceof SkipPageException)){
        out = _jspx_out;
        if (out != null && out.getBufferSize() != 0)
          out.clearBuffer();
        if (_jspx_page_context != null) _jspx_page_context.handlePageException(t);
      }
    } finally {
      if (_jspxFactory != null) _jspxFactory.releasePageContext(_jspx_page_context);
    }
  }
}
