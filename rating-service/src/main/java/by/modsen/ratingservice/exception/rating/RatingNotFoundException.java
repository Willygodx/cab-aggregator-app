package by.modsen.ratingservice.exception.rating;

import by.modsen.ratingservice.exception.MessageSourceException;

public class RatingNotFoundException extends MessageSourceException {

    public RatingNotFoundException(String messageKey, Object... args) {
        super(messageKey, args);
    }
}
