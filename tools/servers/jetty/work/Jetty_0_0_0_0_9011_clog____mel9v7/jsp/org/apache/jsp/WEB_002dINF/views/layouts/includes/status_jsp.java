package org.apache.jsp.WEB_002dINF.views.layouts.includes;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import com.scooterframework.web.controller.ACH;
import com.scooterframework.web.util.M;

public final class status_jsp extends org.apache.jasper.runtime.HttpJspBase
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
 if (!M.isEmpty(ACH.getAC().getLatestFlashMessage("notice"))) { 
      out.write("\r\n");
      out.write("  <div id=\"notice\" style=\"color: green\">");
      out.print( ACH.getAC().getLatestFlashMessage("notice") );
      out.write("</div>\r\n");
 } 
      out.write('\r');
      out.write('\n');
 if (!M.isEmpty(ACH.getAC().getLatestFlashMessage("error"))) { 
      out.write("\r\n");
      out.write("  <div id=\"error\" style=\"color: red\">");
      out.print( ACH.getAC().getLatestFlashMessage("error") );
      out.write("</div>\r\n");
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
