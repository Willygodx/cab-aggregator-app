package by.modsen.driverservice.service.component.validation;

import by.modsen.driverservice.dto.request.CarRequest;
import by.modsen.driverservice.model.Car;

public interface CarServiceValidation {

  void restoreOption(CarRequest carRequest);

  void checkAlreadyExists(CarRequest carRequest);

  Car findCarByIdWithCheck(Long id);

}
