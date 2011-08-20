<%@ page import="com.scooterframework.web.util.W"
%>
<h2>Demo Examples</h2>

<p><b>Example 1</b>: How routes are handled</p>
<ol>
  <li>Click <%=W.labelLink("/hello/index", "/hello/index")%> (Route pattern: <tt>/controller/action</tt>)</li>
  <li>See how it is done in <tt>greeting/WEB-INF/src/greeting/controllers/HelloController.java</tt></li>
  <li>Update method <tt>HelloController.index()</tt> and refresh browser to see changes</li>
  <li>Make a compile error in method <tt>HelloController.index()</tt> and see how Scooter responds</li>
</ol>

<p><b>Example 2</b>: How to pass data to view file</p>
<ol>
  <li>Click <%=W.labelLink("/welcome/sayit", "/welcome/sayit")%></li>
  <li>See how it is done in <tt>greeting/WEB-INF/src/greeting/controllers/WelcomeController.java</tt></li>
  <li>Update method <tt>WelcomeController.sayit()</tt> and refresh browser to see changes</li>
</ol>

<p><b>Example 3</b>: How to handle different HTML Form elements</p>
<ol>
  <li>Click <%=W.labelLink("Online Registration Form Example", "/registration/input")%></li>
  <li>See how it is done in <tt>greeting/WEB-INF/src/greeting/controllers/RegistrationController.java</tt></li>
</ol>