package by.modsen.ridesservice.exception.ride;

import by.modsen.ridesservice.exception.MessageSourceException;

public class RideNotFoundException extends MessageSourceException {

    public RideNotFoundException(String messageKey, Object... args) {
        super(messageKey, args);
    }
}
