package by.modsen.driverservice.constants;

import by.modsen.driverservice.dto.request.CarRequest;
import by.modsen.driverservice.dto.request.DriverRequest;
import by.modsen.driverservice.dto.response.CarResponse;
import by.modsen.driverservice.dto.response.DriverResponse;
import by.modsen.driverservice.model.Car;
import by.modsen.driverservice.model.Driver;
import by.modsen.driverservice.model.enums.Sex;
import java.util.Collections;
import java.util.HashSet;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TestDataConstants {

    public static final Long DRIVER_ID = 1L;
    public static final String DRIVER_FIRST_NAME = "John";
    public static final String DRIVER_LAST_NAME = "Doe";
    public static final String DRIVER_EMAIL = "john.doe@example.com";
    public static final String DRIVER_PHONE_NUMBER = "+375291234567";
    public static final String DRIVER_SEX = Sex.MALE.toString();
    public static final Driver DRIVER;
    public static final int PAGE_OFFSET = 1;
    public static final int PAGE_LIMIT = 10;

    static {
        DRIVER = new Driver();
        DRIVER.setId(1L);
        DRIVER.setFirstName("Ruslan");
        DRIVER.setLastName("Alhava");
        DRIVER.setEmail("ruslan322@example.com");
        DRIVER.setPhoneNumber("+375338723636");
        DRIVER.setSex(Sex.valueOf("MALE"));
        DRIVER.setAverageRating(0.0);
        DRIVER.setIsDeleted(false);
        DRIVER.setCars(new HashSet<>());
    }

    public static final DriverRequest DRIVER_REQUEST = new DriverRequest(
        DRIVER_FIRST_NAME,
        DRIVER_LAST_NAME,
        DRIVER_EMAIL,
        DRIVER_PHONE_NUMBER,
        DRIVER_SEX
    );

    public static final DriverResponse DRIVER_RESPONSE = new DriverResponse(
        1L,
        "Matthew",
        "McConaughey",
        "matt.mcconh23@gmail.com",
        "+375339482732",
        "MALE",
        5.0,
        false,
        Collections.emptyList()
    );

    public static final Long CAR_ID = 1L;
    public static final String CAR_COLOR = "Red";
    public static final String CAR_BRAND = "Toyota";
    public static final String CAR_NUMBER = "ABC123";
    public static final Car CAR;

    static {
        CAR = new Car();
        CAR.setId(1L);
        CAR.setColor("Yellow");
        CAR.setCarBrand("Bmw");
        CAR.setCarNumber("ABC123");
        CAR.setIsDeleted(false);
        CAR.setDrivers(new HashSet<>());
    }

    public static final CarRequest CAR_REQUEST = new CarRequest(
        CAR_COLOR,
        CAR_BRAND,
        CAR_NUMBER
    );

    public static final CarResponse CAR_RESPONSE = new CarResponse(
        1L,
        "Yellow",
        "Bmw",
        "ABC123",
        false,
        Collections.emptyList()
    );

    public static final String GET_ALL_CARS_ENDPOINT = "/api/v1/cars";
    public static final String GET_CAR_BY_ID_ENDPOINT = "/api/v1/cars/{carId}";
    public static final String CREATE_CAR_ENDPOINT = "/api/v1/cars";
    public static final String UPDATE_CAR_BY_ID_ENDPOINT = "/api/v1/cars/{carId}";
    public static final String DELETE_CAR_BY_ID_ENDPOINT = "/api/v1/cars/{carId}";
    public static final String INVALID_ARGS_GET_ALL_CARS_ENDPOINT = "/api/v1/cars?offset=-1&limit=10";
    public static final String INVALID_OFFSET_MESSAGE = "must be greater than or equal to 0";
    public static final String INVALID_CAR_NUMBER = "License plate is invalid!";
    public static final String HTTP_STATUS_CONFLICT = "CONFLICT";
    public static final String HTTP_STATUS_NOT_FOUND = "NOT_FOUND";
    public static final CarRequest INVALID_CAR_REQUEST_DATA = new CarRequest(
        "Red",
        "Toyota",
        "INVALID_NUMBER"
    );

    public static final String GET_ALL_DRIVERS_ENDPOINT = "/api/v1/drivers";
    public static final String GET_DRIVER_BY_ID_ENDPOINT = "/api/v1/drivers/{driverId}";
    public static final String CREATE_DRIVER_ENDPOINT = "/api/v1/drivers";
    public static final String UPDATE_DRIVER_BY_ID_ENDPOINT = "/api/v1/drivers/{driverId}";
    public static final String DELETE_DRIVER_BY_ID_ENDPOINT = "/api/v1/drivers/{driverId}";
    public static final String ADD_CAR_TO_DRIVER_ENDPOINT = "/api/v1/drivers/{driverId}/add-car/{carId}";
    public static final String INVALID_ARGS_GET_ALL_DRIVERS_ENDPOINT = "/api/v1/drivers?offset=-1&limit=10";
    public static final String INVALID_EMAIL = "Email is invalid!";

    public static final DriverRequest INVALID_DRIVER_REQUEST_DATA = new DriverRequest(
        "Matthew",
        "McConaughey",
        "invalid-email",
        "+375336392343",
        "MALE"
    );

    public static final String LOG_LOCALE_MESSAGE = "Locale: %s, Message: %s";

}