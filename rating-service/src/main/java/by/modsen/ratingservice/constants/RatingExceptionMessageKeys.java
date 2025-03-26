package by.modsen.ratingservice.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RatingExceptionMessageKeys {

    public static final String RATING_NOT_FOUND_MESSAGE = "rating.not.found.message";
    public static final String RATING_ALREADY_EXISTS_MESSAGE = "rating.already.exists.message";
    public static final String RATING_FROM_PASSENGER_NOT_FOUND_MESSAGE = "rating.from.passenger.not.found.message";
    public static final String RATING_FROM_DRIVER_NOT_FOUND_MESSAGE = "rating.from.driver.not.found.message";
    public static final String DRIVER_ID_MISMATCH_MESSAGE = "driver.id.mismatch.message";
    public static final String PASSENGER_ID_MISMATCH_MESSAGE = "passenger.id.mismatch.message";
    public static final String ACCESS_DENIED_MESSAGE = "access.denied.message";
    public static final String INVALID_ROLE_USER_MESSAGE = "invalid.role.message";

}
