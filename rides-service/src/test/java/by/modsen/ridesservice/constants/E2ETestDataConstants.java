package by.modsen.ridesservice.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class E2ETestDataConstants {

    public static final String BASE_URL = "http://localhost:8080/api/v1/rides";
    public static final String OFFSET_PARAM = "?offset=";
    public static final String LIMIT_PARAM = "&limit=";
    public static final String ID_POSTFIX = "/{id}";
    public static final String STATUS_POSTFIX = "/status/{id}";

}