package by.modsen.driverservice.constants;

import by.modsen.driverservice.dto.request.CarRequest;
import by.modsen.driverservice.dto.request.DriverRequest;
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

}