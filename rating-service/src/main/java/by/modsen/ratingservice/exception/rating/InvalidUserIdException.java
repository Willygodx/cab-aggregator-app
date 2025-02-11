package by.modsen.ratingservice.exception.rating;

import by.modsen.ratingservice.exception.MessageSourceException;

public class InvalidUserIdException extends MessageSourceException {

    public InvalidUserIdException(String messageKey, Object... args) {
        super(messageKey, args);
    }
}
