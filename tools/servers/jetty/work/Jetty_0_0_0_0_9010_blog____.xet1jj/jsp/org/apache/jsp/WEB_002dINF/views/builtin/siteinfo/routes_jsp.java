package org.apache.jsp.WEB_002dINF.views.builtin.siteinfo;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import java.util.Iterator;
import java.util.List;
import com.scooterframework.web.util.O;
import com.scooterframework.web.util.T;
import com.scooterframework.web.util.W;
import com.scooterframework.web.route.Route;
import com.scooterframework.web.route.RestRoute;
import com.scooterframework.web.route.RouteConstants;

public final class routes_jsp extends org.apache.jasper.runtime.HttpJspBase
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

List routes = (List)request.getAttribute("routes");

      out.write("\r\n");
      out.write("\r\n");
      out.write("<div id=\"locator\">\r\n");
      out.write("    <p>");
      out.print(W.labelLink("Home", "/"));
      out.write(" > Routes</p>\r\n");
      out.write("</div>\r\n");
      out.write("\r\n");
      out.write("<h3>auto-rest: ");
      out.print(W.get("auto.rest"));
      out.write(", auto-crud: ");
      out.print(W.get("auto.crud"));
      out.write("</h3>\r\n");
      out.write("\r\n");
      out.write("<h3>");
      out.print(T.pluralize(O.count(routes), "route"));
      out.write(" declared for this application</h3>\r\n");
      out.write("\r\n");
      out.write("<table class=\"sTable\">\r\n");
      out.write("    <tr>\r\n");
      out.write("        <th></th>\r\n");
      out.write("        <th>Name</th>\r\n");
      out.write("        <th>Type</th>\r\n");
      out.write("        <th>Allowed Methods</th>\r\n");
      out.write("        <th>URL</th>\r\n");
      out.write("        <th>Controller</th>\r\n");
      out.write("        <th>Controller Class</th>\r\n");
      out.write("        <th>Action</th>\r\n");
      out.write("        <th>Id</th>\r\n");
      out.write("        <th>Format</th>\r\n");
      out.write("        <th>Allowed Formats</th>\r\n");
      out.write("        <th>Singular</th>\r\n");
      out.write("        <th>Namespace</th>\r\n");
      out.write("        <th>Path Prefix</th>\r\n");
      out.write("        <th>Requirements</th>\r\n");
      out.write("        <th>Resource</th>\r\n");
      out.write("    </tr>\r\n");
      out.write("\r\n");

int order = -1;
for (Iterator it = O.iteratorOf(routes); it.hasNext();) {
    order++;

      out.write("\r\n");
      out.write("    <tr class=\"");
      out.print(W.cycle("odd, even"));
      out.write("\">\r\n");

        Route route = (Route)it.next();
        String resource = "";
        if (RouteConstants.ROUTE_TYPE_REST.equals(route.getRouteType())) 
            resource = ((RestRoute)route).getResourceName();

      out.write("\r\n");
      out.write("        <td>");
      out.print(order);
      out.write("</td>\r\n");
      out.write("        <td nowrap=\"nowrap\" align=\"right\">");
      out.print(O.property(route, "name"));
      out.write("</td>\r\n");
      out.write("        <td align=\"right\">");
      out.print(O.property(route, "routeType"));
      out.write("</td>\r\n");
      out.write("        <td align=\"right\">");
      out.print(O.property(route, "allowedMethods"));
      out.write("</td>\r\n");
      out.write("        <td nowrap=\"nowrap\">");
      out.print(O.property(route, "URLPattern"));
      out.write("</td>\r\n");
      out.write("        <td>");
      out.print(O.property(route, "controller"));
      out.write("</td>\r\n");
      out.write("        <td>");
      out.print(O.property(route, "controllerClass"));
      out.write("</td>\r\n");
      out.write("        <td>");
      out.print(O.property(route, "action"));
      out.write("</td>\r\n");
      out.write("        <td>");
      out.print(O.property(route, "id"));
      out.write("</td>\r\n");
      out.write("        <td>");
      out.print(O.property(route, "format"));
      out.write("</td>\r\n");
      out.write("        <td>");
      out.print(O.property(route, "allowedFormats"));
      out.write("</td>\r\n");
      out.write("        <td>");
      out.print(O.property(route, "singular"));
      out.write("</td>\r\n");
      out.write("        <td nowrap=\"nowrap\">");
      out.print(O.property(route, "namespace"));
      out.write("</td>\r\n");
      out.write("        <td nowrap=\"nowrap\">");
      out.print(O.property(route, "pathPrefix"));
      out.write("</td>\r\n");
      out.write("        <td>");
      out.print(O.property(route, "requirements"));
      out.write("</td>\r\n");
      out.write("        <td>");
      out.print(resource);
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
