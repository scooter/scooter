package org.apache.jsp.WEB_002dINF.views.builtin.databrowser.tables;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import java.util.Iterator;
import java.util.List;
import com.scooterframework.admin.Constants;
import com.scooterframework.orm.sqldataexpress.object.TableInfo;
import com.scooterframework.web.util.O;
import com.scooterframework.web.util.R;
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
String database = (String)request.getAttribute("database");
List tableInfos = (List)request.getAttribute("tables");

      out.write("\r\n");
      out.write("\r\n");
      out.write("<div id=\"locator\">\r\n");
      out.write("    <p>");
      out.print(W.labelLink("Home", "/"));
      out.write(" > \r\n");
      out.write("       ");
      out.print(W.labelLink("Databases", R.resourcePath("databases")));
      out.write(" > \r\n");
      out.write("       ");
      out.print(W.labelLink(database, R.resourceRecordPath("databases", database)));
      out.write(" > \r\n");
      out.write("       ");
      out.print(W.labelLink("Tables", R.resourcePath(resource)));
      out.write("</p>\r\n");
      out.write("</div>\r\n");
      out.write("\r\n");
      out.write("<h3>Tables (");
      out.print(O.count(tableInfos));
      out.write(")</h3>\r\n");
      out.write("\r\n");
      out.write("<table class=\"sTable\">\r\n");
      out.write("    <tr>\r\n");
      out.write("        <th>Name</th>\r\n");
      out.write("        <th>Catalog</th>\r\n");
      out.write("        <th>Schema</th>\r\n");
      out.write("    </tr>\r\n");
for (Iterator it = O.iteratorOf(tableInfos); it.hasNext();) { 
        TableInfo ti = (TableInfo)it.next();

      out.write("\r\n");
      out.write("    <tr class=\"");
      out.print(W.cycle("odd, even"));
      out.write("\">\r\n");
      out.write("        <td>");
      out.print(W.labelLink(ti.getName(), R.nestedResourceRecordPath("databases", database, "tables", ti.getName())));
      out.write("</td>\r\n");
      out.write("        <td>");
      out.print(ti.getCatalog());
      out.write("</td>\r\n");
      out.write("        <td>");
      out.print(ti.getSchema());
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
