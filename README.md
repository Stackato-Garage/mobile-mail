Mobile mail Sample
=============

This is a Java sample using Vaading Framework.  It does not use Spring so there is a StackatoServices
class that gets database informations and uses it for Hibernate.

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
