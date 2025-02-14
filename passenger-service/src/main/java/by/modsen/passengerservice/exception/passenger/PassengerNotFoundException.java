package by.modsen.passengerservice.exception.passenger;

import by.modsen.passengerservice.exception.MessageSourceException;

public class PassengerNotFoundException extends MessageSourceException {

    public PassengerNotFoundException(String messageKey, Object... args) {
        super(messageKey, args);
    }
}
