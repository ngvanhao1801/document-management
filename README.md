# Bookstore-Ecommerce
An Online Bookstore implemented in Java that provides a platform for seller and buyer to trade.<br>

## Environment
1. Java: __JDK 1.8.0_79__
2. Server: __Tomcat 9__
3. Database: __MySQL 5.1__
4. IDE: __Intellij__

## Deployment
One way to depoly this program is just to put Directory [Bookstore-Ecommerce](https://github.com/minhtuan16/BookStore-Ecommerce) into webapp in Tomcat's or Jetty's Directory, while the other way is for debugging:
### MySQL
1. setup MySQL 5.1 and start MySQL service;
2. create a database named 'Bookstore-Ecommerce' and run.
3. if you want to change the username and password of database, you can rewrite them in OperDB.java.

### Project
1. import [demo](https://github.com/minhtuan16/BookStore-Ecommerce) into IDE;
2. select the JDK bin's installing path for the project while not using JRE;

### Server
1. install Jetty plugin in eclipse;
2. right click the project and click Run As -> Run Jetty. __Be careful that Jetty's version should be '8.x.x', or annotations of WebServlet would not work and servlets' path cannot be found.__
3. you can also change the port and the context of this project(server) in Run As -> Run Configuration.

## Key Points
* use simple __Java Servlet__ to get/forward request and send response.
* use __MVC pattern__ to decouple View(JSP) from Controller(XXXClServlet) and Model(XXXBeanCl and XXXBean).
* create __12 tables__ to undertake the tasks of all the bussinesses.

## For More
If you want to learn more about the development of this project, you can visit (https://github.com/minhtuan16).
