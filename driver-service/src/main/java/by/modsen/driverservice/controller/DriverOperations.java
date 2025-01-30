package by.modsen.driverservice.controller;

import by.modsen.driverservice.dto.request.DriverRequest;
import by.modsen.driverservice.dto.response.DriverResponse;
import by.modsen.driverservice.dto.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Driver controller",
    description = "This controller contains CRUD operations for driver controller in driver service")
public interface DriverOperations {

  @Operation(description = "Retrieving all drivers (pagination type)")
  PageResponse<DriverResponse> getAllDrivers(@RequestParam(defaultValue = "0") @Min(0) Integer offset,
                                             @RequestParam(defaultValue = "10") @Min(1) @Max(100) Integer limit);

  @Operation(description = "Retrieving a driver by id")
  DriverResponse getDriverById(@PathVariable Long driverId);

  @Operation(description = "Creates a driver")
  DriverResponse createDriver(@RequestBody @Valid DriverRequest driverRequest);

  @Operation(description = "Updates a driver")
  DriverResponse updateDriverById(@PathVariable Long driverId,
                                  @RequestBody @Valid DriverRequest driverRequest);

  @Operation(description = "Deletes a driver")
  void deleteDriverById(@PathVariable Long driverId);

  @Operation(description = "Adds car to a current driver")
  void addCarToDriver(@PathVariable Long driverId,
                      @PathVariable Long carId);

}
