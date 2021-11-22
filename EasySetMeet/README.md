# Backend package for EasySetMeet

## How to develop

* Have IntelliJ IDEA installed
* Open this package in IntelliJ
* Try go to `src/main/java/com.gatech.ihi.app.http/HttpServer`, and click on the green run icon next to `public class HttpServer {` see if you are able to run the server

## How to setup your IntelliJ IDEA

* You need the following plugins installed:
  * Lombok
  * CheckStyle

## How to setup your dev environment

* If you are using Windows:
  * Go to 'Edit your system environment variables'
  * Click on 'Environment Variables' at the bottom right corner
  * Under 'System Variable':
    * Create an entry
      * Key is `JAVA_HOME`
      * Value is your java path, normally `C:\Program Files\Java\jdk<some version>`
* If you are using *nix, including Mac:
  * Find you java path
  * Run `export JAVA_HOME='<java path>'`
  
## How to run in command line
* To run the application: `./gradlew.bat run` for windows, `./gradle run` for *nix
* To build a jar: `./gradlew.bat build` for windows, `./gradle build` for *nix
  * The jar is located at `build/libs/EasySetMeet-1.0.jar`
  
## How to run unit tests and functional tests in IntelliJ

* You need to add 2 environment variables in run configuration, please google for instructions.
  * AWS_ACCESS_KEY_ID=xxx
  * AWS_SECRET_ACCESS_KEY=xxx