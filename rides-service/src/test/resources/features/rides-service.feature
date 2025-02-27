Feature: Ride Service API

  Scenario: Create a new ride
    Given There is the following ride details
    """
      {
        "driverId": 1,
        "passengerId": 2,
        "pickupAddress": "123 Main St",
        "destinationAddress": "456 Elm St"
      }
    """
    When Creates the ride
    Then the response status is 201
    And the response body should contain the information about created ride
    """
      {
          "id": 30,
          "driverId": 1,
          "passengerId": 2,
          "pickupAddress": "123 Main St",
          "destinationAddress": "456 Elm St",
          "rideStatus": "CREATED",
          "orderDateTime": "2025-02-27T02:52:11.697227",
          "cost": 76.72
      }
    """

  Scenario: Create a new ride with invalid data
    Given There is the following invalid ride details
    """
      {
        "driverId": null,
        "passengerId": null,
        "pickupAddress": "",
        "destinationAddress": ""
      }
    """
    When Creates the ride
    Then the response status is 400
    And the response body should contain the validation errors
    """
      {
          "errors": [
              {
                  "fieldName": "destinationAddress",
                  "message": "Destination address cannot be blank!"
              },
              {
                  "fieldName": "driverId",
                  "message": "Driver ID cannot be blank!"
              },
              {
                  "fieldName": "passengerId",
                  "message": "Passenger ID cannot be blank!"
              },
              {
                  "fieldName": "pickupAddress",
                  "message": "Pickup address cannot be blank!"
              }
          ]
      }
    """

  Scenario: Update ride
    Given There is the following ride details
    """
      {
        "pickupAddress": "789 Oak St",
        "destinationAddress": "101 Pine St"
      }
    """
    When Update ride with id 1
    Then the response status is 200
    And the response body should contain the information about updated ride
    """
      {
          "id": 1,
          "driverId": 1,
          "passengerId": 1,
          "pickupAddress": "789 Oak St",
          "destinationAddress": "101 Pine St",
          "rideStatus": "CREATED",
          "orderDateTime": "2023-10-01T10:00:00",
          "cost": 12.50
      }
    """

  Scenario: Update non-existent ride
    Given There is the following ride details
    """
      {
        "pickupAddress": "789 Oak St",
        "destinationAddress": "101 Pine St"
      }
    """
    When Update ride with id -1
    Then the response status is 404
    And the response body should contain the error message
    """
      {
          "message": "Ride with id -1 not found!",
          "status": "NOT_FOUND",
          "timestamp": "2025-02-27T05:37:47.134121"
      }
    """

  Scenario: Update ride status
    Given There is the following ride status details
    """
      {
        "rideStatus": "ACCEPTED"
      }
    """
    When Update ride status with id 7
    Then the response status is 200
    And the response body should contain the information about updated ride
    """
      {
          "id": 7,
          "driverId": 4,
          "passengerId": 1,
          "pickupAddress": "123 Maple St",
          "destinationAddress": "321 Cedar St",
          "rideStatus": "ACCEPTED",
          "orderDateTime": "2023-10-01T16:00:00",
          "cost": 14.50
      }
    """

  Scenario: Update ride status with invalid status
    Given There is the following invalid ride status details
    """
      {
        "rideStatus": "INVALID_STATUS"
      }
    """
    When Update ride status with id 7
    Then the response status is 400
    And the response body should contain the error message
    """
      {
          "errors": [
              {
                  "fieldName": "rideStatus",
                  "message": "Ride status is invalid!"
              }
          ]
      }
    """

  Scenario: Get paginated rides
    When Get a page with rides with current offset 1 and limit 2
    Then the response status is 200
    And the response body should contain the information about first ten rides
    """
      {
          "currentOffset": 1,
          "currentLimit": 2,
          "totalPages": 15,
          "totalElements": 30,
          "sort": "UNSORTED",
          "values": [
              {
                  "id": 4,
                  "driverId": 2,
                  "passengerId": 3,
                  "pickupAddress": "321 Birch St",
                  "destinationAddress": "987 Spruce St",
                  "rideStatus": "ON_THE_WAY_TO_DESTINATION",
                  "orderDateTime": "2023-10-01T13:00:00",
                  "cost": 20.00
              },
              {
                  "id": 5,
                  "driverId": 3,
                  "passengerId": 2,
                  "pickupAddress": "654 Oak St",
                  "destinationAddress": "123 Main St",
                  "rideStatus": "FINISHED",
                  "orderDateTime": "2023-10-01T14:00:00",
                  "cost": 18.25
              }
          ]
      }
    """

  Scenario: Get rides with invalid pagination parameters
    When Get a page with rides with current offset -1 and limit 101
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

  Scenario: Get ride by ID
    When Get ride with id 2
    Then the response status is 200
    And the response body should contain the information about ride
    """
      {
          "id": 2,
          "driverId": 1,
          "passengerId": 2,
          "pickupAddress": "789 Oak St",
          "destinationAddress": "321 Pine St",
          "rideStatus": "ACCEPTED",
          "orderDateTime": "2023-10-01T11:00:00",
          "cost": 15.75
      }
    """

  Scenario: Get non-existent ride by ID
    When Get ride with id -1
    Then the response status is 404
    And the response body should contain the error message
    """
      {
          "message": "Ride with id -1 not found!",
          "status": "NOT_FOUND",
          "timestamp": "2025-02-27T05:43:40.989852"
      }
    """

  Scenario: Delete ride
    When Delete ride with id 1
    Then the response status is 204

  Scenario: Delete non-existent ride
    When Delete ride with id -1
    Then the response status is 404
    And the response body should contain the error message
    """
      {
          "message": "Ride with id -1 not found!",
          "status": "NOT_FOUND",
          "timestamp": "2025-02-27T05:44:47.304708"
      }
    """