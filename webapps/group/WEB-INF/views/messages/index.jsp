<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page contentType="text/html; charset=UTF-8"%>

<%@ page import="
	java.util.Iterator,
	com.scooterframework.orm.sqldataexpress.object.RESTified,
	com.scooterframework.web.util.O,
	com.scooterframework.web.util.R,
	com.scooterframework.web.util.T,
	com.scooterframework.web.util.W"
%>

<h2><%=T.pluralize(O.count("messages"), "message")%></h2>
<%=W.errorMessage("message")%>

<table class="sTable">
    <tr>
        <th>Cid</th>
        <th>Eid</th>
        <th>A</th>
        <th>B</th>
        <th>C</th>
        <th>D</th>
        <th>E</th>
        <th>F</th>
        <th>Employeename</th>
        <th>G</th>
        <th>H</th>
        <th>I</th>
        <th>J</th>
        <th>K</th>
        <th>L</th>
        <th>M</th>
        <th>N</th>
        <th>O</th>
        <th>P</th>
        <th>Q</th>
        <th>R</th>
        <th>S</th>
        <th>T</th>
        <th>U</th>
        <th>V</th>
        <th>W</th>
        <th>X</th>
        <th>Y</th>
        <th>Z</th>
        <th>Aa</th>
        <th>Bb</th>
        <th>Cc</th>
        <th>Dd</th>
        <th>Ee</th>
        <th>Ff</th>
        <th>Gg</th>
        <th>Hh</th>
        <th>Ii</th>
        <th>Jj</th>
        <th>Kk</th>
        <th>Ll</th>
        <th>Mm</th>
        <th>Nn</th>
        <th>Oo</th>
        <th>Pp</th>
        <th>Qq</th>
        <th>Rr</th>
        <th>Ss</th>
        <th>Tt</th>
        <th>Uu</th>
        <th>Vv</th>
        <th>Ww</th>
        <th>Xx</th>
        <th>Yy</th>
        <th>Zz</th>
        <th>A1</th>
        <th>A2</th>
        <th>A3</th>
        <th>A4</th>
        <th></th>
    </tr>
<%
for (Iterator it = O.iteratorOf("messages"); it.hasNext();) {
    RESTified message = (RESTified)it.next();
%>
    <tr class="<%=W.cycle("odd, even")%>">
        <td><%=O.hp(message, "cid")%></td>
        <td><%=O.hp(message, "eid")%></td>
        <td><%=O.hp(message, "a")%></td>
        <td><%=O.hp(message, "b")%></td>
        <td><%=O.hp(message, "c")%></td>
        <td><%=O.hp(message, "d")%></td>
        <td><%=O.hp(message, "e")%></td>
        <td><%=O.hp(message, "f")%></td>
        <td><%=O.hp(message, "employeename")%></td>
        <td><%=O.hp(message, "g")%></td>
        <td><%=O.hp(message, "h")%></td>
        <td><%=O.hp(message, "i")%></td>
        <td><%=O.hp(message, "j")%></td>
        <td><%=O.hp(message, "k")%></td>
        <td><%=O.hp(message, "l")%></td>
        <td><%=O.hp(message, "m")%></td>
        <td><%=O.hp(message, "n")%></td>
        <td><%=O.hp(message, "o")%></td>
        <td><%=O.hp(message, "p")%></td>
        <td><%=O.hp(message, "q")%></td>
        <td><%=O.hp(message, "r")%></td>
        <td><%=O.hp(message, "s")%></td>
        <td><%=O.hp(message, "t")%></td>
        <td><%=O.hp(message, "u")%></td>
        <td><%=O.hp(message, "v")%></td>
        <td><%=O.hp(message, "w")%></td>
        <td><%=O.hp(message, "x")%></td>
        <td><%=O.hp(message, "y")%></td>
        <td><%=O.hp(message, "z")%></td>
        <td><%=O.hp(message, "aa")%></td>
        <td><%=O.hp(message, "bb")%></td>
        <td><%=O.hp(message, "cc")%></td>
        <td><%=O.hp(message, "dd")%></td>
        <td><%=O.hp(message, "ee")%></td>
        <td><%=O.hp(message, "ff")%></td>
        <td><%=O.hp(message, "gg")%></td>
        <td><%=O.hp(message, "hh")%></td>
        <td><%=O.hp(message, "ii")%></td>
        <td><%=O.hp(message, "jj")%></td>
        <td><%=O.hp(message, "kk")%></td>
        <td><%=O.hp(message, "ll")%></td>
        <td><%=O.hp(message, "mm")%></td>
        <td><%=O.hp(message, "nn")%></td>
        <td><%=O.hp(message, "oo")%></td>
        <td><%=O.hp(message, "pp")%></td>
        <td><%=O.hp(message, "qq")%></td>
        <td><%=O.hp(message, "rr")%></td>
        <td><%=O.hp(message, "ss")%></td>
        <td><%=O.hp(message, "tt")%></td>
        <td><%=O.hp(message, "uu")%></td>
        <td><%=O.hp(message, "vv")%></td>
        <td><%=O.hp(message, "ww")%></td>
        <td><%=O.hp(message, "xx")%></td>
        <td><%=O.hp(message, "yy")%></td>
        <td><%=O.hp(message, "zz")%></td>
        <td><%=O.hp(message, "a1")%></td>
        <td><%=O.hp(message, "a2")%></td>
        <td><%=O.hp(message, "a3")%></td>
        <td><%=O.hp(message, "a4")%></td>
        <td nowrap>
            <%=W.labelLink("show", R.resourceRecordPath("messages", message))%>
            <%=W.labelLink("edit", R.editResourceRecordPath("messages", message))%>
            <%=W.labelLink("delete", R.resourceRecordPath("messages", message), "confirm:'Are you sure?'; method:delete")%>
        </td>
    </tr>
<%}%>
</table>

<br />

<%=W.labelLink("Add message", R.addResourcePath("messages"))%>|
<%=W.labelLink("Paged List", R.resourcePath("messages") + "?paged=true")%>
