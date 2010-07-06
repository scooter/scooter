package org.apache.jsp.WEB_002dINF.views.layouts.includes;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import com.scooterframework.web.util.W;

public final class header_jsp extends org.apache.jasper.runtime.HttpJspBase
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
      response.setContentType("text/html; charset=UTF-8");
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
      out.write("\r\n");
      out.write("<div id=\"siteName\">\r\n");
      out.write("    <h2>clog</h2>\r\n");
      out.write("</div>\r\n");
      out.write("\r\n");

    StringBuffer printUrl = new StringBuffer();
    printUrl.append("?printable=true");
    if (request.getQueryString()!=null) {
        printUrl.append('&');
        printUrl.append(request.getQueryString());
    }

      out.write("\r\n");
      out.write("<div id=\"topLinks\">\r\n");
      out.write("    <p align=\"right\">");
      out.print(W.labelLink("Home", "/"));
      out.write(' ');
      out.write('|');
      out.write(' ');
      out.print(W.labelLink("Routes", "/routes"));
      out.write(" | <a href=\"");
      out.print( printUrl );
      out.write("\">printable version</a></p>\r\n");
      out.write("</div>\r\n");
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
