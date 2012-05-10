Mobile mail Sample
=============

This is a Java sample using Vaadin Framework. Add your email account (imap protocol) and read your emails or send one.

This application uses a [java buildpack](https://github.com/heroku/heroku-buildpack-java).

The java buildpack allows to download dependencies, build and run the application directly on the server. You do not need to 
build the application before pushing it on Stackato. It uses [Jetty](http://jetty.codehaus.org/jetty/). Jetty is a lightweight Java application server which includes a Jetty Runner jar. Therefore, the Java Application can be run directly from the java command and can be passed a war file to load right on the command line. An example of this would be:

	java -jar jetty-runner.jar application.war

As it is a Heroku java buildpack, the execution is declared in the Procfile file:

	web:	 java $JAVA_OPTS -jar target/dependency/jetty-runner.jar --port $PORT target/*.war

Important things about this example:
* It does not use Spring. So there is a StackatoServices class that gets database informations and uses it for Hibernate. (Therefore, Hibernate configuration is generated dynamically)
* It read and send emails without any configuration needed.
* It uses Vaadin Touchkit which is mainly targeted for [Webkit-based browsers](http://trac.webkit.org/wiki/Applications%20using%20WebKit). For example, it will not work on Firefox and IE by default. But you can make it work by [adding GWT User Agents in the MobileMailWidgetSet.gwt.xml file](https://vaadin.com/book/-/page/gwt.widgetset.html).

As it is an example, some features are not implemented yet. 

Deploying the Application
-------------------------

To deploy to stackato:

    stackato push -n

You can view the application at the 'Application Deployed URL'.

Building and runnig the Application
------------------------

To build the application, make sure you have [Maven](http://maven.apache.org/ "Maven") installed.
Then, *cd* into the root directory and execute:

	mvn clean package

That will create the *mobilemail-0.0.1-SNAPSHOT.war* file in the 'target' directory.

To run the application:

	mvn jetty:run
