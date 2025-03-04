package by.modsen.ratingservice.constants;

import by.modsen.ratingservice.client.driver.DriverResponse;
import by.modsen.ratingservice.client.passenger.PassengerResponse;
import by.modsen.ratingservice.client.ride.RideResponse;
import by.modsen.ratingservice.dto.request.RatingRequest;
import by.modsen.ratingservice.dto.response.AverageRatingResponse;
import by.modsen.ratingservice.dto.response.RatingResponse;
import by.modsen.ratingservice.model.Rating;
import by.modsen.ratingservice.model.enums.RatedBy;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TestDataConstants {

    public static final String RATING_ID = "1";
    public static final Long RIDE_ID = 2L;
    public static final Long DRIVER_ID = 1L;
    public static final Long PASSENGER_ID = 1L;
    public static final Integer MARK = 5;
    public static final String COMMENT = "Great ride!";
    public static final String RATED_BY_DRIVER = "DRIVER";
    public static final String LANGUAGE_TAG = "en";
    public static final String PICKUP_ADDRESS = "123 Main St";
    public static final String DESTINATION_ADDRESS = "456 Elm St";
    public static final String RIDE_STATUS = "ACCEPTED";

    public static final RatingRequest RATING_REQUEST = new RatingRequest(
        RIDE_ID,
        MARK,
        COMMENT,
        RATED_BY_DRIVER
    );

    public static final RatingRequest RATING_REQUEST_FOR_UPDATE = new RatingRequest(
        null,
        MARK,
        COMMENT,
        null
    );

    public static final RatingResponse RATING_RESPONSE = new RatingResponse(
        RATING_ID,
        RIDE_ID,
        DRIVER_ID,
        PASSENGER_ID,
        MARK,
        COMMENT,
        RATED_BY_DRIVER
    );

    public static final AverageRatingResponse AVERAGE_RATING_RESPONSE = new AverageRatingResponse(
        5.0
    );

    public static final RideResponse RIDE_RESPONSE = new RideResponse(
        RIDE_ID,
        DRIVER_ID,
        PASSENGER_ID,
        PICKUP_ADDRESS,
        DESTINATION_ADDRESS,
        RIDE_STATUS,
        LocalDateTime.now(),
        new BigDecimal("10.00")
    );

    public static final PassengerResponse PASSENGER_RESPONSE = new PassengerResponse(
        PASSENGER_ID,
        "John",
        "Doe",
        "john.doe@example.com",
        "+123456789",
        false
    );

    public static final DriverResponse DRIVER_RESPONSE = new DriverResponse(
        DRIVER_ID,
        "Jane",
        "Doe",
        "jane.doe@example.com",
        "+987654321",

        "FEMALE",

        false,
        List.of(1L, 2L)
    );

    public static final Rating RATING;

    static {
        RATING = new Rating();
        RATING.setId(RATING_ID);
        RATING.setRideId(RIDE_ID);
        RATING.setDriverId(DRIVER_ID);
        RATING.setPassengerId(PASSENGER_ID);
        RATING.setMark(MARK);
        RATING.setComment(COMMENT);
        RATING.setRatedBy(RatedBy.DRIVER);
    }

    public static final String GET_ALL_RATINGS_ENDPOINT = "/api/v1/ratings";
    public static final String GET_RATING_BY_ID_ENDPOINT = "/api/v1/ratings/{ratingId}";
    public static final String CREATE_RATING_ENDPOINT = "/api/v1/ratings";
    public static final String UPDATE_RATING_ENDPOINT = "/api/v1/ratings/{ratingId}";
    public static final String DELETE_RATING_ENDPOINT = "/api/v1/ratings/{ratingId}";
    public static final int PAGE_OFFSET = 1;
    public static final int PAGE_LIMIT = 10;
    public static final String RATING_NOT_FOUND_MESSAGE = "Rating not found!";
    public static final String RATING_ALREADY_EXISTS_MESSAGE = "Rating already exists!";
    public static final String HTTP_STATUS_CONFLICT = "CONFLICT";
    public static final String HTTP_STATUS_NOT_FOUND = "NOT_FOUND";
}