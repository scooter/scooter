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

<h2><%=T.pluralize(O.count("comments"), "comment")%></h2>
<%=W.errorMessage("comment")%>

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
for (Iterator it = O.iteratorOf("comments"); it.hasNext();) {
    RESTified comment = (RESTified)it.next();
%>
    <tr class="<%=W.cycle("odd, even")%>">
        <td><%=O.hp(comment, "cid")%></td>
        <td><%=O.hp(comment, "eid")%></td>
        <td><%=O.hp(comment, "a")%></td>
        <td><%=O.hp(comment, "b")%></td>
        <td><%=O.hp(comment, "c")%></td>
        <td><%=O.hp(comment, "d")%></td>
        <td><%=O.hp(comment, "e")%></td>
        <td><%=O.hp(comment, "f")%></td>
        <td><%=O.hp(comment, "employeename")%></td>
        <td><%=O.hp(comment, "g")%></td>
        <td><%=O.hp(comment, "h")%></td>
        <td><%=O.hp(comment, "i")%></td>
        <td><%=O.hp(comment, "j")%></td>
        <td><%=O.hp(comment, "k")%></td>
        <td><%=O.hp(comment, "l")%></td>
        <td><%=O.hp(comment, "m")%></td>
        <td><%=O.hp(comment, "n")%></td>
        <td><%=O.hp(comment, "o")%></td>
        <td><%=O.hp(comment, "p")%></td>
        <td><%=O.hp(comment, "q")%></td>
        <td><%=O.hp(comment, "r")%></td>
        <td><%=O.hp(comment, "s")%></td>
        <td><%=O.hp(comment, "t")%></td>
        <td><%=O.hp(comment, "u")%></td>
        <td><%=O.hp(comment, "v")%></td>
        <td><%=O.hp(comment, "w")%></td>
        <td><%=O.hp(comment, "x")%></td>
        <td><%=O.hp(comment, "y")%></td>
        <td><%=O.hp(comment, "z")%></td>
        <td><%=O.hp(comment, "aa")%></td>
        <td><%=O.hp(comment, "bb")%></td>
        <td><%=O.hp(comment, "cc")%></td>
        <td><%=O.hp(comment, "dd")%></td>
        <td><%=O.hp(comment, "ee")%></td>
        <td><%=O.hp(comment, "ff")%></td>
        <td><%=O.hp(comment, "gg")%></td>
        <td><%=O.hp(comment, "hh")%></td>
        <td><%=O.hp(comment, "ii")%></td>
        <td><%=O.hp(comment, "jj")%></td>
        <td><%=O.hp(comment, "kk")%></td>
        <td><%=O.hp(comment, "ll")%></td>
        <td><%=O.hp(comment, "mm")%></td>
        <td><%=O.hp(comment, "nn")%></td>
        <td><%=O.hp(comment, "oo")%></td>
        <td><%=O.hp(comment, "pp")%></td>
        <td><%=O.hp(comment, "qq")%></td>
        <td><%=O.hp(comment, "rr")%></td>
        <td><%=O.hp(comment, "ss")%></td>
        <td><%=O.hp(comment, "tt")%></td>
        <td><%=O.hp(comment, "uu")%></td>
        <td><%=O.hp(comment, "vv")%></td>
        <td><%=O.hp(comment, "ww")%></td>
        <td><%=O.hp(comment, "xx")%></td>
        <td><%=O.hp(comment, "yy")%></td>
        <td><%=O.hp(comment, "zz")%></td>
        <td><%=O.hp(comment, "a1")%></td>
        <td><%=O.hp(comment, "a2")%></td>
        <td><%=O.hp(comment, "a3")%></td>
        <td><%=O.hp(comment, "a4")%></td>
        <td nowrap>
            <%=W.labelLink("show", R.resourceRecordPath("comments", comment))%>
            <%=W.labelLink("edit", R.editResourceRecordPath("comments", comment))%>
            <%=W.labelLink("delete", R.resourceRecordPath("comments", comment), "confirm:'Are you sure?'; method:delete")%>
        </td>
    </tr>
<%}%>
</table>

<br />

<%=W.labelLink("Add comment", R.addResourcePath("comments"))%>|
<%=W.labelLink("Paged List", R.resourcePath("comments") + "?paged=true")%>
