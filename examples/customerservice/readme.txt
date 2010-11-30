Customer Service Application
============================

This is Scooter-powered customer service application.

An old version of customer service video can be seen 
at http://www.scooterframework.com/screencasts.html.

--------------------------
How to run the application
--------------------------

Run the app with default port 8080
scooter>java -jar tools/server.jar examples/customerservice

Run the app installed in user home directory with port 9090
scooter>java -jar tools/server.jar /home/joe/customerservice 9090

-------------------------------
How is this application created
-------------------------------

1. Create an application
scooter>java -jar tools/create.jar customerservice

Site admin username/password is admin/welcome.

2. Start web server (no need to restart web server in the following steps)
scooter>java -jar tools/server.jar customerservice

3. Create database
mysql>source customerservice_development.sql

CREATE TABLE entries (
  id int(11) NOT NULL auto_increment,
  name       varchar(30),
  content    text,
  created_at timestamp,
  PRIMARY KEY  (id)
)

4. Generate scaffold code
>java -jar tools/generate.jar scaffold entry

5. Browse entries: http://localhost:8080/customerservice/entries


--------------------
Add a security login 
--------------------
1. Generate sign on code
>java -jar tools/generate-signon.jar

2. Hook entries with login by adding the following code to EntriesController:
    static {
        filterManagerFor(EntriesController.class).declareBeforeFilter(
            SignonController.class, "loginRequired");
    }
    
3. Refresh URL: http://localhost:8080/customerservice/entries
    You should see a login page appears.


--------------------------------------
Change URL from /entries to /feedbacks
--------------------------------------
1. Open customerservice/WEB-INF/config/routes.properties file

2. Comment out line resources.list=entries. 

3. Add the following line: 
   resources.name.entries=path_alias:feedbacks
   
4. Refresh URL: http://localhost:8080/customerservice/entries
   You should see error: No route is found for "GET /entries"
   
5. Refresh URL: http://localhost:8080/customerservice/feedbacks
   You should be able to browse entries.