Vehicle Management API
A comprehensive REST API for managing vehicle data and providing detailed vehicle information, including pricing and location details.

Features
REST API utilizing main HTTP verbs and functionalities
HATEOAS support for resource linking
Custom API error handling via ControllerAdvice
Swagger for API documentation
HTTP WebClient for external service interaction
MVC testing for robust application validation
Automatic model mapping for ease of data handling
Instructions
TODOs
Complete the TODOs in CarService.java and CarController.java
Enhance CarControllerTest.java with additional tests based on the TODOs
Document the API using Swagger
Running the Application
Ensure that the Orders API and Service API are running before starting this application.

Package the application:

go
Copy code
$ mvn clean package
Run the application:

shell
Copy code
$ java -jar target/vehicle-management-api-0.0.1-SNAPSHOT.jar
Import the project into your preferred IDE as a Maven Project.

API Operations
Swagger UI: http://localhost:8080/swagger-ui.html

Create a Vehicle
POST /cars

json
Copy code
{
   "condition": "USED",
   "details": {
      "body": "sedan",
      "model": "Impala",
      "manufacturer": {
         "code": 101,
         "name": "Chevrolet"
      },
      "numberOfDoors": 4,
      "fuelType": "Gasoline",
      "engine": "3.6L V6",
      "mileage": 32280,
      "modelYear": 2018,
      "productionYear": 2018,
      "externalColor": "white"
   },
   "location": {
      "lat": 40.73061,
      "lon": -73.935242
   }
}
Retrieve a Vehicle
GET /cars/{id}

Fetches the vehicle details from the database and enriches the information with pricing and location details using the Pricing Service and Boogle Maps.

Update a Vehicle
PUT /cars/{id}

json
Copy code
{
   "condition": "USED",
   "details": {
      "body": "sedan",
      "model": "Impala",
      "manufacturer": {
         "code": 101,
         "name": "Chevrolet"
      },
      "numberOfDoors": 4,
      "fuelType": "Gasoline",
      "engine": "3.6L V6",
      "mileage": 32280,
      "modelYear": 2018,
      "productionYear": 2018,
      "externalColor": "white"
   },
   "location": {
      "lat": 40.73061,
      "lon": -73.935242
   }
}
Delete a Vehicle
DELETE /cars/{id}

Removes a vehicle entry from the database based on the provided ID.