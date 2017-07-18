Overview:
---------
This is a Spring boot web application to upload file in a file system. The metadata for the files are stored in a properties file. An HTML form has been provided for file upload and listing the upload files.

Below are the REST End points:

  1. /rest/upload?file={file}&author={author}&date={date} [POST]
  2. /rest/files [GET]
  3. /rest/files?author={author}&date={date} [GET]
  4. /rest/file?id-{uuid} [GET]
  5. /rest/file/{id} [GET]

Technologies used:
------------------
  Spring boot
  Angular JS
  Maven


How to run the application
--------------------------
Using maven:

1. Unzip the code and navigate to the folder in your machine.
2. Execute this command: mvn spring-boot:run

From IDE:

1. Import the project into Eclipse
2. Run as Spring boot application from the run configurations

From Command Line:

1. Navigate to the jar file folder and execute this command:
java -jar FileUpload-0.0.1-SNAPSHOT.jar


Screenshot are provided.
