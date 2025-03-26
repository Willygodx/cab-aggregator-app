package by.modsen.passengerservice.exception.security;

import by.modsen.passengerservice.exception.MessageSourceException;

public class AccessDeniedException extends MessageSourceException {

    public AccessDeniedException(String messageKey, Object... args) {
        super(messageKey, args);
    }
}
