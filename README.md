**1.	APPLICATION DESCRIPTION**

This is the REST API that receives a data from the client and saves it to the database of Federation of Sports Tourism (FSTR).

FSTR database name is “pereval” and it has four tables. ER Diagram:

![image](https://user-images.githubusercontent.com/90723839/163256971-fe32928b-29bf-46ec-8ff7-12341dd10b44.png)

 
Client is a FSTR mobile application where users can submit information about a new mountain pass (e.g. mountain pass name, coordinates, difficulty level, also application user details and attach pictures of the mountain pass) by filling in fields in the application.

Data arrives to the API in json format (pls check json example in the APPENDIX below) and gets submitted to database tables mount_pass_added and mount_pass_images.

**2.	USED TOOLS AND TECHNOLOGIES**

Spring Boot version 2.6.3,

JDK version 1.8 or later,

Spring Framework,

Hibernate,

Maven,

PostgreSQL

**3.	FUNCTIONALITY EXPLANATION**
 
  **3.1	Connection to the database**
  
In order every API user could connect database with own host/ username/ password, API establishes connection using environment variables FSTR_DB_HOST, FSTR_DB_PORT, FSTR_DB_LOGIN, FSTR_DB_PASS:

![image](https://user-images.githubusercontent.com/90723839/163257384-7c4b07ba-4d76-4fc9-bdfe-a814e4c92a90.png)

For example:
 
 ![image](https://user-images.githubusercontent.com/90723839/163257428-15daa67f-559a-4c6c-bc0d-9e353e5ae4a8.png)

**3.2	Main functions**

**@PostMapping("/mount_pass_added")** Parses json received from the client:

•	Checks if mandatory data (user details and mountain pass coordinates) are available

•	Saves images to the mount_pass_images table (using @PostMapping("/mount_pass_images"))

•	Saves time of submitting the data (current time), information about mountain pass (in json format), saved images titles and IDs from mount_pass_images table (in json format), status of the added record (always added as “new”)

**@GetMapping("/mount_pass_added/{id}")** Returns a record from mount_pass_added table by its ID

**@GetMapping("/mount_pass_added/{id}/status")** Returns status of the record from mount_pass_added table by its ID

**@PutMapping("/mount_pass_added/{id}")** Receives an updated record from the client. If a record in mount_pass_added table is in status “new”:

•	Checks if updated record user data matches the database record 

•	Checks if mountain pass coordinates are available in the updated record

If above criterias are satisfied deletes all images of the mountain pass from mount_pass_images table (using **@DeleteMapping("/mount_pass_images/{id}")**) and updates records with new data.

**4.	TESTING**

By default the API makes connection to http://localhost:8080/submitData/ where you can send your queries to the API in JSON format.

**@PostMapping("/mount_pass_added")** test

![image](https://user-images.githubusercontent.com/90723839/163257773-844f9b3e-27d3-4e2b-b2b9-b0bfe7fce9c2.png)

Testing **@PutMapping("/mount_pass_added/{id}")** (trying to update record with changed user details to check error handling)
 
 ![image](https://user-images.githubusercontent.com/90723839/163257870-e8197b0c-e0c2-4e5d-81b8-3741f004be7a.png)

Testing **@PutMapping("/mount_pass_added/{id}")** (changing latitude and images)
 
![image](https://user-images.githubusercontent.com/90723839/163257925-8260d406-1588-47f1-bfed-c32ebb7bf638.png)

**5.	APPENDIX**
An example of json received from the client

    {
    
    "id": "12211",
    
    "beautyTitle": "Nice pass",
    
    "title": "Rimutaka Saddle",
    
    "other_titles": "",
    
    "connect": "",
    
    "add_time": "2021-09-22 13:18:13",
    
    "user": {
        
        "id":"5",
    
        "email":"john@gmail.com",
        
        "phone":"6658298",
        
        "name":"John",
        
        "surname":"Lennon"
    
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
            
            "url": "https://www.mwallpapers.com/download-image/648946/1080x864",
            
            "title": "Front view"
        
        },
        
        {
            
            "url": "https://www.mwallpapers.com/download-image/819883/1080x2232",
            
            "title": "Top view"
        
        }
    
    ]
    
    }

