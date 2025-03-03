Feature: Rating Service API

  Scenario: Create a new rating
    Given There is the following rating details
    """
      {
          "rideId": 25,
          "mark": 5,
          "comment": "Nice driver!",
          "ratedBy": "DRIVER"
      }
    """
    When Creates the rating
    Then the response status is 201
    And save the response body as "createdRating"
    And the response body should contain the information about created rating
    """
      {
          "rideId": 25,
          "driverId": 3,
          "passengerId": 1,
          "mark": 5,
          "comment": "Nice driver!",
          "ratedBy": "DRIVER"
      }
    """

  Scenario: Create a new rating with invalid data
    Given There is the following invalid rating details
    """
      {
          "mark": 6,
          "comment": "Nice driver!",
          "ratedBy": "INVALID"
      }
    """
    When Creates the rating
    Then the response status is 400
    And the response body should contain the validation errors
    """
      {
          "errors": [
              {
                  "fieldName": "rideId",
                  "message": "You should put down ride ID for rating!"
              },
              {
                  "fieldName": "ratedBy",
                  "message": "Invalid rated by value! (should be DRIVER or PASSENGER)"
              },
              {
                  "fieldName": "mark",
                  "message": "Mark should be from 1 to 5!"
              }
          ]
      }
    """

  Scenario: Get rating by ID
    When Get rating with id "createdRating"
    Then the response status is 200
    And the response body should contain the information about rating
    """
      {
          "rideId": 25,
          "driverId": 3,
          "passengerId": 1,
          "mark": 5,
          "comment": "Nice driver!",
          "ratedBy": "DRIVER"
      }
    """

  Scenario: Get non-existent rating by ID
    When Get rating with id "non-existent-id"
    Then the response status is 404
    And the response body should contain the error message
    """
      {
          "message": "Rating non-existent-id not found!",
          "status": "NOT_FOUND",
          "timestamp": "2025-03-03T02:11:20.542182"
      }
    """

  Scenario: Get paginated ratings
    When Get a page with ratings with current offset 1 and limit 2
    Then the response status is 200
    And the response body should contain the information about first ten ratings
    """
      {
          "currentOffset": 1,
          "currentLimit": 2,
          "totalPages": 25,
          "totalElements": 49,
          "sort": "UNSORTED",
          "values": [
              {
                  "rideId": 2,
                  "driverId": 1,
                  "passengerId": 2,
                  "mark": 3,
                  "comment": "Good ride, could be better.",
                  "ratedBy": "PASSENGER"
              },
              {
                  "rideId": 2,
                  "driverId": 1,
                  "passengerId": 2,
                  "mark": 4,
                  "comment": "Punctual and respectful passenger.",
                  "ratedBy": "DRIVER"
              }
          ]
      }
    """

  Scenario: Get ratings with invalid pagination parameters
    When Get a page with ratings with current offset -1 and limit 101
    Then the response status is 400
    And the response body should contain the validation errors
    """
      {
          "errors": [
              {
                  "fieldName": "limit",
                  "message": "must be less than or equal to 100"
              },
              {
                  "fieldName": "offset",
                  "message": "must be greater than or equal to 0"
              }
          ]
      }
    """

  Scenario: Get average rating for passenger
    When Get average rating for passenger with id 1
    Then the response status is 200
    And the response body should contain the average rating
    """
      {
          "averageRating": 4.14
      }
    """

  Scenario: Get average rating for driver
    When Get average rating for driver with id 1
    Then the response status is 200
    And the response body should contain the average rating
    """
      {
          "averageRating": 3.67
      }
    """

  Scenario: Trigger Kafka to calculate and send average ratings
    When Trigger Kafka to calculate and send average ratings
    Then the response status is 200

  Scenario: Update rating
    Given There is the following rating details
    """
      {
        "mark": 4,
        "comment": "Good service!"
      }
    """
    When Update rating with id from "createdRating"
    Then the response status is 200
    And the response body should contain the information about updated rating
    """
      {
          "rideId": 25,
          "driverId": 3,
          "passengerId": 1,
          "mark": 4,
          "comment": "Good service!",
          "ratedBy": "DRIVER"
      }
    """

  Scenario: Update non-existent rating
    Given There is the following rating details
    """
      {
        "mark": 4,
        "comment": "Good service!"
      }
    """
    When Update rating with id "non-existent-id"
    Then the response status is 404
    And the response body should contain the error message
    """
      {
          "message": "Rating non-existent-id not found!",
          "status": "NOT_FOUND",
          "timestamp": "2025-03-03T04:03:00.035793"
      }
    """

  Scenario: Delete rating
    When Delete rating with id from "createdRating"
    Then the response status is 204

  Scenario: Delete non-existent rating
    When Delete rating with id "non-existent-id"
    Then the response status is 404
    And the response body should contain the error message
    """
      {
          "message": "Rating non-existent-id not found!",
          "status": "NOT_FOUND",
          "timestamp": "2025-03-03T04:03:42.891899"
      }
    """