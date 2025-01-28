package by.modsen.driverservice.controller;

import by.modsen.driverservice.dto.Marker;
import by.modsen.driverservice.dto.request.CarRequest;
import by.modsen.driverservice.dto.response.CarResponse;
import by.modsen.driverservice.dto.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Validated
@Tag(name = "Car controller",
    description = "This controller contains CRUD operations for car controller in driver service")
public interface CarOperations {

  @Operation(description = "Retrieving all cars (pagination type)")
  PageResponse<CarResponse> getAllCars(@RequestParam(defaultValue = "0") @Min(0) Integer offset,
                                       @RequestParam(defaultValue = "10") @Min(1) @Max(100) Integer limit);

  @Operation(description = "Retrieving a car by id")
  CarResponse getCarById(@PathVariable Long carId);

  @Operation(description = "Creates a car")
  @Validated(Marker.OnCreate.class)
  CarResponse createCar(@RequestBody @Valid CarRequest carRequest);

  @Operation(description = "Updates a car")
  @Validated(Marker.OnUpdate.class)
  CarResponse updateCarById(@PathVariable Long carId,
                            @RequestBody @Valid CarRequest carRequest);

  @Operation(description = "Deletes a car")
  void deleteCarById(@PathVariable Long carId);

  @Operation(description = "Adds current car to a driver")
  void addCarToDriver(@PathVariable Long carId,
                      @PathVariable Long driverId);

}
