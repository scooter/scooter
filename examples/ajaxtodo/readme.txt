AJAX-backed TODO List Application
=================================

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

The dash "-" in front of each item will be translated to <li> in Markdown wiki.
For more information on Markdown wiki, visit http://daringfireball.net/projects/markdown/basics

--------------------------
How to run the application
--------------------------

Run the app with default port 8080
scooter>java -jar tools/server.jar examples/ajaxtodo

Browse http://localhost:8080/ajaxtodo to see the AJAX link to show current time.
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

3. Create Ajax-backed CRUD on posts
scooter>java -jar tools/generate.jar ajaxtodo scaffold-ajax entry

4. Browser open url: http://localhost:8080/ajaxtodo/entries and add an entry

5. Modify the index.jsp under webapps/ajaxtodo/WEB-INF/views/entries until you like it.
