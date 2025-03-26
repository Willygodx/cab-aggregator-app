Feature: Car Service API

  Scenario: Create a new car
    Given There is the following car details
    """
      {
        "color": "BLACK",
        "carBrand": "BMW",
        "carNumber": "WMZ993"
      }
    """
    When Creates the car
    Then the response status is 201
    And the response body should contain the information about created car
    """
      {
        "id": 50,
        "color": "BLACK",
        "carBrand": "BMW",
        "carNumber": "WMZ993",
        "isDeleted": false,
        "driverIds": []
      }
    """

  Scenario: Create a new car with existing car number
    Given There is the following car details
    """
      {
        "color": "BLUE",
        "carBrand": "AUDI",
        "carNumber": "WMZ993"
      }
    """
    When Creates the car
    Then the response status is 409
    And the response body should contain the error message
    """
      {
          "message": "Car number: WMZ993 is already connected to another car!",
          "status": "CONFLICT",
          "timestamp": "2025-02-25T11:52:49.567249"
      }
    """

  Scenario: Create a new car with invalid data
    Given There is the following invalid car details
    """
      {
        "color": "",
        "carBrand": "",
        "carNumber": "invalid"
      }
    """
    When Creates the car
    Then the response status is 400
    And the response body should contain the validation errors
    """
      {
          "errors": [
              {
                  "fieldName": "color",
                  "message": "Car's color cannot be blank!"
              },
              {
                  "fieldName": "carBrand",
                  "message": "Car brand cannot be blank!"
              },
              {
                  "fieldName": "carNumber",
                  "message": "License plate is invalid!"
              }
          ]
      }
    """

  Scenario: Get paginated cars
    When Get a page with cars with current offset 0 and limit 2
    Then the response status is 200
    And the response body should contain the information about first ten cars
    """
      {
          "currentOffset": 0,
          "currentLimit": 2,
          "totalPages": 25,
          "totalElements": 50,
          "sort": "UNSORTED",
          "values": [
              {
                  "id": 1,
                  "color": "BLACK",
                  "carBrand": "TOYOTA",
                  "carNumber": "ABC001",
                  "isDeleted": false,
                  "driverIds": [
                      1
                  ]
              },
              {
                  "id": 2,
                  "color": "WHITE",
                  "carBrand": "FORD",
                  "carNumber": "ABC002",
                  "isDeleted": false,
                  "driverIds": [
                      1
                  ]
              }
          ]
      }
    """

  Scenario: Get cars with invalid pagination parameters
    When Get a page with cars with current offset -1 and limit 101
    Then the response status is 400
    And the response body should contain the validation errors
    """
      {
          "errors": [
              {
                  "fieldName": "offset",
                  "message": "must be greater than or equal to 0"
              },
              {
                  "fieldName": "limit",
                  "message": "must be less than or equal to 100"
              }
          ]
      }
    """

  Scenario: Get car by ID
    When Get car with id 1
    Then the response status is 200
    And the response body should contain the information about car
    """
      {
          "id": 1,
          "color": "BLACK",
          "carBrand": "TOYOTA",
          "carNumber": "ABC001",
          "isDeleted": false,
          "driverIds": [
              1
          ]
      }
    """

  Scenario: Get non-existent car by ID
    When Get car with id -1
    Then the response status is 404
    And the response body should contain the error message
    """
      {
          "message": "Car with id -1 not found!",
          "status": "NOT_FOUND",
          "timestamp": "2025-02-25T11:57:42.275883"
      }
    """

  Scenario: Update car
    Given There is the following car details
    """
      {
        "color": "BLACK",
        "carBrand": "MERCEDES",
        "carNumber": "DEF456"
      }
    """
    When Update car with id 1
    Then the response status is 200
    And the response body should contain the information about updated car
    """
      {
          "id": 1,
          "color": "BLACK",
          "carBrand": "MERCEDES",
          "carNumber": "DEF456",
          "isDeleted": false,
          "driverIds": [
              1
          ]
      }
    """

  Scenario: Update car with existing car number of another car
    Given There is the following car details
    """
      {
          "carNumber": "DEF456"
      }
    """
    When Update car with id 2
    Then the response status is 409
    And the response body should contain the error message
    """
      {
          "message": "Car number: DEF456 is already connected to another car!",
          "status": "CONFLICT",
          "timestamp": "2025-02-25T11:59:34.169143"
      }
    """

  Scenario: Update non-existent car
    Given There is the following car details
    """
      {
        "carNumber": "KKK999"
      }
    """
    When Update car with id -1
    Then the response status is 404
    And the response body should contain the error message
    """
      {
          "message": "Car with id -1 not found!",
          "status": "NOT_FOUND",
          "timestamp": "2025-02-25T12:00:18.361584"
      }
    """

  Scenario: Update car with invalid data
    Given There is the following invalid car details
    """
      {
        "carNumber": "invalid"
      }
    """
    When Update car with id 3
    Then the response status is 400
    And the response body should contain the validation errors
    """
      {
          "errors": [
              {
                  "fieldName": "carNumber",
                  "message": "License plate is invalid!"
              }
          ]
      }
    """

  Scenario: Delete car
    When Delete car with id 1
    Then the response status is 204

  Scenario: Delete non-existent car
    When Delete car with id -1
    Then the response status is 404
    And the response body should contain the error message
    """
      {
          "message": "Car with id -1 not found!",
          "status": "NOT_FOUND",
          "timestamp": "2025-02-25T12:02:00.872173"
      }
    """

  Scenario: Create a new driver
    Given There is the following driver details
    """
      {
          "firstName": "John",
          "lastName": "Doe",
          "email": "john.doe1337@exam1ple.com",
          "phoneNumber": "+375296799880",
          "sex": "MALE"
      }
    """
    When Creates the driver
    Then the response status is 201
    And the response body should contain the information about created driver
    """
      {
          "id": 21,
          "firstName": "John",
          "lastName": "Doe",
          "email": "john.doe1337@exam1ple.com",
          "phoneNumber": "+375296799880",
          "sex": "MALE",
          "averageRating": 0.0,
          "isDeleted": false,
          "carIds": []
      }
    """

  Scenario: Create a new driver with existing email
    Given There is the following driver details
    """
      {
          "firstName": "Johnny",
          "lastName": "Opel",
          "email": "john.doe1337@exam1ple.com",
          "phoneNumber": "+375296799111",
          "sex": "MALE"
      }
    """
    When Creates the driver
    Then the response status is 409
    And the response body should contain the error message
    """
      {
          "message": "Email: john.doe1337@exam1ple.com is already taken by another driver!",
          "status": "CONFLICT",
          "timestamp": "2025-02-25T12:04:08.457296"
      }
    """

  Scenario: Create a new driver with existing phone number
    Given There is the following driver details
    """
      {
          "firstName": "Ruslan",
          "lastName": "James",
          "email": "ruslan.james999@exam1ple.com",
          "phoneNumber": "+375296799880",
          "sex": "MALE"
      }
    """
    When Creates the driver
    Then the response status is 409
    And the response body should contain the error message
    """
      {
          "message": "Phone number: +375296799880 is already taken by another driver!",
          "status": "CONFLICT",
          "timestamp": "2025-02-25T12:05:15.179485"
      }
    """

  Scenario: Create a new driver with invalid data
    Given There is the following invalid driver details
    """
      {
          "firstName": "",
          "lastName": "",
          "email": "invalid",
          "phoneNumber": "invalid",
          "sex": "MALEEEE"
      }
    """
    When Creates the driver
    Then the response status is 400
    And the response body should contain the validation errors
    """
      {
          "errors": [
              {
                  "fieldName": "email",
                  "message": "Email is invalid!"
              },
              {
                  "fieldName": "lastName",
                  "message": "Last name shouldn't be blank!"
              },
              {
                  "fieldName": "firstName",
                  "message": "First name shouldn't be blank!"
              },
              {
                  "fieldName": "phoneNumber",
                  "message": "Phone number is invalid!"
              },
              {
                  "fieldName": "sex",
                  "message": "Driver's sex is invalid! (need MALE, FEMALE, NONE or OTHER)"
              }
          ]
      }
    """

  Scenario: Get paginated drivers
    When Get a page with drivers with current offset 0 and limit 2
    Then the response status is 200
    And the response body should contain the information about first ten drivers
    """
      {
          "currentOffset": 0,
          "currentLimit": 2,
          "totalPages": 11,
          "totalElements": 21,
          "sort": "UNSORTED",
          "values": [
              {
                  "id": 1,
                  "firstName": "Ivan",
                  "lastName": "Ivanov",
                  "email": "ivan.ivanov@example.com",
                  "phoneNumber": "+375291234567",
                  "sex": "MALE",
                  "averageRating": 0.0,
                  "isDeleted": false,
                  "carIds": [
                      2,
                      1
                  ]
              },
              {
                  "id": 2,
                  "firstName": "Anna",
                  "lastName": "Petrova",
                  "email": "anna.petrova@example.com",
                  "phoneNumber": "+375291234568",
                  "sex": "FEMALE",
                  "averageRating": 0.0,
                  "isDeleted": false,
                  "carIds": [
                      3,
                      4
                  ]
              }
          ]
      }
    """

  Scenario: Get drivers with invalid pagination parameters
    When Get a page with drivers with current offset -1 and limit 101
    Then the response status is 400
    And the response body should contain the validation errors
    """
      {
          "errors": [
              {
                  "fieldName": "offset",
                  "message": "must be greater than or equal to 0"
              },
              {
                  "fieldName": "limit",
                  "message": "must be less than or equal to 100"
              }
          ]
      }
    """

  Scenario: Get driver by ID
    When Get driver with id 1
    Then the response status is 200
    And the response body should contain the information about driver
    """
      {
          "id": 1,
          "firstName": "Ivan",
          "lastName": "Ivanov",
          "email": "ivan.ivanov@example.com",
          "phoneNumber": "+375291234567",
          "sex": "MALE",
          "averageRating": 0.0,
          "isDeleted": false,
          "carIds": [
              2,
              1
          ]
      }
    """

  Scenario: Get non-existent driver by ID
    When Get driver with id -1
    Then the response status is 404
    And the response body should contain the error message
    """
      {
          "message": "Driver with id -1 not found!",
          "status": "NOT_FOUND",
          "timestamp": "2025-02-25T12:08:44.351797"
      }
    """

  Scenario: Update driver
    Given There is the following driver details
    """
      {
        "firstName": "Ruslan",
        "lastName": "Alhava",
        "email": "ruslan322updated@example.com",
        "phoneNumber": "+375339999999",
        "sex": "MALE"
      }
    """
    When Update driver with id 1
    Then the response status is 200
    And the response body should contain the information about updated driver
    """
      {
          "id": 1,
          "firstName": "Ruslan",
          "lastName": "Alhava",
          "email": "ruslan322updated@example.com",
          "phoneNumber": "+375339999999",
          "sex": "MALE",
          "averageRating": 0.0,
          "isDeleted": false,
          "carIds": [
              1,
              2
          ]
      }
    """

  Scenario: Update driver with existing email of another driver
    Given There is the following driver details
    """
      {
        "email": "ruslan322updated@example.com"
      }
    """
    When Update driver with id 5
    Then the response status is 409
    And the response body should contain the error message
    """
      {
          "message": "Email: ruslan322updated@example.com is already taken by another driver!",
          "status": "CONFLICT",
          "timestamp": "2025-02-25T12:10:03.35836"
      }
    """

  Scenario: Update driver with existing phone number of another driver
    Given There is the following driver details
    """
      {
        "phoneNumber": "+375339999999"
      }
    """
    When Update driver with id 5
    Then the response status is 409
    And the response body should contain the error message
    """
      {
          "message": "Phone number: +375339999999 is already taken by another driver!",
          "status": "CONFLICT",
          "timestamp": "2025-02-25T12:10:38.419513"
      }
    """

  Scenario: Update non-existent driver
    Given There is the following driver details
    """
      {
          "firstName": "Ilya"
      }
    """
    When Update driver with id -1
    Then the response status is 404
    And the response body should contain the error message
    """
      {
          "message": "Driver with id -1 not found!",
          "status": "NOT_FOUND",
          "timestamp": "2025-02-25T12:11:09.646924"
      }
    """

  Scenario: Update driver with invalid data
    Given There is the following invalid driver details
    """
      {
        "email": "invalid-email",
        "phoneNumber": "123",
        "sex": "MALEEE"
      }
    """
    When Update driver with id 5
    Then the response status is 400
    And the response body should contain the validation errors
    """
      {
          "errors": [
              {
                  "fieldName": "email",
                  "message": "Email is invalid!"
              },
              {
                  "fieldName": "phoneNumber",
                  "message": "Phone number is invalid!"
              },
              {
                  "fieldName": "sex",
                  "message": "Driver's sex is invalid! (need MALE, FEMALE, NONE or OTHER)"
              }
          ]
      }
    """

  Scenario: Delete driver
    When Delete driver with id 1
    Then the response status is 204

  Scenario: Delete non-existent driver
    When Delete driver with id -1
    Then the response status is 404
    And the response body should contain the error message
    """
      {
          "message": "Driver with id -1 not found!",
          "status": "NOT_FOUND",
          "timestamp": "2025-02-25T12:13:05.556838"
      }
    """

  Scenario: Add car to driver
    Given There is an existing driver with id 12 and an existing car with id 5
    When Add car with id 5 to driver with id 12
    Then the response status is 200

  Scenario: Add car to non-existent driver
    Given There is an existing car with id 5
    When Add car with id 5 to driver with id -1
    Then the response status is 404
    And the response body should contain the error message
    """
      {
          "message": "Driver with id -1 not found!",
          "status": "NOT_FOUND",
          "timestamp": "2025-02-25T12:13:56.414034"
      }
    """

  Scenario: Add non-existent car to driver
    Given There is an existing driver with id 12
    When Add car with id -1 to driver with id 12
    Then the response status is 404
    And the response body should contain the error message
    """
      {
          "message": "Car with id -1 not found!",
          "status": "NOT_FOUND",
          "timestamp": "2025-02-25T12:14:17.619439"
      }
    """