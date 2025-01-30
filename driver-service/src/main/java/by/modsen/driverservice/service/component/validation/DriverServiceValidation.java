package by.modsen.driverservice.service.component.validation;

import by.modsen.driverservice.dto.request.DriverRequest;
import by.modsen.driverservice.model.Driver;

public interface DriverServiceValidation {

  void restoreOption(DriverRequest driverRequest);

  void checkAlreadyExists(DriverRequest driverRequest);

  Driver findDriverByIdWithCheck(Long id);

}
