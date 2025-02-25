package by.modsen.driverservice.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class IntegrationTestDataConstants {

    public static final String CAR_COLOR = "Yellow";
    public static final String CAR_BRAND = "Bmw";
    public static final String CAR_NUMBER = "ABC123";
    public static final String CAR_NEW_COLOR = "Red";
    public static final String CAR_NEW_BRAND = "Toyota";
    public static final String CAR_NEW_NUMBER = "XYZ789";
    public static final String CAR_NEW_NUMBER_FOR_CREATE = "XYZ789";
    public static final String SQL_DELETE_ALL_CARS = "DELETE FROM car;";
    public static final String SQL_RESTART_CAR_SEQUENCES = "ALTER SEQUENCE car_id_seq RESTART WITH 1;";
    public static final String SQL_INSERT_CAR_DATA = "INSERT INTO car (color, car_brand, car_number, is_deleted) " +
        "VALUES ('" + CAR_COLOR + "', '" + CAR_BRAND + "', '" + CAR_NUMBER + "', false);";
    public static final String SQL_DELETE_DRIVER_CAR = "DELETE FROM driver_car;";
    public static final String CAR_ENDPOINT = "/api/v1/cars";
    public static final String CAR_BY_ID_ENDPOINT = CAR_ENDPOINT + "/{id}";
    public static final int DEFAULT_OFFSET = 0;
    public static final int DEFAULT_LIMIT = 10;
    public static final int TOTAL_PAGES = 1;
    public static final int TOTAL_ELEMENTS = 1;
    public static final int CAR_ID = 1;
    public static final int HTTP_STATUS_OK = 200;
    public static final int HTTP_STATUS_CREATED = 201;
    public static final int HTTP_STATUS_NO_CONTENT = 204;
    public static final int HTTP_STATUS_BAD_REQUEST = 400;
    public static final int HTTP_STATUS_NOT_FOUND = 404;
    public static final String POSTGRESQL_IMAGE_NAME = "postgres:latest";
    public static final String EUREKA_CLIENT_ENABLED_PROPERTY = "eureka.client.enabled";
    public static final String EUREKA_CLIENT_DISABLED_VALUE = "false";
    public static final String INVALID_COLOR = "";
    public static final String INVALID_NUMBER = "123";
    public static final int INVALID_OFFSET = -1;
    public static final int INVALID_LIMIT = 101;
    public static final long NON_EXISTENT_CAR_ID = 999L;
    public static final String DRIVER_ENDPOINT = "/api/v1/drivers";
    public static final String DRIVER_BY_ID_ENDPOINT = DRIVER_ENDPOINT + "/{driverId}";
    public static final String DRIVER_FIRST_NAME = "Ruslan";
    public static final String DRIVER_LAST_NAME = "Alhava";
    public static final String DRIVER_EMAIL = "ruslan322@example.com";
    public static final String DRIVER_PHONE_NUMBER = "+375338723636";
    public static final String DRIVER_SEX = "MALE";
    public static final String DRIVER_NEW_FIRST_NAME = "Ivan";
    public static final String DRIVER_NEW_LAST_NAME = "Ivanov";
    public static final String DRIVER_NEW_EMAIL = "ivanov@example.com";
    public static final String DRIVER_NEW_PHONE_NUMBER = "+375291234567";
    public static final String DRIVER_NEW_SEX = "MALE";
    public static final String INVALID_FIRST_NAME = "";
    public static final String INVALID_EMAIL = "invalid-email";
    public static final String INVALID_PHONE_NUMBER = "12345";
    public static final String INVALID_SEX = "UNKNOWN";
    public static final int DRIVER_ID = 1;
    public static final long NON_EXISTENT_DRIVER_ID = 999L;
    public static final String SQL_DELETE_ALL_DRIVERS = "DELETE FROM driver;";
    public static final String SQL_RESTART_DRIVER_SEQUENCES = "ALTER SEQUENCE driver_id_seq RESTART WITH 1;";
    public static final String SQL_INSERT_DRIVER_DATA = "INSERT INTO driver " +
        "(first_name, last_name, email, phone_number, sex, average_rating, is_deleted) " +
        "VALUES ('" + DRIVER_FIRST_NAME + "', '" + DRIVER_LAST_NAME + "', '" + DRIVER_EMAIL + "', '" +
        DRIVER_PHONE_NUMBER + "', 0, 0.0, false);";

}