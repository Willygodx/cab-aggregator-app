package by.modsen.driverservice.exception.driver;

import by.modsen.driverservice.exception.MessageSourceException;

public class DriverNotFoundException extends MessageSourceException {

    public DriverNotFoundException(String messageKey, Object... args) {
        super(messageKey, args);
    }
}
