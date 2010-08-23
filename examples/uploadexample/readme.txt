--------------------------
How to run the application
--------------------------

scooter>java -jar tools/server.jar examples/uploadexample 8080

Then browse http://localhost:8080/uploadexample/files/index

-----------------------
How is this app created
-----------------------

1. Create an application
scooter>java -jar tools/create.jar uploadexample

2. Start web server
scooter>java -jar tools/server.jar uploadexample 8080

2. Create controller
scooter>java -jar tools/generate.jar uploadexample controller files index upload

3. Implement index() and upload() methods of FilesController.java

4. Update files/index.jsp and files/upload.jsp views

5. Test run the app: http://localhost:8080/uploadexample/files/index
