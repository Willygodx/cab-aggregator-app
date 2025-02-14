package by.modsen.driverservice.exception.car;

import by.modsen.driverservice.exception.MessageSourceException;

public class CarNumberAlreadyExistsException extends MessageSourceException {

    public CarNumberAlreadyExistsException(String messageKey, Object... args) {
        super(messageKey, args);
    }
}
