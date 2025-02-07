package by.modsen.ratingservice.exception.rating;

import by.modsen.ratingservice.exception.MessageSourceException;

public class InvalidRatedByUserException extends MessageSourceException {

    public InvalidRatedByUserException(String messageKey, Object... args) {
        super(messageKey, args);
    }
}
