OAI-PMH Server
==========
This project contains a data driver and configuration for Proai, a repository-neutral, OAI-PMH server: <a href="http://proai.sourceforge.net">http://proai.sourceforge.net</a>

This allows institutions to focus on providing a OAI-PMH feed rather than maintaining a OAI-PMH server codebase.

Proai requires a database with JDBC drivers. The data driver also requires a JDBC-available database. Currently, the server has been tested with Tomcat, using Postgres and Derby as backends.    

Installing
====
You can get the latest <a href="http://dev.redboxresearchdata.com.au/nexus/service/local/artifact/maven/redirect?r=snapshots&g=au.com.redboxresearchdata.oai&a=oai-server&v=LATEST&e=war">snapshot WARs in here</a> and install them in your application server.

Prior to running the server, you will need to create the backend database and the user specified in the <a href="https://github.com/redbox-mint/oai-server/blob/master/src/main/java/proai.properties">proai.properties</a> file. By default, you will need to create the ff. on your localhost Postgres server:

	DB: oaiserver
	User: proai
	Password: proai

Of course, you can modify the above values and secure your DB server as appropriate. :)

Customizing
====
The quickest way to customize is to modify the proai.properties found in the "classes" subdirectory of your deployed application. To apply your changes, the web application will have to be restarted.  

If using Tomcat, it is found in your "tomcat-webapps/application/WEB-INF/classes" directory, where "tomcat-webapps" refers to the directory where Tomcat deploys applications, and "application" refers to the application name, likely "oai-server".

You can use any database backend, as long as you modify the appropriate property values and provide the required jar files, to suit your choice.

Building
====
To successfully build this project, you will need to deploy "proai.war" and "proai.jar" to your local Maven repository, using the ff. commands:
 
	mvn install:install-file -DgroupId=proai -DartifactId=proai -Dversion=1.1.1 -Dpackaging=jar -Dfile=proai.jar
	mvn install:install-file -DgroupId=proai -DartifactId=proai -Dversion=1.1.1 -Dpackaging=war -Dfile=proai.war

Please modify the -Dfile property to the appropriate location of the files you had <a href="http://proai.sourceforge.net#download">downloaded from Proai.</a>

