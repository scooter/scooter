<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page contentType="text/html; charset=UTF-8"%>

<h2>Site Administration</h2>

<form id="siteloginform" name="siteloginform" action="<%=request.getContextPath()%>/admin/signon/authenticate" method="POST">
    <p>Please enter site admin username and password.</p>
    <table border="0" cellspacing="0" cellpadding="2">
        <tr>
            <th align="right"><label for="username">Username:</label></th>
            <td><input id="username" name="username" type="text" /></td>
        </tr>
        <tr>
            <th align="right"><label for="password">Password:</label></th>
            <td><input id="password" name="password" type="password" /></td>
        </tr>
        <tr>
            <th></th>
            <td><input name="submit" type="submit" value="Sign In" /></td>
        </tr>
    </table>
</form>

<script>
document.siteloginform.username.focus();
</script>
