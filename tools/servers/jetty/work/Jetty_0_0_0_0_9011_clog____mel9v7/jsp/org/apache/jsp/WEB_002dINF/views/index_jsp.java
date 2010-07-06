package org.apache.jsp.WEB_002dINF.views;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import java.util.Date;
import java.util.Map;
import com.scooterframework.admin.Constants;
import com.scooterframework.admin.EnvConfig;
import com.scooterframework.web.controller.ActionContext;
import com.scooterframework.web.controller.MainActionServlet;
import com.scooterframework.web.util.D;
import com.scooterframework.web.util.W;

public final class index_jsp extends org.apache.jasper.runtime.HttpJspBase
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
      out.write("<h2>");
      out.print(W.label("welcome.message"));
      out.write("</h2>\r\n");
      out.write("<h4>Have a fun drive!</h4>\r\n");
      out.write("\r\n");
      out.write("<h5>Application environment</h5>\r\n");
      out.write("<div id=\"appenv\">\r\n");
      out.write("    <table class=\"sTable\">\r\n");
      out.write("        <tr>\r\n");
      out.write("            <td>Java Version</td><td>");
      out.print(((Map)ActionContext.getFromGlobalData(Constants.APP_KEY_SCOOTER_PROPERTIES)).get(Constants.APP_KEY_JAVA_VERSION));
      out.write("</td>\r\n");
      out.write("        </tr>\r\n");
      out.write("        <tr>\r\n");
      out.write("            <td>Scooter Version</td><td>");
      out.print(((Map)ActionContext.getFromGlobalData(Constants.APP_KEY_SCOOTER_PROPERTIES)).get(Constants.APP_KEY_SCOOTER_VERSION));
      out.write("</td>\r\n");
      out.write("        </tr>\r\n");
      out.write("        <tr>\r\n");
      out.write("            <td>Running Environment</td><td>");
      out.print(((Map)ActionContext.getFromGlobalData(Constants.APP_KEY_SCOOTER_PROPERTIES)).get(Constants.APP_KEY_RUNNING_ENVIRONMENT));
      out.write("</td>\r\n");
      out.write("        </tr>\r\n");
      out.write("        <tr>\r\n");
      out.write("            <td>Context Name</td><td>");
      out.print(((Map)ActionContext.getFromGlobalData(Constants.APP_KEY_SCOOTER_PROPERTIES)).get(Constants.APP_KEY_APPLICATION_CONTEXT_NAME));
      out.write("</td>\r\n");
      out.write("        </tr>\r\n");
      out.write("        <tr>\r\n");
      out.write("            <td>Application Root</td><td>");
      out.print(((Map)ActionContext.getFromGlobalData(Constants.APP_KEY_SCOOTER_PROPERTIES)).get(Constants.APP_KEY_APPLICATION_ROOT_PATH));
      out.write("</td>\r\n");
      out.write("        </tr>\r\n");
      out.write("        <tr>\r\n");
      out.write("            <td>Default Database</td><td>");
      out.print(((Map)ActionContext.getFromGlobalData(Constants.APP_KEY_SCOOTER_PROPERTIES)).get(Constants.APP_KEY_APPLICATION_DATABASE_NAME));
      out.write("</td>\r\n");
      out.write("        </tr>\r\n");
      out.write("        <tr>\r\n");
      out.write("            <td>Server Startup Time</td><td>");
      out.print(((Map)ActionContext.getFromGlobalData(Constants.APP_KEY_SCOOTER_PROPERTIES)).get(Constants.APP_KEY_APPLICATION_START_TIME));
      out.write(" \r\n");
      out.write("                                            (");
      out.print(D.message((Date)(((Map)ActionContext.getFromGlobalData(Constants.APP_KEY_SCOOTER_PROPERTIES)).get(Constants.APP_KEY_APPLICATION_START_TIME))));
      out.write(")</td>\r\n");
      out.write("        </tr>\r\n");
      out.write("    </table>\r\n");
      out.write("</div>\r\n");
      out.write("\r\n");
      out.write("<h2>Getting started</h2>\r\n");
      out.write("<h4>Create your application</h4>\r\n");
      out.write("<ol>\r\n");
      out.write("    <li>Unzip <tt>scooter.zip</tt> to a directory</li>\r\n");
      out.write("    <li>Rename <tt>scooter</tt> directory name to your application name.</li>\r\n");
      out.write("    <li>Type <tt>java tools/init.jar mysql</tt> to initialize your application.</li>\r\n");
      out.write("    <li>Copy jdbc jar to <tt>references</tt> and <tt>WEB-INF/lib</tt> as instructed by console screen message.</li>\r\n");
      out.write("    <li>Type <tt>java tools/server.jar</tt> to start up your web server.</li>\r\n");
      out.write("    <li>(Optional) Use <tt>java tools/generate.jar</tt> to create your controllers and models</li>\r\n");
      out.write("    <li>(Optional) Set up routes in <tt>config/routes.properties</tt></li>\r\n");
      out.write("</ol>\r\n");
      out.write("\r\n");
      out.write("<h2>Browse your routes</h2>\r\n");
if (MainActionServlet.isUsingRestfulProcessor()) {
      out.write("\r\n");
      out.write("<h4>Click ");
      out.print(W.labelLink("here", "/routes"));
      out.write(" to see all routes supported by this site</h4>\r\n");
} else {
      out.write("\r\n");
      out.write("<h4>You need to choose <tt>com.scooterframework.web.controller.RestfulRequestProcessor</tt> \r\n");
      out.write("as the processor in <tt>web.xml</tt></h4>\r\n");
}
      out.write("\r\n");
      out.write("\r\n");
      out.write("<h2>Browse your data</h2>\r\n");
if (MainActionServlet.isUsingRestfulProcessor() && EnvConfig.getInstance().allowDataBrowser()) {
      out.write("\r\n");
      out.write("<h4>Click ");
      out.print(W.labelLink("here", "/databases"));
      out.write(" to see what you have in your data store</h4>\r\n");
} else {
      out.write("\r\n");
      out.write("<h4>You need to choose <tt>com.scooterframework.web.controller.RestfulRequestProcessor</tt> \r\n");
      out.write("as the processor in <tt>web.xml</tt> and also set <tt>allow.databrowser=true</tt> in environment.properties file.</h4>\r\n");
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
