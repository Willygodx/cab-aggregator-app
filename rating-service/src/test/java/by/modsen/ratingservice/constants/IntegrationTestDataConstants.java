package by.modsen.ratingservice.constants;

import by.modsen.ratingservice.dto.request.RatingRequest;
import by.modsen.ratingservice.model.Rating;
import by.modsen.ratingservice.model.enums.RatedBy;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class IntegrationTestDataConstants {

    public static final String MONGODB_IMAGE_NAME = "mongo:5.0.2";
    public static final String EUREKA_CLIENT_ENABLED_PROPERTY = "eureka.client.enabled";
    public static final String BOOLEAN_PROPERTY_VALUE = "false";
    public static final String RATING_ID = "65c8b8b0e4b0e1a0e4b0e1a0";
    public static final String RATING_BY_ID_ENDPOINT = "/api/v1/ratings/{ratingId}";
    public static final String RATING_ENDPOINT = "/api/v1/ratings";
    public static final String RATINGS_BY_DRIVER_ENDPOINT = "/api/v1/ratings/drivers/{driverId}";
    public static final String RATINGS_BY_PASSENGER_ENDPOINT = "/api/v1/ratings/passengers/{passengerId}";
    public static final String DRIVER_AVERAGE_RATING_ENDPOINT = "/api/v1/ratings/drivers/{driverId}/average-rating";
    public static final String PASSENGER_AVERAGE_RATING_ENDPOINT = "/api/v1/ratings/passengers/{passengerId}/average-rating";
    public static final Long DRIVER_ID = 1L;
    public static final Long PASSENGER_ID = 1L;
    public static final Long RIDE_ID = 2L;
    public static final Integer UPDATED_MARK = 5;
    public static final String RATED_BY = "DRIVER";
    public static final Double AVERAGE_RATING = 5.0;

    public static final RatingRequest INVALID_RATING_REQUEST = new RatingRequest(
        RIDE_ID,
        null,
        null,
        RATED_BY
    );

    public static final String PASSENGER_FROM_ANOTHER_SERVICE_ENDPOINT = "/api/v1/passengers/" + PASSENGER_ID;
    public static final String DRIVER_FROM_ANOTHER_SERVICE_ENDPOINT = "/api/v1/drivers/" + DRIVER_ID;
    public static final String RIDE_FROM_ANOTHER_SERVICE_ENDPOINT = "/api/v1/rides/" + RIDE_ID;

    public static final Rating BEFORE_EACH_RATING = new Rating(
        "65c8b8b0e4b0e1a0e4b0e1a0",
        1L,
        1L,
        1L,
        5,
        "Great ride!",
        RatedBy.DRIVER
    );

    public static final String DATABASE_NAME = "ratings";

}