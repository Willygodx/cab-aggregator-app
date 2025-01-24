package by.modsen.driverservice.service;

import by.modsen.driverservice.dto.request.DriverRequest;
import by.modsen.driverservice.dto.response.DriverResponse;
import by.modsen.driverservice.dto.response.PageResponse;

public interface DriverService {

  PageResponse<DriverResponse> getAllDrivers(Integer offset, Integer limit);

  DriverResponse createDriver(DriverRequest driverRequest);

  DriverResponse updateDriverById(DriverRequest driverRequest, Long driverId);

  void deleteDriverById(Long driverId);

  DriverResponse getDriverById(Long driverId);

}
