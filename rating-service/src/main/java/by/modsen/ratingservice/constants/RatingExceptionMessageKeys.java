package by.modsen.ratingservice.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RatingExceptionMessageKeys {

    public static final String RATING_NOT_FOUND_MESSAGE = "rating.not.found.message";
    public static final String RATING_ALREADY_EXISTS_MESSAGE = "rating.already.exists.message";
    public static final String RATING_FROM_PASSENGER_NOT_FOUND_MESSAGE = "rating.from.passenger.not.found.message";
    public static final String RATING_FROM_DRIVER_NOT_FOUND_MESSAGE = "rating.from.driver.not.found.message";

}
