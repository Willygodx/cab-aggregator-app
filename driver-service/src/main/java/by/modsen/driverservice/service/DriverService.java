package by.modsen.driverservice.service;

import by.modsen.driverservice.dto.request.DriverRequest;
import by.modsen.driverservice.dto.response.DriverResponse;
import by.modsen.driverservice.dto.response.PageResponse;
import java.util.UUID;

public interface DriverService {

    PageResponse<DriverResponse> getAllDrivers(Integer offset, Integer limit);

    DriverResponse createDriver(DriverRequest driverRequest);

    DriverResponse updateDriverById(DriverRequest driverRequest, UUID driverId);

    void deleteDriverById(UUID driverId);

    DriverResponse getDriverById(UUID driverId);

    void addCarToDriver(UUID driverId, Long carId);

}
