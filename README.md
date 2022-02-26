# FSTR-Rest-API
REST API for FSTR mobile application

**DESCRIPTION**

The REST API for a mobile application of Federation of Sports Tourism of Russia (FSTR).
The API receives the new montain pass data submitted buy a user via mobile application and posts it to the database of FSTR.

**USED TOOLS AND TECHNOLOGIES**

Spring Boot version 2.6.3
JDK version 1.8 or later
Spring Framework
Hibernate
Maven
PostgreSQL

**PROJECT STRUCTURE AND FUNCTIONALITY DESCRIPTION**

![image](https://user-images.githubusercontent.com/90723839/155845799-292701b8-72f0-472d-a4a6-a99b6c3f7d4e.png)

The current version of the API allows to add new mountain pass data to the database table "peraval_added".
Class Pass is a model of the "peraval_added" table entity with parameters representing the columns of the table, getter/ setter methods and overridden toString() method.

Interface PassRepository extending JpaRepository <Pass, Integer> is a DAO interface providing standard methods save and get data from the database.

Class PassController is a controller class processing requests from the mobile application and sending them to the database with PassRepository methods.

BadRequestException is thrown by PassController submitData method in case the data received from mobile application is not full and therefor is not sufficient to be added
to the database and corresponds to HTTP responce 400 BAD REQUEST.
OperationExecutionexception is thrown in case of fail to connect server and corresponds to HTTP status 503 SERVICE UNAVAILABLE.

