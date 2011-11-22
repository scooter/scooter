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
        <th>A</th>
        <th>B</th>
        <th>C</th>
        <th>D</th>
        <th>E</th>
        <th>F</th>
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
        <th>Aaa</th>
        <th>Bbb</th>
        <th>Ccc</th>
        <th>Ddd</th>
        <th>Eee</th>
        <th>Fff</th>
        <th>Ggg</th>
        <th>Hhh</th>
        <th>Iii</th>
        <th>Jjj</th>
        <th>Kkk</th>
        <th>Lll</th>
        <th>Mmm</th>
        <th>Nnn</th>
        <th>Ooo</th>
        <th>Ppp</th>
        <th>Qqq</th>
        <th>Rrr</th>
        <th>Sss</th>
        <th>Ttt</th>
        <th>Uuu</th>
        <th>Vvv</th>
        <th>Www</th>
        <th>Xxx</th>
        <th>Yyy</th>
        <th>Zzz</th>
        <th>Aaaa</th>
        <th>Bbbb</th>
        <th>Cccc</th>
        <th>Dddd</th>
        <th>Eeee</th>
        <th>Ffff</th>
        <th>Gggg</th>
        <th>Hhhh</th>
        <th>Iiii</th>
        <th>Jjjj</th>
        <th>Kkkk</th>
        <th>Llll</th>
        <th>Mmmm</th>
        <th>Nnnn</th>
        <th>Oooo</th>
        <th>Pppp</th>
        <th>Qqqq</th>
        <th>Rrrr</th>
        <th>Ssss</th>
        <th>Tttt</th>
        <th></th>
    </tr>
<%
for (Iterator it = O.iteratorOf("messages"); it.hasNext();) {
    RESTified message = (RESTified)it.next();
%>
    <tr class="<%=W.cycle("odd, even")%>">
        <td><%=O.hp(message, "a")%></td>
        <td><%=O.hp(message, "b")%></td>
        <td><%=O.hp(message, "c")%></td>
        <td><%=O.hp(message, "d")%></td>
        <td><%=O.hp(message, "e")%></td>
        <td><%=O.hp(message, "f")%></td>
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
        <td><%=O.hp(message, "aaa")%></td>
        <td><%=O.hp(message, "bbb")%></td>
        <td><%=O.hp(message, "ccc")%></td>
        <td><%=O.hp(message, "ddd")%></td>
        <td><%=O.hp(message, "eee")%></td>
        <td><%=O.hp(message, "fff")%></td>
        <td><%=O.hp(message, "ggg")%></td>
        <td><%=O.hp(message, "hhh")%></td>
        <td><%=O.hp(message, "iii")%></td>
        <td><%=O.hp(message, "jjj")%></td>
        <td><%=O.hp(message, "kkk")%></td>
        <td><%=O.hp(message, "lll")%></td>
        <td><%=O.hp(message, "mmm")%></td>
        <td><%=O.hp(message, "nnn")%></td>
        <td><%=O.hp(message, "ooo")%></td>
        <td><%=O.hp(message, "ppp")%></td>
        <td><%=O.hp(message, "qqq")%></td>
        <td><%=O.hp(message, "rrr")%></td>
        <td><%=O.hp(message, "sss")%></td>
        <td><%=O.hp(message, "ttt")%></td>
        <td><%=O.hp(message, "uuu")%></td>
        <td><%=O.hp(message, "vvv")%></td>
        <td><%=O.hp(message, "www")%></td>
        <td><%=O.hp(message, "xxx")%></td>
        <td><%=O.hp(message, "yyy")%></td>
        <td><%=O.hp(message, "zzz")%></td>
        <td><%=O.hp(message, "aaaa")%></td>
        <td><%=O.hp(message, "bbbb")%></td>
        <td><%=O.hp(message, "cccc")%></td>
        <td><%=O.hp(message, "dddd")%></td>
        <td><%=O.hp(message, "eeee")%></td>
        <td><%=O.hp(message, "ffff")%></td>
        <td><%=O.hp(message, "gggg")%></td>
        <td><%=O.hp(message, "hhhh")%></td>
        <td><%=O.hp(message, "iiii")%></td>
        <td><%=O.hp(message, "jjjj")%></td>
        <td><%=O.hp(message, "kkkk")%></td>
        <td><%=O.hp(message, "llll")%></td>
        <td><%=O.hp(message, "mmmm")%></td>
        <td><%=O.hp(message, "nnnn")%></td>
        <td><%=O.hp(message, "oooo")%></td>
        <td><%=O.hp(message, "pppp")%></td>
        <td><%=O.hp(message, "qqqq")%></td>
        <td><%=O.hp(message, "rrrr")%></td>
        <td><%=O.hp(message, "ssss")%></td>
        <td><%=O.hp(message, "tttt")%></td>
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
