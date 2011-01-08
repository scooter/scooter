AJAX Examples
=============
This example app includes the following examples:

Example 1: Regular non-AJAX link vs. AJAX link
----------------------------------------------
This example demonstrates how easy it is to convert a regular HTTP link to an AJAX link.

Example 2: Retrieving and displaying JSON data
-------------------------------------------
This example demonstrates how easy it is to use JSON data retrieved from the server.
Please notice that ajaxexamples/static/javascripts/app.js is modified to customize the 
handling of JSON data. Open app.js to see how JSON data is used.

Example 3: A complete sample app -- AJAX-Backed Wiki-Powered TODO List
----------------------------------------------------------------------
This example demonstrates how easy it is to create an ajax-backed application 
by using Scooter framework.

This TODO-list example also is backed by Markdown wiki. Therefore, you 
can use Markdown syntax when entering details. 

For example:
  Subject: 
    Before going home
  Details: 
    - Buy rose
    - Pick up kids

The dash "-" in front of each item will be translated to <ul><li> in Markdown wiki.
For more information on Markdown wiki, visit http://daringfireball.net/projects/markdown/basics

--------------------------
How to run the application
--------------------------

Run the app with default port 8080
scooter>java -jar tools/server.jar examples/ajaxtodo

Browse http://localhost:8080/ajaxtodo to see links to the examples.
Browse http://localhost:8080/ajaxtodo/entries to perform CRUD on entries through AJAX.

-------------------------------
How is this application created
-------------------------------

0. Create database in MySQL
scooter>mysql -u root < examples/ajaxtodo/static/docs/ajaxtodo_development.sql

1. Create an application
scooter>java -jar tools/create.jar ajaxtodo

Site admin username/password is admin/welcome.

2. Start web server
scooter>java -jar tools/server.jar ajaxtodo

3. Create Ajax-backed TODO list application
scooter>java -jar tools/generate.jar ajaxtodo scaffold-ajax entry

4. Browser open url: http://localhost:8080/ajaxtodo/entries and add an entry

5. Modify the index.jsp under webapps/ajaxtodo/WEB-INF/views/entries until you like it.
