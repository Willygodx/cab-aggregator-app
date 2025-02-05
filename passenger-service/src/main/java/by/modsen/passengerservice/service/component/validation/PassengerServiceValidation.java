package by.modsen.passengerservice.service.component.validation;

import by.modsen.passengerservice.dto.request.PassengerRequest;
import by.modsen.passengerservice.model.Passenger;

public interface PassengerServiceValidation {

    void restoreOption(PassengerRequest passengerRequest);

    void checkAlreadyExists(PassengerRequest passengerRequest);

    Passenger findPassengerByIdWithChecks(Long passengerId);

}
