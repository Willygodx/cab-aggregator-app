package by.modsen.passengerservice.exception.passenger;

import by.modsen.passengerservice.exception.MessageSourceException;

public class PassengerAlreadyExistsException extends MessageSourceException {

    public PassengerAlreadyExistsException(String messageKey, Object... args) {
        super(messageKey, args);
    }
}
