AJAX-backed Blog Application
============================

This example demonstrates how easy it is to create an ajax-backed application 
by using Scooter framework.

--------------------------
How to run the application
--------------------------

Run the app with default port 8080
scooter>java -jar tools/server.jar examples/ajax-blog

Browse http://localhost:8080/blog to see the AJAX link to show current time.
Browse http://localhost:8080/blog/posts to perform CRUD on posts through AJAX.

-------------------------------
How is this application created
-------------------------------

1. Create an application
scooter>java -jar tools/create.jar blog

Site admin username/password is admin/welcome.

2. Start web server
scooter>java -jar tools/server.jar blog

3. Create Ajax-backed CRUD on posts
scooter>java -jar tools/generate.jar blog scaffold-ajax post
