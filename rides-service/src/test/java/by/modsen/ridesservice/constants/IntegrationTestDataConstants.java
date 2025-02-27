package by.modsen.ridesservice.constants;

import by.modsen.ridesservice.client.driver.DriverResponse;
import by.modsen.ridesservice.client.passenger.PassengerResponse;
import by.modsen.ridesservice.dto.request.RideRequest;
import by.modsen.ridesservice.dto.request.RideStatusRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class IntegrationTestDataConstants {

    public static final long PASSENGER_ID = 1L;
    public static final long DRIVER_ID = 1L;
    public static final long RIDE_ID = 1L;
    public static final String PASSENGER_FROM_ANOTHER_SERVICE_ENDPOINT = "/api/v1/passengers/" + PASSENGER_ID;
    public static final String DRIVER_FROM_ANOTHER_SERVICE_ENDPOINT = "/api/v1/drivers/" + DRIVER_ID;
    public static final String RIDE_ENDPOINT = "/api/v1/rides";
    public static final String RIDE_BY_ID_ENDPOINT = RIDE_ENDPOINT + "/{id}";
    public static final String RIDE_STATUS_ENDPOINT = RIDE_ENDPOINT + "/status/{id}";
    public static final String RIDES_BY_DRIVER_ENDPOINT = RIDE_ENDPOINT + "/drivers/{id}";
    public static final String RIDES_BY_PASSENGER_ENDPOINT = RIDE_ENDPOINT + "/passengers/{id}";
    public static final String DELETE_RIDES_SQL = "DELETE FROM ride;";
    public static final String RESET_RIDE_ID_SEQUENCE_SQL = "ALTER SEQUENCE ride_id_seq RESTART WITH 1;";
    public static final String INSERT_RIDE_SQL = "INSERT INTO ride (driver_id, passenger_id, pickup_address, destination_address, ride_status, order_date_time, cost) " +
        "VALUES (1, 1, '123 Main St', '456 Elm St', 0, '2023-10-01 10:00:00', 12.50);";
    public static final String PICKUP_ADDRESS = "123 Main St";
    public static final String DESTINATION_ADDRESS = "456 Elm St";
    public static final String UPDATED_PICKUP_ADDRESS = "Updated Pickup";
    public static final String UPDATED_DESTINATION_ADDRESS = "Updated Destination";
    public static final String RIDE_STATUS_ACCEPTED = "ACCEPTED";
    public static final String RIDE_STATUS_INVALID = "INVALID_STATUS";
    public static final String ORDER_DATE_TIME_STRING = "2023-10-01 10:00:00";
    public static final LocalDateTime ORDER_DATE_TIME = LocalDateTime.parse(
        ORDER_DATE_TIME_STRING,
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    );
    public static final double COST_DOUBLE = 12.50;
    public static final BigDecimal COST = BigDecimal.valueOf(COST_DOUBLE);

    public static final RideRequest RIDE_REQUEST = new RideRequest(
        DRIVER_ID,
        PASSENGER_ID,
        PICKUP_ADDRESS,
        DESTINATION_ADDRESS
    );

    public static final RideRequest INVALID_RIDE_REQUEST = new RideRequest(
        null,
        null,
        null,
        null);

    public static final RideRequest UPDATED_RIDE_REQUEST = new RideRequest(
        null,
        null,
        UPDATED_PICKUP_ADDRESS,
        UPDATED_DESTINATION_ADDRESS
    );

    public static final RideStatusRequest RIDE_STATUS_REQUEST = new RideStatusRequest(
        RIDE_STATUS_ACCEPTED
    );

    public static final RideStatusRequest INVALID_RIDE_STATUS_REQUEST = new RideStatusRequest(
        RIDE_STATUS_INVALID
    );

    public static final PassengerResponse PASSENGER_RESPONSE = new PassengerResponse(
        PASSENGER_ID,
        "John",
        "Doe",
        "john.doe@example.com",
        "+1234567890",
        false
    );

    public static final DriverResponse DRIVER_RESPONSE = new DriverResponse(
        DRIVER_ID,
        "John",
        "Doe",
        "john.doe@example.com",
        "+1234567890",
        "Male",
        false,
        List.of()
    );

    public static final String POSTGRESQL_IMAGE_NAME = "postgres:latest";
    public static final String EUREKA_CLIENT_ENABLED_PROPERTY = "eureka.client.enabled";
    public static final String BOOLEAN_PROPERTY_VALUE = "false";

}