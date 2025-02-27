package by.modsen.ridesservice.constants;

import by.modsen.ridesservice.client.driver.DriverResponse;
import by.modsen.ridesservice.client.passenger.PassengerResponse;
import by.modsen.ridesservice.dto.request.RideRequest;
import by.modsen.ridesservice.dto.request.RideStatusRequest;
import by.modsen.ridesservice.dto.response.RideResponse;
import by.modsen.ridesservice.model.Ride;
import by.modsen.ridesservice.model.enums.RideStatus;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TestDataConstants {

    public static final Long RIDE_ID = 1L;
    public static final Long PASSENGER_ID = 1L;
    public static final Long DRIVER_ID = 1L;
    public static final String LANGUAGE_TAG = "en";

    public static final RideRequest RIDE_REQUEST = new RideRequest(
        DRIVER_ID,
        PASSENGER_ID,
        "123 Main St",
        "456 Elm St"
    );

    public static final RideRequest RIDE_REQUEST_FOR_UPDATE = new RideRequest(
        null,
        null,
        "123 Main St",
        "456 Elm St"
    );

    public static final RideRequest INVALID_RIDE_REQUEST_DATA = new RideRequest(
        1L,
        1L,
        "123 Main St",
        ""
    );

    public static final RideStatusRequest RIDE_STATUS_REQUEST = new RideStatusRequest(
        "ACCEPTED"
    );

    public static final RideResponse RIDE_RESPONSE = new RideResponse(
        RIDE_ID,
        DRIVER_ID,
        PASSENGER_ID,
        "123 Main St",
        "456 Elm St",
        "ACCEPTED",
        LocalDateTime.now(),
        new BigDecimal("10.00")
    );

    public static final Ride RIDE;

    static {
        RIDE = new Ride();
        RIDE.setId(RIDE_ID);
        RIDE.setDriverId(DRIVER_ID);
        RIDE.setPassengerId(PASSENGER_ID);
        RIDE.setPickupAddress("123 Main St");
        RIDE.setDestinationAddress("456 Elm St");
        RIDE.setRideStatus(RideStatus.ACCEPTED);
        RIDE.setOrderDateTime(LocalDateTime.now());
        RIDE.setCost(new BigDecimal("10.00"));
    }

    public static final String GET_ALL_RIDES_ENDPOINT = "/api/v1/rides";
    public static final String GET_RIDE_BY_ID_ENDPOINT = "/api/v1/rides/{rideId}";
    public static final String CREATE_RIDE_ENDPOINT = "/api/v1/rides";
    public static final String UPDATE_RIDE_ENDPOINT = "/api/v1/rides/{rideId}";
    public static final String PATCH_RIDE_STATUS_ENDPOINT = "/api/v1/rides/status/{rideId}";
    public static final String DELETE_RIDE_ENDPOINT = "/api/v1/rides/{rideId}";
    public static final int PAGE_OFFSET = 1;
    public static final int PAGE_LIMIT = 10;
    public static final String INVALID_DESTINATION_ADDRESS = "Destination address cannot be blank!";
    public static final String DRIVER_NOT_FOUND_MESSAGE = "Driver not found!";
    public static final String PASSENGER_NOT_FOUND_MESSAGE = "Passenger not found!";
    public static final String FEIGN_CLIENT_ERROR_MESSAGE = "Feign client error!";
    public static final String HTTP_STATUS_CONFLICT = "CONFLICT";
    public static final String HTTP_STATUS_NOT_FOUND = "NOT_FOUND";
    public static final String RIDE_STATUS_CREATED = "CREATED";
    public static final String RIDE_STATUS_ACCEPTED = "ACCEPTED";
    public static final String RIDE_STATUS_FINISHED = "FINISHED";

    public static final DriverResponse DRIVER_RESPONSE = new DriverResponse(
        TestDataConstants.DRIVER_ID,
        "Jane",
        "Doe",
        "jane.doe@example.com",
        "+987654321",
        "FEMALE",
        false,
        List.of(1L, 2L)
    );

    public static final PassengerResponse PASSENGER_RESPONSE = new PassengerResponse(
        TestDataConstants.PASSENGER_ID,
        "John",
        "Doe",
        "john.doe@example.com",
        "+123456789",
        false
    );

}