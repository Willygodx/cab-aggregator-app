package by.modsen.ridesservice.exception.security;

import by.modsen.ridesservice.exception.MessageSourceException;

public class AccessDeniedException extends MessageSourceException {

    public AccessDeniedException(String messageKey, Object... args) {
        super(messageKey, args);
    }
}
