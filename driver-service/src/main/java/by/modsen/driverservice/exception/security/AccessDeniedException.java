package by.modsen.driverservice.exception.security;

import by.modsen.driverservice.exception.MessageSourceException;

public class AccessDeniedException extends MessageSourceException {

    public AccessDeniedException(String messageKey, Object... args) {
        super(messageKey, args);
    }
}
