package by.modsen.driverservice.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class E2ETestDataConstants {

    public static final String BASE_CARS_URL = "http://localhost:8080/api/v1/cars";
    public static final String BASE_DRIVERS_URL = "http://localhost:8080/api/v1/drivers";
    public static final String OFFSET_PARAM = "?offset=";
    public static final String LIMIT_PARAM = "&limit=";
    public static final String ID_POSTFIX = "/{id}";

}