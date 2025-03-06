package by.modsen.ratingservice.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ApplicationConstants {

    public static final String SCHEDULED_CRON = "0 0 0 * * MON";
    public static final String INTERNAL_SERVER_ERROR_MESSAGE = "Internal server error occurred!";

}
