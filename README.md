# FSTR-Rest-API
REST API for FSTR mobile application

**1. DESCRIPTION**

The REST API for a mobile application of Federation of Sports Tourism of Russia (FSTR).
The API receives the new montain pass data submitted buy a user via mobile application and posts it to the database of FSTR.

**2. USED TOOLS AND TECHNOLOGIES**

Spring Boot version 2.6.3,

JDK version 1.8 or later,

Spring Framework,

Hibernate,

Maven,

PostgreSQL

**3. PROJECT STRUCTURE AND FUNCTIONALITY DESCRIPTION**

![image](https://user-images.githubusercontent.com/90723839/156062002-411a0cc1-0eb8-4c6c-a734-b8e0edfe6c1f.png)

The current version of the API allows:

 a) to add new mountain pass data to the database table "peraval_added" and images to to the database table "peraval_images" -  @PostMapping("/pereval_added").
 
 b) to get Pass data from the database by pass ID - @GetMapping("/pereval_added/{id}")
 
 c) update Pass data in the database in case the record in in status "new" - @PutMapping("/pereval_added/{id}") (application allows to update all fields except user details)
 
 d) delete images from "peraval_images" table - @DeleteMapping("/pereval_images/{id}"). This method is used in @PutMapping("/pereval_added/{id}") to remove old images from the table and link new images to the Pass record

Class Pass is a model of the "peraval_added" table entry with parameters representing the columns of the table, getter/ setter methods and overridden toString() method.

Class Image is a model of the "peraval_images" table entry with parameters representing the columns of the table, getter/ setter methods and overridden toString() method.

Interface PassRepository extending JpaRepository <Pass, Integer> is a DAO interface providing standard methods save and get data from the database.

Interface ImageRepository extending JpaRepository <Image, Integer> is a DAO interface providing standard methods save and get data from the database.

Class PassController is a controller class processing requests from the mobile application and sending them to the database with PassRepository methods.

@PostMapping("/pereval_added") throw BadRequestException in case the data received from mobile application is not full and therefor is not sufficient to be added
to the database (corresponds to HTTP responce 400 BAD REQUEST) and IOException in case images URL is invalid or cannot be connected.

@PutMapping("/pereval_added/{id}") throw BadRequestException in case

OperationExecutionexception is thrown in case of fail to connect server and corresponds to HTTP status 503 SERVICE UNAVAILABLE.

**4. HOW TO RUN THE API**

You can download an executable jar file here: https://1drv.ms/u/s!Au5Mbiai9-SxhHw7rthtbOLyLk7A?e=Mk0Ubr

Before running the API you have to define Environment variables FSTR_DB_HOST - for database server host (jdbc:postgresql://"your host"), FSTR_DB_PORT - for database server port, FSTR_LOGIN - for database username, FSTR_PASS - for database password. Please note API will be able to connect only database named "pereval"

Once API is running it will connect to http://localhost:8080/submitData/ where you can send your queries to the API in JSON format.

**4.1** To test the @PostMapping("/pereval_added") please connect to http://localhost:8080/submitData/pereval_added.

JSON format example:

{
    "id": "12211",
    "beautyTitle": "beauty",
    "title": "beautiful pass",
    "other_titles": "",
    "connect": "",
    "add_time": "2021-09-22 13:18:13",
    "user": {
        "id":"5",
        "email":"jjj@gmail.com",
        "phone":"6658298",
        "name":"John",
        "surname":"Johanson",
        "otc":"junior"
    },
    "coords": {
        "latitude": "45.3842",
        "longitude": "7.1525",
        "height": "1200"
    },
    "type": "pass",
    "level": {
        "winter": "",
        "summer": "1?",
        "autumn": "1?",
        "spring": ""
    },
    "images": [
        {
            "url": "https://www.mwallpapers.com/download-image/64",
            "title": "right side"
        },
        {
            "url": "https://www.mwallpapers.com/download-image/819883/1080x2232",
            "title": "front side"
        }
    ]
}

TESTING:

![image](https://user-images.githubusercontent.com/90723839/156900046-1159e8a2-7dc9-4144-a64a-b85b98b50a04.png)

**4.2** Testing @PutMapping("/pereval_added/{id}") (trying to update record with changed user details)

![image](https://user-images.githubusercontent.com/90723839/156900189-8ca8ad65-bba3-4d30-b43a-618d7bade5ca.png)

**4.3** Testing @PutMapping("/pereval_added/{id}") (changing images)

![image](https://user-images.githubusercontent.com/90723839/156900240-236e3c51-4d58-4e88-a428-28bd8283873d.png)



