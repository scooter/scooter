<form action="<%=request.getContextPath()%>/signon/authenticate" method="POST">
    <p>Please enter your username and password.</p>
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
