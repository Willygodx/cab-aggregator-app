package by.modsen.ratingservice.exception.rating;

import by.modsen.ratingservice.exception.MessageSourceException;

public class RatingAlreadyExistsException extends MessageSourceException {

    public RatingAlreadyExistsException(String messageKey, Object... args) {
        super(messageKey, args);
    }
}
