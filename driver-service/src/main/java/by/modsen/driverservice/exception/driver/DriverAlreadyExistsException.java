package by.modsen.driverservice.exception.driver;

import by.modsen.driverservice.exception.MessageSourceException;

public class DriverAlreadyExistsException extends MessageSourceException {

    public DriverAlreadyExistsException(String messageKey, Object... args) {
        super(messageKey, args);
    }
}
