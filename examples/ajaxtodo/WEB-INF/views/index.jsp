<%@ page import="
        com.scooterframework.admin.EnvConfig,
        com.scooterframework.web.controller.MainActionServlet,
        com.scooterframework.web.util.W"
%>
<h2><%=W.label("welcome.message")%></h2>
<h4>Have a fun drive!</h4>

<b>Example 1: Regular non-AJAX link vs. AJAX link</b><br/>
<a href="/ajaxtodo/time/current">Show Time (non ajax)</a>
<a href="/ajaxtodo/time/current" data-ajax="true" data-target="#display_time">Show Time (ajax)</a>
<span id="display_time"><i>&lt;&lt;Time will be displayed here&gt;&gt;</i></span>
<br/><br/>

<b>Example 2: Retrieving and showing JSON data</b><br/>
<a href="/ajaxtodo/data/retrieve" data-ajax="true" data-target="#display_data" data-type="json" data-handler="json">Show json data</a>
<span id="display_data"><i>&lt;&lt;JSON data will be displayed below&gt;&gt;</i></span>
<br/><br/>

<b>Example 3: A complete sample app</b><br/>
<p>You need to first turn on the database as specified by ajaxtodo_development in <tt>ajaxtodo/WEB-INF/config/database.properties</tt> and then install the database with <tt>ajaxtodo/static/docs/ajaxtodo_development.sql</tt>.</p>
<a href="/ajaxtodo/entries">My AJAX-Backed Wiki-Powered TODO List</a> 
(using <a href="http://daringfireball.net/projects/markdown/basics" target="markdown">MarkDown</a> wiki syntax)

<br/><br/>

<b>Example 4: Select change example</b><br/>
<p>Select an item from List A and watch changes in List B: the selected item disappeared</p>

<form data-target="#filtered_list" data-handler="html" data-ajax="true" action="/ajaxtodo/list/filter">
<table width="50%">
<tr><th align="left"><b>List A:</b></th><th align="left"><b>List B:</b></th></tr>
<tr>
  <td>
<select name="number" size="10" data-ajax="true">
    <option value="0">0</option>
    <option value="1">1</option>
    <option value="2">2</option>
    <option value="3">3</option>
    <option value="4">4</option>
    <option value="5">5</option>
    <option value="6">6</option>
    <option value="7">7</option>
    <option value="8">8</option>
    <option value="9">9</option>
</select>
  </td>
  <td>
<div id="filtered_list">
<select name="number" size="10">
    <option value="0">0</option>
    <option value="1">1</option>
    <option value="2">2</option>
    <option value="3">3</option>
    <option value="4">4</option>
    <option value="5">5</option>
    <option value="6">6</option>
    <option value="7">7</option>
    <option value="8">8</option>
    <option value="9">9</option>
</select>
</div>
  </td>
</tr>
</table>
</form>