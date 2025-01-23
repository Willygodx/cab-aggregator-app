package by.modsen.passengerservice.service.component.validation;

import by.modsen.passengerservice.dto.request.PassengerRequestDto;
import by.modsen.passengerservice.model.Passenger;

public interface PassengerServiceValidation {

  void restoreOption(PassengerRequestDto passengerDto);

  void checkAlreadyExists(PassengerRequestDto passengerDto);

  Passenger findPassengerByIdWithChecks(Long passengerId);

}
