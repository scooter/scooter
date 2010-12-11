<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.scooterframework.orm.activerecord.ActiveRecord,
                 com.scooterframework.web.util.O,
                 com.scooterframework.web.util.W,
                 com.scooterframework.security.LoginHelper"%>

<%
ActiveRecord user = (ActiveRecord)W.get("user");
if (user == null) user = LoginHelper.loginUser();
Object username = (user == null)?"":user.getField("username");
%>

<b>Name: </b> <%=username%>

<br />

<table>
  <tr>
    <td><%=O.hp(user, "followings_count")%></td>
    <td><%=O.hp(user, "followers_count")%></td>
  </tr>
  <tr>
    <td><%=W.labelLink("followings", "/" + username + "/followings")%></td>
    <td><%=W.labelLink("followers", "/" + username + "/followers")%></td>
  </tr>
</table>

<b>Tweets</b> <%=O.hp(user, "tweets_count")%>