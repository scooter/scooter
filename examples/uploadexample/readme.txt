File Upload Example Application
===============================

This example demonstrates how easy it is to create a file upload application 
by using Scooter framework.

--------------------------
How to run the application
--------------------------

Run the app with default port 8080
scooter>java -jar tools/server.jar examples/uploadexample

Then browse http://localhost:8080/uploadexample/files/index

-------------------------------
How is this application created
-------------------------------

1. Create an application
scooter>java -jar tools/create.jar uploadexample

Site admin username/password is admin/welcome.

2. Start web server
scooter>java -jar tools/server.jar uploadexample

3. Create controller
scooter>java -jar tools/generate.jar uploadexample controller files index upload

4. Implement index() and upload() methods of FilesController.java

5. Update files/index.jsp and files/upload.jsp views

6. Test run the app: http://localhost:8080/uploadexample/files/index
