package by.modsen.ratingservice.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ApplicationConstants {

    public static final String SCHEDULED_CRON = "0 0 0 * * MON";
    public static final String INTERNAL_SERVER_ERROR_MESSAGE = "Internal server error occurred!";
    public static final String REQUEST_LOGGING_MESSAGE =
        "Request. Method: {}, URI: {}, Args -> {}";
    public static final String RESPONSE_LOGGING_MESSAGE =
        "Response. Method: {}, URI: {}, Args -> {} Time Taken: {} ms";
    public static final String ERROR_SERIALIZING_JSON_MESSAGE = "Error serializing object to JSON";

}
