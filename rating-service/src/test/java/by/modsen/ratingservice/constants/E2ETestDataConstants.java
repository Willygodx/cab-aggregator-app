package by.modsen.ratingservice.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class E2ETestDataConstants {

    public static final String BASE_URL = "/api/v1/ratings";
    public static final String ID_POSTFIX = "/{id}";
    public static final String PASSENGER_AVERAGE_RATING_POSTFIX = "/passengers/{id}/average-rating";
    public static final String DRIVER_AVERAGE_RATING_POSTFIX = "/drivers/{id}/average-rating";
    public static final String TRIGGER_KAFKA_POSTFIX = "/test/trigger-kafka";
    public static final String OFFSET_PARAM = "?offset=";
    public static final String LIMIT_PARAM = "&limit=";

}
