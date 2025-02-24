Feature: Passenger Service API

  Scenario: Create a new passenger
    Given There is the following passenger details
    """
      {
        "firstName": "Ruslan",
        "lastName": "Alhava",
        "email": "ruslan322@example.com",
        "phoneNumber": "+375338723636"
      }
    """
    When Creates the passenger
    Then the response status is 201
    And the response body should contain the information about created passenger
    """
      {
        "id": 54,
        "firstName": "Ruslan",
        "lastName": "Alhava",
        "email": "ruslan322@example.com",
        "phoneNumber": "+375338723636",
        "averageRating": "0.0",
        "isDeleted": false
      }
    """

  Scenario: Create a new passenger with existing email
    Given There is the following passenger details
    """
      {
        "firstName": "Ruslan",
        "lastName": "Alhava",
        "email": "ruslan322@example.com",
        "phoneNumber": "+375338723637"
      }
    """
    When Creates the passenger
    Then the response status is 409
    And the response body should contain the error message
    """
      {
          "message": "Email: ruslan322@example.com is already taken by another passenger!",
          "status": "CONFLICT",
          "timestamp": "2025-02-20T16:49:04.371963"
      }
    """

  Scenario: Create a new passenger with existing phone number
    Given There is the following passenger details
    """
      {
        "firstName": "Ruslan",
        "lastName": "Alhava",
        "email": "ruslanRuslan1234@gmail.com",
        "phoneNumber": "+375338723636"
      }
    """
    When Creates the passenger
    Then the response status is 409
    And the response body should contain the error message
    """
      {
          "message": "Phone number: +375338723636 is already taken by another passenger!",
          "status": "CONFLICT",
          "timestamp": "2025-02-20T16:50:25.794514"
      }
    """

  Scenario: Create a new passenger with invalid data
    Given There is the following invalid passenger details
    """
      {
        "firstName": "",
        "lastName": "Alhava",
        "email": "invalid-email",
        "phoneNumber": "123"
      }
    """
    When Creates the passenger
    Then the response status is 400
    And the response body should contain the validation errors
    """
      {
          "errors": [
              {
                  "fieldName": "phoneNumber",
                  "message": "Phone number is invalid!"
              },
              {
                  "fieldName": "email",
                  "message": "Email is invalid!"
              },
              {
                  "fieldName": "firstName",
                  "message": "First name shouldn't be blank!"
              }
          ]
      }
    """

  Scenario: Get paginated passengers
    When Get a page with passengers with current offset 0 and limit 10
    Then the response status is 200
    And the response body should contain the information about first ten passengers
    """
      {
          "currentOffset": 0,
          "currentLimit": 10,
          "totalPages": 6,
          "totalElements": 54,
          "sort": "UNSORTED",
          "values": [
              {
                  "id": 1,
                  "firstName": "Ivan",
                  "lastName": "Ivanov",
                  "email": "ivan1@example.com",
                  "phoneNumber": "+375336401000",
                  "averageRating": "0.0",
                  "isDeleted": false
              },
              {
                  "id": 2,
                  "firstName": "Petr",
                  "lastName": "Petrov",
                  "email": "petr1@example.com",
                  "phoneNumber": "+375336401001",
                  "averageRating": "0.0",
                  "isDeleted": false
              },
              {
                  "id": 3,
                  "firstName": "Anna",
                  "lastName": "Annova",
                  "email": "anna1@example.com",
                  "phoneNumber": "+375336401002",
                  "averageRating": "0.0",
                  "isDeleted": false
              },
              {
                  "id": 4,
                  "firstName": "Svetlana",
                  "lastName": "Svetlova",
                  "email": "svetlana1@example.com",
                  "phoneNumber": "+375336401003",
                  "averageRating": "0.0",
                  "isDeleted": false
              },
              {
                  "id": 5,
                  "firstName": "Dmitry",
                  "lastName": "Dmitriev",
                  "email": "dmitry1@example.com",
                  "phoneNumber": "+375336401004",
                  "averageRating": "0.0",
                  "isDeleted": false
              },
              {
                  "id": 6,
                  "firstName": "Olga",
                  "lastName": "Olgina",
                  "email": "olga1@example.com",
                  "phoneNumber": "+375336401005",
                  "averageRating": "0.0",
                  "isDeleted": false
              },
              {
                  "id": 7,
                  "firstName": "Alexey",
                  "lastName": "Alexeev",
                  "email": "alexey1@example.com",
                  "phoneNumber": "+375336401006",
                  "averageRating": "0.0",
                  "isDeleted": false
              },
              {
                  "id": 8,
                  "firstName": "Maria",
                  "lastName": "Marina",
                  "email": "maria1@example.com",
                  "phoneNumber": "+375336401007",
                  "averageRating": "0.0",
                  "isDeleted": false
              },
              {
                  "id": 9,
                  "firstName": "Nikolai",
                  "lastName": "Nikolayev",
                  "email": "nikolai1@example.com",
                  "phoneNumber": "+375336401008",
                  "averageRating": "0.0",
                  "isDeleted": false
              },
              {
                  "id": 10,
                  "firstName": "Tatiana",
                  "lastName": "Tatianova",
                  "email": "tatiana1@example.com",
                  "phoneNumber": "+375336401009",
                  "averageRating": "0.0",
                  "isDeleted": false
              }
          ]
      }
    """

  Scenario: Get passengers with invalid pagination parameters
    When Get a page with passengers with current offset -1 and limit 101
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

  Scenario: Get passenger by ID
    When Get passenger with id 1
    Then the response status is 200
    And the response body should contain the information about passenger
    """
      {
        "id": 1,
        "firstName": "Ivan",
        "lastName": "Ivanov",
        "email": "ivan1@example.com",
        "phoneNumber": "+375336401000",
        "averageRating": "0.0",
        "isDeleted": false
      }
    """

  Scenario: Get non-existent passenger by ID
    When Get passenger with id -1
    Then the response status is 404
    And the response body should contain the error message
    """
      {
          "message": "Passenger with id -1 not found!",
          "status": "NOT_FOUND",
          "timestamp": "2025-02-20T16:43:27.77588"
      }
    """

  Scenario: Update passenger
    Given There is the following passenger details
    """
      {
        "firstName": "Ruslan",
        "lastName": "Alhava",
        "email": "ruslan3224321341234@gmail.com",
        "phoneNumber": "+375339999999"
      }
    """
    When Update passenger with id 1
    Then the response status is 200
    And the response body should contain the information about updated passenger
    """
      {
        "id": 1,
        "firstName": "Ruslan",
        "lastName": "Alhava",
        "email": "ruslan3224321341234@gmail.com",
        "phoneNumber": "+375339999999",
        "averageRating": "0.0",
        "isDeleted": false
      }
    """

  Scenario: Update passenger with existing email of another passenger
    Given There is the following passenger details
    """
      {
        "email": "ruslan3224321341234@gmail.com"
      }
    """
    When Update passenger with id 1
    Then the response status is 409
    And the response body should contain the error message
    """
      {
          "message": "Email: ruslan3224321341234@gmail.com is already taken by another passenger!",
          "status": "CONFLICT",
          "timestamp": "2025-02-20T17:01:00.986706"
      }
    """

  Scenario: Update passenger with existing phone number of another passenger
    Given There is the following passenger details
    """
      {
        "phoneNumber": "+375339999999"
      }
    """
    When Update passenger with id 1
    Then the response status is 409
    And the response body should contain the error message
    """
      {
          "message": "Phone number: +375339999999 is already taken by another passenger!",
          "status": "CONFLICT",
          "timestamp": "2025-02-20T17:02:00.468493"
      }
    """

  Scenario: Update non-existent passenger
    Given There is the following passenger details
    """
      {
        "firstName": "Ruslan",
        "lastName": "Alhava",
        "email": "ruslan123123322@exakle.com",
        "phoneNumber": "+375338723000"
      }
    """
    When Update passenger with id -1
    Then the response status is 404
    And the response body should contain the error message
    """
      {
          "message": "Passenger with id -1 not found!",
          "status": "NOT_FOUND",
          "timestamp": "2025-02-20T16:44:42.439521"
      }
    """

  Scenario: Update passenger with invalid data
    Given There is the following invalid passenger details
    """
      {
        "firstName": "Ruslan",
        "lastName": "Alhava",
        "email": "invalid-email",
        "phoneNumber": "123"
      }
    """
    When Update passenger with id 1
    Then the response status is 400
    And the response body should contain the validation errors
    """
      {
          "errors": [
              {
                  "fieldName": "phoneNumber",
                  "message": "Phone number is invalid!"
              },
              {
                  "fieldName": "email",
                  "message": "Email is invalid!"
              }
          ]
      }
    """

  Scenario: Delete passenger
    When Delete passenger with id 1
    Then the response status is 204

  Scenario: Delete non-existent passenger
    When Delete passenger with id -1
    Then the response status is 404
    And the response body should contain the error message
    """
      {
          "message": "Passenger with id -1 not found!",
          "status": "NOT_FOUND",
          "timestamp": "2025-02-20T16:45:40.113556"
      }
    """