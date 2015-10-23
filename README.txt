Install
-----------------

This application was designed to be lightweight with little or no setup involved. Therefore, there is nothing required
to install the application. You can simply execute the jar (with dependencies) and access the web page as described
in the "Run" section below.

If you would prefer to use a different database, after you install and configure your database, you can modify
the default.properties file to provide the jdbc class and jdbc url required; these are properties jdbcClass and jdbcUrl
respectively. Then run the jar (without dependencies) and supply on the classpath your properties file and required
libraries.

The database included is SQLite (https://www.sqlite.org/) and the web server included is Spark (http://sparkjava.com/).

SQLite Public Domain: https://www.sqlite.org/copyright.html
Spark License: https://github.com/perwendel/spark/blob/master/LICENSE

Run
-----------------

To run this project with its default configuration, simply access the jar with dependencies from the command line as shown:

java -jar ./path-to-philosophy-<version>-jar-with-dependencies.jar

For example, at version 1.0-SNAPSHOT:

java -jar ./path-to-philosophy-1.0-SNAPSHOT-jar-with-dependencies.jar

This will start up a web server on your local machine using port 4567. Open a web browser and access the index page:

http://localhost:4567/index

With the default configuration, the application will expect a SQLite file called philosophy.db to exist in the same
directory that you run the application from. If the file does not exist, it will be created and initialized.

Expected
-----------------

This application requires Java 1.8

Loops and Dead Ends
-------------------

The engine that finds the path to philosophy is able to detect when it has hit a dead end without reaching philosophy,
or when it has entered a loop (accessing the same page twice). When detecting either of these scenarios, it backs up
a page and tries a different route by following the second or third link, for example, that it finds. The number of
times the engine will attempt to try a different route before giving up is configurable in the default.properties file
under the property numDeadEnds (must be an integer).

Dead ends and loops are logged to standard out, but not in the final resulting path viewable in the application.