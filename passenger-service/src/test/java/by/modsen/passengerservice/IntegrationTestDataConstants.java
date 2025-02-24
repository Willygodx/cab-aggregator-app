package by.modsen.passengerservice;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class IntegrationTestDataConstants {

    public static final String PASSENGER_FIRST_NAME = "Ivan";
    public static final String PASSENGER_LAST_NAME = "Ivanov";
    public static final String PASSENGER_EMAIL = "ivan@example.com";
    public static final String PASSENGER_PHONE_NUMBER = "+375291234567";
    public static final String PASSENGER_NEW_PHONE_NUMBER = "+375291234569";
    public static final String PASSENGER_NEW_FIRST_NAME = "John";
    public static final String PASSENGER_NEW_LAST_NAME = "Doe";
    public static final String PASSENGER_NEW_EMAIL = "john.doe@example.com";
    public static final String PASSENGER_NEW_PHONE_NUMBER_FOR_CREATE = "+375291234568";
    public static final String SQL_DELETE_ALL_DATA = "DELETE FROM passenger;";
    public static final String SQL_RESTART_SEQUENCES = "ALTER SEQUENCE passenger_id_seq RESTART WITH 1;";
    public static final String SQL_INSERT_DATA = "INSERT INTO passenger (first_name, last_name, email, phone_number, average_rating, is_deleted) " +
        "VALUES ('" + PASSENGER_FIRST_NAME + "', '" + PASSENGER_LAST_NAME + "', '" + PASSENGER_EMAIL + "', '" + PASSENGER_PHONE_NUMBER + "', 0.0, false);";
    public static final String PASSENGER_ENDPOINT = "/api/v1/passengers";
    public static final String PASSENGER_BY_ID_ENDPOINT = PASSENGER_ENDPOINT + "/{id}";
    public static final int DEFAULT_OFFSET = 0;
    public static final int DEFAULT_LIMIT = 10;
    public static final int TOTAL_PAGES = 1;
    public static final int TOTAL_ELEMENTS = 1;
    public static final int PASSENGER_ID = 1;
    public static final int HTTP_STATUS_OK = 200;
    public static final int HTTP_STATUS_CREATED = 201;
    public static final int HTTP_STATUS_NO_CONTENT = 204;
    public static final int HTTP_STATUS_BAD_REQUEST = 400;
    public static final int HTTP_STATUS_NOT_FOUND = 404;
    public static final String POSTGRESQL_IMAGE_NAME = "postgres:latest";
    public static final String EUREKA_CLIENT_ENABLED_PROPERTY = "eureka.client.enabled";
    public static final String EUREKA_CLIENT_DISABLED_VALUE = "false";
    public static final String INVALID_FIRST_NAME = "";
    public static final String INVALID_EMAIL = "invalid-email";
    public static final String INVALID_PHONE_NUMBER = "123";
    public static final int INVALID_OFFSET = -1;
    public static final int INVALID_LIMIT = 101;
    public static final long NON_EXISTENT_PASSENGER_ID = 999L;

}