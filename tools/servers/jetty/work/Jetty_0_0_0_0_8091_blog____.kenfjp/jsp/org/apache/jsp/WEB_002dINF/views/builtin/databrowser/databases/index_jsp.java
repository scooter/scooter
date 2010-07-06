package org.apache.jsp.WEB_002dINF.views.builtin.databrowser.databases;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import java.util.Iterator;
import java.util.List;
import com.scooterframework.admin.Constants;
import com.scooterframework.web.util.O;
import com.scooterframework.web.util.R;
import com.scooterframework.web.util.T;
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
      out.write("\r\n");

String resource = (String)request.getAttribute(Constants.RESOURCE);
String recordName = (String)request.getAttribute(Constants.MODEL);
List records = (List)request.getAttribute("databases");

      out.write("\r\n");
      out.write("\r\n");
      out.write("<div id=\"locator\">\r\n");
      out.write("    <p>");
      out.print(W.labelLink("Home", "/"));
      out.write(" > Databases</p>\r\n");
      out.write("</div>\r\n");
      out.write("\r\n");
      out.write("<h3>");
      out.print(T.pluralize(O.count(records), "database"));
      out.write("</h3>\r\n");
      out.write("\r\n");
      out.write("<table class=\"sTable\">\r\n");
for (Iterator it = O.iteratorOf(records); it.hasNext();) { 
      out.write("\r\n");
      out.write("    <tr>\r\n");

        String db = (String)it.next();

      out.write("\r\n");
      out.write("        <td>");
      out.print(db);
      out.write("</td>\r\n");
      out.write("        <td>");
      out.print(W.labelLink("show", R.resourceRecordPath(resource, db)));
      out.write("</td>\r\n");
      out.write("    </tr>\r\n");
}
      out.write("\r\n");
      out.write("</table>\r\n");
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
