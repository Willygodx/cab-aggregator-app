package by.modsen.ratingservice.exception.security;

import by.modsen.ratingservice.exception.MessageSourceException;

public class AccessDeniedException extends MessageSourceException {

    public AccessDeniedException(String messageKey, Object... args) {
        super(messageKey, args);
    }
}
