package org.apache.jsp.WEB_002dINF.views.builtin.databrowser.databases;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import com.scooterframework.admin.Constants;
import com.scooterframework.builtin.databrowser.Database;
import com.scooterframework.common.util.NamedProperties;
import com.scooterframework.orm.sqldataexpress.object.TableInfo;
import com.scooterframework.web.util.O;
import com.scooterframework.web.util.R;
import com.scooterframework.web.util.W;

public final class show_jsp extends org.apache.jasper.runtime.HttpJspBase
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
Properties connInfo = (Properties)request.getAttribute("database");
String database = O.property(connInfo, NamedProperties.KEY_NAME);
String[] s2 = Database.getCatalogAndSchema(database);
String schema = (String)W.get("schema", s2[1]);
String targetURL = "/databases/" + database;

      out.write("\r\n");
      out.write("\r\n");
      out.write("<div id=\"locator\">\r\n");
      out.write("    <p>");
      out.print(W.labelLink("Home", "/"));
      out.write(" > \r\n");
      out.write("       ");
      out.print(W.labelLink("Databases", R.resourcePath(resource)));
      out.write(" > \r\n");
      out.write("       ");
      out.print(database);
      out.write("</p>\r\n");
      out.write("</div>\r\n");
      out.write("\r\n");
      out.write("<table class=\"sTable\">\r\n");
for (Iterator it = O.iteratorOf(connInfo); it.hasNext();) { 
      out.write("\r\n");
      out.write("    <tr>\r\n");

        String key = (String)it.next();
        String value = connInfo.getProperty(key);
        if ("password".equalsIgnoreCase(key)) value = "******";

      out.write("\r\n");
      out.write("        <td><b>");
      out.print(key);
      out.write("</b></td>\r\n");
      out.write("        <td>");
      out.print(value);
      out.write("</td>\r\n");
      out.write("    </tr>\r\n");
}
      out.write("\r\n");
      out.write("</table>\r\n");
      out.write("\r\n");
      out.write("<p>");
      out.print(W.labelLink("Tables", R.nestedResourcePath(resource, database, "tables")));
      out.write(" | \r\n");
      out.write("   ");
      out.print(W.labelLink("Views", R.nestedResourcePath(resource, database, "views")));
      out.write("\r\n");
      out.write("</p>\r\n");
      out.write("\r\n");
if (Database.isOracle(database)) {
      out.write("\r\n");
      out.write("<form action=\"");
      out.print(W.getURL(targetURL));
      out.write("\">\r\n");
      out.write("<table>\r\n");
      out.write("    <tr>\r\n");
      out.write("        <td align=\"right\"><b>Enter schema name:</b></td>\r\n");
      out.write("        <td><input type=\"TEXT\" name=\"schema\" value=\"");
      out.print(schema);
      out.write("\" size=\"20\" />&nbsp;</td>\r\n");
      out.write("        <td colspan=\"2\">\r\n");
      out.write("            <input name=\"submit\"  type=\"submit\" value=\"Tables\" />\r\n");
      out.write("            <input name=\"submit\"  type=\"submit\" value=\"Views\" />\r\n");
      out.write("        </td>\r\n");
      out.write("    </tr>\r\n");
      out.write("</table>\r\n");
      out.write("</form>\r\n");
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
