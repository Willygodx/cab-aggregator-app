package by.modsen.driverservice.exception.car;

import by.modsen.driverservice.exception.MessageSourceException;

public class CarNotFoundException extends MessageSourceException {

    public CarNotFoundException(String messageKey, Object... args) {
        super(messageKey, args);
    }
}
