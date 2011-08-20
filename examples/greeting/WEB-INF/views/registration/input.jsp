<h2>Registration Form</h2>

<form name="myform" action="/greeting/registration/process" method="post">

First name: <input type="text" name="firstname" /><br />
Last name:  <input type="text" name="lastname" /><br />
Password:   <input type="password" name="pwd" /><br />
Date of birth: <input type="text" name="dob" size="10" />(YYYY-MM-DD)<br />

Gender: 
<input type="radio" name="gender" value="male" /> Male 
<input type="radio" name="gender" value="female" /> Female<br />

Hobby:
<input type="checkbox" name="hobby" value="Reading" /> Reading
<input type="checkbox" name="hobby" value="Talking" /> Talking
<input type="checkbox" name="hobby" value="Listening" /> Listening<br /><br />

Are you from Mars? 
<input type="radio" name="mars" value="true" /> Yes 
<input type="radio" name="mars" value="false" /> No<br />

Bio:<br />
<textarea name="bio" rows="3" cols="30">I am from Mars.</textarea><br /><br />

Select best framework used (single selection):<br />
<select name="framework">
<option value="asp">ASP.NET</option>
<option value="lamp">LAMP</option>
<option value="rails">Rails</option>
<option value="struts">Struts</option>
<option value="scooter" selected="selected">Scooter</option>
<option value="spring">Spring MVC</option>
</select><br /><br />

Select programming languages used (multiple selection):<br />
<select name="tools" multiple="multiple">
<option value="c++">C++</option>
<option value="java">Java</option>
<option value="php">PHP</option>
<option value="ruby">Ruby</option>
</select><br />

<br />

<input type="submit" value="Submit" />

</form>

<br />