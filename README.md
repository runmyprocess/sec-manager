# SEC Protocol Manager
The SEC Protocol Manager is part of the "Secure Enterprise Connector" (SEC) built by Fujitsu RunMyProcess.
The SEC allows the user to access local resources through a secure, encrypted tunnel from RunMyProcess.

The "SEC Protocol Manager" manages data received from the "Data Connector Agent" and determines which adapter it must forward the information to. The "SEC Protocol Manager" also keeps a registry of all adapters and regularly checks the health of each adapter to ensure it is running smoothly.


As of 02/10/2013, the SEC Protocol Manager has only been tested with Jetty 7 standalone. It's basically a Java servlet that runs with the help of the SEC-SDK. The pom.xml dependency for the SDK usually points to the current development SDK. If you want a more stable version, you should download the latest version of the SEC-SDK and import it in your project. The latest version of the SEC-SDK that can be found [here](https://github.com/MalcolmHaslam/RunMyProcess-SEC-SDK).


Install and run suggestion
---------------------------

1. Make sure you have [Java](http://www.oracle.com/technetwork/java/index.html) and [Maven](http://maven.apache.org/) installed on your machine. You must also have the RMP's [JSON](https://github.com/runmyprocess/json/) and the [sec-sdk](https://github.com/runmyprocess/sec-sdk) libraries installed on your local mvn repo.
2. Download **[Jetty](http://www.eclipse.org/jetty/)** standalone (tested on Jetty 7.6.11).
3. Download the **sec-manager** project and run mvn clean install to generate the war file with all dependencies.

Run mvn clean install :

	mvn clean install
	
4. Copy the generated war file in Jetty webapps folder.
5. Create the **Configuration File** (see below).
6. Run Jetty.

When Jetty is running you can test the Manager by navigating on your browser to the port where Jetty is serving the servlet (usually *localhost:8080*). The following message should appear :
 
  The SEC-Protocol Manager is running! Registered Protocols *[protocols registered in the Manager]*
  
  
Configuration file
-------------------

The SEC-Manager expects a config file to exist inside a folder named "configFiles". This folder must be placed on the folder outside of where the Manager is running. For example, on a Jetty configuration, it must be placed next to the Jetty folder. 

**NB :** The relative path can be modified in the ProtocolManager.java file.

The config file inside the "configFiles" folder must be named "manager.config" and must follow this structure :

	#ProtocolManager configuration
	pingPort = 4444
	pingFrequency = 10000

Where :

 * **pingPort** is the port where the Manager will wait for Adapters registration information periodically.
 
 * **pingFrequency** is the time, in milliseconds, that the Manager will wait for a ping from the registered Adapters before declaring an adapter idle.


How it Works
-------------

As mentioned before, the Manager is a JAVA Servlet that listens to a port for GETs and POSTs.

When it receives a GET, it looks into a "registered Adapters" list and returns a message with all the Adapters currently registered.

When the Manager receives a POST, it opens the package and retrieves the name of the intended destination Adapter. It then looks for this Adapter in the "registered Adapters" list and gets all the information about the Adapter's location, availability and communication port. The Manager then creates a thread to forward the information to the Adapter as raw data and wait for a response to send back.

The Manager is always listening to the "registration port". This port is a configurable port that listens to pings from the Adapters. When it receives a call, it will check if the adapter is already registered and if not, will register it. If the Manager does not receive a ping in a configurable amount of time, the Manager will consider that the adapter is idle or not responding, and therefore log the problem and flag the Adapter as "idle".
