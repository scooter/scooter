<%@ page import="
        com.scooterframework.web.util.W"
%>
<h2><%=W.label("welcome.message")%></h2>

<img src="static/images/pets.png" align="right" style="position:relative;right:30px;">

<ul>
  <li><%=W.labelLink("Find owner", "/findOwners")%></li>
  <li><%=W.labelLink("Display all veterinarians", "/vets")%></li>
  <li><a href="http://www.scooterframework.com/docs/petclinic.html">Tutorial</a></li>
  <li><%=W.labelLink("Documentation", "/static/javadoc/index.html")%></li>
</ul>