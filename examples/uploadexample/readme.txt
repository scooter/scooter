--------------------------
How to run the application
--------------------------

>java -jar tools/server.jar examples/uploadexample 8080

Then browse http://localhost:9090/uploadexample/files/index

-----------------------
How is this app created
-----------------------

1. Create an application
>java -jar tools/create.jar uploadexample

2. Start web server
>java -jar tools/server.jar uploadexample 8080

2. Create controller
>java -jar tools/generate.jar uploadexample controller files index upload

3. Implement index() and upload() methods of FilesController.java

4. Update files/index.jsp and files/upload.jsp views

5. Test run the app: http://localhost:9090/uploadexample/files/index
