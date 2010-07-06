package org.apache.jsp.WEB_002dINF.views.builtin.databrowser.tables;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import java.util.Iterator;
import com.scooterframework.orm.sqldataexpress.object.ColumnInfo;
import com.scooterframework.orm.sqldataexpress.object.RowInfo;
import com.scooterframework.web.util.O;
import com.scooterframework.web.util.R;
import com.scooterframework.web.util.T;
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

String database = (String)request.getAttribute("database");
String table = (String)request.getAttribute("table");
String recordsCount = (String)request.getAttribute("records_count");
String resource = "records";
String[] parentResourceNames = {"databases", "tables"};
String[] parentRestfuls = {database, table};
Iterator cols = O.columns((RowInfo)W.get("header"));

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
      out.write(" >\r\n");
      out.write("       ");
      out.print(W.labelLink("Tables", R.nestedResourcePath("databases", database, "tables")));
      out.write(" > \r\n");
      out.write("       ");
      out.print(table);
      out.write("</p>\r\n");
      out.write("</div>\r\n");
      out.write("\r\n");
      out.write("<h3>");
      out.print(T.pluralize(recordsCount, "record"));
      out.write(" in Table ");
      out.print(table);
      out.write("</h3>\r\n");
      out.write("\r\n");
      out.write("<table class=\"sTable\">\r\n");
      out.write("<tr>\r\n");
      out.write("  <th>Column</th>\r\n");
      out.write("  <th>Index</th>\r\n");
      out.write("  <th>PK</th>\r\n");
      out.write("  <th>Auto</th>\r\n");
      out.write("  <th>Java Class</th>\r\n");
      out.write("  <th>SQL Type</th>\r\n");
      out.write("  <th>Type Name</th>\r\n");
      out.write("  <th>Size</th>\r\n");
      out.write("  <th>Scale</th>\r\n");
      out.write("  <th>Precision</th>\r\n");
      out.write("  <th>Null</th>\r\n");
      out.write("</tr>\r\n");

int index = 0;
while(cols.hasNext()) {
    ColumnInfo ci = (ColumnInfo)cols.next();
    index++;

      out.write("\r\n");
      out.write("<tr class=\"");
      out.print(W.cycle("odd, even"));
      out.write("\">\r\n");
      out.write("  <td>");
      out.print(ci.getColumnName().toLowerCase());
      out.write("</td>\r\n");
      out.write("  <td align=\"right\">");
      out.print(index);
      out.write("</td>\r\n");
      out.write("  <td align=\"center\">");
      out.print(T.booleanWord(ci.isPrimaryKey(), "Y", ""));
      out.write("</td>\r\n");
      out.write("  <td align=\"center\">");
      out.print(T.booleanWord(ci.isAutoIncrement(), "Y", ""));
      out.write("</td>\r\n");
      out.write("  <td>");
      out.print(ci.getColumnClassName());
      out.write("</td>\r\n");
      out.write("  <td align=\"right\">");
      out.print(ci.getSQLDataType());
      out.write("</td>\r\n");
      out.write("  <td>");
      out.print(ci.getColumnTypeName());
      out.write("</td>\r\n");
      out.write("  <td align=\"right\">");
      out.print(ci.getColumnDisplaySize());
      out.write("</td>\r\n");
      out.write("  <td align=\"right\">");
      out.print(ci.getPrecision());
      out.write("</td>\r\n");
      out.write("  <td align=\"right\">");
      out.print(ci.getScale());
      out.write("</td>\r\n");
      out.write("  <td align=\"center\">");
      out.print(T.booleanWord(ci.isNull(), "Y", ""));
      out.write("</td>\r\n");
      out.write("</tr>\r\n");
}
      out.write("\r\n");
      out.write("</table>\r\n");
      out.write("\r\n");
      out.write("<p class=\"multilink\">\r\n");
      out.print(W.labelLink("Add New Record", R.addNestedResourcePath(parentResourceNames, parentRestfuls, resource)));
      out.write("&nbsp;|\r\n");
      out.print(W.labelLink("List", R.nestedResourcePath(parentResourceNames, parentRestfuls, resource)));
      out.write("&nbsp;|\r\n");
      out.print(W.labelLink("Paged List", R.resourcePath(resource) + "?paged=true"));
      out.write("\r\n");
      out.write("</p>\r\n");
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
