# FSTR-Rest-API
REST API for FSTR mobile application

**DESCRIPTION**

The REST API for a mobile application of Federation of Sports Tourism of Russia (FSTR).
The API receives the new montain pass data submitted buy a user via mobile application and posts it to the database of FSTR.

**USED TOOLS AND TECHNOLOGIES**

Spring Boot version 2.6.3,

JDK version 1.8 or later,

Spring Framework,

Hibernate,

Maven,

PostgreSQL

**PROJECT STRUCTURE AND FUNCTIONALITY DESCRIPTION**

![image](https://user-images.githubusercontent.com/90723839/156062002-411a0cc1-0eb8-4c6c-a734-b8e0edfe6c1f.png)

The current version of the API allows to add new mountain pass data to the database table "peraval_added" and images to to the database table "peraval_images".

Class Pass is a model of the "peraval_added" table entry with parameters representing the columns of the table, getter/ setter methods and overridden toString() method.

Class Image is a model of the "peraval_images" table entry with parameters representing the columns of the table, getter/ setter methods and overridden toString() method.

Interface PassRepository extending JpaRepository <Pass, Integer> is a DAO interface providing standard methods save and get data from the database.

Interface ImageRepository extending JpaRepository <Image, Integer> is a DAO interface providing standard methods save and get data from the database.

Class PassController is a controller class processing requests from the mobile application and sending them to the database with PassRepository methods.

BadRequestException is thrown by PassController submitData method in case the data received from mobile application is not full and therefor is not sufficient to be added
to the database and corresponds to HTTP responce 400 BAD REQUEST.

OperationExecutionexception is thrown in case of fail to connect server and corresponds to HTTP status 503 SERVICE UNAVAILABLE.

**HOW TO RUN THE API**

You can download an executable jar file here: https://1drv.ms/u/s!Au5Mbiai9-SxhHtLJJ1CBwRj_FGi?e=MAxtQv

Before running the API you have to define Environment variables FSTR_DB_HOST - for database server host (jdbc:postgresql://"your host"), FSTR_DB_PORT - for database server port, FSTR_LOGIN - for database username, FSTR_PASS - for database password. Please note API will be able to connect only database named "pereval"

Once API is running it will connect to http://localhost:8080/api/ where you can send your queries to the API in JSON format.

To test the PostMapping (add new entry to "peraval_added" and images to "peraval_images" table) please connect to http://localhost:8080/api/pereval_added.

JSON format example:

{
    "id": "100",
    "beautyTitle": "пер. ",
    "title": "Пхия",
    "other_titles": "Триев",
    "connect": "",
    "add_time": "2021-09-22 13:18:13",
    "user": {},
    "coords": {
        "latitude": "45.3842",
        "longitude": "7.1525",
        "height": "1111"
    },
    "type": "pass",
    "level": {
        "winter": "",
        "summer": "1А",
        "autumn": "1А",
        "spring": ""
    },
    "images": [
        {
            "url": "",
            "title": "??????. ???? ?1"
        },
        {
            "url": "https://www.mwallpapers.com/download-image/819883/1080x2232",
            "title": "??????. ???? ?2"
        }
    ]
}


