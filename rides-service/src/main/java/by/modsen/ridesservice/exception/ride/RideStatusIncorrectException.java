package by.modsen.ridesservice.exception.ride;

import by.modsen.ridesservice.exception.MessageSourceException;

public class RideStatusIncorrectException extends MessageSourceException {

    public RideStatusIncorrectException(String messageKey, Object... args) {
        super(messageKey, args);
    }
}
