package by.modsen.passengerservice;

import by.modsen.passengerservice.dto.request.PassengerRequest;
import by.modsen.passengerservice.dto.response.PassengerResponse;
import by.modsen.passengerservice.model.Passenger;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TestDataConstants {

    public static final PassengerRequest PASSENGER_REQUEST = new PassengerRequest(
        "Matthew",
        "McConaughey",
        "matt.mcconh23@gmail.com",
        "+375339482732");

    public static final PassengerResponse PASSENGER_RESPONSE = new PassengerResponse(
        1L,
        "Matthew",
        "McConaughey",
        "matt.mcconh23@gmail.com",
        "+375339482732",
        "0.0",
        false
    );

    public static final Long PASSENGER_ID = 1L;

    public static final int PAGE_OFFSET = 1;

    public static final int PAGE_LIMIT = 10;

    public static final Passenger PASSENGER;

    static {
        PASSENGER = new Passenger();
        PASSENGER.setId(1L);
        PASSENGER.setFirstName("Ruslan");
        PASSENGER.setLastName("Alhava");
        PASSENGER.setEmail("ruslan322@example.com");
        PASSENGER.setPhoneNumber("+375338723636");
        PASSENGER.setAverageRating(0.0);
        PASSENGER.setIsDeleted(false);
    }

    public static final String GET_ALL_PASSENGERS_ENDPOINT = "/api/v1/passengers";

    public static final String GET_PASSENGER_BY_ID_ENDPOINT = "/api/v1/passengers/{passengerId}";

    public static final String CREATE_PASSENGER_ENDPOINT = "/api/v1/passengers";

    public static final String UPDATE_PASSENGER_BY_ID_ENDPOINT = "/api/v1/passengers/{passengerId}";

    public static final String DELETE_PASSENGER_BY_ID_ENDPOINT = "/api/v1/passengers/{passengerId}";

    public static final String INVALID_ARGS_GET_ALL_PASSENGERS_ENDPOINT = "/api/v1/passengers?offset=-1&limit=10";

    public static final String INVALID_OFFSET_MESSAGE = "must be greater than or equal to 0";

    public static final String HTTP_STATUS_CONFLICT = "CONFLICT";

    public static final String HTTP_STATUS_NOT_FOUND = "NOT_FOUND";

    public static final PassengerRequest INVALID_PASSENGER_REQUEST_DATA = new PassengerRequest(
        "Matthew",
        "McConaughey",
        "invalid-email",
        "+375336392343"
    );

    public static final String INVALID_EMAIL = "Email is invalid!";

}
